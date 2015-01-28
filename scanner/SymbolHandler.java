package scanner;
import compiler.Token;
public class SymbolHandler {
    private static final Token.ID[] SYMBOL_TOKENS = {
        Token.ID.ASSIGN,
        Token.ID.COLON,
        Token.ID.COMMA,
        Token.ID.EQUAL,
        Token.ID.FLOAT_DIVIDE,
        Token.ID.GEQUAL,
        Token.ID.GTHAN,
        Token.ID.LEQUAL,
        Token.ID.LTHAN,
        Token.ID.LPAREN,
        Token.ID.RPAREN,
        Token.ID.MINUS,
        Token.ID.NEQUAL,
        Token.ID.PERIOD,
        Token.ID.PLUS,
        Token.ID.SCOLON,
        Token.ID.TIMES,
        Token.ID.EOF
    };
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
        if (c == '{') {
            str = "" + c;
            do {
                next = scanner.nextChar();
                str += next;
                if (next == '}') {
                    return new Token(str, Token.ID.COMMENT);
                }
            }
            while (next != '\u001a');
            return new Token("Run-on comment on line "
                    + scanner.linenumber() + ".",
                    Token.ID.RUN_COMMENT);
        }
        else if (c ==  '\'') {
            str = "" + c;
            do {
                next = scanner.nextChar();
                str += next;
                if (next == '\'') {
                    return new Token(str, Token.ID.STRING_LIT);
                }
            }
            while (next != '\u001a');
            return new Token("Run-on string on line "
                    + scanner.linenumber() + ".",
                    Token.ID.RUN_STRING);
        }
        for (Token.ID i : SYMBOL_TOKENS){
            //  Match 
            //  The "" is somehow needed by NetBeans
            String news = "" +  c + scanner.peekChar();
            if (news.matches(i.regex())) {
                scanner.nextChar();
                return new Token(news, i);
            }
            if (("" + c).matches(i.regex())) {
                return new Token("" + c, i);
            }
        }
        return new Token("{ Unidentified token " + c + " }", Token.ID.ERROR);
    }
    private final Scanner scanner;
    public SymbolHandler(Scanner dispatcher) {
        this.scanner = dispatcher;
    }
}