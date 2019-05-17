package com.airlenet.webssh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("ssh_device")
@Data
public class DeviceEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String identifiy;
    private String name;
    private String type;
    private String ip;
    private Integer port;
    private String username;
    private String password;
    private String desc;
    @TableField("userId")
    private Long userId;
    @TableField("orgId")
    private Long orgId;
}
