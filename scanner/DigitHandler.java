package scanner;
import compiler.Token;
public class DigitHandler {
    private static final String digit = "(0|1|2|3|4|5|6|7|8|9)";
    /**
     * Gets the numeric token
     * @param c The first character of the token
     * @return The numeric token
     */
    public Token getToken(char c) {
        String str = "" + c;
        //  Find all consecutive numbers
        while (true) {
            c = scanner.peekChar();
            if (("" + c).matches(digit)) {
                scanner.nextChar();
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
            scanner.nextChar();
            //  track to make sure we find digits to force the correct format
            boolean accept = false;
            //  Then look for more numbers
            while (true) {
                c = scanner.peekChar();
                if (("" + c).matches(digit)) {
                    accept = true;
                    scanner.nextChar();
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
                System.out.println("Syntax Error at line " + scanner.linenumber() + " col " + scanner.col() + ": " +
                        "Number format error " + str + " missing value after decimal.");
                return null;
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
            scanner.nextChar();
            c = scanner.nextChar();
            if (c == '+' || c == '-') {
                str += c;
                boolean accept = false;
                //  Then look for more numbers
                while (true) {
                    c = scanner.peekChar();
                    if (("" + c).matches(digit)) {
                        accept = true;
                        scanner.nextChar();
                        str += c;
                    }
                    else {
                        break;
                    }
                }
                if (accept) {
                    return new Token(str, Token.ID.FLOAT_LIT);
                }
                //  Exponent but no power
                else {
                    System.out.println("Syntax Error at line " + scanner.linenumber() + " col " + scanner.col() + ": " +
                            "Number format error " + str + " missing exponent.");
                    return null;
                }
            }
            //  Exponent but no +/-
            else {
                System.out.println("Syntax Error at line " + scanner.linenumber() + " col " + scanner.col() + ": " +
                        "Number format error " + str + " needs +/- in exponent.");
                return null;
            }
    }
    private final Scanner scanner;
    public DigitHandler(Scanner dispatcher) {
        this.scanner = dispatcher;
    }
}