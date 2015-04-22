package util;

import java.io.FileWriter;
import java.io.IOException;

public class Writer {
    String str, filename;
    FileWriter w;
    
    public void writeLine(String line) {
        str += line + "\n";
    }
    
    //  This format makes it so the writer only creates a nonexisting file if
    //  the compile was successful
    public void close() {
        try {
            w = new FileWriter(filename);
            w.write(str);
            w.close();
        }
        catch (IOException e) {
            System.out.println("Failed to close the file.");
        }
    }
    
    public Writer(String filename) {
        str = "";
        this.filename = filename;
    }
}
