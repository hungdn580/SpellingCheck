/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DiacriticsRestoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author thai
 */
public class Restoration {
    private HashMap<String, Integer> tag3gram = new HashMap<String, Integer>();
    private HashMap<String, Integer> tag2gram = new HashMap<String, Integer>();
    private HashMap<String, Integer> tag1gram = new HashMap<String, Integer>();
    private HashMap<String, Integer> originalEmission = new HashMap<String, Integer>();
    private HashMap<String, Integer> extensionEmission = new HashMap<String, Integer>();
    private HashMap<String, String> lexicon = new HashMap<String, String>();
    private HashMap<String, String> abbreviation = new HashMap<String, String>();
    private HashSet<String> dictionary = new HashSet<String>();
    private HashSet<String> phrase = new HashSet<String>();
    double oo = -9999999.0;
    
    public Restoration(){
        LoadData ld = new LoadData();
        System.out.println("loading unigram ...");
        this.tag1gram = ld.loadUnigram();
        System.out.println("loading bigram ...");
        this.tag2gram = ld.loadBigram();
        System.out.println("loading trigram ...");
        this.tag3gram = ld.loadTrigram();
        System.out.println("loading original emission ...");
        this.originalEmission = ld.loadOriginalEmission();
        System.out.println("loading extension emission ...");
        this.extensionEmission = ld.loadExtensionEmission();
        System.out.println("loading lexicon ...");
        this.lexicon = ld.loadLexicon();
        ld.loadSyllable();
        System.out.println("loading abbreviation ...");
        this.abbreviation = ld.loadAbbreviation();
        System.out.println("loding words dictionary ...");
        this.dictionary = ld.loadDict("src/model/vietnamese_dictionary.txt");
    }
    
    public double transition(String tag, String tagMinus1, String tagMinus2){
        String s = tagMinus2 + " " + tagMinus1 + " " + tag;
        int t2t1tCount, t2t1Count, t1tCount, t1Count, tCount;
        if(this.tag3gram.containsKey(s))
            t2t1tCount = this.tag3gram.get(s);
        else
            t2t1tCount = 0;
        //System.out.println(t2t1tCount);
        s = tagMinus2 + " " + tagMinus1;
        if(this.tag2gram.containsKey(s))
            t2t1Count = this.tag2gram.get(s);
        else
            t2t1Count = 0;
        //System.out.println(t2t1Count);
        s = tagMinus1 + " " + tag;
        if(this.tag2gram.containsKey(s))
            t1tCount = this.tag2gram.get(s);
        else
            t1tCount = 0;
        //System.out.println(t1tCount);
        if(this.tag1gram.containsKey(tagMinus1))
            t1Count = this.tag1gram.get(tagMinus1);
        else
            t1Count = 0;
        //System.out.println(t1Count);
        if(this.tag1gram.containsKey(tag))
            tCount = this.tag1gram.get(tag);
        else
            tCount = 0;
        //System.out.println(tCount);
        return Math.log10(this.TInterpolation(t2t1tCount, t2t1Count, t1tCount, t1Count, tCount));
    }
    
    public double originalEmission(String curWord, String curTag){
        String s = curWord + " " + curTag;
        int numerator, denominator;
        if(this.originalEmission.containsKey(s))
            numerator = this.originalEmission.get(s);
        else
            numerator = 0;
        if(this.tag1gram.containsKey(curTag))
            denominator = this.tag1gram.get(curTag);
        else
            denominator = 0;
        return Math.log10(this.EInterpolation(numerator, denominator, LoadData.SAMPLE_SIZE_ORIEMISSION, LoadData.VOCAB_SIZE_ORIEMISSION));
    }
    
