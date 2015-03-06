/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import java.io.PrintWriter;
import scanner.Scanner;

/**
 *
 * @author team 4
 */
public class Parser {

    private volatile Token l1; // look ahead token
    private volatile Scanner scanner;
    private volatile PrintWriter rFile;

    private int Table[][] = {
        /*  SystemGoal*/{},
        /*  Program*/ {},
        /*  ProgramHeading*/ {},
        /*  Block*/ {},
        /*  VariableDeclarationPart*/ {},
        /*5 VariableDeclaration*/ {},
        /*  Type*/ {},
        /*  ProcedureAndFunctionDeclarationPart*/ {},
        /*  ProcedureDeclaration*/ {},
        /*  FunctionDeclaration*/ {},
        /*10ProcedureHeading*/ {},
        /*  FunctionHeading*/ {},
        /*  OptionalFormalParameterList*/ {},
        /*  FormalParameterSectionTail*/ {},
        /*  FormalParameterSection*/ {},
        /*15ValueParameterSection*/ {},
        /*  VariableParameterSection*/ {},
        /*  StatementPart*/ {},
        /*  CompoundStatement*/ {},
        /*  StatementTail*/ {},
        /*20CompoundStatement*/ {},
        /*  StatementSequence*/ {},
        /*  StatementTail*/ {},
        /*  Statement*/ {},
        /*  EmptyStatement*/ {},
        /*25ReadStatment*/ {},
        /*  ReadParameterTail*/ {},
        /*  ReadParameter*/ {},
        /*  WriteStatement*/ {},
        /*  WriteParameterTail*/ {},
        /*30WriteParameter*/ {},
        /*  AssignmentStatement*/ {},
        /*  IfStatement*/ {},
        /*  OptionalElsePart*/ {},
        /*  RepeatStatement*/ {},
        /*35WhileStatement*/ {},
        /*  ForStatement*/ {},
        /*  ControlVariable*/ {},
        /*  InitialValue*/ {},
        /*  StepValue*/ {},
        /*40FinalValue*/ {},
        /*  ProcedureStatement*/ {},
        /*  OptionalActualParameterList*/ {},
        /*  ActualParameterTail*/ {},
        /*  ActualParameter*/ {},
        /*45Expression*/ {},
        /*  OptionalRelationalPart*/ {},
        /*  RelationalOperator*/ {},
        /*  SimpleExpression*/ {},
        /*  TermTail*/ {},
        /*50OptionalSign*/ {},
        /*  AddingOperator*/ {},
        /*  Term*/ {},
        /*  FactorTail*/ {},
        /*  MultiplyingOperator*/ {},
        /*55Factor*/ {},
        /*  ProgramIdentifier*/ {},
        /*  VariableIdentifier*/ {},
        /*  ProcedureIdentifier*/ {},
        /*  FunctionIdentifier*/ {},
        /*60BooleanExpression*/ {},
        /*  OrdinalExpression*/ {},
        /*  IdentifierList*/ {},
        /*  IdentifierTail*/ {}
    };

