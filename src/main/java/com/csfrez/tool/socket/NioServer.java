package com.csfrez.tool.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioServer {

    public static void main(String[] args) throws IOException {
        //获取ServerSocketChannel
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        //设置非阻塞模式
        ssChannel.configureBlocking(false);
        //绑定端口
        ssChannel.bind(new InetSocketAddress(9999));
        //获取选择器
        Selector selector = Selector.open();
        //将ServerSocketChannel注册到选择器上，并且监听建立连接事件
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);
        // 使用Selector选择器轮询已经就绪好的事件
        while (selector.select() > 0) {
            // 获取选择器就绪事件
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            //遍历事件
            while (it.hasNext()) {
                SelectionKey sk = it.next();
                //判断事件类型
                if (sk.isAcceptable()) {
                    // 获取客户端channel
                    SocketChannel channel = ssChannel.accept();
                    //切换非阻塞模式
                    channel.configureBlocking(false);
                    //将该channel注册到选择器上
                    channel.register(selector, SelectionKey.OP_READ);
                } else if (sk.isReadable()) {
                    //获取channel
                    SocketChannel sChannel = (SocketChannel) sk.channel();
                    //读取网络数据
                    ByteBuffer buf = ByteBuffer.allocate(1024);
                    int len = 0;
                    while ((len = sChannel.read(buf)) > 0) {
                        String msg = new String(buf.array(), 0, len);
                        System.out.println(msg);
                        //处理数据的粘包拆包
                        //对完整的数据包进行解码操作
                        //得到客户端消息
                        //触发各种统计类事件如心跳检测 信息统计
                    }
                } else if (sk.isWritable()) {
                    //得到响应消息
                    //对响应消息进行编码
                }
                // 15.取消选择键SelectionKey
                it.remove();
            }
        }
    }
}
