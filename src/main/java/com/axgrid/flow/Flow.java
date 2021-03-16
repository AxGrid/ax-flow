package com.axgrid.flow;

import com.axgrid.flow.dto.FlowEventEnum;
import com.axgrid.flow.dto.FlowStateEnum;
import com.axgrid.flow.dto.IFlowContext;
import com.axgrid.flow.exception.FlowTerminateException;
import com.axgrid.flow.lbd.FlowAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class Flow<C extends IFlowContext> {

    final FlowStateEnum startState;

    final AtomicLong actionId = new AtomicLong();

    @SuppressWarnings(value = "unchecked")
    public static <C1 extends IFlowContext> FlowBuilder<C1> from(FlowStateEnum startState) {
        return (FlowBuilder<C1>)FlowBuilder.from(startState);
    }

    @Getter
    final Map<FlowStateEnum, Map<FlowEventEnum, List<ActionHolder>>> actions;

    Comparator<ActionHolder> comparator = Comparator.comparing((item) -> item.id);

    protected void add(FlowStateEnum state, FlowEventEnum event, FlowAction<C> action) {
        var stateActions = actions.get(state);
        if (!stateActions.containsKey(event)) stateActions.put(event, new ArrayList<>());
        stateActions.get(event).add(new ActionHolder(actionId.incrementAndGet(), action));
    }

    public void addAll(FlowEventEnum event, FlowAction<C> action) {
        var holder = new ActionHolder(actionId.incrementAndGet(), action);
        for(var eventMap : actions.values()) {
            if (!eventMap.containsKey(event)) eventMap.put(event, new ArrayList<>());
                eventMap.get(event).add(holder);
        }
    }

    private List<ActionHolder> getAllActions(FlowStateEnum state, FlowEventEnum event) {
        if (event == null) return actions.get(state).getOrDefault(null, Collections.emptyList());
        return Stream.concat(
                actions.get(state).getOrDefault(event, Collections.emptyList()).stream(),
                actions.get(state).getOrDefault(null, Collections.emptyList()).stream()
        )
                .sorted(comparator)
                .collect(Collectors.toList());

    }

    public void execute(C context, FlowEventEnum event) {
        if (context.getState() == null) context.setState(startState);
        context.setLastEvent(event);
        for(var executor : getAllActions(context.getState(), event)) {
            try {
                executor.action.op(context);
            }catch (FlowTerminateException terminateException) {
                break;
            }
        }
    }

    public Flow(FlowStateEnum startState){
        this.startState = startState;
        this.actions = Arrays.stream(startState.getClass().getEnumConstants())
                .collect(Collectors.toMap(item -> item, item -> new HashMap<>()));
    }


    @AllArgsConstructor
    class ActionHolder {
        final long id;
        final FlowAction<C> action;
    }
}