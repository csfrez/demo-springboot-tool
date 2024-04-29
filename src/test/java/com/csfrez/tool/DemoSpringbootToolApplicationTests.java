package com.csfrez.tool;

import cn.hutool.core.util.IdUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DemoSpringbootToolApplicationTests {

    @Test
    void contextLoads() {
    }

    public static void main(String[] args) {
        String s = IdUtil.randomUUID();
        System.out.println(s);
    }
}
