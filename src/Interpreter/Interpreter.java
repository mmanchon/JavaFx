package Interpreter;

import GUI.Controllers.PopUpController;
import Interpreter.Errors.BasicError;
import Interpreter.FileReader.Reader;
import GUI.Models.MemoryRow;
import Interpreter.SymbolTable.*;
import Interpreter.SymbolTable.Contexts.Context;
import Interpreter.SymbolTable.Contexts.Loop;
import Interpreter.SymbolTable.Objects.PointerVariable;
import Interpreter.SymbolTable.Objects.Variable;
import Interpreter.SymbolTable.Types.ArrayType;
import Interpreter.SymbolTable.Types.ITypes;
import Interpreter.Tokens.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

public class Interpreter {

    private Reader reader;

    private Headers headers;
    private Declarations declarations;
    private Instructions instructions;

    private SymbolTable symbolTable = new SymbolTable();
    private ObservableList<MemoryRow> memoryRows = FXCollections.observableArrayList();
    private ObservableList<MemoryRow> dynamicMemoryRows = FXCollections.observableArrayList();

    private Token token = new Token();

    public Interpreter() {
        this.reader = new Reader();
        this.symbolTable.addNode(new Node());

        this.headers = new Headers(reader, symbolTable);
        this.declarations = new Declarations(reader, symbolTable);
        this.instructions = new Instructions(reader, symbolTable);
    }

    public void setNewFile(File file) {
        this.reader.openNewFile(file.getAbsolutePath());
        token = this.headers.readFunctionsHeaders(token, symbolTable);
        token = this.headers.readMainHeader(token, symbolTable);
        System.out.println(this.symbolTable.toString());
    }


    public void analiseNextLine() {

        while (token.getId() == Type.LINE_BREAK) {
            token = this.reader.extractToken();
        }

        try {

            if (this.token.getId() == Type.CLOSE_KEY) {

                if (!this.symbolTable.getNode(this.symbolTable.getActualNode()).hasContext()) {
                    Context context = this.symbolTable.getNode(this.symbolTable.getActualNode()).getLastContext();

                    if (context.getTypeOfContext() == Type.IF) {
                        this.token = this.reader.extractToken();

                        if (token.getId() == Type.ELSE) {
                            while (token.getId() == Type.CLOSE_KEY) {
                                token = this.reader.extractToken();
                            }
                        }

                        this.symbolTable.getNode(this.symbolTable.getActualNode()).removeLastContext();
                    } else if (context.getTypeOfContext() == Type.ELSE) {
                        this.symbolTable.getNode(this.symbolTable.getActualNode()).removeLastContext();

                    } else if (context.getTypeOfContext() == Type.DO) {

                    } else if (context.getTypeOfContext() == Type.WHILE) {

                    } else if (context.getTypeOfContext() == Type.FOR) {
                        Loop loop = (Loop) context.getLastCondition();

                        Variable variable = loop.getIncrementVariable();
                        ITypes iTypes = variable.getType();
                        iTypes.setValue(String.valueOf(Integer.valueOf(iTypes.getValue().toString()) + Integer.valueOf(loop.getIncrementValue().toString())));
                        variable.setType(iTypes);
                        this.symbolTable.getNode(this.symbolTable.getActualNode()).addVariable(variable);

                        if (loop.analyseOperand()) {
                            this.reader.goToLine(context.getLineNumber());
                        } else {
                            this.symbolTable.getNode(this.symbolTable.getActualNode()).removeLastContext();
                        }

                    }
                    this.token = this.reader.extractToken();

                    while (token.getId() == Type.LINE_BREAK) {
                        token = this.reader.extractToken();
                    }
                } else if (this.symbolTable.getActualNode() > 0) {

                    this.reader.goToLine(this.symbolTable.getNode(this.symbolTable.getActualNode()).getReturnLine());
                    this.symbolTable.getNode(this.symbolTable.getActualNode()).deleteAllData();
                    this.symbolTable.setActualNode(this.symbolTable.getNode(this.symbolTable.getActualNode()).getReturnNode());
                }


            }

            this.token = this.declarations.readDeclarations(token, symbolTable);
            this.token = this.instructions.readInstruction(this.token, this.symbolTable);

        } catch (BasicError basicError) {
            this.openPopUpView(basicError);
        }

    }

