package semanticanalyzer;

import compiler.Compiler;
import compiler.Token;
import java.util.ArrayList;
import java.util.HashMap;
import symboltable.Parameter;
import symboltable.Symbol;
import symboltable.SymbolTableHandler;
import util.Kind;
import util.Operator;
import util.Type;
import util.Writer;

public class SemanticAnalyzer {

    //  Whether or not errors have arisen - cancels compilation if set to true
    public boolean noerrors = true;

    //  Initialization time conflict - just set it public
    public SymbolTableHandler sh;

    //  A special file writer that only creates/writes to a file if the compile
    //  was successful.
    private final Writer w;

    //  Hashmap provides easy lookup for procedures/functions to know which
    //  labels to reference. 
    private final HashMap<String, Integer> callLocations;

    //  The running coint of how many labels have been made
    private static int LABEL_COUNTER = 0;

    //  The writing mode. True for writeln, false for write
    private boolean writingLine;

    // tell if processing parameters
    public boolean funcCall;

    //  GENERAL NECESSARY FUNCTIONS
    /**
     * Produce an error and cancel compile.
     *
     * @param err The error message
     */
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
        if (rec.symbol != null) {
            switch (rec.symbol.kind) {
                case INOUTVARIABLE:
                    if (funcCall) {
                        w.writeLine("PUSH " + rec.code);
                    } else {
                        w.writeLine("PUSH @" + rec.code);
                    }
                    break;
                case INOUTPARAMETER:
                    //Put address of variable onto the stack
                    w.writeLine("PUSH D" + rec.symbol.nestinglevel);
                    w.writeLine("PUSH #" + rec.symbol.offset);
                    w.writeLine("ADDS");
                    break;
                case VARIABLE:
                    if (funcCall) {
                        //Put address of variable onto the stack
                        w.writeLine("PUSH D" + rec.symbol.nestinglevel);
                        w.writeLine("PUSH #" + rec.symbol.offset);
                        w.writeLine("ADDS");
                    } else {
                        w.writeLine("PUSH " + rec.code);
                    }
                    break;
                default:
                    w.writeLine("PUSH " + rec.code);
            }
        } else {
            w.writeLine("PUSH " + rec.code);
        }
    }

    /**
     * Used for storing register values on the stack when starting the program
     */
    public void genStoreRegisters(int nestingL) {
        w.writeLine("PUSH D" + nestingL);
        w.writeLine("MOV SP D" + nestingL);
    }

    /**
     * Used for restoring register values after program run
     */
    public void genRestoreRegisters(int nestingL) {
        w.writeLine("MOV D" + nestingL + " SP");
        w.writeLine("POP D" + nestingL);
    }

    /**
     * Halt the program and close the output file.
     */
    public void genHalt() {
        w.writeLine("HLT");
        w.close();
    }

    /**
     * Move something from one place to another.
     *
     * @param from The place to move from
     * @param to The place to move to
     */
    public void genMove(String from, String to) {
        w.writeLine("MOV " + from + " " + to);
    }

    //  C LEVEL: EXPRESSIONS, ASSIGNMENTS, READS, WRITES
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

    /**
     * Generate a negation of a boolean.
     */
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
     * Assign a variable to a destination. Type casting is handled.
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
        if (from.symbol != null && from.symbol.kind == Kind.FUNCTION) {// & from.symbol.kind == Kind.FUNCTION) {
            //push returned value onto the stack
            w.writeLine("PUSH " + from.code);
        }
        if (into.symbol.kind == Kind.INOUTPARAMETER || into.symbol.kind == Kind.INOUTVARIABLE) {
            w.writeLine("POP @" + into.code);
        } else {
            w.writeLine("POP " + into.code);
        }
    }

    //  READING
    /**
     * Read user input into the program.
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
    /**
     * Flag the begin of a write statement
     *
     * @param writingLine Whether to writeln rather than just write
     */
    public void startWrite(boolean writingLine) {
        this.writingLine = writingLine;
    }

    /**
     * Generate a write statement using current writing mode.
     *
     * @param t The Token containing the desired write value to write
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
        if (writingLine) {
            w.writeLine("WRTLNS");
        } else {
            w.writeLine("WRTS");
        }
    }

    //  B LEVEL
    //  Prepare conditional branching
    /**
     * Create a new label.
     *
     * @return The new label value.
     */
    public int newLabel() {
        return LABEL_COUNTER++;
    }

    /**
     * Put a label into the program.
     *
     * @param l The label to put into the program.
     */
    public void putLabel(int l) {
        w.writeLine("L" + l + ":");
    }

    /**
     * Put an unconditional branch to a label.
     *
     * @param l The label to branch to.
     */
    public void genBranch(int l) {
        w.writeLine("BR L" + l);
    }

    /**
     * Put a conditional branch to a label.
     *
     * @param l The label to branch to if the condition is false.
     */
    public void genBranchFalse_S(int l) {
        w.writeLine("BRFS L" + l);
    }

    /**
     * Generate the initialize part of a for statement
     *
     * @param control The variable to iterate over
     * @param initial The initial value of that variable
     */
    public void genForInitialize(SemanticRecord control, SemanticRecord initial) {
        //  It should only ever be an integer... right?
        w.writeLine("PUSH " + initial.code);
        //  Cast it anyway? lol
        if (initial.type == Type.INTEGER) {
            w.writeLine("POP " + control.code);
        } else if (initial.type == Type.FLOAT) {
            w.writeLine("CASTSI");
            w.writeLine("POP " + control.code);
        } else {
            error("Cannot construct a non-numeric iterator");
        }
    }

    /**
     * Generate the alter part of a for statement
     *
     * @param control The variable to iterate over
     * @param increment The iterate direction (up = true, down = false)
     */
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

    /**
     * Generate the test part of a for statement
     *
     * @param control The control variable to iterate over
     * @param increment Whether to iterate up rather than down
     * @param end The expected end value of the for loop
     */
    public void genForTest(SemanticRecord control, boolean increment, SemanticRecord end) {
        //  Push parameters
        w.writeLine("PUSH " + control.code);
        w.writeLine("PUSH " + end.code);
        //  Test <= or >= depends on whether to use increment or decrement
        if (increment) {
            w.writeLine("CMPLTS");
        } else {
            w.writeLine("CMPGTS");
        }
    }

    //  A LEVEL :3
    //  FUNCTIONS AND PROCEDURES CALLS
    /**
     * Begins the logic at the start of a procedure or function call.
     *
     * @param callLocation The location called to
     */
    public void onStartFormalCall(Symbol callLocation) {

        ArrayList<Parameter> params = callLocation.params;
        int nestingL = sh.nestinglevel;

        //  Generate the label to the destination
        w.writeLine("L" + LABEL_COUNTER + ":");
        callLocations.put(callLocation.name, LABEL_COUNTER++);

        //store register
        genStoreRegisters(nestingL);

        w.writeLine("ADD SP #" + params.size() + " SP");
        int offset = -2 - params.size();
        for (int i = 0; i < params.size(); i++) {
            w.writeLine("MOV " + (offset - params.size() + i) + "(SP) "
                    + sh.getEntry(params.get(i).name).offset + "(D" + nestingL + ")");
        }
    }

    /**
     * Removes the local variables from the current scope.
     *
     * @param locals The list of local variables
     */
    public void removeLocals(ArrayList<Symbol> locals) {
        //w.writeLine("SUB SP #" + locals.size() + " SP");
    }

    /**
     * Produces a return statement to the appropriate location
     *
     * @param callLocation The location called to
     */
    public void onEndFormalCall(Symbol callLocation) {

        //restore register used
        genRestoreRegisters(sh.nestinglevel + 1);
        //  return
        w.writeLine("RET");
    }

    /**
     * Prepares a call with the parameters provided and then makes a call to the
     * procedure or function.
     *
     * @param callLocation The destination to call to
     * @param actual The list of actual parameters provided
     */
    public void onStartActualCall(Symbol callLocation, ArrayList<SemanticRecord> actual) {
        ArrayList<Parameter> formal = callLocation.params;
        //  Error handling
        if (formal.size() != actual.size()) {
            error("Call Error: Incorrect number of parameters provided for call"
                    + " to " + callLocation.name + ". Provided: " + actual.size()
                    + ". Wanted: " + formal.size());
            return;
        }
        //  Needs to be iterative not iteratorative
        for (int i = 0; i < formal.size(); i++) {
            Parameter f = formal.get(i);
            SemanticRecord a = actual.get(i);
            if (f.type != a.type) {
                error("Call Error: Parameter provided is incorrect type.");
                return;
            }
        }
        w.writeLine("CALL L" + callLocations.get(callLocation.name));
    }

    /**
     * Prepares runtime to come back from a function or procedure call.
     *
     * @param callLocation The destination to call to
     * @param actual The list of actual parameters provided for the call
     */
    public void onEndActualCall(Symbol callLocation, ArrayList<SemanticRecord> actual) {
//        ArrayList<Parameter> formal = callLocation.params;
//        //  Again must be iterative
//        //  We could save 1/10000000th of a second by decrementing
//        for (int i = 0; i < formal.size(); i++) {
//            Parameter f = formal.get(formal.size() - 1 - i);
//            SemanticRecord a = actual.get(actual.size() - 1 - i);
//            if (f.kind == Kind.INOUTPARAMETER) {
//                w.writeLine("POP " + a.code);
//            } else {
//                w.writeLine("SUB SP #1 SP");
//            }
//        }
//        //  Restore the register to its value before the call
//        w.writeLine("POP D" + callLocation.nestinglevel);
        //  Because of the way the return value was only optionally added, there
        //  is no need for a procedure to worry about it. If it was a function
        //  call, the return value is now on the top of the stack.
    }

    /**
     * Provides padding on the stack to store a variable.
     */
    public void padForVariable() {
        //  It's one statement, but the method name helps provide usage
        w.writeLine("ADD SP #1 SP");
    }

    public SemanticAnalyzer(String filename, SymbolTableHandler sh) {
        w = new Writer(filename);
        this.sh = sh;
        callLocations = new HashMap<>();
    }
}
