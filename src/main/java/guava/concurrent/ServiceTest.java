package guava.concurrent;

import junit.framework.TestCase;
import static com.google.common.util.concurrent.Service.State.FAILED;
import static com.google.common.util.concurrent.Service.State.NEW;
import static com.google.common.util.concurrent.Service.State.RUNNING;
import static com.google.common.util.concurrent.Service.State.STARTING;
import static com.google.common.util.concurrent.Service.State.STOPPING;
import static com.google.common.util.concurrent.Service.State.TERMINATED;

import org.junit.Test;

public class ServiceTest extends TestCase {

	/**
	 * Assert on the comparison ordering of the State enum since we guarantee
	 * it.
	 */
	@Test
	public void testStateOrdering() {
		// List every valid (direct) state transition.
		
		assertLessThan(NEW, STARTING);
		assertLessThan(NEW, TERMINATED);

		assertLessThan(STARTING, RUNNING);
		assertLessThan(STARTING, STOPPING);
		assertLessThan(STARTING, FAILED);

		assertLessThan(RUNNING, STOPPING);
		assertLessThan(RUNNING, FAILED);

		assertLessThan(STOPPING, FAILED);
		assertLessThan(STOPPING, TERMINATED);
	}

	private static <T extends Comparable<? super T>> void assertLessThan(T a, T b) {
		if (a.compareTo(b) >= 0) {
			fail(String.format("Expected %s to be less than %s", a, b));
		}
	}
}
