package com.airlenet.webssh.rtty;

import lombok.Data;

@Data
public class RttyUser {
    private int sid;
    private RttyConnect connect;

    public RttyUser(RttyConnect connect) {
        this.connect = connect;
    }
}
