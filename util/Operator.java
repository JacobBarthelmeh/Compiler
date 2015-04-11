package util;
public enum Operator {
    //  ADDITION LEVEL OPERATORS
    ADDITION(0, "ADDS"),
    SUBTRACTION(0, "SUBS"),
    OR(0, "ORS"),
    
    //  MULTIPLICATION LEVEL OPERATORS
    MULTIPLICATION(1, "MULS"),
    DIVISION(1, "DIVS"),
    AND(1, "ANDS"),
    
    //  COMPARISON LEVEL OPERATORS
    EQUAL(2, "CMPEQS"),
    NEQUAL(2, "CMPNEQS"),
    GEQUAL(2, "CMPGEQS"),
    LEQUAL(2, "CMPLEQS"),
    LTHAN(2, "CMPLTS"),
    GTHAN(2, "CMPGTS");
    
    Operator(int p, String code) {
        this.p = p;
        this.code = code;
    }
    int p;
    String code;
    public int precedence() {
        return p;
    }
    public String code() {
        return code;
    }
}
