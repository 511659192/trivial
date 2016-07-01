package guava;

import org.junit.Test;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

public class OtherTest {
	@Test
    public void RangeSetTest(){
        RangeSet<Integer> rangeSet = TreeRangeSet.create();
        rangeSet.add(Range.closed(1, 10));
        System.out.println("rangeSet1:"+rangeSet);
        rangeSet.add(Range.closedOpen(11, 15)); 
        System.out.println("rangeSet2:"+rangeSet);
        rangeSet.add(Range.open(15, 20));
        System.out.println("rangeSet3:"+rangeSet);
        rangeSet.add(Range.openClosed(0, 0)); 
        System.out.println("rangeSet4:"+rangeSet);
        rangeSet.remove(Range.open(5, 10)); 
        System.out.println("rangeSet5:"+rangeSet);
    }
}
