package SymbolTable;

public class Variable {

    private String name;
    private ITypes type;


    public Variable(){}

    public Variable(String name, ITypes type){
        this.name = name;
        this.type = type;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setType(ITypes type){
        this.type = type;
    }

    public String getName(){
        return this.name;
    }

    public ITypes getType(){
        return this.type;
    }

    @Override
    public String toString(){
        String result = "\t\t\t\t<Variable name=\"" +name +"\">";
        result += this.type.toString();
        result += "</Variable>\n";
        return result;

    }

}
