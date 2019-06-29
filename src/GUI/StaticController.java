package GUI;

import Interpreter.Interpreter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.Comparator;

public class StaticController {

    @FXML
    public TableColumn<MemoryRow,String> variableName = new TableColumn<>();
    @FXML
    public TableColumn<MemoryRow,String> variableSize= new TableColumn<>();
    @FXML
    public TableColumn<MemoryRow,String> variableValue = new TableColumn<>();
    @FXML
    public TableColumn<MemoryRow,String> variableOffset = new TableColumn<>();
    @FXML
    public TableColumn<MemoryRow,String> dynamicName = new TableColumn<>();
    @FXML
    public TableColumn<MemoryRow,String> dynamicSize= new TableColumn<>();
    @FXML
    public TableColumn<MemoryRow,String> dynamicValue = new TableColumn<>();
    @FXML
    public TableColumn<MemoryRow,String> dynamicOffset = new TableColumn<>();

    public TableView dynamicTableView;
    public TableView tableView;
    private ObservableList<MemoryRow> memoryRows = FXCollections.observableArrayList();

    private Interpreter interpreter;
    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public StaticController() {

    }

    @FXML
    void initialize(){
        // Initialize the person table with the two columns.
        variableName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        variableValue.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        variableSize.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());
        variableOffset.setCellValueFactory(cellData -> cellData.getValue().offsetProperty());
        dynamicName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        dynamicValue.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        dynamicSize.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());
        dynamicOffset.setCellValueFactory(cellData -> cellData.getValue().offsetProperty());
    }

    public void setInterpreter(Interpreter interpreter){
        this.interpreter = interpreter;
    }

    public void nextLine(){

        this.interpreter.analiseNextLine();

        tableView.getItems().remove(0,tableView.getItems().size());

        tableView.setItems(interpreter.convertToMemoryData());
        tableView.refresh();

        dynamicTableView.setItems(interpreter.getDynamicMemoryRows());
        dynamicTableView.refresh();
    }

    public void updateList(ObservableList<MemoryRow> list){
        this.memoryRows = list;
        Comparator<MemoryRow> comparator = Comparator.comparing(MemoryRow::getOffsetInt);
        this.memoryRows.sort(comparator);

        tableView.setItems(this.memoryRows);
        tableView.refresh();
    }

}
