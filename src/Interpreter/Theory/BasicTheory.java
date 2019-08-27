package Interpreter.Theory;

import javafx.scene.image.Image;

public class BasicTheory {

    private String message;
    private Image imege;

    public BasicTheory(String message, Image image){
        this.imege = image;
        this.message = message;
    }

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
