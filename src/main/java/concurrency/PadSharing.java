package concurrency;

public final class PadSharing implements Runnable {
	public final static int NUM_THREADS = 8; // change
	public final static long ITERATIONS = 500L * 1000L * 1000L;
	private final int arrayIndex;

	private static Long[] longs = new Long[NUM_THREADS];

	public PadSharing(final int arrayIndex) {
		this.arrayIndex = arrayIndex;
	}

	public static void main(final String[] args) throws Exception {
		long start = System.nanoTime();
		runTest();
		System.out.println("duration = " + (System.nanoTime() - start));
//		start = System.nanoTime();
//		FalseSharing.runTest();
//		System.out.println("duration = " + (System.nanoTime() - start));
	}

	private static void runTest() throws InterruptedException {
		Thread[] threads = new Thread[NUM_THREADS];

		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new PadSharing(i));
		}

		for (Thread t : threads) {
			t.start();
		}

		for (Thread t : threads) {
			t.join();
		}
	}

	public void run() {
		long i = ITERATIONS + 1;
		while (0 != --i) {
			longs[arrayIndex] = i;
		}
	}
}
