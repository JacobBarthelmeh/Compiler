package scanner;


import compiler.Token;

public class LetterHandler {
    public Token getToken(char c) {
        return new Token("" + c, Token.ID.IDENTIFIER);
    }
    private Dispatcher dispatcher;
    public LetterHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}