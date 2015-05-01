package compiler;

import parser.Parser;
import semanticanalyzer.SemanticAnalyzer;
import symboltable.SymbolTableHandler;
import util.Test;

public class Compiler {

    public static boolean DEBUG = true;

    public static void main(String[] args) {
        if (args.length > 0) {
            String fIn = "";
            String fOut = "a.mm"; //default output a.mm
            String curDir = System.getProperty("user.dir");
            for (int i = 0; i < args.length; i++) {
                switch (args[i].trim()) {
                    case "-c":
                        fIn = curDir + args[++i];
                        fOut = fIn;
                        fOut = fOut.replace(".mp", ".mm");
                        break;
                    case "-o":
                        fOut = curDir + args[++i];
                }
            }
            Parser par = new Parser(new SemanticAnalyzer(fOut, new SymbolTableHandler()));
            par.setRuleOutputFile(fOut);
            par.parseFile(fIn);
        } else {
            Test.run(args);
        }
    }
}
