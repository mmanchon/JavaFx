package Interpreter.Errors;

import javafx.scene.image.Image;

public class BasicError extends Exception {

    private String message;
    private Image imege;

    public BasicError(String message, Image image){
        this.imege = image;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Image getImege() {
        return imege;
    }

    public void setImege(Image imege) {
        this.imege = imege;
    }
}
