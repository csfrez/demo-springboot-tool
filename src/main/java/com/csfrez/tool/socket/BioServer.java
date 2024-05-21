package com.csfrez.tool.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BioServer {

    //定义一个循环接收客户端的Socket连接请求。初始化一个线程池对象
    private static ExecutorService poolHandler = new ThreadPoolExecutor(5, 5,
            120, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

    public static void main(String[] args) {
        try {
            // 注册端口
            ServerSocket ss = new ServerSocket(9999);
            while (true) {
                Socket socket = ss.accept();
                // 把Socket封装成一个任务对象交给线程池处理
                Runnable target = new ServerRunnable(socket);
                poolHandler.execute(target);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ServerRunnable implements Runnable {
        private Socket socket;

        public ServerRunnable(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            // 处理接收的客户端Socket通信需求
            try {
                InputStream is = socket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String msg;
                while ((msg = br.readLine()) != null) {
                    System.out.println(msg);

                    //处理数据的粘包拆包
                    //对完整的数据包进行解码操作
                    //得到客户端消息
                    //触发各种统计类事件如心跳检测 信息统计
                    //处理客户端的消息
                    //得到响应消息
                    //对响应消息进行编码
                }
            } catch (IOException e) {
                e.printStackTrace();
                //处理网络断开事件
                //处理其他异常事件
            }
        }
    }
}
