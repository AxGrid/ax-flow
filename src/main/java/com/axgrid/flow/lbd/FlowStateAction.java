package com.axgrid.flow.lbd;

import com.axgrid.flow.FlowStateBuilder;
import com.axgrid.flow.dto.IFlowContext;

public interface FlowStateAction<C extends IFlowContext> {
     void op(FlowStateBuilder<C> state);
}
