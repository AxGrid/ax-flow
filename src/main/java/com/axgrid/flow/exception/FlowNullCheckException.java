package com.axgrid.flow.exception;

public class FlowNullCheckException extends FlowException{
    public FlowNullCheckException() {super("Argument is null");}
    public FlowNullCheckException(String name) {super(String.format("Argument %s is null",name));}
}
