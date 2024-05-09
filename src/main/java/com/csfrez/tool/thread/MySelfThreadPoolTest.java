package com.csfrez.tool.thread;

import java.util.Random;

public class MySelfThreadPoolTest {

    private static final int TASK_NUM = 50;//任务的个数

    public static void main(String[] args) {
        MySelfThreadPool myPool = new MySelfThreadPool(3, 50);
        for (int i = 0; i < TASK_NUM; i++) {
            myPool.execute(new MyTask("task_" + i));
        }
    }

    static class MyTask implements Runnable {
        private String name;

        public MyTask(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            Random random = new Random();
            int r = random.nextInt(1000);
            try {
                Thread.sleep(r);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("task:" + name + " sleep:" + r + " end...");
        }

        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return "name = " + name;
        }
    }
}
