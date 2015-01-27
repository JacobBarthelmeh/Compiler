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
        //  TODO: Fixed and Float
        return new Token(str, Token.ID.INTEGER);
    }
    private Dispatcher dispatcher;
    public DigitHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}