package guava;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ForwardingList;

public class ForwardTest {

}

class AddLoggingList<E> extends ForwardingList<E> {
	final List<E> delegate = null; // backing list

	@Override
	protected List<E> delegate() {
		return delegate;
	}

	@Override
	public void add(int index, E elem) {
		super.add(index, elem);
	}

	@Override
	public boolean add(E elem) {
		return standardAdd(elem); // 用add(int, E)实现
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return standardAddAll(c); // 用add实现
	}
}