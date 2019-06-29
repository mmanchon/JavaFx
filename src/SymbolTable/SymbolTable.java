package SymbolTable;

import java.util.Vector;

public class SymbolTable {

    private int actualNode;
    private int staticOffset;
    private int dynamicOffset;

    private Vector nodeList = new Vector();

    public SymbolTable(){
        this.actualNode = 0;
        this.staticOffset = -4;
        this.dynamicOffset = 0;
    }

    public void addNode(Node node){ this.nodeList.add(node);}

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
}
