package scanner;
import compiler.Token;
public class Dispatcher {
    public Token nextToken() {
        //  Precondition: c is the first character in the file
        //      OR the first character that hasn't been returned in 
        //      a previous token.
        char c = r.peekChar();
        int row = r.linenumber, col = r.col;
        //  Note- scanner handles new lines
        switch (c) {
            //  Excess newlines have been causing lissues (temp fix)
            case '\r': case '\n':
            //  Dispatcher ignores whitespace
            case '\t': case ' ':
                r.nextChar();
                return nextToken();
            //  Dispatcher extracts comments
            case '{':
                r.nextChar();   //  Accept the {
                while (c != '}' && c != '\u001a') {
                    c = r.nextChar();   //  Accept next character always
                }
                if (c == '\u001a') {
                    return new Token("Comment not closed", Token.ID.RUN_COMMENT, row, col);
                }
                r.nextChar();
                return nextToken();
            //  Dispatcher identifiers unclosed comments
            case '}':
                r.nextChar();
                return new Token("Comment not closed at " +
                        r.linenumber + " col " + r.col, Token.ID.ERROR, row, col);
            //  Begin FSAs!
            case '\'': return FSA.TEST_STRING_LIT(r, row, col);
            case ':': return FSA.TEST_COLON(r, row, col);
            case '>': return FSA.TEST_GTHAN(r, row, col);
            case '<': return FSA.TEST_LTHAN(r, row, col);
            case ',': return FSA.TEST_COMMA(r, row, col);
            case '=': return FSA.TEST_EQUAL(r, row, col);
            case '/': return FSA.TEST_FLOAT_DIVIDE(r, row, col);
            case '(': return FSA.TEST_LPAREN(r, row, col);
            case ')': return FSA.TEST_RPAREN(r, row, col);
            case '-': return FSA.TEST_MINUS(r, row, col);
            case '.': return FSA.TEST_PERIOD(r, row, col);
            case '+': return FSA.TEST_PLUS(r, row, col);
            case ';': return FSA.TEST_SCOLON(r, row, col);
            case '*': return FSA.TEST_TIMES(r, row, col);
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
                return FSA.TEST_DIGIT(r, row, col);
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
                return FSA.TEST_LETTER(r, row, col);
            case '\u001a':
                return new Token("\u001a", Token.ID.EOF, row, col);
        }
        r.nextChar();
        return new Token("Unidentified symbol " + c, Token.ID.ERROR, row, col);
    }
    public Dispatcher(String filename) {
        r = new Reader(filename);
    }
    //  File handling
    private Reader r;
}