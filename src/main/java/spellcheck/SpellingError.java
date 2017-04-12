/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spellcheck;

/**
 *
 * @author binhdangtran
 */
public class SpellingError {
    private int startPos;
    private int endPos;
    private String alternativeWord;
    SpellingError(int a, int b, String s){
        this.startPos=a;
        this.endPos=b;
        this.alternativeWord=s;
    }
    public String getout(){
        System.out.println(this.startPos+","+this.endPos+","+this.alternativeWord);
        return null;
    }

    public String toString() {
        return this.startPos+","+this.endPos+","+this.alternativeWord;
    }
    
}
