package com.ym.netty.marshalling;

import org.junit.Test;

import com.ym.netty.marshalling.entity.SubScriptReq;
import com.ym.netty.marshalling.entity.SubscriptResp;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MarchallingTest {

	@Test
	public void testServer() throws Exception {
		new SubReqServer().start(8080);
	}

	@Test
	public void testClient() throws Exception {
		new SubReqClient().start("127.0.0.1", 8080);
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

	}
}
