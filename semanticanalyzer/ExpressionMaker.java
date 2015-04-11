package semanticanalyzer;
import compiler.Token;
import java.util.Stack;
import symboltable.Symbol;
import symboltable.SymbolTableHandler;
import util.Operator;
import util.Type;
import util.Writer;

public class ExpressionMaker {
    public ExpressionMaker(SymbolTableHandler sh, Writer w) {
        this.sh = sh;
        this.w = w;
        types = new Stack();
        operators = new Stack();
    }
    SymbolTableHandler sh;
    Writer w;
    Stack<Type> types;
    Stack<Operator> operators;
    
    private Type getType(Token t) {
        switch (t.getTerminal()) {
            case INTEGER_LIT:
                return Type.INTEGER;
            case STRING_LIT:
                return Type.STRING;
            case FLOAT_LIT:
                return Type.FLOAT;
            case TRUE: case FALSE:
                return Type.BOOLEAN;
            default:
                System.err.println("Failed to match type. Found "
                        + t.getContents() + " at line " + t.getLine() + " col " + t.getCol());
                return Type.NOTYPE;
        }
    }
    //  C LEVEl
    public void receiveLiteral(Token t) {
        w.writeLine("PUSH #" + t.getContents());
        types.push(getType(t));
    }
    public void receiveVariable(Token t) {
        SemanticRecord sr = new SemanticRecord();
        Symbol s = sh.getEntry(t.getContents());
        w.writeLine("PUSH " + s.offset + "(D" + s.nestinglevel + ")");
        types.push(getType(t));
    }
    public boolean receiveOperator(Token t) {
        Operator o;
        switch (t.getTerminal()) {
            case PLUS:
                o = Operator.ADDITION;
                break;
            case MINUS:
                o = Operator.SUBTRACTION;
                break;
            case TIMES:
                o = Operator.MULTIPLICATION;
                break;
            case DIV: case FLOAT_DIVIDE:
                o = Operator.DIVISION;
                break;
            case MOD:
                o = Operator.MODULO;
                break;
            case EQUAL:
                o = Operator.EQUAL;
                break;
            case NEQUAL:
                o = Operator.NEQUAL;
                break;
            case GEQUAL:
                o = Operator.GEQUAL;
                break;
            case LEQUAL:
                o = Operator.LEQUAL;
                break;
            case GTHAN:
                o = Operator.GTHAN;
                break;
            case LTHAN:
                o = Operator.LTHAN;
                break;
            default:
                System.out.println("Invalid operator " + t.getTerminal());
                o = null;
                break;
        }
        Operator previous = operators.empty() ? null : operators.peek();
        
        boolean good = true;
        //  Before pushing the new operator, make sure it isn't smaller than the
        //  previous one.
        if (previous != null && o != null) {
            if (o.precedence() < previous.precedence()) {
                good = finishArithmetic(t);
            }
        }
        
        //  Now operator can be pushed
        operators.push(o);
        return good;
    }
    public void receiveNegation(Token t) {
        operators.push(Operator.NEGATION);
    }
    public boolean finishArithmetic(Token t) {
        if (types.isEmpty()) {
            System.err.println("Not enough types on stack "
                            + "at line " + t.getLine() + " col " + t.getCol());
            return false;
        }
        Type b = types.pop();
        //  There was only one value on the stack so it must be resolved
        if (types.empty()) {
            return true;
        }
        
        if (operators.isEmpty()) {
            System.err.println("Not enough operators for stack "
                            + "at line " + t.getLine() + " col " + t.getCol());
            return false;
        }
        
        Operator o = operators.pop();
        if (o == Operator.NOT) {
            if (b != Type.BOOLEAN) {
                System.err.println("Expression Error: Applying Boolean operation on a non-Boolean value "
                            + "at line " + t.getLine() + " col " + t.getCol());
                return false;
            }
            w.writeLine("NOTS");
            //  Return a type boolean
            types.push(b);
            return true;
        }
        else if (o == Operator.NEGATION) {
            switch (b) {
                case INTEGER:
                    w.writeLine("NEGS");
                    types.push(b);
                    break;
                case FLOAT:
                    w.writeLine("NEGSF");
                    types.push(b);
                    break;
                default:
                    System.err.println("Expression Error: Applying numeric operation on a nonnumeric value "
                                + "at line " + t.getLine() + " col " + t.getCol());
                    return false;
            }
            return true;
        }
        
        Type a = types.pop();
        
        switch (o) {
            case ADDITION: case SUBTRACTION: case MULTIPLICATION: case DIVISION:
            case NEGATION: case MODULO: case LEQUAL: case LTHAN: case GEQUAL:
            case GTHAN:
                if (a != Type.INTEGER && a != Type.FLOAT
                        || b != Type.INTEGER && b != Type.FLOAT) {
                    System.err.println("Expression Error: Applying numerical operation on non-numeric a value "
                            + "at line " + t.getLine() + " col " + t.getCol());

                    return false;
                }
                break;
            case AND: case OR:
                if (a != Type.BOOLEAN || b != Type.BOOLEAN) {
                    System.err.println("Expression Error: Applying a Boolean operation on a non-Boolean value "
                            + "at line " + t.getLine() + " col " + t.getCol());
                    return false;
                }
                break;
        }
        
        boolean useFloat = a == Type.FLOAT || b == Type.FLOAT;
        
        if (a == Type.INTEGER && b == Type.FLOAT) {
            //  The casting hack
            w.writeLine("SUB SP 1 SP");
            w.writeLine("CASTSF");
            w.writeLine("ADD SP 1 SP");
        }
        
        if (b == Type.INTEGER && a == Type.FLOAT) {
            w.writeLine("CASTSF");
        }
        w.writeLine(o.code() + (useFloat ? "F" : ""));
        
        //  If they are mismatched then use float
        if (useFloat) {
            types.push(Type.FLOAT);
        }
        
        //  Otherwise they match so it doesn't matter which is pushed
        else {
            types.push(b);
        }
        return true;
    }
}
