package Interpreter.SymbolTable;

import Interpreter.SymbolTable.Contexts.Context;
import Interpreter.SymbolTable.Objects.Constant;
import Interpreter.SymbolTable.Objects.Variable;
import Interpreter.Tokens.Type;

import javax.xml.bind.TypeConstraintException;
import java.util.Hashtable;
import java.util.Vector;

public class Node {

    private Hashtable contantsList = new Hashtable();

    private Hashtable variablesList = new Hashtable();

    private Hashtable argumentList = new Hashtable();

    private Vector contextsList = new Vector();

    private String nodeName;

    private int nodeLine;

    private Type returnType;

    private Object returnValue;

    private int returnLine;

    private int returnNode;

    public Node(){
        this.returnValue = null;
    }

    public void addConstant(Constant constant){
        this.contantsList.put(constant.getName(),constant);
    }

    public void addVariable(Variable variable){
        this.variablesList.put(variable.getName(),variable);
    }

    public void addArgument(Variable variable){
        this.argumentList.put(variable.getName(),variable);
    }

    public Hashtable getArgumentList() {
        return argumentList;
    }

    public void addContext(Context context){this.contextsList.add(context);}

    public Constant getConstant(String name){
        return (Constant) this.contantsList.get(name);
    }

    public Variable getVariable(String name){
        return (Variable) this.variablesList.get(name);
    }

    public Variable getArgument(String name){
        return (Variable) this.argumentList.get(name);
    }

    public Hashtable getVariablesList(){
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

    public int getReturnNode() {
        return returnNode;
    }

    public void setReturnNode(int returnNode) {
        this.returnNode = returnNode;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
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

        result += "\t\t\t<Name= "+this.nodeName+" Line="+this.nodeLine+">\n";
        result += "\t\t\t<Constants>\n";
        for (int i=0; i<this.contantsList.size(); i++)
            result += ((Constant)this.contantsList.values().toArray()[i]).toString();
        result += "\t\t\t</Constants>\n";

        result += "\t\t\t<Variables>\n";
        for (int i=0; i<this.variablesList.size(); i++)
            result += (this.variablesList.values().toArray()[i]).toString();
        result += "\t\t\t</Variables>\n";

        result += "\t\t\t<Arguments>\n";
        for (int i=0; i<this.argumentList.size(); i++)
            result += (this.argumentList.values().toArray()[i]).toString();
        result += "\t\t\t</Arguments>\n";

        result += "\t\t</Bloc>\n";
        return result;
    }

    public void deleteAllData(){
        this.contantsList.clear();
        this.variablesList.clear();
        this.argumentList.clear();
    }
}
