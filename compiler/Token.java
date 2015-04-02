package compiler;
import util.Terminal;
public class Token {
    /**
     * Constructs a token
     * @param contents The semantic contents of the token
     * @param terminal The classification of the token
     * @param line The line number of the token occurrence
     * @param col The column number of the token's first character
     */
    public Token(String contents, Terminal terminal, int line, int col) {
        this.contents = contents;
        this.terminal = terminal;
        this.line = line;
        this.col = col;
    }
    //  contents - helpful for semantics
    private final String contents;
    /**
     * Gets the semantic content of the token
     * @return The content
     */
    public String getContents() {
        return contents;
    }
    //  ID - helpful for grammar
    private final Terminal terminal;
    /**
     * Gets the classification for the token
     * @return The classification
     */
    public Terminal getTerminal() {
        return terminal;
    }
    private final int line, col;
    public int getLine() {
        return line;
    }
    public int getCol() {
        return col;
    }
    @Override
    public String toString() {
        return terminal + " " + contents + " at line " + line + " col " + col;
    }
}