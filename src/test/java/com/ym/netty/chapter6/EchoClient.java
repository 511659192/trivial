package com.ym.netty.chapter6;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class EchoClient {
	
	public static void main(String[] args) throws Exception {
		new EchoClient("127.0.0.1", 8080, 10).run();
	}
	
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
				userInfo.setAge(i);
				userInfo.setName("ABCDEFG ---->" + i);
				userInfos[i] = userInfo;
			}
			return userInfos;
		}
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			UserInfo[] userInfos = userInfo();
			for (UserInfo userInfo : userInfos) {
				ctx.writeAndFlush(userInfo);
			}
//			ctx.flush();
			System.out.println("----channelActive----");
		}
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			System.out.println("----channelRead----");
			System.out.println("Client receive the msgpack message : " + msg);
//			ctx.write(msg);
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			System.out.println("----exceptionCaught----");
			cause.printStackTrace();
			ctx.close();
		}
	}
}
