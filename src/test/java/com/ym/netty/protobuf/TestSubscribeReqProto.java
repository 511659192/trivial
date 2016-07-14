package com.ym.netty.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;

public class TestSubscribeReqProto {

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

	public static void main(String[] args) throws InvalidProtocolBufferException {
		SubscribeReqProto.SubscribeReq req = createSubscribeReq();
		System.out.println("before encode :" + req.toString());
		SubscribeReqProto.SubscribeReq req2 = decode(encode(req));
		System.out.println("after decode :" + req2.toString());
		System.out.println(req2.equals(req));
	}
}
