package com.airlenet.webssh.shell.rtty;

import com.airlenet.webssh.service.CacheService;
import com.airlenet.webssh.service.DeviceService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@ServerEndpoint(value = "/ws", configurator = WebsshConfigurator.class)
@Slf4j
public class RttyWebsocket {
    private static Logger logger = LoggerFactory.getLogger(RttyWebsocket.class);
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private CacheService cacheService;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) throws Exception {
        Map<String, List<String>> requestParameterMap = session.getRequestParameterMap();
        boolean device = isDevice(session);
        session.getUserProperties().put("device", device);
        String uuid = getDeviceId(session);
        session.getUserProperties().put("uuid", uuid);
        if (device) {
            logger.info("device receive text: open devId {}",uuid);
            String desc = requestParameterMap.get("description").get(0);
            RttyDevice rttyDevice = cacheService.putRttyDevice(uuid, new RttyDevice(uuid, session, desc, null));
            long keepalive = Long.parseLong(requestParameterMap.get("keepalive").get(0))*3;
            rttyDevice.keepAlive(keepalive);
            session.getContainer().setDefaultMaxSessionIdleTimeout(keepalive*1000);
        } else {
            logger.info("user receive text: open devId {}",uuid);
            RttyUser rttyUser = new RttyUser(uuid, session);
            RttyDevice rttyDevice = cacheService.getRttyDevice(uuid);
            if (rttyDevice == null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "login");
                jsonObject.put("err", 1);
                jsonObject.put("msg", "offline");
                session.getBasicRemote().sendText(jsonObject.toJSONString());
                logger.info("Device {} offline", uuid);
                return;
            }

            Integer devsid = rttyDevice.getFreeSid();
            rttyUser.setSid(devsid);
            RttyDeviceUserSession rttyDeviceUserSession = new RttyDeviceUserSession();
            rttyDeviceUserSession.setRttyDevice(rttyDevice);
            rttyDeviceUserSession.setRttyUser(rttyUser);
            rttyDevice.putUserSession(devsid, rttyDeviceUserSession);
//            rttyDevice.getDeviceUserSession().put(devsid, rttyDeviceUserSession);
            session.getUserProperties().put("sid", devsid);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "login");
            jsonObject.put("sid", devsid);
            rttyDevice.sendText(jsonObject.toJSONString());
        }
    }


    @OnMessage
    public void onMessage(Session session, String text) {
        boolean device = isDevice(session);
        String uuid = getDeviceId(session);
        com.alibaba.fastjson.JSONObject parse = JSON.parseObject(text);
        RttyDevice rttyDevice = cacheService.getRttyDevice(uuid);
        if(rttyDevice ==null){
            return;
        }
        if (device) {//收到设备消息，发向用户
            logger.info("device receive text:" + " message:" + text);
            String type = parse.getString("type");
            if ("cmd".equals(type)) {

            } else {
                int devSid = parse.getIntValue("sid");

                rttyDevice.getUserSession(devSid).sendText(text);
            }
        } else {//收到用户消息，发向设备
            logger.info("user receive text:" + " message:" + text);
            rttyDevice.sendText(text);
        }
    }

    @OnMessage
    public void onMessage(Session session, byte[] message) throws IOException {
        boolean device = isDevice(session);
        String uuid = getDeviceId(session);
        RttyDevice rttyDevice = cacheService.getRttyDevice(uuid);
        if(rttyDevice ==null){
            return;
        }
        if (device) {//收到设备消息，发向用户
            Integer devsid = new Integer(message[0]);
            byte[] dest = new byte[message.length - 1];
            System.arraycopy(message, 1, dest, 0, dest.length);

            ByteBuffer byteBuffer = ByteBuffer.wrap(dest);
            logger.info("device receive Binary:" + devsid + " message:" + new String(byteBuffer.array()));
            rttyDevice.getUserSession(devsid).sendBinary(byteBuffer);
        } else {//收到用户消息，发向设备
            Integer sid = (Integer) session.getUserProperties().get("sid");

            byte[] dest = new byte[message.length + 1];
            dest[0] = sid.byteValue();
            System.arraycopy(message, 0, dest, 1, message.length);
            ByteBuffer byteBuffer = ByteBuffer.wrap(dest);

            logger.info("user receive Binary:" + sid + " message:" + new String(message));

            rttyDevice.sendBinary(byteBuffer);

        }
    }

    @OnMessage
    public synchronized void onPong(Session session, PongMessage message) {
        try {
            boolean device = isDevice(session);
            String uuid = getDeviceId(session);
//            session.getBasicRemote().sendPong(message.getApplicationData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        log.error("onClose {} {}", session.getRequestParameterMap(), closeReason.toString());
        boolean device = isDevice(session);
        String uuid = getDeviceId(session);
        RttyDevice rttyDevice = cacheService.getRttyDevice(uuid);
        if(rttyDevice ==null){
            return;
        }
        if (device) {
            cacheService.clearRttyDevice(uuid);
            Set<Map.Entry<Integer, RttyDeviceUserSession>> entrySet = rttyDevice.getDeviceUserSession().entrySet();
            Iterator<Map.Entry<Integer, RttyDeviceUserSession>> iterator = entrySet.iterator();
            while (iterator.hasNext()){
                Map.Entry<Integer, RttyDeviceUserSession> userSessionEntry = iterator.next();
                userSessionEntry.getValue().logout();
            }
        } else {
            Integer sid = (Integer) session.getUserProperties().get("sid");
            RttyDeviceUserSession userSession = rttyDevice.getUserSession(sid);
            if (userSession != null) {
                userSession.close();
                rttyDevice.clearUserSession(sid);
            }

        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        boolean device = isDevice(session);
        String uuid = getDeviceId(session);
        log.error("onError {} {} {} {}",device,uuid, session.getRequestParameterMap(), error);

    }

    public boolean isDevice(Session session) {
        boolean device = session.getRequestParameterMap().get("device") == null ? false : !"".equals(session.getRequestParameterMap().get("device").get(0));
        return device;
    }

    public String getDeviceId(Session session) {
        return session.getRequestParameterMap().get("devid").get(0);
    }

}
