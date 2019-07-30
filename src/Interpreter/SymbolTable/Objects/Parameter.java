package Interpreter.SymbolTable.Objects;

import Interpreter.SymbolTable.Types.ITypes;

public class Parameter extends Variable {

    private boolean reference;

    private String variable;

    public Parameter(){}

    public Parameter(boolean reference, String variable) {
        this.reference = reference;
        this.variable = variable;
    }

    public Parameter(String name, ITypes type, boolean reference, String variable) {
        super(name, type);
        this.reference = reference;
        this.variable = variable;
    }

    public Parameter(Variable variable, boolean reference){
        super(variable.getName(),variable.getType());
        this.reference = reference;
        this.variable = variable.getName();
    }

    public boolean isReference() {
        return reference;
    }

    public void setReference(boolean reference) {
        this.reference = reference;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }
}
