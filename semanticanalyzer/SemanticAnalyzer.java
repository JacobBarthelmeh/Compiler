package semanticanalyzer;

import compiler.Compiler;
import compiler.Token;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import symboltable.Parameter;
import symboltable.Symbol;
import symboltable.SymbolTableHandler;
import util.Kind;
import util.Operator;
import util.Type;
import util.Writer;

public class SemanticAnalyzer {
    HashMap<String, Integer> procedures, functions;
    
    public static SemanticRecord SP = new SemanticRecord(null, null, "SP", "", "", Type.NOTYPE);
    
    static int LABEL_COUNTER = 0;
    public boolean noerrors = true;
    Stack<Type> types;

    //  Initialization time conflict - just set it public
    public SymbolTableHandler sh;

    //  GENERAL NECESSARY FUNCTIONS
    public void error(String err) {
        if (Compiler.DEBUG) {
            System.err.println(err);
        }
        noerrors = false;
    }

    /**
     * Push something to the stack
     *
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
    
    public void genMove(String from, String to) {
        w.writeLine("MOV " + from + " " + to);
    }

    //  C LEVEL: EXPRESSIONS, ASSIGNMENTS, READS, WRITES
    //  HANDLING EXPRESSIONS
    /**
     * Handles casting arithmetic properly to float if necessary
     *
     * @param left The left operand
     * @param right The right operand
     * @return Whether the result is dealing with floating point arithmetic
     */
    public boolean handleArithCasts(SemanticRecord left, SemanticRecord right) {
        //  Error checking on the left side
        if (left.type != Type.INTEGER && left.type != Type.FLOAT) {
            Token t = left.token;
            error("Left operand is incompatible with arithmetic functions. "
                    + t.getContents() + " at line " + t.getLine() + " col " + t.getCol());
            return false;
        }
        //  Error checking on the right side
        if (right.type != Type.INTEGER && right.type != Type.FLOAT) {
            Token t = right.token;
            error("Right operand is incompatible with arithmetic functions. "
                    + t.getContents() + " at line " + t.getLine() + " col " + t.getCol());
            return false;
        }
        //  Cast the left one properly
        if (left.type == Type.INTEGER && right.type == Type.FLOAT) {
            w.writeLine("SUB SP #1 SP");
            w.writeLine("CASTSF");
            w.writeLine("ADD SP #1 SP");
            return true;
        } //  Cast the right one properly
        else if (left.type == Type.FLOAT && right.type == Type.INTEGER) {
            w.writeLine("CASTSF");
            return true;
        }
        return left.type == Type.FLOAT || right.type == Type.FLOAT;
    }

    /**
     * Generate an arithmetic operation given two operands
     *
     * @param left The left operand
     * @param opp The operator
     * @param right The right operand
     * @return Whether floating point arithmetic was used
     */
    public boolean genArithOperator_S(SemanticRecord left, Operator opp, SemanticRecord right) {
        if (handleArithCasts(left, right)) {
            w.writeLine(opp.code + "F");
            return true;
        } else {
            w.writeLine(opp.code);
            return false;
        }
    }

    /**
     * Generate a logical operation given two operands
     *
     * @param left The left operand
     * @param opp The operator
     * @param right The right operand
     */
    public void genLogicalOperator_S(SemanticRecord left, Operator opp, SemanticRecord right) {
        if (left.type != Type.BOOLEAN) {
            Token t = left.token;
            error("Left operand is incompatible with arithmetic functions. "
                    + t.getContents() + " at line " + t.getLine() + " col " + t.getCol());
            return;
        }
        if (right.type != Type.BOOLEAN) {
            Token t = right.token;
            error("Right operand is incompatible with arithmetic functions. "
                    + t.getContents() + " at line " + t.getLine() + " col " + t.getCol());
            return;
        }
        w.writeLine(opp.code);
    }
    
    public void genNot_S() {
        w.writeLine("NOTS");
    }

    /**
     * The record that is being negated.
     *
     * @param rec The integer or float record to negate
     */
    public void genNegation_S(SemanticRecord rec) {
        if (rec.type == Type.INTEGER) {
            w.writeLine("NEGS");
        } else if (rec.type == Type.FLOAT) {
            w.writeLine("NEGSF");
        } else {
            error("Cannot negate a non-numeric value.");
        }
    }

    //  ASSIGNMENT
    /**
     * Signal the Semantic
     *
     * @param into The location to assign to
     * @param from Where to find the value's type
     */
    public void genAssignment(SemanticRecord into, SemanticRecord from) {
        //  Handle the casting problem
        //  Results in loss of data accuracy at runtime
        if (into.type == Type.INTEGER && from.type == Type.FLOAT) {
            w.writeLine("CASTSI");
        }
        w.writeLine("POP " + into.code);
    }

    //  READING
    /**
     * Signal the Semantic Analyzer that a genRead shall begin
     *
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
            w.writeLine(str + " " + rec.code);
        } catch (RuntimeException e) {
            Token t = rec.token;
            System.err.println("Read Error: found " + t.getContents()
                    + " at line " + t.getLine() + " col " + t.getCol());
            System.err.println("Was expecting a variable name");
        }
    }

    //  WRITING
    boolean line;

    /**
     * Signal the Semantic Analyzer that a genWrite_S shall begin
     *
     * @param line Whether to writeln
     */
    public void startWrite(boolean line) {
        this.line = line;
    }

