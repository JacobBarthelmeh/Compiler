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
            col++;
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
    /**
     * Gets the next token from the file. Be sure to check for Error tokens.
     * @return The next token in the file
     */
    public Token nextToken() {
        //  Always progress from the first character found
        char c = nextChar();
        while (c != '\u001a') {
            switch (c){
            /* 
                Dispatcher is in-line right now because there's no need
                to make a separate class that has public access to all of the
                variables.
            */
                    //  Dispatcher handles new lines
                    case '\r': case '\n':
                        //  Problem arises for the \r\n and \n\r cases
                        c = peekChar();
                        //  Free to increment another symbol before incrementing
                        if (c == '\r' || c == '\n') {
                            nextChar();
                        }
                        linenumber++;
                        col = 1;
                        break;
                    //  Ignore whitespace
                    case '\t': case ' ':
                        break;
                    //  Checks that require special attention
                    case '{':
                        while (c != '}' && c != '\u001a') {
                            c = nextChar();
                            if (c == '\r' || c == '\n') {
                                c = peekChar();
                                //  Free to increment another symbol before incrementing
                                if (c == '\r' || c == '\n') {
                                    nextChar();
                                }
                                linenumber++;
                                col = 0;
                            }
                        }
                        if (c == '\u001a') {
                            System.out.println("Syntax Error: " +
                                    "Comment opened but not closed.");
                            return null;
                        }
                        break;
                    case '}':
                        System.out.println("Syntax Error at line " + linenumber + " col " + col + ": " +
                                "Comment closed but not opened.");
                        return null;
                    case '\'':
                        String str = "" + c;
                        do {
                            c = nextChar();
                            str += c;
                        }
                        while (c != '\'' && c != '\u001a');
                        if (c == '\u001a') {
                            System.out.println("Syntax Error: " +
                                    "Quotation mark opened but not closed.");
                            return null;
                        }
                        return new Token(str, Token.ID.STRING_LIT);
                    case ':':
                        c = peekChar();
                        if (c == '=') {
                            nextChar();
                            return new Token(":=", Token.ID.ASSIGN);
                        }
                        return new Token(":", Token.ID.COLON);
                    case '>':
                        c = peekChar();
                        if (c == '=') {
                            nextChar();
                            return new Token(">=", Token.ID.GEQUAL);
                        }
                        return new Token(">", Token.ID.GTHAN);
                    case '<':
                        c = peekChar();
                        switch (c){
                            case '=':
                                nextChar();
                                return new Token("<=", Token.ID.LEQUAL);
                            case '>':
                                nextChar();
                                return new Token("<>", Token.ID.NEQUAL);
                            default:
                                return new Token("<", Token.ID.LTHAN);
                        }
                    //  Trivial checks
                    case ',': return new Token(",", Token.ID.COMMA);
                    case '=': return new Token("=", Token.ID.EQUAL);
                    case '/': return new Token(",", Token.ID.FLOAT_DIVIDE);
                    case '(': return new Token("(", Token.ID.LPAREN);
                    case ')': return new Token(")", Token.ID.RPAREN);
                    case '-': return new Token("-", Token.ID.MINUS);
                    case '.': return new Token(".", Token.ID.PERIOD);
                    case '+': return new Token("+", Token.ID.PLUS);
                    case ';': return new Token(";", Token.ID.SCOLON);
                    case '*': return new Token("*", Token.ID.TIMES);
                    default:
                        if (("" + c).matches("\\d")) {
                            return d_handler.getToken(c);
                        }
                        //  Though \w matches numbers, numbers are already checked previously
                        if (("" + c).matches("(\\_|\\w)")) {
                            return l_handler.getToken(c);
                        }
                        System.out.println("Syntax Error at line " + linenumber + " col " + col + ": " +
                                "Unrecognized symbol " + c);
                        return null;
            }
            //  Whitespace/newline/comment code has us proceed to the next symbol
            c = nextChar();
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
    //  File handling
    private PushbackReader reader;
    private int linenumber, col;
    /**
     * Get the line number that the scanner is currently on
     * @return The line number
     */
    public int linenumber() {
        return linenumber;
    }
    public int col() {
        return col;
    }
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
        //  prepare the handlers
        l_handler = new LetterHandler(this);
        d_handler = new DigitHandler(this);
    }
}