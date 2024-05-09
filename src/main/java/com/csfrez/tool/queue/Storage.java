package com.csfrez.tool.queue;

import java.util.ArrayList;
import java.util.List;

public class Storage {
    private static int MAX_VALUE = 200;
    private List<Object> list = new ArrayList<>();

    public void produce(int num) {
        synchronized (list) {
            while (list.size() + num > MAX_VALUE) {
                System.out.println("暂时不能执行生产任务");
                try {
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < num; i++) {
                list.add(new Object());
            }
            System.out.println("已生产产品数" + num + " 仓库容量" + list.size());
            list.notifyAll();
        }
    }

    public void consume(int num) {
        synchronized (list) {
            while (list.size() < num) {
                System.out.println("暂时不能执行消费任务");
                try {
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < num; i++) {
                list.remove(0);
            }
            System.out.println("已消费产品数" + num + " 仓库容量" + list.size());
            list.notifyAll();
        }
    }
}