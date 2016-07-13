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
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;

public class MsgpackTestV2 {
	
	@Test
	public void testServer() throws Exception {
		new EchoServer().bind(8080);
	}
	
	@Test
	public void testClient() throws Exception {
		new EchoClient().connect("127.0.0.1", 8080);
	}
	
	class EchoServer {
		public void bind(int port) throws Exception {
			EventLoopGroup bossGroup = new NioEventLoopGroup();
			EventLoopGroup workGroup = new NioEventLoopGroup();
			try {
				ServerBootstrap b = new ServerBootstrap();
				b.group(bossGroup, workGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 102400)
						.childHandler(new ChannelInitializer<SocketChannel>() {
							@Override
							protected void initChannel(SocketChannel arg0) throws Exception {
								arg0.pipeline().addLast("frameDecoder",
										new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
								arg0.pipeline().addLast("msgpack decoder", new MsgpackDecoder());
								arg0.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
								arg0.pipeline().addLast("msgpack encoder", new MsgpackEncoder());
								arg0.pipeline().addLast(new EchoServerHandler());
							}
						});
				ChannelFuture future = b.bind(port).sync();
				future.channel().closeFuture().sync();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				bossGroup.shutdownGracefully();
				workGroup.shutdownGracefully();
			}
		}
	}
	
	class EchoServerHandler extends ChannelHandlerAdapter {
		private int count;

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			System.out.println(".....");
			System.out.println("接到客户端信息 ：" + msg + ",counter :" + ++count);
			ctx.write(msg);
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			// TODO Auto-generated method stub
			super.channelReadComplete(ctx);
			ctx.flush();
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			// TODO Auto-generated method stub
			super.exceptionCaught(ctx, cause);
			ctx.close();
			System.out.println("异常。。。关闭连接 ");
		}
	}
	
	class EchoClient {
		public void connect(String post, int port) throws Exception {
			EventLoopGroup group = new NioEventLoopGroup();
			try {
				Bootstrap b = new Bootstrap();
				b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
						.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
						.handler(new ChannelInitializer<SocketChannel>() {
							@Override
							protected void initChannel(SocketChannel arg0) throws Exception {
								arg0.pipeline().addLast("frameDecoder",
										new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
								arg0.pipeline().addLast("msgpack decoder", new MsgpackDecoder());
								arg0.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
								arg0.pipeline().addLast("msgpack encoder", new MsgpackEncoder());
								arg0.pipeline().addLast(new EchoClientHandler());
							}
						});
				ChannelFuture future = b.connect(post, port).sync();
				future.channel().closeFuture().sync();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				group.shutdownGracefully();
			}
		}
	}
	
	class EchoClientHandler extends ChannelHandlerAdapter {
		private int count;

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			UserInfo[] infos = UserInfo();
			for (UserInfo info : infos) {
				ctx.write(info);
				System.out.println("client write :" + info);
			}
			ctx.write("abc");
			ctx.flush();
		}

		private UserInfo[] UserInfo() {
			int n = 10;
			UserInfo[] infos = new UserInfo[n];
			UserInfo info = null;
			for (int i = 0; i < n; i++) {
				info = new UserInfo();
				info.buildUserId(i).buildUserName("name>abc> " + i);
				infos[i] = info;
			}
			System.out.println(infos[5]);
			return infos;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			System.out.println(" Client receive the msgpack message :" + msg + ",couter:" + ++count);
			// ctx.write(msg);
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			ctx.flush();
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			// TODO Auto-generated method stub
			super.exceptionCaught(ctx, cause);
			ctx.close();
		}
	}
	
	class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {
		@Override
		protected void decode(ChannelHandlerContext arg0, ByteBuf arg1, List<Object> arg2) throws Exception {
			final byte[] array;
			final int length = arg1.readableBytes();
			array = new byte[length];
			arg1.getBytes(arg1.readerIndex(), array, 0, length);
			MessagePack msgpack = new MessagePack();
			Object o = msgpack.read(array);
			arg2.add(o);
			System.out.println("解码：" + o);
		}
	}
	
	class MsgpackEncoder extends MessageToByteEncoder<UserInfo> {
		@Override
		protected void encode(ChannelHandlerContext arg0, UserInfo arg1, ByteBuf arg2) throws Exception {
			MessagePack msgpack = new MessagePack();
			msgpack.register(UserInfo.class);
			byte[] raw = msgpack.write(arg1);
			arg2.writeBytes(raw);
		}
	}
}
