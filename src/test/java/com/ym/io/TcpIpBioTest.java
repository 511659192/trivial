package com.ym.io;

import org.junit.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class TcpIpBioTest {

	@org.junit.Test
	public void test() {
		System.out.println(25 / 10);
		System.out.println(25 % 10);
	}

	public static void main(String[] args) throws Exception {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new TcpIpBIoServer().doSomething();
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
					new TcpIpBIoClient().doSomething();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
}

class TcpIpBIoServer {
	
	public void doSomething() throws Exception {
		int port = 9527;
		ServerSocket ss = new ServerSocket(port);
		System.out.println("Server listen on port: " + port);
		Socket socket = ss.accept();
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		while (true) {
			String line = in.readLine();
			if (line == null) {
				Thread.sleep(100);
				continue;
			}
			if ("quit".equalsIgnoreCase(line.trim())) {
				in.close();
				out.close();
				ss.close();
				System.out.println("Server has been shutdown!");
				System.exit(0);
			} else {
				System.out.println("Message from client: " + line);
				out.println("Server response：" + line);
				Thread.sleep(100);
			}
		}
	}
}

class TcpIpBIoClient {
	
	public void doSomething() throws Exception {
		String host = "127.0.0.1";
		int port = 9527;
		Socket socket = new Socket(host, port);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
		boolean flag = true;
		while (flag) {
			String command = systemIn.readLine();
			if (command == null || "quit".equalsIgnoreCase(command.trim())) {
				flag = false;
				System.out.println("Client quit!");
				out.println("quit");
				out.close();
				in.close();
				socket.close();
				continue;
			}
			out.println(command);
			String response = in.readLine();
			System.out.println(response);
		}
	}
}

