package com.ym.asm.webSocket;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.Date;

import org.junit.Test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

public class WebSocketTest {
	
	@Test
	public void test() throws Exception {
		new WebSocketServer().run(8080);
	}

	class WebSocketServer {
		public void run(int port) throws Exception {
			EventLoopGroup bossGroup = new NioEventLoopGroup();
			EventLoopGroup workerGroup = new NioEventLoopGroup();
			try {
				ServerBootstrap serverBootstrap = new ServerBootstrap();
				serverBootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel socketChannel) throws Exception {
							ChannelPipeline pipeline = socketChannel.pipeline();
							pipeline.addLast("http-codec", new HttpServerCodec());
							pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
							pipeline.addLast("http-chunked", new ChunkedWriteHandler());
							pipeline.addLast("handler", new SimpleChannelInboundHandler<Object>() {

								private WebSocketServerHandshaker handshaker;
			
								protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
									if (msg instanceof FullHttpRequest) {
										handleHttpRequest(ctx, (FullHttpRequest) msg);
									} else if (msg instanceof WebSocketFrame) {
										handleHttpSocketFrame(ctx, (WebSocketFrame) msg);
									}
								}
								
								@Override
								public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
									ctx.flush();
								}
								
								@Override
								public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
										throws Exception {
									cause.printStackTrace();
									ctx.close();
								}

								private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
									if (!req.getDecoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))) {
										sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
										return;
									}
									
									WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket", null, false);
									handshaker = factory.newHandshaker(req);
									if (handshaker == null) {
										WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
									} else {
										handshaker.handshake(ctx.channel(), req);
									}
								}

								private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req,
										FullHttpResponse res) {
									if (res.getStatus().code() != 200) {
										ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
										res.content().writeBytes(buf);
										buf.release();
										setContentLength(res, res.content().readableBytes());
									}
									
									ChannelFuture future = ctx.channel().writeAndFlush(res);
									if (!isKeepAlive(req) || res.getStatus().code() != 200) {
										future.addListener(ChannelFutureListener.CLOSE);
									}
								}

								private void handleHttpSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
									
									if (frame instanceof CloseWebSocketFrame) {
										handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
										return;
									}
									
									if (frame instanceof PingWebSocketFrame) {
										ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
										return;
									}
									
									if (!(frame instanceof TextWebSocketFrame)) {
										throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
									}
									
									String request = ((TextWebSocketFrame) frame).text();
									
									ctx.channel().write(new TextWebSocketFrame(request + " , 欢迎使用netty websocket服务，现在时刻：" + new Date().toLocaleString()));
								}
							});
						}
					});
				serverBootstrap.bind(port).sync().channel().closeFuture().sync();
			} finally {
				bossGroup.shutdownGracefully();
				workerGroup.shutdownGracefully();
			}
		}
		
	}
}
