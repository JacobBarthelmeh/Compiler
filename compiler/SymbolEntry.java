package compiler;
import java.util.ArrayList;
public class SymbolEntry {
    public enum Type {
        INTEGER,
        FIXED,
        FLOAT,
        STRING,
        BOOLEAN,
        NOTYPE
    };
    public enum Kind {
        VARIABLE,
        PARAMETER,
        PROCEDURE,
        FUNCTION,
        NOKIND
    };
    public enum Memory {
        COPY,
        REFERENCE,
        NOMEM
    };
    Type type;
    Kind kind;
    Memory mem;
    ArrayList<Parameter> params;
    public SymbolEntry(Type type, Kind kind, Memory mem, ArrayList<Parameter> params) {
        this.type = type;
        this.kind = kind;
        this.mem = mem;
        this.params = params;
    }
    public Type getType() {
        return type;
    }
    public Kind getKind() {
        return kind;
    }
    public Memory getMemory() {
        return mem;
    }
    public ArrayList<Parameter> getParameters() {
        return params;
    }
    @Override
    public String toString() {
        return  type + " | " +
                kind + " | " + 
                mem + " | " + 
                (params == null ? "" : params.toString());
    }
}
