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
    public void addEntry(Symbol entry) {
        entry.offset = offset;
        entries.put(entry.name, entry);
        offset += SymbolTableHandler.typeSize(entry.type);
    }
    public Symbol getEntry(String name) {
        return entries.get(name);
    }
    @Override
    public String toString() {
        String str = " Name | Type | Kind | Offset\n";
        for (Entry<String, Symbol> entry : entries.entrySet()) {
            Symbol e = entry.getValue();
            str += e + "\n";
        }
        return str;
    }
}
