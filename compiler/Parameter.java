package compiler;
public class Parameter {
    public String name;
    public SymbolEntry.Type type;
    public Parameter(String name, SymbolEntry.Type type){
        this.name = name;
        this.type = type;
    }
}
