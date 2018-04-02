package com.ym.orika;

import java.util.List;
import java.util.Map;

public class PersonDto {
    private Map<String, Name> personalNames;
    private String[] firstNames;
    private List<String> lastNames;

    public Map<String, Name> getPersonalNames() {
        return personalNames;
    }

    public void setPersonalNames(Map<String, Name> personalNames) {
        this.personalNames = personalNames;
    }

    public String[] getFirstNames() {
        return firstNames;
    }

    public void setFirstNames(String[] firstNames) {
        this.firstNames = firstNames;
    }

    public List<String> getLastNames() {
        return lastNames;
    }

    public void setLastNames(List<String> lastNames) {
        this.lastNames = lastNames;
    }
}
