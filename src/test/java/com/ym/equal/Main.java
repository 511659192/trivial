package com.ym.equal;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangm on 2017/7/24.
 */
public class Main {


    public static void main(String[] args) {
        Person p1 = new Person("name", 11);
        Person p2 = new Person("name", 11);
        System.out.println(p1.equals(p2));
        System.out.println(p1 == p2);

        Map<Person, String> map = new HashMap<>();
        map.put(p1, "aa");
        System.out.println(map.get(p2));
        Person p3 = new Person("name", 12);
        System.out.println(map.get(p3));
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