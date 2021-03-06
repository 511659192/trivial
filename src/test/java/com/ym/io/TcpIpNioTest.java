package com.ym.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class TcpIpNioTest {
	public static void main(String[] args) throws Exception {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new TcpIpNIoServer().doSomething();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		TimeUnit.SECONDS.sleep(1);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new TcpIpNIoClient().doSomething();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
class TcpIpNIoServer {
	public void doSomething() throws Exception {
		int port = 9527;
		Selector selector = Selector.open();
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ServerSocket serverSocket = ssc.socket();
		serverSocket.bind(new InetSocketAddress(port));
		System.out.println("Server listen on port: " + port);
		ssc.configureBlocking(false);
		ssc.register(selector, SelectionKey.OP_ACCEPT);
		while (true) {
			int nKeys = selector.select(1000);
			if (nKeys > 0) {
				for (SelectionKey key : selector.selectedKeys()) {
					if (key.isAcceptable()) {
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						SocketChannel sc = server.accept();
						if (sc == null) {
							continue;
						}
						sc.configureBlocking(false);
						sc.register(selector, SelectionKey.OP_READ);
					} else if (key.isReadable()) {
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						SocketChannel sc = (SocketChannel) key.channel();
						int readBytes = 0;
						String message = null;
						try {
							int ret;
							try {
								while ((ret = sc.read(buffer)) > 0) {
									readBytes += ret;
								}
							} catch (Exception e) {
								readBytes = 0;
								// IGNORE
							} finally {
								buffer.flip();
							}
							if (readBytes > 0) {
								message = Charset.forName("UTF-8").decode(buffer).toString();
								buffer = null;
							}
						} finally {
							if (buffer != null) {
								buffer.clear();
							}
						}
						if (readBytes > 0) {
							System.out.println("Message from client: " + message);
							if ("quit".equalsIgnoreCase(message.trim())) {
								sc.close();
								selector.close();
								System.out.println("Server has been shutdown!");
								System.exit(0);
							}
							String outMessage = "Server response：" + message;
							sc.write(Charset.forName("UTF-8").encode(outMessage));
						}
					}
				}
				selector.selectedKeys().clear();
			}
		}
	}
}

class TcpIpNIoClient {
	public void doSomething() throws Exception {
		int port = 9527;
		SocketChannel channel = SocketChannel.open();
		channel.configureBlocking(false);
		SocketAddress target = new InetSocketAddress("127.0.0.1", port);
		channel.connect(target);
		Selector selector = Selector.open();
		channel.register(selector, SelectionKey.OP_CONNECT);
		BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			if (channel.isConnected()) {
				String command = systemIn.readLine();
				channel.write(Charset.forName("UTF-8").encode(command));
				if (command == null || "quit".equalsIgnoreCase(command.trim())) {
					systemIn.close();
					channel.close();
					selector.close();
					System.out.println("Client quit!");
					System.exit(0);
				}
			}
			int nKeys = selector.select(1000);
			if (nKeys > 0) {
				for (SelectionKey key : selector.selectedKeys()) {
					if (key.isConnectable()) {
						SocketChannel sc = (SocketChannel) key.channel();
						sc.configureBlocking(false);
						sc.register(selector, SelectionKey.OP_READ);
						sc.finishConnect();
					} else if (key.isReadable()) {
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						SocketChannel sc = (SocketChannel) key.channel();
						int readBytes = 0;
						try {
							int ret = 0;
							try {
								while ((ret = sc.read(buffer)) > 0) {
									readBytes += ret;
								}
							} finally {
								buffer.flip();
							}
							if (readBytes > 0) {
								System.out.println(Charset.forName("UTF-8").decode(buffer).toString());
								buffer = null;
							}
						} finally {
							if (buffer != null) {
								buffer.clear();
							}
						}
					}
				}
				selector.selectedKeys().clear();
			}
		}
	}
}