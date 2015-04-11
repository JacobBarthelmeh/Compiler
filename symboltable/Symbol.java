package symboltable;
import java.util.ArrayList;
import util.Type;
import util.Kind;
public class Symbol {
    public String name;
    public Type type;
    public Kind kind;
    public ArrayList<Parameter> params;
    public int offset, nestinglevel;
    public Symbol(String lexeme) {
        name = lexeme;
    }
    
    @Override
    public String toString() {
        return  name + " | " +
                type + " | " +
                kind + " | " +
                nestinglevel + " | " + 
                (params == null ? "" : params.toString());
    }
}
