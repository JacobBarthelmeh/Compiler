package util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

public class Reader {

    public int linenumber, col, lastlinescolumns;
    private PushbackReader reader;

    public Reader(String filename) {
        linenumber = col = lastlinescolumns = 1;
        //  Prepare the reader
        try {
            reader = new PushbackReader(new FileReader(new File(filename)), 3);
        } //  Handle read error
        catch (IOException e) {
            System.out.println("Failed to read from " + filename
                    + ". Either it doesn't exist or has authority set too high.");
            System.exit(0);
        }

    }

    public void close() {
        try {
            reader.close();
        } catch (Exception e) {
            System.out.println("error in closing " + e);
        }
    }

    /**
     * Reads the next character without progressing the file pointer
     *
     * @return The character found
     */
    public char peekChar() {
        try {
            //  Only read if file can be read
            if (reader.ready()) {
                //  Read the character
                char c = Character.toChars(reader.read())[0];
                //  Unread the character to preserve file pointer
                reader.unread((int) c);
                return c;
            } //  Treat not ready as end of file
            else {
                return '\u001a';
            }
        } //  Failed to read - can't do anything.
        catch (IOException e) {
            System.out.println("Reading from file failed. Aborting.");
            System.exit(0);
            //  Preserve syntax
            return '\u001a';
        }
    }

    /**
     * Reads the next character and progresses the file pointer
     *
     * @return The character found
     */
    public char nextChar() {
        try {
            col++;
            if (reader.ready()) {
                int b = reader.read();
                if (b == -1) {
                    return '\u001a';
                }
                char c = Character.toChars(b)[0];
                if (c == '\n') {
                    //  Found new line
                    lastlinescolumns = c;
                    col = 1;
                    linenumber++;
                    //newline character is needed for state machines
                    return c;
                }
                return c;
            } else {
                //reader.close(); close symbol is needed and could still push char back
                System.out.println("Found end of file.");
                return '\u001a';
            }
        } catch (IOException e) {
            System.out.println("Reading from file failed. Aborting.");
            System.exit(0);
            return '\u001a';
        }
    }

    /**
     * Unreads a character
     *
     * @param c
     */
    public void ungetChar(char c) {
        try {
            if (c == '\n') {
                col = lastlinescolumns;
                linenumber--;
            }
            else {
                col--;
            }
            reader.unread((int)c);
//            reader.unread(Character.getNumericValue(c));
        } catch (IOException e) {
            System.out.println("Unreading from file failed. Aborting. " + e + " char was " + c);
            System.exit(0);
        }
    }

}
