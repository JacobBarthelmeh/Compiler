package scanner;
import compiler.Token;
public class Scanner {
    /**
     * Gets the next token from the file. Be sure to check for Error tokens.
     * @return The next token in the file
     */
    public Token nextToken() {
        return d.nextToken();
    }
    private Dispatcher d;
    /**
     * Constructs a dispatcher to read from a file
     * @param filename The name of the file to search for
     */
    public Scanner(String filename) {
        d = new Dispatcher(filename);
    }
    
    
    /**
     * Close file scanning from
     */
    public void close(){
        d.close();
    }
}