package Interpreter.SymbolTable.Types;

import Interpreter.SymbolTable.Types.ArrayType;

public class PointerVariable extends ArrayType {

    private boolean hasMemory = false;
    private Object pointerOffset;
    private Object pointerVariable;
    private int nodeReferenced;

    public PointerVariable() {
        this.pointerVariable = null;
        this.nodeReferenced = -1;
    }

    public PointerVariable(PointerVariable pointerVariable){
        super(pointerVariable);
        this.hasMemory = pointerVariable.isHasMemory();
        this.pointerOffset = pointerVariable.getPointerOffset();
        this.pointerVariable = pointerVariable.getPointerVariable();
        this.nodeReferenced = pointerVariable.getNodeReferenced();
    }

    public PointerVariable(String varName, String name, int size, Object value, int offset, int minPosition, int maxPosition, boolean hasMemory) {
        super(varName, name, size, value, offset, maxPosition, minPosition);
        this.setValue(offset);
        this.hasMemory = hasMemory;

    }

    public PointerVariable(boolean hasMemory) {
        this.hasMemory = hasMemory;
    }

    public boolean isHasMemory() {
        return hasMemory;
    }

    public void setHasMemory(boolean hasMemory) {
        this.hasMemory = hasMemory;
    }

    public Object getPointerOffset() {
        return this.pointerOffset;
    }

    public void setOffset(Object offset) {
        this.pointerOffset = offset;
    }

    public void setPointerOffset(Object pointerOffset) {
        this.pointerOffset = pointerOffset;
    }

    public Object getPointerVariable() {
        return pointerVariable;
    }

    public void setPointerVariable(Object pointerVariable) {
        this.pointerVariable = pointerVariable;
    }

    public int getNodeReferenced() {
        return nodeReferenced;
    }

    public void setNodeReferenced(int nodeReferenced) {
        this.nodeReferenced = nodeReferenced;
    }
}

