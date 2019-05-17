package com.airlenet.webssh.service;

import com.airlenet.webssh.entity.DeviceEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DeviceService extends IService<DeviceEntity> {

    public DeviceEntity getPlaintextDevice(Long id);
}
