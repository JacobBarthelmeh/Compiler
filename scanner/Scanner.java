package scanner;
import compiler.Token;
import java.io.*;
public class Scanner {
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
        //  Readable code needs no comments
        try {
            if (reader.ready()) {
                return Character.toChars(reader.read())[0];
            }
            else {
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
     * Gets the next token from the file. Be sure to check for Error tokens.
     * @return The next token in the file
     */
    public Token nextToken() {
        //  Always progress from the first character found
        char c = nextChar();
        while (c != '\u001a') {
            /* 
                Dispatcher is in-line right now because there's no need
                to make a separate class that has public access to all of the
                variables.
            */
            //  Dispatcher handles new lines
            if (c == '\r' || c == '\n') {
                //  Problem arises for the \r\n and \n\r cases
                c = peekChar();
                //  Free to increment another symbol before incrementing
                if (c == '\r' || c == '\n') {
                    nextChar();
                }
                c = nextChar();
                linenumber++;
            }
            //  Dispatcher handles spaces
            else if (c == '\t' || c == ' ') {
                c = nextChar();
            }
            //  Match alphabet characters
            else if (("" + c).matches("(_|a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z)")) {
                return l_handler.getToken(c);
            }
            //  Match digit characters
            else if (("" + c).matches("(0|1|2|3|4|5|6|7|8|9)")) {
                return d_handler.getToken(c);
            }
            //  Symbol handler:
            else {
                return s_handler.getToken(c);
            }
        }
        //  Always close files
        try {
            reader.close();
        }
        catch (IOException e) {
            System.out.println("Closing file failed. Aborting.");
            System.exit(0);
            return null;
        }
        //  End of file token returns when nothing else is found
        return new Token("EOF", Token.ID.EOF);
    }
    //  Handlers for when characters are found
    private final LetterHandler l_handler;
    private final DigitHandler d_handler;
    private final SymbolHandler s_handler;
    //  File handling
    private PushbackReader reader;
    private int linenumber;
    /**
     * Get the line number that the scanner is currently on
     * @return The line number
     */
    public int linenumber() {
        return linenumber;
    }
    /**
     * Constructs a dispatcher to read from a file
     * @param filename The name of the file to search for
     */
    public Scanner(String filename) {
        linenumber = 0;
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
        //  prepare the handlers
        l_handler = new LetterHandler(this);
        d_handler = new DigitHandler(this);
        s_handler = new SymbolHandler(this);
    }
}