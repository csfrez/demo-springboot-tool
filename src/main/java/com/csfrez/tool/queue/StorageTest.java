package com.csfrez.tool.queue;

import java.util.Random;

public class StorageTest {

    public static void main(String[] args) throws InterruptedException {
        Storage storage = new Storage();

        Producer p1 = new Producer(storage);
        Producer p2 = new Producer(storage);
        Producer p3 = new Producer(storage);
        Producer p4 = new Producer(storage);
        Customer c1 = new Customer(storage);
        Customer c2 = new Customer(storage);
        Customer c3 = new Customer(storage);
        Customer c4 = new Customer(storage);
        p1.setNum(10);
        p2.setNum(20);
        p3.setNum(80);
        p4.setNum(50);

        c1.setNum(50);
        c2.setNum(20);
        c3.setNum(20);
        c4.setNum(20);
//        c1.start();
//        c2.start();
//        c3.start();
//        p1.start();
//        p2.start();
//        p3.start();
//        p4.start();
//        c4.start();

        Random random = new Random();
        for(int i=0; i<10; i++){
            int r1 = random.nextInt(100);
            int r2 = random.nextInt(100);
            Producer producer = new Producer(storage);
            Customer customer = new Customer(storage);
            producer.setNum(r1);
            customer.setNum(r2);
            producer.start();
            customer.start();
            //Thread.sleep(100);
        }
        Thread.sleep(1000);
    }
}