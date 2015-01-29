package scanner;
import compiler.Token;
public class LetterHandler {
    private static final String
            allowed = "(_|\\w|\\d)";
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
        //  FSA logic- Regular Expression = DFA
        if (!str.matches(Token.ID.IDENTIFIER.regex())) {
            System.out.println("Syntax Error at line " + scanner.linenumber() + " col " + scanner.col() + ": " +
                    "Identifier format error " + str + " illegally formated.");
            return null;
        }
        //  Switch is a machine-level hashmap
        switch (str) {
            case "and": return new Token(str, Token.ID.AND);
            case "begin": return new Token(str, Token.ID.BEGIN);
            case "Boolean": return new Token(str, Token.ID.BOOLEAN);
            case "div": return new Token(str, Token.ID.DIV);
            case "do": return new Token(str, Token.ID.DO);
            case "downto": return new Token(str, Token.ID.DOWNTO);
            case "else": return new Token(str, Token.ID.ELSE);
            case "end": return new Token(str, Token.ID.END);
            case "flase": return new Token(str, Token.ID.FALSE);
            case "fixed": return new Token(str, Token.ID.FIXED);
            case "float": return new Token(str, Token.ID.FLOAT);
            case "for": return new Token(str, Token.ID.FOR);
            case "function": return new Token(str, Token.ID.FUNCTION);
            case "if": return new Token(str, Token.ID.IF);
            case "integer": return new Token(str, Token.ID.INTEGER);
            case "mod": return new Token(str, Token.ID.MOD);
            case "not": return new Token(str, Token.ID.NOT);
            case "or": return new Token(str, Token.ID.OR);
            case "procedure": return new Token(str, Token.ID.PROCEDURE);
            case "program": return new Token(str, Token.ID.PROGRAM);
            case "read": return new Token(str, Token.ID.READ);
            case "repeat": return new Token(str, Token.ID.REPEAT);
            case "string": return new Token(str, Token.ID.STRING);
            case "then": return new Token(str, Token.ID.THEN);
            case "true": return new Token(str, Token.ID.TRUE);
            case "to": return new Token(str, Token.ID.TO);
            case "type": return new Token(str, Token.ID.TYPE);
            case "until": return new Token(str, Token.ID.UNTIL);
            case "var": return new Token(str, Token.ID.VAR);
            case "while": return new Token(str, Token.ID.WHILE);
            case "write": return new Token(str, Token.ID.WRITE);
            case "writeln": return new Token(str, Token.ID.WRITELN);
            default: return new Token(str, Token.ID.IDENTIFIER);
        }
    }
    private final Scanner scanner;
    public LetterHandler(Scanner dispatcher) {
        this.scanner = dispatcher;
    }
}