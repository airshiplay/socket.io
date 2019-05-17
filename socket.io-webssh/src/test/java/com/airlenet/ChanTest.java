package com.airlenet;

import com.airlenet.chan.Chan;
import com.airlenet.chan.ChanMessage;

public class ChanTest
{
    public void test(){
        Chan<ChanMessage<Integer>> chanMessageChan = new Chan<>();


        new Thread(() -> {
            chanMessageChan.put(new ChanMessage<>("timeout", new Integer(1)));
        }).start();


        new Thread(() -> {
            ChanMessage<Integer> chanMessage = chanMessageChan.take();
            switch (chanMessage.getType()) {
                case "timeout":
                    System.out.println(chanMessage.getData());
                    break;
                default:
                    break;

            }
        }).start();
    }
}
