package parser;

import compiler.Compiler;
import compiler.Token;
import symboltable.Parameter;
import symboltable.SymbolTableHandler;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import scanner.Scanner;
import semanticanalyzer.SemanticAnalyzer;
import semanticanalyzer.SemanticRecord;
import symboltable.Symbol;
import util.*;

public class Parser {

    // ll(1) table rules
    // See Complete-LL(1)-Table-2015-03-10.xlsx for information
    // Nonterminal 1
    // <SystemGoal> --> <Program> EOF RULE #1
    private void SystemGoal() {
        stackTrace += "SystemGoal\n";
        switch (getRule(NonTerminal.SystemGoal)) {
            case 1:
                Program(); // nonterminal 2
                if (l1.getTerminal() == Terminal.EOF) {
                    match();
                    //  Closes the file to complete genWrite_S
                    sa.genHalt();
                } else {
                    String[] err = {"end of file"};
                    error(err);
                }
                break;
            default:
                String[] err = {"program"};
                error(err);
                break;
        }
    }

    // Nonterminal 2
    // <Program> --> <ProgramHeading> ; <Block> . RULE #2
    private void Program() {
        stackTrace += "Program\n";
        switch (getRule(NonTerminal.Program)) {
            case 2:
                sh.pushTable(); //  Construct the original table
                int nestingL = sh.nestinglevel;
                //push registers on stack so that they can be restored after run
                sa.genStoreRegisters(nestingL);
                ProgramHeading(); // nonterminal 3
                if (l1.getTerminal() == Terminal.SCOLON) {
                    match();
                } else {
                    String[] err = {";"};
                    error(err);
                }
                Block(); // nonterminal 4
                if (l1.getTerminal() == Terminal.PERIOD) {
                    match();
                } else {
                    String[] err = {"."};
                    error(err);
                }
                //restore the register values
                sa.genRestoreRegisters(nestingL);
                break;
            default:
                String[] err = {"program"};
                error(err);
                break;
        }
    }

