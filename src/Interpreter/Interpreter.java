package Interpreter;

import FileReader.Reader;
import GUI.DynamicController;
import GUI.MemoryRow;
import SymbolTable.*;
import Tokens.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Interpreter {

    private Reader reader;

    private Headers headers;
    private Declarations declarations;
    private Instructions instructions;

    private SymbolTable symbolTable = new SymbolTable();
    private ObservableList<MemoryRow> memoryRows;
    private DynamicController dynamicController;
    private Token token = new Token();

    public Interpreter (String filename){
        this.reader = new Reader(filename);
        this.symbolTable.addNode(new Node());

        this.headers = new Headers(reader,symbolTable);
        this.declarations = new Declarations(reader,symbolTable);
        this.declarations = new Declarations(reader,symbolTable);
        this.instructions = new Instructions(reader,symbolTable);

        token = this.headers.readMainHeader(token,symbolTable);
        this.memoryRows = FXCollections.observableArrayList();

    }

    public SymbolTable start(){
  /*      Token token;

        token = this.reader.extractToken();


        this.symbolTable = this.headers.readMainHeader(token,symbolTable);
        this.symbolTable = this.declarations.readDeclarations(token,symbolTable);


        while (token.getId() != Type.EOF){
          // System.out.println(token.getStringToken());
           token = this.reader.extractToken();
        }

      GUI gui = new GUI();
         gui.start(GUI.classStage);
         Application.launch(GUI.class, args);
*/
        this.reader.closeFile();

        return this.symbolTable;

    }

    public void analiseNextLine(){
        //if(this.token.getId() != Type.LINE_BREAK)this.token = this.reader.extractToken();
        while(token.getId() == Type.LINE_BREAK){
            token = this.reader.extractToken();
        }

      //  System.out.println("Antes" + this.token.getStringToken());
        if(token.getId() == Type.ID)this.token = this.instructions.readInstruction(this.token,this.symbolTable);
        if(token.getId() == Type.INT) this.token = this.declarations.readDeclarations(token,symbolTable);

    //    System.out.println("Middle" + this.token.getStringToken());

//        if(token.getId() != Type.EOF){
         //   if(this.token.getId() != Type.LINE_BREAK) token = this.reader.extractToken();
  //      }
        //System.out.println("Despues" + this.token.getStringToken());

        /*while (token.getId() != Type.EOF){
            // System.out.println(token.getStringToken());
            token = this.reader.extractToken();
        }*/

    }

    public  ObservableList<MemoryRow> convertToMemoryData(){
        Node node = this.symbolTable.getNode(this.symbolTable.getActualNode());
        Variable variable;
        int offset = -4;
        this.memoryRows.removeAll();

        for(int i = 0; i < node.getVariablesList().size(); i++){
            variable = (Variable) node.getVariablesList().values().toArray()[i];
             if(variable.getType() instanceof  PointerVariable){

                 if(((PointerVariable) variable.getType()).isHasMemory()){
                     offset+=variable.getType().getSize();
                     this.memoryRows.add(new MemoryRow(variable.getName(),"@"+(offset+variable.getType().getSize()),variable.getType().getName(),'@' + String.valueOf(offset)));
                     this.dynamicController.updateRows(variable, offset);
                 }else{
                     offset+=variable.getType().getSize();
                     this.memoryRows.add(new MemoryRow(variable.getName(),variable.getType().getValue().toString(),variable.getType().getName(),'@' + String.valueOf(offset )));
                 }

            }else if((variable.getType() instanceof ArrayType)){

                 for(int j = 0; j < ((ArrayType)variable.getType()).getMaxPosition(); j++){
                     offset+=variable.getType().getSize();
                     this.memoryRows.add(new MemoryRow(variable.getName()+"["+j+"]",((ArrayType)variable.getType()).getElement(j).toString(),variable.getType().getName(),'@' + String.valueOf(offset )));
                 }

             }else{
                 offset+=variable.getType().getSize();
                this.memoryRows.add(new MemoryRow(variable.getName(),variable.getType().getValue().toString(),variable.getType().getName(),'@' + String.valueOf(offset)));
            }

        }

        return this.memoryRows;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public void setDynamicController(DynamicController dynamicController){
        this.dynamicController =  dynamicController;
    }
}
