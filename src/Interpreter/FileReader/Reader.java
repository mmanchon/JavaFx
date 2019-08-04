package Interpreter.FileReader;

import Interpreter.Tokens.Dictionary;
import Interpreter.Tokens.Token;
import Interpreter.Tokens.Type;

import java.io.*;
import java.nio.charset.Charset;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.sym.error;

public class Reader implements Cloneable {

    private Dictionary dictionary;
    private String fileName;
    private RandomAccessFile randomAccessFile;

    private final char ID = 1;
    private final char ID_POINTER = 2;
    private final char NUMERIC = 3;
    private final char DOUBLE_COMAS = 4;
    private final char SPACE = 5;
    private final char SPECIAL = 6;
    private final char RELATIONALS = 7;
    private final char ARITHMETIC_SMPL = 8;
    private final char ARITHMETIC_CMPLX=9;
    private final int ERROR = 11;
    private int numLines = 0;
    private int character;

    public Reader(){
        this.dictionary = new Dictionary();
    }

    public void openNewFile(String filename){
        this.fileName = filename;
        if(this.randomAccessFile != null)this.closeFile();

        try {

            this.randomAccessFile = new RandomAccessFile(fileName,"r");
            this.character = this.randomAccessFile.read();

        } catch (FileNotFoundException e) {
            System.out.println("Error opening codeArea");
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
                        while (detectCase((char) this.character) == ID || detectCase((char) this.character) == NUMERIC) {
                            token.appendCharToString((char) character);
                            this.character = this.randomAccessFile.read();
                        }

                        token.setId(Type.ID);

                        break;
                    case ID_POINTER:
                        //token.appendCharToString((char) character);
                        this.character = this.randomAccessFile.read();

                        while(detectCase((char) this.character) == ID_POINTER ){
                            this.character = this.randomAccessFile.read();

                        }
                        while (detectCase((char) this.character) == ID || detectCase((char) this.character) == NUMERIC) {
                            token.appendCharToString((char) character);
                            this.character = this.randomAccessFile.read();
                        }

                        token.setId(Type.ID_POINTER);


                        break;
                    case NUMERIC:

                        while (detectCase((char) this.character) == NUMERIC) {
                            token.appendCharToString((char) character);
                            this.character = this.randomAccessFile.read();
                        }

                        token.setId(Type.INT_CNST);

                        //this.character = this.randomAccessFile.read();

                        break;
                    case SPACE:
                        this.character = this.randomAccessFile.read();
                        if(this.character == '\n'){
                            token.setId(Type.LINE_BREAK);
                            this.numLines++;
                        }
                        break;
                    case ARITHMETIC_SMPL:

                        if(this.character == '+'){
                            token.setId(Type.PLUS);
                        }else if(this.character == '-'){
                            token.setId(Type.MINUS);
                        }
                        token.appendCharToString((char)this.character);

                        this.character = (char) this.randomAccessFile.read();

                        break;
                    case ARITHMETIC_CMPLX:

                        char aux = (char) this.randomAccessFile.read();

                        //Miramos que no sea un comentario

                        if (this.character == '/' && aux == '/') {

                            this.character = (char) this.randomAccessFile.read();

                            while (this.character != '\n') {
                                this.character = (char) this.randomAccessFile.read();
                            }

                            this.character = (char) this.randomAccessFile.read();

                        } else {

                            if(this.character == '*'){
                                token.setId(Type.MULTIPLY);
                            }else if(this.character == '/'){
                                token.setId(Type.DIVIDE);
                            }
                            token.appendCharToString((char)this.character);

                            this.character = aux;
                        }
                        break;
                    case RELATIONALS:
                        token.appendCharToString((char) this.character);
                        token.setId(Type.OPER_REL);

                        this.character = (char) this.randomAccessFile.read();

                        if (this.character == '=') {

                            token.appendCharToString((char) this.character);

                            this.character = (char) this.randomAccessFile.read();

                        }

                        //this.character = this.randomAccessFile.read();

                        break;
                    case SPECIAL:
                        token.appendCharToString((char) this.character);
                        if(this.character == '&' || this.character == '|'){
                            this.character = this.randomAccessFile.read();
                            if(this.character == '&' || this.character == '|'){
                                token.appendCharToString((char) this.character);
                                this.character = this.randomAccessFile.read();
                            }
                        }else {
                            this.character = this.randomAccessFile.read();
                        }
                        break;
                    case DOUBLE_COMAS:

                        token.setId(Type.STRING);

                        this.character = (char) this.randomAccessFile.read();

                        while (detectCase((char)this.character) != DOUBLE_COMAS) {
                            token.appendCharToString((char)this.character);
                            this.character = (char) this.randomAccessFile.read();

                        }

                        this.character = (char) this.randomAccessFile.read();

                        break;
                    //case DOUBLE_COMAS:
                    //      this.character = this.randomAccessFile.read();
                    //break;
                    default:
                        while (this.detectCase((char) this.character) != SPACE && this.detectCase((char) this.character) != ERROR) {
                            token.appendCharToString((char) character);
                            this.character = this.randomAccessFile.read();
                        }
                        this.character = this.randomAccessFile.read();
                        token.setId(Type.NULL);
                        if (this.character == -1) token.setId(Type.EOF);
                        break;
                }

                this.analyseWord(token);

            } catch (IOException e) {
                System.out.println("Error while reading codeArea");
                e.printStackTrace();
            }
        }while (token.getId() == Type.NULL);

        return token;
    }

    public void closeFile(){
        try {
            this.randomAccessFile.close();
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
            return RELATIONALS;
        } else if (ch.matches("[+\\-]")) {
            return ARITHMETIC_SMPL;
        } else if (ch.matches("[*\\/]")) {
            return ARITHMETIC_CMPLX;
        } else if (ch.matches("[\\(\\);\\[\\]:\\,}\\{\\&\\|]")) {
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

    public void restart(){
        if(this.randomAccessFile != null){
            this.closeFile();
            this.openNewFile(this.fileName);
            this.numLines = 0;
        }
    }

    public int getNumLines(){
        return this.numLines;
    }

    public void setNumLines(int numLines){
        this.numLines = numLines;
    }

    public Object clone()
        
    {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void goToLine(int line){
        try {
            this.randomAccessFile.seek(0);
            for(int i = 0; i < line; i++){
                this.randomAccessFile.readLine();
            }
            this.numLines = line;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
