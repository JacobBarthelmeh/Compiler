package semanticanalyzer;
import compiler.Compiler;
import compiler.Token;
import java.util.Stack;
import symboltable.Symbol;
import symboltable.SymbolTableHandler;
import util.Operator;
import util.Type;
import util.Writer;

public class SemanticAnalyzer {
    public static SemanticRecord SP = new SemanticRecord(null, null, "SP", "", "", Type.NOTYPE);
    
    static int labelCounter = 0;
    public boolean noerrors = true;
    Stack<Type> types;
    
    //  Initialization time conflict - just set it public
    public SymbolTableHandler sh;
    
    
    //  GENERAL NECESSARY FUNCTIONS
    public void error(String err) {
        if (Compiler.DEBUG){
            System.err.println(err);
        }
    }
    
    /**
     * Push something to the stack
     * @param rec What to push onto the stack
     */
    public void genPush(SemanticRecord rec) {
        w.writeLine("PUSH " + rec.code);
    }

    /**
     * Halt the program and close the output file.
     */
    public void genHalt() {
        w.writeLine("HLT");
        w.close();
    }

    /**
     * Drop a label 
     * @param label The label
     */
    public void genLabel(int label) {
        w.writeLine("L" + label + ":");
    }
    
    public void genMove(String from, String to) {
        w.writeLine("MOV " + from + " " + to);
    }
    
    
    //  C LEVEL: EXPRESSIONS, ASSIGNMENTS, READS, WRITES
    
    //  HANDLING EXPRESSIONS
    /**
     * Handles casting arithmetic properly to float if necessary
     * @param left The left operand
     * @param right The right operand
     * @return Whether the result is dealing with floating point arithmetic
     */
    public boolean handleArithCasts(SemanticRecord left, SemanticRecord right) {
        //  Error checking on the left side
        if (left.symbol.type != Type.INTEGER && left.symbol.type != Type.FLOAT) {
            Token t = left.token;
            error("Left operand is incompatible with arithmetic functions. "
            + t.getContents() + " at line " + t.getLine() + " col " + t.getCol());
            noerrors = false;
            return false;
        }
        //  Error checking on the right side
        if (right.symbol.type != Type.INTEGER && right.symbol.type != Type.FLOAT) {
            Token t = right.token;
            error("Right operand is incompatible with arithmetic functions. "
            + t.getContents() + " at line " + t.getLine() + " col " + t.getCol());
            noerrors = false;
            return false;
        }
        //  Cast the left one properly
        if (left.symbol.type == Type.INTEGER && right.symbol.type == Type.FLOAT) {
            w.writeLine("SUB SP 1 SP");
            w.writeLine("CASTSF");
            w.writeLine("ADD SP 1 SP");
            return true;
        }
        //  Cast the right one properly
        else if (left.symbol.type == Type.FLOAT && right.symbol.type == Type.INTEGER) {
            w.writeLine("CASTSF");
            return true;
        }
        return left.symbol.type == Type.FLOAT || right.symbol.type == Type.FLOAT;
    }
    
    /**
     * Generate an arithmetic operation given two operands
     * @param left The left operand
     * @param opp The operator
     * @param right The right operand
     * @return Whether floating point arithmetic was used
     */
    public boolean genArithOperator(SemanticRecord left, Operator opp, SemanticRecord right) {
        if (handleArithCasts(left, right)) {
            w.writeLine(opp.code + "F");
            return true;
        }
        else {
            w.writeLine(opp.code);
            return false;
        }
    }
    /**
     * Generate a logical operation given two operands
     * @param left The left operand
     * @param opp The operator
     * @param right The right operand
     */
    public void genLogicalOperator(SemanticRecord left, Operator opp, SemanticRecord right) {
        if (left.type != Type.BOOLEAN) {
            Token t = left.token;
            error("Left operand is incompatible with arithmetic functions. "
            + t.getContents() + " at line " + t.getLine() + " col " + t.getCol());
            noerrors = false;
            return;
        }
        if (right.type != Type.BOOLEAN) {
            Token t = right.token;
            error("Right operand is incompatible with arithmetic functions. "
            + t.getContents() + " at line " + t.getLine() + " col " + t.getCol());
            noerrors = false;
            return;
        }
        w.writeLine(opp.code);
    }        
    /**
     * The record that is being negated.
     * @param rec The integer or float record to negate
     */
    public void genNegation(SemanticRecord rec) {
        if (rec.type == Type.INTEGER) {
            w.writeLine("NEGS");
        }
        else if (rec.type == Type.FLOAT) {
            w.writeLine("NEGSF");
        }
        else {
            error("Cannot negate a non-numeric value.");
        }
    }
    
    //  ASSIGNMENT
    /**
     * Signal the Semantic 
     * @param rec The location to assign to
     */
    public void genAssignment(SemanticRecord rec) {
        w.writeLine("POP " + rec.code);
    }
    
    //  READING
    /**
     * Signal the Semantic Analyzer that a genRead shall begin
     * @param rec The record containing the destination to read into
     */
    public void genRead(SemanticRecord rec) {
        String str = "RD";
        try {
            switch (rec.type) {
                case FLOAT:
                    str += "F";
                    break;
                case INTEGER:
                    //  don't append
                    break;
                case STRING:
                    str += "S";
                    break;
                default:
                    Token t = rec.token;
                    System.err.println("Read parameter must be of type Float, Integer, or String. Found "
                            + t.getContents() + " at line " + t.getLine() + " col " + t.getCol());
                    System.err.println("Was expecting a variable name");
                    break;
            }
            w.writeLine(str + rec.code);
        }
        catch (RuntimeException e) {
            Token t = rec.token;
            System.err.println("Read Error: found " + t.getContents()
                    + " at line " + t.getLine() + " col " + t.getCol());
            System.err.println("Was expecting a variable name");
        }
    }
    
    //  WRITING
    boolean line;
    /**
     * Signal the Semantic Analyzer that a write shall begin
     * @param line Whether to writeln
     */
    public void startwrite(boolean line) {
        line = true;
    }
    /**
     * Signal the Semantic Analyzer to write
     * @param t The Token containing the desired write value
     */
    public void write(Token t) {
        String code = "#" + t.getContents();
        switch (t.getTerminal()) {
            case IDENTIFIER:
                Symbol s = sh.getEntry(t.getContents());
                code = s.offset + "(D" + s.nestinglevel + ")";
                break;
                //  Handle other types
        }
        if (line) {
            w.writeLine("WRTLN " + code);
        }
        else {
            w.writeLine("WRT " + code);
        }
    }
    
    
    
    //  B LEVEL
    //  Prepare conditional branching
    public void ifstatement() {
    }
    public void whilestatement() {
    }
    public void forstatement() {
    }
    
    //  A LEVEL
    //  Prepare nesting level stuff
    public void function() {
    }
    public void procedure() {
    }
        /**
     * Push the stack pointer
     */
    public void genStackPush() {
        w.writeLine("ADD SP #1 SP");
    }

    public SemanticAnalyzer(String filename, SymbolTableHandler sh) {
        w = new Writer(filename);
        this.sh = sh;
    }

    private final Writer w;
}
