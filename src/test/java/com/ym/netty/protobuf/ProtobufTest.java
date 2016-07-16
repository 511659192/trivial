package com.ym.netty.protobuf;

import org.junit.Test;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ym.netty.protobuf.proto.SubscribeReqProto;
import com.ym.netty.protobuf.proto.SubscribeRespProto;
import com.ym.netty.protobuf.proto.SubscribeReqProto.SubscribeReq;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
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
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;

public class ProtobufTest {
	
	private static byte[] encode(SubscribeReqProto.SubscribeReq req) {
		return req.toByteArray();
	}
	
	private static SubscribeReqProto.SubscribeReq decode(byte[] body) throws InvalidProtocolBufferException {
		return SubscribeReqProto.SubscribeReq.parseFrom(body);
	}
	
	private static SubscribeReqProto.SubscribeReq createSubscribeReq() {
		SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
		builder.setSubReqID(1);
		builder.setUserName("Lilinfeng");
		builder.setProductName("Netty book");
		builder.setAddress("address");
		return builder.build();
	}

	@Test
	public void testConvert() throws InvalidProtocolBufferException {
		SubscribeReqProto.SubscribeReq req = createSubscribeReq();
		System.out.println("before encode :" + req.toString());
		SubscribeReqProto.SubscribeReq req2 = decode(encode(req));
		System.out.println("after decode :" + req2.toString());
		System.out.println(req2.equals(req));
	}
	
	@Test
	public void testServer() throws Exception {
		new SubReqServer().bind(8080);
	}
	
	@Test
	public void testClient() throws Exception {
		new SubReqClient().connect("127.0.0.1", 8080);
	}
	class SubReqServer {
		
		public void bind(int port) throws Exception{
			EventLoopGroup bossGroup = new NioEventLoopGroup();
			EventLoopGroup worderGroup = new NioEventLoopGroup();
			try {
				ServerBootstrap serverBootstrap = new ServerBootstrap();
				serverBootstrap.group(bossGroup, worderGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 100)
					.handler(new LoggingHandler())
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel socketChannel) throws Exception {
							socketChannel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
							socketChannel.pipeline().addLast(new ProtobufDecoder(SubscribeReqProto.SubscribeReq.getDefaultInstance()));
							socketChannel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
							socketChannel.pipeline().addLast(new ProtobufEncoder());
							socketChannel.pipeline().addLast(new ChannelHandlerAdapter(){
								@Override
								public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
									SubscribeReqProto.SubscribeReq req = (SubscribeReq) msg;
									if ("Lilinfeng".equalsIgnoreCase(req.getUserName())) {
										System.out.println("Service accept client subscribe req : [" + req.toString() + "]");
										ctx.writeAndFlush(resp(req.getSubReqID()));
									}
								
								}

								private SubscribeRespProto.SubscribeResp resp(int subReqID) {
									SubscribeRespProto.SubscribeResp.Builder builder = SubscribeRespProto.SubscribeResp.newBuilder();
									builder.setSubReqID(subReqID);
									builder.setRespCode(0);
									builder.setDesc("Netty book order succed, 3 days later, sent to the designated address");
									return builder.build();
								}
								
								@Override
								public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
										throws Exception {
									cause.printStackTrace();
									ctx.close();
								}
							});
						}
					});
				ChannelFuture future = serverBootstrap.bind(port).sync();
				future.channel().closeFuture().sync();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				bossGroup.shutdownGracefully();
				worderGroup.shutdownGracefully();
			}
		}
	}
	
	class SubReqClient {
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
							socketChannel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
							socketChannel.pipeline().addLast(new ProtobufDecoder(SubscribeRespProto.SubscribeResp.getDefaultInstance()));
							socketChannel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
							socketChannel.pipeline().addLast(new ProtobufEncoder());
							socketChannel.pipeline().addLast(new ChannelHandlerAdapter(){
								@Override
								public void channelActive(ChannelHandlerContext ctx) throws Exception {
									for (int i = 0; i < 10; i++) {
										ctx.write(subReq(i));
									}
									ctx.flush();
								}

								@Override
								public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
									System.out.println("Receive server response : [" + msg + "]");
								}

								@Override
								public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
									ctx.flush();
								}
								
								public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
									cause.printStackTrace();
									ctx.close();
								};
								
								private SubscribeReqProto.SubscribeReq subReq(int i) {
									SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
									builder.setSubReqID(i);
									builder.setUserName("Lilinfeng");
									builder.setProductName("Netty book for protobuf");
									builder.setAddress("address");
									return builder.build();
								}
							});
						}
					});
				ChannelFuture future = bootstrap.connect(host, port).sync();
				future.channel().closeFuture().sync();
			} finally {
				group.shutdownGracefully();
			}
		}
	}
}
