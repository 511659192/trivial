package guava;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class CacheTest {
	@Test
	public void TestLoadingCache() throws Exception {
		LoadingCache<String, String> cahceBuilder = CacheBuilder.newBuilder().build(new CacheLoader<String, String>() {
			@Override
			public String load(String key) throws Exception {
				System.out.println("------------------" + key);
				String strProValue = "hello " + key + "!";
				return strProValue;
			}

		});

		System.out.println("jerry value:" + cahceBuilder.get("jerry"));
		System.out.println("jerry value:" + cahceBuilder.get("jerry"));
		System.out.println("peida value:" + cahceBuilder.get("peida"));
		System.out.println("peida value:" + cahceBuilder.get("peida"));
		System.out.println("lisa value:" + cahceBuilder.getUnchecked("lisa"));
		cahceBuilder.put("harry", "ssdded");
		System.out.println("harry value:" + cahceBuilder.get("harry"));
	}

	@Test
	public void testcallableCache() throws Exception {
		Cache<String, String> cache = CacheBuilder.newBuilder().maximumSize(1000).build();
		String resultVal = cache.get("jerry", new Callable<String>() {
			public String call() {
				String strProValue = "hello " + "jerry" + "!";
				return strProValue;
			}
		});
		System.out.println("jerry value : " + resultVal);

		resultVal = cache.get("peida", new Callable<String>() {
			public String call() {
				String strProValue = "hello " + "peida" + "!";
				return strProValue;
			}
		});
		System.out.println("peida value : " + resultVal);
	}

	/**
	 * 不需要延迟处理(泛型的方式封装)
	 * 
	 * @return
	 */
	public <K, V> LoadingCache<K, V> cached(CacheLoader<K, V> cacheLoader) {
		LoadingCache<K, V> cache = CacheBuilder.newBuilder().maximumSize(2).weakKeys().softValues()
				.refreshAfterWrite(120, TimeUnit.SECONDS).expireAfterWrite(10, TimeUnit.MINUTES)
				.removalListener(new RemovalListener<K, V>() {
					public void onRemoval(RemovalNotification<K, V> rn) {
						System.out.println(rn.getKey() + "被移除");

					}
				}).build(cacheLoader);
		return cache;
	}

	/**
	 * 通过key获取value 调用方式 commonCache.get(key) ; return String
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */

	public LoadingCache<String, String> commonCache(final String key) throws Exception {
		LoadingCache<String, String> commonCache = cached(new CacheLoader<String, String>() {
			@Override
			public String load(String key) throws Exception {
				return "hello " + key + "!";
			}
		});
		return commonCache;
	}

	@Test
	public void testCache() throws Exception {
		LoadingCache<String, String> commonCache = commonCache("peida");
		System.out.println("peida:" + commonCache.get("peida"));
		commonCache.apply("harry");
		System.out.println("harry:" + commonCache.get("harry"));
		commonCache.apply("lisa");
		System.out.println("lisa:" + commonCache.get("lisa"));
	}
	
private static Cache<String, String> cacheFormCallable = null; 

    
    /**
     * 对需要延迟处理的可以采用这个机制；(泛型的方式封装)
     * @param <K>
     * @param <V>
     * @param key
     * @param callable
     * @return V
     * @throws Exception
     */
    public static <K,V> Cache<K , V> callableCached() throws Exception {
          Cache<K, V> cache = CacheBuilder
          .newBuilder()
          .maximumSize(10000)
          .expireAfterWrite(10, TimeUnit.MINUTES)
          .build();
          return cache;
    }

    
    private String getCallableCache(final String userName) {
           try {
             //Callable只有在缓存值不存在时，才会调用
             return cacheFormCallable.get(userName, new Callable<String>() {
                public String call() throws Exception {
                    System.out.println(userName+" from db");
                    return "hello "+userName+"!";
               }
              });
           } catch (ExecutionException e) {
              e.printStackTrace();
              return null;
            } 
    }
    
    @Test
    public void testCallableCache() throws Exception{
         final String u1name = "peida";
         final String u2name = "jerry"; 
         final String u3name = "lisa"; 
         cacheFormCallable=callableCached();
         System.out.println("peida:"+getCallableCache(u1name));
         System.out.println("jerry:"+getCallableCache(u2name));
         System.out.println("lisa:"+getCallableCache(u3name));
         System.out.println("peida:"+getCallableCache(u1name));
         
    }
}
