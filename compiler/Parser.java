/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import scanner.Scanner;

/**
 *
 * @author team 4
 */
public class Parser {

    private volatile Token l1; // look ahead token
    private volatile Scanner scanner;
    private volatile PrintWriter rFile;
    private volatile String rule_tree_file = "rule_list.csv";
    private volatile boolean error_flag = false;

    private int Table[][];
    private String stackTrace = "";

    public enum NonTerminal {

        SystemGoal,
        Program,
        ProgramHeading,
        Block,
        VariableDeclarationPart,
        VariableDeclarationTail,
        VariableDeclaration,
        Type,
        ProcedureAndFunctionDeclarationPart,
        ProcedureDeclaration,
        FunctionDeclaration,
        ProcedureHeading,
        FunctionHeading,
        OptionalFormalParameterList,
        FormalParameterSectionTail,
        FormalParameterSection,
        ValueParameterSection,
        VariableParameterSection,
        StatementPart,
        CompoundStatement,
        StatementSequence,
        StatementTail,
        Statement,
        EmptyStatement,
        ReadStatement,
        ReadParameterTail,
        ReadParameter,
        WriteStatement,
        WriteParameterTail,
        WriteParameter,
        AssignmentStatement,
        IfStatement,
        OptionalElsePart,
        RepeatStatement,
        WhileStatement,
        ForStatement,
        ControlVariable,
        InitialValue,
        StepValue,
        FinalValue,
        ProcedureStatement,
        OptionalActualParameterList,
        ActualParameterTail,
        ActualParameter,
        Expression,
        OptionalRelationalPart,
        RelationalOperator,
        SimpleExpression,
        TermTail,
        OptionalSign,
        AddingOperator,
        Term,
        FactorTail,
        MultiplyingOperator,
        Factor,
        ProgramIdentifier,
        VariableIdentifier,
        ProcedureIdentifier,
        FunctionIdentifier,
        BooleanExpression,
        OrdinalExpression,
        IdentifierList,
        IdentifierTail
    };

    /**
     * read in csv ll1 table
     */
    public Parser() {
        try {
            /* java scanner used to read in ll1.csv table */
            java.util.Scanner sc = new java.util.Scanner(new File("ll1.csv"));
            sc.nextLine();
            Table = new int[63][];
            int index = 0;
            while (sc.hasNext()) {
                String str = sc.nextLine();
                char[] arr = str.toCharArray();
                arr = (removeStr(arr).toCharArray());
                arr = (removeStr(arr).toCharArray());
                int[] tmparr = new int[52];
                for (int i = 0; i < 52; i++) {
                    String current = nextStr(arr);
                    tmparr[i] = (current.equals("")) ? -1 : Integer.parseInt(current);
                    arr = (removeStr(arr).toCharArray());
                }
                Table[index++] = tmparr;
            }
        } catch (IOException e) {
            System.out.println("Error creating ll1 table from ll1.csv " + e);
        }
    }

    private String nextStr(char[] arr) {
        String s = "";
        int i = 0;
        while (arr[i] != ',' && i < arr.length) {
            s += arr[i++];
        }

        return s;
    }

    private String removeStr(char[] arr) {
        String s = "";
        int i = 0;
        while (arr[i] != ',' && i < arr.length) {
            i++;
        }
        i++; /* move over comma */

        for (; i < arr.length; i++) {
            s += arr[i];
        }

        return s;
    }

    /**
     * Set a file to parse
     *
     * @param in file to be parsed
     * @return 0 on success
     */
    public int parseFile(String in) {
        try {
            scanner = new Scanner(in);
            l1 = scanner.nextToken();
            SystemGoal();
            rFile.close();
            scanner.close();
            if (error_flag) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            System.out.println("\nSTACK TRACE of PARSER\n" + stackTrace);

            System.err.println("Error parsing file " + in + " in parser " + e);
        }
        return -1;
    }

    private int ruleFile(int rule) {
        if (rFile == null) {
            try {
                rFile = new PrintWriter(rule_tree_file);
                rFile.println("Rules Taken");
            } catch (Exception e) {
                System.out.println("Unable to make file " + rule_tree_file);
                return 1;
            }
        }
        rFile.println(rule);
        return 0;
    }

    /**
     * Used to set the output file for the rule tree created when parsing
     *
     * @param in name of file or directory
     */
    public void setRuleOutputFile(String in) {
        this.rule_tree_file = in;
    }

