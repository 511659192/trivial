package com.ym.netty.chapter4.decoder;

import java.util.Date;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class TimeServer {
	
	public static void main(String[] args) throws Exception {
		int port = 8080;
		new TimeServer().bind(port);
	}

	
	public void bind(int port) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 1024)
				.childHandler(new ChildChannelHander());
			ChannelFuture future = serverBootstrap.bind(port).sync();
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	class ChildChannelHander extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel socketChannel) throws Exception {
			System.out.println("----initChannel----");
			socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
			socketChannel.pipeline().addLast(new StringDecoder());
			socketChannel.pipeline().addLast(new TimeServerHandler());
		}
	}
	
	class TimeServerHandler extends ChannelHandlerAdapter {
		
		private int counter = 0;
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			System.out.println("----channelRead----");
			String body = (String) msg;
			System.out.println("The time server recerive order:" + body + " ; the counter is : " + ++counter);
			String currentTime = "Query time order".equalsIgnoreCase(body) ? new Date().toString() : "Bad order";
			currentTime = currentTime + System.getProperty("line.separator");
			ByteBuf res = Unpooled.copiedBuffer(currentTime.getBytes());
			ctx.writeAndFlush(res);
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			System.out.println("----exceptionCaught----");
			cause.printStackTrace();
			ctx.close();
		}
	}
}
