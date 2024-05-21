package com.csfrez.tool.pool;

import cn.hutool.core.util.IdUtil;
import org.slf4j.MDC;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomThreadPoolExecutorTest {


    public static void main(String[] args) {
        // 创建自定义线程池执行器
        //CustomThreadPoolExecutor threadPoolExecutor = new CustomThreadPoolExecutor(2, 4, 60, TimeUnit.SECONDS, new ArrayBlockingQueue(100));
        CustomThreadPoolExecutor threadPoolExecutor = new CustomThreadPoolExecutor(5, 20,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(20),
                new ThreadPoolExecutor.CallerRunsPolicy());
        // 提交一些任务
        for (int i = 0; i < 500; i++) {
            String traceId = IdUtil.simpleUUID();
            MDC.put("traceId", traceId);
            threadPoolExecutor.submit(() -> {
                System.out.println("Executing traceId: " + MDC.get("traceId") + ", taskName: " + Thread.currentThread().getName());
                try {
                    Random random = new Random();
                    int sleepTime = random.nextInt(2000);
                    Thread.sleep(sleepTime); // 模拟任务执行时间
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        // 关闭线程池
        threadPoolExecutor.shutdown();
        try {
            threadPoolExecutor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