    public double extensionEmission(String curWord, String curTag, String preWord, String preTag){
        String s = preWord + " " + curWord + " " + preTag + " " + curTag;
        int numerator, denominator;
        if(this.extensionEmission.containsKey(s))
            numerator = this.extensionEmission.get(s);
        else
            numerator = 0;
        s = preTag + " " + curTag;
        if(this.tag2gram.containsKey(s))
            denominator = this.tag2gram.get(s);
        else
            denominator = 0;
        return Math.log10(this.EInterpolation(numerator, denominator, LoadData.SAMPLE_SIZE_EXEMISSION, LoadData.VOCAB_SIZE_EXEMISSION));
    }
    
    public double Laplace(int numerator, int denominator, int vocabSize){
        return (numerator + 0.1)*1.0/(denominator + vocabSize*0.1);
    }
    
    public double EInterpolation(int numerator, int denominator, int sampleSize, int vocabSize){
        return 0.3*(Laplace(numerator, denominator, vocabSize)) + 0.7*(1.0/sampleSize);
    }
    
    public double TInterpolation(int t2t1tCount, int t2t1Count, int t1tCount, int t1Count, int tCount){
        //System.out.println(LoadData.VOCAB_SIZE_1GRAM);
        //System.out.println(LoadData.SAMPLE_SIZE_1GRAM);
        double trigramProb = Laplace(t2t1tCount, t2t1Count, LoadData.VOCAB_SIZE_1GRAM);
        double bigramProb = Laplace(t1tCount, t1Count, LoadData.VOCAB_SIZE_1GRAM);
        double unigramProb = Laplace(tCount, LoadData.SAMPLE_SIZE_1GRAM, LoadData.VOCAB_SIZE_1GRAM);
        return 0.4*trigramProb + 0.2*bigramProb + 0.4*unigramProb;
    }
    
    //=============================================================================================//
    
    public HashMap<Integer, String> indexSentence(String str){
        HashMap <Integer, String> wordId = new HashMap<Integer, String>();
        String[] words = str.split(" ");
        int id = 1;
        for(int i=0; i<words.length; i++){
            wordId.put(id, words[i]);
            id+=1;
        }
        return wordId;
    }
    
    public ArrayList<String> getCandidate(String word){
        ArrayList<String> candidate = new ArrayList<String>();
        if(this.abbreviation.containsKey(word)){
            candidate.add(this.abbreviation.get(word));
            return candidate;
        }
        //System.out.println(this.lexicon.get("toi"));
        if(this.lexicon.containsKey(word)){
            String[] str = this.lexicon.get(word).split(" ");
            for(int i=0; i<str.length; i++){
                candidate.add(str[i]);
            }
            return candidate;
        }else{
            //Pattern pattern = Pattern.compile("(\\d+|http://.+)");
            //Matcher matcher = pattern.matcher(word);
            //if(matcher.find()){
            //    candidate.add(word);
            //    return candidate;
            //}
            if(LoadData.SYLLABLES.containsKey(word)){
                String[] str = LoadData.SYLLABLES.get(word).split(" ");
                for(int i=0; i<str.length; i++){
                    candidate.add(str[i]);
                }
                return candidate;
            }
            candidate.add(word);
            return candidate;
        }
    }
    
