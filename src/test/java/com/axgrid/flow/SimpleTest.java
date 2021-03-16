package com.axgrid.flow;

import com.axgrid.flow.dto.FlowEventEnum;
import com.axgrid.flow.dto.FlowStateEnum;
import com.axgrid.flow.dto.FlowStatefulContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class SimpleTest {

    enum States implements FlowStateEnum {
        INIT,
        READY,
        DEMO
    }

    enum Events implements FlowEventEnum {
        tick,
        tok,
    }

    @Test
    public void test() {
        FlowStatefulContext context = new FlowStatefulContext();
        Flow<FlowStatefulContext> flow = FlowBuilder.from(States.INIT)
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

        Assert.assertEquals(flow.actions.size(), 2);
        log.info("States:{}", flow.actions);

        Assert.assertNull(context.getState());
        Assert.assertNull(context.getLastEvent());

        flow.execute(context, Events.tick);

        Assert.assertEquals(context.getState(), States.READY);
        Assert.assertEquals(context.getLastEvent(), Events.tick);
        log.info("----");
        flow.execute(context, Events.tick);

        Assert.assertEquals(context.getState(), States.INIT);
        Assert.assertEquals(context.getLastEvent(), Events.tick);

        log.info("----");
        flow.execute(context, Events.tok);

    }

    @Test
    public void test2() {
        FlowStatefulContext context = new FlowStatefulContext();
        Flow<FlowStatefulContext> flow = FlowBuilder.from(States.INIT)
                .on(States.INIT, (state) ->
                       state.execute((c) -> {
                           c.setState(States.READY);
                       })
                )
                .on(States.DEMO, state ->
                    state.execute((c) -> log.info("TEST STATE"))
                            .when(null, (c) -> {
                                log.warn("EXECUTE !!!!");
                                c.setState(States.INIT);
                            })
                        .to(States.INIT)
                )
                .when(States.READY, Events.tick, (c) -> {
                    c.setState(States.INIT);
                })
                .build();

        flow.execute(context, Events.tick);
        Assert.assertEquals(context.getState(), States.READY);

    }
}