    /**
     * If match was found consume it and look ahead
     */
    private void match() {
        if (l1 == null) {
            System.err.println("Invalid input to parser match function.");
            System.exit(1);
        }
        l1 = scanner.nextToken();
        if (l1 == null) {
            System.err.println("Scanner gave the parser a null token");
            System.exit(1);
        }
    }

    /**
     * handle error print out
     *
     * @param err token that caused the error
     * @param expected array of expected tokens
     */
    private void error(String[] expected) {
        if (expected == null) {
            System.err.println("Improper input to parser error function.");
        }
        error_flag = true;
        System.err.println("Error found " + l1.getContents() + " "
                + l1.getID() + " at line " + l1.getLine() + " col " + l1.getCol());
        System.err.print("Was expecting ");
        System.err.print(Arrays.toString(expected));
        System.err.println("");

        /* possibly a system exit here? */
        System.out.println(stackTrace);
        rFile.close();
        System.exit(0);
    }

    /**
     * Get the rule to execute
     *
     * @param rule The current rule
     * @return The rule to execute
     */
    private int getRule(NonTerminal nt) {
        int index = l1.getID().ordinal(),
                nonTerminal = nt.ordinal();
        if (nonTerminal > Table.length) {
            System.out.println("Error nonTerminal " + nonTerminal + " is not in table");
            System.exit(1);
        }
        if (index > Table[nonTerminal].length) {
            System.out.println("Error token " + index + "  " + l1.getID() + " not in table ");
            System.out.println(" " + Table[nonTerminal].length);
            System.exit(1);
        }
        int rule = Table[nonTerminal][index];
        ruleFile(rule); //write the rule taken
        return rule;
    }

    //*************************************************************************
    //  Stubs for rules 1-39
    private void SystemGoal() {
        stackTrace += "SystemGoal\n";
        switch (getRule(NonTerminal.SystemGoal)) {
            case 1:
                Program();
                if (l1.getID() == Token.ID.EOF) {
                    match();
                } else {
                    String[] err = {"end of file"};
                    error(err);
                }
                break;
            default:
                String[] err = {"program"};
                error(err);
        }
    }

    private void Program() {
        stackTrace += "Program\n";
        switch (getRule(NonTerminal.Program)) {
            case 2:
                ProgramHeading();
                if (l1.getID() == Token.ID.SCOLON) {
                    match();
                } else {
                    String[] err = {";"};
                    error(err);
                }
                Block();
                if (l1.getID() == Token.ID.PERIOD) {
                    match();
                } else {
                    String[] err = {"."};
                    error(err);
                }
                break;
            default:
                String[] err = {"program"};
                error(err);
        }
    }

    private void ProgramHeading() {
        stackTrace += "ProgramHeading\n";
        switch (getRule(NonTerminal.ProgramHeading)) {
            case 3:
                if (l1.getID() == Token.ID.PROGRAM) {
                    match();
                } else {
                    String[] err = {"program"};
                    error(err);
                }
                ProgramIdentifier();
                break;
            default:
                String[] err = {"program"};
                error(err);
                break;
        }
    }

    private void Block() {
        stackTrace += "Block\n";
        switch (getRule(NonTerminal.Block)) {
            case 4:
                VariableDeclarationPart();
                ProcedureAndFunctionDeclarationPart();
                StatementPart();
                break;
            default:
                String[] err = {""};
                error(err);
        }
    }

    private void VariableDeclarationPart() {
        stackTrace += "VariableDeclarationPart\n";
        switch (getRule(NonTerminal.VariableDeclarationPart)) {
            case 5:
                if (l1.getID() == Token.ID.VAR) {
                    match();
                } else {
                    String[] err = {"var"};
                    error(err);
                }
                VariableDeclaration();
                if (l1.getID() == Token.ID.SCOLON) {
                    match();
                } else {
                    String[] err = {";"};
                    error(err);
                }
                VariableDeclarationTail();
                break;
            case 6:
                break;
            default:
                String[] err = {"var"};
                error(err);
        }
    }

    private void VariableDeclarationTail() {
        stackTrace += "VariableDeclarationTail\n";
        switch (getRule(NonTerminal.VariableDeclarationTail)) {
            case 7:
                VariableDeclaration();
                if (l1.getID() == Token.ID.SCOLON) {
                    match();
                } else {
                    String[] err = {";"};
                    error(err);
                }
                VariableDeclarationTail();
                break;
            case 8:
                break;
            default:
                String[] err = {"Integer", "Float", "String", "Boolean"};
                error(err);
                break;
        }
    }

