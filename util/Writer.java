package util;

import java.io.FileWriter;
import java.io.IOException;

public class Writer {
    FileWriter w;
    
    public void writeLine(String line) {
        try {
            w.write(line + "\n");
        }
        catch (IOException e) {
            System.out.println("Failed to write to file.");
            System.exit(0);
        }
    }
    
    public void close() {
        try {
            w.close();
        }
        catch (IOException e) {
            System.out.println("Failed to close the file.");
        }
    }
    
    public Writer(String filename) {
        try {
            w = new FileWriter(filename);
        }
        catch (IOException e) {
            System.out.println("Cannot open " + filename + " for writing.");
            System.exit(0);
        }
    }
}
