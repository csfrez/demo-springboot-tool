package com.csfrez.tool.listen;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GracefulShutdown implements ApplicationListener<ContextClosedEvent> {

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        // 在应用程序关闭时执行的逻辑
        // 可以在这里处理资源释放、完成正在进行的请求等操作
       log.info("GracefulShutdown应用程序正在关闭，执行优雅停机操作...");
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("GracefulShutdown应用程序已经关闭，完成优雅停机操作...");
    }
}