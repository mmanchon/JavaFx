package Interpreter;

import FileReader.Reader;
import SymbolTable.SymbolTable;
import Tokens.Token;
import Tokens.Type;

public class Headers {

    private Reader reader;
    private SymbolTable symbolTable;

    public Headers(Reader reader, SymbolTable symbolTable){
        this.reader = reader;
        this.symbolTable = symbolTable;
    }

    public SymbolTable readMainHeader(Token token, SymbolTable symbolTable){
        this.symbolTable = symbolTable;

        while(token.getId() != Type.OPEN_KEY){
            token = this.reader.extractToken();
        }

        token = this.reader.extractToken();
        return this.symbolTable;
    }
}
