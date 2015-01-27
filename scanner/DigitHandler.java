package scanner;


import compiler.Token;

public class DigitHandler {
    public Token getToken(String c) {
        /*
            Return a token:
            
            return new Token("contents", Token.ID.SOMETHING);
            where something is probably:
                INTEGER_LIT, FIXED_LIT, FLOAT_LIT, ERROR
            
            or return new Token("Error message", Token.ID.ERROR);

            Do this by using dispatcher.peekChar() to peek
            at a character without moving the file pointer
            or dispatcher.nextChar() to advance the file
            pointer.
        */
        //  Debug return
        return new Token("x", Token.ID.INTEGER);
    }
    private Dispatcher dispatcher;
    public DigitHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}