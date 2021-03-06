package com.ym.netty;

import org.junit.Test;

import io.netty.bootstrap.Bootstrap;
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
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class SelfDefinedDecoder {
	
	@Test
	public void testTimeServer() throws Exception {
		new EchoServer().bind(8080);
	}
	
	@Test
	public void testTimeClient() throws Exception {
		new EchoClient().connect("127.0.0.1", 8080);
	}
	class EchoServer {

		public void bind(int port) throws Exception {
			EventLoopGroup bossGroup = new NioEventLoopGroup();
			EventLoopGroup worderGroup = new NioEventLoopGroup();
			try {
				ServerBootstrap serverBootstrap = new ServerBootstrap();
				serverBootstrap.group(bossGroup, worderGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 100)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel socketChannel) throws Exception {
							System.out.println("----initChannel----");
							ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
							socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
							socketChannel.pipeline().addLast(new StringDecoder());
							socketChannel.pipeline().addLast(new EchoServerHandler());
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
			int counter = 0;
			
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				System.out.println("----channelRead----");
				String body = (String) msg;
				System.out.println("This is " + ++counter + " times receive client : [" + body + "]");
				body = body + "$_";
				ByteBuf echo = Unpooled.copiedBuffer(body.getBytes());
				ctx.writeAndFlush(echo);
			}
			
			@Override
			public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
				ctx.close();
				cause.printStackTrace();
			}
		}
	}
	
	class EchoClient {
		
		public void connect(String host, int port) throws Exception {
			EventLoopGroup group = new NioEventLoopGroup();
			try {
				Bootstrap bootstrap = new Bootstrap();
				bootstrap.group(group)
					.channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel socketChannel) throws Exception {
							System.out.println("----initChannel----");
							ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
							socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
							socketChannel.pipeline().addLast(new StringDecoder());
							socketChannel.pipeline().addLast(new EchoClientHandler());
						}
					});
				
				ChannelFuture future = bootstrap.connect(host, port).sync();
				future.channel().closeFuture().sync();
			} catch (Exception e) {
				group.shutdownGracefully();
			}
		}
		
		class EchoClientHandler extends ChannelHandlerAdapter {
			int counter = 0;
			String ECHO_REQ = "Hi, lilinfeng, welcome to netty.$_";
			
			@Override
			public void channelActive(ChannelHandlerContext ctx) throws Exception {
				System.out.println("----channelActive----");
				for (int i = 0; i < 10; i++) {
					ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));
				}
			}
			
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				System.out.println("----channelRead----");
				String body = (String) msg;
				System.out.println("This is " + ++counter + " times receive server : [" + body + "]");
			}
			
//			@Override
//			public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//				System.out.println("----channelReadComplete----");
//				ctx.flush();
//			}
		}
	}
}
