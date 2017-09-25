package com.ym.rxJava;

import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;
import rx.subjects.ReplaySubject;

/**
 * Created by yangm on 2017/9/4.
 */
public class SideEffectTest {

    class Inc {
        private int count = 0;
        public void inc() {
            count++;
        }
        public int getCount() {
            return count;
        }
    }

    @Test
    public void testInc() throws Exception {
        Inc index = new Inc();
        Observable<String> values = Observable.just("请", "不要", "有", "副作用")
                .map(w -> {
                    index.inc();
                    return w;
                });
        values.subscribe(w -> System.out.println("1st observer: " + index.getCount() + ": " + w));
        values.subscribe(w -> System.out.println("2nd observer: " + index.getCount() + ": " + w));

    }

    class Indexed <T> {
        public final int index;
        public final T item;
        public Indexed(int index, T item) {
            this.index = index;
            this.item = item;
        }
    }

    @Test
    public void testIndexed() throws Exception {
        Observable<Indexed<String>> indexed = Observable.just("No", "side", "effects", "please")
                .scan(
                        new Indexed<String>(0, null),
                        (prev,v) -> new Indexed<String>(prev.index+1, v))
//                .skip(1)
                ;

        indexed.subscribe(w -> System.out.println("1st observer: " + w.index + ": " + w.item));
        indexed.subscribe(w -> System.out.println("2nd observer: " + w.index + ": " + w.item));
    }

    @Test
    public void testDoOnEach() throws Exception {
        Observable<String> values = Observable.just("side", "effects");

        values
                .doOnEach(new PrintSubscriber("Log"))
                .map(s -> s)
                .subscribe(new PrintSubscriber("Process"));
    }

    static Observable<String> service() {
        return  Observable.just("First", "Second", "Third")
                .doOnEach(new PrintSubscriber("Log")); //                 .filter(s -> s.length() > 5)

    }

    @Test
    public void testDoStaticOnEach() throws Exception {
        service()
                .map(s -> s.toUpperCase())
                .filter(s -> s.length() > 5)
                .subscribe(new PrintSubscriber("Process"));
    }

    @Test
    public void testDoOnNext() throws Exception {
        Observable<String> values = Observable.just("First", "Second", "Third")
                .doOnNext(d -> System.out.println(d));
        values.map(s -> s.toUpperCase())
                .filter(s -> s.length() > 5)
                .subscribe(new PrintSubscriber("Process"));
    }

    @Test
    public void testDoOnSubscribe() throws Exception {
        ReplaySubject<Integer> subject = ReplaySubject.create();
        Observable<Integer> values = subject
                .doOnSubscribe(() -> System.out.println("New subscription"))
                .doOnUnsubscribe(() -> System.out.println("Subscription over"));

        Subscription s1 = values.subscribe(new PrintSubscriber("1st"));
        subject.onNext(0);
        Subscription s2 = values.subscribe(new PrintSubscriber("2st"));
        subject.onNext(1);
        s1.unsubscribe();
        subject.onNext(2);
        subject.onNext(3);
        subject.onCompleted();
    }

    class BrakeableService {
        private final BehaviorSubject<String> items = BehaviorSubject.create("Greet");

        public Observable<String> getValues() {
            return items.asObservable();
        }

        public void play() {
            items.onNext("Hello");
            items.onNext("and");
            items.onNext("goodbye");
        }
    }

    class Data {
        public int id;
        public String name;
        public Data(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Test
    public void testShare() throws Exception {
        Observable<Data> data = Observable.just(
                new Data(1, "Microsoft"),
                new Data(2, "Netflix")
        );

        data.subscribe(d -> d.name = "Garbage");
        data.subscribe(d -> System.out.println(d.id + ": " + d.name));
    }
}
