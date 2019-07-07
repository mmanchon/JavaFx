package Interpreter.SymbolTable.Contexts;

import Interpreter.SymbolTable.Objects.Variable;

public class Loop extends Condition {

    private Object initialValue;
    private Object incrementValue;
    private Variable incrementVariable;

    public Loop(){}

    public Loop(Object initialValue, Object incrementValue) {
        this.initialValue = initialValue;
        this.incrementValue = incrementValue;
    }

    public Loop(String operand, Object rightSideCondition, Object leftSideCondition, Object initialValue, Object incrementValue) {
        super(operand, rightSideCondition, leftSideCondition);
        this.initialValue = initialValue;
        this.incrementValue = incrementValue;
    }

    public Object getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(Object initialValue) {
        this.initialValue = initialValue;
    }

    public Object getIncrementValue() {
        return incrementValue;
    }

    public void setIncrementValue(Object incrementValue) {
        this.incrementValue = incrementValue;
    }

    public Variable getIncrementVariable() {
        return incrementVariable;
    }

    public void setIncrementVariable(Variable incrementVariable) {
        this.incrementVariable = incrementVariable;
    }
}
