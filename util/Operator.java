package util;
public enum Operator {
    //  ADDITION LEVEL OPERATORS
    ADDITION(0, "ADDS"),
    SUBTRACTION(0, "SUBS"),
    OR(0, "ORS"),
    
    //  MULTIPLICATION LEVEL OPERATORS
    MULTIPLICATION(1, "MULS"),
    DIVISION(1, "DIVS"),
    FLOAT_DIVISION(1, "DIVS"),  //  The F is handled in the SA
    MODULO(1, "MODS"),
    NEGATION(1, "NEGS"),
    AND(1, "ANDS"),
    NOT(1, "NOTS"),
    
    //  COMPARISON LEVEL OPERATORS
    EQUAL(2, "CMPEQS"),
    NEQUAL(2, "CMPNEQS"),
    GEQUAL(2, "CMPGES"),
    LEQUAL(2, "CMPLES"),
    LTHAN(2, "CMPLTS"),
    GTHAN(2, "CMPGTS"),
    
    NOOP(-1, "NOOP");
    
    Operator(int p, String code) {
        this.precedence = p;
        this.code = code;
    }
    public int precedence;
    public String code;
}
