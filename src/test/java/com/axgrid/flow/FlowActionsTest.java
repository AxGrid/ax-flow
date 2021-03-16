package com.axgrid.flow;

import com.axgrid.flow.dto.FlowEventEnum;
import com.axgrid.flow.dto.FlowStateEnum;
import com.axgrid.flow.dto.FlowStatefulContext;
import com.axgrid.flow.exception.FlowTerminateException;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.plaf.nimbus.State;

public class FlowActionsTest {

    enum States implements FlowStateEnum {
        INIT,
        READY,
        WAIT
    }

    enum Events implements FlowEventEnum {
        tick,
        cancel,
    }

    public static class Context extends FlowStatefulContext {
        int executeCount = 0;
    }


    @Test
    public void test() {
        Context ctx = new Context();

        Flow<Context> flow = FlowBuilder.<Context>from(States.INIT)
                .when(Events.cancel, (c) -> {
                    if (c.executeCount == 0) throw new FlowTerminateException();
                })
                .execute((c) -> {
                    c.executeCount++;
                })
                .on(States.INIT, state ->
                        state.execute((c) -> {
                            c.executeCount++;
                        }))
                .build();

        flow.execute(ctx, Events.tick);
        Assert.assertEquals(ctx.executeCount, 2);
        ctx = new Context();
        flow.execute(ctx, Events.cancel);
        Assert.assertEquals(ctx.executeCount, 0);
    }

}
