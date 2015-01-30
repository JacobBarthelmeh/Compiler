package scanner;
import compiler.Token;
public class Dispatcher {
    public Token nextToken() {
        //  Precondition: c is the first character in the file
        //      OR the first character that hasn't been returned in 
        //      a previous token.
        char c = s.peekChar();
        //  Note- scanner handles new lines
        switch (c) {
            //  However, excess ones should be ignoreed
            case '\r': case '\n':
            //  Dispatcher ignores whitespace
            case '\t': case ' ':
                s.nextChar();
                return nextToken();
            //  Dispatcher extracts comments
            case '{':
                s.nextChar();   //  Accept the {
                while (c != '}' && c != '\u001a') {
                    c = s.nextChar();   //  Accept next character always
                }
                if (c == '\u001a') {
                    System.out.println("Syntax Error: " +
                            "Comment opened but not closed.");
                    return null;
                }
                s.nextChar();
                return nextToken();
            //  Dispatcher identifiers unclosed comments
            case '}':
                System.out.println("Syntax Error at line " +
                        s.linenumber + " col " + s.col + ": " +
                        "Comment closed but not opened.");
                s.nextChar();
                return null;
            //  Begin FSAs!
            case '\'': return FSA.TEST_STRING_LIT(s);
            case ':': return FSA.TEST_COLON(s);
            case '>': return FSA.TEST_GTHAN(s);
            case '<': return FSA.TEST_LTHAN(s);
            case ',': return FSA.TEST_COMMA(s);
            case '=': return FSA.TEST_EQUAL(s);
            case '/': return FSA.TEST_FLOAT_DIVIDE(s);
            case '(': return FSA.TEST_LPAREN(s);
            case ')': return FSA.TEST_RPAREN(s);
            case '-': return FSA.TEST_MINUS(s);
            case '.': return FSA.TEST_PERIOD(s);
            case '+': return FSA.TEST_PLUS(s);
            case ';': return FSA.TEST_SCOLON(s);
            case '*': return FSA.TEST_TIMES(s);
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
                return FSA.TEST_DIGIT(s);
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
                return FSA.TEST_LETTER(s);
            case '\u001a':
                return new Token("\u001a", Token.ID.EOF);
        }
        s.nextChar();
        System.out.println("Unidenfied symbol " + c + " could not be read.");
        return null;
    }
    //  Handlers for when characters are found
    private Scanner s;
    public Dispatcher(Scanner scanner) {
        this.s = scanner;
    }
}
