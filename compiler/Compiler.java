package compiler;

import parser.Parser;
import semanticanalyzer.SemanticAnalyzer;
import symboltable.SymbolTableHandler;
import util.Test;

public class Compiler {

    public static boolean DEBUG = false;

    public static void main(String[] args) {
        if (args.length > 0) {
            String fIn = "";
            String fOut = "a.mm"; //default output a.mm
            String rOut = "RuleOutput.csv"; //output file for rules if wanted
            String curDir = System.getProperty("user.dir") + "/";
            for (int i = 0; i < args.length; i++) {
                switch (args[i].trim()) {
                    case "-c":
                        fIn = curDir + args[++i];
                        fOut = fIn;
                        fOut = fOut.replace(".mp", ".mm");
                        break;
                    case "-o":
                        fOut = curDir + args[++i];
                        break;
                    case "-d":
                        Compiler.DEBUG = true;
                        break;
                    case "-h":
                        System.out.println("");
                        System.out.println("Micro Pascel Compiler Help");
                        System.out.println("-c outputFile.mp");
                        System.out.println("-o outputFile.mm");
                        System.out.println("-d flag to turn on debug mode");
                        System.out.println("If no arguments, than tries to run on all .mp files in directory");
                        System.exit(0);
                }
            }
            Parser par = new Parser(new SemanticAnalyzer(fOut, new SymbolTableHandler()));
            par.setRuleOutputFile(rOut);
            par.parseFile(fIn);
        } else {
            Test.run(args);
        }
    }
}
