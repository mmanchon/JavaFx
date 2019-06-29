package Interpreter;

import FileReader.Reader;
import GUI.DynamicController;
import GUI.MemoryRow;
import SymbolTable.*;
import Tokens.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collections;
import java.util.Comparator;

public class Interpreter {

    private Reader reader;

    private Headers headers;
    private Declarations declarations;
    private Instructions instructions;

    private SymbolTable symbolTable = new SymbolTable();
    private ObservableList<MemoryRow> memoryRows = FXCollections.observableArrayList();
    private ObservableList<MemoryRow> dynamicMemoryRows = FXCollections.observableArrayList();

    private DynamicController dynamicController;
    private Token token = new Token();

    public Interpreter (String filename){
        this.reader = new Reader(filename);
        this.symbolTable.addNode(new Node());

        this.headers = new Headers(reader,symbolTable);
        this.declarations = new Declarations(reader,symbolTable);
        this.instructions = new Instructions(reader,symbolTable);

        token = this.headers.readMainHeader(token,symbolTable);

    }


    public void analiseNextLine(){

        while(token.getId() == Type.LINE_BREAK){
            token = this.reader.extractToken();
        }

        if(token.getId() == Type.ID)this.token = this.instructions.readInstruction(this.token,this.symbolTable);
        if(token.getId() == Type.INT) this.token = this.declarations.readDeclarations(token,symbolTable);


    }

    public  ObservableList<MemoryRow> convertToMemoryData(){
        Node node = this.symbolTable.getNode(this.symbolTable.getActualNode());
        Variable variable;

        this.memoryRows.removeAll();
        this.dynamicMemoryRows.remove(0,this.dynamicMemoryRows.size());

        for(int i = 0; i < node.getVariablesList().size(); i++){
            variable = (Variable) node.getVariablesList().values().toArray()[i];
           // if(!this.memoryRows.contains()){

                 if(variable.getType() instanceof  PointerVariable){

                     if(((PointerVariable) variable.getType()).isHasMemory()){
                         this.memoryRows.add(new MemoryRow(variable.getName(),"@"+((ArrayType)variable.getType()).getElement(0).getType().getOffset(),variable.getType().getName(),"@" + variable.getType().getOffset()));
                         this.updateDynamicMemoryRows(variable);
                     }else{
                         this.memoryRows.add(new MemoryRow(variable.getName(),variable.getType().getValue().toString(),variable.getType().getName(),"@" + variable.getType().getOffset()));
                     }

                }else if((variable.getType() instanceof ArrayType)){

                    Variable variableArray;
                     for(int j = 0; j < ((ArrayType)variable.getType()).getMaxPosition(); j++){
                         variableArray = ((ArrayType)variable.getType()).getElement(j);
                         this.memoryRows.add(new MemoryRow(variableArray.getName(),(variableArray.getType().getValue()).toString(),variableArray.getType().getName(),"@" + variableArray.getType().getOffset()));
                     }

                 }else{
                    this.memoryRows.add(new MemoryRow(variable.getName(),variable.getType().getValue().toString(),variable.getType().getName(),"@" + variable.getType().getOffset()));
                }
         //   }
        }

        Comparator<MemoryRow> comparator = Comparator.comparing(MemoryRow::getOffsetInt);
        this.memoryRows.sort(comparator);

        this.dynamicController.updateList(this.dynamicMemoryRows);

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
    public void setDynamicController(DynamicController dynamicController){
        this.dynamicController =  dynamicController;
    }

    public  ObservableList<MemoryRow> getDynamicMemoryRows(){
        return this.dynamicMemoryRows;
    }
}
