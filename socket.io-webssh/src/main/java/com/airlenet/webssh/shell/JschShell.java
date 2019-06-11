package com.airlenet.webssh.shell;

import ch.ethz.ssh2.StreamGobbler;
import com.airlenet.io.socket.common.DisconnectReason;
import com.airlenet.io.socket.common.SocketIOException;
import com.airlenet.io.socket.server.DisconnectListener;
import com.airlenet.io.socket.server.EventListener;
import com.airlenet.io.socket.server.Socket;
import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class JschShell extends Thread {
    private static Logger logger = LoggerFactory.getLogger(JschShell.class);
    private final Socket socket;
    String consoleIp;
    Integer consolePort;
    String consoleUsername;
    String consolePassword;


    Lock lock = new ReentrantLock();

    private StepEnum stepEnum;

    private String userMsg;
    Condition condition;
    private OutputStream out;
    private InputStream in;
    private Channel channel;
    private BufferedInputStream stdout;
    private PrintWriter printWriter;
    private Integer cols = 20;
    private Integer rows = 8;

    public enum StepEnum {
        promptYesNo, promptPassword, Commandnteraction
    }

    public JschShell(String consoleIp, Integer consolePort, String consoleUsername, String consolePassword, Socket socket) {
        this.consoleIp = consoleIp;
        this.consolePort = consolePort;
        this.consoleUsername = consoleUsername;
        this.consolePassword = consolePassword;
        this.socket = socket;
        condition = lock.newCondition();
        socket.on("data", new EventListener() {
            @Override
            public Object onEvent(String name, Object[] args, boolean ackRequested) {
                String msg = args[0].toString();
                if (stepEnum == StepEnum.promptYesNo) {
                    try {
                        socket.emit("data", msg.equals("\r") ? "\r\n" : msg);
                    } catch (SocketIOException e) {
                        e.printStackTrace();
                    }
                    if (msg.equals("\r")) {
                        lock.lock();
                        try {
                            condition.signal();
                        } finally {
                            lock.unlock();
                        }
                    } else {
                        userMsg = userMsg + msg;
                    }
                } else if (stepEnum == StepEnum.promptPassword) {
                    if (msg.equals("\r")) {
                        try {
                            socket.emit("data", "\r\n");
                        } catch (SocketIOException e) {
                            e.printStackTrace();
                        }
                        lock.lock();
                        try {
                            condition.signal();
                        } finally {
                            lock.unlock();
                        }
                    } else {
                        userMsg = userMsg + msg;
                    }
                } else {
                    if (printWriter != null) {
                        printWriter.write(msg);
                        printWriter.flush();
                    }
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
                cols = (Integer) args[0];
                rows = (Integer) args[1];
                return "OK";
            }
        });
        socket.on("resize", new EventListener() {
            @Override
            public Object onEvent(String name, Object[] args, boolean ackRequested) {
                Map map = (Map) args[0];
                rows = Integer.valueOf(map.get("rows").toString());
                cols = Integer.valueOf(map.get("cols").toString());
                if (channel != null)
                    ((ChannelShell) channel).setPtySize(cols, rows, cols * 80, rows * 24);
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

    @Override
    public void run() {
        Session session = null;
        try {
            Thread.currentThread().getName();
            JSch jSch = new JSch();
            session = jSch.getSession(consoleUsername, consoleIp, consolePort);
            session.setTimeout(60000);
            session.setUserInfo(new UserInfo() {
                @Override
                public String getPassphrase() {
                    return null;
                }

                @Override
                public String getPassword() {
                    return consolePassword;
                }

                @Override
                public boolean promptPassword(String s) {
                    if (StringUtils.isEmpty(consolePassword)) {
                        try {
                            socket.emit("data", s + ":");
                            stepEnum = StepEnum.promptPassword;
                            lock.lock();
                            try {
                                userMsg = "";
                                condition.await();
                                consolePassword = userMsg;
                                return true;
                            } catch (InterruptedException e) {
                                logger.error("", e);
                            } finally {
                                lock.unlock();
                            }
                        } catch (SocketIOException e) {
                            logger.error("", e);
                        }
                        return false;
                    } else {
                        return true;
                    }
                }

                @Override
                public boolean promptPassphrase(String s) {
                    return false;
                }

                @Override
                public boolean promptYesNo(String s) {
                    if (StringUtils.isEmpty(consolePassword)) {
                        try {
                            socket.emit("data", s.replace("\n", "\r\n").replace("?", " (yes/no)? "));
                            stepEnum = StepEnum.promptYesNo;
                            lock.lock();
                            try {
                                userMsg = "";
                                condition.await();
                                if ("yes".equals(userMsg) || "y".equals(userMsg)) {
                                    return true;
                                }
                            } catch (InterruptedException e) {
                                logger.error("", e);
                            } finally {
                                lock.unlock();
                            }
                            return false;
                        } catch (SocketIOException e) {
                            logger.error("", e);
                        } catch (Exception e) {
                            logger.error("", e);
                        }
                        return false;
                    } else {
                        return true;
                    }

                }

                @Override
                public void showMessage(String s) {
                    logger.info(s);
                }
            });
            session.connect(60000);
            session.setServerAliveInterval(60000);
            channel = session.openChannel("shell");
            channel.connect();
            ((ChannelShell) channel).setPtySize(cols, rows, cols * 80, rows * 24);
            send();
            stepEnum = StepEnum.Commandnteraction;

            //获取标准输出
            stdout = new BufferedInputStream(new StreamGobbler(channel.getInputStream()));

            //获取标准输入
            printWriter = new PrintWriter(channel.getOutputStream());


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

        } catch (JSchException e) {
            logger.error("", e);
        } catch (IOException e) {
            logger.error("", e);
        } finally {

            if (printWriter != null)
                printWriter.close();
            if (stdout != null) {
                try {
                    stdout.close();
                } catch (IOException e) {
                    logger.error("close", e);
                }
            }
            if (session != null){
                if(session.isConnected()){
                    session.disconnect();
                }
            }
            if (channel != null)
                channel.disconnect();
        }
    }

    public void send() throws SocketIOException {
        socket.emit("menu", "<a id=\"logBtn\"><i class=\"fas fa-clipboard fa-fw\"></i> Start Log</a><a id=\"downloadLogBtn\"><i class=\"fas fa-download fa-fw\"></i> Download Log</a>");

        socket.emit("allowreauth", true);

        socket.emit("setTerminalOpts", "{\"cursorBlink\":true,\"scrollback\":10000,\"tabStopWidth\":8,\"bellStyle\":\"sound\"}");

        socket.emit("title", "ssh://" + consoleIp);

        socket.emit("headerBackground", "green");

        socket.emit("status", "SSH CONNECTION ESTABLISHED");

        socket.emit("statusBackground", "green");

        socket.emit("footer", "ssh://" + consoleUsername + "@" + consoleIp + ":" + consolePort);
        socket.emit("status", "SSH CONNECTION ESTABLISHED");

        socket.emit("statusBackground", "green");
        socket.emit("allowreplay", true);
    }

    public void close() {
        if (printWriter != null)
            printWriter.close();
        if (stdout != null) {
            try {
                stdout.close();
            } catch (IOException e) {
                logger.error("close", e);
            }
        }
        if (channel != null)
            channel.disconnect();
    }
}
