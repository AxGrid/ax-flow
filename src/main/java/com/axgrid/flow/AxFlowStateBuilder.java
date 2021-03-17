package com.axgrid.flow;

import com.axgrid.flow.dto.AxFlowEventEnum;
import com.axgrid.flow.dto.AxFlowStateEnum;
import com.axgrid.flow.dto.AxFlowContext;
import com.axgrid.flow.exception.AxFlowNullCheckException;
import com.axgrid.flow.exception.AxFlowTerminateException;
import com.axgrid.flow.lbd.AxFlowAction;
import com.axgrid.flow.lbd.AxFlowCheckAction;
import com.axgrid.flow.lbd.AxFlowExceptionAction;

public class AxFlowStateBuilder<C extends AxFlowContext> {

    final AxFlowStateEnum state;
    final AxFlow<C> axFlow;




    public AxFlowStateBuilder<C> transition(AxFlowEventEnum event, AxFlowStateEnum toState) {
        return transition(event, toState, false);
    }

    public AxFlowStateBuilder<C> transition(AxFlowEventEnum event, AxFlowStateEnum toState, boolean terminate) {
        if (toState == null) throw new AxFlowNullCheckException("toState");
        axFlow.add(state, event, (c) -> {
            c.setState(toState);
            if (terminate) throw new AxFlowTerminateException();
        });
        return this;
    }

    public AxFlowStateBuilder<C> transition(AxFlowCheckAction<C> check, AxFlowStateEnum toState) {
        return transition(check, toState, false);
    }

    public AxFlowStateBuilder<C> transition(AxFlowCheckAction<C> check, AxFlowStateEnum toState, boolean terminate) {
        return transition(null, check, toState, terminate);
    }

    public AxFlowStateBuilder<C> transition(AxFlowEventEnum event, AxFlowCheckAction<C> check, AxFlowStateEnum toState) {
        return transition(event, check, toState, false);
    }
    public AxFlowStateBuilder<C> transition(AxFlowEventEnum event, AxFlowCheckAction<C> check, AxFlowStateEnum toState, boolean terminate) {
        if (toState == null) throw new AxFlowNullCheckException("toState");
        if (check == null) throw new AxFlowNullCheckException("check");

        axFlow.add(state, event, (c) -> {
            if (check.check(c)) {
                c.setState(toState);
                if (terminate) throw new AxFlowTerminateException();
            }
        });
        return this;
    }

    public AxFlowStateBuilder<C> when(AxFlowEventEnum event, AxFlowAction<C> execute) {
        return this.when(event, execute, false);
    }

    public AxFlowStateBuilder<C> when(AxFlowEventEnum event, AxFlowAction<C> execute, boolean terminate) {
        if (execute == null) throw new AxFlowNullCheckException("execute");
        axFlow.add(state, event, (c) -> {
            execute.op(c);
            if (terminate) throw new AxFlowTerminateException();
        });
        return this;
    }

    public AxFlowStateBuilder<C> when(AxFlowAction<C> execute) {
        return when(null, execute, false);
    }

    public AxFlowStateBuilder<C> execute(AxFlowAction<C> action) {
        return when(action);
    }

    public AxFlowStateBuilder<C> terminate(AxFlowEventEnum event) {
        axFlow.add(state, event, (c) -> { throw new AxFlowTerminateException(); });
        return this;
    }

    public AxFlowStateBuilder<C> terminate() {
        return terminate(null);
    }

    public AxFlowStateBuilder<C> to(AxFlowStateEnum toState) {
        return this.execute((c) -> c.setState(toState));
    }

    public AxFlowStateBuilder<C> exception(Class<? extends Throwable> throwable, AxFlowStateEnum toState) { return exception(throwable, toState, false); }
    public AxFlowStateBuilder<C> exception(Class<? extends Throwable> throwable, AxFlowStateEnum toState, boolean terminate) {
        axFlow.addException(state, throwable, (c) -> {
            c.setState(toState);
            if (terminate) throw new AxFlowTerminateException();
        });
        return this;
    }


    public AxFlowStateBuilder<C> exception(AxFlowStateEnum toState) { return exception(toState, false); }
    public AxFlowStateBuilder<C> exception(AxFlowStateEnum toState, boolean terminate) {
        axFlow.addException(state, null, (c) -> {
            c.setState(toState);
            if (terminate) throw new AxFlowTerminateException();
        });
        return this;
    }

    public AxFlowStateBuilder<C> exception(AxFlowAction<C> action) {
        axFlow.addException(state, null, action);
        return this;
    }

    public AxFlowStateBuilder<C> exception(AxFlowExceptionAction<C> action) {
        axFlow.addException(state, null, action);
        return this;
    }


    public AxFlowStateBuilder<C> exception(Class<? extends Throwable> throwable, AxFlowExceptionAction<C> action) {
        axFlow.addException(state, throwable, action);
        return this;
    }

    public AxFlowStateBuilder<C> exception(Class<? extends Throwable> throwable, AxFlowAction<C> action) {
        axFlow.addException(state, throwable, action);
        return this;
    }

    public AxFlowStateBuilder(AxFlow<C> axFlow, AxFlowStateEnum state) {
        this.axFlow = axFlow;
        this.state = state;
    }
}
