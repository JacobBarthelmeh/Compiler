package compiler;
import java.util.ArrayList;
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
    public void startEntry(String lexeme){
        if (tables[nestinglevel].getEntry(lexeme) != null) {
            throw new RuntimeException("Entry with lexeme " + lexeme + " already exists for this nesting level.");
        }
        isParam = false;
        entry = new Symbol(lexeme);
        entry.params = new ArrayList<>();
    }
    public void addParameter(String name) {
        if (isParam) {
            throw new RuntimeException("Cannot add a parameter to a parameter.");   
        }
        if (entry == null) {
            throw new RuntimeException("Parameters cannot be added to null entry.");
        }
        param = new Parameter(name, Symbol.Type.NOTYPE);
    }
    public void finishParameter() {
        isParam = false;
        entry.params.add(param);
    }
    public void setType(Symbol.Type type) {
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
    public void setKind(Symbol.Kind kind) {
        if (entry == null) {
            throw new RuntimeException("Kind cannot be set to null entry.");
        }
        entry.kind = kind;
    }
    public void finalizeEntry(){
        if (entry == null) {
            throw new RuntimeException("Cannot finalize a null entry.");
        }
        tables[nestinglevel].addEntry(entry);
        entry = null;
    }
    public void pushTable() {
        if (entry != null) {
            throw new RuntimeException("Cannot push table while making a symbol.");
        }
        if (nestinglevel == 9) {
            throw new RuntimeException("Cannot push passed ten tables.");
        }
        nestinglevel++;
        tables[nestinglevel] = new SymbolTable();
    }
    public void popTable() {
        if (entry != null) {
            throw new RuntimeException("Cannot pop table while making a symbol.");
        }
        if (nestinglevel == -1) {
            throw new RuntimeException("Can't pop the global table!");
        }
        nestinglevel--;
    }
    public Symbol getEntry(String lexeme) {
        for (int level = nestinglevel; -1 < level; level--) {
            Symbol e = tables[level].getEntry(lexeme);
            if (e != null) {
                return e;
            }
        }
        throw new RuntimeException("Could not find " + lexeme + " in any symbol table.");
    }    
    @Override
    public String toString() {
        String str = "Symbol Table list:";
        for (int i = nestinglevel; -1 < nestinglevel; i++) {
            str += "Nesting level " + i + ":\n";
            str += tables[i].toString();
        }
        return str;
    }
}
