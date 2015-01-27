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
            //  track to make sure we find digits to force the correct format
            boolean accept = false;
            //  Then look for more numbers
            while (true) {
                c = dispatcher.peekChar();
                if (("" + c).matches("(0|1|2|3|4|5|6|7|8|9)")) {
                    accept = true;
                    dispatcher.nextChar();
                    str += c;
                }
                else {
                    break;
                }
            }
            //  Test for float
            if (c == 'e' || c == 'E') {
                return checkFloat(str, c);
            }
            //  It may be decimal point
            else if (accept) {
                return new Token(str, Token.ID.FIXED_LIT);
            }
            //  Decimal found but no digits after
            else {
                return new Token("{ Number Format error " + str + " on line "
                        + dispatcher.linenumber() + "}", Token.ID.ERROR);
            }
        }
        //  Test for float again (the decimal was optional)
        if (c == 'e' || c == 'E') {
            return checkFloat(str, c);
        }
        return new Token(str, Token.ID.INTEGER);
    }
    private Token checkFloat(String str, char c) {
            //  Accept the exponent
            str += c;
            dispatcher.nextChar();
            c = dispatcher.nextChar();
            if (c == '+' || c == '-') {
                str += c;
                boolean accept = false;
                //  Then look for more numbers
                while (true) {
                    c = dispatcher.peekChar();
                    if (("" + c).matches("(0|1|2|3|4|5|6|7|8|9)")) {
                        accept = true;
                        dispatcher.nextChar();
                        str += c;
                    }
                    else {
                        break;
                    }
                }
                return new Token(str, Token.ID.FLOAT_LIT);
            }
            //  Exponent but no power
            else {
                return new Token("{ Number Format error " + str + " on line "
                        + dispatcher.linenumber() + "}", Token.ID.ERROR);
            }
    }
    private final Scanner dispatcher;
    public DigitHandler(Scanner dispatcher) {
        this.dispatcher = dispatcher;
    }
}