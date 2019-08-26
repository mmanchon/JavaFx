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
import com.sun.deploy.security.ValidationState;
import com.sun.xml.internal.ws.util.StringUtils;

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
            if ((node = this.symbolTable.getPreviousNode()) != null) {

                if (token.getId() == Type.INT_CNST) {
                    node.getVariable(this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getReturnVariable()).getType().setValue(token.getLexema());
                    this.symbolTable.setCurrentNode(this.symbolTable.getCurrentNode() - 1);
                    this.symbolTable.substituteExecutionNode(node);
                    this.symbolTable.setCurrentNode(this.symbolTable.getCurrentNode() + 1);

                } else if (token.getId() == Type.ID) {
                    node.getVariable(this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getReturnVariable()).getType().setValue(this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema()).getType().getValue());
                    this.symbolTable.setCurrentNode(this.symbolTable.getCurrentNode() - 1);
                    this.symbolTable.substituteExecutionNode(node);
                    this.symbolTable.setCurrentNode(this.symbolTable.getCurrentNode() + 1);
                }

            }


            this.reader.goToLine(this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getReturnLine());
            this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).deleteAllData();
            this.symbolTable.removeExecutionNode(this.symbolTable.getCurrentNode());
            this.symbolTable.setCurrentNode(this.symbolTable.getCurrentNode() - 1);


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


        token = this.reader.extractToken();

        if (token.getId() == Type.EQUAL) {
            variable = this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(aux.getLexema());

            token = this.assignment(aux, variable);

        } else if (token.getId() == Type.OPEN_BRA) {
            variable = this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(aux.getLexema());

            token = this.arrayAssignment(variable);

        } else if (token.getId() == Type.PLUS) {
            variable = this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(aux.getLexema());

            token = this.reader.extractToken();

            if (token.getId() == Type.PLUS) {
                this.incrementVariableValue(variable, aux.getId() == Type.ID);

            }

            token = this.reader.extractToken();


        } else if (token.getId() == Type.OPEN_PAR) {
            Node node;

            if ((node = this.symbolTable.isFunction(aux.getLexema())) != null) {
                this.analyseFunction(token, null, new Node(node));
                token = this.reader.extractToken();
            }
        }

        token = this.reader.extractToken();

        return token;
    }

    private void incrementVariableValue(Variable variable, boolean isID) {
        if (isID) {

            if (variable.getType() instanceof PointerVariable) {
                PointerVariable pointerVariable = (PointerVariable) variable.getType();
                pointerVariable.setValue(Integer.parseInt((pointerVariable.getValue().toString()).split("@")[0]) + 4);
                variable.setType(pointerVariable);

            } else {
                ITypes iTypes = variable.getType();
                iTypes.setValue((Integer.parseInt(iTypes.getValue().toString()) + 1));
                variable.setType(iTypes);

            }
        } else {

/*            ITypes iTypes = variable.getType();
            iTypes.setValue((Integer.parseInt(iTypes.getValue().toString()) + 1));
            variable.setType(iTypes);
*/
            this.incrementReferencedValue(variable, 1, 0); //Funcion para cambiar el valor de referencia

        }


        this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).addVariable(variable);

    }

    private void incrementArrayValue(Variable variable, int index, int increment) {
        if (variable.getType() instanceof PointerVariable) {
            this.incrementReferencedValue(variable, increment, index);
        } else if (variable.getType() instanceof ArrayType) {
            ((ArrayType) variable.getType()).setElement(index, Integer.valueOf(((ArrayType) variable.getType()).getElement(index).getType().getValue().toString()) + increment);
            this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).addVariable(variable);
        }
    }

    private Token assignment(Token nameVariable, Variable variable) {
        ITypes iTypes;

        Token token = this.reader.extractToken();
        if (token.getId() == Type.INT_CNST) {

            if (nameVariable.getId() == Type.ID) {
                iTypes = variable.getType();
                iTypes.setValue(token.getLexema());

                variable.setType(iTypes);
                this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).addVariable(variable);
            } else {
                if (variable.getType() instanceof PointerVariable) {
                    this.changeReferencedValue(variable, token.getLexema()); //Funcion para cambiar el valor de referencia
                }
            }
        } else if (token.getId() == Type.OPEN_PAR) {

            token = this.reader.extractToken();
            //TODO: Check same type variables
            if (token.getId() == Type.INT) {

                Token aux = token;
                token = this.reader.extractToken();

                if (token.getId() == Type.ID_POINTER) {
                    token = this.reader.extractToken();
                }

            }

            token = this.reader.extractToken();

            if (token.getId() == Type.MALLOC) {
                token = this.askMemory(token, variable);
            }

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
                this.analyseFunction(token, variable, new Node(node));
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
        int numMemory = 0;

        token = this.reader.extractToken();

        if (token.getId() == Type.OPEN_PAR) {
            token = this.reader.extractToken();

            if (token.getId() == Type.SIZEOF) {
                numMemory = 1;

                token = this.reader.extractToken();
                if (token.getId() == Type.OPEN_PAR) {
                    token = this.reader.extractToken();

                    if (token.getId() == Type.INT) {
                        token = this.reader.extractToken();

                        if (token.getId() == Type.ID_POINTER) {
                            token = this.reader.extractToken();
                            while (token.getId() != Type.CLOSE_PAR) {
                                token = this.reader.extractToken();
                            }
                        } else if (token.getId() == Type.CLOSE_PAR) {
                            token = this.reader.extractToken();
                        }
                    }
                }
            }
        }

        while (token.getId() != Type.INT_CNST && token.getId() != Type.CLOSE_PAR && token.getId() != Type.ID && token.getId() != Type.ID_POINTER) {
            token = this.reader.extractToken();

        }

        if (this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema()) == null) {
            try {
                Integer.parseInt(token.getLexema());
                token.setId(Type.INT_CNST);
            } catch (NumberFormatException | NullPointerException nfe) {
                token = this.reader.extractToken();
            }
        }

        if (token.getId() == Type.INT_CNST) {
            numMemory = new Integer(token.getLexema());
        } else if (token.getId() == Type.ID) {
            if (this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema()).getType() instanceof ArrayType) {
                Token aux = token;
                token = this.reader.extractToken();
                if (token.getId() == Type.OPEN_BRA) {
                    token = this.reader.extractToken();
                    int index;

                    if (token.getId() == Type.ID) {

                        index = Integer.valueOf(this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema()).getType().getValue().toString());

                    } else {
                        index = Integer.valueOf(token.getLexema());
                    }

                    numMemory = Integer.valueOf(((ArrayType) this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(aux.getLexema()).getType()).getElement(index).getType().getValue().toString());
                }
                token = this.reader.extractToken();

            } else {
                numMemory = Integer.valueOf(this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema()).getType().getValue().toString());
            }
        } else if (token.getId() == Type.ID_POINTER) {
            if (this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema()).getType() instanceof PointerVariable) {
                numMemory = Integer.valueOf(this.extractReferencedValue(this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema())).toString());

            } else {
                numMemory = Integer.valueOf(this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema()).getType().getValue().toString());

            }
        }

        pointerVariable = new PointerVariable(variable.getName(), "int_pointer", variable.getType().getSize(), (new Random()).nextInt(10000000), this.symbolTable.getDynamicOffset(), 0, numMemory, true);
        pointerVariable.setOffset(variable.getType().getOffset());

        this.symbolTable.setDynamicOffset(this.symbolTable.getDynamicOffset() + (variable.getType().getSize() * numMemory));

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


        token = this.reader.extractToken(); //close bracket
        token = this.reader.extractToken();
        if (token.getId() == Type.EQUAL) {
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
        } else if (token.getId() == Type.PLUS) {

            token = this.reader.extractToken();

            if (token.getId() == Type.PLUS) {
                this.incrementArrayValue(variable, index, 1);
            }
        }

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

            this.assignment(aux, variable); //avanza hasta la ','

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
            Node aux = new Node(node);

            if (aux.getVariablesList().size() != 0) this.extractArguments(token, aux);
            aux.setReturnLine(this.reader.getNumLines() + 1);
            this.reader.setNumLines(aux.getNodeLine());
            this.reader.goToLine(aux.getNodeLine());
            this.symbolTable.setCurrentNode(this.symbolTable.getCurrentNode() + 1);

            if (node.getReturnType() != Type.VOID) {
                aux.setReturnVariable(token.getLexema());
            }

            this.symbolTable.addxecutionFunctions(new Node(aux));
        }

        return token;
    }

    private Token extractArguments(Token token, Node node) {

        Variable variable1, argument;

        while (token.getId() != Type.OPEN_PAR) {
            token = this.reader.extractToken();
        }

        for (int i = 0; token.getId() != Type.CLOSE_PAR && i < node.getVariablesList().size(); i++) {

            token = this.reader.extractToken();

            if (token.getId() == Type.AND) {
                token = this.reader.extractToken();//gets variable name may be &

                variable1 = this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema());
                argument = new Variable((Variable) node.getVariablesList().values().toArray()[i]);

                // token = this.reader.extractToken();//gets variable name
                argument.getType().setValue(variable1.getType().getOffset());
                ((PointerVariable) argument.getType()).setNodeReferenced(this.symbolTable.getCurrentNode());
                ((PointerVariable) argument.getType()).setPointerVariable(variable1);

            } else {


                variable1 = this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).getVariable(token.getLexema());
                argument = new Variable((Variable) node.getVariablesList().values().toArray()[i]);

                if (variable1.getType() != null && variable1.getType() instanceof PointerVariable) {
                    argument.setType(new PointerVariable((PointerVariable) variable1.getType()));
                    argument.getType().setValue(((Variable) ((PointerVariable) variable1.getType()).getPointerVariable()).getType().getOffset());

                } else if(variable1.getType()instanceof ArrayType) {

                    if(argument.getType() instanceof PointerVariable){

                        argument.getType().setValue(variable1.getType().getOffset());
                        ((PointerVariable) argument.getType()).setNodeReferenced(this.symbolTable.getCurrentNode());
                        ((PointerVariable) argument.getType()).setPointerVariable(variable1);

                    }else if(argument.getType() instanceof ArrayType){
                        //TODO Arguments list
                        ArrayType argumentType, variableType;
                        argumentType = ((ArrayType)argument.getType());
                        variableType = ((ArrayType)variable1.getType());
                        argumentType.setMaxPosition(variableType.getMaxPosition());
                        argumentType.setMinPosition(variableType.getMinPosition());
                        argumentType.setElemntsList(variableType.getElemntsList());

                        for(int j = 0; j < argumentType.getMaxPosition(); j++){
                            //argumentType.setElement(j,variableType.getElement(j));
                            argumentType.getElement(j).getType().setOffset(this.symbolTable.getStaticOffset() + 4);
                            this.symbolTable.setStaticOffset(this.symbolTable.getStaticOffset() + argument.getType().getSize());
                            argumentType.getElement(j).setType(new PointerVariable((PointerVariable) argumentType.getElement(j).getType()));
                            ((PointerVariable)argumentType.getElement(j).getType()).setNodeReferenced(this.symbolTable.getCurrentNode());
                        }

                    }

                }else{
                    argument.getType().setValue(variable1.getType().getValue());
                }
            }

            argument.getType().setOffset(this.symbolTable.getStaticOffset() + 4);
            this.symbolTable.setStaticOffset(this.symbolTable.getStaticOffset() + argument.getType().getSize());
            node.addVariable(argument);

            token = this.reader.extractToken();

        }


        return token;
    }

    private void changeReferencedValue(Variable variable, Object newValue) {
        while (((PointerVariable) variable.getType()).getPointerVariable() != null && ((Variable) ((PointerVariable) variable.getType()).getPointerVariable()).getType() instanceof PointerVariable && ((PointerVariable) variable.getType()).getNodeReferenced() != -1) {
            variable = (Variable) ((PointerVariable) variable.getType()).getPointerVariable();
        }
        this.symbolTable.getExecutionNode(((PointerVariable) variable.getType()).getNodeReferenced()).getVariable(((Variable) ((PointerVariable) variable.getType()).getPointerVariable()).getName()).getType().setValue(newValue);

    }

    private void incrementReferencedValue(Variable variable, Object increment, int index) {
        while (((PointerVariable) variable.getType()).getPointerVariable() != null && ((Variable) ((PointerVariable) variable.getType()).getPointerVariable()).getType() instanceof PointerVariable && ((PointerVariable) variable.getType()).getNodeReferenced() != -1) {
            variable = (Variable) ((PointerVariable) variable.getType()).getPointerVariable();
        }

        if (variable.getType() instanceof PointerVariable) {
            if(((Variable) ((PointerVariable) variable.getType()).getPointerVariable()).getType() instanceof ArrayType){
               // variable = (Variable) ((PointerVariable) variable.getType()).getPointerVariable();
                ((ArrayType) ((Variable) ((PointerVariable) variable.getType()).getPointerVariable()).getType()).setElement(index, Integer.valueOf(((ArrayType) ((Variable) ((PointerVariable) variable.getType()).getPointerVariable()).getType()).getElement(index).getType().getValue().toString()) + Integer.valueOf(increment.toString()));
                this.symbolTable.getExecutionNode(((PointerVariable) variable.getType()).getNodeReferenced()).addVariable((Variable) ((PointerVariable) variable.getType()).getPointerVariable());
            }else {
                this.symbolTable.getExecutionNode(((PointerVariable) variable.getType()).getNodeReferenced()).getVariable(((Variable) ((PointerVariable) variable.getType()).getPointerVariable()).getName()).getType().setValue(Integer.parseInt(increment.toString()) + Integer.parseInt(this.symbolTable.getExecutionNode(((PointerVariable) variable.getType()).getNodeReferenced()).getVariable(((Variable) ((PointerVariable) variable.getType()).getPointerVariable()).getName()).getType().getValue().toString()));
            }

        } else {

        }
    }

    private Object extractReferencedValue(Variable variable) {
        while (((PointerVariable) variable.getType()).getPointerVariable() != null && ((Variable) ((PointerVariable) variable.getType()).getPointerVariable()).getType() instanceof PointerVariable && ((PointerVariable) variable.getType()).getNodeReferenced() != -1) {
            variable = (Variable) ((PointerVariable) variable.getType()).getPointerVariable();
        }

        if (variable.getType() instanceof PointerVariable && ((PointerVariable) variable.getType()).getPointerVariable() != null) {
            return this.symbolTable.getExecutionNode(((PointerVariable) variable.getType()).getNodeReferenced()).getVariable(((Variable) ((PointerVariable) variable.getType()).getPointerVariable()).getName()).getType().getValue();
        }
        return ((ArrayType) variable.getType()).getElement(Integer.valueOf(variable.getType().getValue().toString()) / 4).getType().getValue();

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
