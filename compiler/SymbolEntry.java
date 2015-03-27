package compiler;
import java.util.ArrayList;
public class SymbolEntry {
    public enum Type {
        INTEGER,
        FIXED,
        FLOAT,
        STRING,
        BOOLEAN,
        NOTYPE
    };
    public enum Kind {
        VARIABLE,
        INPARAMETER,
        INOUTPARAMETER,
        PROCEDURE,
        FUNCTION,
        NOKIND
    };
    public String name;
    public Type type;
    public Kind kind;
    public ArrayList<Parameter> params;
    public int offset;
    public SymbolEntry(String lexeme) {
        name = lexeme;
    }
    
    @Override
    public String toString() {
        return  type + " | " +
                kind + " | " + 
                (params == null ? "" : params.toString());
    }
}
