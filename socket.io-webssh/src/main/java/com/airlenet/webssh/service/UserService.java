package com.airlenet.webssh.service;

import com.airlenet.webssh.entity.UserEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<UserEntity> {
    UserEntity getUserByName(String username);
}
