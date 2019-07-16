package Interpreter;

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

import java.io.File;
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

    public Interpreter (){
        this.reader = new Reader();
        this.symbolTable.addNode(new Node());

        this.headers = new Headers(reader,symbolTable);
        this.declarations = new Declarations(reader,symbolTable);
        this.instructions = new Instructions(reader,symbolTable);
    }

    public void setNewFile(File file){
        this.reader.openNewFile(file.getAbsolutePath());
        token = this.headers.readFunctionsHeaders(token,symbolTable);
        token = this.headers.readMainHeader(token,symbolTable);

    }


    public void analiseNextLine(){

        while(token.getId() == Type.LINE_BREAK){
            token = this.reader.extractToken();
        }

        if(this.token.getId() == Type.CLOSE_KEY){

            if(!this.symbolTable.getNode(this.symbolTable.getActualNode()).hasContext()){
                Context context = this.symbolTable.getNode(this.symbolTable.getActualNode()).getLastContext();

                if(context.getTypeOfContext() == Type.IF ) {
                    this.token = this.reader.extractToken();

                    if(token.getId() == Type.ELSE){
                        while(token.getId() == Type.CLOSE_KEY){
                            token = this.reader.extractToken();
                        }
                    }

                    this.symbolTable.getNode(this.symbolTable.getActualNode()).removeLastContext();
                }else if(context.getTypeOfContext() == Type.ELSE){
                    this.symbolTable.getNode(this.symbolTable.getActualNode()).removeLastContext();

                }else if(context.getTypeOfContext() == Type.DO){

                }else if(context.getTypeOfContext() == Type.WHILE){

                }else if(context.getTypeOfContext() == Type.FOR){
                    Loop loop = (Loop)context.getLastCondition();

                    Variable variable = loop.getIncrementVariable();
                    ITypes iTypes = variable.getType();
                    iTypes.setValue(String.valueOf(Integer.valueOf(iTypes.getValue().toString()) + Integer.valueOf(loop.getIncrementValue().toString())));
                    variable.setType(iTypes);
                    this.symbolTable.getNode(this.symbolTable.getActualNode()).addVariable(variable);

                    if(loop.analyseOperand()){
                        this.reader.goToLine(context.getLineNumber());
                    }else{
                        this.symbolTable.getNode(this.symbolTable.getActualNode()).removeLastContext();
                    }

                }
                this.token = this.reader.extractToken();

                while(token.getId() == Type.LINE_BREAK){
                    token = this.reader.extractToken();
                }
            }else if(this.symbolTable.getActualNode() > 0){
                
                this.reader.goToLine(this.symbolTable.getNode(this.symbolTable.getActualNode()).getReturnLine());
                this.symbolTable.getNode(this.symbolTable.getActualNode()).deleteAllData();
                this.symbolTable.setActualNode(this.symbolTable.getNode(this.symbolTable.getActualNode()).getReturnNode());
            }


        }

        this.token = this.declarations.readDeclarations(token,symbolTable);
        this.token = this.instructions.readInstruction(this.token,this.symbolTable);


    }

    public  ObservableList<MemoryRow> convertToMemoryData(){
        Variable variable;

        this.memoryRows.removeAll();
        this.dynamicMemoryRows.remove(0,this.dynamicMemoryRows.size());

        for(int j = 0; j < this.symbolTable.getNodeList().size(); j++) {
            Node node = this.symbolTable.getNode(j);

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
                    for (int k = 0; j < ((ArrayType) variable.getType()).getMaxPosition(); k++) {
                        variableArray = ((ArrayType) variable.getType()).getElement(k);
                        this.memoryRows.add(new MemoryRow(variableArray.getName(), (variableArray.getType().getValue()).toString(), variableArray.getType().getName(), "@" + variableArray.getType().getOffset()));
                    }

                } else {
                    this.memoryRows.add(new MemoryRow(variable.getName(), variable.getType().getValue().toString(), variable.getType().getName(), "@" + variable.getType().getOffset()));
                }
                //   }
            }
        }

        Comparator<MemoryRow> comparator = Comparator.comparing(MemoryRow::getOffsetInt);
        this.memoryRows.sort(comparator);
        this.dynamicMemoryRows.sort(comparator);

        return this.memoryRows;
    }

    private void updateDynamicMemoryRows(Variable variable){
        PointerVariable pointerVariable = (PointerVariable) variable.getType();
        Variable variableArray;

        for(int i = 0; i < pointerVariable.getMaxPosition(); i++){
            variableArray = ((ArrayType)variable.getType()).getElement(i);
            this.dynamicMemoryRows.add(new MemoryRow(variableArray.getName(),(variableArray.getType().getValue()).toString(),variableArray.getType().getName(),"@" + variableArray.getType().getOffset()));
        }

    }

    public ObservableList<MemoryRow> getDynamicMemoryRows(){
        return this.dynamicMemoryRows;
    }

    public void eraseAllData(){
        this.memoryRows.remove(0,this.memoryRows.size());
        this.dynamicMemoryRows.remove(0,this.dynamicMemoryRows.size());
        this.symbolTable.deleteAllData();

    }

    public void restart(){
        this.reader.restart();
    }

    public int getNumLines(){
        return this.reader.getNumLines();
    }


}
