package com.axgrid.flow;

import com.axgrid.flow.dto.AxFlowEventEnum;
import com.axgrid.flow.dto.AxFlowStateEnum;
import com.axgrid.flow.dto.AxFlowStatefulContext;
import com.axgrid.flow.exception.AxFlowTerminateException;
import org.junit.Assert;
import org.junit.Test;

public class AxAxFlowActionsTest {

    enum States implements AxFlowStateEnum {
        INIT,
        READY,
        WAIT
    }

    enum Events implements AxFlowEventEnum {
        tick,
        cancel,
    }

    public static class ContextAx extends AxFlowStatefulContext {
        int executeCount = 0;
    }


    @Test
    public void test() {
        ContextAx ctx = new ContextAx();

        AxFlow<ContextAx> axFlow = AxFlowBuilder.<ContextAx>from(States.INIT)
                .when(Events.cancel, (c) -> {
                    if (c.executeCount == 0) throw new AxFlowTerminateException();
                })
                .execute((c) -> {
                    c.executeCount++;
                })
                .on(States.INIT, state ->
                        state.execute((c) -> {
                            c.executeCount++;
                        }))
                .build();

        axFlow.execute(ctx, Events.tick);
        Assert.assertEquals(ctx.executeCount, 2);
        ctx = new ContextAx();
        axFlow.execute(ctx, Events.cancel);
        Assert.assertEquals(ctx.executeCount, 0);
    }

}
