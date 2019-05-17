package com.airlenet.webssh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("ssh_org")
@Data
public class OrgEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    private String desc;
    private Long pId;
    @TableField("userId")
    private Long userId;
}
