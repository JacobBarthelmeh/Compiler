public class Token {
    //  Straightforward constructor
    public Token(String contents, ID id) {
        this.contents = contents;
        this.id = id;
    }

    public static final Token
        MP_AND = new Token("and", ID.AND),
        MP_BEGIN = new Token("begin", ID.BEGIN),
        MP_BOOLEAN = new Token("Boolean", ID.BOOLEAN),
        MP_DIV = new Token("div", ID.DIV),
        MP_DO = new Token("do", ID.DO),
        MP_DOWNTO = new Token("downto", ID.DOWNTO),
        MP_ELSE = new Token("else", ID.ELSE),
        MP_END = new Token("end", ID.END),
        MP_FALSE = new Token("false", ID.FALSE),
        MP_FIXED = new Token("fixed", ID.FIXED),
        MP_FLOAT = new Token("float", ID.FLOAT),
        MP_FOR = new Token("for", ID.FOR),
        MP_FUNCTION = new Token("function", ID.FUNCTION),
        MP_IF = new Token("if", ID.IF),
        MP_INTEGER = new Token("integer", ID.INTEGER),
        MP_MOD = new Token("mod", ID.MOD),
        MP_NOT = new Token("not", ID.NOT),
        MP_OR = new Token("or", ID.OR),
        MP_PROCEDURE = new Token("procedure", ID.PROCEDURE),
        MP_PROGRAM = new Token("program", ID.PROGRAM),
        MP_READ = new Token("read", ID.READ),
        MP_REPEAT = new Token("repeat", ID.REPEAT),
        MP_STRING = new Token("string", ID.STRING),
        MP_THEN = new Token("then", ID.THEN),
        MP_TRUE = new Token("true", ID.TRUE),
        MP_TO = new Token("to", ID.TO),
        MP_TYPE = new Token("type", ID.TYPE),
        MP_UNTIL = new Token("until", ID.UNTIL),
        MP_VAR = new Token("var", ID.VAR),
        MP_WHILE = new Token("while", ID.WHILE),
        MP_WRITE = new Token("write", ID.WRITE),
        MP_WRITELN = new Token("writeln", ID.WRITELN),
        MP_ASSIGN = new Token(":=", ID.ASSIGN),
        MP_COLON = new Token(":", ID.COLON),
        MP_COMMA = new Token(",", ID.COMMA),
        MP_EQUAL = new Token("=", ID.EQUAL),
        MP_FLOAT_DIVIDE = new Token("/", ID.FLOAT_DIVIDE),
        MP_GEQUAL = new Token(">=", ID.GEQUAL),
        MP_GTHAN = new Token(">", ID.GTHAN),
        MP_LEQUAL = new Token("<=", ID.LEQUAL),
        MP_LTHAN = new Token("<", ID.LTHAN),
        MP_LPAREN = new Token("(", ID.LPAREN),
        MP_RPAREN = new Token(")", ID.RPAREN),
        MP_MINUS = new Token("-", ID.MINUS),
        MP_NEQUAL = new Token("<>", ID.NEQUAL),
        MP_PERIOD = new Token(".", ID.PERIOD),
        MP_PLUS = new Token("+", ID.PLUS),
        MP_SCOLON = new Token(";", ID.SCOLON),
        MP_TIMES = new Token("*", ID.TIMES),
        MP_EOF = new Token("\u001a", ID.EOF);

    //  contents - helpful for semantics
    private String contents;
    public String getContents() {
        return contents;
    }
    //  ID - helpful for grammar
    private ID id;
    public ID getID() {
        return id;
    }
    //  Enumeration of all possible types
    //  TODO: Make static tokens for all of the static IDs
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
        RUN_ON_COMMENT,
        RUN_STRING,
        ERROR
    }
}