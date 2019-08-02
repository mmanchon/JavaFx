package Interpreter.SymbolTable;

import Interpreter.SymbolTable.Contexts.Context;
import Interpreter.SymbolTable.Objects.Constant;
import Interpreter.SymbolTable.Objects.Variable;
import Interpreter.Tokens.Type;

import javax.xml.bind.TypeConstraintException;
import java.util.Hashtable;
import java.util.Vector;

public class Node implements Cloneable {

    private Hashtable contantsList = new Hashtable();

    private Hashtable variablesList = new Hashtable();

    private Vector contextsList = new Vector();

    private String nodeName;

    private int nodeLine;

    private Type returnType;

    private int returnLine;

    private String returnVariable;

    public Node() {
    }

    public Node(Node node) {
        this.contextsList = (Vector) node.getContextsList().clone();
        this.variablesList = (Hashtable) node.getVariablesList().clone();
        this.nodeName = node.getNodeName();
        this.nodeLine = node.getNodeLine();
        this.returnType = node.getReturnType();
        this.returnLine = node.getReturnLine();
        this.returnVariable = node.getReturnVariable();
    }

    public void addConstant(Constant constant) {
        this.contantsList.put(constant.getName(), constant);
    }

    public void addVariable(Variable variable) {
        this.variablesList.put(variable.getName(), variable);
    }

    public void addContext(Context context) {
        this.contextsList.add(context);
    }

    public Constant getConstant(String name) {
        return (Constant) this.contantsList.get(name);
    }

    public Variable getVariable(String name) {
        return (Variable) this.variablesList.get(name);
    }


    public Hashtable getVariablesList() {
        return this.variablesList;
    }

    public int getNodeLine() {
        return nodeLine;
    }

    public void setNodeLine(int nodeLine) {
        this.nodeLine = nodeLine;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Vector getContextsList() {
        return contextsList;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public int getReturnLine() {
        return returnLine;
    }

    public void setReturnLine(int returnLine) {
        this.returnLine = returnLine;
    }

    public Context getContext(int index) {
        return (Context) this.contextsList.get(index);
    }

    public void setContextsList(Vector contextsList) {
        this.contextsList = contextsList;
    }

    public Context getLastContext() {
        return (Context) this.contextsList.get(this.contextsList.size() - 1);
    }

    public boolean hasContext() {
        return this.contextsList.isEmpty();
    }

    public void removeLastContext() {
        this.contextsList.remove(this.contextsList.size() - 1);
    }

    public String getReturnVariable() {
        return returnVariable;
    }

    public void setReturnVariable(String returnVariable) {
        this.returnVariable = returnVariable;
    }

    @Override
    public String toString() {
        String result = "\t\t<Bloc>\n";

        result += "\t\t\t<Name= " + this.nodeName + " Line=" + this.nodeLine + " ReturnVariable = " + this.returnVariable + ">\n";
        result += "\t\t\t<Constants>\n";
        for (int i = 0; i < this.contantsList.size(); i++)
            result += ((Constant) this.contantsList.values().toArray()[i]).toString();
        result += "\t\t\t</Constants>\n";

        result += "\t\t\t<Variables>\n";
        for (int i = 0; i < this.variablesList.size(); i++)
            result += (this.variablesList.values().toArray()[i]).toString();
        result += "\t\t\t</Variables>\n";


        result += "\t\t</Bloc>\n";
        return result;
    }

    public void deleteAllData() {
        this.contantsList.clear();
        this.variablesList.clear();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
