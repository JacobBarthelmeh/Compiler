package symboltable;
import java.util.HashMap;
import java.util.Map.Entry;
public class SymbolTable {
    HashMap<String, Symbol> entries;
    int offset;
    public SymbolTable () {
        entries = new HashMap();
        offset = 0;
    }
    public void addEntry(Symbol entry, int nestinglevel) {
        entry.offset = offset;
        entry.nestinglevel = nestinglevel;
        entries.put(entry.name, entry);
        offset += 1;
    }
    public Symbol getEntry(String name) {
        return entries.get(name);
    }
    @Override
    public String toString() {
        String str = " Name | Type | Kind | Offset | Nesting | Params\n";
        for (Entry<String, Symbol> entry : entries.entrySet()) {
            Symbol e = entry.getValue();
            str += e.toString() + "\n";
        }
        return str;
    }
}
