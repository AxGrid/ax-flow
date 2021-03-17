package com.axgrid.flow.lbd;

import com.axgrid.flow.dto.AxFlowContext;

public interface AxFlowCheckAction<C extends AxFlowContext> {
    boolean check(C context);
}
