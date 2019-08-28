package com.airlenet.webssh.namespace;

import com.airlenet.io.socket.server.transport.websocket.WebsocketIOServlet;
import com.airlenet.webssh.shell.DeviceSshConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SshWebSocketNamespace {
    private static final Logger logger = LoggerFactory.getLogger(SshWebSocketNamespace.class);
    private static final String namespace = "/console";
    private static final String room = "ssh";
    private static final String ANNOUNCEMENT = "announcement";       // server to all connected clients
    @Autowired
    WebsocketIOServlet websocketIOServlet;

    @Autowired
    DeviceSshConnection deviceSshConnection;
    @PostConstruct
    public void sshNamespace() {
        websocketIOServlet.of(namespace).on(deviceSshConnection);
    }
}
