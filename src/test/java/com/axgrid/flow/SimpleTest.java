package com.axgrid.flow;

import com.axgrid.flow.dto.AxFlowEventEnum;
import com.axgrid.flow.dto.AxFlowStateEnum;
import com.axgrid.flow.dto.AxFlowStatefulContext;
import org.junit.Assert;
import org.junit.Test;

public class SimpleTest {

    enum States implements AxFlowStateEnum {
        INIT,
        READY,
        DEMO
    }

    enum Events implements AxFlowEventEnum {
        tick,
        tok,
    }

    @Test
    public void test() {
        AxFlowStatefulContext context = new AxFlowStatefulContext();
        AxFlow<AxFlowStatefulContext> axFlow = AxFlowBuilder.from(States.INIT)
                .on(States.INIT, (state) ->
                    state.transition(Events.tick, States.READY) // Перейти
                          .when(Events.tick, (c) -> { }) // Определенное событие
                          .when((c) -> { }) // Каждое событие
                )
                .when(Events.tick, (c) -> {
                    System.out.println("Context:" +c.toString());
                })
                .when(States.INIT, Events.tick, (c) -> {
                    System.out.println("I.Context:" +c.toString());
                    //c.setState(States.READY);
                })
                .when(States.READY, Events.tick, (c) -> {
                    System.out.println("R.Context:" +c.toString());
                    c.setState(States.INIT);
                })
                .when(States.READY, (c) -> {
                    System.out.println("All ready .Context:" +c.toString());
                })
                .build();


        Assert.assertEquals(axFlow.actions.size(), 3);


        Assert.assertNull(context.getState());
        Assert.assertNull(context.getLastEvent());

        axFlow.execute(context, Events.tick);

        Assert.assertEquals(context.getState(), States.READY);
        Assert.assertEquals(context.getLastEvent(), Events.tick);

        axFlow.execute(context, Events.tick);

        Assert.assertEquals(context.getState(), States.INIT);
        Assert.assertEquals(context.getLastEvent(), Events.tick);

        axFlow.execute(context, Events.tok);

    }

    @Test
    public void test2() {
        AxFlowStatefulContext context = new AxFlowStatefulContext();
        AxFlow<AxFlowStatefulContext> axFlow = AxFlowBuilder.from(States.INIT)
                .on(States.INIT, (state) ->
                       state.execute((c) -> {
                           c.setState(States.READY);
                       })
                )
                .on(States.DEMO, state ->
                    state.execute((c) -> System.out.println("TEST STATE"))
                            .when(null, (c) -> {
                                System.out.println("EXECUTE !!!!");
                                c.setState(States.INIT);
                            })
                        .to(States.INIT)
                )
                .when(States.READY, Events.tick, (c) -> {
                    c.setState(States.INIT);
                })
                .build();

        axFlow.execute(context, Events.tick);
        Assert.assertEquals(context.getState(), States.READY);

    }

}
