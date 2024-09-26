package com.csfrez.tool.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * @author csfrez
 * @date 2024/7/17 9:29
 * @email csfrez@163.com
 */
public class ThreadPoolMonitor {

    private final static Logger log = LoggerFactory.getLogger(ThreadPoolMonitor.class);

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2, 4, 0,
            TimeUnit.SECONDS, new LinkedBlockingQueue<>(100),
            new ThreadFactoryBuilder().setNameFormat("my_thread_pool_%d").build());

    public static void main(String[] args) {
        log.info("Pool Size: " + threadPool.getPoolSize());
        log.info("Active Thread Count: " + threadPool.getActiveCount());
        log.info("Task Queue Size: " + threadPool.getQueue().size());
        log.info("Completed Task Count: " + threadPool.getCompletedTaskCount());
        ThreadPoolMonitor threadPoolMonitor = new ThreadPoolMonitor();
        threadPoolMonitor.init();
    }

    /**
     * 活跃线程数
     */
    private AtomicLong activeThreadCount = new AtomicLong(0);

    /**
     * 队列任务数
     */
    private AtomicLong taskQueueSize = new AtomicLong(0);

    /**
     * 完成任务数
     */
    private AtomicLong completedTaskCount = new AtomicLong(0);

    /**
     * 线程池中当前线程的数量
     */
    private AtomicLong poolSize = new AtomicLong(0);

    @PostConstruct
    private void init() {

        /**
         * 通过micrometer API完成统计
         *
         * gauge最典型的使用场景就是统计：list、Map、线程池、连接池等集合类型的数据
         */
//        Metrics.gauge("my_thread_pool_active_thread_count", activeThreadCount);
//        Metrics.gauge("my_thread_pool_task_queue_size", taskQueueSize);
//        Metrics.gauge("my_thread_pool_completed_task_count", completedTaskCount);
//        Metrics.gauge("my_thread_pool_size", poolSize);

        // 模拟线程池的使用
        new Thread(this::runTask).start();
    }

    private void runTask() {
        // 每5秒监控一次线程池的使用情况
        monitorThreadPoolState();
        // 模拟任务执行
        IntStream.rangeClosed(0, 500).forEach(i -> {
            // 每500毫秒，执行一个任务
            try {
                TimeUnit.MILLISECONDS.sleep(500);
                // 每个处理一个任务耗时5秒
                threadPool.submit(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void monitorThreadPoolState() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            activeThreadCount.set(threadPool.getActiveCount());
            taskQueueSize.set(threadPool.getQueue().size());
            poolSize.set(threadPool.getPoolSize());
            completedTaskCount.set(threadPool.getCompletedTaskCount());

            log.info("Pool Size: " + threadPool.getPoolSize());
            log.info("Active Thread Count: " + threadPool.getActiveCount());
            log.info("Task Queue Size: " + threadPool.getQueue().size());
            log.info("Completed Task Count: " + threadPool.getCompletedTaskCount());
        }, 0, 5, TimeUnit.SECONDS);
    }
}