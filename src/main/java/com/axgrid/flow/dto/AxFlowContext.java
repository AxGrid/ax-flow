package com.axgrid.flow.dto;

import com.axgrid.flow.AxFlow;

public interface AxFlowContext {

    AxFlow<? extends AxFlowContext> getFlow();
    void setFlow(AxFlow<? extends AxFlowContext> flow);
    AxFlowStateEnum getState();
    void setState(AxFlowStateEnum state);

    void setLastEvent(AxFlowEventEnum lastEvent);
    AxFlowEventEnum getLastEvent();

    void setExecutionTime(long time);
    long getPreviousExecutionTime();
    long getDeltaTime();

    AxFlowStateEnum getPreviousState();
}
