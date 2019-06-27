package SymbolTable;

public class PointerVariable extends ArrayType {

    private boolean hasMemory = false;

    public PointerVariable(){}

    public PointerVariable(int minPosition, int maxPosition, boolean hasMemory) {
        super(minPosition, maxPosition);
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
}
