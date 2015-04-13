package semanticanalyzer;
import compiler.Compiler;
import compiler.Token;
import java.util.Stack;
import symboltable.Symbol;
import symboltable.SymbolTableHandler;
import util.Type;
import util.Writer;

public class SemanticAnalyzer {
    static int labelCounter = 0;
    public boolean noerrors = true;
    Stack<Type> types;
    
    //  Initialization time conflict - just set it public
    public SymbolTableHandler sh;
    
    //  C LEVEL: EXPRESSIONS, ASSIGNMENTS, READS, WRITES
    
    //  HANDLING EXPRESSIONS
//    Stack <ExpressionMaker> expressions;
    public void genPush(SemanticRecord record) {
        w.writeLine("PUSH " + record.code());
    }
    public void error(String err) {
        if (Compiler.DEBUG){
            System.err.println(err);
        }
    }
    /**
     * Handles casting arithmetic properly to float if necessary
     * @param left The left operand
     * @param right The right operand
     * @return Whether the result is dealing with floating point arithmetic
     */
    public boolean handleArithCasts(SemanticRecord left, SemanticRecord right) {
        //  Error checking on the left side
        if (left.sym.type != Type.INTEGER && left.sym.type != Type.FLOAT) {
            Token t = left.token;
            error("Left operand is incompatible with arithmetic functions. "
            + t.getContents() + " at line " + t.getLine() + " col " + t.getCol());
            noerrors = false;
            return false;
        }
        //  Error checking on the right side
        if (right.sym.type != Type.INTEGER && right.sym.type != Type.FLOAT) {
            Token t = right.token;
            error("Right operand is incompatible with arithmetic functions. "
            + t.getContents() + " at line " + t.getLine() + " col " + t.getCol());
            noerrors = false;
            return false;
        }
        //  Cast the left one properly
        if (left.sym.type == Type.INTEGER && right.sym.type == Type.FLOAT) {
            w.writeLine("SUB SP 1 SP");
            w.writeLine("CASTSF");
            w.writeLine("ADD SP 1 SP");
            return true;
        }
        //  Cast the right one properly
        else if (left.sym.type == Type.FLOAT && right.sym.type == Type.INTEGER) {
            w.writeLine("CASTSF");
            return true;
        }
        return left.sym.type == Type.FLOAT || right.sym.type == Type.FLOAT;
    }
    /**
     * Generate an addition statement given two operands
     * @param left The left operand
     * @param right The right operand
     * @return Whether the addition was performed using floating point arithmetic
     */
    public boolean genAdds(SemanticRecord left, SemanticRecord right) {
        if (handleArithCasts(left, right)) {
            w.writeLine("ADDSF");
            return true;
        }
        else {
            w.writeLine("ADDS");
            return false;
        }
    }
    /**
     * Generate a subtraction statement given two operands
     * @param left The left operand
     * @param right The right operand
     * @return Whether the subtraction was performed using floating point arithmetic
     */
    public boolean genSubs(SemanticRecord left, SemanticRecord right) {
        if (handleArithCasts(left, right)) {
            w.writeLine("SUBSF");
            return true;
        }
        else {
            w.writeLine("SUBS");
            return false;
        }
    }
    /**
     * Generate a multiplication statement given two operands
     * @param left The left operand
     * @param right The right operand
     * @return Whether the multiplication was performed using floating point arithmetic
     */
    public boolean genMuls(SemanticRecord left, SemanticRecord right) {
        if (handleArithCasts(left, right)) {
            w.writeLine("MULSF");
            return true;
        }
        else {
            w.writeLine("MULS");
            return false;
        }
    }
    /**
     * Generate a division statement given two operands
     * @param left The left operand
     * @param right The right operand
     * @return Whether the division was performed using floating point arithmetic
     */
    public boolean genDivs(SemanticRecord left, SemanticRecord right) {
        if (handleArithCasts(left, right)) {
            w.writeLine("DIVSF");
            return true;
        }
        else {
            w.writeLine("DIVS");
            return false;
        }
    }
    
    public boolean genPush(SemanticRecord rec) {
        w.writeLine("PUSH " + rec.code());
    }
    /**
     * Allow the parser to pass a literal in
     * @param t The token containing the literal
     */
    public void receiveLiteral(SemanticRecord record) {
        expressions.peek().receiveLiteral(t);
    }
    /**
     * Allow the parser to pass a variable in
     * @param t The token containing the variable
     */
    public void receiveVariable(SemanticRecord record) {
        expressions.peek().receiveVariable(t);
    }
    /**
     * Allow the parser to pass a variable in
     * @param t The token containing the variable
     */
    public void receiveOperator(Token t) {
        if (!expressions.peek().receiveOperator(t)) {
            noerrors = false;
            System.err.println("at line " + t.getLine() + " col " + t.getCol());
        }
    }
    public void receiveNegation(Token t) {
        expressions.peek().receiveNegation(t);
    }

