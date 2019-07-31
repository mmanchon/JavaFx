package Interpreter;

import Interpreter.FileReader.Reader;
import Interpreter.SymbolTable.*;

import Interpreter.SymbolTable.Objects.PointerVariable;
import Interpreter.SymbolTable.Objects.Variable;
import Interpreter.SymbolTable.Types.ArrayType;
import Interpreter.SymbolTable.Types.ITypes;
import Interpreter.Tokens.Token;
import Interpreter.Tokens.Type;

import java.util.Random;

public class Declarations {

    private Reader reader;
    private SymbolTable symbolTable;

    public Declarations(Reader reader, SymbolTable symbolTable) {
        this.reader = reader;
        this.symbolTable = symbolTable;
    }

    public Token readDeclarations(Token token, SymbolTable symbolTable) {
        this.symbolTable = symbolTable;

        if (token.getId() == Type.INT) {
            token = this.isIntDeclaration(token);

        }


        return token;
    }

    private Token isIntDeclaration(Token token) {
        Variable variable;
        ITypes iTypes;
        Random random = new Random();

        if (token.getId() == Type.INT) {
            while (token.getId() != Type.SEMICOLON) {
                variable = new Variable();

                token = this.reader.extractToken();

                if (token.getId() == Type.ID_POINTER) {

                    iTypes = createIntVariable(token.getLexema(), "int_pointer", this.symbolTable.getStaticOffset() + 4, 4, "@" + random.nextInt(10000000), 0);
                } else {

                    iTypes = createIntVariable(token.getLexema(), "int", this.symbolTable.getStaticOffset() + 4, 4, random.nextInt(10000000), 0);
                }

                this.symbolTable.setStaticOffset(this.symbolTable.getStaticOffset() + iTypes.getSize());

                variable.setType(iTypes);
                variable.setName(token.getLexema());

                token = this.reader.extractToken();

                //Si es una asignacion de variable
                if (token.getId() == Type.EQUAL || token.getId() == Type.COMMA || token.getId() == Type.SEMICOLON) {

                    if (token.getId() != Type.COMMA && token.getId() != Type.SEMICOLON) {

                        if (token.getId() == Type.EQUAL) {
                            token = this.reader.extractToken();

                            iTypes = variable.getType();

                            //TODO: Revisar que estoy haciendo aquí, ambos if son lo mismo y que pasa si es ID
                            if (token.getId() == Type.INT_CNST) {
                                iTypes.setValue(token.getLexema());
                                variable.setType(iTypes);
                            } else {
                                iTypes.setValue(token.getLexema());
                                variable.setType(iTypes);
                            }
                        }

                        token = this.reader.extractToken();
                    }

                    //Si accedemos a un array
                } else if (token.getId() == Type.OPEN_BRA) {

                    token = this.reader.extractToken();

                    variable.setType(this.createIntVariable(variable.getName(), "int_array", this.symbolTable.getStaticOffset(), 4, 0, new Integer(token.getLexema())));

                    token = this.reader.extractToken();
                    token = this.reader.extractToken();

                }

                this.symbolTable.getExecutionNode(this.symbolTable.getCurrentNode()).addVariable(variable);

            }

            token = this.reader.extractToken();
        }
        return token;
    }

    private ITypes createIntVariable(String varName, String typeName, int offset, int size, Object value, int maxLength) {
        if (typeName.equals("int")) {

            return new ITypes(typeName, size, value, offset);

        } else if (typeName.equals("int_pointer")) {

            ITypes iTypes = new PointerVariable();
            iTypes.setSize(4);
            iTypes.setValue(value);
            iTypes.setName("int_pointer");
            iTypes.setOffset(this.symbolTable.getStaticOffset() + iTypes.getSize());

            return iTypes;

        } else if (typeName.equals("int_array")) {
            ArrayType arrayType = new ArrayType(varName, typeName, size, value, offset, maxLength, 0);
            this.symbolTable.setStaticOffset(this.symbolTable.getStaticOffset() + (size * maxLength));
            return arrayType;
        }

        return new ITypes();
    }
}
