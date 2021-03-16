package com.axgrid.flow;

import com.axgrid.flow.dto.FlowEventEnum;
import com.axgrid.flow.dto.FlowStateEnum;
import com.axgrid.flow.dto.FlowStatefulContext;
import org.junit.Assert;
import org.junit.Test;

public class ThrowableTest {

    enum States implements FlowStateEnum {
        INIT,
        READY,
        ERROR
    }

    enum Events implements FlowEventEnum {
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

        Flow<FlowStatefulContext> flow = Flow.<FlowStatefulContext>from(States.INIT)
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

        FlowStatefulContext ctx = new FlowStatefulContext();
        flow.execute(ctx, Events.tok);
        Assert.assertEquals(ctx.getState(), States.INIT);
        flow.execute(ctx, Events.tick);
        Assert.assertEquals(ctx.getState(), States.READY);
        flow.execute(ctx, Events.error);
        Assert.assertEquals(ctx.getState(), States.ERROR);
        flow.execute(ctx, Events.tick);
        Assert.assertEquals(ctx.getState(), States.INIT);
        flow.execute(ctx, Events.tick);
        Assert.assertEquals(ctx.getState(), States.READY);
        flow.execute(ctx, Events.tok);
        Assert.assertEquals(ctx.getState(), States.INIT);


    }

}
