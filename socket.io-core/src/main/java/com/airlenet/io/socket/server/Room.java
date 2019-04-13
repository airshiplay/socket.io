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

import com.airlenet.io.socket.common.SocketIOException;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author Alexander Sova (bird@codeminders.com)
 */
public class Room implements Outbound
{
    private String id;
    private Deque<Socket> sockets = new ConcurrentLinkedDeque<>();

    Room(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    @Override
    public void emit(String name, Object... args) throws SocketIOException
    {
        for(Socket s : sockets)
        {
            try
            {
                s.emit(name, args);
            }
            catch (SocketIOException e)
            {
                // ignore for now
                // TODO: add getLastError method?
            }
        }
    }

    public void join(Socket socket)
    {
        sockets.add(socket);
    }

    public void leave(Socket socket)
    {
        sockets.remove(socket);
    }

    public boolean contains(Socket socket)
    {
        return sockets.contains(socket);
    }

    public void broadcast(Socket sender, String name, Object... args)
            throws SocketIOException
    {
        for (Socket socket: sockets)
        {
            if (socket != sender)
                socket.emit(name, args);
        }
    }

    public Iterable<Socket> getSockets()
    {
        return sockets;
    }
}
