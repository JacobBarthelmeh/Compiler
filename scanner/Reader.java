package scanner;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

public class Reader {
    public int linenumber, col;
    private PushbackReader reader;
    public Reader(String filename) {
        linenumber = col = 0;
        //  Prepare the reader
        try {
            reader = new PushbackReader(new FileReader(new File(filename)));
        }
        //  Handle read error
        catch (IOException e) {
            System.out.println("Failed to read from " + filename +
                    ". Either it doesn't exist or has authority set too high.");
            System.exit(0);
        }

    }
        /**
     * Reads the next character without progressing the file pointer
     * @return The character found
     */
    public char peekChar() {
        try {
            //  Only read if file can be read
            if (reader.ready()) {
                //  Read the character
                char c = Character.toChars(reader.read())[0];
                //  Unread the character to preserve file pointer
                reader.unread((int)c);
                return c;
            }
            //  Treat not ready as end of file
            else { 
                return '\u001a';
            }
        }
        //  Failed to read - can't do anything.
        catch (IOException e) {
            System.out.println("Reading from file failed. Aborting.");
            System.exit(0);
            //  Preserve syntax
            return '\u001a';
        }
    }
    /**
     * Reads the next character and progresses the file pointer
     * @return The character found
     */
    public char nextChar() {
        try {
            col++;
            if (reader.ready()) {
                int b = reader.read();
                if(b == -1) {
                    return '\u001a';
                }
                char c = Character.toChars(b)[0];
                if (c == '\r' || c == '\n') {
                    //  Found new line
                    col = 0;
                    linenumber++;
                    //  We only want to remove \r, \n, \r\n, or \n\r
                    //  Any more might cause empty lines to be ignored
                    b = reader.read();
                    if(b == -1) {
                        return '\u001a';
                    }
                    char c2 = Character.toChars(b)[0];
                    if (c2 == '\r' || c2 == '\n') {
                        b = reader.read();
                        if(b == -1) {
                            return '\u001a';
                        }
                        c2 = Character.toChars(b)[0];
                    }
                    c = c2;
                }
                return c;
            }
            else {
                System.out.println("Found end of file.");
                return '\u001a';
            }
        }
        catch (IOException e) {
            System.out.println("Reading from file failed. Aborting.");
            System.exit(0);
            return '\u001a';
        }
    }
    /**
     * Unreads a character
     * @param c 
     */
    public void ungetChar(char c) {
        try {
            reader.unread((int)c);
        }
        catch (IOException e) {
            System.out.println("Unreading from file failed. Aborting.");
            System.exit(0);
        }
    }
    
}
