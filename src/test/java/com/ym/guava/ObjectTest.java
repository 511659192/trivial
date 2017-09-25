package com.ym.guava;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import org.junit.Test;

import java.util.Comparator;

/**
 * Created by yangm on 2017/8/26.
 */
public class ObjectTest {

    @Test
    public void StudentTest(){

        Student student=new Student("peida",23,80);
        Student student1=new Student("aida",23,36);
        Student student2=new Student("jerry",24,90);
        Student student3=new Student("peida",23,80);

        System.out.println("==========equals===========");
        System.out.println(student.equals(student2));//false
        System.out.println(student.equals(student1));//false
        System.out.println(student.equals(student3));//true

        System.out.println("==========hashCode===========");
        System.out.println(student.hashCode());//-991998617
        System.out.println(student1.hashCode());//92809683
        System.out.println(student3.hashCode());//-991998617
        System.out.println(student2.hashCode());//-1163491205

        System.out.println("==========toString===========");
        System.out.println(student.toString());//Student{peida, 23, 80}
        System.out.println(student1.toString());//Student{aida, 23, 36}
        System.out.println(student2.toString());//Student{jerry, 24, 90}
        System.out.println(student3.toString());//Student{peida, 23, 80}

        System.out.println("==========compareTo===========");
        System.out.println(student.compareTo(student1));//1
        System.out.println(student.compareTo(student2));//1
        System.out.println(student2.compareTo(student1));//1
        System.out.println(student2.compareTo(student));//-1

    }

    class Student implements Comparable<Student>{
        public String name;
        public int age;
        public int score;

        Student(String name, int age,int score) {
            this.name = name;
            this.age = age;
            this.score=score;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, age);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Student) {
                Student that = (Student) obj;
                return Objects.equal(name, that.name)
                        && Objects.equal(age, that.age)
                        && Objects.equal(score, that.score);
            }
            return false;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .addValue(name)
                    .addValue(age)
                    .addValue(score)
                    .toString();
        }

        @Override
        public int compareTo(Student other) {
            return ComparisonChain.start()
                    .compare(name, other.name)
                    .compare(age, other.age)
                    .compare(score, other.score, Ordering.natural().nullsLast())
                    .result();
        }
    }

    class StudentComparator implements Comparator<Student> {
        @Override public int compare(Student s1, Student s2) {
            return ComparisonChain.start()
                    .compare(s1.name, s2.name)
                    .compare(s1.age, s2.age)
                    .compare(s1.score, s2.score)
                    .result();
        }
    }
}


