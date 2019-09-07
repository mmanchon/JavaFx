package Interpreter.Theory;

import javafx.scene.image.Image;

import java.net.URI;

public class BasicTheory {

    private String message;
    private URI url;
    private Image imege;

    public BasicTheory(String message, Image image, URI url){
        this.imege = image;
        this.message = message;
        this.url = url;

    }

    public BasicTheory(){}

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

    public URI getUrl() {
        return url;
    }

    public void setUrl(URI url) {
        this.url = url;
    }
}
