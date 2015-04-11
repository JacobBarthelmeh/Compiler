package util;
public enum Operator {
    //  ADDITION LEVEL OPERATORS
    ADDITION(0),
    SUBTRACTION(0),
    OR(0),
    
    //  MULTIPLICATION LEVEL OPERATORS
    MULTIPLICATION(1),
    DIVISION(1),
    AND(1),
    
    //  COMPARISON LEVEL OPERATORS
    EQUAL(2),
    GEQUAL(2),
    LEQUAL(2),
    LTHAN(2),
    GTHAN(2);
    
    Operator(int p) {
        this.p = p;
    }
    int p;
    public int precedence() {
        return p;
    }
}
