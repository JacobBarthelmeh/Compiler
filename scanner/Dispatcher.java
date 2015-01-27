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
    public char peekChar() {
        try {
            if (reader.ready()) {
                char c = Character.toChars(reader.read())[0];
                reader.unread((int)c);
                return c;
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
    //  Prototype for nextChar
    //  This should read a char from the file and move the file pointer
    public char nextChar() {
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
    //  Get the next token.
    //  Should the Token class contain the String to be returned?
    public Token nextToken() {
        char c = nextChar();
        while (c != '\u001a') {
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
                //System.out.println("Debug: Found new line.");
            }
            //  Dispatcher handles spaces
            else if (c == '\t' || c == ' ') {
                c = nextChar();
                //System.out.println("Debug: Found space.");
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
                Token t = s_handler.getToken(c);
                
                //  Invalid symbol
                if (t == null) {
                    nextChar();
                    return new Token("Unknown symbol " + c + " at line " + linenumber, Token.ID.ERROR);
                }
                else {
                    return t;
                }
            }
        }
        try {
            reader.close();
        }
        catch (IOException e) {
            System.out.println("Closing file failed. Aborting.");
            System.exit(0);
            return null;
        }
        
        return new Token("EOF", Token.ID.EOF);
    }
/*
    Variables and Construction
*/

    //  Handlers for when symbols are found
    private final LetterHandler l_handler;
    private final DigitHandler d_handler;
    private final SymbolHandler s_handler;

    //  File handling
    private PushbackReader reader;
    private int linenumber;
    public int linenumber() {
        return linenumber;
    }


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
        s_handler = new SymbolHandler(this);
    }
}