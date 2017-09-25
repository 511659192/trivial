package com.ym.rxJava;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.io.*;

/**
 * Created by yangm on 2017/9/1.
 */
public class DiskCache implements ICache{

    private static final String NAME = ".db";
    public static long OTHER_CACHE_TIME = 10 * 60 * 1000;
    public static long WIFI_CACHE_TIME = 30 * 60 * 1000;
    File fileDir;
    public DiskCache() {
        fileDir = new File("");
    }

    @Override
    public <T> Observable<T> get(final String key, final Class<T> cls) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {

                T t = null;

                if (subscriber.isUnsubscribed()) {
                    return;
                }

                if (t == null) {
                    subscriber.onNext(null);
                } else {
                    subscriber.onNext(t);
                }

                subscriber.onCompleted();
            }
        });

    }

    @Override
    public <T> void put(final String key, final T t) {
        Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {

                boolean isSuccess = isSave(key + NAME, t);

                if (!subscriber.isUnsubscribed() && isSuccess) {

                    subscriber.onNext(t);
                    subscriber.onCompleted();
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    /**
     * 保存数据
     */
    private <T> boolean isSave(String fileName, T t) {
        File file = new File(fileDir, fileName);

        ObjectOutputStream objectOut = null;
        boolean isSuccess = false;
        try {
            FileOutputStream out = new FileOutputStream(file);
            objectOut = new ObjectOutputStream(out);
            objectOut.writeObject(t);
            objectOut.flush();
            isSuccess=true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            closeSilently(objectOut);
        }
        return isSuccess;
    }

    /**
     * 获取保存的数据
     */
    private Object getDiskData1(String fileName) {
        File file = new File(fileDir, fileName);

        if (isCacheDataFailure(file)) {
            return null;
        }

        if (!file.exists()) {
            return null;
        }
        Object o = null;
        ObjectInputStream read = null;
        try {
            read = new ObjectInputStream(new FileInputStream(file));
            o = read.readObject();
            o = "afeafe";
        } catch (StreamCorruptedException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            closeSilently(read);
        }
        return o;
    }



    private void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }



    /**
     * 判断缓存是否已经失效
     */
    private boolean isCacheDataFailure(File dataFile) {
        if (!dataFile.exists()) {
            return false;
        }
        long existTime = System.currentTimeMillis() - dataFile.lastModified();
        boolean failure = false;
        failure = existTime > OTHER_CACHE_TIME ? true : false;
        return failure;
    }

    public void clearDisk(String key) {
        File file = new File(fileDir, key + NAME);
        if (file.exists()) file.delete();
    }
}