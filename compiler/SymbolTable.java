package compiler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
public class SymbolTable {
    HashMap<String, SymbolEntry> entries;
    int offset;
    public SymbolTable () {
        entries = new HashMap();
        offset = 0;
    }
    public void addEntry(SymbolEntry entry) {
        entry.offset = offset;
        entries.put(entry.name, entry);
        switch (entry.type) {
            case INTEGER:
                offset += 4;
            case FLOAT:
                offset += 8;
            case BOOLEAN:
                offset += 1;
            case STRING:
                offset += 1;
        }
        
    }
    public SymbolEntry getEntry(String name) {
        return entries.get(name);
    }
    @Override
    public String toString() {
        String str = " Name | Type | Kind | Offset\n";
        for (Entry<String, SymbolEntry> entry : entries.entrySet()) {
            SymbolEntry e = entry.getValue();
            str += e + "\n";
        }
        return str;
    }
}
