package Interpreter;

import Interpreter.FileReader.Reader;
import Interpreter.SymbolTable.Node;
import Interpreter.SymbolTable.Objects.PointerVariable;
import Interpreter.SymbolTable.Objects.Variable;
import Interpreter.SymbolTable.SymbolTable;
import Interpreter.SymbolTable.Types.ArrayType;
import Interpreter.SymbolTable.Types.ITypes;
import Interpreter.Tokens.Token;
import Interpreter.Tokens.Type;

import java.util.Random;

public class Headers {

    private Reader reader;
    private SymbolTable symbolTable;
    private int numLine = 0;
    public Headers(Reader reader, SymbolTable symbolTable){
        this.reader = reader;
        this.symbolTable = symbolTable;
    }

    public Token readFunctionsHeaders(Token token, SymbolTable symbolTable){
        this.symbolTable = symbolTable;
        Node node = new Node();

        while(token.getId() != Type.INT && token.getId() != Type.VOID){
            token = this.reader.extractToken();
        }

        if(token.getId() == Type.INT){
            node.setReturnType(Type.INT);
        }else{
            node.setReturnType(Type.VOID);
        }
        token = this.reader.extractToken();

        if(token.getId() != Type.MAIN){
            token = this.extractFunction(token, node);
        }

        return token;
    }

    public Token readMainHeader(Token token, SymbolTable symbolTable){
        this.symbolTable = symbolTable;

        while(token.getId() != Type.MAIN){
            token = this.reader.extractToken();
        }



        token = this.reader.extractToken();
        token = this.reader.extractToken();
        token = this.reader.extractToken();
        token = this.reader.extractToken();

        this.symbolTable.setActualNode(0);
        this.symbolTable.getNode(this.symbolTable.getActualNode()).setNodeName("main");
        //this.symbolTable.getNode(this.symbolTable.getActualNode()).
        return token;
    }

    public int getNumLine(){
        return this.numLine;
    }

    public Token extractFunction(Token token, Node node){

        Variable variable;
        Token aux;

        node.setNodeName(token.getLexema()); // nuevo nodo donde guardamos la funcion

        token = this.reader.extractToken();
        token = this.reader.extractToken();

        this.symbolTable.setActualNode(this.symbolTable.getActualNode()+1);

        while(token.getId() != Type.CLOSE_PAR){

            variable = new Variable();

            if(token.getId() == Type.INT){

                token = this.reader.extractToken();
                aux = token;

                token = this.reader.extractToken();

                if(token.getId() == Type.OPEN_BRA){
                    //TODO: Hacer arrays sin medida
                    token = this.reader.extractToken();
                    //Number

                    variable.setType(this.createIntArgument(variable.getName(), "int_array", this.symbolTable.getStaticOffset(), 4, 0, new Integer(token.getLexema())));

                    token = this.reader.extractToken();
                    //close bra
                    token = this.reader.extractToken();
                }else if(aux.getId() == Type.ID_POINTER) {
                    variable.setName(aux.getLexema());
                    variable.setType(this.createIntArgument(token.getLexema(), "int_pointer", this.symbolTable.getStaticOffset() + 4, 4, 0, 0));

                } else if(aux.getId() == Type.ID){
                    variable.setName(aux.getLexema());
                    variable.setType(this.createIntArgument(token.getLexema(), "int", this.symbolTable.getStaticOffset() + 4, 4, 0, 0));

                }

               node.addArgument(variable);
            }

            if(token.getId() != Type.CLOSE_PAR) token = this.reader.extractToken();
        }

        token = this.reader.extractToken();
        token = this.reader.extractToken();

        node.setNodeLine(this.reader.getNumLines());

        while(token.getId() != Type.CLOSE_KEY){
            token = this.reader.extractToken();
        }

        this.symbolTable.addNode(node);

        token = this.readFunctionsHeaders(token,this.symbolTable);

        return token;
    }


    private ITypes createIntArgument(String varName, String typeName, int offset, int size, Object value, int maxLength) {
        if (typeName.equals("int")) {

            return new ITypes(typeName, size, value, offset);

        } else if (typeName.equals("int_pointer")) {

            ITypes iTypes = new PointerVariable();
            iTypes.setSize(4);
            iTypes.setValue(value);
            iTypes.setName("int_pointer");

            return iTypes;

        } else if (typeName.equals("int_array")) {
            ArrayType arrayType = new ArrayType(varName, typeName, size, value, offset, maxLength, 0);
            return arrayType;
        }

        return new ITypes();
    }

}