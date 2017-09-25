package com.ym.rxJava;

import org.junit.Test;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;

/**
 * Created by yangm on 2017/9/1.
 */
public class Main {

    public static void main(String[] args) {
        Observable.range(1, 10).flatMap(v -> Observable.range(v, 2))
                .subscribe(System.out::println);
    }

    @Test
    public void test() {
        Observable.range(1, 10)
                .flatMap(v -> Observable.just(v).delay(11 - v, TimeUnit.SECONDS))
                .toBlocking()
                .subscribe(System.out::println);
    }

    @Test
    public void testSubject() {
        PublishSubject<Integer> subject = PublishSubject.create();
        subject.onNext(1);
        subject.subscribe(System.out::println);
        subject.onNext(2);
        subject.onNext(3);
        subject.onNext(4);
    }

    @Test
    public void testTakeAndFirst() throws Exception {
        Observable.range(8, 10)
                .take(5)
                .first()
                .subscribe(new PrintSubscriber("testTakeAndFirst"));
    }
}