    // Nonterminal 3
    // <ProgramHeading> --> program <ProgramIdentifier> RULE #3
    private void ProgramHeading() {
        stackTrace += "ProgramHeading\n";
        switch (getRule(NonTerminal.ProgramHeading)) {
            case 3:
                if (l1.getTerminal() == Terminal.PROGRAM) {
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

    // Nonterminal 4
    // <Block> --> <VariableDeclarationPart> <ProcedureAndFunctionDeclarationPart> <StatementPart> RULE #4
    private void Block() {
        stackTrace += "Block\n";
        switch (getRule(NonTerminal.Block)) {
            case 4:
                ArrayList<Symbol> locals = new ArrayList<>();
                VariableDeclarationPart(locals);
                ProcedureAndFunctionDeclarationPart();
                StatementPart();
                sa.removeLocals(locals);
                sh.popTable();
                break;
            default:
                String[] err = {"var"};
                error(err);
        }
    }

    // Nonterminal 5
    // <VariableDeclarationPart> --> <VariableDeclaration> ; <VariableDeclarationTail> RULE #5
    // <VariableDeclarationPart> --> lambda RULE #6
    private void VariableDeclarationPart(ArrayList<Symbol> locals) {
        stackTrace += "VariableDeclarationPart\n";
        switch (getRule(NonTerminal.VariableDeclarationPart)) {
            case 5:
                if (l1.getTerminal() == Terminal.VAR) {
                    match();
                } else {
                    String[] err = {"var"};
                    error(err);
                }
                VariableDeclaration(locals);
                if (l1.getTerminal() == Terminal.SCOLON) {
                    match();
                } else {
                    String[] err = {";"};
                    error(err);
                }
                VariableDeclarationTail(locals);
                break;
            case 6:
                break;
            default:
                String[] err = {"var"};
                error(err);
        }
    }

    // Nonterminal 6
    // <VariableDeclarationTail> --> <VariableDeclaration> ; <VariableDeclarationTail> RULE #7
    // <VariableDeclarationTail> --> lambda RULE #8
    private void VariableDeclarationTail(ArrayList<Symbol> locals) {
        stackTrace += "VariableDeclarationTail\n";
        switch (getRule(NonTerminal.VariableDeclarationTail)) {
            case 7:
                VariableDeclaration(locals);
                if (l1.getTerminal() == Terminal.SCOLON) {
                    match();
                } else {
                    String[] err = {";"};
                    error(err);
                }
                VariableDeclarationTail(locals);
                break;
            case 8:
                break;
            default:
                String[] err = {"Integer", "Float", "String", "Boolean"};
                error(err);
                break;
        }
    }

    // Nonterminal 7
    // VariableDeclaration> --> <IdentifierList> : <Type> RULE #9
    private void VariableDeclaration(ArrayList<Symbol> locals) {
        stackTrace += "VariableDeclaration\n";
        switch (getRule(NonTerminal.VariableDeclaration)) {
            case 9:
                ArrayList<String> str = IdentifierList();
                if (l1.getTerminal() == Terminal.COLON) {
                    match();
                } else {
                    String[] err = {":"};
                    error(err);
                }
                Type type = Type();
                for (String s : str) {
                    sh.startEntry();
                    sh.setName(s);
                    sh.setType(type);
                    sh.setKind(Kind.VARIABLE);
                    sh.finishEntry();
                    locals.add(sh.getEntry(s));
                    sa.padForVariable();
                }
                break;
            default:
                String[] err = {"identifier"};
                error(err);
        }
    }

    // Nonterminal 8
    // <Type> --> Integer RULE #10
    // <Type> --> Float RULE #11
    // <Type> --> String RULE #12
    // <Type> --> Boolean RULE #13
    private Type Type() {
        stackTrace += "Type\n";
        switch (getRule(NonTerminal.Type)) {
            case 10:
                if (l1.getTerminal() == Terminal.INTEGER) {
                    match();
                    return Type.INTEGER;
                } else {
                    String[] err = {"Integer"};
                    error(err);
                    return Type.NOTYPE;
                }
            case 11:
                if (l1.getTerminal() == Terminal.FLOAT) {
                    match();
                    return Type.FLOAT;
                } else {
                    String[] err = {"Float"};
                    error(err);
                    return Type.NOTYPE;
                }
            case 12:
                if (l1.getTerminal() == Terminal.STRING) {
                    match();
                    return Type.STRING;
                } else {
                    String[] err = {"String"};
                    error(err);
                    return Type.NOTYPE;
                }
            case 13:
                if (l1.getTerminal() == Terminal.BOOLEAN) {
                    match();
                    return Type.BOOLEAN;
                } else {
                    String[] err = {"Boolean"};
                    error(err);
                    return Type.NOTYPE;
                }
            default:
                String err[] = {"Integer", "Float", "String", "Boolean"};
                error(err);
                return Type.NOTYPE;
        }
    }

    // Nonterminal 9
    // <ProcedureAndFunctionDeclarationPart> --> <ProcedureDeclaration> <ProcedureAndFunctionDeclarationPart> RULE #14
    // <ProcedureAndFunctionDeclarationPart> --> <FunctionDeclaration> <ProcedureAndFunctionDeclarationPart> RULE #15
    // <ProcedureAndFunctionDeclarationPart> --> lambda RULE #16
    private void ProcedureAndFunctionDeclarationPart() {
        stackTrace += "ProcedureAndFunctionDeclarationPart\n";
        switch (getRule(NonTerminal.ProcedureAndFunctionDeclarationPart)) {
            case 14:
                int label = sa.newLabel();
                //  Jump over the procedures and declarations... for now
                sa.genBranch(label);
                ProcedureDeclaration();
                ProcedureAndFunctionDeclarationPart();
                //  Go right to the statements
                sa.putLabel(label);
                break;
            case 15:
                label = sa.newLabel();
                //  Jump over the procedures and declarations... for now
                sa.genBranch(label);
                FunctionDeclaration();
                ProcedureAndFunctionDeclarationPart();
                //  Go right to the statements
                sa.putLabel(label);
                break;
            case 16:
                break;
            default:
                String err[] = {"procedure", "function", "begin"};
                error(err);
                break;
        }
    }

    // Nonterminal 10
    // <ProcedureDeclaration --> <ProcedureHeading> ; <Block> ; RULE #17
    private void ProcedureDeclaration() {
        stackTrace += "ProcedureDeclaration\n";
        switch (getRule(NonTerminal.ProcedureDeclaration)) {
            case 17:
                Symbol entry = ProcedureHeading();
                if (l1.getTerminal() == Terminal.SCOLON) {
                    match();
                } else {
                    String[] err = {";"};
                    error(err);
                }
                Block();
                sa.onEndFormalCall(entry);
                if (l1.getTerminal() == Terminal.SCOLON) {
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

    // Nonterminal 11
    // <FunctionDeclaration> --> <FunctionHeading> ; <Block> ; RULE #18
    private void FunctionDeclaration() {
        stackTrace += "FunctionDeclaration\n";
        switch (getRule(NonTerminal.FunctionDeclaration)) {
            case 18:
                Symbol entry = FunctionHeading();
                if (l1.getTerminal() == Terminal.SCOLON) {
                    match();
                } else {
                    String[] err = {";"};
                    error(err);
                }
                Block();
                sa.onEndFormalCall(entry);
                if (l1.getTerminal() == Terminal.SCOLON) {
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

    // Nonterminal 12
    // <ProcedureHeading> --> procedure <ProcedureIdentifier> <OptionalFormalParameterList> RULE #19
    private Symbol ProcedureHeading() {
        stackTrace += "ProcedureHeading\n";
        switch (getRule(NonTerminal.ProcedureHeading)) {
            case 19:
                if (l1.getTerminal() == Terminal.PROCEDURE) {
                    match();
                } else {
                    String[] err = {"procedure"};
                    error(err);
                }
                String name = ProcedureIdentifier();
                sh.startEntry();
                sh.setName(name);
                sh.setType(Type.NOTYPE);
                sh.setKind(Kind.PROCEDURE);
                OptionalFormalParameterList();  //  <-- Parser component
                sh.finishEntry();
                Symbol entry = sh.getEntry(name);
                ArrayList<Parameter> params = sh.getEntry(name).params;
                sh.pushTable();
                for (Parameter p : params) {
                    sh.startEntry();
                    sh.setName(p.name);
                    sh.setType(p.type);
                    if (p.kind == Kind.INPARAMETER) {
                        sh.setKind(Kind.INVARIABLE);
                    } else {
                        sh.setKind(Kind.INOUTVARIABLE);
                    }
                    sh.finishEntry();
                }
                sa.onStartFormalCall(entry);
                return entry;
            default:
                String[] err = {"procedure"};
                error(err);
                return null;
        }
    }

    // Nonterminal 13
    // <FunctionHeading> --> function <FunctionIdentifier> <OptionalFormalParameterList> : <Type> RULE #20
    private Symbol FunctionHeading() {
        stackTrace += "FunctionHeading\n";
        switch (getRule(NonTerminal.FunctionHeading)) {
            case 20:
                if (l1.getTerminal() == Terminal.FUNCTION) {
                    match();
                } else {
                    String[] err = {"function"};
                    error(err);
                }
                String name = FunctionIdentifier();
                sh.startEntry();
                sh.setName(name);
                sh.setKind(Kind.FUNCTION);
                OptionalFormalParameterList();  //  <-- Parser component
                sh.finishEntry();
                Symbol entry = sh.getEntry(name);
                ArrayList<Parameter> params = entry.params;
                sh.pushTable();
                for (Parameter p : params) {
                    sh.startEntry();
                    sh.setName(p.name);
                    sh.setType(p.type);
                    if (p.kind == Kind.INPARAMETER) {
                        sh.setKind(Kind.INVARIABLE);
                    } else {
                        sh.setKind(Kind.INOUTVARIABLE);
                    }
                    sh.finishEntry();
                }
                sa.onStartFormalCall(entry);
                if (l1.getTerminal() == Terminal.COLON) {
                    match();
                } else {
                    String[] err = {":"};
                    error(err);
                }
                Type t = Type();
                entry.type = t;
                return entry;
            default:
                String[] err = {"function"};
                error(err);
                return null;
        }
    }

    // Nonterminal 14
    // <OptionalFormalParameterList> --> ( <FormalParameterSection> <FormalParameterSectionTail> ) RULE #21
    // <OptionalFormalParameterList> --> lambda RULE #22
    private void OptionalFormalParameterList() {
        stackTrace += "OptionalFormalParameterList\n";
        switch (getRule(NonTerminal.OptionalFormalParameterList)) {
            case 21:
                if (l1.getTerminal() == Terminal.LPAREN) {
                    match();
                } else {
                    String[] err = {"("};
                    error(err);
                }
                FormalParameterSection();
                FormalParameterSectionTail();
                if (l1.getTerminal() == Terminal.RPAREN) {
                    match();
                } else {
                    String[] err = {")"};
                    error(err);
                }
                break;
            case 22:
                break;
            default:
                String[] err = {"(", ":"};
                error(err);
                break;
        }
    }

    // Nonterminal 15
    // <FormalParameterSectionTail> --> ; <FormalParameterSection> <FormalParameterSectionTail> RULE #23
    // <FormalParameterSectionTail> --> lambda RULE #24
    private void FormalParameterSectionTail() {
        stackTrace += "FormalParameterSectionTail\n";
        switch (getRule(NonTerminal.FormalParameterSectionTail)) {
            case 23:
                if (l1.getTerminal() == Terminal.SCOLON) {
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
                String[] err = {";", ")"};
                error(err);
                break;
        }
    }

    // Nonterminal 16
    // <FormalParamaterSection> --> <ValueParameterSection> RULE #25
    // <FormalParamaterSection> --> <VariableParameterSection> RULE #26
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

    // Nonterminal 17
    // <ValueParameterSection> --> <IdentifierList> : <Type> RULE #27
    private void ValueParameterSection() {
        stackTrace += "ValueParameterSection\n";
        switch (getRule(NonTerminal.ValueParameterSection)) {
            case 27:
                ArrayList<String> str = IdentifierList();
                if (l1.getTerminal() == Terminal.COLON) {
                    match();
                } else {
                    String[] err = {":"};
                    error(err);
                }
                Type type = Type();
                for (String s : str) {
                    sh.startParameter();
                    sh.setName(s);
                    sh.setType(type);
                    sh.setKind(Kind.INPARAMETER);
                    sh.finishParameter();
                }
                break;
            default:
                String[] err = {":"};
                error(err);
                break;
        }
    }

    // Nonterminal 18
    // <VariableParameterSection> --> var <IdentifierList> : <Type> RULE #28
    private void VariableParameterSection() {
        stackTrace += "VariableParameterSection\n";
        switch (getRule(NonTerminal.VariableParameterSection)) {
            case 28:
                if (l1.getTerminal() == Terminal.VAR) {
                    match();
                } else {
                    String[] err = {"var"};
                    error(err);
                }
                ArrayList<String> str = IdentifierList();
                if (l1.getTerminal() == Terminal.COLON) {
                    match();
                } else {
                    String[] err = {":"};
                    error(err);
                }
                Type type = Type();
                for (String s : str) {
                    sh.startParameter();
                    sh.setName(s);
                    sh.setType(type);
                    sh.setKind(Kind.INOUTPARAMETER);
                    sh.finishParameter();
                }
                break;
            default:
                String[] err = {"var"};
                error(err);
                break;
        }
    }

    // Nonterminal 19
    // <StatementPart> --> <CompoundStatement> RULE #29
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

    // Nonterminal 20
    // <CompoundStatement> --> begin <StatementSequence> end RULE #30
    private void CompoundStatement() {
        stackTrace += "CompoundStatement\n";
        switch (getRule(NonTerminal.CompoundStatement)) {
            case 30:
                if (l1.getTerminal() == Terminal.BEGIN) {
                    match();
                } else {
                    String[] err = {"begin"};
                    error(err);
                }
                StatementSequence();
                if (l1.getTerminal() == Terminal.END) {
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

    // Nonterminal 21
    // <StatementSequence> --> <Statement> <StatementTail> RULE #31
    private void StatementSequence() {
        stackTrace += "StatementSequence\n";
        switch (getRule(NonTerminal.StatementSequence)) {
            case 31:
                Statement();
                StatementTail();
                break;
            default:
                String[] err = {"begin", "read", "write", "writeln", "if",
                    "repeat", "for", "procedure", "identifier"};
                error(err);
                break;
        }
    }

    // Nonterminal 22
    // <StatementTail --> ; <statement> <StatementTail> RULE #32
    // <StatementTail --> lambda RULE #33
    private void StatementTail() {
        stackTrace += "StatementTail\n";
        switch (getRule(NonTerminal.StatementTail)) {
            case 32:
                if (l1.getTerminal() == Terminal.SCOLON) {
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

    // Nonterminal 23
    // Statement --> <EmptyStatement> RULE #34
    // Statement --> <CompoundStatement> RULE #35
    // Statement --> <ReadStatement> RULE #36
    // Statement --> <WriteStatement> RULE #37
    // Statement --> <AssignmentStatement> RULE #38
    // Statement --> <IfStatement> RULE #39
    // Statement --> <WhileStatement> RULE #40
    // Statement --> <RepeatStatement> RULE #41
    // Statement --> <ForStatement> RULE #42
    // Statement --> <ProcedureStatement> RULE #43
    private void Statement() {
        stackTrace += "Statement\n";
        int rule = getRule(NonTerminal.Statement);
        if (rule == 38) {
            l2 = scanner.nextToken();
            if (l2.getTerminal() != Terminal.ASSIGN) {
                rule = 43;
            }
        }
        switch (rule) {
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
                String[] err = {"begin", "read", "write", "writeln", "if",
                    "repeat", "for", "procedure", "identifier"};
                error(err);
                break;
        }
    }

    //**************************************************************************
    // rules 40-47 
    // Jacob Barthelmeh
    // Nonterminal 24
    // <EmptyStatement> --> lambda RULE #44
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

    // Nonterminal 25
    // <ReadStatement> --> genRead ( <ReadParameter> <ReadParameterTail> ) // 45
    private void ReadStatement() {
        stackTrace += "ReadStatement\n";
        switch (getRule(NonTerminal.ReadStatement)) {
            case 45://rule 45
                if (l1.getTerminal() == Terminal.READ) { //rule 45
                    match();
                    if (l1.getTerminal() == Terminal.LPAREN) {
                        match();
                        ReadParameter();
                        ReadParameterTail();
                        if (l1.getTerminal() == Terminal.RPAREN) {
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

    // Nonterminal 26
    // <ReadParameterTail --> , <ReadParameter> <ReadParameterTail> RULE #46
    // <ReadParameterTail --> lambda RULE #47
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

    // Nonterminal 27
    // <ReadParameter> --> <VariableIdentifier> RULE #48
    private void ReadParameter() {
        stackTrace += "ReadParameter\n";
        switch (getRule(NonTerminal.ReadParameter)) {
            case 48://rule 48
                Token t = l1;
                VariableIdentifier();
                sa.genRead(new SemanticRecord(t, sh.getEntry(t.getContents())));
                break;
            default:
                String exp[] = {""};
                error(exp);
                break;
        }
    }

    // Nonterminal 28
// <WriteStatement> --> genWrite_S  ( <WriteParameter> <WriteParameterTail> //49
// <WriteStatement> --> writeln ( <WriteParameter> <WriteParameterTail> ) //  50
    private void WriteStatement() {
        stackTrace += "WriteStatment\n";
        switch (getRule(NonTerminal.WriteStatement)) {
            case 49: //rule 49
                match();
                if (l1.getTerminal() == Terminal.LPAREN) {
                    match();
                    sa.startWrite(false);
                    WriteParameter();
                    WriteParameterTail();
                    if (l1.getTerminal() == Terminal.RPAREN) {
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
                if (l1.getTerminal() == Terminal.LPAREN) {
                    match();
                    sa.startWrite(true);
                    WriteParameter();
                    WriteParameterTail();
                    if (l1.getTerminal() == Terminal.RPAREN) {
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

    // Nonterminal 29
    // <WriteParameterTail> --> , <WriteParameter> <WrieteParameterTail> // 51
    // <WriteParameterTail> -->  lambda RULE #52
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

    // Nonterminal 30
    // <WriteParameter> --> <OrdinalExpression> RULE #53
    private void WriteParameter() {
        stackTrace += "WriteParameter\n";
        switch (getRule(NonTerminal.WriteParameter)) {
            case 53://rule 53
                Token t = l1;
                OrdinalExpression();
                sa.genWrite_S(t);
                //  sa.genWrite_S(); //  genWrite_S the terminal
                break;
            default:
                String exp[] = {""};
                error(exp);
                break;
        }
    }

    // Nonterminal 31
    // <AssignmentStatement> --> <VariableIdentifier> := <Expression> RULE #54
    // <AssignmentStatement> --> <FunctionIdentifier> := <Expression> RULE #55
    private void AssignmentStatement() {
        stackTrace += "AssignmentStatement\n";
        switch (getRule(NonTerminal.AssignmentStatement)) {
            case 54: //rule 54
                Token t = l1;
                VariableIdentifier();
                if (l1.getTerminal() == Terminal.ASSIGN) {
                    match();
                    SemanticRecord returnstuff = Expression();
                    sa.genAssignment(new SemanticRecord(
                            t, sh.getEntry(t.getContents())), returnstuff);
                } else {
                    String[] err = {"assign"};
                    error(err);
                }
                break;
            case 55://rule 55
                t = l1;
                FunctionIdentifier(); //rule 55
                if (l1.getTerminal() == Terminal.ASSIGN) {
                    match();
                    SemanticRecord returnstuff = Expression();
                    sa.genAssignment(new SemanticRecord(
                            t, sh.getEntry(t.getContents())), returnstuff);

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

    // Nonterminal 32
//<IfStatement> -> if <BooleanExpression> then <Statement><OptionalElsePart>//45
    private void IfStatement() {
        stackTrace += "IfStatement\n";
        switch (getRule(NonTerminal.IfStatement)) {
            case 56: //rule 56
                if (l1.getTerminal() == Terminal.IF) {
                    match();
                    int toFalse = sa.newLabel(),
                            toEnd = sa.newLabel();
                    BooleanExpression();
                    sa.genBranchFalse_S(toFalse);
                    if (l1.getTerminal() == Terminal.THEN) {
                        match();
                        Statement();
                        sa.genBranch(toEnd);
                        sa.putLabel(toFalse);
                        OptionalElsePart();
                        sa.putLabel(toEnd);
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

    // Nonterminal 33
    // <OptionalElsePart> --> else <Statement> RULE #57
    // <OptionalElsePart> --> lambda RULE #58
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

    // Nonterminal 34
//<RepeatStatement> -> repeat <StatementSequence> until <BooleanExpression> //59
    private void RepeatStatement() {
        stackTrace += "RepeatStatement\n";
        switch (getRule(NonTerminal.RepeatStatement)) {
            case 59://rule 59
                if (l1.getTerminal() == Terminal.REPEAT) {
                    match();
                    int top = sa.newLabel();
                    sa.putLabel(top);
                    StatementSequence();
                    if (l1.getTerminal() == Terminal.UNTIL) {
                        match();
                        BooleanExpression();
                        sa.genBranchFalse_S(top);
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

    // Nonterminal 35
    // <WhileStatement> --> while <BooleanExpression> do <Statement> RULE #60
    private void WhileStatement() {
        stackTrace += "WhileStatment\n";
        switch (getRule(NonTerminal.WhileStatement)) {
            case 60://rule 60
                if (l1.getTerminal() == Terminal.WHILE) {
                    int loop = sa.newLabel(),
                            exit = sa.newLabel();
                    match();
                    sa.putLabel(loop);
                    BooleanExpression();
                    sa.genBranchFalse_S(exit);
                    if (l1.getTerminal() == Terminal.DO) {
                        match();
                        Statement();
                        sa.genBranch(loop);
                        sa.putLabel(exit);
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

    // Nonterminal 36
    // <ForStatement> -->
        //  for <ControlVariable> := <InitialValue> <StepValue> <FinalValue>
        //  do <Statement> RULE #61
    private void ForStatement() {
        stackTrace += "ForStatement\n";
        switch (getRule(NonTerminal.ForStatement)) {
            case 61://rule 61
                if (l1.getTerminal() == Terminal.FOR) {
                    match();
                    int loop = sa.newLabel(),
                            exit = sa.newLabel();
                    SemanticRecord control = ControlVariable();
                    if (l1.getTerminal() == Terminal.ASSIGN) {
                        match();
                        SemanticRecord initialvalue = InitialValue();
                        //  Either to or downto
                        boolean increment = StepValue();
                        SemanticRecord finalvalue = FinalValue();
                        sa.genForInitialize(control, initialvalue);
                        sa.putLabel(loop);
                        sa.genForTest(control, increment, finalvalue);
                        sa.genBranchFalse_S(exit);
                        if (l1.getTerminal() == Terminal.DO) {
                            match();
                            Statement();
                            sa.genForAlter(control, increment);
                            sa.genBranch(loop);
                            sa.putLabel(exit);
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

    // Nonterminal 37
    // <ControlVariable> --> <VariableIdentifier> RULE #62
    private SemanticRecord ControlVariable() {
        stackTrace += "ControlVariable\n";
        switch (getRule(NonTerminal.ControlVariable)) {
            case 62://rule 62
                Token t = l1;
                VariableIdentifier();
                return new SemanticRecord(t, sh.getEntry(t.getContents()));
            default:
                String exp[] = {""};
                error(exp);
                return null;
        }
    }

    // Nonterminal 38
    // <InitialValue> --> <OrdinalExpression> RULE #63
    private SemanticRecord InitialValue() {
        stackTrace += "InitialValue\n";
        switch (getRule(NonTerminal.InitialValue)) {
            case 63://rule 63
                return OrdinalExpression();
            default:
                String exp[] = {""};
                error(exp);
                return null;
        }
    }

    // Nonterminal 39
    // <StepValue> --> to RULE #64
    // <StepValue> --> downto RULE #65
    private boolean StepValue() {
        stackTrace += "StepValue\n";
        switch (getRule(NonTerminal.StepValue)) {
            case 64: //rule 64
                match();
                return true;
            case 65: //rule 65
                match();
                return false;
            default:
                String[] err = {"to", "downto"};
                error(err);
                return true;
        }
    }

    // Nonterminal 40
    // <FinalValue> --> <OrdinalExpression> RULE #66
    private SemanticRecord FinalValue() {
        stackTrace += "FinalValue\n";
        switch (getRule(NonTerminal.FinalValue)) {
            case 66://rule 66
                return OrdinalExpression();
            default:
                String exp[] = {""};
                error(exp);
                return null;
        }
    }

    // Nonterminal 41
    // <ProcedureStatement> --> <ProcedureIdentifier> <OptionalActualParameterList> RULE #67
    private void ProcedureStatement() {
        stackTrace += "ProcedureStatment\n";
        switch (getRule(NonTerminal.ProcedureStatement)) {
            case 67://rule 67
                String id = ProcedureIdentifier();
                Symbol procedure = sh.getEntry(id);
                sa.funcCall = true;
                ArrayList<SemanticRecord> params = OptionalActualParameterList();
                sa.funcCall = false;
                sa.onStartActualCall(procedure, params);
                break;
            default:
                String exp[] = {""};
                error(exp);
                break;
        }
    }

    // Nonterminal 42
//<OptionalActualParameterList> -> ( <ActualParameter><ActualParameterTail>)//68
//<OptionalActualParameterList> -> lambda RULE #69
    private ArrayList<SemanticRecord> OptionalActualParameterList() {
        stackTrace += "OptionalActualParameterList\n";
        switch (getRule(NonTerminal.OptionalActualParameterList)) {
            case 68: //rule 68
                match();
                ArrayList<SemanticRecord> params = new ArrayList();
                params.add(ActualParameter());
                ActualParameterTail(params);
                if (l1.getTerminal() == Terminal.RPAREN) {
                    match();
                } else {
                    String[] err = {")"};
                    error(err);
                }
                return params;
            case 69://rule 69 switch on e
                return new ArrayList();
            default:
                String[] err = {"("};
                error(err);
                return null;
        }
    }

    // Nonterminal 43
    // <ActualParameterTail> --> , <ActualParameter> <ActualParameterTail> // 70
    // <ActualParameterTail> --> lambda RULE #71
    private void ActualParameterTail(ArrayList<SemanticRecord> params) {
        stackTrace += "ActualParameterTail\n";
        switch (getRule(NonTerminal.ActualParameterTail)) {
            case 70: //rule 70
                match();
                params.add(ActualParameter());
                ActualParameterTail(params);
                break;
            case 71: //e
                break;
            default:
                String[] err = {","};
                error(err);
                break;
        }
    }

    // Nonterminal 44
    // <ActualParameter> --> <OrdinalExpression> RULE #72
    private SemanticRecord ActualParameter() {
        stackTrace += "ActualParameter\n";
        switch (getRule(NonTerminal.ActualParameter)) {
            case 72:
                return OrdinalExpression();//rule 72
            default:
                String exp[] = {""};
                error(exp);
                return null;
        }
    }

    // Nonterminal 45
    // <Expression> --> <SimpleExpression> <OptionalRelationalPart> RULE #73
    private SemanticRecord Expression() {
        stackTrace += "Expression\n";
        switch (getRule(NonTerminal.Expression)) {
            case 73:
                SemanticRecord left = SimpleExpression();
                return OptionalRelationalPart(left);
            default:
                String exp[] = {""};
                error(exp);
                return null;
        }
    }

    // Nonterminal 46
    // <OptionalRelationalPart> --> <RelationalOperator> <SimpleExpression> #74
    // <OptionalRelationalPart> --> lambda RULE #75
    private SemanticRecord OptionalRelationalPart(SemanticRecord left) {
        stackTrace += "OptionalRelationalPart\n";
        switch (getRule(NonTerminal.OptionalRelationalPart)) {
            case 74:
                Operator opp = RelationalOperator(); //rule 74
                SemanticRecord right = SimpleExpression(); //rule 74
                sa.genArithOperator_S(left, opp, right);
                return new SemanticRecord(
                        right.token, right.symbol,"", "", "", Type.BOOLEAN);
            case 75://rule 75
                return left;
            default:
                String exp[] = {""};
                error(exp);
                return left;
        }
    }

    // Nonterminal 47
    // RelationalOperator> --> = RULE #76
    // RelationalOperator> --> < RULE #77
    // RelationalOperator> --> > RULE #78
    // RelationalOperator> --> <= RULE #79
    // RelationalOperator> --> >= RULE #80
    // RelationalOperator> --> <> RULE #81
    private Operator RelationalOperator() {
        stackTrace += "RelationalOperator\n";
        switch (getRule(NonTerminal.RelationalOperator)) {
            case 76: //rule 76
                match();
                return Operator.EQUAL;
            case 77: //rule 77
                match();
                return Operator.LTHAN;
            case 78: //rule 78
                match();
                return Operator.GTHAN;
            case 79: //rule 79
                match();
                return Operator.LEQUAL;
            case 80: //rule 80
                match();
                return Operator.GEQUAL;
            case 81: //rule 81
                match();
                return Operator.NEQUAL;
            default:
                String exp[] = {"EQUAL", "LTHAN", "GTHAN", "LEQUAL", "GEQUAL", 
                    "NEQUAL"};
                error(exp);
                return Operator.NOOP;
        }
    }

    //**************************************************************************
    //stubs for rules 78 - 150
    // Nonterminal 48
    // <SimpleExpression> --> <OptionalSign> <Term> <TermTail> RULE #82
    private SemanticRecord SimpleExpression() {
        stackTrace += "SimpleExpression\n";
        switch (getRule(NonTerminal.SimpleExpression)) {
            case 82:
                boolean positive = OptionalSign(); // Rule 82
                SemanticRecord left = Term();
                if (!positive) {
                    sa.genNegation_S(left);
                }
                return TermTail(left);
            default:
                String exp[] = {""};
                error(exp);
                return null;
        }
    }

    // Nonterminal 49
    // <TermTail> --> <AddingOperator> <Term> <TermTail> RULE #83
    // <TermTail> --> lambda RULE #84
    private SemanticRecord TermTail(SemanticRecord left) {
        stackTrace += "TermTail\n";
        switch (getRule(NonTerminal.TermTail)) {
            case 83:
                Operator opp = AddingOperator();
                SemanticRecord right = Term();
                SemanticRecord sum;
                if (opp == Operator.OR) {
                    sa.genLogicalOperator_S(left, opp, right);
                    sum = left;
                } else {
                    boolean floating = sa.genArithOperator_S(left, opp, right);
                    sum = new SemanticRecord(right.token, null, "", "", "",
                            floating ? Type.FLOAT : Type.INTEGER);
                }

                // have enough information for creating arithmitic operation 
                return TermTail(sum);
            case 84:
                return left;
            default:
                String exp[] = {""};
                error(exp);
                return left;
        }
    }

    // Nonterminal 50
    // <OptionalSign> --> + RULE #86
    // <OptionalSign> --> - RULE #87
    // <OptionalSign> --> lambda RULE #88
    private boolean OptionalSign() {
        stackTrace += "OptionalSign\n";
        switch (getRule(NonTerminal.OptionalSign)) {
            case 85: // +
                match();
                return true;
            case 86: // -
                match();
                return false;
            case 87: // e
                return true;
            default:
                String exp[] = {"+", "-"};
                error(exp);
                return true;
        }
    }

    // Nonterminal 51
    // <AddingOperator> --> + RULE #89
    // <AddingOperator> --> - RULE #90
    // <AddingOperator> --> or RULE #91
    private Operator AddingOperator() {
        stackTrace += "AddingOperator\n";
        switch (getRule(NonTerminal.AddingOperator)) {
            case 88: // +
                match();
                return Operator.ADDITION;
            case 89: // -
                match();
                return Operator.SUBTRACTION;
            case 90: // or
                match();
                return Operator.OR;
            default:
                String exp[] = {"+", "-", "or"};
                error(exp);
                return Operator.NOOP;
        }
    }

    // Nonterminal 52
    // <Term> --> <Factor> <FactorTail> RULE #91
    private SemanticRecord Term() {
        stackTrace += "Term\n";
        switch (getRule(NonTerminal.Term)) {
            case 91:
                SemanticRecord left = Factor();      // RULE 91
                return FactorTail(left);  // RULE 91
            default:
                String exp[] = {""};
                error(exp);
                return null;
        }
    }

    // Nonterminal 53
    // <FactorTail> --> <MultiplyingOperator> <Factor> <FactorTail> RULE #92
    // <FactorTail> --> lambda RULE #93
    private SemanticRecord FactorTail(SemanticRecord left) {
        stackTrace += "FactorTail\n";
        switch (getRule(NonTerminal.FactorTail)) {
            case 92:
                Operator opp = MultiplyingOperator();
                SemanticRecord right = Factor();
                SemanticRecord product;
                if (opp == Operator.AND) {
                    sa.genLogicalOperator_S(left, opp, right);
                    product = right;
                } else {
                    boolean floating = sa.genArithOperator_S(left, opp, right);
                    product = new SemanticRecord(
                            right.token, right.symbol, "", "", "",
                            //  Need to handle whether it was casted
                            floating ? Type.FLOAT : Type.INTEGER);
                }
                return FactorTail(product);
            case 93:
                return left;
            default:
                String exp[] = {"operator", "end of expression"};
                error(exp);
                return left;
        }
    }

    // Nonterminal 54
    // <MultiplyingOperator> --> * RULE #94
    // <MultiplyingOperator> --> / RULE #95
    // <MultiplyingOperator> --> div RULE #96
    // <MultiplyingOperator> --> mod RULE #97
    // <MultiplyingOperator> --> and RULE #98
    private Operator MultiplyingOperator() {
        stackTrace += "MultiplyingOperator\n";
        switch (getRule(NonTerminal.MultiplyingOperator)) {
            case 94:         //  * RULE 94
                match();
                return Operator.MULTIPLICATION;
            case 95:  // / RULE 95
                match();
                return Operator.DIVISION;
            case 96:           // / RULE 96
                match();
                return Operator.DIVISION;
            case 97:           // % RULE 97
                match();
                return Operator.MODULO;
            case 98:           // and RULE 98
                match();
                return Operator.AND;
            default:
                String exp[] = {"*", "/", "div", "%", "and"};
                error(exp);
                return Operator.NOOP;
        }
    }

    // Nonterminal 55
    // <Factor> --> unsignedInteger RULE #99
    // <Factor> --> unsignedFloat RULE #100
    // <Factor> --> stringLiteral RULE #101
    // <Factor> --> TRUE RULE #102
    // <Factor> --> FALSE RULE #103
    // <Factor> --> not <Factor> RULE #104
    // <Factor> --> ( <Expression> ) RULE #105
    // <Factor> --> <FunctionIdentifier> <OptionalActualParamterList> RULE #106
    // <Factor> --> <VariableIdentifier> RULE #116
    private SemanticRecord Factor() {
        stackTrace += "Factor\n";

        //handle special case in table that branchs when id is not at end of
        //statement
        int rule = getRule(NonTerminal.Factor);
        if (rule == 106) {
            l2 = scanner.nextToken();
            if (l2.getTerminal() != Terminal.LPAREN) {
                rule = 116;
            }
        }
        SemanticRecord r;
        switch (rule) {
            case 99:
                r = new SemanticRecord(l1, null);
                sa.genPush(r);
                match();
                return r;
            case 100:
                r = new SemanticRecord(l1, null);
                sa.genPush(r);
                match();
                return r;
            case 101:
                r = new SemanticRecord(l1, null);
                sa.genPush(r);
                match();
                return r;
            case 102:          // RULE 102 True
                r = new SemanticRecord(l1, null);
                sa.genPush(r);
                match();
                return r;
            case 103:         // RULE 103 False
                r = new SemanticRecord(l1, null);
                sa.genPush(r);
                match();
                return r;
            case 104:           // not Factor() RULE 104
                match();
                r = Factor();
                sa.genNot_S();
                return r;
            case 105:        // RULE 105
                match();
                r = Expression();
                switch (l1.getTerminal()) {
                    case RPAREN:
                        match();
                        break;
                    default:
                        String exp[] = {")"};
                        error(exp);
                }
                return r;
            case 106:   // RULE 106
                Symbol entry = sh.getEntry(l1.getContents());
                if (entry.kind == Kind.FUNCTION) {
                    r = new SemanticRecord(l1, entry);
                    Symbol function = sh.getEntry(FunctionIdentifier());
                    sa.funcCall = true;
                    ArrayList<SemanticRecord> params =
                            OptionalActualParameterList();
                    sa.funcCall = false;
                    sa.onStartActualCall(function, params);
                    return r;
                }
            //  Fall through. It wasn't a function, so it should be identifier.
            case 116:  // RULE 116
                r = new SemanticRecord(l1, sh.getEntry(l1.getContents()));
                sa.genPush(r);
                match();
                return r;
            default:
                String[] exp = {"INTEGER", "FLOAT", "STRING_LIT", "TRUE",
                    "FALSE", "NOT", "LPAREN EXPRESSION RPAREN", 
                    "FunctionIdentifier OptionalActualParameterList"};
                error(exp);
                return null;
        }
    }

    // Nonterminal 56
    // <ProgramIdentifier> --> identifier RULE #107
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

    // Nonterminal 57
    // <VariableIdentifier> --> identifier RULE #108
    private String VariableIdentifier() {
        stackTrace += "VariableIdentifier\n";
        switch (getRule(NonTerminal.VariableIdentifier)) {
            case 108:    // RULE 108
                String str = l1.getContents();
                match();
                return str;
            default:
                String[] exp = {"IDENTIFIER"};
                error(exp);
                return "";
        }
    }

    // Nonterminal 58
    // <ProcedureIdentifier> --> identifier RULE #109
    private String ProcedureIdentifier() {
        stackTrace += "ProcedureIdentifier\n";
        switch (getRule(NonTerminal.ProcedureIdentifier)) {
            case 109:     // RULE 109
                String str = l1.getContents();
                match();
                return str;
            default:
                String[] exp = {"IDENTIFIER"};
                error(exp);
                return "";
        }
    }

    // Nonterminal 59
    // <FunctionIdentifier> --> identifier RULE #111
    private String FunctionIdentifier() {
        stackTrace += "FunctionIdentifier\n";
        switch (getRule(NonTerminal.FunctionIdentifier)) {
            case 110:    // RULE 110
                String str = l1.getContents();
                match();
                return str;
            default:
                String[] exp = {"IDENTIFIER"};
                error(exp);
                return "";
        }
    }

    // Nonterminal 60
    // <BooleanExpression> --> <Expression> RULE #112
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

    // Nonterminal 61
    // <OrdinalExpression> --> <Expression> RULE #113
    private SemanticRecord OrdinalExpression() {
        stackTrace += "OrdinalExpression\n";
        switch (getRule(NonTerminal.OrdinalExpression)) {
            case 112:    // RULE 112
                return Expression();
            default:
                String[] exp = {"expression"};
                error(exp);
                return null;
        }
    }

    // Nonterminal 62
    // <IdentifierList> --> identifier <IdentifierTail> RULE #114
    private ArrayList<String> IdentifierList() {
        stackTrace += "IdentifierList\n";
        switch (getRule(NonTerminal.IdentifierList)) {
            case 113:    // RULE 113
                ArrayList<String> str = new ArrayList();
                str.add(l1.getContents());
                match();
                return IdentifierTail(str);
            default:
                String[] exp = {"IDENTIFIER"};
                error(exp);
                return null;
        }
    }

    // Nonterminal 63
    // <IdentifierTail> --> , identifier <IdentifierTail> RULE #115
    // <IdentifierTail> --> lambda
    private ArrayList<String> IdentifierTail(ArrayList<String> str) {
        stackTrace += "IdentifierTail\n";
        switch (getRule(NonTerminal.IdentifierTail)) {
            case 114:
                match(); // RULE 114
                if (l1.getTerminal() == Terminal.IDENTIFIER) {
                    str.add(l1.getContents());
                    match();
                } else {
                    String[] err = {"IDENTIFIER"};
                    error(err);
                }
                return IdentifierTail(str);
            case 115: // for rule 115
                return str;
            default:
                String[] err = {"COMMA IDENTIFIER IdentifierTail"};
                error(err);
                return str;
        }
    }

    /**
     * genRead in csv ll1 table
     *
     * @param sa semantic analyzer object
     */
    public Parser(SemanticAnalyzer sa) {
        this.sa = sa;
        this.sh = sa.sh;
    }

    /**
     * Set a file to parse
     *
     * @param in file to be parsed
     * @return 0 on success
     */
    public boolean parseFile(String in) {
        scanner = new Scanner(in);
        l1 = scanner.nextToken();
        SystemGoal();
        if (Compiler.DEBUG) {
            rFile.close();
        }
        scanner.close();
        return noerrors;
    }

    // Prints the rule taken
    private int ruleFile(int rule) {
        if (Compiler.DEBUG) {
            if (rFile == null) {
                try {
                    rFile = new PrintWriter(rule_tree_file);
                    rFile.println("Rules Taken,Token,Non-Terminal");
                } catch (Exception e) {
                    System.out.println("Unable to make file " + rule_tree_file);
                    return 1;
                }
            }
            rFile.print(rule + "," + l1);
        }
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
        if (l2 == null) {
            l1 = scanner.nextToken();
        } else {
            l1 = l2;
            l2 = null;
        }
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
        noerrors = false;
        System.err.println("Parse Error: found " + l1.getContents() + " "
                + l1.getTerminal() + " at line " + l1.getLine() + " col "
                + l1.getCol());
        System.err.print("Was expecting ");
        System.err.print(Arrays.toString(expected));
        System.err.println("");

        /* possibly a system exit here? */
        if (Compiler.DEBUG) {
            System.out.println(stackTrace);
        }
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
        //  The index corresponding to the current look ahead token
        int index = l1.getTerminal().ordinal(),
                nonTerminal = nt.ordinal();

        if (nonTerminal > Table.length) {
            System.out.println("Error nonTerminal " + nonTerminal
                    + " is not in table");
            System.exit(1);
        }
        if (index > Table[nonTerminal].length) {
            System.out.println("Error token " + index + "  " + l1.getTerminal() 
                    + " not in table ");
            System.out.println(" " + Table[nonTerminal].length);
            System.exit(1);
        }
        
        // integer corresponding to rule taken
        int rule = Table[nonTerminal][index]; 
        if (Compiler.DEBUG) {
            ruleFile(rule); // genWrite_S the rule taken
            rFile.print("," + nt + "\n"); //print terminal taken
        }
        return rule;
    }

    private final SymbolTableHandler sh;
    private final SemanticAnalyzer sa;
    private Token l1; // look ahead token
    private Token l2; // used for some cases when table is ll2
    private Scanner scanner;
    private PrintWriter rFile;
    //  Contains rules for going from non-terminals to terminals
    //  Helpful for debugging
    private String rule_tree_file = "rule_list.csv"; 
    private boolean noerrors = true;
    private String stackTrace = "";
    
    //  The best design choice you've ever seen
    private final int Table[][] = {
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 4, -1, -1, -1, -1, -1, 4, -1, -1, -1, -1, -1, -1, -1, -1, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 6, -1, -1, -1, -1, -1, 6, -1, -1, -1, -1, -1, -1, -1, -1, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 8, -1, -1, -1, -1, -1, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, 13, -1, -1, -1, -1, -1, -1, -1, 11, -1, -1, -1, 10, -1, -1, -1, -1, -1, -1, -1, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, 16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 15, -1, -1, -1, -1, -1, 14, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 17, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 18, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 19, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 20, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 22, 22, 21, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 23, -1, -1, 24, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 26, -1, -1, -1, 25, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 27, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 28, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, 29, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, 30, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, 31, -1, -1, -1, -1, -1, 31, -1, -1, -1, 31, -1, 31, -1, -1, -1, -1, -1, -1, 31, 31, -1, -1, -1, -1, 31, -1, 31, 31, 31, 31, -1, -1, -1, -1, -1, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, 33, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 33, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 32, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, 35, -1, -1, -1, -1, 34, 34, -1, -1, -1, 42, -1, 39, -1, -1, -1, -1, 43, -1, 36, 41, -1, -1, -1, -1, 34, -1, 40, 37, 37, 38, -1, -1, -1, -1, -1, 34, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, 44, 44, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 44, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 44, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 45, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 46, -1, -1, -1, 47, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 48, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 49, 50, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 51, -1, -1, -1, 52, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, 53, -1, -1, -1, -1, -1, -1, -1, 53, -1, -1, -1, -1, -1, -1, -1, -1, 53, -1, -1, -1, -1, -1, 53, 53, 53, 53, -1, -1, -1, -1, 53, -1, -1, -1, -1, -1, -1, -1, -1, 53, 53, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 54, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 56, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, 57, 58, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 58, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 58, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 59, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 60, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 61, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, 63, -1, -1, -1, -1, -1, -1, -1, 63, -1, -1, -1, -1, -1, -1, -1, -1, 63, -1, -1, -1, -1, -1, 63, 63, 63, 63, -1, -1, -1, -1, 63, -1, -1, -1, -1, -1, -1, -1, -1, 63, 63, -1, -1},
        {-1, -1, -1, -1, -1, 65, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 64, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, 66, -1, -1, -1, -1, -1, -1, -1, 66, -1, -1, -1, -1, -1, -1, -1, -1, 66, -1, -1, -1, -1, -1, 66, 66, 66, 66, -1, -1, -1, -1, 66, -1, -1, -1, -1, -1, -1, -1, -1, 66, 66, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 67, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, 69, 69, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 69, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 69, -1, 68, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 70, -1, -1, -1, 71, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, 72, -1, -1, -1, -1, -1, -1, -1, 72, -1, -1, -1, -1, -1, -1, -1, -1, 72, -1, -1, -1, -1, -1, 72, 72, 72, 72, -1, -1, -1, -1, 72, -1, -1, -1, -1, -1, -1, -1, -1, 72, 72, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, 73, -1, -1, -1, -1, -1, -1, -1, 73, -1, -1, -1, -1, -1, -1, -1, -1, 73, -1, -1, -1, -1, -1, 73, 73, 73, 73, -1, -1, -1, -1, 73, -1, -1, -1, -1, -1, -1, -1, -1, 73, 73, -1, -1},
        {-1, -1, -1, -1, 75, 75, 75, 75, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 75, 75, -1, 75, -1, -1, -1, -1, -1, -1, -1, -1, -1, 75, 75, -1, 75, 75, 74, 74, 74, 74, 74, 74, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 76, 78, 77, 79, 80, 81, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, 82, -1, -1, -1, -1, -1, -1, -1, 82, -1, -1, -1, -1, -1, -1, -1, -1, 82, -1, -1, -1, -1, -1, 82, 82, 82, 82, -1, -1, -1, -1, 82, -1, -1, -1, -1, -1, -1, -1, -1, 82, 82, -1, -1},
        {-1, -1, -1, -1, 84, 84, 84, 84, -1, -1, -1, -1, -1, -1, -1, -1, -1, 83, -1, -1, -1, -1, -1, 84, 84, -1, 84, -1, -1, -1, -1, -1, -1, -1, -1, -1, 84, 84, -1, -1, 84, 84, 84, 84, 84, 84, 84, -1, 83, 83, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, 87, -1, -1, -1, -1, -1, -1, -1, 87, -1, -1, -1, -1, -1, -1, -1, -1, 87, -1, -1, -1, -1, -1, 87, 87, 87, 87, -1, -1, -1, -1, 87, -1, -1, -1, -1, -1, -1, -1, -1, 85, 86, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 90, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 88, 89, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, 91, -1, -1, -1, -1, -1, -1, -1, 91, -1, -1, -1, -1, -1, -1, -1, -1, 91, -1, -1, -1, -1, -1, 91, 91, 91, 91, -1, -1, -1, -1, 91, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {92, -1, -1, 92, 93, 93, 93, 93, -1, -1, -1, -1, -1, -1, -1, 92, -1, 93, -1, -1, -1, -1, -1, 93, 93, -1, 93, -1, -1, -1, -1, -1, -1, -1, -1, -1, 93, 93, -1, -1, 93, 93, 93, 93, 93, 93, 93, -1, 93, 93, 92, 92},
        {98, -1, -1, 96, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 97, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 94, 95},
        {-1, -1, -1, -1, -1, -1, -1, -1, 103, -1, -1, -1, -1, -1, -1, -1, 104, -1, -1, -1, -1, -1, -1, -1, -1, 102, -1, -1, -1, -1, -1, 106, 99, 100, 101, -1, -1, -1, -1, 105, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 107, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 108, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 109, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 110, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, 111, -1, -1, -1, -1, -1, -1, -1, 111, -1, -1, -1, -1, -1, -1, -1, -1, 111, -1, -1, -1, -1, -1, 111, 111, 111, 111, -1, -1, -1, -1, 111, -1, -1, -1, -1, -1, -1, -1, -1, 111, 111, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, 112, -1, -1, -1, -1, -1, -1, -1, 112, -1, -1, -1, -1, -1, -1, -1, -1, 112, -1, -1, -1, -1, -1, 112, 112, 112, 112, -1, -1, -1, -1, 112, -1, -1, -1, -1, -1, -1, -1, -1, 112, 112, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 113, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 114, -1, 115, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}
    };
/*  Hard-to-read one that doesn't text-wrap in the document
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 4, -1, -1, -1, -1, -1, 4, -1, -
1, -1, -1, -1, -1, -1, -1, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 6, -1, -1, -1, -1, -1, 6, -1, -
1, -1, -1, -1, -1, -1, -1, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 8, -1, -1, -1, -1, -1, 8, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 7, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 9, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, 13, -1, -1, -1, -1, -1, -1, -1, 11, -1, -1, -1, 10, -1, -1, -1, -1, -
1, -1, -1, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, 16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 15, -1, -1, -1, -1, -1, 14, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 17, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 18, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 19, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 20, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 22, 22, 
21, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 23, -1, 
-1, 24, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, 26, -1, -1, -1, 25, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 27, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, 28, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, 29, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, 30, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, 31, -1, -1, -1, -1, -1, 31, -1, -1, -1, 31, -1, 31, -1, -1, -1, -1, -1, -
1, 31, 31, -1, -1, -1, -1, 31, -1, 31, 31, 31, 31, -1, -1, -1, -1, -1, 31, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, 33, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, 33, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 32, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, 35, -1, -1, -1, -1, 34, 34, -1, -1, -1, 42, -1, 39, -1, -1, -1, -1, 43, -
1, 36, 41, -1, -1, -1, -1, 34, -1, 40, 37, 37, 38, -1, -1, -1, -1, -1, 34, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, 44, 44, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, 44, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 44, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, 45, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 46, -1, -1, 
-1, 47, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 48, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 49, 50, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 51, -1, -1, 
-1, 52, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, 53, -1, -1, -1, -1, -1, -1, -1, 53, -1, -1, -
1, -1, -1, -1, -1, -1, 53, -1, -1, -1, -1, -1, 53, 53, 53, 53, -1, -1, -1, -1, 
53, -1, -1, -1, -1, -1, -1, -1, -1, 53, 53, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 54, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 56, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, 57, 58, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, 58, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 58, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, 59, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, 60, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 61, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, 63, -1, -1, -1, -1, -1, -1, -1, 63, -1, -1, -
1, -1, -1, -1, -1, -1, 63, -1, -1, -1, -1, -1, 63, 63, 63, 63, -1, -1, -1, -1, 
63, -1, -1, -1, -1, -1, -1, -1, -1, 63, 63, -1, -1},
{-1, -1, -1, -1, -1, 65, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, 64, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, 66, -1, -1, -1, -1, -1, -1, -1, 66, -1, -1, -
1, -1, -1, -1, -1, -1, 66, -1, -1, -1, -1, -1, 66, 66, 66, 66, -1, -1, -1, -1, 
66, -1, -1, -1, -1, -1, -1, -1, -1, 66, 66, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 67, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, 69, 69, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, 69, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 69, -1, 
68, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 70, -1, -1, 
-1, 71, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, 72, -1, -1, -1, -1, -1, -1, -1, 72, -1, -1, -
1, -1, -1, -1, -1, -1, 72, -1, -1, -1, -1, -1, 72, 72, 72, 72, -1, -1, -1, -1, 
72, -1, -1, -1, -1, -1, -1, -1, -1, 72, 72, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, 73, -1, -1, -1, -1, -1, -1, -1, 73, -1, -1, -
1, -1, -1, -1, -1, -1, 73, -1, -1, -1, -1, -1, 73, 73, 73, 73, -1, -1, -1, -1, 
73, -1, -1, -1, -1, -1, -1, -1, -1, 73, 73, -1, -1},
{-1, -1, -1, -1, 75, 75, 75, 75, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, 75, 75, -1, 75, -1, -1, -1, -1, -1, -1, -1, -1, -1, 75, 75, -1, 
75, 75, 74, 74, 74, 74, 74, 74, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, 76, 78, 77, 79, 80, 81, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, 82, -1, -1, -1, -1, -1, -1, -1, 82, -1, -1, -
1, -1, -1, -1, -1, -1, 82, -1, -1, -1, -1, -1, 82, 82, 82, 82, -1, -1, -1, -1, 
82, -1, -1, -1, -1, -1, -1, -1, -1, 82, 82, -1, -1},
{-1, -1, -1, -1, 84, 84, 84, 84, -1, -1, -1, -1, -1, -1, -1, -1, -1, 83, -1, -
1, -1, -1, -1, 84, 84, -1, 84, -1, -1, -1, -1, -1, -1, -1, -1, -1, 84, 84, -1, 
-1, 84, 84, 84, 84, 84, 84, 84, -1, 83, 83, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, 87, -1, -1, -1, -1, -1, -1, -1, 87, -1, -1, -
1, -1, -1, -1, -1, -1, 87, -1, -1, -1, -1, -1, 87, 87, 87, 87, -1, -1, -1, -1, 
87, -1, -1, -1, -1, -1, -1, -1, -1, 85, 86, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 90, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, 88, 89, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, 91, -1, -1, -1, -1, -1, -1, -1, 91, -1, -1, -
1, -1, -1, -1, -1, -1, 91, -1, -1, -1, -1, -1, 91, 91, 91, 91, -1, -1, -1, -1, 
91, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{92, -1, -1, 92, 93, 93, 93, 93, -1, -1, -1, -1, -1, -1, -1, 92, -1, 93, -1, -
1, -1, -1, -1, 93, 93, -1, 93, -1, -1, -1, -1, -1, -1, -1, -1, -1, 93, 93, -1, 
-1, 93, 93, 93, 93, 93, 93, 93, -1, 93, 93, 92, 92},
{98, -1, -1, 96, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 97, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 94, 95},
{-1, -1, -1, -1, -1, -1, -1, -1, 103, -1, -1, -1, -1, -1, -1, -1, 104, -1, -1, 
-1, -1, -1, -1, -1, -1, 102, -1, -1, -1, -1, -1, 106, 99, 100, 101, -1, -1, -
1, -1, 105, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 107, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 108, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 109, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 110, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, 111, -1, -1, -1, -1, -1, -1, -1, 111, -1, -1, 
-1, -1, -1, -1, -1, -1, 111, -1, -1, -1, -1, -1, 111, 111, 111, 111, -1, -1, -
1, -1, 111, -1, -1, -1, -1, -1, -1, -1, -1, 111, 111, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, 112, -1, -1, -1, -1, -1, -1, -1, 112, -1, -1, 
-1, -1, -1, -1, -1, -1, 112, -1, -1, -1, -1, -1, 112, 112, 112, 112, -1, -1, -
1, -1, 112, -1, -1, -1, -1, -1, -1, -1, -1, 112, 112, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 113, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -
1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 114, -1, 
115, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}
*/
}
