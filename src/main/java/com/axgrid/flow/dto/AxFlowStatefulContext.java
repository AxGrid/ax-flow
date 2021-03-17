package com.axgrid.flow.dto;

import com.axgrid.flow.AxFlow;

import java.io.Serializable;
import java.util.Date;


public class AxFlowStatefulContext implements AxFlowContext, Serializable  {

    private AxFlow<? extends AxFlowContext> flow;
    private AxFlowStateEnum state;
    private AxFlowStateEnum previousState;
    private AxFlowEventEnum lastEvent;
    private long previousExecutionTime;
    private long executionTime = new Date().getTime();


    @Override
    public AxFlow<? extends AxFlowContext> getFlow() { return flow; }

    @Override
    public void setFlow(AxFlow<? extends AxFlowContext> flow) { this.flow = flow; }

    @Override
    public AxFlowStateEnum getState() { return state; }

    @Override
    public AxFlowStateEnum getPreviousState() { return previousState; }

    @Override
    public AxFlowEventEnum getLastEvent() { return lastEvent; }

    @Override
    public void setLastEvent(AxFlowEventEnum lastEvent) { this.lastEvent = lastEvent; }

    @Override
    public long getPreviousExecutionTime() { return previousExecutionTime; }

    public long getExecutionTime() { return executionTime; }


    @Override
    public void setState(AxFlowStateEnum state) {
        this.previousState = this.state;
        this.state = state;
    }

    @Override
    public void setExecutionTime(long time) {
        this.previousExecutionTime = this.executionTime;
        this.executionTime = time;
    }

    @Override
    public long getDeltaTime() {
        return executionTime - previousExecutionTime;
    }

}
