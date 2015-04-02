package semanticanalyzer;

import util.Writer;
public class SemanticAnalyzer {

    /**
     * Generate code for pushing something to the stack
     *
     * @param offset offset of value in table ie 4D0 or 2D0 ....
     * @param nesting nesting level of information ie D0 or D1 ...
     */
    public void genPush(int offset, int nesting) {

    }

    /**
     * Pop a value from the stack and store it into a location specified by
     * offset and nesting level
     *
     * @param offset offset of value in table ie 4D0 or 2D0 ....
     * @param nesting nesting level of information ie D0 or D1 ...
     */
    public void genPop(int offset, int nesting) {

    }

    /**
     * Pull the first two values from the stack and then push to results onto
     * the stack
     */
    public void genAdd() {

    }

    /**
     * Pull the first two values than push the subtraction of them
     */
    public void genSub() {

    }

    /**
     * Pull first two values of the  stack and push the resulting multiplication
     * of them
     */
    public void genMul() {

    }

    public void genWrite() {

    }

    /**
     * Read in the information to the specified offset and nesting level
     * 
     * @param offset
     * @param nesting 
     */
    public void genRead(int offset, int nesting) {

    }

    /**
     * Gen code to cast values
     * 
     * @TODO will need type passed in
     */
    public void genCast() {

    }

    
    public SemanticAnalyzer(String filename) {
        w = new Writer(filename);
    }
    
    private final Writer w;
}
