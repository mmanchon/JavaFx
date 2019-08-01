package Interpreter;

import Interpreter.Errors.ArrayErrors.AccessError;
import Interpreter.Errors.BasicError;
import Interpreter.FileReader.Reader;
import Interpreter.SymbolTable.*;
import Interpreter.SymbolTable.Contexts.Condition;
import Interpreter.SymbolTable.Contexts.Context;
import Interpreter.SymbolTable.Contexts.Loop;
import Interpreter.SymbolTable.Types.PointerVariable;
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

        if (token.getId() == Type.ID || token.getId() == Type.ID_POINTER) {

            token = this.variable(token);

        } else if (token.getId() == Type.FREE) {

            token = this.freeMemoryVariable();

        } else if (token.getId() == Type.AND) {

        } else if (token.getId() == Type.IF) {

            token = this.analyseIfSentence();

            if (!this.isTrueCondition()) {

                this.jumpIf(token);
                this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).removeLastContext();

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
                this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).removeLastContext();
                token = this.reader.extractToken();
            }
        } else if (token.getId() == Type.WHILE) {

        } else if (token.getId() == Type.RETURN) {
            token = this.reader.extractToken();
            Node node;
            if((node = this.symbolTable.getPreviousNode()) != null ){

                if (token.getId() == Type.INT_CNST) {
                    node.getVariable(this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getReturnVariable()).getType().setValue(token.getLexema());
                    this.symbolTable.setCurrentNode(this.symbolTable.getCurrentNode()-1);
                    this.symbolTable.substituteExecutionNode(node);
                    this.symbolTable.setCurrentNode(this.symbolTable.getCurrentNode()+1);

                } else if (token.getId() == Type.ID) {
                    node.getVariable(this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getReturnVariable()).getType().setValue(this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema()).getType().getValue());
                    this.symbolTable.setCurrentNode(this.symbolTable.getCurrentNode()-1);
                    this.symbolTable.substituteExecutionNode(node);
                    this.symbolTable.setCurrentNode(this.symbolTable.getCurrentNode()+1);
                }

            }


            this.reader.goToLine(this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getReturnLine()+1);
            this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).deleteAllData();
            this.symbolTable.removeExecutionNode(this.symbolTable.getCurrentNode());
            this.symbolTable.setCurrentNode(this.symbolTable.getCurrentNode()-1);


            token = this.reader.extractToken();
            token = this.reader.extractToken();

        } else if (token.getId() == Type.PRINT) {
            token = this.reader.extractToken();
            token = this.reader.extractToken();

            this.text = "";

            while (token.getId() != Type.CLOSE_PAR) {
                if (token.getId() == Type.STRING) {
                    this.text = this.text + token.getLexema();
                } else if (token.getId() == Type.ID || token.getId() == Type.ID_POINTER) {
                    Variable variable = this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema());
                    this.text = this.text + variable.getType().getValue().toString();
                }
                token = this.reader.extractToken();
            }

            token = this.reader.extractToken();
            token = this.reader.extractToken();

        } else if (token.getId() == Type.SCAN) {
            token = this.reader.extractToken();
            token = this.reader.extractToken();
            //TODO: Detect if variable exists

            Variable variable = this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema());

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

            Variable variable = this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema());
            PointerVariable pointerVariable = (PointerVariable) variable.getType();
            pointerVariable.setHasMemory(false);
            pointerVariable.removeAllElements();
            pointerVariable.setValue("null");
            variable.setType(pointerVariable);
            this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).addVariable(variable);

        }
        token = this.reader.extractToken();
        token = this.reader.extractToken();
        token = this.reader.extractToken();

        return token;
    }

    private Token variable(Token token) throws BasicError {
        Variable variable;
        Token aux = token;


        variable = this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema());


        token = this.reader.extractToken();

        if (token.getId() == Type.EQUAL) {

            token = this.assignment(aux,variable);

        } else if (token.getId() == Type.OPEN_BRA) {

            token = this.arrayAssignment(variable);

        } else if (token.getId() == Type.PLUS) {
            token = this.reader.extractToken();

            if (token.getId() == Type.PLUS) {
                this.incrementVariableValue(variable,aux.getId() == Type.ID);

            }

            token = this.reader.extractToken();


        } else if (token.getId() == Type.OPEN_PAR) {
            Node node;

            if ((node = this.symbolTable.isFunction(aux.getLexema())) != null) {
                this.analyseFunction(aux,  variable, node);
                token = this.reader.extractToken();
            }
        }

        token = this.reader.extractToken();

        return token;
    }

    private void incrementVariableValue(Variable variable, boolean isID) {
        if (isID) {

            if(variable.getType() instanceof PointerVariable) {
                PointerVariable pointerVariable = (PointerVariable) variable.getType();
                pointerVariable.setValue(Integer.parseInt((pointerVariable.getValue().toString()).split("@")[0]) + 4);
                variable.setType(pointerVariable);

            }else{
                ITypes iTypes = variable.getType();
                iTypes.setValue((Integer.parseInt(iTypes.getValue().toString()) + 1));
                variable.setType(iTypes);

            }
        } else {

/*            ITypes iTypes = variable.getType();
            iTypes.setValue((Integer.parseInt(iTypes.getValue().toString()) + 1));
            variable.setType(iTypes);
*/
            this.incrementReferencedValue(variable,1); //Funcion para cambiar el valor de referencia

        }


        this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).addVariable(variable);

    }

    private Token assignment(Token nameVariable,Variable variable) {
        ITypes iTypes;

        Token token = this.reader.extractToken();
        if (token.getId() == Type.INT_CNST) {

            if(nameVariable.getId() == Type.ID) {
                iTypes = variable.getType();
                iTypes.setValue(token.getLexema());

                variable.setType(iTypes);
                this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).addVariable(variable);
            }else {
                if (variable.getType() instanceof PointerVariable) {
                    this.changeReferencedValue(variable, token.getLexema()); //Funcion para cambiar el valor de referencia
                }
            }
        } else if (token.getId() == Type.MALLOC) {

            token = this.askMemory(token, variable);
            this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).addVariable(variable);

        } else if (token.getId() == Type.AND) {

            token = this.reader.extractToken();

            if (token.getId() == Type.ID || token.getId() == Type.ID_POINTER) {

                iTypes = variable.getType();

                iTypes.setValue(this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema()).getType().getOffset());

                variable.setType(iTypes);

            }
            this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).addVariable(variable);

        } else if (token.getId() == Type.ID) {

            Node node;

            if ((node = this.symbolTable.isFunction(token.getLexema())) != null) {
                this.analyseFunction(token, variable,node);
                this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).setReturnVariable(nameVariable.getLexema());
            } else {
                iTypes = variable.getType();

                iTypes.setValue(this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema()).getType().getValue());

                variable.setType(iTypes);
            }
        }




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

    private Token arrayAssignment(Variable variable) throws BasicError {
        ArrayType arrayType;

        Token token = this.reader.extractToken();
        int index;

        if (token.getId() == Type.ID) {

            index = Integer.valueOf(this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema()).getType().getValue().toString());


        } else {
            index = Integer.valueOf(token.getLexema());
        }


        token = this.reader.extractToken();
        token = this.reader.extractToken();
        token = this.reader.extractToken();

        arrayType = (ArrayType) variable.getType();

        if (index > arrayType.getMaxPosition()) {
            throw new AccessError("Error en acceder al array", null, index, arrayType.getMinPosition(), arrayType.getMaxPosition());
        }


        if (token.getId() == Type.INT_CNST) {
            arrayType.setElement(index, token.getLexema());

        } else if (token.getId() == Type.ID) {


            arrayType.setElement(index, this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema()).getType().getValue());


        }


        this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).addVariable(variable);


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


        this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).addContext(context);


        return token;
    }

    private boolean isTrueCondition() {

        return this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getLastContext().getLastCondition().analyseOperand();


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


            condition.setLeftSideCondition(this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema()));


        } else if (token.getId() == Type.INT_CNST) {

            condition.setLeftSideCondition(token.getLexema());

        }

        token = this.reader.extractToken();

        condition.setOperand(token.getLexema());

        token = this.reader.extractToken();

        if (token.getId() == Type.ID || token.getId() == Type.ID_POINTER) {

            condition.setRightSideCondition(this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema()));

        } else if (token.getId() == Type.INT_CNST) {
            condition.setRightSideCondition(token.getLexema());
        }

        return token;
    }

    private Token analyseForSentence() {
        Token token, aux;
        Loop loop = new Loop();
        Context context = new Context();

        context.setTypeOfContext(Type.FOR);

        token = this.reader.extractToken();
        token = this.reader.extractToken();
        aux = token;
        if (token.getId() == Type.ID || token.getId() == Type.ID_POINTER) {
            Variable variable = this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema());

            token = this.reader.extractToken();

            this.assignment(aux,variable); //avanza hasta la ','

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

            this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).addContext(context);

        }

        return token;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    private Token analyseFunction(Token token, Variable variable, Node node) {
        if (node != null) {
            if (node.getReturnType() != Type.VOID) {

//                if ((aux = this.symbolTable.getFunction(token.getLexema())) == null) {
                    //TODO: ARGUMENTS
                    if (node.getVariablesList().size() != 0) this.extractArguments(token, node);

                    node.setReturnLine(this.reader.getNumLines());
                    this.reader.setNumLines(node.getNodeLine());
                    this.reader.goToLine(node.getNodeLine());
                    node.setReturnVariable(token.getLexema());
                    this.symbolTable.setCurrentNode(this.symbolTable.getCurrentNode()+1);
                    this.symbolTable.addxecutionFunctions(new Node(node));

            } else {
                if (node.getVariablesList().size() != 0) this.extractArguments(token, node);
                node.setReturnLine(this.reader.getNumLines() + 1);
                this.reader.setNumLines(node.getNodeLine());
                this.reader.goToLine(node.getNodeLine());
                this.symbolTable.setCurrentNode(this.symbolTable.getCurrentNode()+1);
                this.symbolTable.addxecutionFunctions(new Node(node));

            }
        }

        return token;
    }

    private Token extractArguments(Token token, Node node) {
        token = this.reader.extractToken();//variable


        Variable variable1, argument;

        for (int i = 0; token.getId() != Type.CLOSE_PAR && i < node.getVariablesList().size(); i++) {


            if(token.getId() == Type.AND){
                token = this.reader.extractToken();//gets variable name may be &

                variable1 = this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema());
                argument = (Variable) node.getVariablesList().values().toArray()[i];

               // token = this.reader.extractToken();//gets variable name
                argument.getType().setValue(variable1.getType().getOffset());
                ((PointerVariable)argument.getType()).setNodeReferenced(this.symbolTable.getCurrentNode());
                ((PointerVariable)argument.getType()).setPointerVariable(variable1);

            }else{
                variable1 = this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema());
                argument = (Variable) node.getVariablesList().values().toArray()[i];
                argument.getType().setValue(variable1.getType().getValue());
            }


            if(variable1.getType() instanceof PointerVariable){

            }else if(variable1.getType() instanceof ArrayType){

            }else{

            }
            argument.getType().setOffset(this.symbolTable.getStaticOffset()+4);
            this.symbolTable.setStaticOffset(this.symbolTable.getStaticOffset() + argument.getType().getSize());
            node.addVariable(argument);

            token = this.reader.extractToken();

        }


        return token;
    }

    private void changeReferencedValue(Variable variable, Object newValue){
        while(variable != null && variable.getType() instanceof PointerVariable && ((PointerVariable)variable.getType()).getNodeReferenced() != -1){
            this.symbolTable.getExecutionNode(((PointerVariable)variable.getType()).getNodeReferenced()).getVariable(((Variable)((PointerVariable)variable.getType()).getPointerVariable()).getName()).getType().setValue(newValue);
            variable = (Variable) ((PointerVariable)variable.getType()).getPointerVariable();
        }
    }
    private void incrementReferencedValue(Variable variable, Object increment){
        while(variable != null && variable.getType() instanceof PointerVariable && ((PointerVariable)variable.getType()).getNodeReferenced() != -1){
            this.symbolTable.getExecutionNode(((PointerVariable)variable.getType()).getNodeReferenced()).getVariable(((Variable)((PointerVariable)variable.getType()).getPointerVariable()).getName()).getType().setValue(Integer.parseInt(increment.toString())+Integer.parseInt(this.symbolTable.getExecutionNode(((PointerVariable)variable.getType()).getNodeReferenced()).getVariable(((Variable)((PointerVariable)variable.getType()).getPointerVariable()).getName()).getType().getValue().toString()));
            variable = (Variable) ((PointerVariable)variable.getType()).getPointerVariable();
        }
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
