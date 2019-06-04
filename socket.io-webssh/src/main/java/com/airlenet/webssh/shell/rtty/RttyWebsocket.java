package com.airlenet.webssh.shell.rtty;

import com.airlenet.webssh.service.CacheService;
import com.airlenet.webssh.service.DeviceService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.Map;

@Component
@ServerEndpoint(value = "/ws", configurator = WebsshConfigurator.class)
@Slf4j
public class RttyWebsocket {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private CacheService cacheService;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) throws Exception {
        Map<String, List<String>> requestParameterMap = session.getRequestParameterMap();
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        boolean device = isDevice(session);
        String uuid = getDeviceId(session);
        String sessionId = session.getId();
        RttyConnect rttyConnect = new RttyConnect(uuid, session);

        if (device) {
            String desc = requestParameterMap.get("description").get(0);
            RttyDevice rttyDevice = cacheService.putRttyDevice(uuid, new RttyDevice(rttyConnect, desc, null));
            long keepalive = Long.parseLong(requestParameterMap.get("keepalive").get(0));

        } else {
            RttyUser rttyUser = new RttyUser(rttyConnect);
            RttyDevice rttyDevice = cacheService.getRttyDevice(uuid);
            if (rttyDevice == null) {
                return;
            }

            RttyDeviceUserSession rttyDeviceUserSession = new RttyDeviceUserSession();
            rttyDeviceUserSession.setRttyDevice(rttyDevice);
            rttyDeviceUserSession.setRttyUser(rttyUser);
            rttyDevice.getDeviceUserSession().put(rttyUser.getSid(), rttyDeviceUserSession);
            session.getUserProperties().put("sid", rttyUser.getSid());
            rttyDevice.getDeviceConnect().sendText("{\"type\":\"login\",\"sid\":\"" + rttyUser.getSid() + "\"}");
        }
    }


    @OnMessage
    public void onMessage(Session session, String text) {
        boolean device = isDevice(session);
        String uuid = getDeviceId(session);
        com.alibaba.fastjson.JSONObject parse = JSON.parseObject(text);
        RttyDevice rttyDevice = cacheService.getRttyDevice(uuid);
        if (device) {//收到设备消息，发向用户
            String type = parse.getString("type");
            if ("cmd".equals(type)) {

            } else {
                int devSid = parse.getIntValue("sid");

                rttyDevice.getDeviceUserSession().get(devSid).getRttyUser().getConnect().sendText(text);
            }
        } else {//收到用户消息，发向设备
            rttyDevice.getDeviceConnect().sendText(text);
        }
    }

    @OnMessage
    public void onMessage(Session session, byte[] message) throws IOException {
        boolean device = isDevice(session);
        String uuid = getDeviceId(session);
        RttyDevice rttyDevice = cacheService.getRttyDevice(uuid);
        if (device) {//收到设备消息，发向用户
            Integer devsid = new Integer(message[0]);
            byte[] dest = new byte[message.length - 1];
            System.arraycopy(message, 1, dest, 0, dest.length);
            rttyDevice.getDeviceUserSession().get(devsid).getRttyUser().getConnect().sendBinary(ByteBuffer.wrap(dest));
        } else {//收到用户消息，发向设备
            Integer sid = (Integer) session.getUserProperties().get("sid");
            ByteBuffer allocateDirect = ByteBuffer.allocate(message.length + 1);
            allocateDirect.put(sid.byteValue());
            allocateDirect.put(message);
            rttyDevice.getDeviceUserSession().get(sid).getRttyDevice().getDeviceConnect().sendBinary(allocateDirect);
        }
    }

    @OnMessage
    public synchronized void onPong(Session session, PongMessage message) {
        try {
            session.getBasicRemote().sendPong(message.getApplicationData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        log.error("onClose {} {}", session.getRequestParameterMap(), closeReason.toString());
        boolean device = isDevice(session);
        String uuid = getDeviceId(session);
        if (device) {
//            cacheService.clearRttyDevice(uuid);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("onError {} {}", session.getRequestParameterMap(), error);
        boolean device = isDevice(session);
        String uuid = getDeviceId(session);

    }

    public boolean isDevice(Session session) {
        boolean device = session.getRequestParameterMap().get("device") == null ? false : !"".equals(session.getRequestParameterMap().get("device").get(0));
        return device;
    }

    public String getDeviceId(Session session) {
        return session.getRequestParameterMap().get("devid").get(0);
    }

    public ByteBuffer read(InputStream istream) throws IOException {
        int bufferSize = 4096;
        int writeBufferSize = 4096;
        ReadableByteChannel source = Channels.newChannel(istream);
        ByteArrayOutputStream ostream = new ByteArrayOutputStream(bufferSize);
        WritableByteChannel destination = Channels.newChannel(ostream);
        ByteBuffer buffer = ByteBuffer.allocateDirect(writeBufferSize);
        while (source.read(buffer) != -1) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                destination.write(buffer);
            }
            buffer.clear();
        }
        ByteBuffer wrap = ByteBuffer.wrap(ostream.toByteArray());

        source.close();
        destination.close();
        return wrap;
    }
}
