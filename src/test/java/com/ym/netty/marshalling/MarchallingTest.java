package com.ym.netty.marshalling;

import org.junit.Test;

import com.ym.netty.marshalling.entity.SubScriptReq;
import com.ym.netty.marshalling.entity.SubscriptResp;
import com.ym.netty.protobuf.proto.SubscribeReqProto;
import com.ym.netty.protobuf.proto.SubscribeReqProto.SubscribeReq;
import com.ym.netty.protobuf.proto.SubscribeRespProto;

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
import io.netty.handler.logging.LoggingHandler;

public class MarchallingTest {

	@Test
	public void testServer() throws Exception {
		new SubReqServer().start(8080);
	}

	@Test
	public void testClient() throws Exception {
		new SubReqClient().start("127.0.0.1", 8080);
	}
	
	@Test
	public void testServer2() throws Exception {
		new SubReqServer().bind(8080);
	}

	@Test
	public void testClient2() throws Exception {
		new SubReqClient().connect("127.0.0.1", 8080);
	}

	class SubReqServer {

		public void start(int port) {
			NioEventLoopGroup workGroup = new NioEventLoopGroup();
			NioEventLoopGroup bossGroup = new NioEventLoopGroup();
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workGroup);
			bootstrap.channel(NioServerSocketChannel.class);
			// 配置 NioServerSocketChannel 的 tcp 参数, BACKLOG 的大小
			bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
			bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
					ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
					ch.pipeline().addLast(new ChannelHandlerAdapter() {
						@Override
						public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
							cause.printStackTrace();
							ctx.close();
							super.exceptionCaught(ctx, cause);
						}

						@Override
						public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

							System.out.println(msg);
							SubscriptResp sub = new SubscriptResp();
							sub.setDesc("desc");
							sub.setSubScriptID(999);
							sub.setRespCode("0");
							ctx.writeAndFlush(sub);
						}

						@Override
						public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
							ctx.flush();
						}
					});
				}
			});
			// 绑定端口,随后调用它的同步阻塞方法 sync 等等绑定操作成功,完成之后 Netty 会返回一个 ChannelFuture
			// 它的功能类似于的 Future,主要用于异步操作的通知回调.
			ChannelFuture channelFuture;
			try {
				channelFuture = bootstrap.bind(port).sync();
				// 等待服务端监听端口关闭,调用 sync 方法进行阻塞,等待服务端链路关闭之后 main 函数才退出.
				channelFuture.channel().closeFuture().sync();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				bossGroup.shutdownGracefully();
				workGroup.shutdownGracefully();
			}
		}

		public void bind(int port) throws Exception {
			EventLoopGroup bossGroup = new NioEventLoopGroup();
			EventLoopGroup worderGroup = new NioEventLoopGroup();
			try {
				ServerBootstrap serverBootstrap = new ServerBootstrap();
				serverBootstrap.group(bossGroup, worderGroup).channel(NioServerSocketChannel.class)
						.option(ChannelOption.SO_BACKLOG, 100).handler(new LoggingHandler())
						.childHandler(new ChannelInitializer<SocketChannel>() {

							@Override
							protected void initChannel(SocketChannel socketChannel) throws Exception {
								socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
								socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
								socketChannel.pipeline().addLast(new ChannelHandlerAdapter() {
									@Override
									public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
										SubscribeReqProto.SubscribeReq req = (SubscribeReq) msg;
										if ("Lilinfeng".equalsIgnoreCase(req.getUserName())) {
											System.out.println(
													"Service accept client subscribe req : [" + req.toString() + "]");
											ctx.writeAndFlush(resp(req.getSubReqID()));
										}

									}

									private SubscribeRespProto.SubscribeResp resp(int subReqID) {
										SubscribeRespProto.SubscribeResp.Builder builder = SubscribeRespProto.SubscribeResp
												.newBuilder();
										builder.setSubReqID(subReqID);
										builder.setRespCode(0);
										builder.setDesc(
												"Netty book order succed, 3 days later, sent to the designated address");
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
		public void start(String host, int port) {
			NioEventLoopGroup workGroup = new NioEventLoopGroup();

			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(workGroup);
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.option(ChannelOption.TCP_NODELAY, true);
			bootstrap.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
					ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
					ch.pipeline().addLast(new ChannelHandlerAdapter() {

						@Override
						public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
							cause.printStackTrace();
							ctx.close();
						}

						@Override
						public void channelActive(ChannelHandlerContext ctx) throws Exception {
							SubScriptReq req = new SubScriptReq();
							for (int i = 0; i < 100; i++) {

								req.setSubReq(999);
								req.setProductName("productName");
								req.setUserName("userName");
								req.setAddress("address");
								ctx.writeAndFlush(req);
							}
						}

						@Override
						public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
							System.out.println(msg);
						}

						@Override
						public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
							ctx.flush();
						}

					});
				}
			});

			// 发起异步链接操作
			ChannelFuture future;
			try {
				future = bootstrap.connect(host, port).sync();
				// 等待客户端链路关闭
				future.channel().closeFuture().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				workGroup.shutdownGracefully();
			}
		}

		public void connect(String host, int port) throws Exception {
			EventLoopGroup group = new NioEventLoopGroup();
			try {
				Bootstrap bootstrap = new Bootstrap();
				bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
						.handler(new ChannelInitializer<SocketChannel>() {

							@Override
							protected void initChannel(SocketChannel socketChannel) throws Exception {
								socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
								socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
								socketChannel.pipeline().addLast(new ChannelHandlerAdapter() {
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

									public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
											throws Exception {
										cause.printStackTrace();
										ctx.close();
									};

									private SubscribeReqProto.SubscribeReq subReq(int i) {
										SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq
												.newBuilder();
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
