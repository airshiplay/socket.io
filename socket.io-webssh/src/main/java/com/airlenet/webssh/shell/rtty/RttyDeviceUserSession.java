package com.airlenet.webssh.shell.rtty;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.IOException;
import java.nio.ByteBuffer;

@Data
public class RttyDeviceUserSession {
    private Integer sid;
    private RttyDevice rttyDevice;
    private RttyUser rttyUser;

    public void sendText(String message) {
        try {
            this.rttyUser.getSession().getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "logout");
        jsonObject.put("sid", sid);
        sendText(jsonObject.toJSONString());
    }

    public void sendBinary(ByteBuffer byteBuffer) {
        try {
            this.rttyUser.getSession().getBasicRemote().sendBinary(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            this.rttyUser.getSession().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
