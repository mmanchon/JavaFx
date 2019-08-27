package Interpreter.SymbolTable;

import java.util.ArrayList;

public class SymbolTable {

    private int currentNode;
    private int staticOffset;
    private int dynamicOffset;

    private ArrayList emptyFunctions;

    private ArrayList executionFunctions;

    public SymbolTable() {
        this.currentNode = 0;
        this.staticOffset = -4;
        this.dynamicOffset = 0;
        this.emptyFunctions = new ArrayList();
        this.executionFunctions = new ArrayList();
    }

    public void addEmptyFunctions(Node node) {
        this.emptyFunctions.add(this.currentNode, node);
    }

    public void addxecutionFunctions(Node node) {
        this.executionFunctions.add(this.currentNode, node);
    }

    public void substituteExecutionNode(Node node) {
        this.executionFunctions.set(this.executionFunctions.indexOf(node), node);
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

    public ArrayList getEmptyFunctions() {
        return emptyFunctions;
    }

    public void setEmptyFunctions(ArrayList emptyFunctions) {
        this.emptyFunctions = emptyFunctions;
    }

    public void removeEmptyNode(int index) {
        this.emptyFunctions.remove(this.getEmptyFunction(index));
    }

    public void removeExecutionNode(int index) {
        this.executionFunctions.remove(this.executionFunctions.get(index));

    }

    public Node isFunction(String function) {
        Node node = null;

        for (int i = 0; i < this.emptyFunctions.size(); i++) {
            node = (Node) this.emptyFunctions.get(i);
            if (node.getNodeName().equals(function)) {
                return new Node(node);
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
        result += "\t<Blocs>\n";
        for (int i = 0; i < this.executionFunctions.size(); i++)
            result += getExecutionNode(i).toString();
        result += "\t</Blocs>\n";
        result += "</TaulaSimbols>";
        return result;

    }

    public void deleteAllData() {
        if (!this.emptyFunctions.isEmpty()) {
            for (int i = 0; i < this.emptyFunctions.size(); i++) {
                ((Node) this.emptyFunctions.get(i)).deleteAllData();
            }
            this.emptyFunctions.clear();
        }
        if (!this.executionFunctions.isEmpty()) {
            for (int i = 0; i < this.executionFunctions.size(); i++) {
                ((Node) this.executionFunctions.get(i)).deleteAllData();
            }
            this.executionFunctions.clear();
        }
        this.currentNode = 0;
        this.dynamicOffset = 0;
        this.staticOffset = 0;
        this.currentNode = 0;

    }


    public ArrayList getExecutionFunctions() {
        return executionFunctions;
    }

    public void setExecutionFunctions(ArrayList executionFunctions) {
        this.executionFunctions = executionFunctions;
    }


    public void removeExecutionFunction() {
        this.executionFunctions.remove(this.currentNode);
        this.currentNode--;
    }

    public Node getExecutionNode(int index) {
        return (Node) this.executionFunctions.get(index);

    }

    public Node getPreviousNode() {
        if (this.currentNode > 0) {
            return (Node) this.executionFunctions.get(this.currentNode - 1);
        }

        return null;
    }
}
