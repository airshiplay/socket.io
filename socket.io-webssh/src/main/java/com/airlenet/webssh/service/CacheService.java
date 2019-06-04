package com.airlenet.webssh.service;

import com.airlenet.webssh.shell.rtty.RttyDevice;

public interface CacheService {

    public RttyDevice putRttyDevice(String uuid, RttyDevice rttyDevice);


    public RttyDevice getRttyDevice(String uuid);

    public RttyDevice clearRttyDevice(String uuid);


    public RttyDevice putRttyDeviceSessionId(String sessionId, RttyDevice rttyDevice);
    public RttyDevice getRttyDeviceSessionId(String sessionId);
    public RttyDevice clearRttyDeviceSessionId(String sessionId);
}
