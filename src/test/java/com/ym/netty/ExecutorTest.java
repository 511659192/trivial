package com.ym.netty;

import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import org.junit.Test;

import java.util.concurrent.*;

public class ExecutorTest {

	long SCHEDULE_PURGE_INTERVAL = TimeUnit.SECONDS.toNanos(1);
	private static final long START_TIME = System.nanoTime();

	@Test
	public void test1() throws Exception {
		System.out.println(deadlineNanos(SCHEDULE_PURGE_INTERVAL));

	}

	long deadlineNanos(long delay) {
		return nanoTime() + delay;
	}

	long nanoTime() {
		return System.nanoTime() - START_TIME;
	}


	public static void main(String[] args) {

		MyThreadEventExecutor executor = new MyThreadEventExecutor(null, new DefaultThreadFactory(ExecutorTest.class), false);
		executor.submit(new Runnable() {
			@Override
			public void run() {
				System.out.println("faefafef");
			}
		});
	}

}

class MyThreadEventExecutor extends SingleThreadEventExecutor {


	protected MyThreadEventExecutor(EventExecutorGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp) {
		super(parent, threadFactory, addTaskWakesUp);
	}

	@Override
	protected void run() {
		System.out.println("123445");
	}
}
