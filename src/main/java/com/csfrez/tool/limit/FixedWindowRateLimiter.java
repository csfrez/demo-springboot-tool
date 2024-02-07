package com.csfrez.tool.limit;

public class FixedWindowRateLimiter {
    private static int counter = 0;  // 统计请求数
    private static long lastAcquireTime = 0L;
    private static final long windowUnit = 1000L; // 假设固定时间窗口是1000ms
    private static final int threshold = 10; // 窗口阀值是10

    public synchronized boolean tryAcquire() {
        long currentTime = System.currentTimeMillis();  // 获取系统当前时间
        if (currentTime - lastAcquireTime > windowUnit) {  // 检查是否在时间窗口内
            counter = 0;  // 计数器清零
            lastAcquireTime = currentTime;  // 开启新的时间窗口
        }
        if (counter < threshold) {  // 小于阀值
            counter++;  // 计数器加1
            return true;  // 获取请求成功
        }
        return false;  // 超过阀值，无法获取请求
    }
}