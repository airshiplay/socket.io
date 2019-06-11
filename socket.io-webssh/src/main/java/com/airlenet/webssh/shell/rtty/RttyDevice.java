package com.airlenet.webssh.shell.rtty;

import com.airlenet.webssh.entity.DeviceEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.websocket.Session;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

@Data
public class RttyDevice {
    private String devId;
    @JsonIgnore
    private Session session;
    private String desc; /* description of the device */
    private DeviceEntity deviceEntity;
    private long timestamp; /* Connection time */
    @JsonIgnore
    private Map<Integer, RttyDeviceUserSession> deviceUserSession = new HashMap<>();

    public RttyDeviceUserSession getUserSession(Integer sid) {
        return deviceUserSession.get(sid);
    }

    public void putUserSession(Integer sid, RttyDeviceUserSession userSession) {
        deviceUserSession.put(sid, userSession);
    }

    public void clearUserSession(Integer sid) {
        deviceUserSession.remove(sid);
    }

    public RttyDevice(String devId, Session session, String desc, DeviceEntity deviceEntity) {
        this.devId = devId;
        this.session = session;
        this.desc = desc;
        this.deviceEntity = deviceEntity;
        this.timestamp = System.currentTimeMillis();
    }

    public RttyDevice keepAlive(long keepalive) {
        return this;
    }

    public void readAlway() {

    }

    @JsonIgnore
    public int getFreeSid() {
        for (int i = 1; i <= 5; i++) {
            RttyDeviceUserSession userSession = deviceUserSession.get(i);
            if (userSession == null) {
                return i;
            }
        }
        return 0;
    }

    public void sendText(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendBinary(ByteBuffer byteBuffer) {
        try {
            this.session.getBasicRemote().sendBinary(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
