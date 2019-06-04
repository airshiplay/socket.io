package com.airlenet.webssh.shell.rtty;

import lombok.Data;

import javax.websocket.Session;
import java.io.IOException;
import java.nio.ByteBuffer;

@Data
public class RttyConnect {
    private String devId;
    private Session session;

    public RttyConnect(String devId, Session session) {
        this.devId = devId;
        this.session = session;
    }

   public RttyConnect sendText(String message){
       try {
           this.session.getBasicRemote().sendText(message);
       } catch (IOException e) {
           e.printStackTrace();
       }
       return this;
   }
    public RttyConnect sendBinary(ByteBuffer byteBuffer){
        try {
//            this.session.getAsyncRemote().s
//            this.session.getAsyncRemote().sendBinary(byteBuffer);
            this.session.getBasicRemote().sendBinary(byteBuffer,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }
}
