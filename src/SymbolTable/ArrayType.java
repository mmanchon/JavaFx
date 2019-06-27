package SymbolTable;

import java.util.Hashtable;
import java.util.Vector;

public class ArrayType extends ITypes{

    private int maxPosition = 0;

    private int minPosition = 0;

    private Vector<Object> elemntsList = new Vector<>();


    public ArrayType(){}

    public ArrayType(int minPosition, int maxPosition) {
        this.maxPosition = maxPosition;
        this.minPosition = minPosition;
        this.elemntsList.setSize(maxPosition);
        for(int i = minPosition; i < maxPosition; i++ ){
            this.elemntsList.set(i,0);
        }
    }

    public int getMaxPosition() {
        return maxPosition;
    }

    public void setMaxPosition(int maxPosition) {
        this.maxPosition = maxPosition;
    }

    public int getMinPosition() {
        return minPosition;
    }

    public void setMinPosition(int minPosition) {
        this.minPosition = minPosition;
    }

    public Vector<Object> getElemntsList() {
        return elemntsList;
    }

    public void setElemntsList(Vector<Object> elemntsList) {
        this.elemntsList = elemntsList;
    }

    public Object getElement(int index){
        return this.elemntsList.get(index);
    }

    public void setElement(int index, Object object){
        this.elemntsList.set(index, object);
    }
}
