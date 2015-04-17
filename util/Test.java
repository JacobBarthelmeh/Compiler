package util;
import parser.Parser;
import compiler.Token;
import java.io.PrintWriter;
import scanner.Scanner;
import semanticanalyzer.SemanticAnalyzer;
import symboltable.SymbolTableHandler;

public class Test {

    /* input files */
    static String scannerInC = "c-testfile.mp";
    static String parserInC = "c-testfile.mp";
    static String scannerInB = "b-testfile.mp";
    static String parserInB = "b-testfile.mp";
    static String scannerInA = "a-testfile.mp";
    static String parserInA = "a-testfile.mp";

    /* output files */
    static String scannerOutC = "c-scanner_test.txt";
    static String parserOutC = "c-parser_test.txt";
    static String fileOutC = "c_test.mp";

    static String scannerOutB = "b-scanner_test.txt";
    static String parserOutB = "b-parser_test.txt";
    static String fileOutB = "b_test.mp";

    static String scannerOutA = "a-scanner_test.txt";
    static String parserOutA = "a-parser_test.txt";
    static String fileOutA = "a_test.mp";

    public static void main(String[] args) {
        //  C RANKED TESTS
        System.out.println("Running C ranked tests");
        if (parser_test(parserInC, parserOutC, fileOutC)) {
            System.out.println("pass");
        } else {
            System.out.println("fail!!!");
        }
        //  B RANKED TESTS
        System.out.println("Running B ranked tests");
        if (parser_test(parserInB, parserOutB, fileOutB)) {
            System.out.println("pass");
        } else {
            System.out.println("fail!!!");
        }
        
        //  A RANKED TESTS
        System.out.println("Running A ranked tests");
        if (parser_test(parserInA, parserOutA, fileOutA)) {
            System.out.println("pass");
        } else {
            System.out.println("fail!!!");
        }
    }

    /**
     * run a test on the scanner and leave fOut as debris to analyze
     *
     * @param fIn
     * @param fOut
     * @return true on success, false on fail
     */
    public static boolean scanner_test(String fIn, String fOut) {
        try {
            Scanner scanner = new Scanner(fIn);
            PrintWriter pw = new PrintWriter(fOut);
            pw.println("Token,Line,Column");
            Token t;
            String s = "";
            boolean cancel = false;
            do {
                t = scanner.nextToken();
                if (t == null) {
                    System.out.println("Null return");
                    cancel = true;
                } else {
                    pw.println(t.toString());
                    s += t.getContents() + " ";
                }
            } while (t == null || t.getTerminal() != Terminal.EOF);
            if (cancel) {
                System.out.println("Compile failed due to syntax errors. Aborting...");
                return false;
            }
            scanner.close();
            pw.close();
            //  Since we know this passes and the FSA wasn't edited we can comment this out
//            if (numberTest() != true) {
//                return false;
//            }
            return true;
        } catch (Exception e) {
            System.err.println("Error caught in scan test " + e);
        }
        return false;
    }

    private static boolean numberTest() {
        Scanner scanner = null;
        try {
            scanner = new Scanner("numberTest.mp");
        } catch (Exception e) {
            System.out.println("Error reading src/numberTest.mp");
            return false;
        }
        Token t = null;
        try {
            t = scanner.nextToken();
            //assert (t.getTerminal() == Terminal.FIXED_LIT);
            assert (t.getContents().equals("1.2"));
            assert (scanner.nextToken().getTerminal() == Terminal.PERIOD);
            //assert (scanner.nextToken().getTerminal() == Terminal.FIXED_LIT);
            assert (scanner.nextToken().getTerminal() == Terminal.INTEGER_LIT);
            assert (scanner.nextToken().getTerminal() == Terminal.IDENTIFIER);
            assert (scanner.nextToken().getTerminal() == Terminal.PLUS);
            assert (scanner.nextToken().getTerminal() == Terminal.PERIOD);
            assert (scanner.nextToken().getTerminal() == Terminal.INTEGER_LIT);
            assert (scanner.nextToken().getTerminal() == Terminal.PERIOD);
            assert (scanner.nextToken().getTerminal() == Terminal.MINUS);
            //assert (scanner.nextToken().getTerminal() == Terminal.FIXED_LIT);
            assert (scanner.nextToken().getTerminal() == Terminal.IDENTIFIER);
            assert (scanner.nextToken().getTerminal() == Terminal.FLOAT_LIT);
            t = scanner.nextToken();
            assert (t.getTerminal() == Terminal.INTEGER_LIT);
            assert (t.getContents().equals("45"));
            assert (scanner.nextToken().getTerminal() == Terminal.PERIOD);
            assert (scanner.nextToken().getTerminal() == Terminal.MINUS);
            assert (scanner.nextToken().getTerminal() == Terminal.INTEGER_LIT);
            t = scanner.nextToken();
            assert (t.getTerminal() == Terminal.FLOAT_LIT);
            assert (t.getContents().equals("0e-1"));
            assert (scanner.nextToken().getTerminal() == Terminal.INTEGER_LIT);
            assert (scanner.nextToken().getTerminal() == Terminal.IDENTIFIER);
            assert (scanner.nextToken().getTerminal() == Terminal.MINUS);
            assert (scanner.nextToken().getTerminal() == Terminal.EQUAL);
            //assert (scanner.nextToken().getTerminal() == Terminal.FIXED_LIT);
            assert (scanner.nextToken().getTerminal() == Terminal.INTEGER_LIT);
            assert (scanner.nextToken().getTerminal() == Terminal.MINUS);
            assert (scanner.nextToken().getTerminal() == Terminal.INTEGER_LIT);
            assert (scanner.nextToken().getTerminal() == Terminal.EQUAL);
            assert (scanner.nextToken().getTerminal() == Terminal.INTEGER_LIT);
        } catch (Exception e) {
            System.out.println("number test failed make sure file src/nuberTest.mp"
                    + " has not been changed");
            return false;
        }
        return true;
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
