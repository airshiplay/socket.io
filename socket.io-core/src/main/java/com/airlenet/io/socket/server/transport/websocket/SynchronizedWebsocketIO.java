/**
 * The MIT License
 * Copyright (c) 2018 Alex Saveliev (lyolik@codeminders.com)
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

import java.io.IOException;

/**
 * @author Alex Saveliev (lyolik@codeminders.com)
 */
public class SynchronizedWebsocketIO extends WebsocketIO {

    public SynchronizedWebsocketIO(javax.websocket.Session remoteEndpoint) {
        super(remoteEndpoint);
    }

    public synchronized void sendString(String data) throws IOException {
        super.sendString(data);
    }

    public synchronized void sendBinary(byte[] data) throws IOException {
        super.sendBinary(data);
    }

    public void disconnect() throws IOException {
        remoteEndpoint.close();
    }
}
