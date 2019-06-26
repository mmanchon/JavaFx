package SymbolTable;

import java.util.Vector;

public class SymbolTable {

    private int actualNode;

    private Vector nodeList = new Vector();

    public SymbolTable(){
        this.actualNode = 0;
    }

    public void addNode(Node node){ this.nodeList.add(node);}

    public Node getNode(int node){return (Node) this.nodeList.get(node);}

    public int getActualNode(){
        return this.actualNode;
    }

    public void setActualNode(int actualNode){
        this.actualNode = actualNode;
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
