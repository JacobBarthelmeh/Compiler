package scanner;
import compiler.Token;
public class LetterHandler {
    /**
     * Gets a word token
     * @param c The first character in the token
     * @return The word token
     */
    public Token getToken(char c) {
        String str = "" + c;
        //  Accept all consecutive alphanumeric characters (with _ included)
        while (true) {
            c = dispatcher.peekChar();
            //  Note- numbers are permitted after the first letter is read
            if (("" + c).matches("(_|0|1|2|3|4|5|6|7|8|9|a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z)")) {
            str += c;
                dispatcher.nextChar();
            }
            else {
                break;
            }
        }
        //  Enforce string rules (no double _ and no lonely _)
        if (str.contains("__") || str.equals("_")) {
            return new Token("Error " + str, Token.ID.ERROR);
        }
        //  Handle all reserved words
        if (str.equals("and")) {
            return new Token(str, Token.ID.AND);
        }
        if (str.equals("begin")) {
            return new Token(str, Token.ID.BEGIN);
        }
        if (str.equals("Boolean")) {
            return new Token(str, Token.ID.BOOLEAN);
        }
        if (str.equals("div")) {
            return new Token(str, Token.ID.DIV);
        }
        if (str.equals("do")) {
            return new Token(str, Token.ID.DO);
        }
        if (str.equals("downto")) {
            return new Token(str, Token.ID.DOWNTO);
        }
        if (str.equals("else")) {
            return new Token(str, Token.ID.ELSE);
        }
        if (str.equals("end")) {
            return new Token(str, Token.ID.END);
        }
        if (str.equals("false")) {
            return new Token(str, Token.ID.FALSE);
        }
        if (str.equals("fixed")) {
            return new Token(str, Token.ID.FIXED);
        }
        if (str.equals("float")) {
            return new Token(str, Token.ID.FLOAT);
        }
        if (str.equals("for")) {
            return new Token(str, Token.ID.FOR);
        }
        if (str.equals("function")) {
            return new Token(str, Token.ID.FUNCTION);
        }
        if (str.equals("if")) {
            return new Token(str, Token.ID.IF);
        }
        if (str.equals("integer")) {
            return new Token(str, Token.ID.INTEGER);
        }
        if (str.equals("mod")) {
            return new Token(str, Token.ID.MOD);
        }
        if (str.equals("not")) {
            return new Token(str, Token.ID.NOT);
        }
        if (str.equals("or")) {
            return new Token(str, Token.ID.OR);
        }
        if (str.equals("procedure")) {
            return new Token(str, Token.ID.PROCEDURE);
        }
        if (str.equals("program")) {
            return new Token(str, Token.ID.PROGRAM);
        }
        if (str.equals("read")) {
            return new Token(str, Token.ID.READ);
        }
        if (str.equals("repeat")) {
            return new Token(str, Token.ID.REPEAT);
        }
        if (str.equals("string")) {
            return new Token(str, Token.ID.STRING);
        }
        if (str.equals("then")) {
            return new Token(str, Token.ID.THEN);
        }
        if (str.equals("true")) {
            return new Token(str, Token.ID.TRUE);
        }
        if (str.equals("to")) {
            return new Token(str, Token.ID.TO);
        }
        if (str.equals("type")) {
            return new Token(str, Token.ID.TYPE);
        }
        if (str.equals("until")) {
            return new Token(str, Token.ID.UNTIL);
        }
        if (str.equals("var")) {
            return new Token(str, Token.ID.VAR);
        }
        if (str.equals("while")) {
            return new Token(str, Token.ID.WHILE);
        }
        if (str.equals("write")) {
            return new Token(str, Token.ID.WRITE);
        }
        if (str.equals("writeln")) {
            return new Token(str, Token.ID.WRITELN);
        }
        //  It's not a reserved word so it's an identifier
        return new Token(str, Token.ID.IDENTIFIER);
    }
    private final Scanner dispatcher;
    public LetterHandler(Scanner dispatcher) {
        this.dispatcher = dispatcher;
    }
}