package guava;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

public class ImmutableTest {
	@Test
	public void testJDKImmutable() {
		List<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		list.add("c");

		System.out.println(list);

		List<String> unmodifiableList = Collections.unmodifiableList(list);

		System.out.println(unmodifiableList);

		List<String> unmodifiableList1 = Collections.unmodifiableList(Arrays.asList("a", "b", "c"));
		System.out.println(unmodifiableList1);

		String temp = unmodifiableList.get(1);
		System.out.println("unmodifiableList [0]£º" + temp);

		list.add("baby");
		System.out.println("list add a item after list:" + list);
		System.out.println("list add a item after unmodifiableList:" + unmodifiableList);

		unmodifiableList1.add("bb");
		System.out.println("unmodifiableList add a item after list:" + unmodifiableList1);

		unmodifiableList.add("cc");
		System.out.println("unmodifiableList add a item after list:" + unmodifiableList);
	}

	@Test
	public void testGuavaImmutable() {

		List<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		list.add("c");
		System.out.println("list£º" + list);

		ImmutableList<String> imlist = ImmutableList.copyOf(list);
		System.out.println("imlist£º" + imlist);

		ImmutableList<String> imOflist = ImmutableList.of("peida", "jerry", "harry");
		System.out.println("imOflist£º" + imOflist);

		ImmutableSortedSet<String> imSortList = ImmutableSortedSet.of("a", "b", "c", "a", "d", "b");
		System.out.println("imSortList£º" + imSortList);

		list.add("baby");
		System.out.println("list add a item after list:" + list);
		System.out.println("list add a item after imlist:" + imlist);

		ImmutableSet<Color> imColorSet = ImmutableSet.<Color> builder().add(new Color(0, 255, 255))
				.add(new Color(0, 191, 255)).build();

		System.out.println("imColorSet:" + imColorSet);
	}
	
	@Test
    public void testCotyOf(){
        ImmutableSet<String> imSet=ImmutableSet.of("peida","jerry","harry","lisa");
        System.out.println("imSet£º"+imSet);
        ImmutableList<String> imlist=ImmutableList.copyOf(imSet);
        System.out.println("imlist£º"+imlist);
        ImmutableSortedSet<String> imSortSet=ImmutableSortedSet.copyOf(imSet);
        System.out.println("imSortSet£º"+imSortSet);
        
        List<String> list=new ArrayList<String>();
        for(int i=0;i<20;i++){
            list.add(i+"x");
        }
        System.out.println("list£º"+list);
        ImmutableList<String> imInfolist=ImmutableList.copyOf(list.subList(2, 18));
        System.out.println("imInfolist£º"+imInfolist);
        int imInfolistSize=imInfolist.size();
        System.out.println("imInfolistSize£º"+imInfolistSize);
        ImmutableSet<String> imInfoSet=ImmutableSet.copyOf(imInfolist.subList(2, imInfolistSize-3));
        System.out.println("imInfoSet£º"+imInfoSet);
    }
	
	@Test
    public void testAsList(){
        ImmutableList<String> imList=ImmutableList.of("peida","jerry","harry","lisa","jerry");
        System.out.println("imList£º"+imList);
        ImmutableSortedSet<String> imSortList=ImmutableSortedSet.copyOf(imList);
        System.out.println("imSortList£º"+imSortList);
        System.out.println("imSortList as list£º"+imSortList.asList());
    }
}
