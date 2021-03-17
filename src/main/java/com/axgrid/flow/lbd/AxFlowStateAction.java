package com.axgrid.flow.lbd;

import com.axgrid.flow.AxFlowStateBuilder;
import com.axgrid.flow.dto.AxFlowContext;

public interface AxFlowStateAction<C extends AxFlowContext> {
     void op(AxFlowStateBuilder<C> state);
}
