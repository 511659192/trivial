package com.ym.jmx;

/**
 * Created by yangm on 2017/8/21.
 */
public interface HelloMBean {
    public String getName();

    public void setName(String name);

    public void printHello();

    public void printHello(String whoName);
}