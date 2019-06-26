package SymbolTable;

public class SimpleTypes extends ITypes {

    private int max;
    private int min;

    public  SimpleTypes(){
        this.max = 0;
        this.min = 0;
    }

    public SimpleTypes(int max, int min){
        this.max = max;
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
