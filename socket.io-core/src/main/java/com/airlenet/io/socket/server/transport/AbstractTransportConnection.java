/**
 * The MIT License
 * Copyright (c) 2010 Tad Glines
 *
 * Contributors: Ovea.com, Mycila.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.airlenet.io.socket.server.transport;

import com.airlenet.io.socket.server.*;
import com.airlenet.io.socket.common.ConnectionState;
import com.airlenet.io.socket.common.DisconnectReason;
import com.airlenet.io.socket.common.SocketIOException;
import com.airlenet.io.socket.protocol.SocketIOPacket;
import com.airlenet.io.socket.protocol.SocketIOProtocol;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Mathieu Carbou
 * @author Alexander Sova (bird@codeminders.com)
 */
public abstract class AbstractTransportConnection implements TransportConnection
{
    private static final Logger LOGGER = Logger.getLogger(AbstractTransportConnection.class.getName());

    private Config config;
    private Session session;
    private Transport transport;
    private HttpServletRequest request;
    private Map<String, String[]> parameterMap;
    public AbstractTransportConnection(Transport transport)
    {
        this.transport = transport;
    }

    @Override
    public final void init(Config config) {
        this.config = config;
        init();
    }

    @Override
    public Transport getTransport()
    {
        return transport;
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }

    protected final Config getConfig() {
        return config;
    }

    public Session getSession() {
        return session;
    }

    protected void init()
    {
    }

    @Override
    public void disconnect(String namespace, boolean closeConnection)
    {
        try
        {
            send(SocketIOProtocol.createDisconnectPacket(namespace));
            getSession().setDisconnectReason(DisconnectReason.DISCONNECT);
        }
        catch (SocketIOException e)
        {
            getSession().setDisconnectReason(DisconnectReason.CLOSE_FAILED);
        }

        if(closeConnection)
            abort();
    }

    @Override
    public void emit(String namespace, String name, Object... args)
            throws SocketIOException
    {
        if (getSession().getConnectionState() != ConnectionState.CONNECTED)
            throw new SocketIOClosedException();

        ACKListener ack_listener = null;
        if(args.length > 0 && args[args.length-1] instanceof ACKListener)
        {
            ack_listener = (ACKListener)args[args.length-1];
            args = Arrays.copyOfRange(args, 0, args.length-1);
        }

        int packet_id = -1;
        if(ack_listener != null)
            packet_id = getSession().getNewPacketId();

        SocketIOPacket packet = SocketIOProtocol.createEventPacket(packet_id, namespace, name, args);

        if(packet_id >= 0)
            getSession().subscribeACK(packet_id, ack_listener);

        send(packet);
    }

    @Override
    public HttpServletRequest getRequest()
    {
        return request;
    }
    @Override
    public Map<String, String[]> getParameterMap(){
            return parameterMap;
    }

    public void setRequest(HttpServletRequest request)
    {
        this.request = request;
        this.parameterMap =request.getParameterMap();
    }
}
