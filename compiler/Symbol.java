package compiler;
import java.util.ArrayList;
import util.*;
public class Symbol {
    public String name;
    public Type type;
    public Kind kind;
    public ArrayList<Parameter> params;
    public int offset;
    public Symbol(String lexeme) {
        name = lexeme;
    }
    
    @Override
    public String toString() {
        return  type + " | " +
                kind + " | " + 
                (params == null ? "" : params.toString());
    }
}
