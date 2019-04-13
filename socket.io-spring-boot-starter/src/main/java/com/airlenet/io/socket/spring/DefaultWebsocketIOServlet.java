package com.airlenet.io.socket.spring;


import com.airlenet.io.socket.server.transport.websocket.WebsocketIOServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

//@WebServlet(name = "socket.io", urlPatterns = "/socket.io/*")
public class DefaultWebsocketIOServlet extends WebsocketIOServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
}