    //get the tokens index in the look ahead table
    private int tokenIndex(Token.ID in) {
        switch (in) {
            case AND:
                return 0;
            case BEGIN:
                return 1;
            case BOOLEAN:
                return 2;
            case DIV:
                return 3;
            case DO:
                return 4;
            case DOWNTO:
                return 5;
            case ELSE:
                return 6;
            case END:
                return 7;
            case FALSE:
                return 8;
            case FIXED:
                return 9;
            case FLOAT:
                return 10;
            case FOR:
                return 11;
            case FUNCTION:
                return 12;
            case IF:
                return 13;
            case INTEGER:
                return 14;
            case MOD:
                return 15;
            case NOT:
                return 16;
            case OR:
                return 17;
            case PROCEDURE:
                return 18;
            case PROGRAM:
                return 19;
            case READ:
                return 20;
            case REPEAT:
                return 21;
            case STRING:
                return 22;
            case THEN:
                return 23;
            case TRUE:
                return 24;
            case TO:
                return 25;
            case TYPE:
                return 26;
            case UNTIL:
                return 27;
            case VAR:
                return 28;
            case WHILE:
                return 29;
            case WRITE:
                return 30;
            case WRITELN:
                return 31;
            case IDENTIFIER:
                return 32;
            case INTEGER_LIT:
                return 33;
            case FIXED_LIT:
                return 34;
            case FLOAT_LIT:
                return 35;
            case STRING_LIT:
                return 36;
            case ASSIGN:
                return 37;
            case COLON:
                return 38;
            case COMMA:
                return 39;
            case EQUAL:
                return 40;
            case FLOAT_DIVIDE:
                return 41;
            case GEQUAL:
                return 42;
            case GTHAN:
                return 43;
            case LEQUAL:
                return 44;
            case LTHAN:
                return 45;
            case LPAREN:
                return 46;
            case RPAREN:
                return 47;
            case MINUS:
                return 48;
            case NEQUAL:
                return 49;
            case PERIOD:
                return 50;
            case PLUS:
                return 51;
            case SCOLON:
                return 52;
            case TIMES:
                return 53;
            case EOF:
                return 54;
            case RUN_COMMENT:
                return 55;
            case RUN_STRING:
                return 56;
            case ERROR:
                return 57;
        }
        return -1;
    }

    /**
     * Set a file to parse
     *
     * @param in file to be parsed
     * @return 0 on success
     */
    public int setFile(String in) {
        try {
            scanner = new Scanner(in);
            l1 = scanner.nextToken();
            return 0;
        } catch (Exception e) {
            System.err.println("Error set file " + in + " in parser");
        }
        return 1;
    }

    private int ruleFile(int rule) {
        if (rFile == null) {
            try {
                rFile = new PrintWriter("rule_list.txt", "UFT-8");
            } catch (Exception e) {
                System.out.println("Unable to make file rule_list.csv");
                return 1;
            }
        }
        rFile.print(rule + " ");
        return 0;
    }

    /**
     * If match was found consume it and look ahead
     *
     * @param in the current token being looked at
     */
    private void match(Token in) {
        if (in == null) {
            System.err.println("Invalid input to parser match function.");
            System.exit(1);
        }
        l1 = scanner.nextToken();
    }

    /**
     * handle error print out
     *
     * @param err token that caused the error
     * @param expected array of expected tokens
     */
    private void error(Token err, String[] expected) {
        if (err == null || expected == null) {
            System.err.println("Improper input to parser error function.");
        }
        rFile.close();
        System.err.println("Error found " + err.getContents() + " "
                + err.getID() + " at line " + err.getLine() + " col " + err.getCol());
        System.err.print("Was expecting ");
        for (int i = 0; i < expected.length; i++) {
            System.err.print(", " + expected[i]);
        }
    }

    /**
     * Get the rule to execute
     *
     * @param rule The current rule
     * @return The rule to execute
     */
    private int getRule(int nonTerminal) {
        int rule = Table[nonTerminal][tokenIndex(l1.getID())];
        ruleFile(rule); //write the rule taken
        return rule;
    }

    //*************************************************************************
    //  Stubs for rules 1-39
    private void SystemGoal() {
        Program();  //  rule 2
        if (l1.getID() == Token.ID.EOF) {
            match(l1);
        } else {
            String[] err = {"end of file"};
            error(l1, err);
        }
    }

    private void Program() {
        ProgramHeading();
        if (l1.getID() == Token.ID.SCOLON) {
            match(l1);
        } else {
            String[] err = {";"};
            error(l1, err);
        }
        Block();
        if (l1.getID() == Token.ID.PERIOD) {
            match(l1);
        } else {
            String[] err = {"."};
            error(l1, err);
        }
    }

    private void ProgramHeading() {
        if (l1.getID() == Token.ID.PROGRAM) {
            match(l1);
        } else {
            String[] err = {"program"};
            error(l1, err);
        }
        ProgramIdentifier();
    }

