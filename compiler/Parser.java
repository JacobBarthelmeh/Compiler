/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import scanner.Scanner;

/**
 *
 * @author sweetness
 */
public class Parser {

    private volatile Token l1; // look ahead token
    private volatile Scanner scanner;

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
        System.err.println("Error found " + err.getContents() + " "
                + err.getID() + " at line " + err.getLine() + " col " + err.getCol());
        System.err.print("Was expecting ");
        for (int i = 0; i < expected.length; i++) {
            System.err.print(", " + expected[i]);
        }
    }
    //*************************************************************************
    //  Stubs for rules 1-39
    private void SystemGoal() {
        Program();  //  rule 2
        if (l1.getID() == Token.ID.EOF) {
            match(l1);
        }
        else {
            String[] err = {"end of file"};
            error(l1, err);
        }
    }
    private void Program() {
        ProgramHeading();
        if (l1.getID() == Token.ID.SCOLON) {
            match(l1);
        }
        else {
            String[] err = {";"};
            error(l1, err);
        }
        Block();
        if (l1.getID() == Token.ID.PERIOD) {
            match(l1);
        }
        else {
            String[] err = {"."};
            error(l1, err);
        }
    }
    private void ProgramHeading() {
        if (l1.getID() == Token.ID.PROGRAM) {
            match(l1);
        }
        else {
            String[] err = {"program"};
            error(l1, err);
        }
        ProgramIdentifier();
    }
    private void Block(){
        VariableDeclarationPart();
        ProcedureAndFunctionDeclarationPart();
        StatementPart();
    }
    private void VariableDeclarationPart() {
        if (l1.getID() == Token.ID.VAR) {
            match(l1);
        }
        else {
            String[] err = {"var"};
            error(l1, err);
        }
        VariableDeclaration();
        if (l1.getID() == Token.ID.SCOLON) {
            match(l1);
        }
        else {
            String[] err = {";"};
            error(l1, err);
        }
        VariableDeclarationTail();
    }
    private void VariableDeclarationTail() {
        VariableDeclaration();
        if (l1.getID() == Token.ID.SCOLON) {
            match(l1);
        }
        else {
            String[] err = {";"};
            error(l1, err);
        }
        VariableDeclarationTail();
    }
    private void VariableDeclaration(){
        IdentifierList();
        if (l1.getID() == Token.ID.COLON) {
            match(l1);
        }
        else {
            String[] err = {":"};
            error(l1, err);
        }
        Type();
    }
    private void Type() {
        switch(l1.getID()) {
            case INTEGER:
            case FLOAT:
            case BOOLEAN:
            case STRING:
                match(l1);
            default:
                String err[] = {"Integer", "Float", "String", "Boolean"};
                error(l1, err);
        }
    }
    private void ProcedureAndFunctionDeclarationPart() {
        //  Get lookup
        int branch = 0;
        if (branch == 0) {
            ProcedureDeclaration();
            ProcedureAndFunctionDeclarationPart();
        }
        else {
            FunctionDeclaration();
            ProcedureAndFunctionDeclarationPart();
        }
    }
    private void ProcedureDeclaration() {
        ProcedureHeading();
        if (l1.getID() == Token.ID.SCOLON) {
            match(l1);
        }
        else {
            String[] err = {";"};
            error(l1, err);
        }
        Block();
        if (l1.getID() == Token.ID.SCOLON) {
            match(l1);
        }
        else {
            String[] err = {";"};
            error(l1, err);
        }
    }
    private void FunctionDeclaration() {
        FunctionHeading();
        if (l1.getID() == Token.ID.SCOLON) {
            match(l1);
        }
        else {
            String[] err = {";"};
            error(l1, err);
        }
        Block();
        if (l1.getID() == Token.ID.SCOLON) {
            match(l1);
        }
        else {
            String[] err = {";"};
            error(l1, err);
        }
    }
    private void ProcedureHeading() {        
        if (l1.getID() == Token.ID.PROCEDURE) {
            match(l1);
        }
        else {
            String[] err = {"procedure"};
            error(l1, err);
        }
        procedureIdentifier();
        OptionalFormalParameterList();
    }
    private void FunctionHeading() {
        if (l1.getID() == Token.ID.FUNCTION) {
            match(l1);
        }
        else {
            String[] err = {"function"};
            error(l1, err);
        }
        functionIdentifier();
        OptionalFormalParameterList();
    }
    private void OptionalFormalParameterList() {
        if (l1.getID() == Token.ID.LPAREN) {
            match(l1);
        }
        else {
            String[] err = {"("};
            error(l1, err);
        }
        FormalParameterSection();
        FormalParameterSectionTail();
    }
    private void FormalParameterSectionTail() {
        if (l1.getID() == Token.ID.SCOLON) {
            match(l1);
        }
        else {
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
        }
        else {
            VariableParameterSection();
        }
    }
    private void ValueParameterSection() {
        IdentifierList();
        if (l1.getID() == Token.ID.COLON) {
            match(l1);
        }
        else {
            String[] err = {":"};
            error(l1, err);
        }
        Type();
    }
    private void VariableParameterSection() {
        if (l1.getID() == Token.ID.VAR) {
            match(l1);
        }
        else {
            String[] err = {"var"};
            error(l1, err);
        }
        IdentifierList();
        if (l1.getID() == Token.ID.COLON) {
            match(l1);
        }
        else {
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
        }
        else {
            String[] err = {"begin"};
            error(l1, err);
        }
        StatementSequence();
        if (l1.getID() == Token.ID.END) {
            match(l1);
        }
        else {
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
                AssignStatement();
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
    private void EmptyStatement() {
    }

    private void ReadStatement() {
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
    }

    private void ReadParameterTail() {
        switch (l1.getID()) {
            case COMMA: //rule 46
                match(l1);
                ReadParameter();
                ReadParameterTail();
                break;

            //@TODO handle case of e rule 47
            default:
                String[] err = {"comma", "e"};
                error(l1, err);
        }
    }

    private void ReadParameter() {
        VariableIdentifier(); //rule 48
    }

    private void WriteStatment() {
        switch (l1.getID()) {
            case WRITE: //rule 49
                match(l1);
                break;

            case WRITELN: //rule 50
                match(l1);
                break;

            default:
                String[] err = {"write", "writeln"};
                error(l1, err);
        }
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
    }

    private void WriteParameterTail() {
        switch (l1.getID()) {
            case COMMA: //rule 51
                match(l1);
                WriteParameter();
                WriteParameterTail();
                break;

            //@TODO cas of e rule 52
            default:
                String[] err = {",", "e"};
                error(l1, err);
        }
    }

    private void WriteParameter() {
        OrdinalExpression(); //rule 53
    }

    private void AssignmentStatement() {
        VariableIdentifier(); //rule 54
        FunctionIdentifier(); //rule 55
        if (l1.getID() == Token.ID.ASSIGN) {
            match(l1);
            Expression();
        } else {
            String[] err = {"assign"};
            error(l1, err);
        }
    }

    private void IfStatement() {
        //rule 56
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
    }

    private void OptionalElsePart() {
        switch (l1.getID()) {
            case ELSE: //rule 57
                match(l1);
                Statement();
                break;

            //@TODO case of e rule 58
            default:
                String[] err = {"else", "e"};
                error(l1, err);

        }
    }

    private void RepeatStatement() {
        //rule 59
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
    }

    private void WhileStatement() {
        //rule 60
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
    }

    private void ForStatement() {
        //rule 61
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
    }

    private void ControlVariable() {
        VariableIdentifier(); //rule 62
    }

    private void InitialValue() {
        OrdinalExpression(); //rule 63
    }

    private void StepValue() {
        switch (l1.getID()) {
            case TO: //rule 64
                match(l1);
                break;
            case DOWNTO: //rule 65
                match(l1);
                break;
            default:
                String[] err = {"to", "downto"};
                error(l1, err);
        }
    }

    private void FinalValue() {
        //rule 66
        OrdinalExpression();
    }

    private void ProcedureStatement() {
        //rule 67
        ProcedureIdentifier();
        OptionalActualParameterList();
    }

    private void OptionalActualParameterList() {
        switch (l1.getID()) {
            case LPAREN: //rule 68
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

            //rule 69 @TODO switch on e
            default:
                String[] err = {"("};
                error(l1, err);
        }
    }

    private void ActualParameterTail() {
        switch (l1.getID()) {
            case COMMA: //rule 70
                match(l1);
                ActualParameter();
                ActualParameterTail();
                break;

            // @TODO switch on e
            default:
                String[] err = {","};
                error(l1, err);
        }
    }

    private void ActualParameter() {
        OrdinalEpression();//rule 72
    }

    private void Expression() {
        SimpleExpression(); //rule 73
        OptionalRelationalPart(); //rule 73
    }

    private void OptionalRelationalPart() {
        RelationalOperator(); //rule 74
        SimpleExpression(); //rule 74
        switch (l1.getID()) {
            //rule 75
            //@TODO switch on e
        }
    }

    private void RelationalOperator() {
        switch (l1.getID()) {
            case EQUAL: //rule 76
                match(l1);
                break;
            case LTHAN: //rule 77
                match(l1);
                break;
            case GTHAN: //rule 78
                match(l1);
                break;
            case LEQUAL: //rule 79
                match(l1);
                break;
            case GEQUAL: //rule 80
                match(l1);
                break;
            case NEQUAL: //rule 81
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
                OptionalActualParamterList();
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
