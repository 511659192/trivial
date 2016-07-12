package com.ym.netty.chapter6;

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
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class EchoServer {
	
	public static void main(String[] args) throws Exception {
		new EchoServer(8080).run();
	}

	int port;
	
	public EchoServer(int port) {
		super();
		this.port = port;
	}

	public void run() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup worderGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, worderGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 100)
				.handler(new LoggingHandler(LogLevel.DEBUG))
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel socketChannel) throws Exception {
						System.out.println("----initChannel----");
						socketChannel.pipeline().addLast("msgpack encoder", new MsgpackEncoder());
						socketChannel.pipeline().addLast("msgpack decoder", new MsgpackDecoder());
						socketChannel.pipeline().addLast(new ChannelHandlerAdapter(){
							
							@Override
							public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
								System.out.println("----channelRead----");
								ctx.writeAndFlush(msg);
							}
							
							@Override
							public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
								ctx.close();
								cause.printStackTrace();
							}
						});
					}
				});
			ChannelFuture future = serverBootstrap.bind(port).sync();
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
			bossGroup.shutdownGracefully();
			worderGroup.shutdownGracefully();
		}
	}
	
	class EchoServerHandler extends ChannelHandlerAdapter {
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			System.out.println("----channelRead----");
			System.out.println("server receive the msgpack message : " + msg);
			ctx.write(msg);
		}
		
		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			System.out.println("----channelReadComplete----");
			ctx.flush();
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			System.out.println("----exceptionCaught----");
			cause.printStackTrace();
			ctx.close();
		}
	}
}
