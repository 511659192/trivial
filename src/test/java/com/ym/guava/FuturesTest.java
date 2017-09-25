package com.ym.guava;

import com.google.common.util.concurrent.*;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
/**
 * Created by yangm on 2017/8/27.
 */
public class FuturesTest {

    // 创建一个线程缓冲池Service
    ExecutorService executor = Executors.newCachedThreadPool();
    //创建一个ListeningExecutorService实例
    ListeningExecutorService executorService =
            MoreExecutors.listeningDecorator(executor);
    //提交一个可监听的线程
    ListenableFuture<String> listenableFuture = null;

    Person person = new Person("a", 19);

    @Test
    public void testFuturesTransform() throws ExecutionException, InterruptedException {
        listenableFuture = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "张三";
            }
        });
        AsyncFunction<String, Person> asyncFunction = new AsyncFunction<String, Person>() {
            @Override
            public ListenableFuture<Person> apply(String input) throws Exception {
                person.setName(input);
                return executorService.submit(new Callable<Person>() {
                    @Override
                    public Person call() throws Exception {
                        return person;
                    }
                });
            }
        };
        ListenableFuture<Person> lf =
                Futures.transform(listenableFuture, asyncFunction);
        assertThat(lf.get().getName(), is("张三"));
    }

    @Test
    public void testFuturesFallback() throws ExecutionException, InterruptedException {
        listenableFuture = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                throw new RuntimeException();
            }
        });
        FutureFallback<String> futureFallback = new FutureFallback<String>() {
            @Override
            public ListenableFuture create(Throwable t) throws Exception {
                if (t instanceof RuntimeException) {
                    SettableFuture<String> settableFuture =
                            SettableFuture.create();
                    settableFuture.set("Not Found");
                    return settableFuture;
                }
                throw new Exception(t);
            }
        };
        ListenableFuture<String> lf =
                Futures.withFallback(listenableFuture,futureFallback);
        assertThat(lf.get(), is("Not Found"));
    }

    static class Person {
        private String name;
        private Integer age;

        public Person(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Person person = (Person) o;

            if (name != null ? !name.equals(person.name) : person.name != null) return false;
            if (age != null ? !age.equals(person.age) : person.age != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (age != null ? age.hashCode() : 0);
            return result;
        }

    }
}
