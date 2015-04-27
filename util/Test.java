package util;
import parser.Parser;
import semanticanalyzer.SemanticAnalyzer;
import symboltable.SymbolTableHandler;

public class Test {

    /* input files */
    static String inputC = "c-testfile.mp";
    static String inputB = "b-testfile.mp";
    static String inputA = "a-testfile.mp";

    /* output files */
    static String scannerOutC = "c-scanner_test.txt";
    static String parserOutC = "c-parser_test.txt";
    static String fileOutC = "c_output.mp";

    static String scannerOutB = "b-scanner_test.txt";
    static String parserOutB = "b-parser_test.txt";
    static String fileOutB = "b_output.mp";

    static String scannerOutA = "a-scanner_test.txt";
    static String parserOutA = "a-parser_test.txt";
    static String fileOutA = "a_output.mp";

    public static void main(String[] args) {
        /*
        //  C RANKED TESTS
        System.out.println("Running C ranked tests");
        if (parser_test(inputC, parserOutC, fileOutC)) {
            System.out.println("pass");
        } else {
            System.out.println("fail!!!");
        }
        //  B RANKED TESTS
        System.out.println("Running B ranked tests");
        if (parser_test(inputB, parserOutB, fileOutB)) {
            System.out.println("pass");
        } else {
            System.out.println("fail!!!");
        }
        
        //  A RANKED TESTS
        System.out.println("Running A ranked tests");
        if (parser_test(inputA, parserOutA, fileOutA)) {
           System.out.println("pass");
        } else {
            System.out.println("fail!!!");
        }
        */
        String
            testLoc = "Test_Program_1.mp",
            parseOutput = "testprogram1parse.txt",
            outputLoc = "Test_Program_1.mm";
        System.out.println("Running " + testLoc);
        if (parser_test("Test_Program_1.mp", parseOutput, outputLoc)) {
           System.out.println("pass");
        } else {
            System.out.println("fail!!!");
        }
    }

    /**
     * Function used to test the parser operations
     *
     * @param fIn name of the input file
     * @param fOut name of the output file for debris such as rule tree
     * @param file The .MP file to print the machine code to
     * @return true on success, false on fail
     */
    public static boolean parser_test(String fIn, String fOut, String file) {
        Parser par = new Parser(new SemanticAnalyzer(file, new SymbolTableHandler()));
        par.setRuleOutputFile(fOut);
        return par.parseFile(fIn);
    }
}
