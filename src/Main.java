import Interpreter.Interpreter;

public class Main {

    public static void main(String[] args) {
        Interpreter interpreter;

        if(args.length == 0){
            System.out.println("ERROR: Numero de argumentos invalido");
        }else{

            interpreter = new Interpreter(args[0]);
            //interpreter.start(args);
        }
    }
}
