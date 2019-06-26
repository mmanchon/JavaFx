package Interpreter;

import FileReader.Reader;
import GUI.MemoryRow;
import SymbolTable.SymbolTable;
import SymbolTable.Node;
import SymbolTable.Variable;
import Tokens.Token;
import Tokens.Type;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Interpreter {

    private Reader reader;

    private Headers headers;
    private Declarations declarations;

    private SymbolTable symbolTable = new SymbolTable();
    private ObservableList<MemoryRow> memoryRows;

    private Token token = new Token();

    public Interpreter (String filename){
        this.reader = new Reader(filename);
        this.headers = new Headers(reader,symbolTable);
        this.declarations = new Declarations(reader,symbolTable);
        this.symbolTable.addNode(new Node());
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
        System.out.println(this.token.getStringToken());
        this.token = this.reader.extractToken();
        this.token = this.declarations.readDeclarations(token,symbolTable);
        if(token.getId() != Type.EOF && token.getId() == Type.LINE_BREAK){
//            token = this.reader.extractToken();
        }
        System.out.println(this.token.getStringToken());

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
            this.memoryRows.add(new MemoryRow(variable.getName(),variable.getType().getValue().toString(),variable.getType().getName(),'@' + String.valueOf(offset + variable.getType().getSize())));
            offset+=variable.getType().getSize();
        }

        return this.memoryRows;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
