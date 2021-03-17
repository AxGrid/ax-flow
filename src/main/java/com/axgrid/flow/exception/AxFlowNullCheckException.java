package com.axgrid.flow.exception;

public class AxFlowNullCheckException extends AxFlowException {
    public AxFlowNullCheckException() {super("Argument is null");}
    public AxFlowNullCheckException(String name) {super(String.format("Argument %s is null",name));}
}
