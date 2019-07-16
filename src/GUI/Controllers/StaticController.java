package GUI.Controllers;

import GUI.Models.Editor;
import GUI.Models.MemoryRow;
import GUI.Models.TextFile;
import Interpreter.Interpreter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import javax.xml.soap.Text;
import java.io.File;
import java.util.Arrays;


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
    @FXML
    public TextArea textArea;
    @FXML
    public TableView dynamicTableView;
    @FXML
    public TableView tableView;
    @FXML
    public Label numLines;

    private Interpreter interpreter;
    private Editor editor;
    private TextFile currentTextfile;
    private int from = 0;
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
        textArea.setStyle("-fx-highlight-fill: lightgray; -fx-highlight-text-fill: firebrick; -fx-font-size: 12px;");

    }

    public void setInterpreter(Interpreter interpreter){
        this.interpreter = interpreter;
    }

    public void setEditor(Editor editor){ this.editor = editor;}

    @FXML
    public void nextLine(){


        tableView.getItems().remove(0,tableView.getItems().size());

        tableView.setItems(interpreter.convertToMemoryData());
        tableView.refresh();

        dynamicTableView.setItems(interpreter.getDynamicMemoryRows());
        dynamicTableView.refresh();

    //    if(this.from == 0) {
        this.from = 0;
            for (int i = 0; i < this.interpreter.getNumLines() - 1; i++) {
                this.from += Arrays.asList(textArea.getText().split("\n")).get(i).length()+1;
            }
      //  }
        int to = Arrays.asList(textArea.getText().split("\n")).get(this.interpreter.getNumLines()-1).length()+1;
        this.textArea.selectRange(from, from+to);

        from = from+to;
        this.numLines.setText("Line: "+this.interpreter.getNumLines());
        this.interpreter.analiseNextLine();

    }

    @FXML
    private void onSave(){
        TextFile textFile = new TextFile(this.currentTextfile.getFile(), Arrays.asList(textArea.getText().split("\n")));
        editor.save(textFile);
    }

    @FXML
    private void onLoad(){
        this.tableView.getItems().remove(0,this.tableView.getItems().size());
        this.dynamicTableView.getItems().remove(0,this.dynamicTableView.getItems().size());
        this.interpreter.eraseAllData();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        File file = fileChooser.showOpenDialog(null);

        if(file != null){
            this.currentTextfile = editor.load(file.toPath());
            if(this.currentTextfile != null){
                textArea.clear();
                this.currentTextfile.getContent().forEach(line -> textArea.appendText(line + "\n"));
                this.interpreter.setNewFile(file);
            }else{
                System.out.println("Error loading file!");
            }
        }

       this.from = 0;
    //    for(int i = 0; i < this.interpreter.getNumLines(); i++){
    //        this.from += Arrays.asList(textArea.getText().split("\n")).get(i).length();
     //   }
    }

    @FXML
    private void onClose(){
        editor.close();
    }

    @FXML
    private void onAbout(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("About");
        alert.setContentText("Program created to help those who strugle with memory :)");
        alert.show();
    }

    @FXML
    private void restart(){
        this.tableView.getItems().remove(0,this.tableView.getItems().size());
        this.dynamicTableView.getItems().remove(0,this.dynamicTableView.getItems().size());
        this.interpreter.eraseAllData();
        this.interpreter.restart();
        this.from = 0;
    }

}