    private void VariableDeclaration() {
        stackTrace += "VariableDeclaration\n";
        switch (getRule(NonTerminal.VariableDeclaration)) {
            case 9:
                IdentifierList();
                if (l1.getID() == Token.ID.COLON) {
                    match();
                } else {
                    String[] err = {":"};
                    error(err);
                }
                Type();
                break;
            default:
                String[] err = {"identifier"};
        }
    }

    private void Type() {
        stackTrace += "Type\n";
        switch (getRule(NonTerminal.Type)) {
            case 10:
                if (l1.getID() == Token.ID.INTEGER) {
                    match();
                    break;
                } else {
                    String[] err = {"Integer"};
                    error(err);
                }
                break;
            case 11:
                if (l1.getID() == Token.ID.FLOAT) {
                    match();
                    break;
                } else {
                    String[] err = {"Float"};
                    error(err);
                }
                break;
            case 12:
                if (l1.getID() == Token.ID.STRING) {
                    match();
                    break;
                } else {
                    String[] err = {"String"};
                    error(err);
                }
                break;
            case 13:
                if (l1.getID() == Token.ID.BOOLEAN) {
                    match();
                    break;
                } else {
                    String[] err = {"Boolean"};
                    error(err);
                }
                break;
            default:
                String err[] = {"Integer", "Float", "String", "Boolean"};
                error(err);
                break;
        }
    }

    private void ProcedureAndFunctionDeclarationPart() {
        stackTrace += "ProcedureAndFunctionDeclarationPart\n";
        switch (getRule(NonTerminal.ProcedureAndFunctionDeclarationPart)) {
            case 14:
                ProcedureDeclaration();
                ProcedureAndFunctionDeclarationPart();
                break;
            case 15:
                FunctionDeclaration();
                ProcedureAndFunctionDeclarationPart();
                break;
            case 16:
                break;
            default:
                String err[] = {"procedure", "function", "begin"};
                error(err);
                break;
        }
    }

    private void ProcedureDeclaration() {
        stackTrace += "ProcedureDeclaration\n";
        switch (getRule(NonTerminal.ProcedureDeclaration)) {
            case 17:
                ProcedureHeading();
                if (l1.getID() == Token.ID.SCOLON) {
                    match();
                } else {
                    String[] err = {";"};
                    error(err);
                }
                Block();
                if (l1.getID() == Token.ID.SCOLON) {
                    match();
                } else {
                    String[] err = {";"};
                    error(err);
                }
                break;
            default:
                String[] err = {";"};
                error(err);
                break;
        }
    }

    private void FunctionDeclaration() {
        stackTrace += "FunctionDeclaration\n";
        switch (getRule(NonTerminal.FunctionDeclaration)) {
            case 18:
                FunctionHeading();
                if (l1.getID() == Token.ID.SCOLON) {
                    match();
                } else {
                    String[] err = {";"};
                    error(err);
                }
                Block();
                if (l1.getID() == Token.ID.SCOLON) {
                    match();
                } else {
                    String[] err = {";"};
                    error(err);
                }
                break;
            default:
                String[] err = {";"};
                error(err);
                break;
        }
    }

    private void ProcedureHeading() {
        stackTrace += "ProcedureHeading\n";
        switch (getRule(NonTerminal.ProcedureHeading)) {
            case 19:
                if (l1.getID() == Token.ID.PROCEDURE) {
                    match();
                } else {
                    String[] err = {"procedure"};
                    error(err);
                }
                ProcedureIdentifier();
                OptionalFormalParameterList();
                break;
            default:
                String[] err = {"procedure"};
                error(err);
                break;
        }
    }

    private void FunctionHeading() {
        stackTrace += "FunctionHeading\n";
        switch (getRule(NonTerminal.FunctionHeading)) {
            case 20:
                if (l1.getID() == Token.ID.FUNCTION) {
                    match();
                } else {
                    String[] err = {"function"};
                    error(err);
                }
                FunctionIdentifier();
                OptionalFormalParameterList();
                break;
            default:
                String[] err = {"function"};
                error(err);
                break;
        }
    }

