package com.csfrez.tool.limit;

import java.util.LinkedList;
import java.util.List;

public class SlidingLogRateLimiter {
    private int requests; // 请求总数
    private List<Long> timestamps; // 存储请求的时间戳列表
    private long windowDuration; // 窗口持续时间，单位：毫秒
    private int threshold; // 窗口内的请求数阀值

    public SlidingLogRateLimiter(int threshold, long windowDuration) {
        this.requests = 0;
        this.timestamps = new LinkedList<>();
        this.windowDuration = windowDuration;
        this.threshold = threshold;
    }

    public synchronized boolean tryAcquire() {
        long currentTime = System.currentTimeMillis(); // 获取当前时间戳

        // 删除超过窗口持续时间的时间戳
        while (!timestamps.isEmpty() && currentTime - timestamps.get(0) > windowDuration) {
            timestamps.remove(0);
            requests--;
        }

        if (requests < threshold) { // 判断当前窗口内请求数是否小于阀值
            timestamps.add(currentTime); // 将当前时间戳添加到列表
            requests++; // 请求总数增加
            return true; // 获取请求成功
        }

        return false; // 超过阀值，无法获取请求
    }
}