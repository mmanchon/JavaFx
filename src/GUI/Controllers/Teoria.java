package GUI.Controllers;

import Interpreter.Errors.BasicError;
import Interpreter.Theory.BasicTheory;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.fxmisc.richtext.LineNumberFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
       // this.seeMoreLink = new Hyperlink(basicTheory.getUrl().toString());
   /*     this.image.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event arg0) {
                Pane pane = new Pane();
                Stage stage = new Stage();
                stage.setTitle("Image");
                stage.setWidth(500);
                stage.setHeight(500);

                ImageView imageView = new ImageView(image.getImage());
                imageView.setSmooth(true);
                imageView.setPreserveRatio(true);
                imageView.setFitHeight(stage.getHeight());
                imageView.setFitWidth(stage.getWidth());
                pane.getChildren().add(imageView);
                Scene scene = new Scene(pane);
                stage.setScene(scene);
                stage.show();

            }
        });*/

        this.seeMore.setOnAction(new EventHandler<ActionEvent>() {
                               @Override public void handle(ActionEvent e) {
                                   try {
                                       Desktop.getDesktop().browse(basicTheory.getUrl());
                                   } catch (IOException e1) {
                                       e1.printStackTrace();
                                   }
                               }
                           }
        );
    }

    @FXML
    public void close(){
        Stage stage = (Stage) accept.getScene().getWindow();
        stage.close();
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }
}

