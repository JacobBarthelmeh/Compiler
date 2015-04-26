package symboltable;
import util.Kind;
import util.Type;
public class Parameter {
    public String name;
    public Type type;
    public Kind kind;
    public Parameter(String name, Type type, Kind kind){
        this.name = name;
        this.type = type;
        this.kind = kind;
    }
    @Override
    public String toString() {
        return "(" + name + ":" + type + "," + kind + ")";
    }
}
