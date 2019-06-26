package Interpreter;

import FileReader.Reader;
import SymbolTable.SymbolTable;
import SymbolTable.ITypes;
import SymbolTable.Variable;
import Tokens.Token;
import Tokens.Type;

public class Declarations {

    private Reader reader;
    private SymbolTable symbolTable;

    public Declarations(Reader reader, SymbolTable symbolTable){
        this.reader = reader;
        this.symbolTable = symbolTable;
    }

    public SymbolTable readDeclarations(Token token, SymbolTable symbolTable){
        this.symbolTable = symbolTable;
        Variable variable;
        ITypes iTypes;

        while(token.getId() == Type.INT){
            while(token.getId() != Type.SEMICOLON){
                variable = new Variable();
                token = this.reader.extractToken();

                variable.setName(token.getLexema());
                System.out.println(token.getStringToken());
                variable.setType(new ITypes("int",4,0));

                token = this.reader.extractToken();

                if(token.getId() != Type.COMMA){
                    if(token.getId() == Type.EQUAL){
                        token = this.reader.extractToken();

                        iTypes = variable.getType();
                        iTypes.setValue(token.getLexema());

                        variable.setType(iTypes);
                    }

                    token = this.reader.extractToken();
                }

                this.symbolTable.getNode(this.symbolTable.getActualNode()).addVariable(variable);

            }
            token = this.reader.extractToken();

        }




        return this.symbolTable;
    }
}
