package com.ym.netty;

import java.util.List;

import org.junit.Test;
import org.msgpack.MessagePack;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
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
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class MsgpackTest {
	
	@Test
	public void testServer() throws Exception {
		new EchoServer(8080).run();
	}
	
	@Test
	public void testClient() throws Exception {
		new EchoClient("127.0.0.1", 8080, 100).run();
	}
	
	class EchoServer {
		
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
			
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
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
	
	class EchoClient {
		
		String host;
		int port;
		int sendNumber;
		
		public EchoClient(String host, int port, int sendNumber) {
			super();
			this.host = host;
			this.port = port;
			this.sendNumber = sendNumber;
		}

		public void run() throws Exception {
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
							socketChannel.pipeline().addLast("msgpack decoder", new MsgpackDecoder());
							socketChannel.pipeline().addLast("msgpack encoder", new MsgpackEncoder());
							socketChannel.pipeline().addLast(new EchoClientHander(sendNumber));
						}
					});
				
				ChannelFuture future = bootstrap.connect(host, port).sync();
				future.channel().closeFuture().sync();
			} catch (Exception e) {
				group.shutdownGracefully();
			}
		}
		
		class EchoClientHander extends ChannelHandlerAdapter {
			int sendNumber;

			public EchoClientHander(int sendNumber) {
				super();
				this.sendNumber = sendNumber;
			}
			
			private UserInfo[] userInfo() {
				UserInfo[] userInfos = new UserInfo[sendNumber];
				UserInfo userInfo = null;
				for (int i = 0; i < sendNumber; i++) {
					userInfo = new UserInfo();
					userInfo.setUserId(i);
					userInfo.setUserName("ABCDEFG ---->" + i);
					userInfos[i] = userInfo;
				}
				return userInfos;
			}
			
			@Override
			public void channelActive(ChannelHandlerContext ctx) throws Exception {
				UserInfo[] userInfos = userInfo();
				for (UserInfo userInfo : userInfos) {
					ctx.write(userInfo);
				}
				ctx.flush();
				System.out.println("----channelActive----");
			}
			
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				System.out.println("----channelRead----");
				System.out.println("Client receive the msgpack message : " + msg);
//				ctx.write(msg);
			}
			
			@Override
			public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
				System.out.println("----exceptionCaught----");
				cause.printStackTrace();
				ctx.close();
			}
		}
	}
	
	class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {

		@Override
		protected void decode(ChannelHandlerContext arg0, ByteBuf arg1, List<Object> arg2) throws Exception {
			System.out.println("----decode----");
			byte[] array;
			int len = arg1.readableBytes();
			array = new byte[len];
			arg1.getBytes(arg1.readerIndex(), array, 0, len);
			MessagePack messagePack = new MessagePack();
			arg2.add(messagePack.read(array));
		}
	}
	
	class MsgpackEncoder extends MessageToByteEncoder<UserInfo> {

		@Override
		protected void encode(ChannelHandlerContext arg0, UserInfo arg1, ByteBuf arg2) throws Exception {
			System.out.println("----encode----");
			MessagePack messagePack = new MessagePack();
			messagePack.register(UserInfo.class);
			byte[] raw = messagePack.write(arg1);
			arg2.writeBytes(raw);
		}
	}
}
