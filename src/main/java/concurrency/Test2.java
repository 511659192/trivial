package concurrency;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

public class Test2 {

	private Long count = 500000000L;
	private Integer threadNum = 1;
	static Object lock = new Object();
	static PadLock padLock = new PadLock();
	static Long index = 0L;
	
	@Test
	public void noLock(){
		Long startTime = getTime();
		index = 0L;
		for (int i = 0; i < count; i++) {
			index++;
		}
		System.out.println("noLock:" + index);
		System.out.println(getTime() - startTime);
	}
	
	
	@Test
	public void hasLock(){
		Long startTime = getTime();
		index = 0L;
		for (int i = 0; i < count; i++) {
			synchronized (lock) {
				index++;
			}
		}
		System.out.println("hasLock:" + index);
		System.out.println(getTime() - startTime);
	}
	
	@Test
	public void hasPadLock(){
		Long startTime = getTime();
		index = 0L;
		for (int i = 0; i < count; i++) {
			synchronized (padLock) {
				index++;
			}
		}
		System.out.println("hasPadLock:" + index);
		System.out.println(getTime() - startTime);
	}
	
	@Test
	public void hasLock_2thread() throws InterruptedException{
		Long startTime = getTime();
		index = 0L;
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				for (int i = 0; i < count / 2; i++) {
					synchronized (lock) {
						index++;
					}
				}
			}
		});
		Thread t2 = new Thread(new Runnable() {
			public void run() {
				for (int i = 0; i < count / 2; i++) {
					synchronized (lock) {
						index++;
					}
				}
			}
		});
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		System.out.println("hasLock_2thread:" + index);
		System.out.println(getTime() - startTime);
	}
	
	@Test
	public void hasPadLock_2thread() throws InterruptedException{
		Long startTime = getTime();
		index = 0L;
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				for (int i = 0; i < count / 2; i++) {
					synchronized (padLock) {
						index++;
					}
				}
			}
		});
		Thread t2 = new Thread(new Runnable() {
			public void run() {
				for (int i = 0; i < count / 2; i++) {
					synchronized (padLock) {
						index++;
					}
				}
			}
		});
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		System.out.println("hasPadLock_2thread:" + index);
		System.out.println(getTime() - startTime);
	}
	
	private Long getTime() {
		return System.currentTimeMillis();
	}
	
}

class PadLock {
	public long p1, p2, p3, p4, p5, p6, p7;

	public long getP1() {
		return p1;
	}

	public void setP1(long p1) {
		this.p1 = p1;
	} 
}
