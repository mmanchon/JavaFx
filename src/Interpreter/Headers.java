package Interpreter;

import Interpreter.FileReader.Reader;
import Interpreter.SymbolTable.Node;
import Interpreter.SymbolTable.Objects.Parameter;
import Interpreter.SymbolTable.Types.PointerVariable;
import Interpreter.SymbolTable.SymbolTable;
import Interpreter.SymbolTable.Types.ArrayType;
import Interpreter.SymbolTable.Types.ITypes;
import Interpreter.Tokens.Token;
import Interpreter.Tokens.Type;

public class Headers {

    private Reader reader;
    private SymbolTable symbolTable;
    private int numLine = 0;

    public Headers(Reader reader, SymbolTable symbolTable) {
        this.reader = reader;
        this.symbolTable = symbolTable;
    }

    public Token readFunctionsHeaders(Token token, SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        Node node = new Node();


        while (token.getId() != Type.INT && token.getId() != Type.VOID) {
            token = this.reader.extractToken();
        }

        if (token.getId() == Type.INT) {
            node.setReturnType(Type.INT);
        } else {
            node.setReturnType(Type.VOID);
        }
        token = this.reader.extractToken();

        if (token.getId() != Type.MAIN) {
            token = this.extractFunction(token, node);
        }

        return token;
    }

    public Token readMainHeader(Token token, SymbolTable symbolTable) {
        this.symbolTable = symbolTable;

        while (token.getId() != Type.MAIN) {
            token = this.reader.extractToken();
        }


        token = this.reader.extractToken();
        token = this.reader.extractToken();
        token = this.reader.extractToken();
        token = this.reader.extractToken();

        this.symbolTable.setCurrentNode(0);
        this.symbolTable.addxecutionFunctions(new Node());
        this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).setNodeName("main");
        //this.symbolTable.getEmptyFunction(this.symbolTable.getCurrentNode()).
        return token;
    }

    public int getNumLine() {
        return this.numLine;
    }

    public Token extractFunction(Token token, Node node) {

        Parameter variable;
        Token aux;

        node.setNodeName(token.getLexema()); // nuevo nodo donde guardamos la funcion

        token = this.reader.extractToken();
        token = this.reader.extractToken();

        while (token.getId() != Type.CLOSE_PAR) {

            variable = new Parameter();

            if (token.getId() == Type.INT) {

                token = this.reader.extractToken();
                aux = token;

                token = this.reader.extractToken();

                if (token.getId() == Type.OPEN_BRA) {
                    //TODO: Hacer arrays sin medida
                    token = this.reader.extractToken();
                    //Number

                    variable.setType(this.createIntArgument(variable.getName(), "int_array", this.symbolTable.getStaticOffset(), 4, 0, new Integer(token.getLexema())));

                    token = this.reader.extractToken();
                    //close bra
                    token = this.reader.extractToken();
                    variable.setReference(true);
                } else if (aux.getId() == Type.ID_POINTER) {
                    variable.setName(aux.getLexema());
                    variable.setType(this.createIntArgument(token.getLexema(), "int_pointer", this.symbolTable.getStaticOffset() + 4, 4, 0, 0));
                    variable.setReference(true);
                } else if (aux.getId() == Type.ID) {
                    variable.setName(aux.getLexema());
                    variable.setType(this.createIntArgument(token.getLexema(), "int", this.symbolTable.getStaticOffset() + 4, 4, 0, 0));
                    variable.setReference(false);
                }

                node.addVariable(variable); //hacerlo como variable
            }

            if (token.getId() != Type.CLOSE_PAR) token = this.reader.extractToken();
        }


        token = this.reader.extractToken();
        token = this.reader.extractToken();

        node.setNodeLine(this.reader.getNumLines());

        while (token.getId() != Type.CLOSE_KEY) {
            token = this.reader.extractToken();
        }

        this.symbolTable.addEmptyFunctions(node);

        this.symbolTable.setCurrentNode(this.symbolTable.getCurrentNode() + 1);

        token = this.readFunctionsHeaders(token, this.symbolTable);

        return token;
    }


    private ITypes createIntArgument(String varName, String typeName, int offset, int size, Object value, int maxLength) {
        if (typeName.equals("int")) {

            return new ITypes(typeName, size, value, offset);

        } else if (typeName.equals("int_pointer")) {

            PointerVariable iTypes = new PointerVariable();
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