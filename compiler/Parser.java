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
            System.err.println("Invalid input to parser match function");
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
            System.err.println("Improper input to parser error function");
        }
        System.err.println("Error found " + err.getContents() + " "
                + err.getID() + " at line " + err.getLine() + " col " + err.getCol());
        System.err.print("Was expecting ");
        for (int i = 0; i < expected.length; i++) {
            System.err.print(", " + expected[i]);
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

    }

    private void ReadParameter() {

    }

    private void WriteStatment() {

    }

    private void WriteParameterTail() {

    }

    private void WriteParameter() {

    }

    private void AssignmentStatement() {

    }

    private void IfStatement() {

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
        switch(l1.getID()) {
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
        switch(l1.getID()) {
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
                switch(l1.getID()) {
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
        switch(l1.getID()) {
            case IDENTIFIER:    // RULE 107
                match(l1);
                break;
            default:
                String[] exp = {"IDENTIFIER"};
                error(l1, exp);
        }
    }
        
    private void VariableIdentifier() {
        switch(l1.getID()) {
            case IDENTIFIER:    // RULE 108
                match(l1);
                break;
            default:
                String[] exp = {"IDENTIFIER"};
                error(l1, exp);
        }
    }
    
    private void ProcedureIdentifier() {
        switch(l1.getID()) {
            case IDENTIFIER:     // RULE 109
                match(l1);
                break;
            default:
                String[] exp = {"IDENTIFIER"};
                error(l1, exp);
        }
    }
        
    private void FunctionIdentifier() {
        switch(l1.getID()) {
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
        switch(l1.getID()) {
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
        switch(l1.getID()) {
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
