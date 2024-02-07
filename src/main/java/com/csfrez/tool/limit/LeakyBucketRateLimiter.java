package com.csfrez.tool.limit;


public class LeakyBucketRateLimiter {
    private long capacity; // 漏桶容量，即最大允许的请求数量
    private long rate; // 漏水速率，即每秒允许通过的请求数量
    private long water; // 漏桶当前水量
    private long lastTime; // 上一次请求通过的时间戳

    public LeakyBucketRateLimiter(long capacity, long rate) {
        this.capacity = capacity;
        this.rate = rate;
        this.water = 0;
        this.lastTime = System.currentTimeMillis();
    }

    public synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();
        long elapsedTime = now - lastTime;

        // 计算漏桶中的水量
        water = Math.max(0, water - elapsedTime * rate / 1000);

        if (water < capacity) { // 判断漏桶中的水量是否小于容量
            water++; // 漏桶中的水量加1
            lastTime = now; // 更新上一次请求通过的时间戳
            return true; // 获取请求成功
        }

        return false; // 漏桶已满，无法获取请求
    }
}