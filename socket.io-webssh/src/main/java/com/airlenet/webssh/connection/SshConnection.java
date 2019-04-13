package com.airlenet.webssh.connection;

import com.airlenet.io.socket.common.SocketIOException;
import com.airlenet.io.socket.server.ConnectionException;
import com.airlenet.io.socket.server.ConnectionListener;
import com.airlenet.io.socket.server.Socket;
import com.airlenet.webssh.ssh.SSHAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class SshConnection implements ConnectionListener {
    private static final Logger logger = LoggerFactory.getLogger(SshConnection.class);
    private ExecutorService service = Executors.newFixedThreadPool(3);

    @Override
    public void onConnect(Socket socket) throws ConnectionException {

        logger.debug("onConnect", this + " sessionId=" + socket.getSession().getSessionId());
        try {
            SSHAgent sshAgent = new SSHAgent();
            socket.getSession().setAttribute("sshAgent", sshAgent);

            sshAgent.initSession("172.19.8.251", "root", "123456",socket);
            sshAgent.execCommand(socket);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {


//
//                        // 42["data","Last login: Sat Apr 13 11:01:28 2019 from 172.19.8.199\r\r\n"]
//                        socket.emit("data", "Last login: Sat Apr 13 11:01:28 2019 from 172.19.8.199\r\r\n");
//
//
//                        // 42["data","\u001b]0;root@vcpe01:~\u0007"]
//                        socket.emit("data", "\u001b]0;root@vcpe01:~\u0007");
//
//
//                        //42["data","[root@vcpe01 ~]# "]
//                        socket.emit("data", "[root@vcpe01 ~]# ");
//                        //

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();


        } catch (SocketIOException e) {
            socket.disconnect(true);
        } catch (IOException e) {
            socket.disconnect(true);
        }
    }
}
