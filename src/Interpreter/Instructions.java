package Interpreter;

import Interpreter.Errors.ArrayErrors.AccessError;
import Interpreter.Errors.BasicError;
import Interpreter.FileReader.Reader;
import Interpreter.SymbolTable.*;
import Interpreter.SymbolTable.Contexts.Condition;
import Interpreter.SymbolTable.Contexts.Context;
import Interpreter.SymbolTable.Contexts.Loop;
import Interpreter.SymbolTable.Objects.PointerVariable;
import Interpreter.SymbolTable.Objects.Variable;
import Interpreter.SymbolTable.Types.ArrayType;
import Interpreter.SymbolTable.Types.ITypes;
import Interpreter.Tokens.Token;
import Interpreter.Tokens.Type;

import java.util.Random;

public class Instructions {

    private Reader reader;
    private SymbolTable symbolTable;
    private String text;

    public Instructions(Reader reader, SymbolTable symbolTable) {
        this.reader = reader;
        this.symbolTable = symbolTable;
        this.text = "";
    }

    public Token readInstruction(Token token, SymbolTable symbolTable) throws BasicError {
        this.symbolTable = symbolTable;

        if (token.getId() == Type.ID) {

            token = this.variable(token);

        } else if (token.getId() == Type.FREE) {

            token = this.freeMemoryVariable();

        } else if (token.getId() == Type.AND) {

        } else if (token.getId() == Type.IF) {

            token = this.analyseIfSentence();

            if (!this.isTrueCondition()) {

                this.jumpIf(token);
                this.symbolTable.getNode(this.symbolTable.getActualNode()).removeLastContext();

                token = this.reader.extractToken();

                if (token.getId() == Type.ELSE) {
                    token = this.reader.extractToken();
                    token = this.reader.extractToken();
                    Context context = new Context();
                    context.setTypeOfContext(Type.ELSE);
                }

            }

        } else if (token.getId() == Type.DO) {

        } else if (token.getId() == Type.FOR) {

            token = this.analyseForSentence();

            if (!this.isTrueCondition()) {
                this.jumpIf(token);
                this.symbolTable.getNode(this.symbolTable.getActualNode()).removeLastContext();
                token = this.reader.extractToken();
            }
        } else if (token.getId() == Type.WHILE) {

        } else if (token.getId() == Type.RETURN) {
            token = this.reader.extractToken();

            if (token.getId() == Type.INT_CNST) {
                this.symbolTable.getNode(this.symbolTable.getActualNode()).setReturnValue(token.getLexema());
            } else if (token.getId() == Type.ID) {
                this.symbolTable.getNode(this.symbolTable.getActualNode()).setReturnValue(this.symbolTable.getNode(this.symbolTable.getActualNode()).getVariable(token.getLexema()));
            }

            this.reader.goToLine(this.symbolTable.getNode(this.symbolTable.getActualNode()).getReturnLine());
            this.symbolTable.getNode(this.symbolTable.getActualNode()).deleteAllData();
            this.symbolTable.setActualNode(this.symbolTable.getNode(this.symbolTable.getActualNode()).getReturnNode());

            token = this.reader.extractToken();
            token = this.reader.extractToken();

        }else if(token.getId() == Type.PRINT){
             token = this.reader.extractToken();
             token = this.reader.extractToken();

             this.text = "";

             while(token.getId() != Type.CLOSE_PAR) {
                 if (token.getId() == Type.STRING) {
                    this.text = this.text + token.getLexema();
                 } else if(token.getId() == Type.ID || token.getId() == Type.ID_POINTER){
                     Variable variable = this.symbolTable.getNode(this.symbolTable.getActualNode()).getVariable(token.getLexema());
                     this.text = this.text + variable.getType().getValue().toString();
                 }
                 token = this.reader.extractToken();
             }

             token = this.reader.extractToken();
             token = this.reader.extractToken();

        }else if(token.getId() == Type.SCAN){
            token = this.reader.extractToken();
            token = this.reader.extractToken();
            //TODO: Detect if variable exists

            Variable variable = this.symbolTable.getNode(this.symbolTable.getActualNode()).getVariable(token.getLexema());

            token = this.reader.extractToken();
            token = this.reader.extractToken();
            token = this.reader.extractToken();



        }


        return token;
    }


