package com.ym.guava;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Ticker;
import com.google.common.cache.*;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

/**
 * Created by yangm on 2017/8/27.
 */
public class CacheBuilderSpecTest {

    @Test
    public void main() throws ExecutionException {
        //配置CacheBuilder的字符串
        String spec = "concurrencyLevel=10,expireAfterAccess=5m,softValues";
        //解析字符串，创建CacheBuilderSpec实例
        CacheBuilderSpec cacheBuilderSpec = CacheBuilderSpec.parse(spec);
        //通过CacheBuilderSpec实例构造CacheBuilder实例
        CacheBuilder cacheBuilder = CacheBuilder.from(cacheBuilderSpec);
        //ticker：设置缓存条目过期时间
        //removalListener：监听缓存条目的移除
        LoadingCache cache = cacheBuilder.ticker(Ticker.systemTicker())
                .removalListener(new TradeAccountRemovalListener())
                .build(new CacheLoader<String, TradeAccount>() {
                    @Override
                    public TradeAccount load(String key) throws
                            Exception {
                        return new TradeAccount();
                    }
                });
        System.out.println(cache.get("11"));
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

    class TradeAccountRemovalListener implements RemovalListener<String, CacheTest.TradeAccount> {

        @Override
        public void onRemoval(RemovalNotification<String, CacheTest.TradeAccount> notification) {
            System.out.println(JSON.toJSONString(notification));
        }
    }
}
