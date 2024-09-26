package com.csfrez.tool.thread;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author csfrez
 * @date 2024/7/17 14:50
 * @email csfrez@163.com
 */
public class CopyOnWriteArraySetTest {

    public static void main(String[] args) throws InterruptedException {
        CopyOnWriteArraySet<Integer> set = new CopyOnWriteArraySet();
        new Thread(() -> {
            try {
                set.add(1);
                System.out.println("第一个线程启动，添加了一个元素，睡100毫秒");
                Thread.sleep(100);
                set.add(2);
                set.add(3);
                System.out.println("第一个线程添加了3个元素，执行结束");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        //保证让第一个线程先执行
        Thread.sleep(1);

        new Thread(() -> {
            try {
                System.out.println("第二个线程启动了！睡200毫秒");
                //Thread.sleep(200);//如果在这边睡眠，可以获取到3个元素
                Iterator<Integer> iterator = set.iterator();//生成快照
                Thread.sleep(200);//如果在这边睡眠，只能获取到1个元素
                while (iterator.hasNext()) {
                    System.out.println("第二个线程开始遍历，获取到元素：" + iterator.next());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }
}
