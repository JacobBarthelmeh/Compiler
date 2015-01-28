package scanner;
import compiler.Token;
public class SymbolHandler {
    /**
     * Gets the symbol token
     * @param c The first character in the token
     * @return The token found
     */
    public Token getToken(char c) {
        //  Prepare these- they're used later
        String str;
        char next;
        //  Check the character possibilities
        switch (c) {
            //  Handle comments
            case '{':
                str = "" + c;
                do {
                    next = dispatcher.nextChar();
                    str += next;
                    if (next == '}') {
                        return new Token(str, Token.ID.COMMENT);
                    }
                }
                while (next != '\u001a');
                return new Token("Run-on comment on line "
                        + dispatcher.linenumber() + ".",
                        Token.ID.RUN_ON_COMMENT);
            //  Handle quotes
            case '\'':
                str = "" + c;
                do {
                    next = dispatcher.nextChar();
                    str += next;
                    if (next == '\'') {
                        return new Token(str, Token.ID.STRING_LIT);
                    }
                }
                while (next != '\u001a');
                return new Token("Run-on string on line "
                        + dispatcher.linenumber() + ".",
                        Token.ID.RUN_STRING);
            case ':':
                next = dispatcher.peekChar();
                //  Symbol := is special
                if (next == '=') {
                    dispatcher.nextChar();
                    return new Token(":=", Token.ID.ASSIGN);
                }
                //  Otherwise accept :
                return new Token(":", Token.ID.COLON);
            case '>':
                next = dispatcher.peekChar();
                //  Symbol >= is special
                if (next == '=') {
                    dispatcher.nextChar();
                    return new Token(">=", Token.ID.GTHAN);
                }
                //  Otherwise accept >
                return new Token(">", Token.ID.GTHAN);
            case '<':
                next = dispatcher.peekChar();
                //  Symbol <= is special
                if (next == '=') {
                    dispatcher.nextChar();
                    return new Token("<=", Token.ID.LTHAN);
                }
                //  Symbol <> is also special
                else if (next == '>') {
                    dispatcher.nextChar();
                    return new Token("<>", Token.ID.NEQUAL);
                }
                //  Otherwise accept <
                return new Token("<=", Token.ID.LTHAN);
            case ',':
                return new Token(",", Token.ID.COMMA);
            case '=':
                return new Token("=", Token.ID.EQUAL);
            case '-':
                return new Token("-", Token.ID.MINUS);
            case '(':
                return new Token("(", Token.ID.LPAREN);
            case '.':
                return new Token(".", Token.ID.PERIOD);
            case '+':
                return new Token("+", Token.ID.PLUS);
            case ')':
                return new Token(")", Token.ID.RPAREN);
            case ';':
                return new Token(";", Token.ID.SCOLON);
            case '*':
                return new Token("*", Token.ID.TIMES);
            default:
                //  No other symbols can be recognized. last resort.
                return new Token("{ Unknown symbol " + c + " at line "
                        + dispatcher.linenumber() + " }", Token.ID.ERROR);
        }
    }
    private final Scanner dispatcher;
    public SymbolHandler(Scanner dispatcher) {
        this.dispatcher = dispatcher;
    }
}