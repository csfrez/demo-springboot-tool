package com.csfrez.tool.md5;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileTest {

    public static void main(String[] args) {
        try (InputStream is = Files.newInputStream(Paths.get("E:\\tmp\\oss\\664b431ee405c60001ed5bbb.m4a"))) {
            String md5 = DigestUtils.md5Hex(is);
            System.out.println(md5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (InputStream is = Files.newInputStream(Paths.get("E:\\tmp\\oss\\664b431ee405c60001ed5bbd.m4a"))) {
            String md5 = DigestUtils.md5Hex(is);
            System.out.println(md5);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
