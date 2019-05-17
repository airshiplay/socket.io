package com.airlenet.webssh.service.impl;

import com.airlenet.webssh.config.WebsshProperties;
import com.airlenet.webssh.entity.DeviceEntity;
import com.airlenet.webssh.mapper.DeviceMapper;
import com.airlenet.webssh.service.DeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.codec.binary.Base64;
//import org.apache.tomcat.util.buf.HexUtils;
//import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.encoders.HexEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, DeviceEntity> implements DeviceService {
    private static Logger logger = LoggerFactory.getLogger(DeviceService.class);
    @Resource
    private DeviceMapper deviceMapper;
    @Autowired
    WebsshProperties websshProperties;

    @Override
    public DeviceEntity getPlaintextDevice(Long id) {
        DeviceEntity deviceEntity = baseMapper.selectById(id);
        if (deviceEntity != null) {
            websshProperties.getPublicKey();
            byte[] rsaPrivateKey = Base64.decodeBase64(websshProperties.getPrivateKey());
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(rsaPrivateKey);
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
                Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");//RSA/ECB/PKCS1Padding
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] result = cipher.doFinal(Base64.decodeBase64(deviceEntity.getPassword()));
                deviceEntity.setPassword(new String(result));
            } catch (Exception e) {
                logger.error("", e);
            }

        }
        return deviceEntity;
    }

    @Override
    public boolean save(DeviceEntity entity) {
        if(entity.getType() ==null){
            entity.setType("direct");
        }
        return super.save(entity);
    }
}
