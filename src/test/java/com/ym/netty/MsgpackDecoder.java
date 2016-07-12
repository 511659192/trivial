package com.ym.netty;

import java.util.List;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;

import io.netty.channel.ChannelHandlerContext;

import io.netty.handler.codec.MessageToMessageDecoder;

public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {

	@Override

	protected void decode(ChannelHandlerContext arg0, ByteBuf arg1,

			List<Object> arg2) throws Exception {

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
