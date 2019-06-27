package Interpreter;

import FileReader.Reader;
import SymbolTable.*;

import Tokens.Token;
import Tokens.Type;

public class Declarations {

    private Reader reader;
    private SymbolTable symbolTable;

    public Declarations(Reader reader, SymbolTable symbolTable){
        this.reader = reader;
        this.symbolTable = symbolTable;
    }

    public Token readDeclarations(Token token, SymbolTable symbolTable){
        this.symbolTable = symbolTable;
        Variable variable;
        ITypes iTypes;
        ArrayType arrayType;

        if(token.getId() == Type.INT){
            while(token.getId() != Type.SEMICOLON){
                variable = new Variable();
                token = this.reader.extractToken();
                if(token.getId() == Type.ID_POINTER){

                    iTypes = new PointerVariable();
                    iTypes.setSize(4);
                    iTypes.setName("int_pointer");

                }else{
                    iTypes = new ITypes("int",4,0);
                }
                variable.setName(token.getLexema());

                token = this.reader.extractToken();

                //Si es una asignacion de variable
                if(token.getId() == Type.EQUAL || token.getId() == Type.COMMA || token.getId() == Type.SEMICOLON) {
                    variable.setType(iTypes);

                    if (token.getId() != Type.COMMA && token.getId() != Type.SEMICOLON) {
                        if (token.getId() == Type.EQUAL) {
                            token = this.reader.extractToken();

                            iTypes = variable.getType();
                            iTypes.setValue(token.getLexema());

                            variable.setType(iTypes);
                        }

                        token = this.reader.extractToken();
                    }

                    //Si accedemos a un array
                }else if( token.getId() == Type.OPEN_BRA){
                    token = this.reader.extractToken();

                    arrayType = new ArrayType(0,new Integer(token.getLexema()));
                    arrayType.setName("int_array");
                    arrayType.setSize(4);
                    variable.setType(arrayType);
                    token = this.reader.extractToken();
                    token = this.reader.extractToken();

                }

                this.symbolTable.getNode(this.symbolTable.getActualNode()).addVariable(variable);

            }
            token = this.reader.extractToken();

        }




        return token;
    }
}
