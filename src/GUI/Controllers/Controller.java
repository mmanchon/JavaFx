package GUI.Controllers;

import GUI.Models.Editor;
import GUI.Models.MemoryRow;
import GUI.Models.TextFile;
import Interpreter.Interpreter;
import Interpreter.Theory.BasicTheory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Controller {

    @FXML
    public TableColumn<MemoryRow, String> variableName = new TableColumn<>();
    @FXML
    public TableColumn<MemoryRow, String> variableSize = new TableColumn<>();
    @FXML
    public TableColumn<MemoryRow, String> variableValue = new TableColumn<>();
    @FXML
    public TableColumn<MemoryRow, String> variableOffset = new TableColumn<>();
    @FXML
    public TableColumn<MemoryRow, String> dynamicName = new TableColumn<>();
    @FXML
    public TableColumn<MemoryRow, String> dynamicSize = new TableColumn<>();
    @FXML
    public TableColumn<MemoryRow, String> dynamicValue = new TableColumn<>();
    @FXML
    public TableColumn<MemoryRow, String> dynamicOffset = new TableColumn<>();
    @FXML
    public CodeArea codeArea;
    @FXML
    public TextArea terminal;
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
    private ExecutorService executor;

    private final KeyCombination keyComb1 = new KeyCodeCombination(KeyCode.S,
            KeyCombination.CONTROL_DOWN);

    private static final String[] KEYWORDS = new String[] {
            "int","void","main","if","do","while","return","for","include","define","malloc","free","sizeof"
    };

    //TOERIA Y LINKS



    private final String TEORIA_IF = "The if statement evaluates the test expression inside the parenthesis ().\nIf the test expression is evaluated to true, statements inside the body of if are executed.\n If the test expression is evaluated to false, statements inside the body of if are not executed.";









    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
    private Subscription cleanupWhenDone;

    private String input = "";
    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public Controller() {
    }

    @FXML
    void initialize() {
        // Initialize the person table with the two columns.
        variableName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        variableValue.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        variableSize.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());
        variableOffset.setCellValueFactory(cellData -> cellData.getValue().offsetProperty());
        dynamicName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        dynamicValue.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        dynamicSize.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());
        dynamicOffset.setCellValueFactory(cellData -> cellData.getValue().offsetProperty());

       // codeArea.setStyle("-fx-highlight-fill: lightgray; -fx-highlight-text-fill: firebrick; -fx-font-size: 12px;");

        terminal.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {

                if(newValue.toCharArray()[newValue.length()-1] == '\n' && interpreter.needValue()){
                    interpreter.setValue(input);
                    input = "";
                    //nextLine();
                }else{
                    if(interpreter.needValue()){
                        input = input + newValue.substring(newValue.length()-1);
                    }
                }
            }
        });

        codeArea.setStyle("-fx-background-color: #2B2B2B;");
        codeArea.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event arg0) {

                if(!codeArea.getSelectedText().equals("")){
                    detectInstruction(codeArea.getSelectedText());
                }

                codeArea.setParagraphGraphicFactory(null);
                codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
            }
        });
        executor = Executors.newSingleThreadExecutor();

        this.codeArea.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if (keyComb1.match(event)) {
                    if(getCurrentTextfile() != null) {
                        onSave();
                    }else{
                        FileChooser fileChooser = new FileChooser();

                        //Set extension filter
                        FileChooser.ExtensionFilter extFilter =
                                new FileChooser.ExtensionFilter("C files (*.c)", "*.c");
                        fileChooser.getExtensionFilters().add(extFilter);

                        //Show save file dialog
                        File file = fileChooser.showSaveDialog(null);

                        if(file != null){
                            SaveFile(codeArea.getText(), file);
                        }
                        load(file);
                    }
                }
            }
        });

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        cleanupWhenDone = codeArea.multiPlainChanges()
                .successionEnds(Duration.ofMillis(10))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(codeArea.multiPlainChanges())
                .filterMap(t -> {
                    if(t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);


    }


    private void detectInstruction(String text){
        if(Arrays.asList(KEYWORDS).contains(text)){
            Parent root;
            BasicTheory basicTheory;
            try {
                switch (text){
                    case "if":

                            basicTheory = new BasicTheory(TEORIA_IF,new Image("Resources/img/if-theory.jpg"),new URI("https://www.tutorialspoint.com/cprogramming/if_else_statement_in_c.htm"));

                        break;
                    case "int":
                        basicTheory = new BasicTheory("Teoria del int",null,new URI(""));
                        break;
                    case "void":
                        basicTheory = new BasicTheory("Teoria del void",null,new URI(""));

                        break;
                    case "main":
                        basicTheory = new BasicTheory("Teoria del main",null,new URI(""));

                        break;
                    case "do":
                        basicTheory = new BasicTheory("Teoria del do",null,new URI(""));

                        break;
                    case "while":
                        basicTheory = new BasicTheory("Teoria del while",null,new URI(""));

                        break;
                    case "return":
                        basicTheory = new BasicTheory("Teoria del return",null,new URI(""));

                        break;
                    case "for":
                        basicTheory = new BasicTheory("Teoria del for",null,new URI(""));

                        break;
                    case "include":
                        basicTheory = new BasicTheory("Teoria del include",null,new URI(""));

                        break;
                    case "define":
                        basicTheory = new BasicTheory("Teoria del define",null,new URI(""));

                        break;
                    case "malloc":
                        basicTheory = new BasicTheory("Teoria del define",null,new URI(""));

                        break;
                    case "free":
                        basicTheory = new BasicTheory("Teoria del define",null,new URI(""));

                        break;
                    case "sizeof":
                        basicTheory = new BasicTheory("Teoria del define",null,new URI(""));

                        break;
                        default:
                            basicTheory = new BasicTheory("",null,new URI(""));
                }

            } catch (URISyntaxException e) {
                e.printStackTrace();
                basicTheory = new BasicTheory();

            }
            try {

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../Interface/teoria.fxml"));

                root = loader.load();

                Teoria teoria = (Teoria) loader.getController();
                teoria.setTheory(basicTheory);

                Stage stage = new Stage();
                stage.setResizable(false);
                stage.setTitle("Theory about C");
                stage.setScene(new Scene(root));
                //stage.initStyle(StageStyle.UNDECORATED);
                stage.show();
                // Hide this current window (if this is what you want)
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        codeArea.setStyleSpans(0, highlighting);
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        String styleClass = "nothing";
        while(matcher.find()) {
            styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            null; /* never happens */ assert styleClass != null;

            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    public void setInterpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    @FXML
    public void nextLine() {
        if(getCurrentTextfile() != null && !this.interpreter.needValue()) {
            this.interpreter.analiseNextLine();

            tableView.getItems().remove(0, tableView.getItems().size());
            dynamicTableView.getItems().remove(0, dynamicTableView.getItems().size());

            tableView.setItems(interpreter.convertToMemoryData());
            tableView.refresh();

            dynamicTableView.setItems(interpreter.getDynamicMemoryRows());
            dynamicTableView.refresh();

            this.from = 0;
            for (int i = 0; i < this.interpreter.getNumLines() - 1; i++) {
                this.from += Arrays.asList(codeArea.getText().split("\n")).get(i).length() + 1;
            }

            int to = Arrays.asList(codeArea.getText().split("\n")).get(this.interpreter.getNumLines() - 1).length() + 1;

            from = from + to;

            IntFunction<Node> numberFactory = LineNumberFactory.get(codeArea);
            IntFunction<Node> arrowFactory = new ArrowFactory(codeArea.currentParagraphProperty());
            IntFunction<Node> graphicFactory = line -> {
                HBox hbox = new HBox(
                        numberFactory.apply(line),
                        arrowFactory.apply(line));
                hbox.setAlignment(Pos.CENTER_LEFT);
                return hbox;
            };
            codeArea.setParagraphGraphicFactory(graphicFactory);
          //  codeArea.replaceText("The green arrow will only be on the line where the caret appears.\n\nTry it.");
            codeArea.moveTo(this.interpreter.getNumLines()-1, 0);


            this.numLines.setText("Line: " + this.interpreter.getNumLines());
        }
    }

    @FXML
    private void onSave() {
        TextFile textFile = new TextFile(this.currentTextfile.getFile(), Arrays.asList(codeArea.getText().split("\n")));
        editor.save(textFile);
    }

    @FXML
    private void onLoad() {
        this.tableView.getItems().remove(0, this.tableView.getItems().size());
        this.dynamicTableView.getItems().remove(0, this.dynamicTableView.getItems().size());
        this.interpreter.eraseAllData();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        File file = fileChooser.showOpenDialog(null);
        String code;
        load(file);
        this.from = 0;

    }

    private void load(File file){
        if (file != null) {
            this.currentTextfile = editor.load(file.toPath());
            if (this.currentTextfile != null) {
                this.codeArea.clear();
                this.currentTextfile.getContent().forEach(line -> this.codeArea.appendText(line + "\n"));
                this.interpreter.setNewFile(file);
            } else {
                System.out.println("Error loading codeArea!");
            }
        }
    }

    @FXML
    private void onClose() {
        cleanupWhenDone.unsubscribe();
        editor.close();
    }

    @FXML
    private void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("About");
        alert.setContentText("Program created to help those who strugle with memory :)");
        alert.show();
    }

    @FXML
    private void restart() {
        this.tableView.getItems().remove(0, this.tableView.getItems().size());
        this.dynamicTableView.getItems().remove(0, this.dynamicTableView.getItems().size());
        this.interpreter.eraseAllData();
        this.interpreter.restart();
        this.from = 0;
    }

    @FXML
    private void theoryMenuItem(ActionEvent e){
        detectInstruction(((MenuItem)e.getSource()).getText());
    }

    public void shutdown() {
        cleanupWhenDone.unsubscribe();
    }

    public void addTerminalText(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\\') {
                i++;
                if (text.charAt(i) == 'n') {
                    this.terminal.appendText("\n");
                } else {
                    this.terminal.appendText("\\" + text.charAt(i));
                }
            } else {
                this.terminal.appendText(String.valueOf(text.charAt(i)));
            }
        }

    }


    public CodeArea getCodeArea() {
        return codeArea;
    }

    public TextFile getCurrentTextfile() {
        return currentTextfile;
    }

    public void setCurrentTextfile(TextFile currentTextfile) {
        this.currentTextfile = currentTextfile;
    }

    private void SaveFile(String content, File file){
        try {
            FileWriter fileWriter;

            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }



}
