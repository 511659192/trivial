	public final class FalseSharing3 implements Runnable {  
	    public static int NUM_THREADS = 4; // change  
	    public final static long ITERATIONS = 1000L * 1000L * 1000L;  
	    private final int arrayIndex;  
	    private static VolatileLong[] longs;  
	  
	    public FalseSharing3(final int arrayIndex) {  
	        this.arrayIndex = arrayIndex;  
	    }  
	  
	    public static void main(final String[] args) throws Exception {  
	        System.out.println("starting....");  
	        if (args.length == 1) {  
	            NUM_THREADS = Integer.parseInt(args[0]);  
	        }  
	  
	        longs = new VolatileLong[NUM_THREADS];  
	        for (int i = 0; i < longs.length; i++) {  
	            longs[i] = new VolatileLong();  
	        }  
	        final long start = System.nanoTime();  
	        runTest();  
	        System.out.println("duration = " + (System.nanoTime() - start));  
	    }  
	  
	    private static void runTest() throws InterruptedException {  
	        Thread[] threads = new Thread[NUM_THREADS];  
	        for (int i = 0; i < threads.length; i++) {  
	            threads[i] = new Thread(new FalseSharing3(i));  
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
	            longs[arrayIndex].value = i;  
	        }  
	    }  
	  
	    public final static class VolatileLong {  
	        public long value = 0L;  
	        public long p1, p2, p3, p4, p5, p6; // 注释  
	    }  
	    
	    public static long preventFromOptimization(VolatileLong v) {  
	    	return v.p1 + v.p2 + v.p3 + v.p4 + v.p5 + v.p6;  
	    }

	} 