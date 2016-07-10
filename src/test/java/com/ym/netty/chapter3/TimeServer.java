package com.ym.netty.chapter3;

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
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	class ChildChannelHander extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel socketChannel) throws Exception {
			System.out.println("----initChannel----");
			socketChannel.pipeline().addLast(new TimeServerHandler());
		}
	}
	
	class TimeServerHandler extends ChannelHandlerAdapter {
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			System.out.println("----channelRead----");
			ByteBuf buf = (ByteBuf) msg;
			byte[] req =  new byte[buf.readableBytes()];
			buf.readBytes(req);
			String body = new String(req, "UTF-8");
			System.out.println("The time server recerive order:" + body);
			String currentTime = "Query time order".equalsIgnoreCase(body) ? new Date().toString() : "Bad order";
			ByteBuf res = Unpooled.copiedBuffer(currentTime.getBytes());
			ctx.write(res);
		}
		
		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			System.out.println("----channelReadComplete----");
			ctx.flush();
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			System.out.println("----exceptionCaught----");
			ctx.close();
		}
	}
}
