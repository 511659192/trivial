package com.ym.rxJava;


import org.junit.Test;
import rx.Observable;

/**
 * Created by yangm on 2017/9/6.
 */
public class ExtTest {
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
}
