package com.axgrid.flow.lbd;

import com.axgrid.flow.dto.IFlowContext;

public interface FlowAction<C extends IFlowContext> {
    void op(C context);
}