    private void Block() {
        VariableDeclarationPart();
        ProcedureAndFunctionDeclarationPart();
        StatementPart();
    }

    private void VariableDeclarationPart() {
        switch (getRule(5)) {
            case 5:
                if (l1.getID() == Token.ID.VAR) {
                    match(l1);
                } else {
                    String[] err = {"var"};
                    error(l1, err);
                }
                VariableDeclaration();
                if (l1.getID() == Token.ID.SCOLON) {
                    match(l1);
                } else {
                    String[] err = {";"};
                    error(l1, err);
                }
                VariableDeclarationTail();
                break;
            case 6:
                break;
            case -1:
                //  No rule found!
                String[] err = {"var", "procedure", "function", "begin"};
                error(l1, err);
                break;
        }
    }

    private void VariableDeclarationTail() {
        switch (getRule(7)) {
            case 7:
                VariableDeclaration();
                if (l1.getID() == Token.ID.SCOLON) {
                    match(l1);
                } else {
                    String[] err = {";"};
                    error(l1, err);
                }
                VariableDeclarationTail();
            case 8:
                break;
            case -1:
                String[] err = {"Integer", "Float", "String", "Boolean"};
                error(l1, err);
                break;
        }
    }

    private void VariableDeclaration() {
        IdentifierList();
        if (l1.getID() == Token.ID.COLON) {
            match(l1);
        } else {
            String[] err = {":"};
            error(l1, err);
        }
        Type();
    }

    private void Type() {
        switch (getRule(10)) {
            case 10:
            case 11:
            case 12:
            case 13:
                match(l1);
                break;
            default:
                String err[] = {"Integer", "Float", "String", "Boolean"};
                error(l1, err);
                break;
        }
    }

    private void ProcedureAndFunctionDeclarationPart() {
        switch (getRule(14)) {
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
            case -1:
                String err[] = {"procedure", "function", "begin"};
                error(l1, err);
                break;
        }
    }

    private void ProcedureDeclaration() {
        ProcedureHeading();
        if (l1.getID() == Token.ID.SCOLON) {
            match(l1);
        } else {
            String[] err = {";"};
            error(l1, err);
        }
        Block();
        if (l1.getID() == Token.ID.SCOLON) {
            match(l1);
        } else {
            String[] err = {";"};
            error(l1, err);
        }
    }

    private void FunctionDeclaration() {
        FunctionHeading();
        if (l1.getID() == Token.ID.SCOLON) {
            match(l1);
        } else {
            String[] err = {";"};
            error(l1, err);
        }
        Block();
        if (l1.getID() == Token.ID.SCOLON) {
            match(l1);
        } else {
            String[] err = {";"};
            error(l1, err);
        }
    }

    private void ProcedureHeading() {
        if (l1.getID() == Token.ID.PROCEDURE) {
            match(l1);
        } else {
            String[] err = {"procedure"};
            error(l1, err);
        }
        ProcedureIdentifier();
        OptionalFormalParameterList();
    }

    private void FunctionHeading() {
        if (l1.getID() == Token.ID.FUNCTION) {
            match(l1);
        } else {
            String[] err = {"function"};
            error(l1, err);
        }
        FunctionIdentifier();
        OptionalFormalParameterList();
    }

    private void OptionalFormalParameterList() {
        switch (getRule(21)) {
            case 21:
                if (l1.getID() == Token.ID.LPAREN) {
                    match(l1);
                } else {
                    String[] err = {"("};
                    error(l1, err);
                }
                FormalParameterSection();
                FormalParameterSectionTail();
                break;
            case 22:
                break;
        }
    }
    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    

    private void FormalParameterSectionTail() {
        if (l1.getID() == Token.ID.SCOLON) {
            match(l1);
        } else {
            String[] err = {";"};
            error(l1, err);
        }
        FormalParameterSection();
        FormalParameterSectionTail();
    }