    public ArrayList<String> viterbiAlgorithm(String str){
        HashMap<Integer, String> wordId = this.indexSentence(str);
        int len = wordId.size();
        HashMap<String, Double> bestScore = new HashMap<String, Double>();
        HashMap<String, String> bestEdge = new HashMap<String, String>();
        
        bestScore.put("0" + "<s>", 0.0);
        ArrayList<String> candidateOf1 = this.getCandidate(wordId.get(1));
        //System.out.println(candidateOf1.size());
        for(int i=0; i<candidateOf1.size(); i++){
            //System.out.println(candidateOf1.get(i));
            double p = this.transition(candidateOf1.get(i), "<s>", "<s>") + this.originalEmission(wordId.get(1), candidateOf1.get(i));
            //System.out.println(p);
            bestScore.put("1" + candidateOf1.get(i), p);
            bestEdge.put("1" + "\\" + candidateOf1.get(i), "0" + "\\" + "<s>");
        }
        
        if(len >= 2){
            ArrayList<String> candidateOf2 = this.getCandidate(wordId.get(2));
            candidateOf1 = this.getCandidate(wordId.get(1));
            for(int i=0; i<candidateOf2.size(); i++){
                int index = 0;
                double max = oo;
                for(int j=0; j<candidateOf1.size(); j++){
                    double p = bestScore.get("1" + candidateOf1.get(j)) + this.transition(candidateOf2.get(i), candidateOf1.get(j), "<s>")
                                + this.extensionEmission(wordId.get(2), candidateOf2.get(i), wordId.get(1), candidateOf1.get(j));
                    if(p > max){
                        max = p;
                        index = j;
                    }
                   
                }
                bestScore.put("2"+candidateOf2.get(i), max);
                bestEdge.put("2"+"\\" + candidateOf2.get(i), "1"+"\\"+candidateOf1.get(index));
            }
        }
        
        for(int w=3; w<len+1; w++){
            ArrayList<String> candidateOfw = this.getCandidate(wordId.get(w));
            ArrayList<String> candidateOfw1 = this.getCandidate(wordId.get(w-1));
            for(int i=0; i<candidateOfw.size(); i++){
                int index = 0;
                double max = oo;
                for(int j=0; j<candidateOfw1.size(); j++){
                    String trace = bestEdge.get(Integer.toString(w-1)+"\\" + candidateOfw1.get(j));
                    int pos = trace.indexOf("\\");
                    String tagOfw2 = trace.substring(pos);
                    double p = bestScore.get(Integer.toString(w-1)+candidateOfw1.get(j)) + this.transition(candidateOfw.get(i), candidateOfw1.get(j), tagOfw2)+
                            this.extensionEmission(wordId.get(w), candidateOfw.get(i), wordId.get(w-1), candidateOfw1.get(j));
                    if(p>max){
                        max = p;
                        index = j;
                    }
                }
                bestScore.put(Integer.toString(w)+candidateOfw.get(i), max);
                bestEdge.put(Integer.toString(w)+"\\"+candidateOfw.get(i), Integer.toString(w-1)+"\\"+candidateOfw1.get(index));
            }
        }
        
        // last sentence
        ArrayList<String> candidate = this.getCandidate(wordId.get(len));
        int index = 0;
        double max = oo;
        for(int i=0; i<candidate.size(); i++){
            String trace = bestEdge.get(Integer.toString(len)+"\\"+candidate.get(i));
            int pos = trace.indexOf("\\");
            String preTag = trace.substring(pos+1);
            double p = bestScore.get(Integer.toString(len)+candidate.get(i))+this.transition("<e>", candidate.get(i), preTag);
            if(p>max){
                max = p;
                index = i;
            }
        }
        bestScore.put(Integer.toString(len+1)+"<e>", max);
        
        // backward step
        ArrayList<String> result = new ArrayList<String>();
        result.add(wordId.get(len)+"\\"+candidate.get(index));
        String s = bestEdge.get(Integer.toString(len)+"\\"+candidate.get(index));
        while(s != "0\\<s>"){
            int pos = s.indexOf("\\");
            result.add(wordId.get(Integer.parseInt(s.substring(0,pos))) + "\\" + s.substring(pos+1));
            s = bestEdge.get(s);
        }
        return result;
    } 
    
    public String restoration(String str){
        Tokenize tk = new Tokenize();
        str = tk.Tokenizing(str);
        ArrayList<String> taggedList = this.viterbiAlgorithm(str);
        ArrayList<String> reverseList = new ArrayList<String>();
        // reoder the tagged list
        for(int i=taggedList.size()-1; i>=0; i--){
            int pos = taggedList.get(i).indexOf("\\");
            reverseList.add(taggedList.get(i).substring(pos+1));
        }
        str = "";
        for(int i=0; i<reverseList.size(); i++){
            str += reverseList.get(i)+" ";
        }
        return str.trim();
    }
}
