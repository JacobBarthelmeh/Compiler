package scanner;
import compiler.Token;
public class Dispatcher {
    public Token getNextToken() {
        //  Precondition: c is the first character in the file
        //      OR the first character that hasn't been returned in 
        //      a previous token.
        char c = s.peekChar();
        while (c != '\u001a') {
            switch (c) {
                //  Dispatcher handles new lines
                case '\r': case '\n':
                    s.nextChar();
                    //  Problem arises for the \r\n and \n\r cases
                    c = s.peekChar();
                    //  Free to increment another symbol before incrementing
                    if (c == '\r' || c == '\n') {
                        s.nextChar();
                    }
                    s.linenumber++;
                    s.col = 1;
                    break;
                //  Dispatcher ignores whitespace
                case '\t': case ' ':
                    s.nextChar();
                    break;
                //  Dispatcher extracts comments
                case '{':
                    s.nextChar();
                    while (c != '}' && c != '\u001a') {
                        c = s.nextChar();
                        if (c == '\r' || c == '\n') {
                            c = s.peekChar();
                            //  Free to increment another symbol before incrementing
                            if (c == '\r' || c == '\n') {
                                s.nextChar();
                            }
                            s.linenumber++;
                            s.col = 0;
                        }
                    }
                    if (c == '\u001a') {
                        System.out.println("Syntax Error: " +
                                "Comment opened but not closed.");
                        return null;
                    }
                    break;
                //  Dispatcher identifiers unclosed comments
                case '}':
                    System.out.println("Syntax Error at line " + s.linenumber + " col " + s.col + ": " +
                            "Comment closed but not opened.");
                    s.nextChar();
                    return null;
                //  Begin FSAs!
                case '\'': return FSA.TEST_STRING_LIT(s);
                case ':': return FSA.TEST_COLON(s);
                case '>': 
                    c = s.peekChar();
                    if (c == '=') {
                        s.nextChar();
                        return new Token(">=", Token.ID.GEQUAL);
                    }
                    return new Token(">", Token.ID.GTHAN);
                case '<':
                    c = s.peekChar();
                    switch (c){
                        case '=':
                            s.nextChar();
                            return new Token("<=", Token.ID.LEQUAL);
                        case '>':
                            s.nextChar();
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
                    System.out.println("Syntax Error at line " + s.linenumber + " col " + s.col + ": " +
                            "Unrecognized symbol " + c);
                    return null;
            }
            //  Whitespace/newline/comment code has us proceed to the next symbol
            c = s.nextChar();
        }
    }
    //  Handlers for when characters are found
    private final LetterHandler l_handler;
    private final DigitHandler d_handler;
    private Scanner s;
    public Dispatcher(Scanner scanner) {
        this.s = scanner;
        //  prepare the handlers
        l_handler = new LetterHandler(scanner);
        d_handler = new DigitHandler(scanner);
    }
}
