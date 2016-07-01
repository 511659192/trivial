package guava;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;

public class IteratorsTest {

	public <E> void peekIterator(E e, Iterable<E> source) {
		List<E> result = Lists.newArrayList();
		PeekingIterator<E> iter = Iterators.peekingIterator(source.iterator());
		while (iter.hasNext()) {
			E current = iter.next();
			while (iter.hasNext() && iter.peek().equals(current)) {
				// 跳过重复的元素
				iter.next();
			}
			result.add(current);
		}
	}

	public static Iterator<String> skipNulls(final Iterator<String> in) {
		return new AbstractIterator<String>() {
			protected String computeNext() {
				while (in.hasNext()) {
					String s = in.next();
					if (s != null) {
						return s;
					}
				}
				return endOfData();
			}
		};
	}

}
