package com.axgrid.flow.dto;

import com.axgrid.flow.Flow;

public interface IFlowContext {

    Flow<? extends IFlowContext> getFlow();
    void setFlow(Flow<? extends IFlowContext> flow);
    FlowStateEnum getState();
    void setState(FlowStateEnum state);

    void setLastEvent(FlowEventEnum lastEvent);
    FlowEventEnum getLastEvent();

    void setExecutionTime(long time);
    long getPreviousExecutionTime();
    long getDeltaTime();

    FlowStateEnum getPreviousState();
}
