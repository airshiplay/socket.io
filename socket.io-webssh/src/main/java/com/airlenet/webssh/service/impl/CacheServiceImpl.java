package com.airlenet.webssh.service.impl;

import com.airlenet.webssh.rtty.RttyDevice;
import com.airlenet.webssh.service.CacheService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheServiceImpl implements CacheService {
    @Override
    @CachePut(value = "rttyDevice", key = "#uuid")
    public RttyDevice putRttyDevice(String uuid, RttyDevice rttyDevice) {
        return rttyDevice;
    }

    @Cacheable(value = "rttyDevice", key = "#uuid")
    @Override
    public RttyDevice getRttyDevice(String uuid) {
        return null;
    }

    @CacheEvict(value = "rttyDevice", key = "#uuid")
    @Override
    public RttyDevice clearRttyDevice(String uuid) {
        return null;
    }

    @Override
    @CachePut(value = "rttySessionDevice", key = "#sessionId")
    public RttyDevice putRttyDeviceSessionId(String sessionId, RttyDevice rttyDevice) {
        return rttyDevice;
    }
    @Cacheable(value = "rttySessionDevice", key = "#sessionId")
    @Override
    public RttyDevice getRttyDeviceSessionId(String sessionId) {
        return null;
    }

    @CacheEvict(value = "rttySessionDevice", key = "#sessionId")
    @Override
    public RttyDevice clearRttyDeviceSessionId(String sessionId) {
        return null;
    }
}
