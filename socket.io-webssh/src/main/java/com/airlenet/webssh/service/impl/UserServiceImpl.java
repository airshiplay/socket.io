package com.airlenet.webssh.service.impl;

import com.airlenet.webssh.config.WebsshProperties;
import com.airlenet.webssh.entity.UserEntity;
import com.airlenet.webssh.mapper.UserMapper;
import com.airlenet.webssh.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {
    private static Logger logger = LoggerFactory.getLogger(UserService.class);
    @Resource
    private UserMapper userMapper;
    @Autowired
    WebsshProperties websshProperties;


    @Override
    public UserEntity getUserByName(String username) {
        QueryWrapper queryWrapper = new QueryWrapper(UserEntity.class);
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }
}
