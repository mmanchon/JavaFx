package Tokens;

public enum Type {

    //All necessary for variables
    ID,
    ID_POINTER,
    INT,
    FLOAT,
    CHAR,
    DOUBLE,
    LONG,

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

    //Instructions
    WHILE,
    DO,
    IF,
    FOR,

    //Constantes
    INT_CNST,
    FLOAT_CNST,
    LONG_CNST,

    //Extras
    NULL,
    EOF
}
