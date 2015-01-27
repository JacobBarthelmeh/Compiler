package scanner;


import compiler.Token;

public class LetterHandler {
    public Token getToken(String c) {
        /*
            Return a token:
            
            return new Token("contents", Token.ID.SOMETHING);
                (where something is IDENTIFIER, STRING_LIT, etc)
            
            or return new Token("Error message", Token.ID.ERROR);
            
            Do this by using dispatcher.peekChar() to peek
            at a character without moving the file pointer
            or dispatcher.nextChar() to advance the file
            pointer.
        */
        //  Debug return
        return new Token("variable", Token.ID.IDENTIFIER);
    }
    private Dispatcher dispatcher;
    public LetterHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}