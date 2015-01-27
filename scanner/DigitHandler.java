package scanner;
import compiler.Token;
public class DigitHandler {
    /**
     * Gets the numeric token
     * @param c The first character of the token
     * @return The numeric token
     */
    public Token getToken(char c) {
        String str = "" + c;
        //  Find all consecutive numbers
        while (true) {
            c = dispatcher.peekChar();
            if (("" + c).matches("(0|1|2|3|4|5|6|7|8|9)")) {
                dispatcher.nextChar();
                str += c;
            }
            else {
                break;
            }
        }
        //  Test for float and fixed
        if (c == '.') {
            //  Accept the period
            str += c;
            dispatcher.nextChar();
            //  Then look for more numbers
            while (true) {
                c = dispatcher.peekChar();
                if (("" + c).matches("(0|1|2|3|4|5|6|7|8|9)")) {
                    dispatcher.nextChar();
                    str += c;
                }
                else {
                    break;
                }
            }
            //  Test for float
            if (c == 'e' || c == 'E') {
                //  Accept the exponent
                str += c;
                dispatcher.nextChar();
                //  Then look for more numbers
                while (true) {
                    c = dispatcher.peekChar();
                    if (("" + c).matches("(0|1|2|3|4|5|6|7|8|9)")) {
                        dispatcher.nextChar();
                        str += c;
                    }
                    else {
                        break;
                    }
                }
                return new Token(str, Token.ID.FLOAT_LIT);
            }
            return new Token(str, Token.ID.FIXED_LIT);
        }
        return new Token(str, Token.ID.INTEGER);
    }
    private final Scanner dispatcher;
    public DigitHandler(Scanner dispatcher) {
        this.dispatcher = dispatcher;
    }
}