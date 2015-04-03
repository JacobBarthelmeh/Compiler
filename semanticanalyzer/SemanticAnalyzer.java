package semanticanalyzer;

import util.Writer;
public class SemanticAnalyzer {
    static int LabelCounter = 0;

    public void genAnds() {
        w.writeLine("ANDS");
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

    /**
     * 
     * @param src1
     * @param src2
     * @param dest
     */
    public void genAdd(String src1, String src2, String dest) {
        w.writeLine("ADD " + src1 + " " + src2 + " " + dest);
    }
    
    /**
     * Pull the first two values from the stack and then push to results onto
     * the stack
     */
    public void genAdds() {
        w.writeLine("ADDS");
    }
    public void genAddsf() {
        w.writeLine("ADDSF");
    }

    /**
     * Pull the first two values than push the subtraction of them
     */
    public void genSubs() {
        w.writeLine("SUBS");
    }
    public void genSubsf() {
        w.writeLine("SUBSF");
    }

    /**
     * Pull first two values of the  stack and push the resulting multiplication
     * of them
     */
    public void genMuls() {
        w.writeLine("MULS");
    }
    
    public void genMulsf() {
        w.writeLine("MULSF");
    }

    public void genDivs() {
        w.writeLine("DIVS");
    }
    public void genDivsf() {
        w.writeLine("DIVSF");
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
}
