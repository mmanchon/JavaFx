package GUI;

import Interpreter.Interpreter;
import SymbolTable.ArrayType;
import SymbolTable.PointerVariable;
import SymbolTable.Variable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class DynamicController {

    @FXML
    public TableColumn<MemoryRow,String> dynamicName = new TableColumn<>();
    @FXML
    public TableColumn<MemoryRow,String> dynamicSize= new TableColumn<>();
    @FXML
    public TableColumn<MemoryRow,String> dynamicValue = new TableColumn<>();
    @FXML
    public TableColumn<MemoryRow,String> dynamicOffset = new TableColumn<>();

    public TableView tableView;

    private ObservableList<MemoryRow> memoryRows = FXCollections.observableArrayList();
    private GUI gui;
    private Interpreter interpreter;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public DynamicController() {

    }

    @FXML
    void initialize(){
        // Initialize the person table with the two columns.
        dynamicName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        dynamicValue.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        dynamicSize.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());
        dynamicOffset.setCellValueFactory(cellData -> cellData.getValue().offsetProperty());
    }

    public void setInterpreter(Interpreter interpreter){
        this.interpreter = interpreter;
    }

    public void setMaxHeightWidth(Stage stage){
        // We bind the prefHeight- and prefWidthProperty to the height and width of the stage.
        tableView.prefHeightProperty().bind(stage.heightProperty());
        tableView.prefWidthProperty().bind(stage.widthProperty());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    public void updateRows(Variable variable, int offset){
        PointerVariable pointerVariable = (PointerVariable) variable.getType();
        this.memoryRows.removeAll();
        tableView.getItems().remove(0,tableView.getItems().size());
        // System.out.println("Hola");
        for(int i = 0; i < pointerVariable.getMaxPosition(); i++){
            this.memoryRows.add(new MemoryRow(variable.getName()+"["+i+"]",((ArrayType)variable.getType()).getElement(i).toString(),variable.getType().getName().split("_")[0],'@' + String.valueOf(offset + variable.getType().getSize())));
            offset+=variable.getType().getSize();
        }

        tableView.setItems(this.memoryRows);
        tableView.refresh();
    }

}
