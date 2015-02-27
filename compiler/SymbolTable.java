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
public class SymbolTable {
    
    /**
     * Create new table and return it's object
     * @return 
     */
    public STable addTable(){
        return new STable();
    }
    
    private class STable{
        String lb;
        public String getLable(){
            return lb;
        }
    }
    
}