    private void OptionalFormalParameterList() {
        stackTrace += "OptionalFormalParameterList\n";
        switch (getRule(NonTerminal.OptionalFormalParameterList)) {
            case 21:
                if (l1.getID() == Token.ID.LPAREN) {
                    match();
                } else {
                    String[] err = {"("};
                    error(err);
                }
                FormalParameterSection();
                FormalParameterSectionTail();
                break;
            case 22:
                break;
            default:
                String[] err = {"(", ":"};
                error(err);
                break;
        }
    }

    private void FormalParameterSectionTail() {
        stackTrace += "FormalParameterSectionTail\n";
        switch (getRule(NonTerminal.FormalParameterSectionTail)) {
            case 23:
                if (l1.getID() == Token.ID.SCOLON) {
                    match();
                } else {
                    String[] err = {";"};
                    error(err);
                }
                FormalParameterSection();
                FormalParameterSectionTail();
                break;
            case 24:
                break;
            default:
                String[] err = {";"};
                error(err);
                break;
        }
    }

    private void FormalParameterSection() {
        stackTrace += "FormalParameterSection\n";
        switch (getRule(NonTerminal.FormalParameterSection)) {
            case 25:
                ValueParameterSection();
                break;
            case 26:
                VariableParameterSection();
                break;
            default:
                String[] err = {"identifier", "var"};
                error(err);
                break;
        }
    }

    private void ValueParameterSection() {
        stackTrace += "ValueParameterSection\n";
        switch (getRule(NonTerminal.ValueParameterSection)) {
            case 27:
                IdentifierList();
                if (l1.getID() == Token.ID.COLON) {
                    match();
                } else {
                    String[] err = {":"};
                    error(err);
                }
                Type();
                break;
            default:
                String[] err = {":"};
                error(err);
                break;
        }
    }

    private void VariableParameterSection() {
        stackTrace += "VariableParameterSection\n";
        switch (getRule(NonTerminal.VariableParameterSection)) {
            case 28:
                if (l1.getID() == Token.ID.VAR) {
                    match();
                } else {
                    String[] err = {"var"};
                    error(err);
                }
                IdentifierList();
                if (l1.getID() == Token.ID.COLON) {
                    match();
                } else {
                    String[] err = {":"};
                    error(err);
                }
                Type();
                break;
            default:
                String[] err = {"var"};
                error(err);
                break;
        }
    }

    private void StatementPart() {
        stackTrace += "StatementPart\n";
        switch (getRule(NonTerminal.StatementPart)) {
            case 29:
                CompoundStatement();
                break;
            default:
                String[] err = {"begin"};
                error(err);
                break;
        }
    }

    private void CompoundStatement() {
        stackTrace += "CompoundStatement\n";
        switch (getRule(NonTerminal.CompoundStatement)) {
            case 30:
                if (l1.getID() == Token.ID.BEGIN) {
                    match();
                } else {
                    String[] err = {"begin"};
                    error(err);
                }
                StatementSequence();
                if (l1.getID() == Token.ID.END) {
                    match();
                } else {
                    String[] err = {"end"};
                    error(err);
                }
                break;
            default:
                String[] err = {"begin"};
                error(err);
                break;
        }
    }

    private void StatementSequence() {
        stackTrace += "StatementSequence\n";
        switch (getRule(NonTerminal.StatementSequence)) {
            case 31:
                Statement();
                StatementTail();
                break;
            default:
                String[] err = {"begin", "read", "write", "writeln", "if", "repeat", "for", "procedure", "identifier"};
                error(err);
                break;
        }
    }

    private void StatementTail() {
        stackTrace += "StatementTail\n";
        switch (getRule(NonTerminal.StatementTail)) {
            case 32:
                if (l1.getID() == Token.ID.SCOLON) {
                    match();
                } else {
                    String[] err = {";"};
                    error(err);
                }
                Statement();
                StatementTail();
                break;
            case 33:
                break;
            default:
                String[] err = {";", "end"};
                error(err);
                break;
        }
    }

    private void Statement() {
        stackTrace += "Statement\n";
        switch (getRule(NonTerminal.Statement)) {
            case 34:
                EmptyStatement();
                break;
            case 35:
                CompoundStatement();
                break;
            case 36:
                ReadStatement();
                break;
            case 37:
                WriteStatement();
                break;
            case 38:
                AssignmentStatement();
                break;
            case 39:
                IfStatement();
                break;
            case 40:
                WhileStatement();
                break;
            case 41:
                RepeatStatement();
                break;
            case 42:
                ForStatement();
                break;
            case 43:
                ProcedureStatement();
                break;
            default:
                String[] err = {"begin", "read", "write", "writeln", "if", "repeat", "for", "procedure", "identifier"};
                error(err);
                break;
        }
    }

