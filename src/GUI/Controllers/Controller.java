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
            "int","void","main","if","do","while","return","for","include","define","malloc","free","sizeof","else"
    };

    //TOERIA Y LINKS



    private final String TEORIA_IF = "The if statement evaluates the test expression inside the parenthesis ().\nIf the test expression is evaluated to true, statements inside the body of if are executed.\nIf the test expression is evaluated to false, statements inside the body of if are not executed.";
    private final String TEORIA_INT = "Data types in c refer to an extensive system used for declaring variables or functions of different types.\nThe type of a variable determines how much space it occupies in storage and how the bit pattern stored is interpreted.\nINT: 2 or 4 bytes\n\t-32,768 to 32,767 or -2,147,483,648 to 2,147,483,647";
    private final String TEORIA_VOID = "A function may return a value. The return_type is the data type of the value the function returns.\n Some functions perform the desired operations without returning a value.\n In this case, the return_type is the keyword void.";
    private final String TEORIA_MAIN = "All C language programs must have a main() function. It’s the core of every program.\n It’s required. The main() function doesn’t really have to do anything other than be present inside your C source code.\n Eventually, it contains instructions that tell the computer to carry out whatever task your program is designed to do.\n But it’s not officially required to do anything.";
    private final String TEORIA_DO = "Unlike for and while loops, which test the loop condition at the top of the loop, the do...while loop in C programming checks its condition at the bottom of the loop.\nA do...while loop is similar to a while loop, except the fact that it is guaranteed to execute at least one time.";
    private final String TEORIA_WHILE = "A while loop in C programming repeatedly executes a target statement as long as a given condition is true.";
    private final String TEORIA_RETURN = "The return statement terminates the execution of a function and returns control to the calling function.\n Execution resumes in the calling function at the point immediately following the call.\n A return statement can also return a value to the calling function.";
    private final String TEORIA_FOR = "A for loop is a repetition control structure that allows you to efficiently write a loop that needs to execute a specific number of times.";
    private final String TEORIA_INCLUDE = "A header file is a file with extension .h which contains C function declarations and macro definitions to be shared between several source files. There are two types of header files: the files that the programmer writes and the files that comes with your compiler.\n" +
            "\n" +
            "You request to use a header file in your program by including it with the C preprocessing directive #include, like you have seen inclusion of stdio.h header file, which comes along with your compiler.\n" +
            "\n" +
            "Including a header file is equal to copying the content of the header file but we do not do it because it will be error-prone and it is not a good idea to copy the content of a header file in the source files, especially if we have multiple source files in a program.";
    private final String TEORIA_DEFINE = "The C Preprocessor is not a part of the compiler, but is a separate step in the compilation process. In simple terms, a C Preprocessor is just a text substitution tool and it instructs the compiler to do required pre-processing before the actual compilation. We'll refer to the C Preprocessor as CPP.\n" +
            "\n" +
            "All preprocessor commands begin with a hash symbol (#). It must be the first nonblank character, and for readability, a preprocessor directive should begin in the first column.";
    private final String TEORIA_MALLOC = "The C library function void *malloc(size_t size) allocates the requested memory and returns a pointer to it.";
    private final String TEORIA_FREE = "The C library function void free(void *ptr) deallocates the memory previously allocated by a call to calloc, malloc, or realloc.";
    private final String TEORIA_SIZEOF = "When sizeof() is used with the data types, it simply returns the amount of memory allocated to that data type.\n The output can be different on different machines like a 32-bit system can show different output while a 64-bit system can show different of same data types.";








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
                        basicTheory = new BasicTheory(TEORIA_INT,new Image("Resources/img/int.jpg"),new URI("https://www.tutorialspoint.com/cprogramming/c_data_types.htm"));
                        break;
                    case "void":
                        basicTheory = new BasicTheory(TEORIA_VOID,null,new URI("https://www.tutorialspoint.com/cprogramming/c_functions.htm"));

                        break;
                    case "main":
                        basicTheory = new BasicTheory(TEORIA_MAIN,null,new URI("https://www.dummies.com/programming/c/the-importance-of-the-main-function-in-c-programming/"));

                        break;
                    case "do":
                        basicTheory = new BasicTheory(TEORIA_DO,new Image("Resources/img/do.jpg"),new URI("https://www.tutorialspoint.com/cprogramming/c_do_while_loop.htm"));

                        break;
                    case "while":
                        basicTheory = new BasicTheory(TEORIA_WHILE,new Image("Resources/img/while.jpg"),new URI("https://www.tutorialspoint.com/cprogramming/c_while_loop.htm"));

                        break;
                    case "return":
                        basicTheory = new BasicTheory(TEORIA_RETURN,null,new URI("https://docs.microsoft.com/en-us/cpp/c-language/return-statement-c?view=vs-2019"));

                        break;
                    case "for":
                        basicTheory = new BasicTheory(TEORIA_FOR,new Image("Resources/img/for.jpg"),new URI("https://www.tutorialspoint.com/cprogramming/c_for_loop.htm"));

                        break;
                    case "include":
                        basicTheory = new BasicTheory(TEORIA_INCLUDE,null,new URI("https://www.tutorialspoint.com/cprogramming/c_header_files.htm"));

                        break;
                    case "define":
                        basicTheory = new BasicTheory(TEORIA_DEFINE,null,new URI("https://www.tutorialspoint.com/cprogramming/c_preprocessors.htm"));

                        break;
                    case "malloc":
                        basicTheory = new BasicTheory(TEORIA_MALLOC,new Image("Resources/img/malloc.JPG"),new URI("https://www.tutorialspoint.com/c_standard_library/c_function_malloc.htm"));

                        break;
                    case "free":
                        basicTheory = new BasicTheory(TEORIA_FREE,new Image("Resources/img/free.JPG"),new URI("https://www.tutorialspoint.com/c_standard_library/c_function_free.htm"));

                        break;
                    case "sizeof":
                        basicTheory = new BasicTheory(TEORIA_SIZEOF,new Image("Resources/img/sizeof.JPG"),new URI("https://www.tutorialspoint.com/sizeof-operator-in-c"));

                        break;
                    case "else":
                        basicTheory = new BasicTheory(TEORIA_IF,new Image("Resources/img/if-theory.jpg"),new URI("https://www.tutorialspoint.com/cprogramming/if_else_statement_in_c.htm"));

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
