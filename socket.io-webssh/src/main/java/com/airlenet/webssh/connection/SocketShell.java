package com.airlenet.webssh.connection;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.airlenet.io.socket.common.DisconnectReason;
import com.airlenet.io.socket.common.SocketIOException;
import com.airlenet.io.socket.server.DisconnectListener;
import com.airlenet.io.socket.server.EventListener;
import com.airlenet.io.socket.server.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * @author airlenet
 */
public class SocketShell extends Thread {
    private static Logger logger = LoggerFactory.getLogger(SocketShell.class);
    private final Socket socket;
    private Connection connection;
    private Session session;
    private BufferedInputStream stdout;
    public PrintWriter printWriter;
    private BufferedInputStream stderr;
    String consoleIp;
    Integer consolePort;
    String consoleUsername;
    String consolePassword;
    private int termCols;
    private int termRows;

    public SocketShell(String consoleIp, Integer consolePort, String consoleUsername, String consolePassword, Socket socket) {
        logger.debug("SocketShell" + " consoleIp:" + consoleIp + " consolePort:" + consolePort + consoleUsername + consolePassword + socket);
        this.consoleIp = consoleIp;
        this.consolePort = consolePort;
        this.consoleUsername = consoleUsername;
        this.consolePassword = consolePassword;
        this.socket = socket;
        socket.on("data", new EventListener() {
            @Override
            public Object onEvent(String name, Object[] args, boolean ackRequested) {
                String msg = args[0].toString();
                if (printWriter != null) {
                    printWriter.write(msg);
                    printWriter.flush();
                }
                return "OK";
            }
        });
        socket.on(new DisconnectListener() {
            @Override
            public void onDisconnect(Socket socket, DisconnectReason reason, String errorMessage) {
                close();
                socket.disconnect(true);
            }
        });
        socket.on("connection", new EventListener() {
            @Override
            public Object onEvent(String name, Object[] args, boolean ackRequested) {
                return "OK";
            }
        });

        socket.on("geometry", new EventListener() {
            @Override
            public Object onEvent(String name, Object[] args, boolean ackRequested) {
                return "OK";
            }
        });
        socket.on("resize", new EventListener() {
            @Override
            public Object onEvent(String name, Object[] args, boolean ackRequested) {
                Map map = (Map) args[0];
                map.get("rows");
                map.get("cols");
//                stream.setWindow(map..rows, data.cols)
                return "OK";
            }
        });
        socket.on("disconnecting", new EventListener() {
            @Override
            public Object onEvent(String name, Object[] args, boolean ackRequested) {
                //debugWebSSH2('SOCKET DISCONNECTING: ' + reason)
                try {
                    socket.emit("ssherror", "SOCKET DISCONNECTING:" + args[0]);
                } catch (SocketIOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        socket.on("disconnect", new EventListener() {
            @Override
            public Object onEvent(String name, Object[] args, boolean ackRequested) {
                try {
                    socket.emit("ssherror", "SOCKET DISCONNECT:" + args[0]);
                } catch (SocketIOException e) {
                    e.printStackTrace();
                }
                close();
                return null;
            }
        });
        socket.on("error", new EventListener() {
            @Override
            public Object onEvent(String name, Object[] args, boolean ackRequested) {
                try {
                    socket.emit("ssherror", "SOCKET ERROR" + args[0]);
                } catch (SocketIOException e) {
                    e.printStackTrace();
                }
                close();
                socket.disconnect(true);
                return null;
            }
        });
    }

    public void send() throws SocketIOException {
        socket.emit("menu", "<a id=\"logBtn\"><i class=\"fas fa-clipboard fa-fw\"></i> Start Log</a><a id=\"downloadLogBtn\"><i class=\"fas fa-download fa-fw\"></i> Download Log</a>");

        socket.emit("allowreauth", true);

        socket.emit("setTerminalOpts", "{\"cursorBlink\":true,\"scrollback\":10000,\"tabStopWidth\":8,\"bellStyle\":\"sound\"}");

        //42["title","ssh://172.19.8.251"]
//        socket.emit("title", "ssh://172.19.8.251");

        socket.emit("headerBackground", "green");

        socket.emit("status", "SSH CONNECTION ESTABLISHED");

        socket.emit("statusBackground", "green");

        socket.emit("footer", "ssh://" + consoleUsername + "@" + consoleIp + ":" + consolePort);
        socket.emit("status", "SSH CONNECTION ESTABLISHED");

        socket.emit("statusBackground", "green");
        socket.emit("allowreplay", true);
    }

    public void sshShell(String hostName, String userName, String passwd) throws IOException {
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
//        //开启一个会话
//        session = connection.openSession();
//        //获取标准输出
//        stdout = new BufferedReader(new InputStreamReader(new StreamGobbler(session.getStdout()), StandardCharsets.UTF_8));
//        //获取标准错误输出
//        stderr = new BufferedReader(new InputStreamReader(new StreamGobbler(session.getStderr()), StandardCharsets.UTF_8));
//        //获取标准输入
//        printWriter = new PrintWriter(session.getStdin());
//
//
//        session.requestPTY("xterm-color", 80, 24, 640, 480, null);
//        session.startShell();
//        List<String> strings = stdout.lines().collect(Collectors.toList());
//        logger.debug(strings + "");
    }

    public void start() {
        super.start();
    }

    @Override
    public void run() {
        //根据主机名先获取一个远程连接
        connection = new Connection(consoleIp);
        //发起连接
        try {
            connection.connect();
            //认证账号密码
            boolean authenticateWithPassword = connection.authenticateWithPassword(consoleUsername, consolePassword);
            //如果账号密码有误抛出异常
            if (!authenticateWithPassword) {
                socket.emit("401 UNAUTHORIZED");
                throw new RuntimeException("Authentication failed. Please check hostName, userName and passwd");
            }
            session = connection.openSession();
            //获取标准输出
            stdout = new BufferedInputStream(new StreamGobbler(session.getStdout()));
            //获取标准错误输出
            stderr = new BufferedInputStream(new StreamGobbler(session.getStderr()));

            //获取标准输入
            printWriter = new PrintWriter(session.getStdin());

            session.requestPTY("xterm-color", 80, 24, 640, 480, null);
            session.startShell();

            send();
            byte[] buf = new byte[1024];
            int len = 0;
            StringBuffer sb = new StringBuffer();
            StringWriter stringWriter = new StringWriter();
            while ((len = stdout.read(buf)) > 0) {
                if (stdout.available() > 0) {
                    sb.append(new String(buf, 0, len));
                } else {
                    sb.append(new String(buf, 0, len));
                    socket.emit("data", sb.toString());
                    sb = new StringBuffer();
                }
            }

        } catch (IOException e) {
            logger.error("", e);
            try {
                socket.emit("ssherror", "SOCKET ERROR" + e.getMessage());
            } catch (SocketIOException e1) {
            }
            close();
            socket.disconnect(true);
        }
    }

    public void close() {
        if (printWriter != null)
            printWriter.close();
        if (stdout != null) {
            try {
                stdout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (connection != null)
            connection.close();
    }
}
