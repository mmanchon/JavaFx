import GUI.Controllers.Controller;
import GUI.Models.Editor;
import Interpreter.Interpreter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
        // Load root layout from fxml codeArea.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("GUI/Interface/main.fxml"));

        Pane rootLayout = loader.load();

        Controller controller = loader.getController();
        controller.setInterpreter(this.interpreter);
        controller.setEditor(new Editor());

        Scene scene = new Scene(rootLayout);

        this.stage.getIcons().add(new Image("Resources/img/icon.png"));
        this.stage.setTitle("Memory Debug");
        this.stage.setScene(scene);
        this.interpreter.setController(controller);

        stage.setOnHidden(e -> {
            controller.shutdown();
            Platform.exit();
        });
        this.stage.show();
    }


    public Interpreter getInterpreter() {
        return this.interpreter;
    }

}