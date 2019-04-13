/**
 * The MIT License
 * Copyright (c) 2015 Alexander Sova (bird@codeminders.com)
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.airlenet.io.socket.server.transport.websocket;

import com.airlenet.io.socket.protocol.BinaryPacket;
import com.airlenet.io.socket.server.*;
import com.airlenet.io.socket.server.transport.AbstractTransportConnection;
import com.airlenet.io.socket.common.ConnectionState;
import com.airlenet.io.socket.common.DisconnectReason;
import com.airlenet.io.socket.common.SocketIOException;
import com.airlenet.io.socket.protocol.EngineIOPacket;
import com.airlenet.io.socket.protocol.EngineIOProtocol;
import com.airlenet.io.socket.protocol.SocketIOPacket;
import com.google.common.io.ByteStreams;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Alexander Sova (bird@codeminders.com)
 * @author Alex Saveliev (lyolik@codeminders.com)
 */
@ServerEndpoint(value="/socket.io/", configurator = WebsocketConfigurator.class)
public final class WebsocketTransportConnection extends AbstractTransportConnection
{
    private static final Logger LOGGER = Logger.getLogger(WebsocketTransportConnection.class.getName());

    private static Class<? extends WebsocketIO> websocketIOClass = WebsocketIO.class;

    private WebsocketIO websocketIO;

    public WebsocketTransportConnection() {
        super(WebsocketTransportProvider.websocket);
    }

    public WebsocketTransportConnection(Transport transport)
    {
        super(transport);
    }

    /**
     *
     * @param clazz class responsible for I/O operations
     */
    public static void setWebsocketIOClass(Class<? extends WebsocketIO> clazz) {
        WebsocketTransportConnection.websocketIOClass = clazz;
    }

    @Override
    protected void init()
    {
        getSession().setTimeout(getConfig().getTimeout(Config.DEFAULT_PING_TIMEOUT));

        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine(getConfig().getNamespace() + " WebSocket configuration:" +
                    " timeout=" + getSession().getTimeout());
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) throws Exception
    {
        setupIO(session);
        setupSession(session);
        init(new ServletBasedConfig(
                ServletConfigHolder.getInstance().getConfig(),
                getTransport().getType().toString()));
        session.setMaxBinaryMessageBufferSize(getConfig().getBufferSize());
        session.setMaxIdleTimeout(getConfig().getMaxIdle());
        session.setMaxTextMessageBufferSize(getConfig().getInt(Config.MAX_TEXT_MESSAGE_SIZE, 32000));

        if(getSession().getConnectionState() == ConnectionState.CONNECTING)
        {
            try
            {
                send(EngineIOProtocol.createHandshakePacket(getSession().getSessionId(),
                        new String[]{},
                        getConfig().getPingInterval(Config.DEFAULT_PING_INTERVAL),
                        getConfig().getTimeout(Config.DEFAULT_PING_TIMEOUT)));

                getSession().onConnect(this);
            }
            catch (SocketIOException e)
            {
                LOGGER.log(Level.SEVERE, "Cannot connect", e);
                getSession().setDisconnectReason(DisconnectReason.CONNECT_FAILED);
                abort();
            }
        }
    }

    private void setupIO(Session session) throws Exception {
        websocketIO = websocketIOClass.getConstructor(Session.class).newInstance(session);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason)
    {
        if(LOGGER.isLoggable(Level.FINE))
            LOGGER.log(Level.FINE, "Session[" + getSession().getSessionId() + "]:" +
                    " websocket closed. " + closeReason.toString());

        //If close is unexpected then try to guess the reason based on closeCode, otherwise the reason is already set
        if(getSession().getConnectionState() != ConnectionState.CLOSING)
            getSession().setDisconnectReason(fromCloseCode(closeReason.getCloseCode().getCode()));

        getSession().setDisconnectMessage(closeReason.getReasonPhrase());
        getSession().onShutdown();
    }

    @OnMessage
    public void onMessage(String text)
    {
        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine("Session[" + getSession().getSessionId() + "]: text received: " + text);

        getSession().resetTimeout();

        try
        {
            getSession().onPacket(EngineIOProtocol.decode(text), this);
        }
        catch (SocketIOProtocolException e)
        {
            if(LOGGER.isLoggable(Level.WARNING))
                LOGGER.log(Level.WARNING, "Invalid packet received", e);
        }
    }

