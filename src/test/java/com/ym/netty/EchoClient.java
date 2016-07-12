package com.ym.netty;

import io.netty.bootstrap.Bootstrap;

import io.netty.channel.ChannelFuture;

import io.netty.channel.ChannelInitializer;

import io.netty.channel.ChannelOption;

import io.netty.channel.EventLoopGroup;

import io.netty.channel.nio.NioEventLoopGroup;

import io.netty.channel.socket.SocketChannel;

import io.netty.channel.socket.nio.NioSocketChannel;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import io.netty.handler.codec.LengthFieldPrepender;

public class EchoClient {

	public void connect(String post, int port) throws Exception {

		EventLoopGroup group = new NioEventLoopGroup();

		try {

			Bootstrap b = new Bootstrap();

			b.group(group)

					.channel(NioSocketChannel.class)

					.option(ChannelOption.TCP_NODELAY, true)

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

	public static void main(String[] args) {

		try {

			new EchoClient().connect("127.0.0.1", 20001);

		} catch (Exception e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}

	}

}
