package scanner;


import compiler.Token;

public class DigitHandler {
    public Token getToken(char c) {
        return new Token("" + c, Token.ID.INTEGER);
    }
    private Dispatcher dispatcher;
    public DigitHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}