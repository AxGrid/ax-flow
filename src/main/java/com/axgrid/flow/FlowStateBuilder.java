package com.axgrid.flow;

import com.axgrid.flow.dto.FlowEventEnum;
import com.axgrid.flow.dto.FlowStateEnum;
import com.axgrid.flow.dto.IFlowContext;
import com.axgrid.flow.exception.FlowNullCheckException;
import com.axgrid.flow.exception.FlowTerminateException;
import com.axgrid.flow.lbd.FlowAction;
import com.axgrid.flow.lbd.FlowCheckAction;
import com.axgrid.flow.lbd.FlowExceptionAction;

public class FlowStateBuilder<C extends IFlowContext> {

    final FlowStateEnum state;
    final Flow<C> flow;




    public FlowStateBuilder<C> transition(FlowEventEnum event, FlowStateEnum toState) {
        return transition(event, toState, false);
    }

    public FlowStateBuilder<C> transition(FlowEventEnum event, FlowStateEnum toState, boolean terminate) {
        if (toState == null) throw new FlowNullCheckException("toState");
        flow.add(state, event, (c) -> {
            c.setState(toState);
            if (terminate) throw new FlowTerminateException();
        });
        return this;
    }

    public FlowStateBuilder<C> transition(FlowCheckAction<C> check, FlowStateEnum toState) {
        return transition(check, toState, false);
    }

    public FlowStateBuilder<C> transition(FlowCheckAction<C> check, FlowStateEnum toState,  boolean terminate) {
        return transition(null, check, toState, terminate);
    }

    public FlowStateBuilder<C> transition(FlowEventEnum event, FlowCheckAction<C> check, FlowStateEnum toState) {
        return transition(event, check, toState, false);
    }
    public FlowStateBuilder<C> transition(FlowEventEnum event, FlowCheckAction<C> check, FlowStateEnum toState, boolean terminate) {
        if (toState == null) throw new FlowNullCheckException("toState");
        if (check == null) throw new FlowNullCheckException("check");

        flow.add(state, event, (c) -> {
            if (check.check(c)) {
                c.setState(toState);
                if (terminate) throw new FlowTerminateException();
            }
        });
        return this;
    }

    public FlowStateBuilder<C> when(FlowEventEnum event, FlowAction<C> execute) {
        return this.when(event, execute, false);
    }

    public FlowStateBuilder<C> when(FlowEventEnum event, FlowAction<C> execute, boolean terminate) {
        if (execute == null) throw new FlowNullCheckException("execute");
        flow.add(state, event, (c) -> {
            execute.op(c);
            if (terminate) throw new FlowTerminateException();
        });
        return this;
    }

    public FlowStateBuilder<C> when(FlowAction<C> execute) {
        return when(null, execute, false);
    }

    public FlowStateBuilder<C> execute(FlowAction<C> action) {
        return when(action);
    }

    public FlowStateBuilder<C> terminate(FlowEventEnum event) {
        flow.add(state, event, (c) -> { throw new FlowTerminateException(); });
        return this;
    }

    public FlowStateBuilder<C> terminate() {
        return terminate(null);
    }

    public FlowStateBuilder<C> to(FlowStateEnum state) {
       flow.add(state, null, (c) -> c.setState(state));
       return this;
    }

    public FlowStateBuilder<C> exception(Class<? extends Throwable> throwable, FlowStateEnum toState) { return exception(throwable, toState, false); }
    public FlowStateBuilder<C> exception(Class<? extends Throwable> throwable, FlowStateEnum toState, boolean terminate) {
        flow.addException(state, throwable, (c) -> {
            c.setState(toState);
            if (terminate) throw new FlowTerminateException();
        });
        return this;
    }


    public FlowStateBuilder<C> exception(FlowStateEnum toState) { return exception(toState, false); }
    public FlowStateBuilder<C> exception(FlowStateEnum toState, boolean terminate) {
        flow.addException(state, null, (c) -> {
            c.setState(toState);
            if (terminate) throw new FlowTerminateException();
        });
        return this;
    }

    public FlowStateBuilder<C> exception(FlowAction<C> action) {
        flow.addException(state, null, action);
        return this;
    }

    public FlowStateBuilder<C> exception(FlowExceptionAction<C> action) {
        flow.addException(state, null, action);
        return this;
    }


    public FlowStateBuilder<C> exception(Class<? extends Throwable> throwable, FlowExceptionAction<C> action) {
        flow.addException(state, throwable, action);
        return this;
    }

    public FlowStateBuilder<C> exception(Class<? extends Throwable> throwable, FlowAction<C> action) {
        flow.addException(state, throwable, action);
        return this;
    }

    public FlowStateBuilder(Flow<C> flow, FlowStateEnum state) {
        this.flow = flow;
        this.state = state;
    }
}
