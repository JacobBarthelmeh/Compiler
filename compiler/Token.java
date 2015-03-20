package compiler;
public class Token {
    /**
     * Constructs a token
     * @param contents The semantic contents of the token
     * @param id The classification of the token
     */
    public Token(String contents, ID id, int line, int col) {
        this.contents = contents;
        this.id = id;
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
    private final ID id;
    /**
     * Gets the classification for the token
     * @return The classification
     */
    public ID getID() {
        return id;
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
        return id + " " + contents + " at line " + line + " col " + col;
    }
    //  Enumeration of all possible types
    public enum ID {
        AND,
        BEGIN,
        BOOLEAN,
        DIV,
        DO,
        DOWNTO,
        ELSE,
        END,
        FALSE,
        FIXED,
        FLOAT,
        FOR,
        FUNCTION,
        IF,
        INTEGER,
        MOD,
        NOT,
        OR,
        PROCEDURE,
        PROGRAM,
        READ,
        REPEAT,
        STRING,
        THEN,
        TO,
        TRUE,
        UNTIL,
        VAR,
        WHILE,
        WRITE,
        WRITELN,
        IDENTIFIER,
        INTEGER_LIT,
        FLOAT_LIT,
        STRING_LIT,
        PERIOD,
        COMMA,
        SCOLON,
        COLON,
        LPAREN,
        RPAREN,
        EQUAL,
        GTHAN,
        LTHAN,
        LEQUAL,
        GEQUAL,
        NEQUAL,
        ASSIGN,
        PLUS,
        MINUS,
        TIMES,
        FLOAT_DIVIDE,
        EOF
    }
}