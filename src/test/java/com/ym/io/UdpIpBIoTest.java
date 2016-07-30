package com.ym.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class UdpIpBIoTest {
	public static void main(String[] args) throws Exception {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new UdpIpBIoServer().doSomething();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		TimeUnit.SECONDS.sleep(1);;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new UdpIpBIoClient().doSomething();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}

class UdpIpBIoServer {
	public void doSomething() throws Exception {
		int port = 9527;
		int aport = 9528;
		DatagramSocket server = new DatagramSocket(port);
		DatagramSocket client = new DatagramSocket();
		InetAddress serverAddress = InetAddress.getByName("localhost");
		byte[] buffer = new byte[65507];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		while (true) {
			server.receive(packet);
			String line = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
			if ("quit".equalsIgnoreCase(line.trim())) {
				server.close();
				System.exit(0);
			} else {
				System.out.println("Message from client: " + line);
				packet.setLength(buffer.length);
				String response = "Server response：" + line;
				byte[] datas = response.getBytes("UTF-8");
				DatagramPacket responsePacket = new DatagramPacket(datas, datas.length, serverAddress, aport);
				client.send(responsePacket);
				Thread.sleep(100);
			}
		}
	}
}

class UdpIpBIoClient {
	public void doSomething() throws Exception {
		int port = 9527;
		int aport = 9528;
		DatagramSocket serverSocket = new DatagramSocket(aport);
		byte[] buffer = new byte[65507];
		DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
		DatagramSocket socket = new DatagramSocket();
		InetAddress server = InetAddress.getByName("localhost");
		BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
		boolean flag = true;
		while (flag) {
			String command = systemIn.readLine();
			byte[] datas = command.getBytes("UTF-8");
			DatagramPacket packet = new DatagramPacket(datas, datas.length, server, port);
			socket.send(packet);
			if (command == null || "quit".equalsIgnoreCase(command.trim())) {
				flag = false;
				System.out.println("Client quit!");
				socket.close();
				continue;
			}
			serverSocket.receive(receivePacket);
			String receiveResponse = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
			System.out.println(receiveResponse);
		}
	}
}