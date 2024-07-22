package com.csfrez.tool.cache;

/**
 * @author yangzhi
 * @date 2024/7/17 10:52
 * @email yangzhi@ddjf.com.cn
 */
public class CacheWrite {

    public static void main(String[] args) {
        CacheWrite cacheWrite = new CacheWrite();
        cacheWrite.writeByLine();
        cacheWrite.writeByColumn();
    }


    public void writeByLine() {
        int[][] arr = new int[10000][10000];
        long s = System.currentTimeMillis();
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                arr[i][j] = 0;
            }
        }
        long e = System.currentTimeMillis();
        System.out.println(e-s);
    }

    public void writeByColumn() {
        int[][] arr = new int[10000][10000];
        long s = System.currentTimeMillis();
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                arr[j][i] = 0;
            }
        }
        long e = System.currentTimeMillis();
        System.out.println(e-s);
    }

}
