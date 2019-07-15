package Interpreter.Tokens;

import java.util.HashMap;

public class Dictionary {

    private HashMap<String, Type> dictionary;

    public Dictionary(){

        this.dictionary = new HashMap<>();
        createHashMap();

    }

    private void createHashMap(){

        //Variable types
        this.dictionary.put("int",Type.INT);
        this.dictionary.put("float",Type.FLOAT);
        this.dictionary.put("char",Type.CHAR);
        this.dictionary.put("double",Type.DOUBLE);
        this.dictionary.put("long",Type.LONG);

        //DynamicController memory
        this.dictionary.put("malloc",Type.MALLOC);
        this.dictionary.put("free",Type.FREE);
        this.dictionary.put("calloc",Type.CALLOC);
        this.dictionary.put("realloc",Type.REALLOC);

        //Arithmetic operands
        this.dictionary.put("=",Type.EQUAL);

        //Special characters
        this.dictionary.put("(",Type.OPEN_PAR);
        this.dictionary.put(")",Type.CLOSE_PAR);
        this.dictionary.put("[",Type.OPEN_BRA);
        this.dictionary.put("]",Type.CLOSE_BRA);
        this.dictionary.put("{",Type.OPEN_KEY);
        this.dictionary.put("}",Type.CLOSE_KEY);
        this.dictionary.put(";",Type.SEMICOLON);
        this.dictionary.put(",",Type.COMMA);

        //Logical operands
        this.dictionary.put("&",Type.AND);
        this.dictionary.put("&&",Type.ARITHMETIC_AND);
        this.dictionary.put("|", Type.OR);
        this.dictionary.put("||", Type.ARITHMETIC_OR);

        //Simple instructions
        this.dictionary.put("if",Type.IF);
        this.dictionary.put("do",Type.DO);
        this.dictionary.put("while",Type.WHILE);
        this.dictionary.put("for",Type.FOR);
        this.dictionary.put("else",Type.ELSE);

        //Extras
        this.dictionary.put("main",Type.MAIN);
        this.dictionary.put("void",Type.VOID);
    }

    public Type checkKeyWord(String word){
        return dictionary.get(word);

    }
}
