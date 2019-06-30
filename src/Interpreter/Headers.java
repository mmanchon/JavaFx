package Interpreter;

import Interpreter.FileReader.Reader;
import Interpreter.SymbolTable.SymbolTable;
import Interpreter.Tokens.Token;
import Interpreter.Tokens.Type;

public class Headers {

    private Reader reader;
    private SymbolTable symbolTable;
    private int numLine = 0;
    public Headers(Reader reader, SymbolTable symbolTable){
        this.reader = reader;
        this.symbolTable = symbolTable;
    }

    public Token readMainHeader(Token token, SymbolTable symbolTable){
        this.symbolTable = symbolTable;

        while(token.getId() != Type.OPEN_KEY){
            token = this.reader.extractToken();
        }

        token = this.reader.extractToken();
        return token;
    }

    public int getNumLine(){
        return this.numLine;
    }
}
