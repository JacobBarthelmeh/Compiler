package scanner;
import compiler.Token;
import java.io.*;
public class Scanner {
    /**
     * Gets the next token from the file. Be sure to check for Error tokens.
     * @return The next token in the file
     */
    public Token nextToken() {
        Token t = d.nextToken();
        if (t != null && t.getID() == Token.ID.EOF) {
            //  Always close files
            try {
                reader.close();
            }
            catch (IOException e) {
                System.out.println("Closing file failed. Aborting.");
                System.exit(0);
                return null;
            }
        }
        return t;
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
                char c = Character.toChars(reader.read())[0];
                if (c == '\r' || c == '\n') {
                    //  Found new line
                    col = 0;
                    linenumber++;
                    //  We only want to remove \r, \n, \r\n, or \n\r
                    //  Any more might cause empty lines to be ignored
                    char c2 = Character.toChars(reader.read())[0];
                    if (c2 == '\r' || c == '\n') {
                        c2 = Character.toChars(reader.read())[0];
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
    //  File handling
    private PushbackReader reader;
    private Dispatcher d;
    public int linenumber, col;
    /**
     * Constructs a dispatcher to read from a file
     * @param filename The name of the file to search for
     */
    public Scanner(String filename) {
        linenumber = col = 1;
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
        d = new Dispatcher(this);
    }
}