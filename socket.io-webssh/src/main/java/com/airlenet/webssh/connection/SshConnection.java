package com.airlenet.webssh.connection;

import com.airlenet.io.socket.server.ConnectionException;
import com.airlenet.io.socket.server.ConnectionListener;
import com.airlenet.io.socket.server.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class SshConnection implements ConnectionListener {
    private static final Logger logger = LoggerFactory.getLogger(SshConnection.class);
    private ExecutorService service = Executors.newFixedThreadPool(3);
    String consoleIp = "172.19.8.251";
    Integer consolePort = 22;
    String consoleUsername = "root";
    String consolePassword = "123456";

    @Override
    public void onConnect(Socket socket) throws ConnectionException {
        logger.debug("onConnect", this + " sessionId=" + socket.getSession().getSessionId());

        SocketShell socketShell = new SocketShell(consoleIp, consolePort, consoleUsername, consolePassword, socket);
        socketShell.start();

    }
}
