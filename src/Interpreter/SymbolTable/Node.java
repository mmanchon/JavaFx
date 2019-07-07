package Interpreter.SymbolTable;

import Interpreter.SymbolTable.Contexts.Context;
import Interpreter.SymbolTable.Objects.Constant;
import Interpreter.SymbolTable.Objects.Variable;

import java.util.Hashtable;
import java.util.Vector;

public class Node {

    private Hashtable contantsList = new Hashtable();

    private Hashtable variablesList = new Hashtable();

    private Vector contextsList = new Vector();

    private int offset = 0;

    public Node(){}

    public void addConstant(Constant constant){
        this.contantsList.put(constant.getName(),constant);
    }

    public void addVariable(Variable variable){
        this.variablesList.put(variable.getName(),variable);
    }

    public void addContext(Context context){this.contextsList.add(context);}

    public Constant getConstant(String name){
        return (Constant) this.contantsList.get(name);
    }

    public Variable getVariable(String name){
        return (Variable) this.variablesList.get(name);
    }

    public Hashtable getVariablesList(){
        return this.variablesList;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public Vector getContextsList() {
        return contextsList;
    }

    public Context getContext(int index){
        return (Context) this.contextsList.get(index);
    }

    public void setContextsList(Vector contextsList) {
        this.contextsList = contextsList;
    }

    public Context getLastContext(){
        return (Context) this.contextsList.get(this.contextsList.size()-1);
    }

    public boolean hasContext(){
        return this.contextsList.isEmpty();
    }

    public void removeLastContext(){
        this.contextsList.remove(this.contextsList.size()-1);
    }

    @Override
    public String toString(){
        String result = "\t\t<Bloc>\n";

        result += "\t\t\t<Constants>\n";
        for (int i=0; i<this.contantsList.size(); i++)
            result += ((Constant)this.contantsList.values().toArray()[i]).toString();
        result += "\t\t\t</Constants>\n";

        result += "\t\t\t<Variables>\n";
        for (int i=0; i<this.variablesList.size(); i++)
            result += (this.variablesList.values().toArray()[i]).toString();
        result += "\t\t\t</Variables>\n";

        result += "\t\t</Bloc>\n";
        return result;
    }

    public void deleteAllData(){
        this.contantsList.clear();
        this.variablesList.clear();
        this.offset = 0;
    }
}
