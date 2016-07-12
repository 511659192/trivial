package com.ym.netty;

import io.netty.channel.ChannelHandlerAdapter;

import io.netty.channel.ChannelHandlerContext;

public class EchoClientHandler extends ChannelHandlerAdapter {

	private int count;

	@Override

	public void channelActive(ChannelHandlerContext ctx) throws Exception {

		UserInfo[] infos = UserInfo();

		for (UserInfo info : infos) {

			ctx.writeAndFlush(info);

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

	public void channelRead(ChannelHandlerContext ctx, Object msg)

			throws Exception {

		System.out.println(" Client receive the msgpack message :" + msg + ",couter:" + ++count);

		// ctx.write(msg);

	}

	@Override

	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

		ctx.flush();

	}

	@Override

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)

			throws Exception {

		// TODO Auto-generated method stub

		super.exceptionCaught(ctx, cause);

		ctx.close();

	}

}