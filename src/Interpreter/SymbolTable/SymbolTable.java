package Interpreter.SymbolTable;

import java.util.Vector;

public class SymbolTable {

    private int actualNode;
    private int staticOffset;
    private int dynamicOffset;

    private Vector nodeList;


    public SymbolTable(){
        this.actualNode = 0;
        this.staticOffset = -4;
        this.dynamicOffset = 0;
        this.nodeList = new Vector();
    }

    public void addNode(Node node){
        if(this.nodeList.contains(node)) {
            this.nodeList.set(this.actualNode, node);
        }else{
            this.nodeList.add(this.actualNode, node);
        }
    }

    public Node getNode(int node){return (Node) this.nodeList.get(node);}

    public int getActualNode(){
        return this.actualNode;
    }

    public void setActualNode(int actualNode){
        this.actualNode = actualNode;
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

    public Vector getNodeList() {
        return nodeList;
    }

    public void setNodeList(Vector nodeList) {
        this.nodeList = nodeList;
    }

    public void removeNode(int index){
        this.nodeList.remove(this.getNode(index));
    }

    public int isFunction(String function){
        Node node;

        for(int i = 0; i < this.nodeList.size();i++){
            node = (Node)this.nodeList.get(i);
            if(node.getNodeName().equals(function)) return i;
        }

        return 0;
    }

    public Node getFunction(String function){
        Node node;
        for(int i = 0; i < this.nodeList.size();i++){
            node = (Node)this.nodeList.get(i);
            if(node.getNodeName().equals(function)) return node;
        }

        return null;
    }

    @Override
    public String toString(){
        String result = "<TaulaSimbols>\n";
        result += "\t<Blocs>\n";
        for (int i=0; i<this.nodeList.size(); i++)
            result += getNode(i).toString();
        result += "\t</Blocs>\n";
        result += "</TaulaSimbols>";
        return result;

    }

    public void deleteAllData(){
        for(int i = 0; i < this.actualNode+1; i++){
            ((Node)this.nodeList.get(i)).deleteAllData();
        }
        this.actualNode = 0;

    }
}
