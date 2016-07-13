package com.ym.netty;

import java.util.Date;

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

public class PackExceptionTest {
	
	@Test
	public void testTimeServer() throws Exception {
		new TimeServer().bind(8080);
	}
	
	@Test
	public void testTimeClient() throws Exception {
		new TimeClient().connect(8080, "127.0.0.1");
	}
	
	class TimeServer {
		public void bind(int port) throws Exception {
			EventLoopGroup bossGroup = new NioEventLoopGroup();
			EventLoopGroup workerGroup = new NioEventLoopGroup();
			try {
				ServerBootstrap serverBootstrap = new ServerBootstrap();
				serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
						.option(ChannelOption.SO_BACKLOG, 1024).childHandler(new ChildChannelHander());
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
				socketChannel.pipeline().addLast(new TimeServerHandler());
			}
		}

		class TimeServerHandler extends ChannelHandlerAdapter {

			private int counter = 0;

			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				ByteBuf buf = (ByteBuf) msg;
				byte[] req = new byte[buf.readableBytes()];
				buf.readBytes(req);
				String body = new String(req, "UTF-8").substring(0,
						req.length - System.getProperty("line.separator").length());
				System.out.println("The time server recerive order:" + body + " ; The counter is : " + ++counter);
				String currentTime = "Query time order".equalsIgnoreCase(body) ? new Date().toString() : "Bad order";
				currentTime = currentTime + System.getProperty("line.separator");
				ByteBuf res = Unpooled.copiedBuffer(currentTime.getBytes());
				ctx.write(res);
			}

			@Override
			public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
				ctx.flush();
			}

			@Override
			public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
				ctx.close();
			}
		}
	}

	class TimeClient {

		public void connect(int port, String host) throws Exception {
			EventLoopGroup group = new NioEventLoopGroup();
			try {
				Bootstrap bootstrap = new Bootstrap();
				bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
						.handler(new ChannelInitializer<SocketChannel>() {
							@Override
							protected void initChannel(SocketChannel socketChannel) throws Exception {
								socketChannel.pipeline().addLast(new TimeClientHandler());
							}
						});
				ChannelFuture future = bootstrap.connect(host, port).sync();
				future.channel().closeFuture().sync();
			} catch (Exception e) {
				group.shutdownGracefully();
			}
		}

		class TimeClientHandler extends ChannelHandlerAdapter {

			private int counter;

			private byte[] req;

			public TimeClientHandler() {
				req = ("Query time order" + System.getProperty("line.separator")).getBytes();
			}

			@Override
			public void channelActive(ChannelHandlerContext ctx) throws Exception {
				ByteBuf message = null;
				for (int i = 0; i < 100; i++) {
					message = Unpooled.buffer(req.length);
					message.writeBytes(req);
					ctx.writeAndFlush(message);
				}
			}

			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				ByteBuf buf = (ByteBuf) msg;
				byte[] req = new byte[buf.readableBytes()];
				buf.readBytes(req);
				String body = new String(req, "UTF-8");
				System.out.println("Now is : " + body + " ; The counter is : " + ++counter);
			}

			@Override
			public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
				cause.printStackTrace();
				ctx.close();
			}
		}
	}

}
