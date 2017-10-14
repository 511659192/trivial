package com.ym.rxJava;

import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangm on 2017/9/5.
 */
public class ComposeTest {

    @Test
    public void testConcat() throws Exception {
        Observable<Integer> seq1 = Observable.range(0, 3);
        Observable<Integer> seq2 = Observable.range(10, 3);

        Observable.concat(seq1, seq2)
                .subscribe(System.out::println);
    }

    @Test
    public void testConcat2() throws Exception {
        Observable<String> words = Observable.just(
                "First",
                "Second",
                "Third",
                "Fourth",
                "Fifth",
                "Sixth"
        );

        Subscription subscription = Observable.concat(words.groupBy(v -> v.charAt(0)))
                .subscribe(System.out::println);
        subscription.unsubscribe();
    }

    @Test
    public void testConcatWith() throws Exception {
        Observable<Integer> seq1 = Observable.range(0, 3);
        Observable<Integer> seq2 = Observable.range(10, 3);
        Observable<Integer> seq3 = Observable.just(20);

        seq1.concatWith(seq2)
                .concatWith(seq3)
                .subscribe(System.out::println);
    }

    @Test
    public void testRepeat() throws Exception {
        Observable<Integer> words = Observable.range(0,2);

        words.repeat(2)
                .subscribe(System.out::println);
    }

