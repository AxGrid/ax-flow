package com.axgrid.flow;

import com.axgrid.flow.dto.AxFlowEventEnum;
import com.axgrid.flow.dto.AxFlowStateEnum;
import com.axgrid.flow.dto.AxFlowContext;
import com.axgrid.flow.exception.AxFlowTerminateException;
import com.axgrid.flow.lbd.AxFlowAction;
import com.axgrid.flow.lbd.AxFlowExceptionAction;


import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class AxFlow<C extends AxFlowContext> {

    final AxFlowStateEnum startState;

    final AtomicLong actionId = new AtomicLong();

    @SuppressWarnings(value = "unchecked")
    public static <C1 extends AxFlowContext> AxFlowBuilder<C1> from(AxFlowStateEnum startState) {
        return (AxFlowBuilder<C1>) AxFlowBuilder.from(startState);
    }


    final Map<AxFlowStateEnum, Map<AxFlowEventEnum, List<ActionHolder>>> actions;

    public Map<AxFlowStateEnum, Map<AxFlowEventEnum, List<ActionHolder>>> getActions() { return actions; }

    final Map<AxFlowStateEnum, List<ExceptionHolder>> exceptions;

    public Map<AxFlowStateEnum, List<ExceptionHolder>> getExceptions() { return exceptions; }

    Comparator<ActionHolder> comparator = Comparator.comparing((item) -> item.id);

    protected void add(AxFlowStateEnum state, AxFlowEventEnum event, AxFlowAction<C> action) {
        var stateActions = actions.get(state);
        if (!stateActions.containsKey(event)) stateActions.put(event, new ArrayList<>());
        stateActions.get(event).add(new ActionHolder(actionId.incrementAndGet(), action));
    }

    protected void addException(AxFlowStateEnum state, Class<? extends Throwable> throwable, AxFlowAction<C> action) {
        if (throwable == null) throwable = Exception.class;
        var holder = new ExceptionHolder(throwable, action);
        if (state == null)
            this.exceptions.values().forEach(item -> item.add(holder));
        else
            this.exceptions.get(state).add(holder);
    }

    protected void addException(AxFlowStateEnum state, Class<? extends Throwable> throwable, AxFlowExceptionAction<C> action) {
        if (throwable == null) throwable = Exception.class;
        var holder = new ExceptionHolder(throwable, action);
        if (state == null)
            this.exceptions.values().forEach(item -> item.add(holder));
        else
            this.exceptions.get(state).add(holder);
    }

    public void addAll(AxFlowEventEnum event, AxFlowAction<C> action) {
        var holder = new ActionHolder(actionId.incrementAndGet(), action);
        for(var eventMap : actions.values()) {
            if (!eventMap.containsKey(event)) eventMap.put(event, new ArrayList<>());
                eventMap.get(event).add(holder);
        }
    }

    private List<ActionHolder> getAllActions(AxFlowStateEnum state, AxFlowEventEnum event) {
        if (event == null) return actions.get(state).getOrDefault(null, Collections.emptyList());
        return Stream.concat(
                actions.get(state).getOrDefault(event, Collections.emptyList()).stream(),
                actions.get(state).getOrDefault(null, Collections.emptyList()).stream()
        )
                .sorted(comparator)
                .collect(Collectors.toList());

    }

    public void execute(C context, AxFlowEventEnum event) {
        if (context.getState() == null) context.setState(startState);
        context.setLastEvent(event);
        for(var executor : getAllActions(context.getState(), event)) {
            try {
                executor.action.op(context);
            }catch (AxFlowTerminateException terminateException) {
                break;
            }catch (Exception e) {
                except(context, e);
            }
        }
    }

    private void except(C context, Exception e) {
        for(var eh : exceptions.get(context.getState())) {
            if (eh.throwable.isAssignableFrom(e.getClass())) {
                try {
                    if (eh.action != null) eh.action.op(context);
                    if (eh.exceptionAction != null) eh.exceptionAction.op(context, e);
                }catch (AxFlowTerminateException ignore) {
                    break;
                }
            }
        }
    }

    public AxFlow(AxFlowStateEnum startState){
        this.startState = startState;
        this.actions = Arrays.stream(startState.getClass().getEnumConstants())
                .collect(Collectors.toMap(item -> item, item -> new HashMap<>()));
        this.exceptions = Arrays.stream(startState.getClass().getEnumConstants())
                .collect(Collectors.toMap(item -> item, item -> new ArrayList<>()));
    }


    class ActionHolder {
        final long id;
        final AxFlowAction<C> action;
        public ActionHolder(long id, AxFlowAction<C> action) {
            this.id = id;
            this.action = action;
        }
    }


    class ExceptionHolder {
        final Class<? extends Throwable> throwable;
        final AxFlowAction<C> action;
        final AxFlowExceptionAction<C> exceptionAction;

        public ExceptionHolder(Class<? extends Throwable> throwable, AxFlowAction<C> action) {
            this.throwable = throwable;
            this.action = action;
            this.exceptionAction = null;
        }

        public ExceptionHolder(Class<? extends Throwable> throwable, AxFlowExceptionAction<C> action) {
            this.throwable = throwable;
            this.action = null;
            this.exceptionAction = action;
        }
    }
}
