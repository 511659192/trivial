package reference;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class ReferenceTest {

	@Test
	public void testSoftReference() {
		SoftReference<String> sr = new SoftReference<String>(new String("hello"));
		System.out.println(sr.get());
	}

	@Test
	public void testWeakReference() {
		WeakReference<String> sr = new WeakReference<String>(new String("hello"));
		System.out.println(sr.get());
		System.gc(); // 通知JVM的gc进行垃圾回收
		System.out.println(sr.get());
	}

	@Test
	public void testPhantomReference() {
		ReferenceQueue<String> queue = new ReferenceQueue<String>();
		PhantomReference<String> pr = new PhantomReference<String>(new String("hello"), queue);
		System.out.println(pr.get());
	}
}
