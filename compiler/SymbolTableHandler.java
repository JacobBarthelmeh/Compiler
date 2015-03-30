package compiler;
import java.util.ArrayList;
public class SymbolTableHandler {
    private Symbol entry;
    public int nestinglevel;
    private final SymbolTable[] tables;
    public SymbolTableHandler() {
        tables = new SymbolTable[10];
        nestinglevel = -1;
    }
    public void startEntry(String lexeme){
        entry = new Symbol(lexeme);
        entry.params = new ArrayList<>();
    }
    public void addParameter(Parameter p) {
        if (entry == null) {
            throw new RuntimeException("Parameters cannot be added to null entry.");
        }
        entry.params.add(p);
    }
    public void setType(Symbol.Type type) {
        if (entry == null) {
            throw new RuntimeException("Type cannot be set to null entry.");
        }
        entry.type = type;
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
