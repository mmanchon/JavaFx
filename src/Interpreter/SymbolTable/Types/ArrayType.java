package Interpreter.SymbolTable.Types;

import Interpreter.SymbolTable.Objects.Variable;

import java.util.Random;
import java.util.Vector;

public class ArrayType extends ITypes {

    private int maxPosition = 0;

    private int minPosition = 0;

    private Vector<Variable> elemntsList = new Vector<>();


    public ArrayType() {
    }

    public ArrayType(String varName, String name, int size, Object value, int offset, int maxPosition, int minPosition) {
        super(name, size, value, offset);
        this.maxPosition = maxPosition;
        this.minPosition = minPosition;
        this.elemntsList.setSize(maxPosition);
        Random random = new Random();
        Variable variable;
        for (int i = minPosition; i < maxPosition; i++) {
            variable = new Variable(varName + "[" + i + "]", new ITypes(name, size, random.nextInt(10000000), offset));
            offset += size;
            this.elemntsList.set(i, variable);
        }
    }

    public ArrayType(String name, int minPosition, int maxPosition) {
        this.maxPosition = maxPosition;
        this.minPosition = minPosition;
        this.elemntsList.setSize(maxPosition);
        Variable variable;
        Random random = new Random();
        for (int i = minPosition; i < maxPosition; i++) {
            variable = new Variable(name + "[" + i + "]", this);
            variable.getType().setValue(random.nextInt(10000000));
            this.elemntsList.set(i, variable);
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

    public Vector<Variable> getElemntsList() {
        return elemntsList;
    }

    public void setElemntsList(Vector<Variable> elemntsList) {
        this.elemntsList = elemntsList;
    }

    public Variable getElement(int index) {
        return this.elemntsList.get(index);
    }

    public void setElement(int index, Object object) {
        Variable variable = this.elemntsList.get(index);
        variable.getType().setValue(object);
        this.elemntsList.set(index, variable);
    }

    public void removeAllElements(){
        this.elemntsList.removeAllElements();
    }

}
