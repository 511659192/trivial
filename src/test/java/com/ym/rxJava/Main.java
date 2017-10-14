package com.ym.rxJava;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.util.ActionSubscriber;
import rx.schedulers.TimeInterval;
import rx.schedulers.Timestamped;
import rx.subjects.PublishSubject;

/**
 * @Author yangmeng44
 * @Date 2017/9/12
 */
public class Main {

    @Test
    public void testConcat() {
        Observable.concat(Observable.just(1, 2), Observable.just(3, 4))
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.println(integer);
                    }
                });
    }

    @Test
    public void testCompose() {
        Observable.just(1, 2)
                .compose(new LiftAllTransformer())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.println(integer);
                    }
                });
    }

    public class LiftAllTransformer implements Observable.Transformer<Integer, Integer> {
        @Override
        public Observable<Integer> call(Observable<Integer> observable) {
            return observable.just(4, 5);
        }
    }

    @Test
    public void testMap() {
        Observable.just(1, 2)
                .map(i -> String.valueOf(i))
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println(s);
                    }
                });
    }


    @Test
    public void test() {
        Observable.just(1, 2)
                .doOnNext(v -> {
                    System.out.println("doOnNext " + v);
                })
                .doOnRequest(v -> {
                    System.out.println("doOnRequest " + v);
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.println(integer);
                    }
                });
    }

    @Test
    public void testFlatMap() {
        Observable.just(1, 10)
                .flatMap(o -> {
                    return Observable.range(o, 2);
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.println(integer);
                    }
                });
    }

    @Test
    public void testTime() {
        Observable.just(1)
                .timeInterval()
                .subscribe(new Action1<TimeInterval<Integer>>() {
                    @Override
                    public void call(TimeInterval<Integer> integerTimeInterval) {
                        System.out.println(JSON.toJSONString(integerTimeInterval));
                    }
                });

//        Observable.just(1)
//                .timeInterval()
//                .timestamp()
//                .subscribe(new Action1<Timestamped<TimeInterval<Integer>>>() {
//                    @Override
//                    public void call(Timestamped<TimeInterval<Integer>> timeIntervalTimestamped) {
//                        System.out.println(timeIntervalTimestamped);
//                    }
//                });

        System.out.println("------------------------------");
//        Observable.just(1)
//                .timestamp()
//                .timeInterval()
//                .subscribe(new Action1<TimeInterval<Timestamped<Integer>>>() {
//                    @Override
//                    public void call(TimeInterval<Timestamped<Integer>> timestampedTimeInterval) {
//                        System.out.println(timestampedTimeInterval);
//                    }
//                });
    }

    @Test
    public void testJustDuc() {
        Observable.just(Observable.just(1, 2), Observable.just(10, 20))
                .subscribe(new Action1<Observable<Integer>>() {
                    @Override
                    public void call(Observable<Integer> integerObservable) {
                        System.out.println(integerObservable);
                    }
                });


    }

    @Test
    public void testAA() {
        Observable.just(1, 2, 3)
                .lift(new Observable.Operator<String, Integer>() {
                    @Override
                    public Subscriber<? super Integer> call(Subscriber<? super String> subscriber) {
                        return new Subscriber<Integer>() {
                            @Override
                            public void onCompleted() {
                                if (subscriber.isUnsubscribed()) return;
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (subscriber.isUnsubscribed()) return;
                                subscriber.onError(e);
                            }

                            @Override
                            public void onNext(Integer integer) {
                                if (subscriber.isUnsubscribed()) return;
                                subscriber.onNext(integer.toString());
                                subscriber.onNext("88");
                                subscriber.onError(new RuntimeException("exception"));
                                subscriber.onNext("99");
                            }
                        };
                    }
                })
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String string) {
                        System.out.println("string " + string);
//                        throw new RuntimeException("---------------------");
                        return Integer.valueOf(string);
                    }
                })
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {

                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("onNext " + integer);
                    }
                });


    }
}