    private void FormalParameterSection() {
        int branch = 0;
        if (branch == 0) {
            ValueParameterSection();
        } else {
            VariableParameterSection();
        }
    }

    private void ValueParameterSection() {
        IdentifierList();
        if (l1.getID() == Token.ID.COLON) {
            match(l1);
        } else {
            String[] err = {":"};
            error(l1, err);
        }
        Type();
    }

    private void VariableParameterSection() {
        if (l1.getID() == Token.ID.VAR) {
            match(l1);
        } else {
            String[] err = {"var"};
            error(l1, err);
        }
        IdentifierList();
        if (l1.getID() == Token.ID.COLON) {
            match(l1);
        } else {
            String[] err = {":"};
            error(l1, err);
        }
        Type();
    }

    private void StatementPart() {
        CompoundStatement();
    }

    private void CompoundStatement() {
        if (l1.getID() == Token.ID.BEGIN) {
            match(l1);
        } else {
            String[] err = {"begin"};
            error(l1, err);
        }
        StatementSequence();
        if (l1.getID() == Token.ID.END) {
            match(l1);
        } else {
            String[] err = {"end"};
            error(l1, err);
        }
    }

    private void StatementSequence() {
        Statement();
        StatementTail();
    }

    private void StatementTail() {
    }

    private void Statement() {
        int branch = 0;
        switch (branch) {
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

        }
    }

    //**************************************************************************
    //stubs for rules 40-47 
    // Jacob Barthelmeh
    private void EmptyStatement() {
        switch (getRule(24)) {
            case 44:
                break;
            default:
                String[] err = {""};
                error(l1, err);
        }
    }

    private void ReadStatement() {
        switch (getRule(25)) {
            case 45://rule 45
                if (l1.getID() == Token.ID.READ) { //rule 45
                    match(l1);
                    if (l1.getID() == Token.ID.LPAREN) {
                        match(l1);
                        ReadParameter();
                        ReadParameterTail();
                        if (l1.getID() == Token.ID.RPAREN) {
                            match(l1);
                        } else {
                            String[] err = {")"};
                            error(l1, err);
                        }
                    } else {
                        String[] err = {"("};
                        error(l1, err);
                    }
                } else {
                    String[] err = {"read"};
                    error(l1, err);
                }
                break;
            default:
                String exp[] = {""};
                error(l1, exp);
        }

    }

    private void ReadParameterTail() {
        switch (getRule(26)) {
            case 46: //rule 46
                match(l1);
                ReadParameter();
                ReadParameterTail();
                break;
            case 47://handle case of e rule 47
                break;
            default:
                String[] err = {"comma"};
                error(l1, err);
        }
    }

    private void ReadParameter() {
        switch (getRule(27)) {
            case 48://rule 48
                VariableIdentifier();
                break;
            default:
                String exp[] = {""};
                error(l1, exp);
        }
    }

    private void WriteStatement() {
        switch (getRule(28)) {
            case 49: //rule 49
                match(l1);
                if (l1.getID() == Token.ID.LPAREN) {
                    match(l1);
                    WriteParameter();
                    WriteParameterTail();
                    if (l1.getID() == Token.ID.RPAREN) {
                        match(l1);
                    } else {
                        String[] err = {")"};
                        error(l1, err);
                    }
                } else {
                    String[] err = {"("};
                    error(l1, err);
                }
                break;

            case 59: //rule 50
                match(l1);
                if (l1.getID() == Token.ID.LPAREN) {
                    match(l1);
                    WriteParameter();
                    WriteParameterTail();
                    if (l1.getID() == Token.ID.RPAREN) {
                        match(l1);
                    } else {
                        String[] err = {")"};
                        error(l1, err);
                    }
                } else {
                    String[] err = {"("};
                    error(l1, err);
                }
                break;

            default:
                String[] err = {"write", "writeln"};
                error(l1, err);
        }
    }

