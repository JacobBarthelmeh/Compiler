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
        values = new Stack();
        operators = new Stack();
    }
    SymbolTableHandler sh;
    Writer w;
    Stack<SemanticRecord> values;
    Stack<Operator> operators;
    
    //  C LEVEl
    public void receiveLiteral(Token t) {
        SemanticRecord sr = new SemanticRecord();
        sr.code = "#" + t.getContents();
        values.push(sr);
    }
    public void receiveVariable(Token t) {
        SemanticRecord sr = new SemanticRecord();
        Symbol s = sh.getEntry(t.getContents());
        sr.code = s.offset + "(D" + s.nestinglevel + ")";
        values.push(sr);
    }
    public void receiveOperator(Token t) {
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
            case DIV:
                o = Operator.DIVISION;
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
        
        //  Before pushing the new operator, make sure it isn't smaller than the
        //  previous one.
        if (previous != null && o != null) {
            if (o.precedence() < previous.precedence()) {
                finishArithmetic();
            }
        }
        
        //  Now operator can be pushed
        operators.push(o);
    }
    public void finishArithmetic() {
        SemanticRecord b = values.pop();
        //  There was only one value on the stack
        if (values.empty()) {
            w.writeLine("PUSH " + b.code);
            return;
        }
        
        SemanticRecord a = values.pop();
        Operator o = operators.pop();
        
        boolean useFloat = a.type == Type.FLOAT || b.type == Type.FLOAT;
        w.writeLine("PUSH " + a.code);
        if (a.type == Type.INTEGER && b.type == Type.FLOAT) {
            w.writeLine("CASTSF");
        }
        w.writeLine("PUSH " + a.code);
        if (b.type == Type.INTEGER && a.type == Type.FLOAT) {
            w.writeLine("CASTSF");
        }
        w.writeLine(o.code() + (useFloat ? "F" : ""));
    }
}
