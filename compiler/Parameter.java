package compiler;

import util.Type;
public class Parameter {
    public String name;
    public Type type;
    public Parameter(String name, Type type){
        this.name = name;
        this.type = type;
    }
}
