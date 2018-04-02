package com.ym.orika;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class BasicPerson {

    private String name;
    private int age;
    private Date birthDate;
    private List<String> nameList;
    private Map<String, String> nameMap;
    private Name nameOfName;

    public Name getNameOfName() {
        return nameOfName;
    }

    public void setNameOfName(Name nameOfName) {
        this.nameOfName = nameOfName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public List<String> getNameList() {
        return nameList;
    }

    public void setNameList(List<String> nameList) {
        this.nameList = nameList;
    }

    public Map<String, String> getNameMap() {
        return nameMap;
    }

    public void setNameMap(Map<String, String> nameMap) {
        this.nameMap = nameMap;
    }
}
