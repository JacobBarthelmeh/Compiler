package scanner;
import compiler.Token;
public class DigitHandler {
    /**
     * Gets the numeric token
     * @param c The first character of the token
     * @return The numeric token
     */
    public Token getToken(char c) {
        String str = "" + c + collectDigits();
        c = scanner.peekChar();
        if (c == '.') {
            scanner.nextChar();
            String decimal = collectDigits();
            if (decimal.length() == 0) {
                System.out.println("Syntax Error at line " + scanner.linenumber() + " col " + scanner.col() + ": " +
                        "Number format error " + str + " missing value after decimal.");
                return null;
            }
            else {
                str += c + decimal;
            }
            c = scanner.peekChar();
        }
        if (c == 'e' || c == 'E') {
            str += c;
            scanner.nextChar();
            c = scanner.peekChar();
            if (c == '+' || c == '-') {
                scanner.nextChar();
                str += c + collectDigits();
                return new Token(str, Token.ID.FLOAT_LIT);
            }
            else {
                System.out.println("Syntax Error at line " + scanner.linenumber() + " col " + scanner.col() + ": " +
                        "Number format error " + str + " needs +/- in exponent.");
                return null;
            }
        }
        else {
            return new Token(str, Token.ID.FIXED_LIT);
        }
    }
    private String collectDigits() {
        String str = "";
        while (true) {
            char c = scanner.peekChar();
            if (("" + c).matches("\\d")) {
                scanner.nextChar();
                str += c;
            }
            else {
                break;
            }
        }
        return str;
    }
    private final Scanner scanner;
    public DigitHandler(Scanner dispatcher) {
        this.scanner = dispatcher;
    }
}