    /**
     * Signal the Semantic Analyzer that it should prepare to handle an expression
     */
    public void startExpression() {
        expressions.push(new ExpressionMaker(sh, w));
    }
    /**
     * Signal the Semantic Analyzer that the expression has been completed
     * @param t The token that triggered the end of expression
     */
    public void endExpression(Token t) {
        ExpressionMaker expression = expressions.peek();
        while (!expression.types.empty()) {
            if (!expression.finishArithmetic(t)) {
                noerrors = false;
            }
            expression.finishArithmetic(t);
        }
        expressions.pop();
    }
    
    //  ASSIGNMENT
    SemanticRecord assignvariable;
    /**
     * Signal the Semantic Analyzer that an assignment shall begin
     * @param t The token containing the desired destination
     */
    public void startAssign(Token t) {
        assignvariable = new SemanticRecord();
        Symbol s = sh.getEntry(t.getContents());
        assignvariable.code = s.offset + "(D" + s.nestinglevel + ")";
    }
    /**
     * Signal the Semantic Analyzer that an assignment statement has ended
     */
    public void endAssign() {
        w.writeLine("POP " + assignvariable.code);
    }
    
    //  READING
    /**
     * Signal the Semantic Analyzer that a read shall begin
     * @param t The token containing the destination to read into
     */
    public void read(Token t) {
        try {
            Symbol s = sh.getEntry(t.getContents());
            switch (s.type) {
                case FLOAT:
                    w.writeLine("RDF " + s.offset + "(D" + s.nestinglevel + ")");
                    break;
                case INTEGER:
                    w.writeLine("RD " + s.offset + "(D" + s.nestinglevel + ")");
                    break;
                case STRING:
                    w.writeLine("RDS " + s.offset + "(D" + s.nestinglevel + ")");
                    break;
                default:
                    System.err.println("Read Error: Input variable must be of type Float, Integer, or String. Found "
                            + t.getContents() + " at line " + t.getLine() + " col " + t.getCol());
                    System.err.println("Was expecting a variable name");
                    break;
            }
        }
        catch (RuntimeException e) {
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
     * Close the file code is being written to
     */
    public void close() {
        w.close();
    }
    
    public void genMov(String from, String to) {
        w.writeLine("MOV " + from + " " + to);
    }

    public void genHalt() {
        w.writeLine("HLT");
        w.close();
    }

    //  This arith code likely will not be used but let's not delete it yet
    /**
     * Used for generating arithmetic such as ADD, SUB, MUL
     *
     * @param left_record left hand side of operation
     * @param right_record right hand side of operation
     * @param dest_record destination of operation
     * @return record that contains type on stack from operations
     */
    public Record genArith(Record left_record, Record right_record,
            Record dest_record) {
        Record rec = new SemanticRecord();
        return rec;
    }

    /**
     * Used for generating arithmetic such as ADDS, SUBS, MULS, ORS
     *
     * @param left_record left hand side of operation
     * @param right_record right hand side of operation
     * @return record that contains type on stack from operations
     */
    public Record genArithS(Record left_record, Record right_record) {
        Record rec = new SemanticRecord();
        Type type = left_record.getType();

        //Test if cst is needed
        if (left_record.getType() != right_record.getType()) {
            //@TODO cast
        }

        String str = "";
        str += left_record.getOpp();
        w.writeLine(str);

        rec.setType(type);

        return rec;
    }

    /**
     * Generate a no
     *
     * @param sa_record
     * @return
     */
    public Record genNot(Record sa_record) {
        Record rec = new SemanticRecord();
        Type type = sa_record.getType();

        //Test if cst is needed
        if (sa_record.getType() != Type.BOOLEAN) {
            //@TODO cast
        }

        String str = "";
        str += sa_record.getOpp();
        w.writeLine(str);

        rec.setType(type);

        return rec;
    }

    /**
     * Push the stack pointer
     */
    public void genStackPush() {
        w.writeLine("ADD SP #1 SP");
    }

    /**
     * The genAssign function determines if working with the stack or not by
     * testing if the src record has a location. If no location stored than just
     * the type is used and it is assumed the value is at the top of the stack
     *
     * example from hypertextbook
     * <statement> -> <ident> := <expression> #gen_assign(ident_rec,
     * expression_rec) ;
     *
     * @param src record containing source type
     * @param dest destination location for value
     */
    public void genAssign(Record src, Record dest) {
        if (dest.getRegister() == null) {
            System.out.println("Error in genAssign : dest has no getRegister set!");
        }
        if (src.getRegister() == null) {
            w.writeLine("POP " + dest.getRegister());
        } else {
            w.writeLine("MOV " + src.getRegister() + " " + dest.getRegister());
        }
    }

    public void genLabel() {
        w.writeLine("L" + labelCounter++);
    }

    public SemanticAnalyzer(String filename, SymbolTableHandler sh) {
        w = new Writer(filename);
        this.sh = sh;
    }

    private final Writer w;
}
