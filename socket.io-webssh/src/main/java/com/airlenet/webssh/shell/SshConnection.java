package com.airlenet.webssh.shell;

import com.airlenet.io.socket.common.SocketIOException;
import com.airlenet.io.socket.server.ConnectionException;
import com.airlenet.io.socket.server.ConnectionListener;
import com.airlenet.io.socket.server.Socket;
import com.airlenet.webssh.entity.DeviceEntity;
import com.airlenet.webssh.service.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class SshConnection implements ConnectionListener {
    private static final Logger logger = LoggerFactory.getLogger(SshConnection.class);
    private ExecutorService service = Executors.newFixedThreadPool(3);
    String consoleIp = "";
    Integer consolePort = 0;
    String consoleUsername = "";
    String consolePassword = "";

    @Autowired
    DeviceService deviceService;

    @Override
    public void onConnect(Socket socket) throws ConnectionException {
        logger.debug("onConnect sessionId={} ParameterMap={}", socket.getSession().getSessionId(), socket.getRequest().getParameterMap());
        String id = socket.getRequest().getParameter("id");
        DeviceEntity deviceEntity = deviceService.getPlaintextDevice(Long.parseLong(id));
        if (deviceEntity == null) {
            try {
                socket.emit("error", "DEVICE NOT FOUND" );
            } catch (SocketIOException e) {
                e.printStackTrace();
            }
        } else {
            consoleIp = deviceEntity.getIp();
            consolePort = deviceEntity.getPort();
            consoleUsername = deviceEntity.getUsername();
            consolePassword = deviceEntity.getPassword();

//        SocketShell socketShell = new SocketShell(consoleIp, consolePort, consoleUsername, consolePassword, socket);
//        socketShell.start();

            JschShell jschShell = new JschShell(consoleIp, consolePort, consoleUsername, consolePassword, socket);
            jschShell.start();
        }

    }
}
