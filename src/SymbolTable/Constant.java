package SymbolTable;

public class Constant {

    private String name;
    private ITypes type;

    public Constant(){}

    public Constant(String name, ITypes type){
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
}
