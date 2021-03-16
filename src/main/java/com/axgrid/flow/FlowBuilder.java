package com.axgrid.flow;

import com.axgrid.flow.dto.FlowEventEnum;
import com.axgrid.flow.dto.FlowStateEnum;
import com.axgrid.flow.dto.IFlowContext;
import com.axgrid.flow.exception.FlowNullCheckException;
import com.axgrid.flow.exception.FlowTerminateException;
import com.axgrid.flow.lbd.FlowAction;
import com.axgrid.flow.lbd.FlowCheckAction;
import com.axgrid.flow.lbd.FlowStateAction;

public class FlowBuilder<C extends IFlowContext> {
    private final Flow<C> flow;

    @SuppressWarnings(value = "unchecked")
    public <C1 extends IFlowContext> Flow<C1> build() { return (Flow<C1>)this.flow; }

    public FlowBuilder<C> on(FlowStateEnum state, FlowStateAction<C> flowState) {
        if (state == null) throw new FlowNullCheckException("state");
        if (flowState == null) throw new FlowNullCheckException("flowState");
        flowState.op(new FlowStateBuilder<>(flow, state));
        return this;
    }

    public FlowBuilder<C> when(FlowStateEnum state, FlowAction<C> action) {
        if (state == null) throw new FlowNullCheckException("state");
        if (action == null) throw new FlowNullCheckException("action");
        flow.add(state, null, action);
        return this;
    }

    public FlowBuilder<C> when(FlowEventEnum event, FlowAction<C> action) {
        if (event == null) throw new FlowNullCheckException("event");
        if (action == null) throw new FlowNullCheckException("action");
        flow.addAll(event, action);
        return this;
    }

    public FlowBuilder<C> when(FlowStateEnum state, FlowEventEnum event, FlowAction<C> action) {
        if (state == null) throw new FlowNullCheckException("state");
        if (event == null) throw new FlowNullCheckException("event");
        if (action == null) throw new FlowNullCheckException("action");
        flow.add(state, event, action);
        return this;
    }

    public FlowBuilder<C> when(FlowAction<C> action) {
        if (action == null) throw new FlowNullCheckException("action");
        flow.addAll(null, action);
        return this;
    }

    public FlowBuilder<C> transition(FlowStateEnum state, FlowEventEnum event, FlowStateEnum to) {
        return transition(state, event, to, false);
    }

    public FlowBuilder<C> transition(FlowStateEnum state, FlowEventEnum event, FlowStateEnum to, boolean terminate) {
        if (state == null) throw new FlowNullCheckException("state");
        if (event == null) throw new FlowNullCheckException("event");
        if (to == null) throw new FlowNullCheckException("to");
        flow.add(state, event, (c) -> {
            c.setState(to);
            if (terminate) throw new FlowTerminateException();
        });
        return this;
    }

    public FlowBuilder<C> transition(FlowStateEnum state, FlowStateEnum to) {
        return transition(state, to, false);
    }
    public FlowBuilder<C> transition(FlowStateEnum state, FlowStateEnum to, boolean terminate) {
        if (state == null) throw new FlowNullCheckException("state");
        if (to == null) throw new FlowNullCheckException("to");
        flow.add(state, null, (c) -> {
            c.setState(to);
            if (terminate) throw new FlowTerminateException();
        });
        return this;
    }

    public FlowBuilder<C> transition(FlowEventEnum event, FlowStateEnum to) {
        return transition(event, to, false);
    }
    public FlowBuilder<C> transition(FlowEventEnum event, FlowStateEnum to, boolean terminate) {
        if (event == null) throw new FlowNullCheckException("event");
        if (to == null) throw new FlowNullCheckException("to");
        flow.addAll(event,  (c) -> {
            c.setState(to);
            if (terminate) throw new FlowTerminateException();
        });
        return this;
    }

    public FlowBuilder<C> transition(FlowEventEnum event, FlowCheckAction<C> check, FlowStateEnum to) {
        return transition(event, check, to, false);
    }
    public FlowBuilder<C> transition(FlowEventEnum event, FlowCheckAction<C> check, FlowStateEnum to, boolean terminate) {
        if (event == null) throw new FlowNullCheckException("event");
        if (check == null) throw new FlowNullCheckException("check");
        if (to == null) throw new FlowNullCheckException("to");
        flow.addAll(event, (c) -> {
            if (check.check(c)) {
                c.setState(to);
                if (terminate) throw new FlowTerminateException();
            }
        });
        return this;
    }


    public FlowBuilder<C> terminate(FlowStateEnum state, FlowEventEnum event) {
        this.flow.add(state, event, (c) -> { throw new FlowTerminateException(); });
        return this;
    }

    public FlowBuilder<C> terminate(FlowStateEnum state) {
        return this.terminate(state, null);
    }

    public FlowBuilder<C> terminate(FlowEventEnum event) {
        this.flow.addAll(event,  (c) -> { throw new FlowTerminateException(); });
        return this;
    }


    public FlowBuilder<C> execute(FlowAction<C> action) {
        flow.addAll(null, action);
        return this;
    }

    public static <C1 extends IFlowContext> FlowBuilder<C1> from(FlowStateEnum startState) {
        if (startState == null) throw new FlowNullCheckException("startState");
        return new FlowBuilder<C1>(startState);
    }

    private FlowBuilder(FlowStateEnum startState) {
        this.flow = new Flow<C>(startState);
    }

}
