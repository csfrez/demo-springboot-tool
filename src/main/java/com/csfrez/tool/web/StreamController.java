package com.csfrez.tool.web;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/stream")
@Slf4j
public class StreamController {

    @GetMapping("/response")
    public StreamingResponseBody streamResponse() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David", "Frez");

        return outputStream -> {
            for (String name : names) {
                outputStream.write(name.getBytes());
                outputStream.write("\n".getBytes());
                outputStream.flush();
                try {
                    log.info("name={}", name);
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}