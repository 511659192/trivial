package com.jd.biz.service.util.executor;

/**
 * @Author yangmeng44
 * @Date 2017/7/21
 */
public interface Constant<T extends Constant<T>> extends Comparable<T> {

    /**
     * Returns the unique number assigned to this {@link Constant}.
     */
    int id();

    /**
     * Returns the name of this {@link Constant}.
     */
    String name();
}
