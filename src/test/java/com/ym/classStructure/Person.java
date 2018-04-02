package com.ym.classStructure;

public class Person extends Base {

    public static void main(String[] args) {
        System.out.println("11");
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void cast() {
        int i = 1;
        int j = i;
        Integer k = i;
        Integer l = k;
        i = k;
        String aa = "123";
        String bb = String.valueOf("123");
        Base base = (Base) new Person();
    }
}
