# FLOW

## LIGHT FSM

### example

```java

enum States implements FlowStateEnum {
    INIT,
    READY
}

enum Events implements FlowEventEnum {
    tick,
    tok
}

final Flow<Context> axFlow = FlowBuilder.<Context>from(States.INIT)
    .on(States.INIT, state ->
        state ->
            state
                .execute((c) -> {
                    c.executeCount++;
                })
                .transition(Events.tok, States.READY)
    )
    .build();

```
