package com.ym.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class UdpIpNIoTest {
	public static void main(String[] args) throws Exception {
		new Thread(new Runnable() {
			public void run() {
				try {
					new UdpIpNIoServer().doSomething();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		TimeUnit.SECONDS.sleep(1);
		new Thread(new Runnable() {
			public void run() {
				try {
					new UdpIpNIoClient().doSomething();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}

class UdpIpNIoServer {
	public void doSomething() throws Exception {
		int rport = 9527;
		int sport = 9528;
		DatagramChannel sendChannel = DatagramChannel.open();
		sendChannel.configureBlocking(false);
		SocketAddress target = new InetSocketAddress("127.0.0.1", sport);
		sendChannel.connect(target);
		DatagramChannel receiveChannel = DatagramChannel.open();
		DatagramSocket serverSocket = receiveChannel.socket();
		serverSocket.bind(new InetSocketAddress(rport));
		System.out.println("Data receive listen on port: " + rport);
		receiveChannel.configureBlocking(false);
		Selector selector = Selector.open();
		receiveChannel.register(selector, SelectionKey.OP_READ);
		while (true) {
			int nKeys = selector.select(1000);
			if (nKeys > 0) {
				for (SelectionKey key : selector.selectedKeys()) {
					if (key.isReadable()) {
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						DatagramChannel dc = (DatagramChannel) key.channel();
						dc.receive(buffer);
						buffer.flip();
						String message = Charset.forName("UTF-8").decode(buffer).toString();
						System.out.println("Message from client: " + message);
						if ("quit".equalsIgnoreCase(message.trim())) {
							dc.close();
							selector.close();
							sendChannel.close();
							System.out.println("Server has been shutdown!");
							System.exit(0);
						}
						String outMessage = "Server response：" + message;
						sendChannel.write(Charset.forName("UTF-8").encode(outMessage));
					}
				}
				selector.selectedKeys().clear();
			}
		}
	}
}

class UdpIpNIoClient {
	public void doSomething() throws Exception {
		int rport = 9528;
		int sport = 9527;
		DatagramChannel receiveChannel = DatagramChannel.open();
		receiveChannel.configureBlocking(false);
		DatagramSocket socket = receiveChannel.socket();
		socket.bind(new InetSocketAddress(rport));
		Selector selector = Selector.open();
		receiveChannel.register(selector, SelectionKey.OP_READ);
		DatagramChannel sendChannel = DatagramChannel.open();
		sendChannel.configureBlocking(false);
		SocketAddress target = new InetSocketAddress("127.0.0.1", sport);
		sendChannel.connect(target);
		BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String command = systemIn.readLine();
			sendChannel.write(Charset.forName("UTF-8").encode(command));
			if (command == null || "quit".equalsIgnoreCase(command.trim())) {
				systemIn.close();
				sendChannel.close();
				selector.close();
				System.out.println("Client quit!");
				System.exit(0);
			}
			int nKeys = selector.select(1000);
			if (nKeys > 0) {
				for (SelectionKey key : selector.selectedKeys()) {
					if (key.isReadable()) {
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						DatagramChannel dc = (DatagramChannel) key.channel();
						dc.receive(buffer);
						buffer.flip();
						System.out.println(Charset.forName("UTF-8").decode(buffer).toString());
						buffer = null;
					}
				}
				selector.selectedKeys().clear();
			}
		}
	}
}