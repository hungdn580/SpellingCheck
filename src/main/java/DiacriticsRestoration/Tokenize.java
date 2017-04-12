/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DiacriticsRestoration;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author thai
 */
public class Tokenize {
    private String[] specialReg = {
        "http://[^\"]+",
        "\\d+\\.\\d+\\.\\d+",
        "\\d+\\,\\d+\\,\\d+",
        "\\d+\\.\\d+",
        "\\d+\\,\\d+",
        "\\d+\\-\\d+\\-\\d+",
        "\\d+\\/\\d+\\/\\d+",
        "\\d+\\-\\d+",
        "\\d+\\/\\d+",
        "\\w+\\.(com|vn|org)"
    };
    private String[] markList = {
        "[", "]", ",", ".", "/", ";", "'", "-", "=", "!", "@", "$", "#", "%",
        "^", "&", "*", "(", ")", "\\", "\"", ":", "<", ">", "?", "{", "}", "“",
        "”", "…", "..."
    };
    private HashSet<String> marks = new HashSet();
    
    public Tokenize(){
        for(int i=0; i<this.markList.length;i++){
            this.marks.add(this.markList[i]);
        }
    }
    
    public String signNomalize(String s){
         for(int i=0;i<this.specialReg.length;i++){
             Pattern pattern = Pattern.compile(this.specialReg[i]);
             Matcher matcher = pattern.matcher(s);
             if(matcher.find()){
                 String str = matcher.group();
                 //System.out.println(str);
                 if(str.length() < s.length()){
                     s = s.replace(str, " " + str + " ");
                 }
                 return s;
             }
         }
         if(this.marks.contains(s)){
             return s;
         }
         for(int i=0;i<this.markList.length;i++){
             if (s.contains(this.markList[i])){
                 s = s.replace(this.markList[i], " " + this.markList[i] + " ");
             }
         }
         s = s.replace("\\s+", " ");
         return s.trim();
    }
    
    public String Tokenizing(String s){
        String[] tokens = s.split(" ");
        s = "";
        for(int i=0; i<tokens.length; i++){
            String newStr = this.signNomalize(tokens[i]);
            String[] newTokens = newStr.split(" ");
            for(int j=0; j<newTokens.length; j++){
                s += newTokens[j] + " ";
            }
        }
        return s.trim();
    }
}
