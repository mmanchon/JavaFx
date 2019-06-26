package GUI;

import Interpreter.Interpreter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class StaticController {

    @FXML
    public TableColumn<MemoryRow,String> variableName = new TableColumn<>();
    @FXML
    public TableColumn<MemoryRow,String> variableSize= new TableColumn<>();
    @FXML
    public TableColumn<MemoryRow,String> variableValue = new TableColumn<>();
    @FXML
    public TableColumn<MemoryRow,String> variableOffset = new TableColumn<>();

    public TableView tableView;

    private GUI gui;
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
    }

    public void setInterpreter(Interpreter interpreter){
        this.interpreter = interpreter;
    }

    public void setMaxWidth(Stage stage){
        // We bind the prefHeight- and prefWidthProperty to the height and width of the stage.
        tableView.prefWidthProperty().bind(stage.widthProperty());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void nextLine(){

        this.interpreter.analiseNextLine();
        tableView.getItems().remove(0,tableView.getItems().size());
        tableView.setItems(interpreter.convertToMemoryData());
        tableView.refresh();
    }

}
