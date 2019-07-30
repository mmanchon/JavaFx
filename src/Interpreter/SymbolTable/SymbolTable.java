package Interpreter.SymbolTable;

import java.util.Vector;

public class SymbolTable {

    private int currentNode;
    private int staticOffset;
    private int dynamicOffset;

    private Vector emptyFunctions;

    private Node main;

    private Vector executionFunctions;


    public SymbolTable() {
        this.currentNode = 0;
        this.staticOffset = -4;
        this.dynamicOffset = 0;
        this.emptyFunctions = new Vector();
        this.executionFunctions = new Vector();
        this.main = new Node();
    }

    public void addEmptyFunctions(Node node) {
        this.emptyFunctions.add(this.currentNode, node);
    }

    public void addxecutionFunctions(Node node) {
        this.executionFunctions.add(this.currentNode, node);

    }


    public Node getEmptyFunction(int node) {
        return (Node) this.emptyFunctions.get(node);
    }

    public int getCurrentNode() {
        return this.currentNode;
    }

    public void setCurrentNode(int currentNode) {
        this.currentNode = currentNode;
    }

    public int getStaticOffset() {
        return staticOffset;
    }

    public void setStaticOffset(int staticOffset) {
        this.staticOffset = staticOffset;
    }

    public int getDynamicOffset() {
        return dynamicOffset;
    }

    public void setDynamicOffset(int dynamicOffset) {
        this.dynamicOffset = dynamicOffset;
    }

    public Vector getEmptyFunctions() {
        return emptyFunctions;
    }

    public void setEmptyFunctions(Vector emptyFunctions) {
        this.emptyFunctions = emptyFunctions;
    }

    public void removeNode(int index) {
        this.emptyFunctions.remove(this.getEmptyFunction(index));
    }

    public Node isFunction(String function) {
        Node node = null;

        for (int i = 0; i < this.emptyFunctions.size(); i++) {
            node = (Node) this.emptyFunctions.get(i);
            if (node.getNodeName().equals(function)){
                return node;
            }
        }

        return node;
    }

    public Node getFunction(String function) {
        Node node;
        for (int i = 0; i < this.executionFunctions.size(); i++) {
            node = (Node) this.executionFunctions.get(i);
            if (node.getNodeName().equals(function)) return node;
        }

        return null;
    }

    @Override
    public String toString() {
        String result = "<TaulaSimbols>\n";
        result += "\t<main>\n" +this.main.toString()+"\t</main>\n";
        result += "\t<Blocs>\n";
        for (int i = 0; i < this.emptyFunctions.size(); i++)
            result += getEmptyFunction(i).toString();
        result += "\t</Blocs>\n";
        result += "</TaulaSimbols>";
        return result;

    }

    public void deleteAllData() {
        if(!this.emptyFunctions.isEmpty()) {
            for (int i = 0; i < this.currentNode + 1; i++) {
                ((Node) this.emptyFunctions.get(i)).deleteAllData();
            }
        }
        this.currentNode = 0;

    }

    public Node getMain() {
        return main;
    }

    public void setMain(Node main) {
        this.main = main;
    }

    public Vector getExecutionFunctions() {
        return executionFunctions;
    }

    public void setExecutionFunctions(Vector executionFunctions) {
        this.executionFunctions = executionFunctions;
    }

    public Node getActualNode() {
        if (this.getExecutionFunctions().isEmpty()) {
            return this.main;
        } else {
            return (Node) this.executionFunctions.get(this.currentNode);
        }
    }

    public void removeExecutionFunction(){
        this.executionFunctions.remove(this.currentNode);
        this.currentNode--;
    }

    public Node getExecutionNode(int index){
        return (Node) this.executionFunctions.get(index);

    }

    public Node getPreviousNode(){
        if(this.currentNode > 0){
            return (Node) this.executionFunctions.get(this.currentNode-1);
        }

        return null;
    }
}
