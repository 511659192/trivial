package com.ym.disruptor;

import com.lmax.disruptor.EventHandler;
import org.apache.log4j.pattern.LogEvent;

/**
 * Created by yangm on 2017/7/20.
 */
public class LongEventHandler implements EventHandler<LongEvent> {
    @Override
    public void onEvent(LongEvent longEvent, long l, boolean b) throws Exception {
        System.out.println(l);
        System.out.println(b);
        System.out.println(longEvent.hashCode());
    }
}
