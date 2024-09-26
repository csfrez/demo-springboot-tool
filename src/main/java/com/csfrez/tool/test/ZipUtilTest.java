package com.csfrez.tool.test;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ZipUtil;

/**
 * @author csfrez
 * @date 2024/7/17 14:57
 * @email csfrez@163.com
 */
public class ZipUtilTest {

    public static void main(String[] args) {
        ZipUtilTest.gzip();
        ZipUtilTest.zlib();
    }

    private static void gzip(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append(i);
        }
        System.out.println("压缩前：" + sb.toString().getBytes().length);
        byte[] compressedBytes = ZipUtil.gzip(sb.toString(), CharsetUtil.UTF_8);
        System.out.println("压缩后：" + compressedBytes.length);
        String str = ZipUtil.unGzip(compressedBytes, CharsetUtil.UTF_8);
        System.out.println("压缩还原：" + str.getBytes().length);
    }

    private static void zlib(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append(i);
        }
        System.out.println("压缩前：" + sb.toString().getBytes().length);
        byte[] compressedBytes = ZipUtil.zlib(sb.toString(), CharsetUtil.UTF_8, 1);
        System.out.println("压缩后：" + compressedBytes.length);
        String str = ZipUtil.unZlib(compressedBytes, CharsetUtil.UTF_8);
        System.out.println("压缩还原：" + str.getBytes().length);
    }
}
