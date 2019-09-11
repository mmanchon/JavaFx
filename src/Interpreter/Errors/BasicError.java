package Interpreter.Errors;

import javafx.scene.image.Image;

import java.net.URI;

public class BasicError extends Exception {

    private String message;
    private Image imege;
    private URI url;

    public BasicError(String message, Image image, URI url){
        this.imege = image;
        this.message = message;
        this.url = url;
    }

    public BasicError(){}

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

    public URI getUrl() {
        return url;
    }

    public void setUrl(URI url) {
        this.url = url;
    }
}