    /**
     * Signal the Semantic Analyzer to genWrite_S
     *
     * @param t The Token containing the desired genWrite_S value
     */
    public void genWrite_S(Token t) {
        String code = "#" + t.getContents();
        switch (t.getTerminal()) {
            case IDENTIFIER:
                Symbol s = sh.getEntry(t.getContents());
                code = s.offset + "(D" + s.nestinglevel + ")";
                break;
            //  Handle other types
        }
        if (line) {
            w.writeLine("WRTLNS");
        } else {
            w.writeLine("WRTS");
        }
    }

    //  B LEVEL
    //  Prepare conditional branchinG
    public int newLabel() {
        return LABEL_COUNTER++;
    }

    public void putLabel(int l) {
        w.writeLine("L" + l + ":");
    }

    public void genBranch(int l) {
        w.writeLine("BR L" + l);
    }

    public void genBranchFalse_S(int l) {
        w.writeLine("BRFS L" + l);
    }

    public void genForInitialize(SemanticRecord control, SemanticRecord initial) {
        //  It should only ever be an integer... right?
        w.writeLine("PUSH " + initial.code);
        if (initial.type == Type.INTEGER) {
            w.writeLine("POP " + control.code);
        } else if (initial.type == Type.FLOAT) {
            w.writeLine("CASTSI");
            w.writeLine("POP " + control.code);
        } else {
            error("Cannot construct a non-numeric iterator");
        }
    }

    public void genForAlter(SemanticRecord control, boolean increment) {
        //  to
        if (increment) {
            //  increment
            w.writeLine("ADD " + control.code + " #1 " + control.code);
        } //  downto
        else {
            //  decrement
            w.writeLine("SUB " + control.code + " #1 " + control.code);
        }
    }

    public void genForTest(SemanticRecord control, boolean increment, SemanticRecord end) {
        w.writeLine("PUSH " + control.code);
        w.writeLine("PUSH " + end.code);
        if (increment) {
            w.writeLine("CMPLTS");
        } else {
            w.writeLine("CMPGTS");
            
        }
    }

    //  A LEVEL
    //  Functions and Procedures
    public void genProcedureLabel(Symbol procedure) {
        w.writeLine("L" + LABEL_COUNTER + ":");
        procedures.put(procedure.name, LABEL_COUNTER++);
    }
    public void genFunctionLabel(Symbol function) {
        w.writeLine("L" + LABEL_COUNTER + ":");
        functions.put(function.name, LABEL_COUNTER++);
    }
    public void removeLocals(ArrayList<Symbol> locals) {
        w.writeLine("SUB SP " + locals.size() + " SP");
    }
    public void prepareCall(Symbol callLocation, ArrayList<SemanticRecord> actual) {
        ArrayList<Parameter> formal = callLocation.params;
        Parameter f;
        SemanticRecord a;
        
        //  Function need one slot just UNDER the new location for return value
        //  There is also no need to pop this because it will be on top of the
        //  stack when the stack call has finished.
        if (callLocation.kind == Kind.FUNCTION) {
            w.writeLine("ADD SP #1 SP");
        }
        
        //  Preserve the old nesting level
        w.writeLine("PUSH D" + callLocation.nestinglevel);
        
        //  Prepare the next nesting level
        w.writeLine("MOV SP D" + callLocation.nestinglevel);
        for (int i = 0; i < actual.size(); i++) {
            //  Type check
            try {
                f = formal.get(i);
                a = actual.get(i);
            }
            catch (IndexOutOfBoundsException e) {
                error("Procedure input is not the same size as the parameter list for "
                    + callLocation.name);
                break;
            }
            if (a.type != f.type) {
                error("Procedure input is not the same type as the parameter declaration "
                    + callLocation.name);
                break;
            }
            
            //  push the parameter
            w.writeLine("PUSH " + a.code);
        }
        if (callLocation.kind == Kind.FUNCTION) {            
            w.writeLine("CALL L" + functions.get(callLocation.name));
        }
        else {
            w.writeLine("CALL L" + procedures.get(callLocation.name));
        }
    }
    public void comeFromCall(Symbol callLocation, ArrayList<SemanticRecord> actual) {
        ArrayList<Parameter> formal = callLocation.params;
        Parameter f;
        SemanticRecord a;
        //  Reverse order because stack
        for (int i = formal.size() - 1; -1 < i; i--) {
            //  Type check
            f = formal.get(i);
            
            //  Preserve value
            if (f.kind == Kind.INOUTPARAMETER) {
                a = actual.get(i);
                w.writeLine("POP " + a.code);
            }
            else if (f.kind == Kind.INPARAMETER) {
                w.writeLine("POP");
            }
        }
        
        //  Restore previous nesting level
        w.writeLine("POP D" + callLocation.nestinglevel);
    }
    public void popToReturnLocation(Symbol function) {
        
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
        procedures = new HashMap<>();
        functions = new HashMap<>();
    }
    
    private final Writer w;
}
