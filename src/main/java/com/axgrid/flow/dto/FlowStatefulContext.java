package com.axgrid.flow.dto;

import com.axgrid.flow.Flow;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class FlowStatefulContext implements IFlowContext, Serializable  {

    private Flow<? extends IFlowContext> flow;
    private FlowStateEnum state;
    private FlowStateEnum previousState;
    private FlowEventEnum lastEvent;
    private long previousExecutionTime;
    private long executionTime = new Date().getTime();

    public void setState(FlowStateEnum state) {
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
