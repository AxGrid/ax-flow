package com.axgrid.flow.lbd;

import com.axgrid.flow.dto.AxFlowContext;

public interface AxFlowExceptionAction<C extends AxFlowContext> {
    void op(C context, Throwable e);
}
