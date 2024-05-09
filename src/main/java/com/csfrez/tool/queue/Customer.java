package com.csfrez.tool.queue;

public class Customer extends Thread {
    private int num;
    private Storage storage;

    public Customer(Storage storage) {
        this.storage = storage;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public void run() {
        storage.consume(this.num);
    }
}