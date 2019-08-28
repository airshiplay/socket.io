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
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class DeviceSshConnection implements ConnectionListener {
    private static final Logger logger = LoggerFactory.getLogger(DeviceSshConnection.class);
    private ExecutorService service = Executors.newFixedThreadPool(3);
    String consoleIp = null;
    Integer consolePort = 22;
    String consoleUsername = null;
    String consolePassword = null;

    @Autowired
    DeviceService deviceService;

    @Override
    public void onConnect(Socket socket) throws ConnectionException {
        Map<String, String[]> parameterMap = socket.getRequest().getParameterMap();
        logger.debug("onConnect sessionId={} Request= {} getConnection={} ParameterMap={}", socket.getSession().getSessionId(), socket.getRequest(), socket.getSession().getConnection(), parameterMap);
        String id = getParameter(parameterMap,"id");
        logger.debug("Request= {}  getConnection={} ", socket.getRequest(),socket.getSession().getConnection());
        String host = getParameter(parameterMap,"host");
        String port =  getParameter(parameterMap,"port");
        if (StringUtils.isEmpty(host)) {
            DeviceEntity deviceEntity = deviceService.getPlaintextDevice(Long.parseLong(id));
            if (deviceEntity == null) {
                try {
                    socket.emit("error", "DEVICE NOT FOUND");
                } catch (SocketIOException e) {
                    e.printStackTrace();
                }
                return;
            } else {
                consoleIp = deviceEntity.getIp();
                consolePort = deviceEntity.getPort();
                consoleUsername = deviceEntity.getUsername();
                consolePassword = deviceEntity.getPassword();
            }
        } else {
            consoleIp = host;
            consolePort = StringUtils.isEmpty(port) ? 22 : Integer.parseInt(port);
            consoleUsername = "root";
        }
//        SocketShell socketShell = new SocketShell(consoleIp, consolePort, consoleUsername, consolePassword, socket);
//        socketShell.start();

        JschShell jschShell = new JschShell(consoleIp, consolePort, consoleUsername, consolePassword, socket);
        jschShell.start();


    }
    public String getParameter( Map<String, String[]> parameterMap,String name) {

        String[] value = (String[])parameterMap.get(name);
        return value == null ? null : value[0];
    }
}
