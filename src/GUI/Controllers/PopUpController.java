package GUI.Controllers;

import Interpreter.Errors.BasicError;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class PopUpController {

    @FXML
    private Button accept;
    @FXML
    private Button seeMore;
    @FXML
    private TextArea textArea;
    @FXML
    private ImageView image;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public PopUpController() {

    }

    @FXML
    void initialize(){}

    public void setError(BasicError basicError){
        this.textArea.setText(basicError.getMessage());
        this.image.setImage(basicError.getImege());
    }

    @FXML
    public void close(){
            Stage stage = (Stage) accept.getScene().getWindow();
            stage.close();
    }


}
