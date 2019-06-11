package com.airlenet.webssh.shell.rtty;

import lombok.Data;

import javax.websocket.Session;

@Data
public class RttyUser {
    private int sid;
    //    private RttyConnect connect;
    private String devId;
    private Session session;

    public RttyUser(String devId, Session session) {
        this.devId = devId;
        this.session = session;
    }
}
