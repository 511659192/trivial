package com.ym.netty.executor;

import java.util.EventListener;

/**
 * @Author yangmeng44
 * @Date 2017/7/21
 */
public interface GenericFutureListener<F extends Future<?>> extends EventListener {

    void operationComplete(F future) throws Exception;
}
