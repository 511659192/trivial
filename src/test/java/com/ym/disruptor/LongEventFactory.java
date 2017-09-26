package com.ym.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * Created by yangm on 2017/7/20.
 */
public class LongEventFactory implements EventFactory {
    @Override
    public Object newInstance() {
        return new LongEvent();
    }
}
