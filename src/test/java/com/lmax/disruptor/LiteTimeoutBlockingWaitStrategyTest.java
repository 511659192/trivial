package com.lmax.disruptor;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(JMock.class)
public class LiteTimeoutBlockingWaitStrategyTest
{
    private final Mockery mockery = new Mockery();

    @Test
    public void shouldTimeoutWaitFor() throws Exception
    {
        final SequenceBarrier sequenceBarrier = mockery.mock(SequenceBarrier.class);

        long theTimeout = 500;
        LiteTimeoutBlockingWaitStrategy waitStrategy = new LiteTimeoutBlockingWaitStrategy(theTimeout, TimeUnit.MILLISECONDS);
        Sequence cursor = new Sequence(5);
        Sequence dependent = cursor;

        mockery.checking(
            new Expectations()
            {
                {
                    allowing(sequenceBarrier).checkAlert();
                }
            });

        long t0 = System.currentTimeMillis();

        try
        {
            waitStrategy.waitFor(6, cursor, dependent, sequenceBarrier);
            fail("TimeoutException should have been thrown");
        }
        catch (TimeoutException e)
        {
        }

        long t1 = System.currentTimeMillis();

        long timeWaiting = t1 - t0;

        assertThat(timeWaiting, greaterThanOrEqualTo(theTimeout));
    }
}
