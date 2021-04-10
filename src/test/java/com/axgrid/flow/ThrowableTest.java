package com.axgrid.flow;

import com.axgrid.flow.dto.AxFlowEventEnum;
import com.axgrid.flow.dto.AxFlowStateEnum;
import com.axgrid.flow.dto.AxFlowStatefulContext;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;

public class ThrowableTest {

    enum States implements AxFlowStateEnum {
        INIT,
        READY,
        ERROR
    }

    enum Events implements AxFlowEventEnum {
        tick,
        tok,
        error
    }

    public class MyException extends RuntimeException {
    }

    @Test
    public void testThrowable() {

        Class<? extends Throwable> t = MyException.class;
        Class<? extends Throwable> t1 = Exception.class;
        Class<? extends Throwable> t2 = RuntimeException.class;
        try {
            throw new MyException();
        } catch (Exception e) {
            Assert.assertTrue(t.isAssignableFrom(e.getClass()));
            Assert.assertTrue(t1.isAssignableFrom(e.getClass()));
            Assert.assertTrue(t2.isAssignableFrom(e.getClass()));
        }

        try {
            throw new RuntimeException();
        } catch (Exception e) {
            Assert.assertFalse(t.isAssignableFrom(e.getClass()));
            Assert.assertTrue(t1.isAssignableFrom(e.getClass()));
            Assert.assertTrue(t2.isAssignableFrom(e.getClass()));
        }
    }


    @Test
    public void testFrsThrowable() {

        AxFlow<AxFlowStatefulContext> axFlow = AxFlow.<AxFlowStatefulContext>from(States.INIT)
                .on(States.INIT, state ->
                        state
                                .transition(Events.tick, States.READY)
                )
                .on(States.READY, state ->
                        state
                                .when(Events.error, (c) -> {
                                    throw new MyException();
                                })
                                .when(Events.tok, (c) -> {
                                    throw new RuntimeException();
                                })
                )
                .on(States.ERROR, state ->
                        state
                                .execute((c) -> c.setState(States.INIT))
                )

                .exception(MyException.class, States.ERROR, true)
                .exception(Exception.class, States.INIT)
                .build();

        AxFlowStatefulContext ctx = new AxFlowStatefulContext();
        axFlow.execute(ctx, Events.tok);
        Assert.assertEquals(ctx.getState(), States.INIT);
        axFlow.execute(ctx, Events.tick);
        Assert.assertEquals(ctx.getState(), States.READY);
        axFlow.execute(ctx, Events.error);
        Assert.assertEquals(ctx.getState(), States.ERROR);
        axFlow.execute(ctx, Events.tick);
        Assert.assertEquals(ctx.getState(), States.INIT);
        axFlow.execute(ctx, Events.tick);
        Assert.assertEquals(ctx.getState(), States.READY);
        axFlow.execute(ctx, Events.tok);
        Assert.assertEquals(ctx.getState(), States.INIT);
    }


    @Test
    public void testCycleThrowable() {

        final AtomicLong errorCount = new AtomicLong();
        AxFlow<AxFlowStatefulContext> axFlow = AxFlow.<AxFlowStatefulContext>from(States.INIT)
                .on(States.INIT, state ->
                        state
                                .transition(Events.tick, States.READY)
                )
                .on(States.READY, state ->
                        state
                                .when(Events.error, (c) -> {
                                    throw new MyException();
                                })
                                .when(Events.tok, (c) -> {
                                    throw new RuntimeException();
                                })
                )
                .on(States.ERROR, state ->
                        state
                                .execute((c) -> c.setState(States.INIT))
                )

                .exception((c, e) ->{
                    System.out.println("Exception:" + e.toString());
                    errorCount.incrementAndGet();
                })
                .exception(MyException.class, States.ERROR, true)
                .build();
        AxFlowStatefulContext ctx = new AxFlowStatefulContext();

        axFlow.execute(ctx, Events.tick);
        Assert.assertEquals(errorCount.get(), 0);
        axFlow.execute(ctx, Events.error);
        Assert.assertEquals(errorCount.get(), 1);

    }
}
