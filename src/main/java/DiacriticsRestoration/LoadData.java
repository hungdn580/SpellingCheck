/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DiacriticsRestoration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author thai
 */
public class LoadData {
    public static HashMap<String, String> SYLLABLES = new HashMap<String, String>();
    public static int SAMPLE_SIZE_1GRAM = 1112464;
    public static int VOCAB_SIZE_1GRAM = 0;
    public static int SAMPLE_SIZE_ORIEMISSION = 1005343;
    public static int VOCAB_SIZE_ORIEMISSION = 13325;
    public static int SAMPLE_SIZE_EXEMISSION = 968967;
    public static int VOCAB_SIZE_EXEMISSION = 247441;
    
    public HashMap<String, Integer> loadUnigram(){
        HashMap<String, Integer> unigram = new HashMap<String, Integer>();
        try{
            BufferedReader br = new BufferedReader(new FileReader("src/model/tag1gram.txt"));
            String s = "";
            while((s=br.readLine())!= null){
                String[] tokens = s.split(" ");
                if(tokens.length == 2){
                    unigram.put(tokens[0], Integer.parseInt(tokens[1]));
                    //SAMPLE_SIZE_1GRAM += Integer.parseInt(tokens[1]);
                }
            }
            VOCAB_SIZE_1GRAM = unigram.size()-2;
        }catch(IOException ex){
            ex.printStackTrace();
        }catch(NumberFormatException ex){
            ex.printStackTrace();
        }
        return unigram;
    }
    
    public HashMap<String, Integer> loadBigram(){
        HashMap<String, Integer> bigram = new HashMap<String, Integer>();
        try{
            BufferedReader br = new BufferedReader(new FileReader("src/model/tag2gram.txt"));
            String s = "";
            while((s=br.readLine()) != null){
                String[] tokens = s.split(" ");
                bigram.put(tokens[0] + " " + tokens[1], Integer.parseInt(tokens[2]));
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }catch(NumberFormatException ex){
            ex.printStackTrace();
        }
        return bigram;
    }
    
    public HashMap<String, Integer> loadTrigram(){
        HashMap<String, Integer> trigram = new HashMap<String, Integer>();
        try{
            BufferedReader br = new BufferedReader(new FileReader("src/model/tag3gram.txt"));
            String s = "";
            while((s=br.readLine()) != null){
                String[] tokens = s.split(" ");
                trigram.put(tokens[0] + " " + tokens[1] + " " + tokens[2], Integer.parseInt(tokens[3]));
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }catch(NumberFormatException ex){
            ex.printStackTrace();
        }
        return trigram;
    }
    
    public HashMap<String, Integer> loadOriginalEmission(){
        HashMap<String, Integer> oriEmission = new HashMap<String, Integer>();
        try{
            BufferedReader br = new BufferedReader(new FileReader("src/model/originalEmission.txt"));
            String s = "";
            while((s=br.readLine()) != null){
                String[] tokens = s.split(" ");
                if(tokens.length == 3){
                    oriEmission.put(tokens[0] + " " + tokens[1], Integer.parseInt(tokens[2]));
                    //SAMPLE_SIZE_ORIEMISSION += Integer.parseInt(tokens[2]);
                }
            }
            //VOCAB_SIZE_ORIEMISSION = oriEmission.size();
        }catch(IOException ex){
            ex.printStackTrace();
        }catch(NumberFormatException ex){
            ex.printStackTrace();
        }
        return oriEmission;
    }
    
    public HashMap<String, Integer> loadExtensionEmission(){
        HashMap<String, Integer> exEmission = new HashMap<String, Integer>();
        try{
            BufferedReader br = new BufferedReader(new FileReader("src/model/extensionEmission.txt"));
            String s = "";
            while((s=br.readLine()) != null){
                String[] tokens = s.split(" ");
                if(tokens.length == 5){
                    exEmission.put(tokens[0] + " " + tokens[1] + " " + tokens[2] + tokens[3], Integer.parseInt(tokens[4]));
                    //SAMPLE_SIZE_EXEMISSION += Integer.parseInt(tokens[4]);
                }
            }
            //VOCAB_SIZE_EXEMISSION = exEmission.size();
        }catch(IOException ex){
            ex.printStackTrace();
        }catch(NumberFormatException ex){
            ex.printStackTrace();
        }
        return exEmission;
    }
    
    public HashMap<String, String> loadLexicon(){
        HashMap<String, String> lexicon = new HashMap<String, String>();
        try{
            BufferedReader br = new BufferedReader(new FileReader("src/model/lexicon.txt"));
            String s = "";
            while((s=br.readLine()) != null){
                int pos = s.indexOf(" ");
                lexicon.put(s.substring(0,pos), s.substring(pos+1));
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }catch(NumberFormatException ex){
            ex.printStackTrace();
        }
        return lexicon;
    }
    public HashMap<String, String> loadAbbreviation(){
        HashMap<String, String> abb = new HashMap<String, String>();
        try{
            BufferedReader br = new BufferedReader(new FileReader("src/model/standard.txt"));
            String s = "";
            while((s=br.readLine()) != null){
                String[] tokens = s.split("/");
                abb.put(tokens[0], tokens[1]);
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }catch(NumberFormatException ex){
            ex.printStackTrace();
        }
        return abb;
    }
    
    public HashMap<String, String> loadSyllable(){
        try{
            BufferedReader br = new BufferedReader(new FileReader("src/model/SyllableLexicon.txt"));
            String s = "";
            while((s = br.readLine()) != null){
                int pos = s.indexOf(" ");
                SYLLABLES.put(s.substring(0,pos), s.substring(pos+1));
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
        return SYLLABLES;
    }
    
    public HashSet<String> loadDict(String inputFile){
        HashSet<String> dictionary = new HashSet<String>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            String s = "";
            while((s = br.readLine()) != null){
                dictionary.add(s);
            }
            br.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
        return dictionary;
    }
}
