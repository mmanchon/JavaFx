package GUI.Models;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Editor {

    public void save(TextFile textFile){

        try {
            Files.write(textFile.getFile(),textFile.getContent(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.out.println("Unable to write to codeArea");
            e.printStackTrace();
        }
    }

    public TextFile load(Path file){
        try {

            List<String> lines = Files.readAllLines(file);
            return new TextFile(file,lines);

        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }

    }

    public void close(){
        System.exit(0);
    }
}
