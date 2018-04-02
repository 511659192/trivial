package com.ym.orika;

import java.util.Date;

public class BasicPersonDto {

    private String fullName;
    private int currentAge;
    private Date birthDate;
    private String firstNameFromList;
    private String lastNameFromList;
    private String firstNameFromMap;
    private String lastNameFromMap;
    private String firstName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getCurrentAge() {
        return currentAge;
    }

    public void setCurrentAge(int currentAge) {
        this.currentAge = currentAge;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getFirstNameFromList() {
        return firstNameFromList;
    }

    public void setFirstNameFromList(String firstNameFromList) {
        this.firstNameFromList = firstNameFromList;
    }

    public String getLastNameFromList() {
        return lastNameFromList;
    }

    public void setLastNameFromList(String lastNameFromList) {
        this.lastNameFromList = lastNameFromList;
    }

    public String getFirstNameFromMap() {
        return firstNameFromMap;
    }

    public void setFirstNameFromMap(String firstNameFromMap) {
        this.firstNameFromMap = firstNameFromMap;
    }

    public String getLastNameFromMap() {
        return lastNameFromMap;
    }

    public void setLastNameFromMap(String lastNameFromMap) {
        this.lastNameFromMap = lastNameFromMap;
    }
}
