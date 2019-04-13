package com.airlenet.io.socket.server.transport;

import com.airlenet.io.socket.protocol.BinaryPacket;
import com.airlenet.io.socket.server.SocketIOProtocolException;
import com.airlenet.io.socket.server.Transport;
import com.airlenet.io.socket.common.SocketIOException;
import com.airlenet.io.socket.protocol.EngineIOPacket;
import com.airlenet.io.socket.protocol.EngineIOProtocol;
import com.airlenet.io.socket.protocol.SocketIOPacket;
import com.google.common.io.CharStreams;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Alexander Sova (bird@codeminders.com)
 */
public class XHRTransportConnection extends AbstractTransportConnection
{
    private static final String ALLOWED_ORIGINS   = "allowedOrigins";
    private static final String ALLOW_ALL_ORIGINS = "allowAllOrigins";

    private static final Logger LOGGER = Logger.getLogger(XHRTransportConnection.class.getName());

    private BlockingQueue<EngineIOPacket> packets = new LinkedBlockingDeque<>();

    private boolean done = false;

    public XHRTransportConnection(Transport transport)
    {
        super(transport);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        if(done)
            return;

        // store request for end-user to check for cookies, or user-agent or something else
        setRequest(request);

        if(getConfig().getBoolean(ALLOW_ALL_ORIGINS, false))
        {
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }
        else
        {
            String origins = getConfig().getString(ALLOWED_ORIGINS);
            if (origins != null)
            {
                response.setHeader("Access-Control-Allow-Origin", origins);
                response.setHeader("Access-Control-Allow-Credentials", "true");
            }
        }

        if ("POST".equals(request.getMethod())) //incoming
        {
            response.setContentType("text/plain");

            String contentType = request.getContentType();
            if (contentType.startsWith("text/"))
            {
                // text encoding
                String payload = CharStreams.toString(request.getReader());

                for (EngineIOPacket packet : EngineIOProtocol.decodePayload(payload))
                    getSession().onPacket(packet, this);
            }
            else
            if (contentType.startsWith("application/octet-stream"))
            {
                // binary encoding
                for (EngineIOPacket packet : EngineIOProtocol.binaryDecodePayload(request.getInputStream()))
                    getSession().onPacket(packet, this);
            }
            else
            {
                throw new SocketIOProtocolException("Unsupported request content type for incoming polling request: " + contentType);
            }
            response.getWriter().print("ok");
        }
        else if ("GET".equals(request.getMethod())) //outgoing
        {
            response.setContentType("application/octet-stream");
            try
            {

                OutputStream os = response.getOutputStream();
                for (EngineIOPacket packet = packets.poll(3, TimeUnit.MINUTES); packet != null; packet = packets.poll())
                {
                    if(done)
                        break;
                    EngineIOProtocol.binaryEncode(packet, os);
                }

                response.flushBuffer();
            }
            catch (InterruptedException e)
            {
                if (LOGGER.isLoggable(Level.FINE))
                    LOGGER.log(Level.FINE, "Polling connection interrupted", e);
            }
        }
        else if(!"OPTIONS".equals(request.getMethod()))
        {
            // OPTIONS is CORS pre-flight request
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    @Override
    public void abort()
    {
        try
        {
            done = true;
            send(EngineIOProtocol.createNoopPacket());
        }
        catch (SocketIOException e)
        {
            // ignore
        }
    }

    @Override
    public void send(EngineIOPacket packet) throws SocketIOException
    {
        packets.add(packet);
    }

    @Override
    public void send(SocketIOPacket packet) throws SocketIOException
    {
        send(EngineIOProtocol.createMessagePacket(packet.encode()));
        if(packet instanceof BinaryPacket)
        {
            for (InputStream is : ((BinaryPacket)packet).getAttachments())
                send(EngineIOProtocol.createMessagePacket(is));
        }
    }
}
