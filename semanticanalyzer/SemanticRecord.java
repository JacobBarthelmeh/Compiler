package semanticanalyzer;
import compiler.Token;
import symboltable.Symbol;
import util.Type;

public class SemanticRecord {
    

    public Token token;
    public Symbol symbol;
    public String code;
    
    //  Can be found from symbol, but not all records have symbols
    public Type type;
    
    // why is this needed?
    public String opp;
    public String register;
    
    /**
     * Generate everything in one initialize
     * @param token
     * @param symbol
     * @param code
     * @param opp
     * @param register
     * @param type 
     */
    public SemanticRecord(Token token, Symbol symbol, String code, String opp,
            String register, Type type) {
        this.token = token;
        this.symbol = symbol;
        this.code = code;
        this.opp = opp;
        this.register = register;
        this.type = type;
    }
    
    /**
     * Allow the record to generate its own contents
     * @param token
     * @param symbol 
     */
    public SemanticRecord(Token token, Symbol symbol) {
        this.token = token;
        this.symbol = symbol;
        if (symbol == null) {
            type = Type.NOTYPE;
        }
        else {
            type = symbol.type;
        }
        switch(token.getTerminal()) {
            case IDENTIFIER:
                code = symbol.offset + "(D" + symbol.nestinglevel + ")";
                break;
            case INTEGER_LIT:
                code = "#" + token.getContents();
                type = Type.INTEGER;
                break;
            case FLOAT_LIT:
                code = "#" + token.getContents();
                type = Type.FLOAT;
                break;
            case STRING_LIT:
                code = "#\"" + token.getContents() + "\"";
                type = Type.STRING;
                break;
            case TRUE:
                code = "#1";
                type = Type.BOOLEAN;
                break;
            case FALSE:
                code = "#0";
                type = Type.BOOLEAN;
                break;
            default:
                code = "";
                break;
        }
        
        opp = "";
        register = "";
    }


    @Override
    public String toString() {
        return "Semantic Record:" +
                "\nType: " + type +
                "\nOpp: " + opp +
                "\nRegister: " + register +
                "\nToken: " + token.toString();
    }
}
