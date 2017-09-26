package com.ym.rxJava;


import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by yangm on 2017/9/6.
 */
public class OperatorTest {
    static class AverageAcc {
        public final int sum;
        public final int count;
        public AverageAcc(int sum, int count) {
            this.sum = sum;
            this.count = count;
        }
    }

    public static Observable<Double> runningAverage(Observable<Integer> source) {
        return source
                .scan(
                        new AverageAcc(0,0),
                        (acc, v) -> new AverageAcc(acc.sum + v, acc.count + 1))
                .filter(acc -> acc.count > 0)
                .map(acc -> acc.sum/(double)acc.count);
    }

    @Test
    public void testAvg() throws Exception {
        runningAverage(Observable.just(3, 5, 6, 4, 4))
                .subscribe(System.out::println);
    }

    @Test
    public void testAvg2() throws Exception {
        runningAverage(
                Observable.just("The brown fox jumped and I forget the rest")
                        .flatMap(phrase -> Observable.from(phrase.split(" ")))
                        .map(word -> word.length()))
                .subscribe(System.out::println);
    }

    class RunningAverage implements Observable.Transformer<Integer, Double> {
        private class AverageAccInner {
            public final int sum;
            public final int count;
            public AverageAccInner(int sum, int count) {
                this.sum = sum;
                this.count = count;
            }
        }

        @Override
        public Observable<Double> call(Observable<Integer> source) {
            return source
                    .scan(
                            new AverageAccInner(0,0),
                            (acc, v) -> new AverageAccInner(acc.sum + v, acc.count + 1))
                    .filter(acc -> acc.count > 0)
                    .map(acc -> acc.sum/(double)acc.count);
        }

    }

    @Test
    public void testAvg3() throws Exception {
        Observable.just(3, 5, 6, 4, 4)
                .compose(new RunningAverage())
                .subscribe(System.out::println);
    }


    class RunningAverageWithParam implements Observable.Transformer<Integer, Double> {
        private class AverageAccInner {
            public final int sum;
            public final int count;
            public AverageAccInner(int sum, int count) {
                this.sum = sum;
                this.count = count;
            }
        }

        final int threshold;

        public RunningAverageWithParam() {
            this.threshold = Integer.MAX_VALUE;
        }

        public RunningAverageWithParam(int threshold) {
            this.threshold = threshold;
        }

        @Override
        public Observable<Double> call(Observable<Integer> source) {
            return source
                    .filter(i -> i< this.threshold)
                    .scan(
                            new AverageAccInner(0,0),
                            (acc, v) -> new AverageAccInner(acc.sum + v, acc.count + 1))
                    .filter(acc -> acc.count > 0)
                    .map(acc -> acc.sum/(double)acc.count);
        }
    }

    @Test
    public void testAvg4() throws Exception {
        Observable.just(3, 5, 6, 4, 4)
                .compose(new RunningAverageWithParam(5))
                .subscribe(System.out::println);
    }

    class MyMap<T,R> implements Observable.Operator<R, T> {

        private Func1<T,R> transformer;

        public MyMap() {
        }

        public MyMap(Func1<T,R> transformer) {
            this.transformer = transformer;
        }

        public <T,R> MyMap<T,R> create(Func1<T,R> transformer) {
            return new MyMap<T,R>(transformer);
        }

        @Override
        public Subscriber<? super T> call(Subscriber<? super R> subscriber) {
            return new Subscriber<T>() {

                @Override
                public void onCompleted() {
                    if (!subscriber.isUnsubscribed())
                        subscriber.onCompleted();
                }

                @Override
                public void onError(Throwable e) {
                    if (!subscriber.isUnsubscribed())
                        subscriber.onError(e);
                }

                @Override
                public void onNext(T t) {
                    if (!subscriber.isUnsubscribed())
                        subscriber.onNext(transformer.call(t));
                }

            };
        }
    }

    @Test
    public void testMap() throws Exception {
        Observable.range(0, 5)
                .lift(new MyMap<Integer, String>(i -> i + "!"))
                .subscribe(System.out::println);

    }

    @Test
    public void testMap2() throws Exception {
        Observable.range(0, 5)
                .lift(new MyMap().create(i -> i + "!"))
                .subscribe(System.out::println);
    }

    @Test
    public void testInvalidProcess() throws Exception {
        Observable<Integer> source = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onCompleted();
            o.onNext(3);
            o.onCompleted();
        });

        source.doOnUnsubscribe(() -> System.out.println("Unsubscribed"))
                .subscribe(
                        System.out::println,
                        System.out::println,
                        () -> System.out.println("Completed"));

    }

    @Test
    public void testInvalidProcess2() throws Exception {
        Observable<Integer> source = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onCompleted();
            o.onNext(3);
            o.onCompleted();
        });

        source.doOnUnsubscribe(() -> System.out.println("Unsubscribed"))
                .unsafeSubscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("Completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e);
                    }

                    @Override
                    public void onNext(Integer t) {
                        System.out.println(t);
                    }
                });
    }

    @Test
    public void testSerialize() throws Exception {
        Observable<Integer> source = Observable.create(o -> {
                    o.onNext(1);
                    o.onNext(2);
                    o.onCompleted();
                    o.onNext(3);
                    o.onCompleted();
                })
                .cast(Integer.class)
                .serialize();;


        source.doOnUnsubscribe(() -> System.out.println("Unsubscribed"))
                .unsafeSubscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("Completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e);
                    }

                    @Override
                    public void onNext(Integer t) {
                        System.out.println(t);
                    }
                });

    }
}
