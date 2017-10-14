package com.ym.rxJava.user;

import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.jmock.internal.Formatting.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by cdyangmeng on 2017/10/12.
 */
public class FindUsersTest {

    private static final long[] EMPTY = new long[0];
    private UserDbAccessor mUserDbAccessor = new UserDbAccessor();
    private UserInfoApi mUserInfoApi = new UserInfoApi();

    Observable<List<User>> batchUserInfoUnordered(long[] ids) {
        Observable<Pair<List<User>, long[]>> cacheResult =
                Observable.defer(() -> {                                      // 1
                    List<User> cached = mUserDbAccessor.getIn(ids);           // 2
                    if (cached.size() == ids.length) {
                        return Observable.just(Pair.create(cached, EMPTY));   // 3
                    }
                    long[] missed = new long[ids.length - cached.size()];
                    long[] hits = extractids(cached);
                    Arrays.sort(hits);
                    int pos = 0;
                    for (long id : ids) {
                        if (Arrays.binarySearch(hits, id) < 0) {              // 4
                            missed[pos] = id;
                            pos++;
                        }
                    }
                    return Observable.just(Pair.create(cached, missed));
                });
        cacheResult.publish().autoConnect(2);
        Observable<List<User>> hits = cacheResult.map(pair -> pair.first);    // 5
        Observable<List<User>> missed = cacheResult
                .flatMap(pair -> {                                            // 6
                    if (pair.second == EMPTY) {                               // 7
                        return Observable.just(Collections.<User>emptyList());
                    }
                    return mUserInfoApi.multipleUserInfo(pair.second);        // 8
                })
                .doOnNext(mUserDbAccessor::put);                              // 9
        return Observable.zip(hits, missed, (local, remote) -> {              // 10
            List<User> merged = new ArrayList<>(local.size() + remote.size());// 11
            merged.addAll(local);
            merged.addAll(remote);
            return merged;
        });
    }

    private long[] extractids(List<User> cached) {
        return EMPTY;
    }


}
