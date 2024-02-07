package com.csfrez.tool.limit;

import java.util.LinkedList;
import java.util.Queue;

public class SlidingWindowRateLimiter {
    private Queue<Long> timestamps; // 存储请求的时间戳队列
    private int windowSize; // 窗口大小，即时间窗口内允许的请求数量
    private long windowDuration; // 窗口持续时间，单位：毫秒

    public SlidingWindowRateLimiter(int windowSize, long windowDuration) {
        this.windowSize = windowSize;
        this.windowDuration = windowDuration;
        this.timestamps = new LinkedList<>();
    }

    public synchronized boolean tryAcquire() {
        long currentTime = System.currentTimeMillis(); // 获取当前时间戳

        // 删除超过窗口持续时间的时间戳
        while (!timestamps.isEmpty() && currentTime - timestamps.peek() > windowDuration) {
            timestamps.poll();
        }

        if (timestamps.size() < windowSize) { // 判断当前窗口内请求数是否小于窗口大小
            timestamps.offer(currentTime); // 将当前时间戳加入队列
            return true; // 获取请求成功
        }

        return false; // 超过窗口大小，无法获取请求
    }
}