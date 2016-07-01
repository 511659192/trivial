package concurrency;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class ConcurrencyTest {

	@Test
	public void testCountDownLatch() {
		int count = 10;
		final CountDownLatch latch = new CountDownLatch(count);
		ExecutorService es = Executors.newFixedThreadPool(count);
		for (int i = 0; i < count; i++) {
			es.execute(new TaskTest((i + 1) * 1000, latch));
		}

		try {
			System.out.println(" waiting...");
			// 主线程等待其它事件发生
			latch.await();
			// 其它事件已发生，继续执行主线程
			System.out.println(" continue。。。");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			es.shutdown();
		}
	}

	@Test
	public void testSemaphore() throws InterruptedException {
		// 线程数目
		int threadCount = 10;
		// 资源数目
		Semaphore semaphore = new Semaphore(3);

		ExecutorService es = Executors.newFixedThreadPool(threadCount);

		// 启动若干线程
		for (int i = 0; i < threadCount; i++)
			es.execute(new ConsumeResourceTask((i + 1) * 1000, semaphore));
		TimeUnit.SECONDS.sleep(10);
	}

	@Test
	public void testCyclicBarrier() throws InterruptedException {
		int count = 5;

		ExecutorService es = Executors.newFixedThreadPool(count);

		CyclicBarrier barrier = new CyclicBarrier(count, new Runnable() {
			public void run() {
				ConcurrencyTest.print(" 所有线程到达栅栏处,可以在此做一些处理...");
			}
		});
		for (int i = 0; i < count; i++)
			es.execute(new CyclicBarrierTaskTest(barrier, (i + 1) * 1000));
		
		TimeUnit.SECONDS.sleep(10);
	}

	@Test
	public void testBlockingQueue() throws InterruptedException {
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10);

        for (int i = 0; i < 1; i++) {
            new Thread(new Producer(queue)).start();
        }
        for (int i = 0; i < 6; i++) {
            new Thread(new Consumer(queue)).start();
        }
        
        TimeUnit.SECONDS.sleep(10);
	}
	
	public static void print(String str) {
		SimpleDateFormat dfdate = new SimpleDateFormat("HH:mm:ss");
		System.out.println("[" + dfdate.format(new Date()) + "]" + Thread.currentThread().getName() + str);
	}
}

class Producer implements Runnable {
    private final BlockingQueue<String> fileQueue;

    public Producer(BlockingQueue<String> queue) {
        this.fileQueue = queue;

    }

    public void run() {
        try {
            while (true) {
                TimeUnit.MILLISECONDS.sleep(1000);
                String produce = this.produce();
                System.out.println(Thread.currentThread() + "生产：" + produce);
                fileQueue.put(produce);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String produce() {
        SimpleDateFormat dfdate = new SimpleDateFormat("HH:mm:ss");
        return dfdate.format(new Date());
    }
}

// 消费者
class Consumer implements Runnable {
    private final BlockingQueue<String> queue;

    public Consumer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            while (true) {
                TimeUnit.MILLISECONDS.sleep(1000);
                System.out.println(Thread.currentThread() + "prepare 消费");
                System.out.println(Thread.currentThread() + "starting："
                        + queue.take());
                System.out.println(Thread.currentThread() + "end 消费");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class CyclicBarrierTaskTest implements Runnable {
	private CyclicBarrier cyclicBarrier;

	private int timeout;

	public CyclicBarrierTaskTest(CyclicBarrier cyclicBarrier, int timeout) {
		this.cyclicBarrier = cyclicBarrier;
		this.timeout = timeout;
	}

	public void run() {
		ConcurrencyTest.print(" 正在running...");
		try {
			TimeUnit.MILLISECONDS.sleep(timeout);
			ConcurrencyTest.print(" 到达栅栏处，等待其它线程到达");
			cyclicBarrier.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}

		ConcurrencyTest.print(" 所有线程到达栅栏处，继续执行各自线程任务...");
	}
}

class TaskTest implements Runnable {

	private CountDownLatch latch;
	private int sleepTime;

	public TaskTest(int sleepTime, CountDownLatch latch) {
		this.sleepTime = sleepTime;
		this.latch = latch;
	}

	public void run() {
		try {
			System.out.println(" is running。");
			TimeUnit.MILLISECONDS.sleep(sleepTime);
			System.out.println(" finished。");
			// 计数器减减
			latch.countDown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class ConsumeResourceTask implements Runnable {
	private Semaphore semaphore;
	private int sleepTime;

	/**
	     * 
	     */
	public ConsumeResourceTask(int sleepTime, Semaphore semaphore) {
		this.sleepTime = sleepTime;
		this.semaphore = semaphore;
	}

	public void run() {
		try {
			// 获取资源
			semaphore.acquire();
			ConcurrencyTest.print(" 占用一个资源...");
			TimeUnit.MILLISECONDS.sleep(sleepTime);
			ConcurrencyTest.print(" 资源使用结束，释放资源");
			// 释放资源
			semaphore.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}