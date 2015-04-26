package scanner;
import util.Reader;
import compiler.Token;
import util.Terminal;
public class Dispatcher {    
    //  File handling
    private final Reader reader;
    
    public Token nextToken() {
        //  Precondition: c is the first character in the file
        //      OR the first character that hasn't been returned in 
        //      a previous token.
        char c = reader.peekChar();
        int row = reader.linenumber, col = reader.col;
        //  Note- scanner handles new lines
        switch (c) {
            //  Excess newlines have been causing lissues (temp fix)
            case '\r': case '\n':
            //  Dispatcher ignores whitespace
            case '\t': case ' ':
                reader.nextChar();
                return nextToken();
            //  Dispatcher extracts comments
            case '{':
                reader.nextChar();   //  Accept the {
                while (c != '}' && c != '\u001a') {
                    c = reader.nextChar();   //  Accept next character always
                }
                if (c == '\u001a') {
                    return new Token("Comment not closed", Terminal.EOF, row, col);
                }
                reader.nextChar();
                return nextToken();
            //  Dispatcher identifiers unclosed comments
            case '}':
                reader.nextChar();
                return new Token("Comment not closed at " +
                        reader.linenumber + " col " + reader.col, Terminal.EOF, row, col);
            //  Begin FSAs!
            //  Ugly, yes... 1 bytecode for all cases, also yes!
            case '\'': return FSA.TEST_STRING_LIT(reader, row, col);
            case ':': return FSA.TEST_COLON(reader, row, col);
            case '>': return FSA.TEST_GTHAN(reader, row, col);
            case '<': return FSA.TEST_LTHAN(reader, row, col);
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
                return FSA.TEST_DIGIT(reader, row, col);
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
                return FSA.TEST_LETTER(reader, row, col);
                //  Technically these are one-state FSA's.
                //  This should make the code a little less ugly
            case ',': 
                reader.nextChar();  //  I am not sure why this is needed.
                return new Token(",", Terminal.COMMA, row, col);
            case '=': 
                reader.nextChar(); //   because these all seem redundant
                return new Token("=", Terminal.EQUAL, row, col);
            case '/':
                reader.nextChar(); //   but you'd imagine since I wrote the code
                return new Token("/", Terminal.FLOAT_DIVIDE, row, col);
            case '(': 
                reader.nextChar(); //   that I would have that answer.
                return new Token("(", Terminal.LPAREN, row, col);
            case ')':
                reader.nextChar(); 
                return new Token(")", Terminal.RPAREN, row, col);
            case '-': 
                reader.nextChar(); 
                return new Token("-", Terminal.MINUS, row, col);
            case '.':
                reader.nextChar(); 
                return new Token(".", Terminal.PERIOD, row, col);
            case '+':
                reader.nextChar(); 
                return new Token("+", Terminal.PLUS, row, col);
            case ';':
                reader.nextChar(); 
                return new Token(";", Terminal.SCOLON, row, col);
            case '*':
                reader.nextChar(); 
                return new Token("*", Terminal.TIMES, row, col);
            case '\u001a':
                return new Token("\u001a", Terminal.EOF, row, col);
        }
        reader.nextChar();
        return new Token("" + c, Terminal.IDENTIFIER, row, col);
    }
    /**
     * close file
     */
    public void close(){
        reader.close();
    }
    public Dispatcher(String filename) {
        reader = new Reader(filename);
    }
}