    private void WriteParameterTail() {
        switch (getRule(29)) {
            case 51: //rule 51
                match(l1);
                WriteParameter();
                WriteParameterTail();
                break;
            case 52://case of e rule 52
                break;
            default:
                String[] err = {","};
                error(l1, err);
        }
    }

    private void WriteParameter() {
        switch (getRule(30)) {
            case 53://rule 53
                OrdinalExpression();
                break;
            default:
                String exp[] = {""};
                error(l1, exp);
        }
    }

    private void AssignmentStatement() {
        switch (getRule(31)) {
            case 54: //rule 54
                VariableIdentifier();
                if (l1.getID() == Token.ID.ASSIGN) {
                    match(l1);
                    Expression();
                } else {
                    String[] err = {"assign"};
                    error(l1, err);
                }
                break;
            case 55://rule 55
                FunctionIdentifier(); //rule 55
                if (l1.getID() == Token.ID.ASSIGN) {
                    match(l1);
                    Expression();
                } else {
                    String[] err = {"assign"};
                    error(l1, err);
                }
                break;
            default:
                String[] err = {"else", "e"};
                error(l1, err);

        }
    }

    private void IfStatement() {
        switch (getRule(32)) {
            case 56: //rule 56
                if (l1.getID() == Token.ID.IF) {
                    match(l1);
                    BooleanExpression();
                    if (l1.getID() == Token.ID.THEN) {
                        match(l1);
                        Statement();
                        OptionalElsePart();
                    } else {
                        String[] err = {"then"};
                        error(l1, err);
                    }
                } else {
                    String[] err = {"if"};
                    error(l1, err);
                }
                break;
            default:
                String exp[] = {""};
                error(l1, exp);
        }
    }

    private void OptionalElsePart() {
        switch (getRule(33)) {
            case 57: //rule 57
                match(l1);
                Statement();
                break;
            case 58://case of e rule 58
                break;
            default:
                String[] err = {"else", "e"};
                error(l1, err);

        }
    }

    private void RepeatStatement() {
        switch (getRule(34)) {
            case 59://rule 59
                if (l1.getID() == Token.ID.REPEAT) {
                    match(l1);
                    StatementSequence();
                    if (l1.getID() == Token.ID.UNTIL) {
                        match(l1);
                        BooleanExpression();
                    } else {
                        String[] err = {"until"};
                        error(l1, err);
                    }
                } else {
                    String[] err = {"repeat"};
                    error(l1, err);
                }
                break;
            default:
                String exp[] = {""};
                error(l1, exp);
        }
    }

    private void WhileStatement() {
        switch (getRule(35)) {
            case 60://rule 60
                if (l1.getID() == Token.ID.WHILE) {
                    match(l1);
                    BooleanExpression();
                    if (l1.getID() == Token.ID.DO) {
                        match(l1);
                        Statement();
                    } else {
                        String[] err = {"do"};
                        error(l1, err);
                    }
                } else {
                    String[] err = {"while"};
                    error(l1, err);
                }
                break;
            default:
                String exp[] = {"while"};
                error(l1, exp);
        }
    }

    private void ForStatement() {
        switch (getRule(36)) {
            case 61://rule 61
                if (l1.getID() == Token.ID.FOR) {
                    match(l1);
                    ControlVariable();
                    if (l1.getID() == Token.ID.ASSIGN) {
                        match(l1);
                        InitialValue();
                        StepValue();
                        FinalValue();
                        if (l1.getID() == Token.ID.DO) {
                            match(l1);
                            Statement();
                        } else {
                            String[] err = {"do"};
                            error(l1, err);
                        }
                    } else {
                        String[] err = {":="};
                        error(l1, err);
                    }
                } else {
                    String[] err = {"for"};
                    error(l1, err);
                }
                break;
            default:
                String exp[] = {"for"};
                error(l1, exp);
        }
    }

    private void ControlVariable() {
        switch (getRule(37)) {
            case 62://rule 62
                VariableIdentifier();
                break;
            default:
                String exp[] = {""};
                error(l1, exp);
        }
    }

