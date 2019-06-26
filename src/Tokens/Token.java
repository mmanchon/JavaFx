package Tokens;

public class Token {

    private Type id;
    private String lexema;

    public Token(){
        this.id = Type.NULL;
        this.lexema = "";

    }

    public Token(Type id, String lexema){
        this.id = id;
        this.lexema = lexema;
    }

    public Type getId(){
        return this.id;
    }

    public String getLexema(){
        return this.lexema;
    }

    public void setLexeme(String lexema) {
        this.lexema = lexema;
    }

    public void setId(Type id){
        this.id = id;
    }

    public void appendCharToString(char character){
        this.lexema += character;
    }

    public String getStringToken(){
        return "Token ("+this.id+" / '"+this.lexema+"')\n";
    }

}
