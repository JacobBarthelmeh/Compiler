/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

/**
 *
 * @author sweetness
 */
public class Parser {

    /**
     * See if a token is a match
     *
     * @param in the current token being looked at
     * @param toMatch the token compared to
     * @return true on match
     */
    private boolean match(Token in, Token toMatch) {
        if (in == null || toMatch == null) {
            System.err.println("Invalid input to parser match function");
            return false;
        }
        if (in.getID() == toMatch.getID()) {
            return true;
        } else {
            return false;
        }
    }
    
    
    /**
     * handle error print out
     * @param err token that caused the error
     * @param expected array of expected tokens
     */
    private void error(Token err, Token[] expected) {
        if (err == null || expected == null){
            System.err.println("Improper input to parser error function");
        }
        System.err.println("Error found " + err.getContents() + " " + 
                err.getID() + " at line " + err.getLine() + " col " + err.getCol());
        System.err.print("Was expecting ");
        for (int i = 0; i < expected.length;i++) {
            System.err.print(", " + expected[i].getID());
        }
    }

    //stubs for rules 40-47 
    private void emptyStatement() {

    }

    //**************************************************************************
    //stubs for rules 78 - 150
}
