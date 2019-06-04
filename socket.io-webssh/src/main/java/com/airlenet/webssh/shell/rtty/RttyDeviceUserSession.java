package com.airlenet.webssh.shell.rtty;

import lombok.Data;

@Data
public class RttyDeviceUserSession {
    private Integer sid;
    private RttyDevice rttyDevice;
    private RttyUser rttyUser;
}