    //**************************************************************************
    //rules 40-47 
    // Jacob Barthelmeh
    private void EmptyStatement() {
        stackTrace += "EmptyStatement\n";
        switch (getRule(NonTerminal.EmptyStatement)) {
            case 44:
                break;
            default:
                String[] err = {""};
                error(err);
                break;
        }
    }

    private void ReadStatement() {
        stackTrace += "ReadStatement\n";
        switch (getRule(NonTerminal.ReadStatement)) {
            case 45://rule 45
                if (l1.getID() == Token.ID.READ) { //rule 45
                    match();
                    if (l1.getID() == Token.ID.LPAREN) {
                        match();
                        ReadParameter();
                        ReadParameterTail();
                        if (l1.getID() == Token.ID.RPAREN) {
                            match();
                        } else {
                            String[] err = {")"};
                            error(err);
                        }
                    } else {
                        String[] err = {"("};
                        error(err);
                    }
                } else {
                    String[] err = {"read"};
                    error(err);
                }
                break;
            default:
                String exp[] = {""};
                error(exp);
                break;
        }

    }

    private void ReadParameterTail() {
        stackTrace += "ReadParameterTail\n";
        switch (getRule(NonTerminal.ReadParameterTail)) {
            case 46: //rule 46
                match();
                ReadParameter();
                ReadParameterTail();
                break;
            case 47://handle case of e rule 47
                break;
            default:
                String[] err = {"comma"};
                error(err);
                break;
        }
    }

    private void ReadParameter() {
        stackTrace += "ReadParameter\n";
        switch (getRule(NonTerminal.ReadParameter)) {
            case 48://rule 48
                VariableIdentifier();
                break;
            default:
                String exp[] = {""};
                error(exp);
                break;
        }
    }

    private void WriteStatement() {
        stackTrace += "WriteStatment\n";
        switch (getRule(NonTerminal.WriteStatement)) {
            case 49: //rule 49
                match();
                if (l1.getID() == Token.ID.LPAREN) {
                    match();
                    WriteParameter();
                    WriteParameterTail();
                    if (l1.getID() == Token.ID.RPAREN) {
                        match();
                    } else {
                        String[] err = {")"};
                        error(err);
                    }
                } else {
                    String[] err = {"("};
                    error(err);
                }
                break;

            case 50: //rule 50
                match();
                if (l1.getID() == Token.ID.LPAREN) {
                    match();
                    WriteParameter();
                    WriteParameterTail();
                    if (l1.getID() == Token.ID.RPAREN) {
                        match();
                    } else {
                        String[] err = {")"};
                        error(err);
                    }
                } else {
                    String[] err = {"("};
                    error(err);
                }
                break;

            default:
                String[] err = {"write", "writeln"};
                error(err);
        }
    }

    private void WriteParameterTail() {
        stackTrace += "WriteParameterTail\n";
        switch (getRule(NonTerminal.WriteParameterTail)) {
            case 51: //rule 51
                match();
                WriteParameter();
                WriteParameterTail();
                break;
            case 52://case of e rule 52
                break;
            default:
                String[] err = {","};
                error(err);
                break;
        }
    }

    private void WriteParameter() {
        stackTrace += "WriteParameter\n";
        switch (getRule(NonTerminal.WriteParameter)) {
            case 53://rule 53
                OrdinalExpression();
                break;
            default:
                String exp[] = {""};
                error(exp);
                break;
        }
    }

    private void AssignmentStatement() {
        stackTrace += "AssignmentStatement\n";
        switch (getRule(NonTerminal.AssignmentStatement)) {
            case 54: //rule 54
                VariableIdentifier();
                if (l1.getID() == Token.ID.ASSIGN) {
                    match();
                    Expression();
                } else {
                    String[] err = {"assign"};
                    error(err);
                }
                break;
            case 55://rule 55
                FunctionIdentifier(); //rule 55
                if (l1.getID() == Token.ID.ASSIGN) {
                    match();
                    Expression();
                } else {
                    String[] err = {"assign"};
                    error(err);
                }
                break;
            default:
                String[] err = {"else", "e"};
                error(err);
                break;
        }
    }

