package scanner;


import compiler.Token;

public class DigitHandler {
    public Token getToken(char c) {
        //  UNFINISHED
        String str = "" + c;
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
    private final Dispatcher dispatcher;
    public DigitHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}