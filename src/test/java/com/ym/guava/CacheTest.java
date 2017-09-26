package com.ym.guava;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Ticker;
import com.google.common.cache.*;
import org.junit.Test;

import java.awt.print.Book;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangm on 2017/8/26.
 */
public class CacheTest {


    @Test
    public void testCache() throws InterruptedException {
        LoadingCache<String, String> cache = CacheBuilder.<String, String>newBuilder()
                .refreshAfterWrite(1, TimeUnit.SECONDS)
                .ticker(Ticker.systemTicker())
//                .expireAfterWrite(1, TimeUnit.SECONDS)
//                .removalListener(new RemovalListener<String, String>() {
//                    @Override
//                    public void onRemoval(RemovalNotification<String, String> notification) {
//                        System.out.println("onRemoval" + JSON.toJSONString(notification));
//                    }
//                })
                .maximumSize(1000)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) throws Exception {
                        System.out.println("feafafe");
                        return "121";
                    }
                });

        System.out.println(cache.getUnchecked("1"));
        TimeUnit.SECONDS.sleep(10);
        System.out.println(cache.getUnchecked("1"));
    }

    @Test
    public void cacheBuilderTest() {
        LoadingCache<String, TradeAccount> tradeAccountCache =
                CacheBuilder.newBuilder()
                        .expireAfterWrite(5L, TimeUnit.MINUTES)
                        .maximumSize(5000L)
                        .removalListener(new TradeAccountRemovalListener())
                        .ticker(Ticker.systemTicker())
                        .build(new CacheLoader<String, TradeAccount>() {
                            @Override
                            public TradeAccount load(String key) throws Exception {
                                return new TradeAccount();
                            }
                        });
    }

    @Test
    public void expireAfterAccessBuilderTest() {
        LoadingCache<String, TradeAccount> bookCache = CacheBuilder.newBuilder()
                .expireAfterAccess(20L, TimeUnit.MINUTES)
                .softValues()
                .removalListener(new TradeAccountRemovalListener())
                .build(new CacheLoader<String, TradeAccount>() {
                    @Override
                    public TradeAccount load(String key) throws Exception {
                        return new TradeAccount();
                    }
                });
    }

    @Test
    public void refreshAfterWriteTest() {
        LoadingCache<String, TradeAccount> tradeAccountCache =
                CacheBuilder.newBuilder()
                        .concurrencyLevel(10)
                        .refreshAfterWrite(5L, TimeUnit.SECONDS)
                        .ticker(Ticker.systemTicker())
                        .build(new CacheLoader<String, TradeAccount>() {
                            @Override
                            public TradeAccount load(String key) throws Exception {
                                return new TradeAccount();
                            }
                        });
    }

    public class TradeAccount {
        private String id; //ID
        private String owner; //所有者
        private double balance; //余额

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }
    }

    class TradeAccountRemovalListener implements RemovalListener<String, TradeAccount> {

        @Override
        public void onRemoval(RemovalNotification<String, TradeAccount> notification) {
            System.out.println(JSON.toJSONString(notification));
        }
    }
}
