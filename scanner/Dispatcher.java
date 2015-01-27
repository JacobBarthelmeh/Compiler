package scanner;

/* calls various fsa that scan for various tokens (first thing scanner calls)
   skips white space, peeks at first non white space character
   1) if letter call identifier fsa
   2) left, right () or digit
   .... if not the start of any token than throw an error (line number)

   function to skip over white space and place file pointer to the first
   possible character
*/

//   Messy import
import compiler.Token;
import java.io.*;
public class Dispatcher {
    /*
        Note- I use String for characters because they are easy
        to match Regex. Java's String has native Regex functionality,
        making state machines easy to imitate
    */
    //  Prototype for peekChar()
    //  This should only read a char from the file without moving the file pointer
    public String peekChar() {
        try {
            if (reader.ready()) {
                char c = Character.toChars(reader.read())[0];
                String in = "" + c;
                reader.unread((int)in.charAt(0));
                return in;
            }
            else { 
                return "\u001a";
            }
        }
        catch (IOException e) {
            System.out.println("Reading from file failed. Aborting.");
            System.exit(0);
            return null;
        }
    }
    //  Prototype for nextChar
    //  This should read a char from the file and move the file pointer
    public String nextChar() {
        try {
            if (reader.ready()) {
                char c = Character.toChars(reader.read())[0];
                String in = "" + c;
                return in;
            }
            else {
                return "\u001a";
            } 
        }
        catch (IOException e) {
            System.out.println("Reading from file failed. Aborting.");
            System.exit(0);
            return null;
        }
    }
    //  Get the next token.
    //  Should the Token class contain the String to be returned?
    public Token nextToken() {
        String next = peekChar();
        while (next.charAt(0) != '\u001a') {
            //  Match alphabet characters
            if (next.matches("\\w") || next.charAt(0) == '\'') {
                return l_handler.getToken(next);
            }
            //  Match digit characters
            else if (next.matches("\\d")) {
                return d_handler.getToken(next);
            }
            //  Symbol handler:
            //  Switch on the character
            else switch (next.charAt(0)) {
                case '{':
                    String str = next;
                    String nextc;
                    do {
                        nextc = nextChar();
                        str += nextc;
                        if (nextc.charAt(0) == '}') {
                            return new Token(str, Token.ID.COMMENT);
                        }
                    }
                    while (nextc.charAt(0) != '\u001a');
                    return new Token("Run-on comment on line " + linenumber + ".", Token.ID.RUN_ON_COMMENT);
                case ':':
                    next = nextChar();
                    //  Symbol := is special
                    if (next.charAt(0) == '=') {
                        nextChar();
                        return new Token(":=", Token.ID.ASSIGN);
                    }
                    //  Otherwise accept :
                    return new Token(":", Token.ID.COLON);
                case '>':
                    next = nextChar();
                    //  Symbol >= is special
                    if (next.charAt(0) == '=') {
                        nextChar();
                        return new Token(">=", Token.ID.GTHAN);
                    }
                    //  Otherwise accept >
                    return new Token(">", Token.ID.GTHAN);
                case '<':
                    next = nextChar();
                    //  Symbol <= is special
                    if (next.charAt(0) == '=') {
                        nextChar();
                        return new Token("<=", Token.ID.LTHAN);
                    }
                    //  Symbol <> is also special
                    else if (next.charAt(0) == '>') {
                        nextChar();
                        return new Token("<>", Token.ID.NEQUAL);
                    }
                    //  Otherwise accept <
                    return new Token("<=", Token.ID.LTHAN);
                case ',':
                    //  Proceed to the next character to advance the file pointer
                    nextChar();
                    return new Token(".", Token.ID.COMMA);
                case '=':
                    //  Proceed to the next character to advance the file pointer
                    nextChar();
                    return new Token("=", Token.ID.EQUAL);
                case '-':
                    //  Proceed to the next character to advance the file pointer
                    nextChar();
                    return new Token("-", Token.ID.MINUS);
                case '(':
                    //  Proceed to the next character to advance the file pointer
                    nextChar(); 
                    return new Token("(", Token.ID.LPAREN);
                case '.':
                    //  Proceed to the next character to advance the file pointer
                    nextChar(); 
                    return new Token(".", Token.ID.PERIOD);
                case '+':
                    //  Proceed to the next character to advance the file pointer
                    nextChar(); 
                    return new Token("+", Token.ID.PLUS);
                case ')':
                    //  Proceed to the next character to advance the file pointer
                    nextChar(); 
                    return new Token(")", Token.ID.RPAREN);
                case ';':
                    //  Proceed to the next character to advance the file pointer
                    nextChar(); 
                    return new Token(";", Token.ID.SCOLON);
                case '*':
                    //  Proceed to the next character to advance the file pointer
                    nextChar(); 
                    return new Token("*", Token.ID.TIMES);
                case '\r':case '\n':
                    //  Problem arises for the \r\n and \n\r cases
                    next = peekChar();
                    //  Free to increment another symbol before incrementing
                    if (next.charAt(0) == '\r' || next.charAt(0) == '\n') {
                        nextChar();
                    }
                    next = nextChar();
                    linenumber++;
                    System.out.println("Debug: Found new line.");
                    break;
                case '\t':case ' ':
                    next = nextChar();
                    System.out.println("Debug: Found space.");
                    break;
                default:
                    char nextc2 = next.charAt(0);
                    nextChar();
                    return new Token("Unknown symbol " + nextc2 + " at line " + linenumber, Token.ID.ERROR);
            }
        }
        try {
            reader.close();
        }
        catch (IOException e) {
            System.out.println("Closing from file failed. Aborting.");
            System.exit(0);
            return null;
        }
        
        return new Token("EOF", Token.ID.EOF);
    }

/*
    Variables and Construction
*/

    //  Handlers for when symbols are found
    private LetterHandler l_handler;
    private DigitHandler d_handler;

    //  File handling
    private PushbackReader reader;
    private int linenumber;


    public Dispatcher(String filename) {
        try {
            reader =
                new PushbackReader(
                    new FileReader(
                        new File(filename)));
        }
        catch (IOException e) {
            System.out.println("Failed to read from " + filename + ". Either it doesn't exist or has authority set too high.");
            System.exit(0);
        }
        linenumber = 0;
        //  prepare the handlers
        l_handler = new LetterHandler(this);
        d_handler = new DigitHandler(this);
    }
}