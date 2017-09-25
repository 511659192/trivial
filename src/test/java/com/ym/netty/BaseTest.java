package com.ym.netty;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;

import com.google.common.collect.Maps;
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

public class BaseTest {

	public static void main(String[] args) throws Exception {
		ExecutorService executorService = Executors.newCachedThreadPool();
		Future f1 = executorService.submit(new Runnable() {
			@Override
			public void run() {
				System.out.println("11111");
			}
		});
		Future f2 = executorService.submit(new Callable() {
			@Override
			public Object call() throws Exception {
				System.out.println("33333");
				return "22222";
			}
		});
		final Map<String, Object> map = Maps.newHashMap();
		Future f3 = executorService.submit(new Runnable() {
			@Override
			public void run() {
				map.put("12", "32");
				System.out.println("44444");
			}
		}, map);
//		System.out.println(f1.get());
		System.out.println(f2.get());
//		System.out.println(f3.get());
		executorService.shutdown();
	}


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
				System.out.println("----initChannel----");
				socketChannel.pipeline().addLast(new TimeServerHandler());
			}
		}

		class TimeServerHandler extends ChannelHandlerAdapter {
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				System.out.println("----channelRead----");
				ByteBuf buf = (ByteBuf) msg;
				byte[] req = new byte[buf.readableBytes()];
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

	class TimeClient {

		public void connect(int port, String host) throws Exception {
			EventLoopGroup group = new NioEventLoopGroup();
			try {
				Bootstrap bootstrap = new Bootstrap();
				bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
						.handler(new ChannelInitializer<SocketChannel>() {
							@Override
							protected void initChannel(SocketChannel socketChannel) throws Exception {
								System.out.println("----initChannel----");
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

			private final ByteBuf firstMessage;

			public TimeClientHandler() {
				System.out.println("----TimeClientHandler----");
				byte[] req = "Query time order".getBytes();
				firstMessage = Unpooled.buffer(req.length);
				firstMessage.writeBytes(req);
			}

			@Override
			public void channelActive(ChannelHandlerContext ctx) throws Exception {
				System.out.println("----channelActive----");
				ctx.writeAndFlush(firstMessage);
			}

			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				System.out.println("----channelRead----");
				ByteBuf buf = (ByteBuf) msg;
				byte[] req = new byte[buf.readableBytes()];
				buf.readBytes(req);
				String body = new String(req, "UTF-8");
				System.out.println("Now is : " + body);
			}

			@Override
			public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
				System.out.println("----exceptionCaught----");
				cause.printStackTrace();
				ctx.close();
			}
		}
	}
}
