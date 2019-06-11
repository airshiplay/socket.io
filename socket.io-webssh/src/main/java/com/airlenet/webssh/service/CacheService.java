package com.airlenet.webssh.service;

import com.airlenet.webssh.shell.rtty.RttyDevice;

import java.util.List;

public interface CacheService {

    public List<RttyDevice> getRttyDeviceList();

    public RttyDevice getRttyDevice(String uuid);

    public RttyDevice putRttyDevice(String uuid, RttyDevice rttyDevice);

    public RttyDevice clearRttyDevice(String uuid);
}
