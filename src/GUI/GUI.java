package GUI;

import Interpreter.Interpreter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.List;

public class GUI extends Application {

    private Pane rootLayout;
    private Stage stage;
   // private Stage dynamicStage;

    private Interpreter interpreter;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parameters params = getParameters();
        List<String> list = params.getRaw();

        if(list.size() != 1){
            System.out.println("ERROR: Numero de argumentos invalido");
        }else{

            this.interpreter = new Interpreter(list.get(0));
            //this.interpreter.start();
            this.stage = primaryStage;

            initStaticMemoryLayout();
            //initDynamicStage();

        }

    }

    public static void main(String[] args){
        launch(args);
    }

    /**
     * Initializes the root layout.
     */

    public void initStaticMemoryLayout() throws Exception{
        // Load root layout from fxml file.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("static.fxml"));

        rootLayout = loader.load();

        StaticController staticController = (StaticController) loader.getController();
        staticController.setInterpreter(this.interpreter);
        this.interpreter.setDynamicController(staticController);

        // Show the scene containing the root layout.
        Scene scene = new Scene(rootLayout);
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
      //  stage.setX(stage.getWidth());
      //  stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 4);
        // this.stage.setX((primScreenBounds.getWidth() - this.stage.getWidth()) / 2);
      //  this.stage.setY((primScreenBounds.getHeight() - this.stage.getHeight()) / 4);
        this.stage.setTitle("Memory Debug");
        this.stage.setScene(scene);

       // staticController.setMaxWidth(this.stage);

        this.stage.show();
    }

    /**
     * Initializes the root layout.
     */

   /* public void initDynamicStage() throws Exception{
        this.dynamicStage = new Stage();

        // Load root layout from fxml file.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("dynamic.fxml"));

        GridPane dynamicPane = (GridPane) loader.load();

        DynamicController controller = (DynamicController) loader.getController();
      //  this.interpreter.setDynamicController(controller);
        // Show the scene containing the root layout.
        Scene scene = new Scene(dynamicPane, 400, 800);


        this.dynamicStage.setTitle("Program Dynamic Memory");
        this.dynamicStage.setScene(scene);

        controller.setMaxHeightWidth(this.dynamicStage);

        this.dynamicStage.show();

    }*/

    public Interpreter getInterpreter(){ return this.interpreter;}

}