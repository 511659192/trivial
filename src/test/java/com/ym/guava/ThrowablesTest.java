package com.ym.guava;

import com.google.common.base.Throwables;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by yangm on 2017/8/26.
 */
public class ThrowablesTest {
    @Test
    public void testThrowables(){
        try {
            throw new Exception();
        } catch (Throwable t) {
            String ss = Throwables.getStackTraceAsString(t);
            System.out.println("ss:"+ss);
            Throwables.propagate(t);
        }
    }

    @Test
    public void call() throws IOException {
        try {
            throw new IOException();
        } catch (Throwable t) {
            Throwables.propagateIfInstanceOf(t, IOException.class);
            throw Throwables.propagate(t);
        }
    }

    public Void testPropagateIfPossible() throws Exception {
        try {
            throw new Exception();
        } catch (Throwable t) {
            Throwables.propagateIfPossible(t, Exception.class);
            Throwables.propagate(t);
        }

        return null;
    }
}
