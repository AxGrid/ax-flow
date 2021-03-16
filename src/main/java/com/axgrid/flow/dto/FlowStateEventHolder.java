package com.axgrid.flow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class FlowStateEventHolder {
    FlowEventEnum event;
    FlowStateEnum state;
}
