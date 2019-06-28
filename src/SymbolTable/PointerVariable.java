package SymbolTable;

public class PointerVariable extends ArrayType {

    private boolean hasMemory = false;
    private Object pointerOffset;

    public PointerVariable(){
    }

    public PointerVariable(String name, int minPosition, int maxPosition, boolean hasMemory) {
        super(name, minPosition, maxPosition);
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
}

