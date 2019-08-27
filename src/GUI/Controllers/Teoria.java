package GUI.Controllers;

import Interpreter.Errors.BasicError;
import Interpreter.Theory.BasicTheory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class Teoria {


    @FXML
    private Button accept;
    @FXML
    private Button seeMore;
    @FXML
    private TextArea explicacion;
    @FXML
    private ImageView image;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public Teoria() {

    }

    @FXML
    void initialize(){}

    public void setTheory(BasicTheory basicTheory){
        this.explicacion.setText(basicTheory.getMessage());
        this.image.setImage(basicTheory.getImege());
    }

    @FXML
    public void close(){
        Stage stage = (Stage) accept.getScene().getWindow();
        stage.close();
    }


}

