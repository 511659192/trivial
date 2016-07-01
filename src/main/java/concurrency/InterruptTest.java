package concurrency;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class InterruptTest {

	private static void listFile(File f) throws InterruptedException {
		if(f == null) {
			throw new IllegalArgumentException();
		}
		if(f.isFile()) {
//			System.out.println(f);
			return;
		}
		File[] allFiles = f.listFiles();
		if(Thread.interrupted()) {
			throw new InterruptedException("文件扫描任务被中断");
		}
		if (allFiles == null) {
			return;
		}
		for(File file : allFiles) {
			//还可以将中断检测放到这里
			listFile(file);
		}
	}
	
	public static String readFromConsole() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			return reader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static void main(String[] args) throws Exception {
		final Thread fileIteratorThread = new Thread() {
			public void run() {
				try {
					listFile(new File("c:\\"));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread() {
			public void run() {
				while(true) {
					if("quit".equalsIgnoreCase(readFromConsole())) {
						if(fileIteratorThread.isAlive()) {
							fileIteratorThread.interrupt();
							return;
						}
					} else {
						System.out.println("输入quit退出文件扫描");
					}
				}
			}
		}.start();
		fileIteratorThread.start();
	}
}
