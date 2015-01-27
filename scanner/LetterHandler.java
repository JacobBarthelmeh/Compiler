package scanner;


import compiler.Token;

public class LetterHandler {
    public Token getToken(String c) {
        //  Debug return
        String str = c;
        while (true) {
            c = dispatcher.nextChar();
            if (c.matches("\\w")) {
                str += c;
            }
            else {
                break;
            }
        }
        if ()
        return new Token(str, Token.ID.IDENTIFIER);
    }
    private Dispatcher dispatcher;
    public LetterHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}