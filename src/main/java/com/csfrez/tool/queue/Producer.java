package com.csfrez.tool.queue;

public class Producer extends Thread {
    private int num;
    private Storage storage;

    public Producer(Storage storage) {
        this.storage = storage;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public void run() {
        storage.produce(this.num);
    }
}