    @Test
    public void testRepeatWhen() throws Exception {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);
        values
                .take(2)
                .repeatWhen(ob -> {
                    return ob.take(4);
                })
                .subscribe(new PrintSubscriber("repeatWhen"));
        System.in.read();
    }

    @Test
    public void testRepeatWhen2() throws Exception {
        Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

        values
                .take(5)
                .repeatWhen((ob)-> {
                    ob.subscribe();
                    return Observable.interval(2, TimeUnit.SECONDS);
                })
                .subscribe(new PrintSubscriber("repeatWhen"));
        System.in.read();
    }

    @Test
    public void testStartWith() throws Exception {
        Observable<Integer> values = Observable.range(0, 3);

        values.startWith(-1,-2)
                .subscribe(System.out::println);
    }

    @Test
    public void testAbm() throws Exception {
        Observable.amb(
                Observable.interval(100, TimeUnit.MILLISECONDS).map(i -> "First" + i),
                Observable.interval(50, TimeUnit.MILLISECONDS).map(i -> "Second" + i))
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testAmbWith() throws Exception {
        Observable.timer(100, TimeUnit.MILLISECONDS).map(i -> "First")
                .ambWith(Observable.timer(50, TimeUnit.MILLISECONDS).map(i -> "Second"))
                .ambWith(Observable.timer(70, TimeUnit.MILLISECONDS).map(i -> "Third"))
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testMerge() throws Exception {
        Observable.merge(
                Observable.interval(250, TimeUnit.MILLISECONDS).map(i -> "First" + i),
                Observable.interval(150, TimeUnit.MILLISECONDS).map(i -> "Second" + i))
                .take(10)
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testMergeWith() throws Exception {
        Observable.interval(250, TimeUnit.MILLISECONDS).map(i -> "First")
                .mergeWith(Observable.interval(150, TimeUnit.MILLISECONDS).map(i -> "Second"))
                .take(10)
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testMergeDelayError() throws Exception {
        Observable<Long> failAt200 =
                Observable.concat(
                        Observable.interval(100, TimeUnit.MILLISECONDS).take(2),
                        Observable.error(new Exception("Failed")));
        Observable<Long> completeAt400 =
                Observable.interval(100, TimeUnit.MILLISECONDS)
                        .take(4);

        Observable.mergeDelayError(failAt200, completeAt400)
                .subscribe(new PrintSubscriber("testMergeDelayError"));
        System.in.read();
    }

    @Test
    public void testMergeDelayError2() throws Exception {
        Observable<Long> failAt200 =
                Observable.concat(
                        Observable.interval(100, TimeUnit.MILLISECONDS).take(2),
                        Observable.error(new Exception("Failed")));
        Observable<Long> failAt300 =
                Observable.concat(
                        Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
                        Observable.error(new Exception("Failed")));
        Observable<Long> completeAt400 =
                Observable.interval(100, TimeUnit.MILLISECONDS)
                        .take(4);

        // 多个源 Observable 出现了异常，则合并后的 Observable 会用一个 CompositeException 来结束
        Observable.mergeDelayError(failAt200, failAt300, completeAt400)
                .subscribe(new PrintSubscriber("testMergeDelayError2"));
        System.in.read();
    }

    @Test
    public void testSwitchOnNext() throws Exception {
        Observable.switchOnNext(
                Observable.interval(100, TimeUnit.MILLISECONDS)
                        .map(i -> Observable.interval(30, TimeUnit.MILLISECONDS).map(i2 -> {
                            System.out.println("i2:" + i2 + " i:" + i);
                            return i;
                        }))
        )
                .take(9)
                .subscribe(new PrintSubscriber("testSwitchOnNext"));
        System.in.read();
    }

    @Test
    public void testSwitchOnNext2() throws Exception {
        //每隔500毫秒产生一个observable
        Observable<Observable<Long>> observable = Observable.timer(0, 500, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Long aLong) {
                        System.out.println("L1:" + aLong);
                        //每隔200毫秒产生一组数据（0,10,20,30,40)
                        return Observable.timer(0, 200, TimeUnit.MILLISECONDS).map(new Func1<Long, Long>() {
                            @Override
                            public Long call(Long aLong) {
                                System.out.println("L2:" + aLong);
                                return aLong * 10;
                            }
                        }).take(5);
                    }
                }).take(2);

        Observable.switchOnNext(observable)
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("Sequence complete.");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.err.println("Error: " + e.getMessage());
                    }

                    @Override
                    public void onNext(Long aLong) {
                        System.out.println("Next:" + aLong);
                    }
                });
        TimeUnit.SECONDS.sleep(5);
    }

    @Test
    public void testSwitchMap() throws Exception {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .switchMap(i ->
                        Observable.interval(30, TimeUnit.MILLISECONDS)
                                .map(l -> {
                                    System.out.println("l:" + l);
                                    return i;
                                }))
                .take(9)
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testZip() throws Exception {
        // 有多个 源 Observable，则 zip 会等待最慢的一个 Observable 发射完数据才开始组合这次发射的所有数据
        Observable.zip(
                Observable.interval(100, TimeUnit.MILLISECONDS)
                        .doOnNext(i -> System.out.println("Left emits " + i)),
                Observable.interval(150, TimeUnit.MILLISECONDS)
                        .doOnNext(i -> System.out.println("Right emits " + i)),
                (i1,i2) -> i1 + " - " + i2)
                .take(6)
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testZipCount() throws Exception {
        Observable.zip(
                Observable.range(0, 5),
                Observable.range(0, 3),
                Observable.range(0, 8),
                (i1,i2,i3) -> i1 + " - " + i2 + " - " + i3)
                .count()
                .subscribe(System.out::println);
    }

    @Test
    public void testZipWith() throws Exception {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .zipWith(
                        Observable.interval(150, TimeUnit.MILLISECONDS),
                        (i1,i2) -> i1 + " - " + i2)
                .take(6)
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testZipWithIterable() throws Exception {
        Observable.range(0, 5)
                .zipWith(
                        Arrays.asList(0,2,4,6,8),
                        (i1,i2) -> i1 + " - " + i2)
                .subscribe(System.out::println);
    }

    @Test
    public void testCombineLatest() throws Exception {
        Observable.combineLatest(
                Observable.interval(100, TimeUnit.MILLISECONDS)
                        .timestamp()
                        .doOnNext(i -> System.out.println("Left emits " + i)),
                Observable.interval(150, TimeUnit.MILLISECONDS)
                        .timestamp()
                        .doOnNext(i -> System.out.println("Right emits " + i)),
                (i1,i2) -> i1 + " - " + i2
        )
                .take(6)
                .subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testCombileLatest2() throws Exception {
        Observable<Integer> values1 = Observable.just(1, 2,3);
    }


}
