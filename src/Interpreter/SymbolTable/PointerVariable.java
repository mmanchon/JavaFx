package Interpreter.SymbolTable;

public class PointerVariable extends ArrayType {

    private boolean hasMemory = false;
    private Object pointerOffset;

    public PointerVariable(){
    }

    public PointerVariable(String varName, String name, int size, Object value, int offset, int minPosition, int maxPosition, boolean hasMemory) {
        super(varName,name,size,value,offset,maxPosition,minPosition);
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
}

