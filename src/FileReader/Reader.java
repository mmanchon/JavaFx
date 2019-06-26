package FileReader;

import Tokens.Dictionary;
import Tokens.Token;
import Tokens.Type;

import java.io.*;
import java.nio.charset.Charset;

public class Reader {

    private BufferedReader bufferedReader;
    private Dictionary dictionary;

    private final char ID = 1;
    private final char ID_POINTER = 2;
    private final char NUMERIC = 3;
    private final char DOUBLE_COMAS = 4;
    private final char SPACE = 5;
    private final char SPECIAL = 6;
    private final char RELATIONALS = 7;
    private final int ERROR = 8;

    private int character;

    public Reader(String filename){

        try {

            this.bufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filename), Charset.forName("UTF-8"))
            );

            this.character = this.bufferedReader.read();
            this.dictionary = new Dictionary();

        } catch (FileNotFoundException e) {
            System.out.println("Error opening file");
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Error while reading character");
            e.printStackTrace();
        }

    }

    public Token extractToken(){
        Token token = new Token();

        do {
            try {

                switch (detectCase((char) this.character)) {
                    case ID:
                        while (detectCase((char) this.character) == ID) {
                            token.appendCharToString((char) character);
                            this.character = this.bufferedReader.read();
                        }

                        token.setId(Type.ID);

                        break;
                    case ID_POINTER:
                        token.appendCharToString((char) character);
                        this.character = this.bufferedReader.read();

                        while (detectCase((char) this.character) == ID) {
                            token.appendCharToString((char) character);
                            this.character = this.bufferedReader.read();
                        }

                        token.setId(Type.ID_POINTER);


                        break;
                    case NUMERIC:

                        while (detectCase((char) this.character) == NUMERIC) {
                            token.appendCharToString((char) character);
                            this.character = this.bufferedReader.read();
                        }

                        token.setId(Type.INT_CNST);

                        //this.character = this.bufferedReader.read();

                        break;
                    case SPACE:
                        this.character = this.bufferedReader.read();
                        if(this.character == '\n'){
                            token.setId(Type.LINE_BREAK);
                        }
                        break;
                    case RELATIONALS:
                        if (this.character == '=') {
                            token.appendCharToString((char) this.character);
                            token.setId(Type.EQUAL);
                        }

                        this.character = this.bufferedReader.read();

                        break;
                    case SPECIAL:
                        token.appendCharToString((char) this.character);
                        this.character = this.bufferedReader.read();
                        break;
                    //case DOUBLE_COMAS:
                    //      this.character = this.bufferedReader.read();
                    //break;
                    default:
                        while (this.detectCase((char) this.character) != SPACE && this.detectCase((char) this.character) != ERROR) {
                            token.appendCharToString((char) character);
                            this.character = this.bufferedReader.read();
                        }
                        this.character = this.bufferedReader.read();
                        token.setId(Type.NULL);
                        if (this.character == -1) token.setId(Type.EOF);
                        break;
                }

                this.analyseWord(token);

            } catch (IOException e) {
                System.out.println("Error while reading file");
                e.printStackTrace();
            }
        }while (token.getId() == Type.NULL);

        return token;
    }

    public void closeFile(){
        try {
            this.bufferedReader.close();
        } catch (IOException e) {
            System.out.println("Error en cerrar el fichero" + e.getMessage());
            e.printStackTrace();
        }
    }

    private int detectCase(char character){
        String ch = Character.toString(character);
        if (ch.matches("[a-zA-Z]")) {
            return ID;
        } else if (ch.matches("[0-9]")) {
            return NUMERIC;
        } else if (ch.matches("[*]")){
            return ID_POINTER;
        } else if (ch.matches("[<->]|[!]")) {
            return RELATIONALS;/*
        } else if (ch.matches("[+\\-]")) {
            return ARITHMETIC_SMPL;
        } else if (ch.matches("[*\\/]")) {
            return ARITHMETIC_CMPLX;*/
        } else if (ch.matches("[\\(\\);\\[\\]:\\,}\\{\\&]")) {
            return SPECIAL;
        } else if (ch.matches("[ ]|[\0]|[\\t]|[\\n]|[\\r]")) {
            return SPACE;
        } else if (ch.matches("[\"]")) {
            return DOUBLE_COMAS;
        //} else if (ch.matches("[_]")) {
       //     return UNDERSCORE;
        } else {
            return ERROR;
        }
    }

    private void analyseWord(Token token){
        Type type;

        token.setLexeme(token.getLexema().toLowerCase());

        type = this.dictionary.checkKeyWord(token.getLexema());

        if(type != null){
            token.setId(type);
        }

    }

}
