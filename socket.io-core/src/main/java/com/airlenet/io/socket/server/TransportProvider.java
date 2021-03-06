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
package com.airlenet.io.socket.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import java.util.Collection;

/**
 * Transport factory
 *
 * @author Alexander Sova (bird@codeminders.com)
 */
public interface TransportProvider {

    /**
     * Creates all the transports
     *
     * @param config servlet configuration
     * @param context servlet context
     * @throws ServletException if init failed
     */
    void init(ServletConfig config, ServletContext context)
            throws ServletException;
    void destroy();

    /**
     *   Finds appropriate Transport class based on the rules defined at
     *   https://github.com/socketio/engine.io-protocol#transports
     *
     *   @param request incoming servlet request
     *   @return appropriate Transport object
     *   @throws UnsupportedTransportException no transport was found
     *   @throws SocketIOProtocolException invalid request was sent
     */
    Transport getTransport(ServletRequest request)
            throws UnsupportedTransportException, SocketIOProtocolException;

    Transport getTransport(TransportType type);
    Collection<Transport> getTransports();
}
