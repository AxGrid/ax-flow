package com.axgrid.flow.lbd;

import com.axgrid.flow.dto.IFlowContext;

public interface FlowCheckAction<C extends IFlowContext> {
    boolean check(C context);
}
