import GUI.Controllers.StaticController;
import GUI.Models.Editor;
import Interpreter.Interpreter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class GUI extends Application {

    private Stage stage;

    private Interpreter interpreter;

    @Override
    public void start(Stage primaryStage) throws Exception {


        this.interpreter = new Interpreter();
        this.stage = primaryStage;

        initStaticMemoryLayout();

    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes the root layout.
     */

    private void initStaticMemoryLayout() throws Exception {
        // Load root layout from fxml file.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("GUI/Interface/static.fxml"));

        Pane rootLayout = loader.load();

        StaticController staticController = loader.getController();
        staticController.setInterpreter(this.interpreter);
        staticController.setEditor(new Editor());

        Scene scene = new Scene(rootLayout);


       // this.stage.setMaximized(true);
        this.stage.setTitle("Memory Debug");
        this.stage.setScene(scene);
        this.interpreter.setController(staticController);

        this.stage.show();
    }


    public Interpreter getInterpreter() {
        return this.interpreter;
    }

}