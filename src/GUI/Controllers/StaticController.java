package GUI.Controllers;

import GUI.Models.Editor;
import GUI.Models.MemoryRow;
import GUI.Models.TextFile;
import Interpreter.Interpreter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StaticController {

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
    private Words words;
    private int from = 0;
    private ExecutorService executor;

    private static final String[] KEYWORDS = new String[] {
            "int","void","main","if"
    };

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
    private static final String sampleCode = String.join("\n", new String[] {
            "package com.example;",
            "",
            "import java.util.*;",
            "",
            "public class Foo extends Bar implements Baz {",
            "",
            "    /*",
            "     * multi-line comment",
            "     */",
            "    public static void main(String[] args) {",
            "        // single-line comment",
            "        for(String arg: args) {",
            "            if(arg.length() != 0)",
            "                System.out.println(arg);",
            "            else",
            "                System.err.println(\"Warning: empty string as argument\");",
            "        }",
            "    }",
            "",
            "}"
    });
    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public StaticController() {
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
                // this will run whenever text is changed
                System.out.println("OLD VALUE: " + oldValue);
                System.out.println("NEW VALUE: " + newValue);
            }
        });

        codeArea.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event arg0) {
                System.out.println("selected text:"
                        + codeArea.getSelectedText());
            }
        });
        executor = Executors.newSingleThreadExecutor();

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        Subscription cleanupWhenDone = codeArea.multiPlainChanges()
                .successionEnds(Duration.ofMillis(500))
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
        while(matcher.find()) {
            String styleClass =
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

        this.interpreter.analiseNextLine();

        tableView.getItems().remove(0, tableView.getItems().size());

        tableView.setItems(interpreter.convertToMemoryData());
        tableView.refresh();

        dynamicTableView.setItems(interpreter.getDynamicMemoryRows());
        dynamicTableView.refresh();

        this.from = 0;
        for (int i = 0; i < this.interpreter.getNumLines() - 1; i++) {
            this.from += Arrays.asList(codeArea.getText().split("\n")).get(i).length() + 1;
        }

        int to = Arrays.asList(codeArea.getText().split("\n")).get(this.interpreter.getNumLines() - 1).length() + 1;
       // this.codeArea.selectRange(from, from + to);

        from = from + to;
        this.numLines.setText("Line: " + this.interpreter.getNumLines());

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

        this.from = 0;
        //    for(int i = 0; i < this.interpreter.getNumLines(); i++){
        //        this.from += Arrays.asList(textArea.getText().split("\n")).get(i).length();
        //   }
    }

    @FXML
    private void onClose() {
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

}
