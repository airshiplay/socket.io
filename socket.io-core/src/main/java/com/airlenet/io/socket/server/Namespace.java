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

import com.airlenet.io.socket.common.DisconnectReason;
import com.airlenet.io.socket.common.SocketIOException;

import java.util.*;

/**
 * @author Alexander Sova (bird@codeminders.com)
 */
public class Namespace implements Outbound, ConnectionListener, DisconnectListener
{
    private String                   id;

    private List<Socket>             sockets             = Collections.synchronizedList(new LinkedList<Socket>());
    private List<ConnectionListener> connectionListeners = Collections.synchronizedList(new LinkedList<ConnectionListener>());
    private Map<String, Room>        rooms               = Collections.synchronizedMap(new LinkedHashMap<String, Room>());

    Namespace(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    @Override
    public void emit(String name, Object... args)
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


    public void on(ConnectionListener listener)
    {
        connectionListeners.add(listener);
    }

    @Override
    public void onConnect(Socket socket)
            throws ConnectionException
    {
        for(ConnectionListener listener : connectionListeners)
            listener.onConnect(socket);
    }

    public Socket createSocket(Session session)
    {
        Socket socket = new Socket(session, this);
        socket.on(this);
        sockets.add(socket);

        return socket;
    }

    @Override
    public void onDisconnect(Socket socket, DisconnectReason reason, String errorMessage)
    {
        leaveAll(socket);
        sockets.remove(socket);
    }

    /**
     * Finds or creates a room.
     *
     * @param roomId room id
     * @return Room object
     */
    public Room room(String roomId)
    {
        Room room = rooms.get(roomId);
        if(room == null)
        {
            room = new Room(roomId);
            rooms.put(roomId, room);
        }
        return room;
    }

    /**
     * Finds or creates a room.
     *
     * @param roomId room id
     * @return Room object
     */
    public Room in(String roomId)
    {
        return room(roomId);
    }

    void leaveAll(Socket socket)
    {
        for (Room room : rooms.values())
        {
            if(room.contains(socket))
                room.leave(socket);
        }
    }

    public Iterable<Socket> getSockets()
    {
        return sockets;
    }
}