    private void InitialValue() {
        switch (getRule(38)) {
            case 63://rule 63
                OrdinalExpression();
                break;
            default:
                String exp[] = {""};
                error(l1, exp);
        }
    }

    private void StepValue() {
        switch (getRule(39)) {
            case 64: //rule 64
                match(l1);
                break;
            case 65: //rule 65
                match(l1);
                break;
            default:
                String[] err = {"to", "downto"};
                error(l1, err);
        }
    }

    private void FinalValue() {
        switch (getRule(40)) {
            case 66://rule 66
                OrdinalExpression();
                break;
            default:
                String exp[] = {""};
                error(l1, exp);
        }
    }

    private void ProcedureStatement() {
        switch (getRule(41)) {
            case 67://rule 67
                ProcedureIdentifier();
                OptionalActualParameterList();
                break;
            default:
                String exp[] = {""};
                error(l1, exp);
        }
    }

    private void OptionalActualParameterList() {
        switch (getRule(42)) {
            case 68: //rule 68
                match(l1);
                ActualParameter();
                ActualParameterTail();
                if (l1.getID() == Token.ID.RPAREN) {
                    match(l1);
                } else {
                    String[] err = {")"};
                    error(l1, err);
                }
                break;
            case 69://rule 69 switch on e
                break;
            default:
                String[] err = {"("};
                error(l1, err);
        }
    }

    private void ActualParameterTail() {
        switch (getRule(43)) {
            case 70: //rule 70
                match(l1);
                ActualParameter();
                ActualParameterTail();
                break;
            case 71: //e
                break;
            default:
                String[] err = {","};
                error(l1, err);
        }
    }

    private void ActualParameter() {
        switch (getRule(44)) {
            case 72:
                OrdinalExpression();//rule 72
                break;
            default:
                String exp[] = {""};
                error(l1, exp);
        }
    }

    private void Expression() {
        switch (getRule(45)) {
            case 73:
                SimpleExpression(); //rule 73
                OptionalRelationalPart(); //rule 73
                break;
            default:
                String exp[] = {""};
                error(l1, exp);
        }
    }

    private void OptionalRelationalPart() {
        switch (getRule(46)) {
            case 74:
                RelationalOperator(); //rule 74
                SimpleExpression(); //rule 74
                break;
            case 75://rule 75
                break;
            default:
                String exp[] = {""};
                error(l1, exp);
        }
    }

    private void RelationalOperator() {
        switch (getRule(47)) {
            case 76: //rule 76
                match(l1);
                break;
            case 77: //rule 77
                match(l1);
                break;
            case 78: //rule 78
                match(l1);
                break;
            case 79: //rule 79
                match(l1);
                break;
            case 80: //rule 80
                match(l1);
                break;
            case 81: //rule 81
                match(l1);
                break;
            default:
                String exp[] = {"EQUAL", "LTHAN", "GTHAN", "LEQUAL", "GEQUAL", "NEQUAL"};
                error(l1, exp);
        }
    }

    //**************************************************************************
    //stubs for rules 78 - 150
    private void SimpleExpression() {
        OptionalSign(); // Rule 82
        Term();
        TermTail();
    }

    private void TermTail() {
        AddingOperator(); // Rule 83
        Term();           // Rule 83
        TermTail();       // Rule 83
        switch (l1.getID()) {
            // Rule 84
        }
    }

    private void OptionalSign() {
        switch (l1.getID()) {
            case PLUS: // Rule 85
                match(l1);
                break;
            case MINUS: // RULE 86
                match(l1);
                break;
            // Rule 87 @TODO switch on e
            default:
                String exp[] = {"PLUS", "MINUS"};
                error(l1, exp);
        }
    }

