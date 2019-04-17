package com.airlenet.webssh.ssh;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.airlenet.io.socket.common.DisconnectReason;
import com.airlenet.io.socket.common.SocketIOException;
import com.airlenet.io.socket.server.DisconnectListener;
import com.airlenet.io.socket.server.EventListener;
import com.airlenet.io.socket.server.Socket;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wolfcode-lanxw
 */

public final class SSHAgent {
    private static final Logger logger =LoggerFactory.getLogger(SSHAgent.class);
    private Connection connection;
    private Session session;
    private BufferedReader stdout;
    public PrintWriter printWriter;
    private BufferedReader stderr;
    private String input;
    private ExecutorService service = Executors.newFixedThreadPool(3);

    public void initSession(String hostName, String userName, String passwd, Socket socket) throws IOException {
        //根据主机名先获取一个远程连接
        connection = new Connection(hostName);
        //发起连接
        connection.connect();
        //认证账号密码
        boolean authenticateWithPassword = connection.authenticateWithPassword(userName, passwd);
        //如果账号密码有误抛出异常
        if (!authenticateWithPassword) {
            throw new RuntimeException("Authentication failed. Please check hostName, userName and passwd");
        }
        //开启一个会话
        session = connection.openSession();

        //获取标准输出
        stdout = new BufferedReader(new InputStreamReader(new StreamGobbler(session.getStdout()), StandardCharsets.UTF_8));
        //获取标准错误输出
        stderr = new BufferedReader(new InputStreamReader(new StreamGobbler(session.getStderr()), StandardCharsets.UTF_8));
        //获取标准输入
        printWriter = new PrintWriter(session.getStdin());


    }

    public void execCommand(final Socket socket) throws IOException {

        socket.on("connection", new EventListener() {
            @Override
            public Object onEvent(String name, Object[] args, boolean ackRequested) {
                return null;
            }
        });
        //执行命令方法，使用线程池来执行
        service.submit(new Runnable() {
            @Override
            public void run() {
                String line;
                boolean a =false;
                try {

                    //持续获取服务器标准输出
                    while ((line = stdout.readLine()) != null) {
                        a= stdout.ready();
                        socket.emit("data", "\r\n");
                        socket.emit("data", line);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        session.requestPTY("xterm-color", 80, 24, 640, 480, null);
        session.startShell();
        socket.emit("menu", "<a id=\"logBtn\"><i class=\"fas fa-clipboard fa-fw\"></i> Start Log</a><a id=\"downloadLogBtn\"><i class=\"fas fa-download fa-fw\"></i> Download Log</a>");
        //42["allowreauth",true]
        socket.emit("allowreauth", true);
        //42["setTerminalOpts",{"cursorBlink":true,"scrollback":10000,"tabStopWidth":8,"bellStyle":"sound"}]
        socket.emit("setTerminalOpts", "{\"cursorBlink\":true,\"scrollback\":10000,\"tabStopWidth\":8,\"bellStyle\":\"sound\"}");

        //42["title","ssh://172.19.8.251"]
        socket.emit("title", "ssh://172.19.8.251");

        //42["headerBackground","green"]
        socket.emit("headerBackground", "green");
        //42["status","SSH CONNECTION ESTABLISHED"]
        socket.emit("status", "SSH CONNECTION ESTABLISHED");
        //42["statusBackground","green"]
        socket.emit("statusBackground", "green");
        //42["footer","ssh://root@172.19.8.251:22"]
        socket.emit("footer", "ssh://root@172.19.8.251:22");
        //42["status","SSH CONNECTION ESTABLISHED"]
        socket.emit("status", "SSH CONNECTION ESTABLISHED");


        //42["statusBackground","green"]
        socket.emit("statusBackground", "green");


        //42["allowreplay",true]
        socket.emit("allowreplay", true);



        socket.on("data", new EventListener() {
            @Override
            public Object onEvent(String name, Object[] args, boolean ackRequested) {
                socket.getSession().getAttribute("sshAgent");
                input = args[0].toString();

                if (input.equals("\r")) {
                    printWriter.write(input+"\n");
                    printWriter.flush();
                } else {
                    printWriter.write(input);
                    try {
                        socket.emit("data", input);
                    } catch (SocketIOException e) {
                        e.printStackTrace();
                    }
                }


                return "OK";
            }
        });
        socket.on(new DisconnectListener() {
            @Override
            public void onDisconnect(Socket socket, DisconnectReason reason, String errorMessage) {
                logger.debug("Disconnect");
                close();
            }
        });
    }

    //关闭资源方法
    public void close() {
        IOUtils.closeQuietly(stdout);
        IOUtils.closeQuietly(stderr);
        IOUtils.closeQuietly(printWriter);
        session.close();
        connection.close();
    }
}