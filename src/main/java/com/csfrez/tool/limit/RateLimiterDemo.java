package com.csfrez.tool.limit;

import com.google.common.util.concurrent.RateLimiter;

public class RateLimiterDemo {
    
    public static void main(String[] args) {
        // 创建一个每秒允许2个请求的RateLimiter
        RateLimiter rateLimiter = RateLimiter.create(2.0);

        while (true) {
            // 请求RateLimiter一个令牌
            rateLimiter.acquire();
            // 执行操作
            doSomeLimitedOperation();
        }
    }

    private static void doSomeLimitedOperation() {
        // 模拟一些操作
        System.out.println("Operation executed at: " + System.currentTimeMillis());
    }
}