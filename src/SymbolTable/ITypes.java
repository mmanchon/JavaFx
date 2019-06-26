package SymbolTable;

public class ITypes {

    private String name;
    private int size;
    private Object value;

    public ITypes(){
        this.size = 0;
        this.name = "";
        this.value = new Object();
    }

    public ITypes(String name, int size, Object value){
        this.name = name;
        this.size = size;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString(){
        String result = "<IType name=\"" +this.name +"\" size=\""+this.size+"\" value=\""+this.value+"\">";
        result += "</IType>";
        return result;
    }
}
