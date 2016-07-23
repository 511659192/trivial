package com.ym.netty.marshalling;

import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.MarshallingEncoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;

public final class MarshallingCodeCFactory {
	public static MarshallingDecoder buildMarshallingDecoder() {
		MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
		MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		UnmarshallerProvider provider = new DefaultUnmarshallerProvider(factory, configuration);
		MarshallingDecoder decoder = new MarshallingDecoder(provider, 1024);
		return decoder;
	}
	public static MarshallingEncoder buildMarshallingEncoder() {
		MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
		MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		MarshallerProvider provider = new DefaultMarshallerProvider(factory, configuration);
		MarshallingEncoder encoder = new MarshallingEncoder(provider);
		return encoder;
	}
}