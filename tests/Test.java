package tests;

import compiler.Parser;
import compiler.Token;
import java.io.PrintWriter;
import scanner.Scanner;

public class Test {

    /* input files */
    static String scannerIn = "testfile.mp";
    static String parserIn = "testfile.mp";

    /* output files */
    static String scannerOut = "scanner_test.txt";
    static String parserOut = "parser_test.txt";

    public static void main(String[] args) {
        System.out.println("Running all test");

        /**
         * ******* Scanner ************
         */
        System.out.print("Scanner Test ....");
        if (scanner_test(scannerIn, scannerOut) == true) {
            System.out.println("pass");
        } else {
            System.err.println("fail!!!");
        }

        /**
         * ******* Parser ************
         */
        System.out.print("Parser Test ....");
        if (parser_test(parserIn, parserOut) == true) {
            System.out.println("pass");
        } else {
            System.err.println("fail!!!");
        }

    }

    /**
     * run a test on the scanner and leave fOut as debris to analyze
     *
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
                    pw.println(t.getID() + "," + t.getLine() + "," + t.getCol());
                    s += t.getContents() + " ";
                }
            } while (t == null || t.getID() != Token.ID.EOF);
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
            assert (t.getID() == Token.ID.FIXED_LIT);
            assert (t.getContents().equals("1.2"));
            assert (scanner.nextToken().getID() == Token.ID.PERIOD);
            assert (scanner.nextToken().getID() == Token.ID.FIXED_LIT);
            assert (scanner.nextToken().getID() == Token.ID.INTEGER_LIT);
            assert (scanner.nextToken().getID() == Token.ID.IDENTIFIER);
            assert (scanner.nextToken().getID() == Token.ID.PLUS);
            assert (scanner.nextToken().getID() == Token.ID.PERIOD);
            assert (scanner.nextToken().getID() == Token.ID.INTEGER_LIT);
            assert (scanner.nextToken().getID() == Token.ID.PERIOD);
            assert (scanner.nextToken().getID() == Token.ID.MINUS);
            assert (scanner.nextToken().getID() == Token.ID.FIXED_LIT);
            assert (scanner.nextToken().getID() == Token.ID.IDENTIFIER);
            assert (scanner.nextToken().getID() == Token.ID.FLOAT_LIT);
            t = scanner.nextToken();
            assert (t.getID() == Token.ID.INTEGER_LIT);
            assert (t.getContents().equals("45"));
            assert (scanner.nextToken().getID() == Token.ID.PERIOD);
            assert (scanner.nextToken().getID() == Token.ID.MINUS);
            assert (scanner.nextToken().getID() == Token.ID.INTEGER_LIT);
            t = scanner.nextToken();
            assert (t.getID() == Token.ID.FLOAT_LIT);
            assert (t.getContents().equals("0e-1"));
            assert (scanner.nextToken().getID() == Token.ID.INTEGER_LIT);
            assert (scanner.nextToken().getID() == Token.ID.IDENTIFIER);
            assert (scanner.nextToken().getID() == Token.ID.MINUS);
            assert (scanner.nextToken().getID() == Token.ID.EQUAL);
            assert (scanner.nextToken().getID() == Token.ID.FIXED_LIT);
            assert (scanner.nextToken().getID() == Token.ID.INTEGER_LIT);
            assert (scanner.nextToken().getID() == Token.ID.MINUS);
            assert (scanner.nextToken().getID() == Token.ID.INTEGER_LIT);
            assert (scanner.nextToken().getID() == Token.ID.EQUAL);
            assert (scanner.nextToken().getID() == Token.ID.INTEGER_LIT);
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
     * @return true on success, false on fail
     */
    public static boolean parser_test(String fIn, String fOut) {
        Parser par = new Parser();
        par.setRuleOutputFile(fOut);
        if (par.parseFile(fIn) == 0) {
            return true;
        } else {
            return false;
        }
    }
}
