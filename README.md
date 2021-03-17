# FLOW

## LIGHT FSM


### install
```xml
<dependency>
    <groupId>com.axgrid.flow</groupId>
    <artifactId>ax-flow</artifactId>
    <version>${axflow.version}</version>
</dependency>
```


### example
```java

enum States implements AxFlowStateEnum {
    INIT,
    READY
}

enum Events implements AxFlowEventEnum {
    tick,
    tok
}

AxFlow<Context> flow = AxFlow.<Context>from(States.INIT)
    .on(States.INIT, state ->
        state ->
            state
                .execute((c) -> {
                    c.executeCount++;
                })
                .transition(Events.tok, States.READY)
    )
    .build();

flow.execute(new Context(), Events.tick);
```

### Create context

```java
import com.axgrid.flow.dto.AxFlowStatefulContext;

public class MyFlowContext extends AxFlowStatefulContext {
    int myIntVariable;
}

// OR

public class MyFlowContext implements AxFlowContext {
    int myIntVariable;
}

```

### Create flow

```java
public class MyController {
    final AxFlow<Context> flow = AxFlow.<Context>from(States.INIT)
            .build();
}
```

### Methods

* transition

```
flow.transition(event, to-state, [terminate])
flow.transition(from-state, to-state, [terminate])
flow.transition(from-state, event, to-state, [terminate])
flow.transition(from-state, (checkContext) -> { return true; }, to-state, [terminate])
```


* when

```
flow.when((context) -> { actions; }) // Copy of flow.execute(...)
flow.when(state, (context) -> { actions; })
flow.when(event, (context) -> { actions; })
flow.when(state, event, (context) -> { actions; })
```

* exception

```
flow.exception((context) -> { ... })
flow.exception((context, throwable) -> { ... })
flow.exception(throwable, (context) -> { ... })
flow.exception(throwable, (context, throwable) -> { ... })
flow.exception(to-state, [terminate])
flow.exception(throwable, to-state, [terminate])
```


* on (state bracers)

```
flow.on(state, statebuilder -> { ... })
flow.on(state, statebuilder -> 
    statebuilder
        .transition(event, to-state, [terminate])
        .when(event, (context) -> { ... })
        .to(to-state)
        .exception(throwable, (context) -> { ... });
)
```
