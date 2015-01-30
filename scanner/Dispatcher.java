package scanner;
import compiler.Token;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
public class Dispatcher {
    public Token nextToken() {
        //  Precondition: c is the first character in the file
        //      OR the first character that hasn't been returned in 
        //      a previous token.
        char c = peekChar();
        //  Note- scanner handles new lines
        switch (c) {
            //  However, excess ones should be ignoreed
            case '\r': case '\n':
            //  Dispatcher ignores whitespace
            case '\t': case ' ':
                nextChar();
                return nextToken();
            //  Dispatcher extracts comments
            case '{':
                nextChar();   //  Accept the {
                while (c != '}' && c != '\u001a') {
                    c = nextChar();   //  Accept next character always
                }
                if (c == '\u001a') {
                    System.out.println("Syntax Error: " +
                            "Comment opened but not closed.");
                    return null;
                }
                nextChar();
                return nextToken();
            //  Dispatcher identifiers unclosed comments
            case '}':
                System.out.println("Syntax Error at line " +
                        linenumber + " col " + col + ": " +
                        "Comment closed but not opened.");
                nextChar();
                return null;
            //  Begin FSAs!
            case '\'': return FSA.TEST_STRING_LIT(this);
            case ':': return FSA.TEST_COLON(this);
            case '>': return FSA.TEST_GTHAN(this);
            case '<': return FSA.TEST_LTHAN(this);
            case ',': return FSA.TEST_COMMA(this);
            case '=': return FSA.TEST_EQUAL(this);
            case '/': return FSA.TEST_FLOAT_DIVIDE(this);
            case '(': return FSA.TEST_LPAREN(this);
            case ')': return FSA.TEST_RPAREN(this);
            case '-': return FSA.TEST_MINUS(this);
            case '.': return FSA.TEST_PERIOD(this);
            case '+': return FSA.TEST_PLUS(this);
            case ';': return FSA.TEST_SCOLON(this);
            case '*': return FSA.TEST_TIMES(this);
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
                return FSA.TEST_DIGIT(this);
            case 'a': case 'b': case 'c': case 'd': case 'e':
            case 'f': case 'g': case 'h': case 'i': case 'j':
            case 'k': case 'l': case 'm': case 'n': case 'o':
            case 'p': case 'q': case 'r': case 's': case 't':
            case 'u': case 'v': case 'w': case 'x': case 'y':
            case 'z': case 'A': case 'B': case 'C': case 'D':
            case 'E': case 'F': case 'G': case 'H': case 'I':
            case 'J': case 'K': case 'L': case 'M': case 'N':
            case 'O': case 'P': case 'Q': case 'R': case 'S':
            case 'T': case 'U': case 'V': case 'W': case 'X':
            case 'Y': case 'Z': case '_':
                return FSA.TEST_LETTER(this);
            case '\u001a':
                return new Token("\u001a", Token.ID.EOF);
        }
        nextChar();
        System.out.println("Unidenfied symbol " + c + " could not be read.");
        return null;
    }
    public int linenumber, col;
    public Dispatcher(String filename) {
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

}
