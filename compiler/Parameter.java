package compiler;
public class Parameter {
    public String name;
    public Symbol.Type type;
    public Parameter(String name, Symbol.Type type){
        this.name = name;
        this.type = type;
    }
}
