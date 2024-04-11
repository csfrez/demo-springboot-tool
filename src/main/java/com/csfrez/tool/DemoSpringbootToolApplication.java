package com.csfrez.tool;

import com.feiniaojin.gracefulresponse.EnableGracefulResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableGracefulResponse
public class DemoSpringbootToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSpringbootToolApplication.class, args);
    }

}
