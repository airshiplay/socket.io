package com.airlenet.webssh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotNull;

@TableName("ssh_device")
@Data
public class DeviceEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String identifiy;
    @NotNull
    private String name;
    private String type;
    @NotNull
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
