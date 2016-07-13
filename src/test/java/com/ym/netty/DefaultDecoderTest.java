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
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class DefaultDecoderTest {
	
	@Test
	public void testServer() throws Exception {
		new TimeServer().bind(8080);
	}
	
	@Test
	public void testClient() throws Exception {
		new TimeClient().connect(8080, "127.0.0.1");
	}
	
	class TimeServer {
		
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
	
	class TimeClient {

		public void connect(int port, String host) throws Exception {
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
							socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
							socketChannel.pipeline().addLast(new StringDecoder());
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

			private int counter = 0;
			
			private byte[] req = null;
			
			public TimeClientHandler() {
				System.out.println("----TimeClientHandler----");
				req = ("Query time order" + System.getProperty("line.separator")).getBytes();
			}

			@Override
			public void channelActive(ChannelHandlerContext ctx) throws Exception {
				System.out.println("----channelActive----");
				ByteBuf message = null;
				for (int i = 0; i < 100; i++) {
					message = Unpooled.buffer(req.length);
					message.writeBytes(req);
					ctx.writeAndFlush(message);
				}
			}

			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				System.out.println("----channelRead----");
				String body = (String) msg;
				System.out.println("Now is : " + body + " ; the counter is : " + ++counter);
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