    private Token freeMemoryVariable() {

        Token token = this.reader.extractToken();
        token = this.reader.extractToken();

        if (token.getId() == Type.ID || token.getId() == Type.ID_POINTER) {

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

    private Token variable(Token token) throws BasicError {
        Variable variable;
        Token aux = token;

        variable = this.symbolTable.getNode(this.symbolTable.getActualNode()).getVariable(token.getLexema());
        token = this.reader.extractToken();

        if (token.getId() == Type.EQUAL) {

            token = this.assignment(variable);

        } else if (token.getId() == Type.OPEN_BRA) {

            token = this.arrayAssignment(variable);

        } else if (token.getId() == Type.PLUS) {
            token = this.reader.extractToken();

            if (token.getId() == Type.PLUS) {
                this.incrementVariableValue(variable);
            }

            token = this.reader.extractToken();


        } else if (token.getId() == Type.OPEN_PAR) {
            int index;

            if ((index = this.symbolTable.isFunction(aux.getLexema())) > 0) {
                this.analyseFunction(aux, null, variable, index);
                token = this.reader.extractToken();
            }
        }

        token = this.reader.extractToken();

        return token;
    }

    private void incrementVariableValue(Variable variable) {
        if (variable.getType() instanceof PointerVariable) {

            PointerVariable pointerVariable = (PointerVariable) variable.getType();
            pointerVariable.setValue(Integer.valueOf((pointerVariable.getValue().toString()).split("@")[0]) + 4);
            variable.setType(pointerVariable);

        } else {

            ITypes iTypes = variable.getType();
            iTypes.setValue((Integer.valueOf(iTypes.getValue().toString()) + 1));
            variable.setType(iTypes);
        }

        this.symbolTable.getNode(this.symbolTable.getActualNode()).addVariable(variable);

    }

    private Token assignment(Variable variable) {
        ITypes iTypes;

        Token token = this.reader.extractToken();
        if (token.getId() == Type.INT_CNST) {

            iTypes = variable.getType();
            iTypes.setValue(token.getLexema());

            variable.setType(iTypes);

        } else if (token.getId() == Type.MALLOC) {

            token = this.askMemory(token, variable);

        } else if (token.getId() == Type.AND) {

            token = this.reader.extractToken();

            if (token.getId() == Type.ID || token.getId() == Type.ID_POINTER) {

                iTypes = variable.getType();
                iTypes.setValue(this.symbolTable.getNode(this.symbolTable.getActualNode()).getVariable(token.getLexema()).getType().getOffset());
                variable.setType(iTypes);

            }

        } else if (token.getId() == Type.ID) {

            int index;
            iTypes = variable.getType();

            if ((index = this.symbolTable.isFunction(token.getLexema())) > 0) {
                this.analyseFunction(token, iTypes, variable, index);
            } else {
                iTypes.setValue(this.symbolTable.getNode(this.symbolTable.getActualNode()).getVariable(token.getLexema()).getType().getValue());

                variable.setType(iTypes);
            }
        }

        this.symbolTable.getNode(this.symbolTable.getActualNode()).addVariable(variable);

        token = this.reader.extractToken();

        return token;
    }

    private Token askMemory(Token token, Variable variable) {
        PointerVariable pointerVariable;

        while (token.getId() != Type.INT_CNST) {
            token = this.reader.extractToken();

        }

        pointerVariable = new PointerVariable(variable.getName(), "int_pointer", variable.getType().getSize(), (new Random()).nextInt(10000000), this.symbolTable.getDynamicOffset(), 0, new Integer(token.getLexema()), true);
        pointerVariable.setOffset(variable.getType().getOffset());
        this.symbolTable.setDynamicOffset(this.symbolTable.getDynamicOffset() + (variable.getType().getSize() * (new Integer(token.getLexema()))));
        variable.setType(pointerVariable);

        token = this.reader.extractToken();

        return token;
    }

    private Token arrayAssignment(Variable variable) throws BasicError{
        ArrayType arrayType;

        Token token = this.reader.extractToken();
        int index;

        if (token.getId() == Type.ID) {
            index = Integer.valueOf(this.symbolTable.getNode(this.symbolTable.getActualNode()).getVariable(token.getLexema()).getType().getValue().toString());
        } else {
            index = Integer.valueOf(token.getLexema());
        }


        token = this.reader.extractToken();
        token = this.reader.extractToken();
        token = this.reader.extractToken();

        arrayType = (ArrayType) variable.getType();

        if(index > arrayType.getMaxPosition()){
            throw new AccessError("Error en acceder al array",null,index,arrayType.getMinPosition(),arrayType.getMaxPosition());
        }


        if (token.getId() == Type.INT_CNST) {
            arrayType.setElement(index, token.getLexema());
        } else if (token.getId() == Type.ID) {
            arrayType.setElement(index, this.symbolTable.getNode(this.symbolTable.getActualNode()).getVariable(token.getLexema()).getType().getValue());
        }

        this.symbolTable.getNode(this.symbolTable.getActualNode()).addVariable(variable);

        token = this.reader.extractToken();

        return token;
    }

    private Token analyseIfSentence() {
        Token token;
        Context context;

        Condition condition = new Condition();
        context = new Context();

        context.setTypeOfContext(Type.IF);

        token = this.reader.extractToken();
        token = this.reader.extractToken();

        token = this.analyseCondition(condition, token);

        context.addCondition(condition);

        token = this.reader.extractToken();

        //TODO: Analizar más condicionales de If con && and ||
        if (token.getId() != Type.CLOSE_PAR) {

        }

        token = this.reader.extractToken();
        token = this.reader.extractToken();

        context.setLineNumber(this.reader.getNumLines());

        this.symbolTable.getNode(this.symbolTable.getActualNode()).addContext(context);

        return token;
    }

    private boolean isTrueCondition() {
        return this.symbolTable.getNode(this.symbolTable.getActualNode()).getLastContext().getLastCondition().analyseOperand();
    }

    private void jumpIf(Token token) {
        token = this.reader.extractToken();
        while (token.getId() != Type.CLOSE_KEY) {
            if (token.getId() == Type.OPEN_KEY) this.jumpIf(token);
            token = this.reader.extractToken();
        }
    }

    private Token analyseCondition(Condition condition, Token token) {
        if (token.getId() == Type.ID || token.getId() == Type.ID_POINTER) {

            condition.setLeftSideCondition(this.symbolTable.getNode(this.symbolTable.getActualNode()).getVariable(token.getLexema()));

        } else if (token.getId() == Type.INT_CNST) {

            condition.setLeftSideCondition(token.getLexema());

        }

        token = this.reader.extractToken();

        condition.setOperand(token.getLexema());

        token = this.reader.extractToken();

        if (token.getId() == Type.ID || token.getId() == Type.ID_POINTER) {

            condition.setRightSideCondition(this.symbolTable.getNode(this.symbolTable.getActualNode()).getVariable(token.getLexema()));

        } else if (token.getId() == Type.INT_CNST) {
            condition.setRightSideCondition(token.getLexema());
        }

        return token;
    }

    private Token analyseForSentence() {
        Token token;
        Loop loop = new Loop();
        Context context = new Context();

        context.setTypeOfContext(Type.FOR);

        token = this.reader.extractToken();
        token = this.reader.extractToken();

        if (token.getId() == Type.ID || token.getId() == Type.ID_POINTER) {
            Variable variable = this.symbolTable.getNode(this.symbolTable.getActualNode()).getVariable(token.getLexema());

            token = this.reader.extractToken();

            this.assignment(variable); //avanza hasta la ','

            token = this.reader.extractToken();


            this.analyseCondition(loop, token);

            token = this.reader.extractToken();//te deja en ','
            token = this.reader.extractToken();

            //TODO: Diferentes formas de incrementar la variable
            loop.setInitialValue(variable.getType().getValue());
            loop.setIncrementVariable(variable);

            //TODO: Error de nombre de variables
            if (variable.getName().equals(token.getLexema())) {
                token = this.reader.extractToken();

                if (token.getId() == Type.PLUS) {

                    token = this.reader.extractToken();

                    if (token.getId() == Type.PLUS) {
                        loop.setIncrementValue(1);

                    }
                }
            }

            context.addCondition(loop);

            token = this.reader.extractToken();
            token = this.reader.extractToken();
            token = this.reader.extractToken();

            context.setLineNumber(this.reader.getNumLines());

            this.symbolTable.getNode(this.symbolTable.getActualNode()).addContext(context);

        }

        return token;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    private Token analyseFunction(Token token, ITypes iTypes, Variable variable, int index) {
        Node node = this.symbolTable.getNode(index);

        if (node != null) {
            if (node.getReturnType() != Type.VOID) {

                if (node.getReturnValue() == null) {
                    //TODO: ARGUMENTS
                    if(node.getArgumentList().size() != 0)this.extractArguments(token, node);

                    node.setReturnLine(this.reader.getNumLines());
                    this.reader.setNumLines(node.getNodeLine());
                    this.reader.goToLine(node.getNodeLine());
                    node.setReturnNode(this.symbolTable.getActualNode());
                    this.symbolTable.setActualNode(index);
                    this.symbolTable.addNode(node);



                } else {

                    iTypes = variable.getType();

                    if (node.getReturnValue() instanceof Variable) {
                        iTypes.setValue(variable.getType().getValue());
                    } else {
                        iTypes.setValue(node.getReturnValue());
                    }

                    variable.setType(iTypes);
                    while (token.getId() != Type.CLOSE_PAR) {
                        token = this.reader.extractToken();
                    }
                }

            } else {

                node.setReturnLine(this.reader.getNumLines() + 1);
                this.reader.setNumLines(node.getNodeLine());
                this.reader.goToLine(node.getNodeLine());
                node.setReturnNode(this.symbolTable.getActualNode());
                this.symbolTable.setActualNode(index);
                this.symbolTable.addNode(node);

            }
        }

        return token;
    }

    private Token extractArguments(Token token, Node node){
        token = this.reader.extractToken();
        token = this.reader.extractToken();

        Variable variable1,variable;

        for(int i = 0; i < node.getArgumentList().size(); i++){
            variable1 = this.symbolTable.getNode(this.symbolTable.getActualNode()).getVariable(token.getLexema());

            variable = (Variable) node.getArgumentList().values().toArray()[i];
            variable.getType().setValue(variable1.getType().getValue());

            node.addArgument(variable);

            token = this.reader.extractToken();
            if(token.getId() != Type.CLOSE_PAR) token = this.reader.extractToken();

        }





        return token;
    }

    public String getText() {
        String aux = text;
        this.text = "";
        return aux;
    }

    public void setText(String text) {
        this.text = text;
    }
}
