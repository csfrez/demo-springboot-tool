package com.csfrez.tool.limit;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TokenBucketRateLimiter {
    private long capacity; // 令牌桶容量，即最大允许的请求数量
    private long rate; // 令牌产生速率，即每秒产生的令牌数量
    private long tokens; // 当前令牌数量
    private ScheduledExecutorService scheduler; // 调度器

    public TokenBucketRateLimiter(long capacity, long rate) {
        this.capacity = capacity;
        this.rate = rate;
        this.tokens = capacity;
        this.scheduler = new ScheduledThreadPoolExecutor(1);
        scheduleRefill(); // 启动令牌补充任务
    }

    private void scheduleRefill() {
        scheduler.scheduleAtFixedRate(() -> {
            synchronized (this) {
                tokens = Math.min(capacity, tokens + rate); // 补充令牌，但不超过容量
            }
        }, 1, 1, TimeUnit.SECONDS); // 每秒产生一次令牌
    }

    public synchronized boolean tryAcquire() {
        if (tokens > 0) { // 判断令牌数量是否大于0
            tokens--; // 消耗一个令牌
            return true; // 获取请求成功
        }
        return false; // 令牌不足，无法获取请求
    }
}