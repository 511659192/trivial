package com.ym.rxJava;

import org.junit.Test;
import rx.Observable;
import rx.Subscription;

import java.util.concurrent.TimeUnit;

/**
 * Created by yangm on 2017/9/3.
 */
public class CheckTest {

    @Test
    public void testAll() throws Exception {
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(0);
            o.onNext(10);
            o.onNext(10);
            o.onNext(2);
            o.onCompleted();
        });


        Subscription evenNumbers = values
                .all(i -> i % 2 == 0)
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
    }

    @Test
    public void testAll2() throws Exception {
        Observable<Long> values = Observable.just(1L, 2L, 3L, 4L);

        Subscription subscription = values
                .all(i -> i < 3) // Will fail eventually
                .subscribe(
                        v -> System.out.println("All: " + v),
                        e -> System.out.println("All: Error: " + e),
                        () -> System.out.println("All: Completed")
                );
//        Subscription subscription2 = values
//                .subscribe(
//                        v -> System.out.println(v),
//                        e -> System.out.println("Error: " + e),
//                        () -> System.out.println("Completed")
//                );
    }

    @Test
    public void testAllWithError() throws Exception {
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(0);
            o.onNext(2);
            o.onError(new Exception());
        });

        Subscription subscription = values
                // 如果源 Observable 出现了错误，则 all 操作就没有意义了，all 会直接发射一个 error 然后结束
                .all(i -> i % 2 == 0)
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
    }

    @Test
    public void testAllWithError2() throws Exception {
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onError(new Exception());
        });

        Subscription subscription = values
                // 如果源 Observable 在出错之前就发射了一个不满足条件的数据，则 源 Observable 的错误对 all 没有影响
                .all(i -> i % 2 == 0)
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
    }

    @Test
    public void testExists() throws Exception {
        Observable<Integer> values = Observable.range(0, 2);

        Subscription subscription = values
                // 如果源 exists 发射的数据中有一个满足条件，则 exists 就返回 true
                .exists(i -> i > 2)
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
    }

    @Test
    public void testIsEmpty() throws Exception {
        Observable<Long> values = Observable.timer(1000, TimeUnit.MILLISECONDS);

        Subscription subscription = values
                .isEmpty()
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
        System.in.read();
    }

    @Test
    public void testContains() throws Exception {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        Subscription subscription = values
                // contains 使用 Object.equals 函数来判断源 Observable 是否发射了相同的数据 只要遇到相同的数据，则 contains 就立刻返回
                .contains(4L)
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
        System.in.read();
    }

    @Test
    public void testDefaultIfEmpty() throws Exception {
        Observable<Integer> values = Observable.empty();

        Subscription subscription = values
                .defaultIfEmpty(2)
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
    }

    @Test
    public void testDefaultIfEmptyWithError() throws Exception {
        Observable<Integer> values = Observable.error(new Exception());

        Subscription subscription = values
                .defaultIfEmpty(2)
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
    }

    @Test
    public void testElementAt() throws Exception {
        Observable<Integer> values = Observable.range(100, 10);

        Subscription subscription = values
                // 从特定的位置选择一个数据发射
                .elementAt(2)
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
    }

    @Test
    public void testElementAtOrDefault() throws Exception {
        Observable<Integer> values = Observable.range(100, 10);

        Subscription subscription = values
                // 该函数和访问数组或者集合类似，如果 Observable 发射的数据个数没有这么多，则会抛出 java.lang.IndexOutOfBoundsException 。
                // 可以使用一个默认值（elementAtOrDefault）来避免抛出 java.lang.IndexOutOfBoundsException
                .elementAtOrDefault(22, 0)
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
    }

    @Test
    public void testSequenceEqual() throws Exception {
        Observable<String> strings = Observable.just("1", "2", "3");
        Observable<Integer> ints = Observable.just(1, 2, 3);

        // 比较两个 Observable 发射的数据是否是一样的，同样位置的数据是一样的
        Observable.sequenceEqual(strings, ints, (s,i) -> s.equals(i.toString()))
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );

    }
    @Test
    public void testSequenceEqualWithError() throws Exception {
        Observable<Integer> values = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onError(new Exception());
        });

        Observable.sequenceEqual(values, values)
                .subscribe(
                        v -> System.out.println(v),
                        e -> System.out.println("Error: " + e),
                        () -> System.out.println("Completed")
                );
    }

}
