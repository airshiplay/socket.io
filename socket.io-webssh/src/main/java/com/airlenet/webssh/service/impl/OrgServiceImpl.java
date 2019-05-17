package com.airlenet.webssh.service.impl;

import com.airlenet.webssh.config.WebsshProperties;
import com.airlenet.webssh.entity.OrgEntity;
import com.airlenet.webssh.mapper.OrgMapper;
import com.airlenet.webssh.service.OrgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OrgServiceImpl extends ServiceImpl<OrgMapper, OrgEntity> implements OrgService {
    private static Logger logger = LoggerFactory.getLogger(OrgService.class);
    @Resource
    private OrgMapper orgMapper;
    @Autowired
    WebsshProperties websshProperties;


}
