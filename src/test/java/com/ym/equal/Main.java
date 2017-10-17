package com.ym.equal;

import com.google.common.base.Objects;
import org.jctools.queues.MpscArrayQueue;
import org.jctools.queues.SpmcArrayQueue;
import rx.internal.util.unsafe.MpscLinkedQueue;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangm on 2017/7/24.
 */
public class Main {


    public static void main(String[] args) {
        SpmcArrayQueue<Integer> queue = new SpmcArrayQueue<>(2);
        queue.offer(1);
        queue.offer(2);
        queue.offer(3);
        System.out.println(queue.poll());

        System.out.println(queue.poll());
        System.out.println(queue.poll());
    }


}
class Person {
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