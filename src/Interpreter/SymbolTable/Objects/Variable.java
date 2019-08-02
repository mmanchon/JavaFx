package Interpreter.SymbolTable.Objects;

import Interpreter.SymbolTable.Types.ArrayType;
import Interpreter.SymbolTable.Types.ITypes;
import Interpreter.SymbolTable.Types.PointerVariable;

public class Variable {

    private String name;
    private ITypes type;


    public Variable() {
    }

    public Variable(Variable variable){
        this.name = variable.getName();

        if (variable.getType() instanceof PointerVariable) {
            this.type = new PointerVariable((PointerVariable) variable.getType());
        } else if (variable.getType() instanceof ArrayType) {
            this.type = new ArrayType((ArrayType) variable.getType());
        } else {
            this.type = new ITypes(variable.getType());
        }
    }

    public Variable(String name, ITypes type) {
        this.name = name;
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(ITypes type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public ITypes getType() {
        return this.type;
    }

    @Override
    public String toString() {
        String result = "\t\t\t\t<Variable name=\"" + name + "\">";
        result += this.type.toString();
        result += "</Variable>\n";
        return result;

    }


}
