package com.axgrid.flow.lbd;

import com.axgrid.flow.dto.AxFlowContext;

public interface AxFlowAction<C extends AxFlowContext> {
    void op(C context);
}
