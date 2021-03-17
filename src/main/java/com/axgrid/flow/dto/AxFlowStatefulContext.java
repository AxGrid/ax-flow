package com.axgrid.flow.dto;

import com.axgrid.flow.AxFlow;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AxFlowStatefulContext implements AxFlowContext, Serializable  {

    private AxFlow<? extends AxFlowContext> flow;
    private AxFlowStateEnum state;
    private AxFlowStateEnum previousState;
    private AxFlowEventEnum lastEvent;
    private long previousExecutionTime;
    private long executionTime = new Date().getTime();

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
