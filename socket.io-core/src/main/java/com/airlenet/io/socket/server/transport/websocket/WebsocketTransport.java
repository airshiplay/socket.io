/**
 * The MIT License
 * Copyright (c) 2010 Tad Glines
 * Copyright (c) 2015 Alexander Sova (bird@codeminders.com)
 * <p/>
 * Contributors: Ovea.com, Mycila.com
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

import com.airlenet.io.socket.server.SocketIOManager;
import com.airlenet.io.socket.server.TransportConnection;
import com.airlenet.io.socket.server.TransportType;
import com.airlenet.io.socket.server.transport.AbstractTransport;
import com.airlenet.io.socket.server.transport.AbstractTransportConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public final class WebsocketTransport extends AbstractTransport
{
    private static final Logger LOGGER = Logger.getLogger(WebsocketTransport.class.getName());

    @Override
    public TransportType getType()
    {
        return TransportType.WEB_SOCKET;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       SocketIOManager sessionManager) throws IOException
    {

        if(!"GET".equals(request.getMethod()))
        {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "Only GET method is allowed for websocket transport");
            return;
        }

        if (request.getHeader("Sec-WebSocket-Key") == null) {

            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Missing request header 'Sec-WebSocket-Key'");
            return;
        }

        final TransportConnection connection = getConnection(request, sessionManager);

        // a bit hacky but safe since we know the type of TransportConnection here
        ((AbstractTransportConnection)connection).setRequest(request);
    }

    @Override
    public TransportConnection createConnection()
    {
        return new WebsocketTransportConnection(this);
    }
}
