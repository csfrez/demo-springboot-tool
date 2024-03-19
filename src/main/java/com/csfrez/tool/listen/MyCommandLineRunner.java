package com.csfrez.tool.listen;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyCommandLineRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // 在这里执行初始化操作
        log.info("MyCommandLineRunner应用程序启动后执行初始化操作...");
        Thread.sleep(1000L);
        log.info("MyCommandLineRunner应用程序启动后完成初始化操作...");
    }
}