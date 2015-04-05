package semanticanalyzer;

import util.Type;
import util.Writer;

public class SemanticAnalyzer {

    static int LabelCounter = 0;

    /**
     * Close the file code is being written to
     */
    public void close() {
        w.close();
    }

    /**
     * Generate code for pushing something to the stack
     *
     * @param offset offset of value in table ie 4D0 or 2D0 ....
     * @param nesting nesting level of information ie D0 or D1 ...
     */
    public void genPush(int offset, int nesting) {
        w.writeLine("PUSH " + offset + "(D" + nesting + ")");
    }

    public void genPush(String literal) {
        w.writeLine("PUSH #" + literal);
    }

    /**
     * Pop a value from the stack and store it into a location specified by
     * offset and nesting level
     *
     * @param offset offset of value in table ie 4D0 or 2D0 ....
     * @param nesting nesting level of information ie D0 or D1 ...
     */
    public void genPop(int offset, int nesting) {
        w.writeLine("POP " + offset + "(D" + nesting + ")");
    }

    public void genWrite(String args) {

    }

    public void genWriteln(String args) {

    }

    /**
     * Read in the information to the specified offset and nesting level
     *
     * @param offset
     * @param nesting
     */
    public void genRead(int offset, int nesting) {
        w.writeLine("RD " + offset + "(D" + nesting + ")");
    }

    public void genCasti() {
        w.writeLine("CASTI");
    }

    public void genCastf() {
        w.writeLine("CASTF");
    }

    public void genMov(String from, String to) {
        w.writeLine("MOV " + from + " " + to);
    }

    public void genHalt() {
        w.writeLine("HLT");
        w.close();
    }

    public SemanticAnalyzer(String filename) {
        w = new Writer(filename);
    }

    private final Writer w;

    /**
     * Used for generating arithmitic such as ADD, SUB, MUL
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
     * Used for generating arithmitic such as ADDS, SUBS, MULS, ORS
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
     * @param in amount to push it ie 4 for an int
     */
    public void genSPush(int in) {
        w.writeLine("ADD SP #" + in + " SP");
    }

}
