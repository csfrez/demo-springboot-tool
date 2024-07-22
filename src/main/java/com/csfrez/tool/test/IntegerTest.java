package com.csfrez.tool.test;

/**
 * @author yangzhi
 * @date 2024/7/17 14:07
 * @email yangzhi@ddjf.com.cn
 */
public class IntegerTest {
    public static void main(String[] args) {
        long s = System.currentTimeMillis();
        testInt();
        long e = System.currentTimeMillis();
        System.out.println(e - s);
        testInteger();
        long e2 = System.currentTimeMillis();
        System.out.println(e2 - e);
    }

    private static void testInt() {
        int sum = 1;
        for (int i = 1; i < 50000000; i++) {
            sum++;
        }
        System.out.println(sum);
    }

    private static void testInteger() {
        Integer sum = 1;
        for (int i = 1; i < 50000000; i++) {
            sum++;
        }
        System.out.println(sum);
    }
}