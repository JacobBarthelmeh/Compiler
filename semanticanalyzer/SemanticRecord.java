/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semanticanalyzer;

import compiler.Token;
import symboltable.Symbol;
import util.Type;

/**
 *
 * @author sweetness
 */
public class SemanticRecord implements Record {

    Token token;
    Symbol sym;
    Type typ;
    Integer nest;
    String opp;

    @Override
    public Type getType() {
        if (typ == null) {
            if (sym == null) {
                System.out.println("Error type not set");
            } else {
                typ = sym.type;
            }
        }
        return typ;
    }

    @Override
    public String getRegister() {
        if (sym == null || nest == null) {
            System.out.println("Error with get register call\n"
                    + "Either sym or nest is null");
        }
        return sym.offset + "(D" + nest + ")";
    }

    @Override
    public void setToken(Token in) {
        token = in;
    }

    @Override
    public void setSymbol(Symbol s, int nesting) {
        sym = s;
        nest = nesting;
    }

    @Override
    public void setOpp(String in) {
        opp = in;
    }

    @Override
    public String getOpp() {
        if (opp == null) {
            System.out.println("Error opperation was not set!!!");
        }
        return opp;
    }

    @Override
    public void setType(Type in) {
        typ = in;
    }
}