    private void IfStatement() {
        stackTrace += "IfStatement\n";
        switch (getRule(NonTerminal.IfStatement)) {
            case 56: //rule 56
                if (l1.getID() == Token.ID.IF) {
                    match();
                    BooleanExpression();
                    if (l1.getID() == Token.ID.THEN) {
                        match();
                        Statement();
                        OptionalElsePart();
                    } else {
                        String[] err = {"then"};
                        error(err);
                    }
                } else {
                    String[] err = {"if"};
                    error(err);
                }
                break;
            default:
                String exp[] = {""};
                error(exp);
                break;
        }
    }

    private void OptionalElsePart() {
        stackTrace += "OptionalElsePart\n";
        switch (getRule(NonTerminal.OptionalElsePart)) {
            case 57: //rule 57
                match();
                Statement();
                break;
            case 58://case of e rule 58
                break;
            default:
                String[] err = {"else", "e"};
                error(err);
                break;

        }
    }

    private void RepeatStatement() {
        stackTrace += "RepeatStatement\n";
        switch (getRule(NonTerminal.RepeatStatement)) {
            case 59://rule 59
                if (l1.getID() == Token.ID.REPEAT) {
                    match();
                    StatementSequence();
                    if (l1.getID() == Token.ID.UNTIL) {
                        match();
                        BooleanExpression();
                    } else {
                        String[] err = {"until"};
                        error(err);
                    }
                } else {
                    String[] err = {"repeat"};
                    error(err);
                }
                break;
            default:
                String exp[] = {""};
                error(exp);
                break;
        }
    }

    private void WhileStatement() {
        stackTrace += "WhileStatment\n";
        switch (getRule(NonTerminal.WhileStatement)) {
            case 60://rule 60
                if (l1.getID() == Token.ID.WHILE) {
                    match();
                    BooleanExpression();
                    if (l1.getID() == Token.ID.DO) {
                        match();
                        Statement();
                    } else {
                        String[] err = {"do"};
                        error(err);
                    }
                } else {
                    String[] err = {"while"};
                    error(err);
                }
                break;
            default:
                String exp[] = {"while"};
                error(exp);
                break;
        }
    }

    private void ForStatement() {
        stackTrace += "ForStatement\n";
        switch (getRule(NonTerminal.ForStatement)) {
            case 61://rule 61
                if (l1.getID() == Token.ID.FOR) {
                    match();
                    ControlVariable();
                    if (l1.getID() == Token.ID.ASSIGN) {
                        match();
                        InitialValue();
                        StepValue();
                        FinalValue();
                        if (l1.getID() == Token.ID.DO) {
                            match();
                            Statement();
                        } else {
                            String[] err = {"do"};
                            error(err);
                        }
                    } else {
                        String[] err = {":="};
                        error(err);
                    }
                } else {
                    String[] err = {"for"};
                    error(err);
                }
                break;
            default:
                String exp[] = {"for"};
                error(exp);
                break;
        }
    }

    private void ControlVariable() {
        stackTrace += "ControlVariable\n";
        switch (getRule(NonTerminal.ControlVariable)) {
            case 62://rule 62
                VariableIdentifier();
                break;
            default:
                String exp[] = {""};
                error(exp);
                break;
        }
    }

    private void InitialValue() {
        stackTrace += "InitialValue\n";
        switch (getRule(NonTerminal.InitialValue)) {
            case 63://rule 63
                OrdinalExpression();
                break;
            default:
                String exp[] = {""};
                error(exp);
                break;
        }
    }

    private void StepValue() {
        stackTrace += "StepValue\n";
        switch (getRule(NonTerminal.StepValue)) {
            case 64: //rule 64
                match();
                break;
            case 65: //rule 65
                match();
                break;
            default:
                String[] err = {"to", "downto"};
                error(err);
                break;
        }
    }

    private void FinalValue() {
        stackTrace += "FinalValue\n";
        switch (getRule(NonTerminal.FinalValue)) {
            case 66://rule 66
                OrdinalExpression();
                break;
            default:
                String exp[] = {""};
                error(exp);
                break;
        }
    }

    private void ProcedureStatement() {
        stackTrace += "ProcedureStatment\n";
        switch (getRule(NonTerminal.ProcedureStatement)) {
            case 67://rule 67
                ProcedureIdentifier();
                OptionalActualParameterList();
                break;
            default:
                String exp[] = {""};
                error(exp);
                break;
        }
    }

