package util;

import java.io.File;
import parser.Parser;
import semanticanalyzer.SemanticAnalyzer;
import symboltable.SymbolTableHandler;

public class Test {

    public static void run(String[] args) {
        try {

            /*
             File search method based from
             http://stackoverflow.com/questions/15482423/how-to-list-the-files-in-current-directory
             */
            File f = new File("."); //gets the current directory

            //look for files in the directory ending with .mp
            File[] files = f.listFiles();
            System.out.println("Looking at all files in current directory and compiling those that end in .mp");
            for (File file : files) {
                if (!file.isDirectory()) {
                    String s = file.getCanonicalPath();
                    if (s.contains(".mp")) {
                        System.out.println("Compiling " + s);
                        String fOut = s.replace(".mp", ".mm");
                        String fRules = s.replace(".mp", ".csv");
                        parser_test(s, fRules, fOut);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error when reading in files " + e);
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
