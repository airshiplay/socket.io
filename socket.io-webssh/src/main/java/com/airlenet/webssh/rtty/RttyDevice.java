package com.airlenet.webssh.rtty;

import com.airlenet.webssh.entity.DeviceEntity;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class RttyDevice {
    private String desc; /* description of the device */
    private DeviceEntity deviceEntity;
    private long timestamp; /* Connection time */
    private RttyConnect deviceConnect;
    private Map<Integer, RttyDeviceUserSession> deviceUserSession = new HashMap<>();

    public RttyDevice(RttyConnect connect, String desc, DeviceEntity deviceEntity) {
        this.deviceConnect = connect;
        this.desc = desc;
        this.deviceEntity = deviceEntity;
        this.timestamp = System.currentTimeMillis();
    }

    public RttyDevice keepAlive(long keepalive) {
        return this;
    }

    public void readAlway() {

    }
}