    private void OptionalActualParameterList() {
        stackTrace += "OptionalActualParameterList\n";
        switch (getRule(NonTerminal.OptionalActualParameterList)) {
            case 68: //rule 68
                match();
                ActualParameter();
                ActualParameterTail();
                if (l1.getID() == Token.ID.RPAREN) {
                    match();
                } else {
                    String[] err = {")"};
                    error(err);
                }
                break;
            case 69://rule 69 switch on e
                break;
            default:
                String[] err = {"("};
                error(err);
                break;
        }
    }

    private void ActualParameterTail() {
        stackTrace += "ActualParameterTail\n";
        switch (getRule(NonTerminal.ActualParameterTail)) {
            case 70: //rule 70
                match();
                ActualParameter();
                ActualParameterTail();
                break;
            case 71: //e
                break;
            default:
                String[] err = {","};
                error(err);
                break;
        }
    }

    private void ActualParameter() {
        stackTrace += "ActualParameter\n";
        switch (getRule(NonTerminal.ActualParameter)) {
            case 72:
                OrdinalExpression();//rule 72
                break;
            default:
                String exp[] = {""};
                error(exp);
                break;
        }
    }

    private void Expression() {
        stackTrace += "Expression\n";
        switch (getRule(NonTerminal.Expression)) {
            case 73:
                SimpleExpression(); //rule 73
                OptionalRelationalPart(); //rule 73
                break;
            default:
                String exp[] = {""};
                error(exp);
                break;
        }
    }

    private void OptionalRelationalPart() {
        stackTrace += "OptionalRelationalPart\n";
        switch (getRule(NonTerminal.OptionalRelationalPart)) {
            case 74:
                RelationalOperator(); //rule 74
                SimpleExpression(); //rule 74
                break;
            case 75://rule 75
                break;
            default:
                String exp[] = {""};
                error(exp);
                break;
        }
    }

    private void RelationalOperator() {
        stackTrace += "RelationalOperator\n";
        switch (getRule(NonTerminal.RelationalOperator)) {
            case 76: //rule 76
                match();
                break;
            case 77: //rule 77
                match();
                break;
            case 78: //rule 78
                match();
                break;
            case 79: //rule 79
                match();
                break;
            case 80: //rule 80
                match();
                break;
            case 81: //rule 81
                match();
                break;
            default:
                String exp[] = {"EQUAL", "LTHAN", "GTHAN", "LEQUAL", "GEQUAL", "NEQUAL"};
                error(exp);
                break;
        }
    }

    //**************************************************************************
    //stubs for rules 78 - 150
    private void SimpleExpression() {
        stackTrace += "SimpleExpression\n";
        switch (getRule(NonTerminal.SimpleExpression)) {
            case 82:
                OptionalSign(); // Rule 82
                Term();
                TermTail();
                break;
            default:
                String exp[] = {""};
                error(exp);
                break;
        }
    }

    private void TermTail() {
        stackTrace += "TermTail\n";
        switch (getRule(NonTerminal.TermTail)) {
            case 83:
                AddingOperator(); // Rule 83
                Term();           // Rule 83
                TermTail();       // Rule 83
                break;
            case 84:
                break;
            default:
                String exp[] = {""};
                error(exp);
                break;
        }
    }

    private void OptionalSign() {
        stackTrace += "OptionalSign\n";
        switch (getRule(NonTerminal.OptionalSign)) {
            case 85: // +
                match();
                break;
            case 86: // -
                match();
                break;
            case 87: // e
                break;
            default:
                String exp[] = {"+", "-"};
                error(exp);
                break;
        }
    }

    private void AddingOperator() {
        stackTrace += "AddingOperator\n";
        switch (getRule(NonTerminal.AddingOperator)) {
            case 88: // +
                match();
                break;
            case 89: // -
                match();
                break;
            case 90: // or
                match();
                break;
            default:
                String exp[] = {"+", "-", "or"};
                error(exp);
                break;
        }
    }

    private void Term() {
        stackTrace += "Term\n";
        switch (getRule(NonTerminal.Term)) {
            case 91:
                Factor();      // RULE 91
                FactorTail();  // RULE 91
                break;
            default:
                String exp[] = {""};
                error(exp);
                break;
        }
    }

    private void FactorTail() {
        stackTrace += "FactorTail\n";
        switch (getRule(NonTerminal.FactorTail)) {
            case 92:
                MultiplyingOperator();  // RULE 92
                Factor();               // RULE 92
                FactorTail();           // RULE 92
                break;
            case 93:
                break;
            default:
                String exp[] = {""};
                error(exp);
                break;
        }
    }

