package Interpreter.Theory;

import javafx.scene.image.Image;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class InstructionsTheory extends BasicTheory {


    public InstructionsTheory(String message, Image image) throws URISyntaxException {
        super(message, image, new URI(""));
    }
}
