package com.airlenet.webssh.service.impl;

import com.airlenet.webssh.service.CacheService;
import com.airlenet.webssh.shell.rtty.RttyDevice;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CacheServiceImpl implements CacheService {
    Map<String, RttyDevice> rttyDeviceMap = new HashMap<>();

    @Override
    public List<RttyDevice> getRttyDeviceList() {
        return new ArrayList<>(rttyDeviceMap.values());
    }

    @Cacheable(value = "rttyDevice", key = "#uuid")
    @Override
    public RttyDevice getRttyDevice(String uuid) {
        return null;
    }

    @Override
    @CachePut(value = "rttyDevice", key = "#uuid")
    public RttyDevice putRttyDevice(String uuid, RttyDevice rttyDevice) {
        rttyDeviceMap.put(uuid, rttyDevice);
        return rttyDevice;
    }

    @CacheEvict(value = "rttyDevice", key = "#uuid")
    @Override
    public RttyDevice clearRttyDevice(String uuid) {
        rttyDeviceMap.remove(uuid);
        return null;
    }
}
