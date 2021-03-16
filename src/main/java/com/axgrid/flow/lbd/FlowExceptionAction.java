package com.axgrid.flow.lbd;

import com.axgrid.flow.dto.IFlowContext;

public interface FlowExceptionAction<C extends IFlowContext> {
    void op(C context, Throwable e);
}
