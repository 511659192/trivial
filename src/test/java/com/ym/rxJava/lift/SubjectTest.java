package com.ym.rxJava.lift;

import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.observers.Observers;
import rx.observers.SerializedObserver;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by cdyangmeng on 2017/10/19.
 */
public class SubjectTest {


    static class ReactiveList<T> {
        enum ChangeType {
            ADD, REMOVE, UPDATE                      // (1)
        };

        final List<T> list = new ArrayList<>();      // (2)

        final PublishSubject<ChangeType> changes = PublishSubject.create();             // (3)
        final BehaviorSubject<T> changeValues = BehaviorSubject.create();
        final ReplaySubject<T> latestAdded = ReplaySubject.createWithSize(10);

        public Observable<ChangeType> changes() {    // (4)
            return changes;
        }

        public Observable<T> changeValues() {
            return changeValues;
        }

        public Observable<T> latestAdded() {
            return latestAdded;
        }

        public void add(T value) {
            list.add(value);
            changes.onNext(ChangeType.ADD);
            changeValues.onNext(value);
            latestAdded.onNext(value);
        }
        public void remove(T value) {
            if (list.remove(value)) {
                changes.onNext(ChangeType.REMOVE);
                changeValues.onNext(value);
            }
        }
        public void replace(T value, T newValue) {
            int index = list.indexOf(value);
            if (index >= 0) {
                list.set(index, newValue);
                changes.onNext(ChangeType.UPDATE);
                changeValues.onNext(newValue);
            }
        }
    }

    static class MoreReactiveList<T> {
        enum ChangeType {
            ADD, REMOVE
        };

        final List<T> list = new ArrayList<>();

        final Subject<ChangeType, ChangeType> changes;  // (1)
        final Subject<T, T> changeValues;
        final Observer<T> addObserver;                  // (2)
        final Observer<T> removeObserver;

        public MoreReactiveList() {
            changes = PublishSubject.<ChangeType>create().toSerialized();                     // (1)
            changeValues = BehaviorSubject.<T>create().toSerialized();

            addObserver = new SerializedObserver<>(      // (2)
                    Observers.create(this::onAdd,
                            t -> {
                                clear();
                                changes.onError(t);
                                changeValues.onError(t);
                            },
                            () -> {
                                clear();
                                changes.onCompleted();
                                changeValues.onCompleted();
                            }
                    ));
            removeObserver = new SerializedObserver<>(   // (3)
                    Observers.create(this::onRemove,
                            t -> {
                                clear();
                                changes.onError(t);
                                changeValues.onError(t);
                            },
                            () -> {
                                clear();
                                changes.onCompleted();
                                changeValues.onCompleted();
                            }
                    ));
        }

        public Observable<ChangeType> changes() {
            return changes;
        }

        public Observable<T> changeValues() {
            return changeValues;
        }

        public Observable<T> list() {                   // (3)
            List<T> copy = new ArrayList<>();
            synchronized (list) {
                copy.addAll(list);
            }
            return Observable.from(copy);
        }

        public Observer<T> adder() {                    // (4)
            return addObserver;
        }

        public Observer<T> remover() {
            return removeObserver;
        }

        void onAdd(T value) {                           // (5)
            synchronized (list) {
                list.add(value);
            }
            changes.onNext(ChangeType.ADD);
            changeValues.onNext(value);
        }

        void onRemove(T value) {
            synchronized (list) {
                if (!list.remove(value)) {
                    System.out.println("onRemove fail " + value + " list:" + list);
                    return;
                }
            }
            System.out.println("onRemove success " + value);
            changes.onNext(ChangeType.REMOVE);
            changeValues.onNext(value);
        }

        void clear() {
            synchronized (list) {
                list.clear();
            }
        }
    }

    @Test
    public void testMoreReactiveList() throws Exception {
        MoreReactiveList<Long> list = new MoreReactiveList<>();

        Observable.timer(0, 1, TimeUnit.SECONDS)
                .take(10)
                .subscribe(list.adder());

        Observable.timer(4, 1, TimeUnit.SECONDS)
                .take(10)
                .subscribe(list.remover());

//        list.changes().subscribe(System.out::println);

        
        list.changes()
                .flatMap(e -> list.list().toList())
                .subscribe(System.out::println);

//        list.changeValues.toBlocking().forEach(System.out::println);
        System.in.read();
    }

}
