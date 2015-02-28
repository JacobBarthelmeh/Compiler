package compiler;
import java.util.HashMap;
public class SymbolTableHandler {
    private HashMap<String, SymbolTable> tables;
    public SymbolTableHandler() {
        tables = new HashMap();
    }
    public SymbolTable newTable(String name) {
        SymbolTable table = new SymbolTable(name);
        tables.put(name, table);
        return table;
    }
    public SymbolTable getTable(String name) {
        return tables.get(name);
    }
}