    private void AddingOperator() {
        switch (l1.getID()) {
            case PLUS: // Rule 88
                match(l1);
                break;
            case MINUS: // RULE 89
                match(l1);
                break;
            case OR: // RULE 90
                match(l1);
                break;
            default:
                String exp[] = {"PLUS", "MINUS", "OR"};
                error(l1, exp);
        }
    }

    private void Term() {
        Factor();      // RULE 91
        FactorTail();  // RULE 91
    }

    private void FactorTail() {
        MultiplyingOperator();  // RULE 92
        Factor();               // RULE 92
        FactorTail();           // RULE 92
        switch (l1.getID()) {
            // RULE 93
        }
    }

    private void MultiplyingOperator() {
        switch (l1.getID()) {
            case TIMES:         // RULE 94
                match(l1);
                break;
            case FLOAT_DIVIDE:  // RULE 95
                match(l1);
                break;
            case DIV:           // RULE 96
                match(l1);
                break;
            case MOD:           // RULE 97
                match(l1);
                break;
            case AND:           // RULE 98
                match(l1);
                break;
        }
    }

    private void Factor() {
        switch (l1.getID()) {
            case INTEGER:
                match(l1);      // RULE 99
                break;
            case FLOAT:
                match(l1);      // RULE 100
                break;
            case STRING_LIT:
                match(l1);      // RULE 101
                break;
            case TRUE:          // RULE 102
                match(l1);
                break;
            case FALSE:         // RULE 103
                match(l1);
                break;
            case NOT:           // RULE 104
                match(l1);
                Factor();
                break;
            case LPAREN:        // RULE 105
                match(l1);
                Expression();
                switch (l1.getID()) {
                    case RPAREN:
                        match(l1);
                        break;
                    default:
                        String exp[] = {")"};
                        error(l1, exp);
                }
            case IDENTIFIER:   // RULE 106
                FunctionIdentifier();
                OptionalActualParameterList();
                break;
            default:
                String[] exp = {"INTEGER", "FLOAT", "STRING_LIT", "TRUE", "FALSE", "NOT", "LPAREN EXPRESSION RPAREN", "FunctionIdentifier OptionalActualParameterList"};
                error(l1, exp);
        }
    }

    private void ProgramIdentifier() {
        switch (l1.getID()) {
            case IDENTIFIER:    // RULE 107
                match(l1);
                break;
            default:
                String[] exp = {"IDENTIFIER"};
                error(l1, exp);
        }
    }

    private void VariableIdentifier() {
        switch (l1.getID()) {
            case IDENTIFIER:    // RULE 108
                match(l1);
                break;
            default:
                String[] exp = {"IDENTIFIER"};
                error(l1, exp);
        }
    }

    private void ProcedureIdentifier() {
        switch (l1.getID()) {
            case IDENTIFIER:     // RULE 109
                match(l1);
                break;
            default:
                String[] exp = {"IDENTIFIER"};
                error(l1, exp);
        }
    }

    private void FunctionIdentifier() {
        switch (l1.getID()) {
            case IDENTIFIER:    // RULE 110
                match(l1);
                break;
            default:
                String[] exp = {"IDENTIFIER"};
                error(l1, exp);
        }
    }

    private void BooleanExpression() {
        Expression();           // RULE 111
    }

    private void OrdinalExpression() {
        Expression();           // RULE 112
    }

    private void IdentifierList() {
        switch (l1.getID()) {
            case IDENTIFIER:    // RULE 113
                match(l1);
                IdentifierTail();
                break;
            default:
                String[] exp = {"IDENTIFIER"};
                error(l1, exp);
        }
    }

    private void IdentifierTail() {
        switch (l1.getID()) {
            case COMMA:
                match(l1); // RULE 114
                if (l1.getID() == Token.ID.IDENTIFIER) {
                    match(l1);
                } else {
                    String[] err = {"IDENTIFIER"};
                    error(l1, err);
                }
                IdentifierTail();
                break;
            // @TODO Switch on e
            // for rule 115
            default:
                String[] err = {"COMMA IDENTIFIER IdentifierTail"};
        }
    }
}
