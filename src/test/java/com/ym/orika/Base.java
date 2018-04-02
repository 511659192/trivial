package com.ym.orika;

import java.util.List;

public class Base<T> {
    private List<T> list;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