    @OnMessage
    public void onMessage(InputStream is)
    {
        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine("Session[" + getSession().getSessionId() + "]: binary received");

        getSession().resetTimeout();

        try
        {
            getSession().onPacket(EngineIOProtocol.decode(is), this);
        }
        catch (SocketIOProtocolException e)
        {
            if(LOGGER.isLoggable(Level.WARNING))
                LOGGER.log(Level.WARNING, "Problem processing binary received", e);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        // TODO implement
        // One reason might be when you are refreshing web page causing connection to be dropped
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unexpected request on upgraded WebSocket connection");
    }

    @Override
    public void abort()
    {
        getSession().clearTimeout();
        if (websocketIO != null)
        {
            disconnectEndpoint();
            websocketIO = null;
        }
    }

    @Override
    public void send(EngineIOPacket packet) throws SocketIOException
    {
        sendString(EngineIOProtocol.encode(packet));
    }

    @Override
    public void send(SocketIOPacket packet) throws SocketIOException
    {
        send(EngineIOProtocol.createMessagePacket(packet.encode()));
        if(packet instanceof BinaryPacket)
        {
            Collection<InputStream> attachments = ((BinaryPacket) packet).getAttachments();
            for (InputStream is : attachments)
            {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try
                {
                    os.write(EngineIOPacket.Type.MESSAGE.value());
                    ByteStreams.copy(is, os);
                }
                catch (IOException e)
                {
                    if(LOGGER.isLoggable(Level.WARNING))
                        LOGGER.log(Level.SEVERE, "Cannot load binary object to send it to the socket", e);
                }
                sendBinary(os.toByteArray());
            }
        }
    }

    protected void sendString(String data) throws SocketIOException
    {
        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.log(Level.FINE, "Session[" + getSession().getSessionId() + "]: send text: " + data);

        try
        {
            websocketIO.sendString(data);
        }
        catch (IOException e)
        {
            disconnectEndpoint();
            throw new SocketIOException(e);
        }
    }

    //TODO: implement streaming. right now it is all in memory.
    //TODO: read and send in chunks using sendPartialBytes()
    protected void sendBinary(byte[] data) throws SocketIOException
    {
        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.log(Level.FINE, "Session[" + getSession().getSessionId() + "]: send binary");

        try
        {
            websocketIO.sendBinary(data);
        }
        catch (IOException e)
        {
            disconnectEndpoint();
            throw new SocketIOException(e);
        }
    }

    private void disconnectEndpoint()
    {
        try
        {
            websocketIO.disconnect();
        }
        catch (IOException ex)
        {
            // ignore
        }
    }

    /**
     * @link https://tools.ietf.org/html/rfc6455#section-11.7
     */
    private DisconnectReason fromCloseCode(int code)
    {
        switch (code) {
            case 1000:
                return DisconnectReason.CLOSED; // Normal Closure
            case 1001:
                return DisconnectReason.CLIENT_GONE; // Going Away
            default:
                return DisconnectReason.ERROR;
        }
    }

    /**
     * @param session websocket session
     * @return session id extracted from handshake request's parameter
     */
    private String getSessionId(Session session)
    {
        HandshakeRequest handshake = (HandshakeRequest)
                session.getUserProperties().get(HandshakeRequest.class.getName());
        if (handshake == null) {
            return null;
        }
        List<String> values = handshake.getParameterMap().get(EngineIOProtocol.SESSION_ID);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    private HttpSession getHttpSession(Session session)
    {
        HandshakeRequest handshake = (HandshakeRequest)
                session.getUserProperties().get(HandshakeRequest.class.getName());
        if (handshake == null)
        {
            return null;
        }
        if (!(handshake.getHttpSession() instanceof HttpSession))
        {
            return null;
        }
        return (HttpSession) handshake.getHttpSession();
    }

    /**
     * Initializes socket.io session
     * @param session
     * @throws Exception
     */
    private void setupSession(Session session) throws Exception
    {
        String sessionId = getSessionId(session);
        com.airlenet.io.socket.server.Session sess = null;
        if (sessionId != null) {
            sess = SocketIOManager.getInstance().getSession(sessionId);
        }
        if (sess == null) {
            HttpSession httpSession = getHttpSession(session);
            sess = SocketIOManager.getInstance().createSession(httpSession);
        }
        setSession(sess);
    }
}
