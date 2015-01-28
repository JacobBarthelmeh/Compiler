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
        AND         ("and"),
        BEGIN       ("begin"),
        BOOLEAN     ("Boolean"),
        DIV         ("div"),
        DO          ("do"),
        DOWNTO      ("downto"),
        ELSE        ("else"),
        END         ("end"),
        FALSE       ("false"),
        FIXED       ("fixed"),
        FLOAT       ("float"),
        FOR         ("for"),
        FUNCTION    ("function"),
        IF          ("if"),
        INTEGER     ("integer"),
        MOD         ("mod"),
        NOT         ("not"),
        OR          ("or"),
        PROCEDURE   ("procedure"),
        PROGRAM     ("program"),
        READ        ("read"),
        REPEAT      ("repeat"),
        STRING      ("string"),
        THEN        ("then"),
        TRUE        ("true"),
        TO          ("to"),
        TYPE        ("type"),
        UNTIL       ("until"),
        VAR         ("var"),
        WHILE       ("while"),
        WRITE       ("write"),
        WRITELN     ("writeln"),
        IDENTIFIER  ("(\\w|_\\w)(\\w|\\d|_\\w|_\\d)*"),
        INTEGER_LIT ("\\d+"),
        FIXED_LIT   ("\\d+\\.\\d+"),
        FLOAT_LIT   ("(\\d+\\.\\d+|\\d+)(e|E)(+|-)\\d+"),
        STRING_LIT  ("\\'.*\\'"),
        ASSIGN      (":="),
        COLON       (":"),
        COMMA       (","),
        EQUAL       ("="),
        FLOAT_DIVIDE("/"),
        GEQUAL      (">="),
        GTHAN       (">"),
        LEQUAL      ("<="),
        LTHAN       ("<"),
        LPAREN      ("\\("),
        RPAREN      ("\\)"),
        MINUS       ("-"),
        NEQUAL      ("<>"),
        PERIOD      ("\\."),
        PLUS        ("\\+"),
        SCOLON      (";"),
        TIMES       ("\\*"),
        EOF         ("\u001a"),
        COMMENT     ("\\{.*\\}"),
        RUN_COMMENT ("\\{.*"),
        RUN_STRING  ("\\'.*"),
        ERROR       ("");
        
        private String regex;
        public String regex() {
            return regex;
        }
        ID(String regex) {
            this.regex = regex;
        }
    }
}