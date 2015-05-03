package symboltable;
import compiler.Compiler;
import java.util.ArrayList;
import util.Type;
import util.Kind;
public class SymbolTableHandler {
    private Symbol entry;
    private boolean isParam;
    private Parameter param;
    public int nestinglevel;
    private final SymbolTable[] tables;
    public SymbolTableHandler() {
        tables = new SymbolTable[10];
        nestinglevel = -1;
    }
    public void startEntry(){
        isParam = false;
        entry = new Symbol("DEFAULT");
        entry.params = new ArrayList<>();
    }
    public void finishEntry(){
        if (entry == null) {
            throw new RuntimeException("Cannot finalize a null entry.");
        }
        tables[nestinglevel].addEntry(entry, nestinglevel);
        entry = null;
    }
    public void startParameter() {
        if (isParam) {
            throw new RuntimeException(
                    "Cannot add a parameter to a parameter.");   
        }
        if (entry == null) {
            throw new RuntimeException(
                    "Parameters cannot be added to null entry.");
        }
        param = new Parameter("DEFAULT", Type.NOTYPE, Kind.NOKIND);
        isParam= true;
    }
    public void finishParameter() {
        if (param.kind == Kind.NOKIND) {
            throw new RuntimeException("Parameter needs a kind.");
        }
        if (param.type == Type.NOTYPE) {
            throw new RuntimeException("Parameter needs a type.");
        }
        isParam = false;
        entry.params.add(param);
    }
    public void setName(String name) {
        if (entry == null) {
            throw new RuntimeException(
                    "Parameters cannot be added to null entry.");
        }
        name = name.toLowerCase();
        if (isParam) {
            param.name = name;
        }
        else {
            if (tables[nestinglevel].getEntry(name) != null) {
                throw new RuntimeException("Entry with lexeme '" + name
                        + "' already exists for this nesting level.");
            }
            entry.name = name;
        }
    }
    public void setType(Type type) {
        if (entry == null) {
            throw new RuntimeException("Type cannot be set to null entry.");
        }
        if (isParam) {
            param.type = type;
        }
        else {
            entry.type = type;
        }
    }
    public void setKind(Kind kind) {
        if (entry == null) {
            throw new RuntimeException("Kind cannot be set to null entry.");
        }
        if (isParam) {
            param.kind = kind;
        }
        else {
            entry.kind = kind;
        }
    }
    public void pushTable() {
        if (entry != null) {
            throw new RuntimeException(
                    "Cannot push table while making a symbol.");
        }
        if (nestinglevel == 9) {
            throw new RuntimeException("Cannot push passed ten tables.");
        }
        nestinglevel++;
        tables[nestinglevel] = new SymbolTable();
    }
    public void popTable() {
        if (entry != null) {
            throw new RuntimeException(
                    "Cannot pop table while making a symbol.");
        }
        if (nestinglevel == -1) {
            throw new RuntimeException("Can't pop the global table!");
        }
        if (Compiler.DEBUG) {
            System.out.println("Pop reached! Table status before printing:");
            System.out.println(toString());
        }
        nestinglevel--;
    }
    public Symbol getEntry(String lexeme) {
        lexeme = lexeme.toLowerCase();
        for (int level = nestinglevel; -1 < level; level--) {
            Symbol e = tables[level].getEntry(lexeme);
            if (e != null) {
                return e;
            }
        }
        if (Compiler.DEBUG) {
            System.out.println(toString());
        }
        throw new RuntimeException("Could not find '" + lexeme
                + "' in any symbol table.");
    }    
    @Override
    public String toString() {
        String str = "Number of tables: " + (nestinglevel + 1);
        for (int i = 0; i < nestinglevel + 1; i++) {
            str += "\nNesting level " + i + ":\n";
            str += tables[i].toString();
        }
        return str;
    }
    
}
