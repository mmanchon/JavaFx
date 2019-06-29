package GUI;

import javafx.beans.property.SimpleStringProperty;

public class MemoryRow {
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleStringProperty size = new SimpleStringProperty();
    private final SimpleStringProperty value = new SimpleStringProperty();
    private final SimpleStringProperty offset = new SimpleStringProperty();



    public MemoryRow(String name,
                      String value,
                     String size, String offset){

        this.name.setValue(name);
        this.value.set(value);
        this.size.set(size);
        this.offset.set(offset);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getSize() {
        return size.get();
    }

    public SimpleStringProperty sizeProperty() {
        return size;
    }

    public void setSize(String size) {
        this.size.set(size);
    }

    public Object getValue() {
        return value.get();
    }

    public SimpleStringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public String getOffset() {
        return offset.get();
    }

    public SimpleStringProperty offsetProperty() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset.set(offset);
    }

    public int getOffsetInt(){
        String offset = String.valueOf(this.offset);
        return new Integer(offset.split("@")[1].split("]")[0]);
    }
}
