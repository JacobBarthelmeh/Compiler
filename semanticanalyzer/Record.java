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
public interface Record {

    
    /**
     * Set operation to be used if needed, for example ADD, SUB, ADDS, MUL ....
     *
     * @param in String of operation to perform
     */
    public void setOpp(String in);

    /**
     * Returns the string repesentation of operation such as ADD, MULS ....
     *
     * @return
     */
    public String getOpp();

    /**
     * Get the type
     *
     * @return Type that is things such as integer, string, float ......
     */
    public Type getType();

    /**
     * Gets the string formated for the operation
     *
     * @return string ... something for example like 1(D0)
     */
    public String getRegister();

    /**
     * Set the token, this can be used for error output
     *
     * @param in Token
     */
    public void setToken(Token in);

    /**
     * Set the symbol, the type is used from this
     *
     * @param s Symbol table object
     * @param nesting nesting level for when getting register
     */
    public void setSymbol(Symbol s, int nesting);

    /**
     * To set the type of the record
     *
     * @param in Type
     */
    public void setType(Type in);
}
