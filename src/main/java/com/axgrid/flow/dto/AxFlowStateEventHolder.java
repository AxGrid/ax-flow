package com.axgrid.flow.dto;


public class AxFlowStateEventHolder {
    AxFlowEventEnum event;
    AxFlowStateEnum state;

    public AxFlowStateEventHolder(AxFlowEventEnum event, AxFlowStateEnum state) {
        this.event = event;
        this.state = state;
    }
}
