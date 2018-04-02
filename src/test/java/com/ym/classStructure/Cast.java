package com.ym.classStructure;

public class Cast {

    public static void main(String[] args) {
        Parent parent = (Parent) new Parent();
    }
}

class Parent {
}

class Child extends Parent {}