    public ObservableList<MemoryRow> convertToMemoryData() {
        Variable variable;

        this.memoryRows.removeAll();
        this.dynamicMemoryRows.remove(0, this.dynamicMemoryRows.size());
        this.memoryRows.remove(0, this.memoryRows.size());

        for (int j = 0; j < this.symbolTable.getNodeList().size(); j++) {
            Node node = this.symbolTable.getNode(j);

            if (this.symbolTable.getActualNode() == j && j > 0) {
                for (int i = 0; i < node.getArgumentList().size(); i++) {
                    variable = (Variable) node.getArgumentList().values().toArray()[i];
                    this.memoryRows.add(new MemoryRow(variable.getName(), variable.getType().getValue().toString(), variable.getType().getName(), "@" + variable.getType().getOffset()));
                }
            }

            for (int i = 0; i < node.getVariablesList().size(); i++) {
                variable = (Variable) node.getVariablesList().values().toArray()[i];
                // if(!this.memoryRows.contains()){

                if (variable.getType() instanceof PointerVariable) {

                    if (((PointerVariable) variable.getType()).isHasMemory()) {
                        this.memoryRows.add(new MemoryRow(variable.getName(), "@" + variable.getType().getValue(), variable.getType().getName(), "@" + variable.getType().getOffset()));
                        this.updateDynamicMemoryRows(variable);
                    } else {
                        this.memoryRows.add(new MemoryRow(variable.getName(), variable.getType().getValue().toString(), variable.getType().getName(), "@" + variable.getType().getOffset()));
                    }

                } else if ((variable.getType() instanceof ArrayType)) {

                    Variable variableArray;
                    for (int k = 0; k < ((ArrayType) variable.getType()).getMaxPosition(); k++) {
                        variableArray = ((ArrayType) variable.getType()).getElement(k);
                        this.memoryRows.add(new MemoryRow(variableArray.getName(), (variableArray.getType().getValue()).toString(), variableArray.getType().getName(), "@" + variableArray.getType().getOffset()));
                    }

                } else {
                    this.memoryRows.add(new MemoryRow(variable.getName(), variable.getType().getValue().toString(), variable.getType().getName(), "@" + variable.getType().getOffset()));
                }

            }
        }

        Comparator<MemoryRow> comparator = Comparator.comparing(MemoryRow::getOffsetInt);
        this.memoryRows.sort(comparator);
        this.dynamicMemoryRows.sort(comparator);

        return this.memoryRows;
    }

    private void updateDynamicMemoryRows(Variable variable) {
        PointerVariable pointerVariable = (PointerVariable) variable.getType();
        Variable variableArray;

        for (int i = 0; i < pointerVariable.getMaxPosition(); i++) {
            variableArray = ((ArrayType) variable.getType()).getElement(i);
            this.dynamicMemoryRows.add(new MemoryRow(variableArray.getName(), (variableArray.getType().getValue()).toString(), variableArray.getType().getName(), "@" + variableArray.getType().getOffset()));
        }

    }

    public ObservableList<MemoryRow> getDynamicMemoryRows() {
        return this.dynamicMemoryRows;
    }

    public void eraseAllData() {
        this.memoryRows.remove(0, this.memoryRows.size());
        this.dynamicMemoryRows.remove(0, this.dynamicMemoryRows.size());
        this.symbolTable.deleteAllData();

    }

    public void restart() {
        this.reader.restart();
    }

    public int getNumLines() {
        return this.reader.getNumLines();
    }


    private void openPopUpView(BasicError basicError){
        Parent root;
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../GUI/Interface/popup.fxml"));

            root = loader.load();

            PopUpController popUpController = (PopUpController) loader.getController();
            popUpController.setError(basicError);

            Stage stage = new Stage();

            stage.setTitle("Ups!");
            stage.setScene(new Scene(root));

            stage.show();
            // Hide this current window (if this is what you want)
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
