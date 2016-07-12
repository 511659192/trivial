package com.ym.netty.chapter6;


import java.util.List;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {

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