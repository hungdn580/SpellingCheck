/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spellcheck;

/**
 *
 * @author binh
 */
public class Check {
    public int index;
    public String sen;
    public Check(int i, String s){
        this.index=i;
        this.sen=s;
        
    }

    @Override
    public String toString() {
        return index + "-" + sen;
    }
    
}
