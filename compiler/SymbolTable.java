package compiler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
public class SymbolTable {
    HashMap<String, SymbolEntry> entries;
    String name;
    public SymbolTable (String name) {
        this.name = name;
        entries = new HashMap();
    }
    public SymbolEntry addEntry(String name, SymbolEntry.Type type, 
            SymbolEntry.Kind kind, SymbolEntry.Memory mem,
            ArrayList<Parameter> parameters) {
        SymbolEntry entry = new SymbolEntry(type, kind, mem, parameters);
        entries.put(name, entry);
        return entry;
    }
    public SymbolEntry getEntry(String name) {
        return entries.get(name);
    }
    @Override
    public String toString() {
        String str = " Name | Type | Kind | Memory\n";
        for (Entry<String, SymbolEntry> entry : entries.entrySet()) {
            SymbolEntry e = entry.getValue();
            str += e + "\n";
        }
        return str;
    }
}
