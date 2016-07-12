package com.ym.netty.chapter6;


import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MsgpackEncoder extends MessageToByteEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext arg0, Object arg1, ByteBuf arg2) throws Exception {
		System.out.println("----encode----");
		MessagePack messagePack = new MessagePack();
		byte[] raw = messagePack.write(arg1);
		arg2.writeBytes(raw);
	}
}