    private void MultiplyingOperator() {
        stackTrace += "MultiplyingOperator\n";
        switch (getRule(NonTerminal.MultiplyingOperator)) {
            case 94:         //  * RULE 94
                match();
                break;
            case 95:  // / RULE 95
                match();
                break;
            case 96:           // / RULE 96
                match();
                break;
            case 97:           // % RULE 97
                match();
                break;
            case 98:           // and RULE 98
                match();
                break;
            default:
                String exp[] = {"*", "/", "div", "%", "amd"};
                error(exp);
                break;
        }
    }

    private void Factor() {
        stackTrace += "Factor\n";
        switch (getRule(NonTerminal.Factor)) {
            case 99:
                match();      //int RULE 99
                break;
            case 100:
                match();      // RULE 100
                break;
            case 101:
                match();      // RULE 101
                break;
            case 102:          // RULE 102
                match();
                break;
            case 103:         // RULE 103
                match();
                break;
            case 104:           // RULE 104
                match();
                Factor();
                break;
            case 105:        // RULE 105
                match();
                Expression();
                switch (l1.getID()) {
                    case RPAREN:
                        match();
                        break;
                    default:
                        String exp[] = {")"};
                        error(exp);
                }
                break;
            case 106:   // RULE 106
                FunctionIdentifier();
                OptionalActualParameterList();
                break;
            case 116:  // RULE 116
                match();
                break;
            default:
                String[] exp = {"INTEGER", "FLOAT", "STRING_LIT", "TRUE", "FALSE", "NOT", "LPAREN EXPRESSION RPAREN", "FunctionIdentifier OptionalActualParameterList"};
                error(exp);
                break;
        }
    }

    private void ProgramIdentifier() {
        stackTrace += "ProgramIdentifier\n";
        switch (getRule(NonTerminal.ProgramIdentifier)) {
            case 107:    // RULE 107
                match();
                break;
            default:
                String[] exp = {"IDENTIFIER"};
                error(exp);
                break;
        }
    }

    private void VariableIdentifier() {
        stackTrace += "VariableIdentifier\n";
        switch (getRule(NonTerminal.VariableIdentifier)) {
            case 108:    // RULE 108
                match();
                break;
            default:
                String[] exp = {"IDENTIFIER"};
                error(exp);
                break;
        }
    }

    private void ProcedureIdentifier() {
        stackTrace += "ProcedureIdentifier\n";
        switch (getRule(NonTerminal.ProcedureIdentifier)) {
            case 109:     // RULE 109
                match();
                break;
            default:
                String[] exp = {"IDENTIFIER"};
                error(exp);
                break;
        }
    }

    private void FunctionIdentifier() {
        stackTrace += "FunctionIdentifier\n";
        switch (getRule(NonTerminal.FunctionIdentifier)) {
            case 110:    // RULE 110
                match();
                break;
            default:
                String[] exp = {"IDENTIFIER"};
                error(exp);
                break;
        }
    }

    private void BooleanExpression() {
        stackTrace += "BooleanExpression\n";
        switch (getRule(NonTerminal.BooleanExpression)) {
            case 111:    // RULE 111
                Expression();           // RULE 111
                break;
            default:
                String[] exp = {""};
                error(exp);
                break;
        }
    }

    private void OrdinalExpression() {
        stackTrace += "OrdinalExpression\n";
        switch (getRule(NonTerminal.OrdinalExpression)) {
            case 112:    // RULE 112
                Expression();
                break;
            default:
                String[] exp = {"expression"};
                error(exp);
                break;
        }
    }

    private void IdentifierList() {
        stackTrace += "IdentifierList\n";
        switch (getRule(NonTerminal.IdentifierList)) {
            case 113:    // RULE 113
                match();
                IdentifierTail();
                break;
            default:
                String[] exp = {"IDENTIFIER"};
                error(exp);
                break;
        }
    }

    private void IdentifierTail() {
        stackTrace += "IdentifierTail\n";
        switch (getRule(NonTerminal.IdentifierTail)) {
            case 114:
                match(); // RULE 114
                if (l1.getID() == Token.ID.IDENTIFIER) {
                    match();
                } else {
                    String[] err = {"IDENTIFIER"};
                    error(err);
                }
                IdentifierTail();
                break;
            case 115: // for rule 115
                break;
            default:
                String[] err = {"COMMA IDENTIFIER IdentifierTail"};
                error(err);
                break;
        }
    }
}
