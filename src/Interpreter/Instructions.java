package Interpreter;

import Interpreter.FileReader.Reader;
import Interpreter.SymbolTable.*;
import Interpreter.Tokens.Token;
import Interpreter.Tokens.Type;
import com.sun.deploy.security.ValidationState;

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


        if(token.getId() == Type.ID){

            token = this.variable(token);

        }else if(token.getId() == Type.FREE){

            token = this.freeMemoryVariable();

        }else if(token.getId() == Type.AND){

        }


        return token;
    }


    private Token freeMemoryVariable(){

        Token token = this.reader.extractToken();
        token = this.reader.extractToken();

        if(token.getId() == Type.ID || token.getId() == Type.ID_POINTER) {

            Variable variable = this.symbolTable.getNode(this.symbolTable.getActualNode()).getVariable(token.getLexema());
            PointerVariable pointerVariable = (PointerVariable) variable.getType();
            pointerVariable.setHasMemory(false);
            pointerVariable.removeAllElements();
            pointerVariable.setValue("null");
            variable.setType(pointerVariable);
            this.symbolTable.getNode(this.symbolTable.getActualNode()).addVariable(variable);

        }
        token = this.reader.extractToken();
        token = this.reader.extractToken();
        token = this.reader.extractToken();

        return token;
    }

    private Token variable(Token token){
        Variable variable;

        variable = this.symbolTable.getNode(this.symbolTable.getActualNode()).getVariable(token.getLexema());
        token = this.reader.extractToken();

        if(token.getId() == Type.EQUAL){

            token = this.assignment(variable);

        }else if( token.getId() == Type.OPEN_BRA){

            token = this.arrayAssignment(variable);

        }else if(token.getId() == Type.PLUS){
            token = this.reader.extractToken();

            if(token.getId() == Type.PLUS) {
                this.incrementVariableValue(variable);
            }

            token = this.reader.extractToken();


        }

        token = this.reader.extractToken();

        return token;
    }

    private void incrementVariableValue(Variable variable){
        if(variable.getType() instanceof PointerVariable){
            PointerVariable pointerVariable = (PointerVariable) variable.getType();
            pointerVariable.setValue(Integer.valueOf((pointerVariable.getValue().toString()).split("@")[0])+4);
            variable.setType(pointerVariable);
        }else{
            ITypes iTypes = variable.getType();
            iTypes.setValue((Integer.valueOf(iTypes.getValue().toString())+1));
            variable.setType(iTypes);
        }

        this.symbolTable.getNode(this.symbolTable.getActualNode()).addVariable(variable);

    }

    private Token assignment(Variable variable){
        ITypes iTypes;

        Token token = this.reader.extractToken();

        if(token.getId() == Type.INT_CNST){

            iTypes = variable.getType();
            iTypes.setValue(token.getLexema());

            variable.setType(iTypes);

        }else if(token.getId() == Type.MALLOC){

            token = this.askMemory(token,variable);

        }else if(token.getId() == Type.AND){

            token = this.reader.extractToken();

            if(token.getId() == Type.ID || token.getId() == Type.ID_POINTER){
                //Variable aux = this.symbolTable.getNode(this.symbolTable.getActualNode()).getVariable(token.getLexema());
                iTypes = variable.getType();
                iTypes.setValue(this.symbolTable.getNode(this.symbolTable.getActualNode()).getVariable(token.getLexema()).getType().getOffset());
                variable.setType(iTypes);
            }

        }else if(token.getId() == Type.ID){
            iTypes = variable.getType();
            iTypes.setValue(this.symbolTable.getNode(this.symbolTable.getActualNode()).getVariable(token.getLexema()).getType().getValue());

            variable.setType(iTypes);
        }

        this.symbolTable.getNode(this.symbolTable.getActualNode()).addVariable(variable);

        token = this.reader.extractToken();

        return token;
    }

    private Token askMemory(Token token, Variable variable){
        PointerVariable pointerVariable;

        while (token.getId() != Type.INT_CNST){
            token = this.reader.extractToken();

        }

        pointerVariable = new PointerVariable(variable.getName(),"int_pointer",variable.getType().getSize(),(new Random()).nextInt(10000000),this.symbolTable.getDynamicOffset(),0,new Integer(token.getLexema()), true);
        pointerVariable.setOffset(variable.getType().getOffset());
        this.symbolTable.setDynamicOffset(this.symbolTable.getDynamicOffset()+(variable.getType().getSize()*(new Integer(token.getLexema()))));
        variable.setType(pointerVariable);

        token = this.reader.extractToken();

        return token;
    }

    private Token arrayAssignment(Variable variable){
        ArrayType arrayType;

        Token token = this.reader.extractToken();
        int index;

        if(token.getId() == Type.ID){
            index = Integer.valueOf(this.symbolTable.getNode(this.symbolTable.getActualNode()).getVariable(token.getLexema()).getType().getValue().toString());
        }else{
            index = Integer.valueOf(token.getLexema());
        }


        token = this.reader.extractToken();
        token = this.reader.extractToken();
        token = this.reader.extractToken();

        arrayType = (ArrayType) variable.getType();

        if (token.getId() == Type.INT_CNST){
            arrayType.setElement(index, token.getLexema());
        }else if(token.getId() == Type.ID){
            arrayType.setElement(index, this.symbolTable.getNode(this.symbolTable.getActualNode()).getVariable(token.getLexema()).getType().getValue());
        }

        this.symbolTable.getNode(this.symbolTable.getActualNode()).addVariable(variable);

        token = this.reader.extractToken();

        return token;
    }
}
