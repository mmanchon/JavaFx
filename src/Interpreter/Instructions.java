package Interpreter;

import FileReader.Reader;
import SymbolTable.*;
import Tokens.Token;
import Tokens.Type;

import java.util.Random;

public class Instructions {

    private Reader reader;
    private SymbolTable symbolTable;

    public Instructions(Reader reader, SymbolTable symbolTable){
        this.reader = reader;
        this.symbolTable = symbolTable;
    }

    public Token readInstruction(Token token, SymbolTable symbolTable){
        this.symbolTable = symbolTable;
        Variable variable;
        ITypes iTypes;
        ArrayType arrayType;
        PointerVariable pointerVariable;

        if(token.getId() == Type.ID){

            variable = this.symbolTable.getNode(this.symbolTable.getActualNode()).getVariable(token.getLexema());
            token = this.reader.extractToken();

            if(token.getId() == Type.EQUAL){
                token = this.reader.extractToken();

                if(token.getId() == Type.INT_CNST){

                    iTypes = variable.getType();
                    iTypes.setValue(token.getLexema());

                    variable.setType(iTypes);

                }else if(token.getId() == Type.MALLOC){

                    while (token.getId() != Type.INT_CNST){
                        token = this.reader.extractToken();

                    }

                    pointerVariable = new PointerVariable(variable.getName(),"int_pointer",variable.getType().getSize(),(new Random()).nextInt(10000000),this.symbolTable.getDynamicOffset(),0,new Integer(token.getLexema()), true);
                    pointerVariable.setOffset(variable.getType().getOffset());
                    this.symbolTable.setDynamicOffset(this.symbolTable.getDynamicOffset()+(variable.getType().getSize()*(new Integer(token.getLexema()))));
                    variable.setType(pointerVariable);

                    token = this.reader.extractToken();

                }

                this.symbolTable.getNode(this.symbolTable.getActualNode()).addVariable(variable);

                token = this.reader.extractToken();

            }else if( token.getId() == Type.OPEN_BRA){

                token = this.reader.extractToken();

                int index = Integer.valueOf(token.getLexema());

                token = this.reader.extractToken();
                token = this.reader.extractToken();
                token = this.reader.extractToken();

                arrayType = (ArrayType) variable.getType();

                if (token.getId() == Type.INT_CNST){
                    arrayType.setElement(index,token.getLexema());
                }

                this.symbolTable.getNode(this.symbolTable.getActualNode()).addVariable(variable);

                token = this.reader.extractToken();

            }

            token = this.reader.extractToken();

        }


        return token;
    }
}
