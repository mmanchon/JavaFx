package GUI;

import Interpreter.Interpreter;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.List;

public class GUI extends Application {

    private GridPane rootLayout;
    private Stage staticStage;
    private Stage dynamicStage;

    private ObservableList<MemoryRow> memoryRows = FXCollections.observableArrayList();
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
            this.staticStage = primaryStage;

            initStaticMemoryLayout();
            initDynamicStage();

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

        rootLayout = (GridPane) loader.load();

        StaticController staticController = (StaticController) loader.getController();
        staticController.setInterpreter(this.interpreter);

        // Show the scene containing the root layout.
        Scene scene = new Scene(rootLayout, 400, 500);
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();

        this.staticStage.setX((primScreenBounds.getWidth() - this.staticStage.getWidth()) / 2);
        this.staticStage.setY((primScreenBounds.getHeight() - this.staticStage.getHeight()) / 4);
        this.staticStage.setTitle("Program Static Memory");
        this.staticStage.setScene(scene);

       // staticController.setMaxWidth(this.staticStage);

        this.staticStage.show();
    }

    /**
     * Initializes the root layout.
     */

    public void initDynamicStage() throws Exception{
        this.dynamicStage = new Stage();

        // Load root layout from fxml file.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("dynamic.fxml"));

        GridPane dynamicPane = (GridPane) loader.load();

        DynamicController controller = (DynamicController) loader.getController();
        controller.setInterpreter(this.interpreter);

        // Show the scene containing the root layout.
        Scene scene = new Scene(dynamicPane, 400, 800);


        this.dynamicStage.setTitle("Program Dynamic Memory");
        this.dynamicStage.setScene(scene);

        controller.setMaxHeightWidth(this.dynamicStage);

        this.dynamicStage.show();

    }


    /**
     * Returns the main stage.
     * @return
     */

    public Stage getStaticStage() {
        return staticStage;
    }

    /**
     * Returns the data as an observable list of Persons.
     * @return
     */
    public ObservableList<MemoryRow> getMemoryData() {
        return memoryRows;
    }

    public Interpreter getInterpreter(){ return this.interpreter;}

}