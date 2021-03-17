package com.axgrid.flow;

import com.axgrid.flow.dto.AxFlowEventEnum;
import com.axgrid.flow.dto.AxFlowStateEnum;
import com.axgrid.flow.dto.AxFlowContext;
import com.axgrid.flow.exception.AxFlowNullCheckException;
import com.axgrid.flow.exception.AxFlowTerminateException;
import com.axgrid.flow.lbd.AxFlowAction;
import com.axgrid.flow.lbd.AxFlowCheckAction;
import com.axgrid.flow.lbd.AxFlowExceptionAction;
import com.axgrid.flow.lbd.AxFlowStateAction;

public class AxFlowBuilder<C extends AxFlowContext> {
    private final AxFlow<C> axFlow;

    @SuppressWarnings(value = "unchecked")
    public <C1 extends AxFlowContext> AxFlow<C1> build() { return (AxFlow<C1>)this.axFlow; }

    public AxFlowBuilder<C> on(AxFlowStateEnum state, AxFlowStateAction<C> flowState) {
        if (state == null) throw new AxFlowNullCheckException("state");
        if (flowState == null) throw new AxFlowNullCheckException("flowState");
        flowState.op(new AxFlowStateBuilder<>(axFlow, state));
        return this;
    }

    public AxFlowBuilder<C> when(AxFlowStateEnum state, AxFlowAction<C> action) {
        if (state == null) throw new AxFlowNullCheckException("state");
        if (action == null) throw new AxFlowNullCheckException("action");
        axFlow.add(state, null, action);
        return this;
    }

    public AxFlowBuilder<C> when(AxFlowEventEnum event, AxFlowAction<C> action) {
        if (event == null) throw new AxFlowNullCheckException("event");
        if (action == null) throw new AxFlowNullCheckException("action");
        axFlow.addAll(event, action);
        return this;
    }

    public AxFlowBuilder<C> when(AxFlowStateEnum state, AxFlowEventEnum event, AxFlowAction<C> action) {
        if (state == null) throw new AxFlowNullCheckException("state");
        if (event == null) throw new AxFlowNullCheckException("event");
        if (action == null) throw new AxFlowNullCheckException("action");
        axFlow.add(state, event, action);
        return this;
    }

    public AxFlowBuilder<C> when(AxFlowAction<C> action) {
        if (action == null) throw new AxFlowNullCheckException("action");
        axFlow.addAll(null, action);
        return this;
    }

    public AxFlowBuilder<C> transition(AxFlowStateEnum state, AxFlowEventEnum event, AxFlowStateEnum to) {
        return transition(state, event, to, false);
    }

    public AxFlowBuilder<C> transition(AxFlowStateEnum state, AxFlowEventEnum event, AxFlowStateEnum to, boolean terminate) {
        if (state == null) throw new AxFlowNullCheckException("state");
        if (event == null) throw new AxFlowNullCheckException("event");
        if (to == null) throw new AxFlowNullCheckException("to");
        axFlow.add(state, event, (c) -> {
            c.setState(to);
            if (terminate) throw new AxFlowTerminateException();
        });
        return this;
    }

    public AxFlowBuilder<C> transition(AxFlowStateEnum state, AxFlowStateEnum to) {
        return transition(state, to, false);
    }
    public AxFlowBuilder<C> transition(AxFlowStateEnum state, AxFlowStateEnum to, boolean terminate) {
        if (state == null) throw new AxFlowNullCheckException("state");
        if (to == null) throw new AxFlowNullCheckException("to");
        axFlow.add(state, null, (c) -> {
            c.setState(to);
            if (terminate) throw new AxFlowTerminateException();
        });
        return this;
    }

    public AxFlowBuilder<C> transition(AxFlowEventEnum event, AxFlowStateEnum to) {
        return transition(event, to, false);
    }
    public AxFlowBuilder<C> transition(AxFlowEventEnum event, AxFlowStateEnum to, boolean terminate) {
        if (event == null) throw new AxFlowNullCheckException("event");
        if (to == null) throw new AxFlowNullCheckException("to");
        axFlow.addAll(event,  (c) -> {
            c.setState(to);
            if (terminate) throw new AxFlowTerminateException();
        });
        return this;
    }

    public AxFlowBuilder<C> transition(AxFlowEventEnum event, AxFlowCheckAction<C> check, AxFlowStateEnum to) {
        return transition(event, check, to, false);
    }
    public AxFlowBuilder<C> transition(AxFlowEventEnum event, AxFlowCheckAction<C> check, AxFlowStateEnum to, boolean terminate) {
        if (event == null) throw new AxFlowNullCheckException("event");
        if (check == null) throw new AxFlowNullCheckException("check");
        if (to == null) throw new AxFlowNullCheckException("to");
        axFlow.addAll(event, (c) -> {
            if (check.check(c)) {
                c.setState(to);
                if (terminate) throw new AxFlowTerminateException();
            }
        });
        return this;
    }


    public AxFlowBuilder<C> terminate(AxFlowStateEnum state, AxFlowEventEnum event) {
        this.axFlow.add(state, event, (c) -> { throw new AxFlowTerminateException(); });
        return this;
    }

    public AxFlowBuilder<C> terminate(AxFlowStateEnum state) {
        return this.terminate(state, null);
    }

    public AxFlowBuilder<C> terminate(AxFlowEventEnum event) {
        this.axFlow.addAll(event,  (c) -> { throw new AxFlowTerminateException(); });
        return this;
    }


    public AxFlowBuilder<C> execute(AxFlowAction<C> action) {
        axFlow.addAll(null, action);
        return this;
    }

    public AxFlowBuilder<C> exception(Class<? extends Throwable> throwable, AxFlowStateEnum toState) { return exception(throwable, toState, false); }
    public AxFlowBuilder<C> exception(Class<? extends Throwable> throwable, AxFlowStateEnum toState, boolean terminate) {
        axFlow.addException(null, throwable, (c) -> {
            c.setState(toState);
            if (terminate) throw new AxFlowTerminateException();
        });
        return this;
    }



    public AxFlowBuilder<C> exception(AxFlowStateEnum toState) { return exception(toState, false); }
    public AxFlowBuilder<C> exception(AxFlowStateEnum toState, boolean terminate) {
        axFlow.addException(null, null, (c) -> {
            c.setState(toState);
            if (terminate) throw new AxFlowTerminateException();
        });
        return this;
    }

    public AxFlowBuilder<C> exception(AxFlowAction<C> action) {
        axFlow.addException(null, null, action);
        return this;
    }

    public AxFlowBuilder<C> exception(AxFlowExceptionAction<C> action) {
        axFlow.addException(null, null, action);
        return this;
    }

    public AxFlowBuilder<C> exception(Class<? extends Throwable> throwable, AxFlowExceptionAction<C> action) {
        axFlow.addException(null, throwable, action);
        return this;
    }

    public AxFlowBuilder<C> exception(Class<? extends Throwable> throwable, AxFlowAction<C> action) {
        axFlow.addException(null, throwable, action);
        return this;
    }

    public static <C1 extends AxFlowContext> AxFlowBuilder<C1> from(AxFlowStateEnum startState) {
        if (startState == null) throw new AxFlowNullCheckException("startState");
        return new AxFlowBuilder<C1>(startState);
    }

    private AxFlowBuilder(AxFlowStateEnum startState) {
        this.axFlow = new AxFlow<C>(startState);
    }

}
