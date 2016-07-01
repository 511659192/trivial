package guava.concurrent;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class FutureTest {
	@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	@Test
	public void should_test_furture() throws Exception {
		ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

		ListenableFuture future1 = service.submit(new Callable<Integer>() {
			public Integer call() throws InterruptedException {
				Thread.sleep(4000);
				double d = 1 / 0;
				System.out.println("call future 1.");
				return 1;
			}
		});

		ListenableFuture future2 = service.submit(new Callable<Integer>() {
			public Integer call() throws InterruptedException {
				Thread.sleep(1000);
				System.out.println("call future 2.");
				// throw new RuntimeException("----call future 2.");
				return 2;
			}
		});

		
		final ListenableFuture allFutures = Futures.successfulAsList(future1, future2);
		System.out.println(allFutures.get());
		final ListenableFuture transform = Futures.transform(allFutures, new AsyncFunction<List<Integer>, Boolean>() {
			public ListenableFuture apply(List<Integer> results) throws Exception {
				System.out.println(JSON.toJSON(results));
				return Futures.immediateFuture(String.format("success future:%d", results.size()));
			}
		});
		System.out.println(transform.get());
		Futures.addCallback(transform, new FutureCallback<Object>() {

			public void onSuccess(Object result) {
				System.out.printf("success with: %s%n", result);
			}

			public void onFailure(Throwable thrown) {
				System.out.printf("onFailure%s%n", thrown.getMessage());
			}
		});

		System.out.println(transform.get());
	}
}
