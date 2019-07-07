package Interpreter.SymbolTable.Contexts;

import Interpreter.SymbolTable.Objects.Variable;

public class Condition {

    private Object leftSideCondition;

    private Object rightSideCondition;

    private String operand;

    public Condition(){}

    public Condition(String operand, Object rightSideCondition, Object leftSideCondition){
        this.operand = operand;
        this.rightSideCondition = rightSideCondition;
        this.leftSideCondition = leftSideCondition;
    }

    public Object getLeftSideCondition() {
        return leftSideCondition;
    }

    public void setLeftSideCondition(Object leftSideCondition) {
        this.leftSideCondition = leftSideCondition;
    }

    public Object getRightSideCondition() {
        return rightSideCondition;
    }

    public void setRightSideCondition(Object rightSideCondition) {
        this.rightSideCondition = rightSideCondition;
    }

    public String getOperand() {
        return operand;
    }

    public void setOperand(String operand) {
        this.operand = operand;
    }

    public boolean analyseOperand(){
        int leftValue, rightValue;

        if(this.leftSideCondition instanceof Variable){
            leftValue = Integer.valueOf(((Variable)this.leftSideCondition).getType().getValue().toString());
        }else{
            leftValue = Integer.valueOf(this.leftSideCondition.toString());
        }

        if(this.rightSideCondition instanceof Variable){
            rightValue = Integer.valueOf(((Variable)this.rightSideCondition).getType().getValue().toString());
        }else{
            rightValue = Integer.valueOf(this.rightSideCondition.toString());
        }

        if(this.operand.equals("==")){
            return (leftValue == rightValue);
        }else if(this.operand.equals("!=")){
            return (leftValue != rightValue);
        }else if(this.operand.equals(">=")){
            return (leftValue >= rightValue);
        }else if(this.operand.equals("<=")){
            return (leftValue <= rightValue);
        }else if(this.operand.equals("<")){
            return (leftValue < rightValue);
        }else if(this.operand.equals(">")){
            return (leftValue > rightValue);
        }

        return false;
    }
}
