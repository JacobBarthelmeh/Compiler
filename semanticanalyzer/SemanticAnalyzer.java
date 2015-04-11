package semanticanalyzer;
import compiler.Token;
import java.util.Stack;
import symboltable.Symbol;
import symboltable.SymbolTableHandler;
import util.Type;
import util.Writer;

public class SemanticAnalyzer {
    static int labelCounter = 0;
    
    //  Initialization time conflict - just set it public
    public SymbolTableHandler sh;
    
    //  C LEVEL: EXPRESSIONS, ASSIGNMENTS, READS, WRITES
    
    //  HANDLING EXPRESSIONS
    Stack <ExpressionMaker> expressions;
    /**
     * Allow the parser to pass a literal in
     * @param t The token containing the literal
     */
    public void receiveLiteral(Token t) {
        expressions.peek().receiveLiteral(t);
    }
    /**
     * Allow the parser to pass a variable in
     * @param t The token containing the variable
     */
    public void receiveVariable(Token t) {
        expressions.peek().receiveVariable(t);
    }
    /**
     * Signal the Semantic Analyzer that it should prepare to handle an expression
     */
    public void startExpression() {
        expressions.push(new ExpressionMaker(sh, w));
    }
    /**
     * Signal the Semantic Analyzer that the expression has been completed
     */
    public void endExpression() {
        ExpressionMaker expression = expressions.peek();
        while (!expression.values.empty()) {
            expression.finishArithmetic();
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
        Symbol s = sh.getEntry(t.getContents());
        w.writeLine("RD " + s.offset + "(D" + s.nestinglevel + ")");
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
        //  Determine whether to write a literal or a variable
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

    
    //  Let the parser tell the SA it found some stuff
    //  The SA should handle the logic
    public void receiveOperator(Token t) {
    }
    public void receieveBoolean(Token t) {
    }
    public void receiveString(Token t) {
    }
    public void receieveFloat(Token t) {
    }
    public void receiveInteger(Token t) {
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
        expressions = new Stack();
        this.sh = sh;
    }

    private final Writer w;
}
