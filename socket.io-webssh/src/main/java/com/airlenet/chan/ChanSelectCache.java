package com.airlenet.chan;

import java.util.ArrayList;
import java.util.List;

public class ChanSelectCache<T extends ChanMessage> {
    private List<T> lists = new ArrayList<>();

    private int size;

    public ChanSelectCache(int size) {
        this.size = size;
    }


    public synchronized T take() {
        while (lists.size() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        T message = lists.remove(0);
        notifyAll();
        return message;
    }

    public synchronized void put(T message) {
        while (lists.size() >= size) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        lists.add(message);
        notifyAll();
    }
}
