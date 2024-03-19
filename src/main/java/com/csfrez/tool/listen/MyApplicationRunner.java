package com.csfrez.tool.listen;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 在这里执行初始化操作
        log.info("MyApplicationRunner应用程序启动后执行初始化操作...");
        Thread.sleep(2000L);
        // 在这里执行初始化操作
        log.info("MyApplicationRunner应用程序启动后完成初始化操作...");
    }
}