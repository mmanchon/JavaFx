package Interpreter.Tokens;

public enum Type {

    //All necessary for variables
    ID,
    ID_POINTER,
    INT,
    FLOAT,
    CHAR,
    DOUBLE,
    LONG,
    STRING,

    //Instructions for dynamic memory
    MALLOC,
    FREE,
    CALLOC,
    REALLOC,

    //Arthmetic symbols and others
    EQUAL,
    SEMICOLON,
    OPEN_PAR,
    CLOSE_PAR,
    OPEN_BRA,
    CLOSE_BRA,
    OPEN_KEY,
    CLOSE_KEY,
    COMMA,


    AND,
    OR,
    ARITHMETIC_AND,
    ARITHMETIC_OR,
    OPER_REL,

    //Instructions
    WHILE,
    DO,
    IF,
    FOR,
    ELSE,
    PRINT,
    SCAN,


    //Arithmetic operations
    PLUS,
    MINUS,
    DIVIDE,
    MULTIPLY,

    //Constantes
    INT_CNST,
    FLOAT_CNST,
    LONG_CNST,

    //Extras
    LINE_BREAK,
    MAIN,
    VOID,
    RETURN,
    NULL,
    EOF
}
