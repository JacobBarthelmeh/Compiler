package scanner;
import compiler.Token;
public class LetterHandler {
    private static final Token.ID[] LETTER_TOKENS = {
        Token.ID.AND,
        Token.ID.BEGIN,
        Token.ID.BOOLEAN,
        Token.ID.DIV,
        Token.ID.DO,
        Token.ID.DOWNTO,
        Token.ID.ELSE,
        Token.ID.END,
        Token.ID.FALSE,
        Token.ID.FIXED,
        Token.ID.FLOAT,
        Token.ID.FOR,
        Token.ID.FUNCTION,
        Token.ID.IF,
        Token.ID.INTEGER,
        Token.ID.MOD,
        Token.ID.NOT,
        Token.ID.OR,
        Token.ID.PROCEDURE,
        Token.ID.PROGRAM,
        Token.ID.READ,
        Token.ID.REPEAT,
        Token.ID.STRING,
        Token.ID.THEN,
        Token.ID.TRUE,
        Token.ID.TO,
        Token.ID.TYPE,
        Token.ID.UNTIL,
        Token.ID.VAR,
        Token.ID.WHILE,
        Token.ID.WRITE,
        Token.ID.WRITELN,
        Token.ID.IDENTIFIER
    };
    private static final String
            allowed = "(_|a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z|0|1|2|3|4|5|6|7|8|9)",
            regex = "(\\w|_\\w)(\\w|\\d|_\\w|_\\d)*";
    /**
     * Gets a word token
     * @param c The first character in the token
     * @return The word token
     */
    public Token getToken(char c) {
        String str = "" + c;
        //  Accept all consecutive alphanumeric characters (with _ included)
        while (true) {
            c = scanner.peekChar();
            //  Note- numbers are permitted after the first letter is read
            if (("" + c).matches(allowed)) {
                str += c;
                scanner.nextChar();
            }
            else {
                break;
            }
        }
        for (Token.ID i : LETTER_TOKENS) {
            if (str.matches(i.regex())) {
                return new Token(str, i);
            }
        }
        System.out.println(str);
        System.exit(0);
        return new Token("{ Unidentified token " + str + " }", Token.ID.ERROR);
    }
    private final Scanner scanner;
    public LetterHandler(Scanner dispatcher) {
        this.scanner = dispatcher;
    }
}