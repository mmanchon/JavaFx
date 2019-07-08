package Interpreter.SymbolTable.Contexts;

import Interpreter.FileReader.Reader;
import Interpreter.Tokens.Type;

import java.util.Vector;

public class Context {

    private Vector conditionsList = new Vector();

    private Vector logicOperands = new Vector();

    private int lineNumber;

    private Type typeOfContext;

    public Context(){}

    public Context(int lineNumber, Type typeOfContext){
        this.lineNumber = lineNumber;
        this.typeOfContext = typeOfContext;
    }

    public Vector getConditionsList() {
        return conditionsList;
    }

    public void setConditionsList(Vector conditionsList) {
        this.conditionsList = conditionsList;
    }

    public Vector getLogicOperands() {
        return logicOperands;
    }

    public void setLogicOperands(Vector logicOperands) {
        this.logicOperands = logicOperands;
    }

    public Type getLogicOperand(int index){
        return (Type) this.logicOperands.get(index);
    }

    public Condition getCondition(int index){
        return (Condition) this.conditionsList.get(index);
    }

    public void addCondition(Condition condition){
        this.conditionsList.add(condition);
    }

    public void addOperand(Type type){
        this.logicOperands.add(type);
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Type getTypeOfContext() {
        return typeOfContext;
    }

    public void setTypeOfContext(Type typeOfContext) {
        this.typeOfContext = typeOfContext;
    }

    public Condition getLastCondition(){
        return (Condition)this.conditionsList.get(this.conditionsList.size()-1);
    }

    public void removeLast(){
        this.conditionsList.remove(this.conditionsList.size()-1);
    }
}
