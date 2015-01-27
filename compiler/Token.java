package compiler;
public class Token {
    /**
     * Constructs a token
     * @param contents The semantic contents of the token
     * @param id The classification of the token
     */
    public Token(String contents, ID id) {
        this.contents = contents;
        this.id = id;
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
        TRUE,
        TO,
        TYPE,
        UNTIL,
        VAR,
        WHILE,
        WRITE,
        WRITELN,
        IDENTIFIER,
        INTEGER_LIT,
        FIXED_LIT,
        FLOAT_LIT,
        STRING_LIT,
        ASSIGN,
        COLON,
        COMMA,
        EQUAL,
        FLOAT_DIVIDE,
        GEQUAL,
        GTHAN,
        LEQUAL,
        LTHAN,
        LPAREN,
        RPAREN,
        MINUS,
        NEQUAL,
        PERIOD,
        PLUS,
        SCOLON,
        TIMES,
        EOF,
        COMMENT,
        RUN_ON_COMMENT,
        RUN_STRING,
        ERROR
    }
}