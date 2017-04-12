/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spellcheck;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import com.example.model.Suggest;
import spellcheck.Check.*;

;

/**
 *
 * @author TUNG
 */
public class SpellChecker {

    private static final double kk = 0.7;
    private static final double mm = 0.3;
    private static final double smooth = 0.01;
//    private static final int title_length = 60;
//    private static final int total = 347438011;
//Bảng đánh dấu các kí tự có dấu hay lỗi với nhau
//    private static final int sign_check[] = {1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13, 14, 14, 15, 15, 16, 16, 17, 17, 18, 18, 19, 19, 20, 20, 21, 21, 22, 22, 23, 23, 24, 24};
    //Spellcheck variables
    boolean symbolMark1, symbolMark2, symbolMark3, symbolMark4;
    String[] commonEnglishWord;
    String[] syllables;
    String[] vietdict;
    Unigram[] unigramDictionary;
    Bigram[] bigramDictionary;
    Trigram[] trigramDictionary;
    UnigramExtractsign[] unigram_extractsign_dict;
    private static final int bigramDictionarySize = 1208943;
    private static final int trigramDictionarySize = 4886364;
    private static final int unigramDictionarySize = 6771;
    private static final int vietdictSize = 56486;
    double itCondition;
    
    public String namefile;

    public SpellChecker(String a) throws Exception {
        this.namefile = a;
        this.symbolMark1 = false;
        this.symbolMark2 = false;
        this.symbolMark3 = false;
        this.symbolMark4 = false;
        this.itCondition = 1.325;
        this.Init();
        
    }

//
////convert a word into its Telex keyboard list. "biên" -> "bieen"
//void full_extract_sign(String& word)
//{
//    //remove sign from vowel
//    for(int i = 0;i < word.length();i++)
//    {
//        int ind = source.indexOf(word[i]);
//        if(ind >=0 && ind<_length)
//        {
//            word[i] = dest[ind];
//            word+=sign[ind];
//            break;
//        }
//    }
//
//    //convert vietnamese only character
//    int vn_count = 0;
//    String temp_str = word;
//    for(int i = 0;i < word.length();i++)
//    {
//        int index = vnCharacter.indexOf(word[i]);
//        if(index >= 0 && index < _length_str)
//        {
//            temp_str.replace(i+vn_count,1,vnCharacterExtractsign[index]);
//            vn_count++;
//            //wprintf("%s\n",word.c_str());
//            //wcout<<" "<<vnCharacterExtractsign[index]<<" ";
//        }  
//    }
//    word = temp_str;
//    //cout<<endl;
//    return;
//}
    //search for index of word in extractsign_dict
    int unigramExtractsignSearch(String word, int first, int last) {
        if (last < first) {
            return -1;
        }
        int mid = (first + last) / 2;
        if (word.equals(unigram_extractsign_dict[mid].text)) {
            return mid;
        }
        if (word.compareTo(unigram_extractsign_dict[mid].text) < 0) {
            return unigramExtractsignSearch(word, first, mid - 1);
        } else {
            return unigramExtractsignSearch(word, mid + 1, last);
        }
    }

    void typingError(String text, int unigram_str_length, List<String> result, List<Integer> type) {
        String text_extract = Utility.extractsign(text);

        int length = text_extract.length();
        for (int i = 0; i <= length; i++) {
            for (char a = 'a'; a != 'z'; a++) {
                StringBuilder tmp = new StringBuilder(text_extract);
                result.add(tmp.insert(i, a).toString());
                type.add(1);
            }
        }
        for (int i = 0; i < length; i++) {
            StringBuilder tmp = new StringBuilder(text_extract);
            tmp = tmp.delete(i, i + 1);
            result.add(tmp.toString());
            type.add(2);
            //wprintf("%s\n",temp_extract.c_str());
            int temp1 = -1;
            int temp2 = 3;
            //wprintf("%c\n",text_extract[i]);
            for (int j = 0; j < 3; j++) {
                temp1 = StringConstant.keyboard[j].indexOf(text_extract.charAt(i));
                if (temp1 >= 0 && temp1 < StringConstant.keyboard[j].length()) {
                    if (StringConstant.keyboard[j].charAt(temp1) == text_extract.charAt(i)) {
                        //wprintf("%c %c\n",text_extract[i],keyboard[j][temp1]);
                        temp2 = j;
                        break;
                    }
                }

            }
            //cout << temp1 <<" "<< temp2 << endl;
            if (temp2 < 3) {
                if (temp1 - 1 >= 0) {
                    StringBuilder temp = new StringBuilder(tmp);
                    temp.insert(i, StringConstant.keyboard[temp2].charAt(temp1 - 1));
                    result.add(temp.toString());
                    type.add(3);
//                    System.out.println("typing: "+temp);
                }
                if (temp1 + 1 < StringConstant.keyboard[temp2].length()) {
                    StringBuilder temp = new StringBuilder(tmp);
                    temp.insert(i, StringConstant.keyboard[temp2].charAt(temp1 + 1));
                    result.add(temp.toString());
                    type.add(3);
//                    System.out.println("typing: "+temp);
                }
            }
        }
    }

// lỗi âm tiết cuối
    void replaceEndError(List<String> temp, String text, List<Integer> index_error, List<Integer> index_replace) {
        temp.clear();
        index_error.clear();
        index_replace.clear();
        //		if (text.substr(text.length()-a.length(),a.length()).compare(a)==0);
        for (int i = 0; i < StringConstant.endErrorLength; i++) {
            if (text.length() > StringConstant.end_error[i].length()) {
                if (text.substring(text.length() - StringConstant.end_error[i].length(), text.length()).compareTo(StringConstant.end_error[i]) == 0) {
                    for (int j = i + 1; j < i + 4; j++) {
                        if ((j >= 0) && (j < StringConstant.endErrorLength)) {
                            if (StringConstant.endErrorCheck[i] == StringConstant.endErrorCheck[j]) {
                                StringBuilder strtemp = new StringBuilder(text);
                                strtemp = strtemp.delete(text.length() - StringConstant.end_error[i].length(), text.length());
                                strtemp = strtemp.append(StringConstant.end_error[j]);
                                temp.add(strtemp.toString());
                                index_error.add(i);
                                index_replace.add(j);
                            } else {
                                break;
                            }
                        }
                    }
                    for (int j = i - 1; j > i - 4; j--) {
                        if ((j >= 0) && (j < StringConstant.endErrorLength)) {
                            if (StringConstant.endErrorCheck[i] == StringConstant.endErrorCheck[j]) {
                                StringBuilder strtemp = new StringBuilder(text);
                                strtemp = strtemp.delete(text.length() - StringConstant.end_error[i].length(), text.length());
                                strtemp = strtemp.append(StringConstant.end_error[j]);
                                temp.add(strtemp.toString());
                                index_error.add(i);
                                index_replace.add(j);
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
// Kiểm tra 1 âm tiết có mắc phải lỗi âm cuối hay không

    boolean checkEnd(String text, int i, int j, String syllable[], String eng_dict[]) {
        if (text.compareTo("") == 0) {
            return false;
        }
        if (text.length() < StringConstant.end_error[i].length()) {
            return false;
        }
        if (text.substring(text.length() - StringConstant.end_error[i].length(), text.length()).compareTo(StringConstant.end_error[i]) != 0) {
            return false;
        }
        StringBuilder temp = new StringBuilder(text);
        temp.delete(text.length() - StringConstant.end_error[i].length(), text.length());
        temp.append(StringConstant.end_error[j]);
        if (Utility.myBinarySearch(eng_dict, text, 0, 73) != -1 || Utility.myBinarySearch(syllable, text, 0, 6691) != -1) {
            return true;
        } else {
            return false;
        }
    }

// Lỗi âm tiết đầu
    void replaceStartError(List<String> temp, String text, List<Integer> index_error, List<Integer> index_replace) {
        temp.clear();
        index_error.clear();
        index_replace.clear();
        for (int i = 0; i < StringConstant.startErrorLength; i++) {
            if (text.indexOf(StringConstant.startError[i]) >= 0 || text.indexOf(StringConstant.startError[i]) < text.length()) {
                if (text.length() >= StringConstant.startError[i].length()) {
                    if (text.substring(0, StringConstant.startError[i].length()).compareTo(StringConstant.startError[i]) == 0) {
//                        System.out.println("replace: "+startError[i]);
                        for (int j = i + 1; j < i + 4; j++) {
                            if ((j >= 0) && (j < StringConstant.startErrorLength)) {
                                if (StringConstant.startErrorCheck[i] == StringConstant.startErrorCheck[j]) {
                                    StringBuilder strtemp = new StringBuilder(text);
//                                    System.out.println("init: "+strtemp);
                                    strtemp = strtemp.delete(0, StringConstant.startError[i].length());
//                                    System.out.println("delete: "+strtemp+", delete length: "+startError[i].length());
                                    strtemp = strtemp.insert(0, StringConstant.startError[j]);
//                                    System.out.println("insert: "+strtemp);
                                    temp.add(strtemp.toString());
                                    index_error.add(i);
                                    index_replace.add(j);
                                } else {
                                    break;
                                }
                            }
                        }
                        for (int j = i - 1; j > i - 4; j--) {
                            if ((j >= 0) && (j < StringConstant.startErrorLength)) {
                                if (StringConstant.startErrorCheck[i] == StringConstant.startErrorCheck[j]) {
                                    StringBuilder strtemp = new StringBuilder(text);
//                                    System.out.println("init: "+strtemp);
                                    strtemp = strtemp.delete(0, StringConstant.startError[i].length());
//                                    System.out.println("delete: "+strtemp+", delete length: "+startError[i].length());
                                    strtemp = strtemp.insert(0, StringConstant.startError[j]);
                                    temp.add(strtemp.toString());
//                                    System.out.println("insert: "+strtemp);
                                    /*wprintf(" replace_startError, add %s\n",strtemp.c_str());
                                     if(strtemp == "đang")
                                     cout<<"great!"<<endl;*/
                                    index_error.add(i);
                                    index_replace.add(j);
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
// Kiểm tra 1 âm tiết có mắc phải lỗi âm đầu hay không

    boolean checkStart(String text, int i, int j, String syllable[], String eng_dict[]) {
        if (text.compareTo("") == 0) {
            return false;
        }
        if (text.length() < StringConstant.startError[i].length()) {
            return false;
        }
        if (text.substring(0, StringConstant.startError[i].length()).compareTo(StringConstant.startError[i]) != 0) {
            return false;
        }
        StringBuilder temp = new StringBuilder(text);
        temp.delete(0, StringConstant.startError[i].length());
        temp.insert(0, StringConstant.startError[i]);
        //if (unigram.indict(temp,unigram.start[(int) temp[0]],unigram.end[(int) temp[0]]) == 0) return false;
        if (Utility.myBinarySearch(eng_dict, text, 0, 73) != -1 || Utility.myBinarySearch(syllable, text, 0, 6691) != -1) {
            return true;
        } else {
            return false;
        }
    }

//lỗi từ địa phương
    void localError(List<String> temp, String text) {
        temp.clear();
        for (int i = 0; i < StringConstant.localErrorLength; i++) {
            if (text.length() > StringConstant.localCheck[i].length()) {
                if (text.substring(text.length() - StringConstant.localCheck[i].length(), text.length()).compareTo(StringConstant.localCheck[i]) == 0) {
                    StringBuilder texttemp = new StringBuilder(text);
                    texttemp = texttemp.delete(text.length() - StringConstant.localCheck[i].length(), text.length());
                    texttemp = texttemp.append(StringConstant.localReplace[i]);
//                    System.out.println(texttemp);
                    temp.add(texttemp.toString());
                    break;
                }
            }
        }
    }

//// kiểm tra 2 xâu có phải là hoán vị của nhau ko
//boolean hoanvi(String text1,String text2)
//{
//    char temp;
//    for (int i = 0;i<text1.length()-1;i++)
//        for (int j = i+1;j<text1.length();j++)
//        {
//            if (text1[i]>text1[j])
//            {
//                temp = text1[i];
//                text1[i] = text1[j];
//                text1[j] = temp;
//            }
//            if (text2[i]>text2[j])
//            {
//                temp = text2[i];
//                text2[i] = text2[j];
//                text2[j] = temp;
//            }
//        }
//        if (text1.compare(text2) == 0) return true;
//        return false;
//}
//
// Lỗi dấu
    void replaceSignError(List<String> temp, String text) {
        temp.clear();
        String text1 = Utility.extractsign(text);
        //wcout<<" text1: "<<text1<<endl;
        int n = text1.length();
        if (StringConstant.signError.indexOf(text1.charAt(n - 1)) >= 0 && StringConstant.signError.indexOf(text1.charAt(n - 1)) < StringConstant.signErrorLength) {
            text1 = text1.substring(0, n - 1);
            //temp.add(text1);
        }
        for (int i = 0; i < StringConstant.signErrorLength; i++) {
            String text2 = text1 + StringConstant.signError.charAt(i);
            temp.add(text2);
        }
    }
    //Checkspelling region
//
//

    int unigramSearch(String word) {
        if (word.compareTo("NUMBER") == 0) {
            return 6766;
        } else if (word.compareTo("DATE") == 0) {
            return 6767;
        } else if (word.compareTo("EMAIL") == 0) {
            return 6768;
        } else if (word.compareTo("WEBSITE") == 0) {
            return 6769;
        } else if (word.compareTo("FOREIGNWORD") == 0) {
            return 6770;
        } else if (word.compareTo("<s>") == 0) {
            return 6771;
        } else if (word.compareTo("</s>") == 0) {
            return 6772;
        }
        int index = Utility.myBinarySearch(commonEnglishWord, word, 0, 73);
        if (index != -1) {
            return 6692 + index;
        } else {
            index = Utility.myBinarySearch(syllables, word, 0, 6691);
            if (index != -1) {
                return index;
            }
        }
        return -1;
    }
//get the word at position index in dictionary

    String getstring(int index) {
        if (index == 6766) {
            return "NUMBER";
        } else if (index == 6767) {
            return "DATE";
        } else if (index == 6768) {
            return "EMAI";
        } else if (index == 6769) {
            return "WEBSITE";
        } else if (index == 6770) {
            return "FOREIGNWORD";
        } else if (index == 6771) {
            return "<s>";
        } else if (index == 6772) {
            return "</s>";
        }
        if (index < 6692) {
            return syllables[index];
        } else {
            return commonEnglishWord[index - 6692];
        }
    }

    void makeDict() throws IOException {
        String[] array = new String[7000000];
        Scanner s = null;
        int count = 0;
        try {
            s = new Scanner(new File("E:/test/newDict/trigram_test.txt"));
            File outFile = new File("E:/test/newDict/trigram_test_int.txt");
            FileWriter wr = new FileWriter(outFile);

            String tmp;
            tmp = s.nextLine();
            while (s.hasNextLine()) {
                tmp = s.nextLine();
//                System.out.println(tmp);
                String[] split = tmp.split(" ");
                wr.append(unigramSearch(split[0]) + " " + unigramSearch(split[1]) + " " + unigramSearch(split[2]) + " " + split[3] + "\n");
            }
            wr.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (s != null) {
                s.close();
            }
        }

    }

    int trigramSearch(String text, int first, int last) {
        if (last < first) {
            return -1;
        }
        int mid = (first + last) / 2;
        //cout<<"trigram_frequency, first: "<<first<<", last: "<<last<<endl;
        String midText = getstring(trigramDictionary[mid].first) + " " + getstring(trigramDictionary[mid].second) + " " + getstring(trigramDictionary[mid].last);
        if (midText.compareTo(text) == 0) {
            return mid;
        }
        if (text.compareTo(midText) < 0) {
            return trigramSearch(text, first, mid - 1);
        } else {
            return trigramSearch(text, mid + 1, last);
        }
    }

//get frequency of a unigram
    int getUnigramFrequency(String text, int first, int last) {
        int index = unigramSearch(text);
        if (index != -1) {
            return unigramDictionary[index].frequency;
        } else {
            return 0;
        }
    }

//get frequency of a bigram
    int getBigramFrequency(String text, int first, int last) {
        if (last < first) {
            return 0;
        }
        int mid = (first + last) / 2;
        String midText = getstring(bigramDictionary[mid].first) + " " + getstring(bigramDictionary[mid].last);
        if (midText.compareTo(text) == 0) {
            return bigramDictionary[mid].frequency;
        }
        if (text.compareTo(midText) < 0) {
            return getBigramFrequency(text, first, mid - 1);
        } else {
            return getBigramFrequency(text, mid + 1, last);
        }
    }

//get frequency of a trigram
    int getTrigramFrequency(String text, int first, int last) {
        if (last < first) {
            return 0;
        }
        int mid = (first + last) / 2;
        //cout<<"trigram_frequency, first: "<<first<<", last: "<<last<<endl;
        String midText = getstring(trigramDictionary[mid].first) + " " + getstring(trigramDictionary[mid].second) + " " + getstring(trigramDictionary[mid].last);
        if (midText.compareTo(text) == 0) {
            return trigramDictionary[mid].frequency;
        }
        if (text.compareTo(midText) < 0) {
            return getTrigramFrequency(text, first, mid - 1);
        } else {
            return getTrigramFrequency(text, mid + 1, last);
        }
    }

//load the dictionary files
    void dictionaryInit() throws Exception {
        Scanner s;
        int syllable_size, count;
        commonEnglishWord = new String[74];
        count = 0;
        s = null;
        try {
            s = new Scanner(new BufferedReader(new FileReader(namefile+"Englishdict.txt")));

            while (s.hasNext()) {
                commonEnglishWord[count] = s.next().toString();
                count++;
            }
            System.out.println("commonEnglishWord_size: " + count);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (s != null) {
                s.close();
            }
        }

        try {
            s = new Scanner(new BufferedReader(new FileReader(this.namefile+"syllables.txt")));

            syllable_size = s.nextInt();
            syllables = new String[syllable_size];
            for (int i = 0; i < syllable_size; i++) {
                syllables[i] = s.next();
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

//load unigram dictionary
    void unigramInit() throws Exception {
        Scanner s;
        int count = 0;

        s = null;
        unigramDictionary = new Unigram[unigramDictionarySize + 1];
        try {
            s = new Scanner(new BufferedReader(new FileReader(this.namefile+"unigram_int.txt")));

            String tmp;
            tmp = s.next();

            while (s.hasNextLine()) {
                unigramDictionary[count] = new Unigram();
                unigramDictionary[count].data = s.nextShort();
                unigramDictionary[count].frequency = s.nextInt();
                count++;
            }
            System.out.println("unigram_size: " + count);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

//load bigram dictionary
    void bigramInit() throws Exception {
        BufferedReader r;
        r = null;
        int count = 0;
        bigramDictionary = new Bigram[bigramDictionarySize];
        try {
            r = new BufferedReader(new FileReader(this.namefile+"bigram_int.txt"));
            String tmp;
            tmp = r.readLine();
            while ((tmp = r.readLine()) != null) {
                bigramDictionary[count] = new Bigram();
                String[] tmpToken = tmp.split(" ");
                bigramDictionary[count].first = Short.parseShort(tmpToken[0]);
                bigramDictionary[count].last = Short.parseShort(tmpToken[1]);
                bigramDictionary[count].frequency = Integer.parseInt(tmpToken[2]);
                count++;
            }
            System.out.println("bigramDictionarySize: " + count);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (r != null) {
                r.close();
            }
        }
    }

    void printCode() throws Exception {
        BufferedReader r;
        r = null;
        int count = 0;
        bigramDictionary = new Bigram[bigramDictionarySize];
        try {
            r = new BufferedReader(new FileReader(this.namefile+"bigram_int.txt"));
            String tmp;
            tmp = r.readLine();
            File outFile = new File("E:/code_bigram.txt");
            FileWriter wr = new FileWriter(outFile);
            while ((tmp = r.readLine()) != null) {
                bigramDictionary[count] = new Bigram();
                String[] tmpToken = tmp.split(" ");

                wr.append("bigramDictionary[" + count + "].first = " + Short.parseShort(tmpToken[0]) + ";"
                        + "bigramDictionary[" + count + "].last = " + Short.parseShort(tmpToken[1]) + ";"
                        + "bigramDictionary[" + count + "].frequency = " + Integer.parseInt(tmpToken[2]) + ";\n");
                count++;
            }
            wr.close();
            System.out.println("bigramDictionarySize: " + count);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (r != null) {
                r.close();
            }
        }
    }

//load trigram dictionary
    void trigramInit() throws Exception {
        int count;
        BufferedReader r;
        r = null;
        trigramDictionary = new Trigram[trigramDictionarySize];
        count = 0;
        try {
            r = new BufferedReader(new FileReader(this.namefile+"trigram_int.txt"));
            String tmp;
            tmp = r.readLine();
            while ((tmp = r.readLine()) != null) {
                trigramDictionary[count] = new Trigram();
                String[] tmpToken = tmp.split(" ");
                trigramDictionary[count].first = Short.parseShort(tmpToken[0]);
                trigramDictionary[count].second = Short.parseShort(tmpToken[1]);
                trigramDictionary[count].last = Short.parseShort(tmpToken[2]);
                trigramDictionary[count].frequency = Integer.parseInt(tmpToken[3]);
                count++;
            }
            System.out.println("trigramDictionarySize: " + count);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (r != null) {
                r.close();
            }
        }
    }

//load unigram_extractsign dictionary
    void unigramExtractsignInit() throws Exception {
        Scanner s;
        int count;
        unigram_extractsign_dict = new UnigramExtractsign[unigramDictionarySize];
        s = null;
        count = 0;
        try {
            s = new Scanner(new BufferedReader(new FileReader(this.namefile+"unigram_extractsign.txt")));
            String tmp;
            tmp = s.next();

//            System.out.println("extract_size read from file: " + s.next());
            for (int i = 0; i < unigramDictionarySize; i++) {
//                System.out.println(s.next()+" "+s.nextInt());
                unigram_extractsign_dict[i] = new UnigramExtractsign();
                unigram_extractsign_dict[i].text = s.next();
                unigram_extractsign_dict[i].index = s.nextInt();
            }
//            System.out.println("unigram_extractsign_size: " + count);
//            for(int i = 0;i < unigramDictionarySize;i++)
//                System.out.println(unigram_extractsign_dict[i].text+" "+unigram_extractsign_dict[i].index);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

//load vietnamese dictionary
    void vietdictInit() throws Exception {
        Scanner s;
        int count;
        vietdict = new String[vietdictSize];
        s = null;
        count = 0;
        try {
            s = new Scanner(new BufferedReader(new FileReader(this.namefile+"vietdict.txt")));
            int tmp;
            tmp = s.nextInt();
            System.out.println("vietdictSize read from file: " + tmp);
            s.nextLine();
            while (s.hasNextLine()) {
                vietdict[count] = s.nextLine();
                count++;
            }
            System.out.println("vietdictSize: " + count);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
//init all dictionary 

    private void Init() throws Exception {
        dictionaryInit();
        vietdictInit();
        unigramExtractsignInit();
        unigramInit();
        bigramInit();
        trigramInit();
    }

    void totalError(List<Integer> error, List<Double> errorCost, String text, String textFirst, String textLast) {
        error.clear();
        errorCost.clear();
        List<String> temp;
        List<Integer> error_type;
        int count1 = 0, count2 = 0; // count (AB) and (BC)
        int count_temp1, count_temp2;
        int index, index2;
        index = 0;
        index2 = 0;

        temp = new ArrayList<>();
        error_type = new ArrayList<>();
        // tinh count
        int l = unigramSearch(text);

        if (l != -1) {
            error.add(l);
            errorCost.add(0.0);
        }

//        System.out.println("total error: "+textFirst+" "+text+" "+textLast);
        /*if (regex_match(text,numbercheck) || text.find("°") != string::npos || text.find("%") != string::npos)
         return;
         if(regex_match(text,date))
         return;
         if(regex_match(text,emailcheck))
         return;
         if( regex_match(text,webcheck))
         return;
         if (isSymbol(text[0]))
         return;*/
//
        if (textLast.compareTo("") != 0) {
            count2 = count2 + getBigramFrequency(text + " " + textLast, 0, bigramDictionarySize - 1);
        }
        if (textFirst.compareTo("") != 0) {
            count1 = count1 + getBigramFrequency(textFirst + " " + text, 0, bigramDictionarySize - 1);
        }

        //cout<<"count: "<<count<<endl;
        // Loi danh may
        typingError(text, 6771, temp, error_type);
//        System.out.println("loi danh may");
        for (int i = 0; i < temp.size(); i++) {
            int t;
//            System.out.println(temp.get(i));
            t = unigramExtractsignSearch(temp.get(i), 0, 6691);
            //wcout<<" "<<(*it)<<endl;
            if (t > -1) {
//                System.out.println("candidate: "+getstring(unigram_extractsign_dict[t].index));
                if (textLast.compareTo("") != 0) {
                    if (Utility.myBinarySearch(vietdict, getstring(unigram_extractsign_dict[t].index) + " " + textLast, 0, vietdictSize - 1) != -1) {
                        if (!error.contains(unigram_extractsign_dict[t].index)) {
                            error.add(unigram_extractsign_dict[t].index);
                            switch (error_type.get(i)) {
                                case 1:
                                    errorCost.add(1.65);
                                    break;
                                case 2:
                                    errorCost.add(2.0);
                                    break;
                                case 3:
                                    errorCost.add(2.0);
                                    break;
                            }
//                            System.out.println(" add new typing error 1: " + unigram_extractsign_dict[t].index);
                            continue;
                        }
                    }
                }
                if (textFirst.compareTo("") != 0) {
                    if (Utility.myBinarySearch(vietdict, textFirst + " " + getstring(unigram_extractsign_dict[t].index), 0, vietdictSize - 1) != -1) {
                        if (!error.contains(unigram_extractsign_dict[t].index)) {
                            error.add(unigram_extractsign_dict[t].index);
                            switch (error_type.get(i)) {
                                case 1:
                                    errorCost.add(1.65);
                                    break;
                                case 2:
                                    errorCost.add(2.0);
                                    break;
                                case 3:
                                    errorCost.add(2.0);
                                    break;
                            }
//                            System.out.println(" add new typing error 2: " + unigram_extractsign_dict[t].index);
                            continue;
                        }
                    }
                }
                count_temp1 = 0;
                count_temp2 = 0;
                if (textLast.compareTo("") != 0) {
                    count_temp2 = count_temp2 + getBigramFrequency(getstring(unigram_extractsign_dict[t].index) + " " + textLast, 0, bigramDictionarySize - 1);
                }
                if (textFirst.compareTo("") != 0) {
                    count_temp1 = count_temp1 + getBigramFrequency(textFirst + " " + getstring(unigram_extractsign_dict[t].index), 0, bigramDictionarySize - 1);
                }
                if (count_temp1 > count1 || count_temp2 > count2) {
//                    System.out.println(" count_temp1: "+count_temp1+", count1: " + count1+", count_temp2: "+count_temp2+", count2: "+count2);
//                    System.out.println(textFirst + " " + getstring(unigram_extractsign_dict[t].index)+" : "+count_temp1+" , "+textFirst + " " + text+": "+count1);
//                    System.out.println(getstring(unigram_extractsign_dict[t].index) + " " + textLast + ": "+count_temp2+ " , " + text + " " + textLast+" : "+ count2);

                    if (!error.contains(unigram_extractsign_dict[t].index)) {
                        error.add(unigram_extractsign_dict[t].index);
                        switch (error_type.get(i)) {
                            case 1:
                                errorCost.add(1.65);
                                break;
                            case 2:
                                errorCost.add(2.0);
                                break;
                            case 3:
                                errorCost.add(2.0);
                                break;
                        }
//                        System.out.println(" add new typing error 3: " + unigram_extractsign_dict[t].index);
                    }
                }
            }
        }

        // loi dau
        List<Integer> index_error;
        List<Integer> index_replace;
        List<String> temp_string;

        index_error = new ArrayList<>();
        index_replace = new ArrayList<>();
        temp_string = new ArrayList<>();
        replaceSignError(temp_string, text);
//        System.out.println("loi dau");

        for (int i = 0; i < temp_string.size(); i++) {
            int t;
//            System.out.println(temp_string.get(i));
            t = unigramExtractsignSearch(temp_string.get(i), 0, 6691);
            if (t > -1) {
                if (textLast.compareTo("") != 0) {
                    if (Utility.myBinarySearch(vietdict, getstring(unigram_extractsign_dict[t].index) + " " + textLast, 0, vietdictSize - 1) != -1) {
                        if (!error.contains(unigram_extractsign_dict[t].index)) {
                            error.add(unigram_extractsign_dict[t].index);
                            errorCost.add(2.0);
//                            System.out.println(" add new sign error 1: " + unigram_extractsign_dict[t].index);
                            continue;
                        }
                    }
                }
                if (textFirst.compareTo("") != 0) {
                    if (Utility.myBinarySearch(vietdict, textFirst + " " + getstring(unigram_extractsign_dict[t].index), 0, vietdictSize - 1) != -1) {
                        if (!error.contains(unigram_extractsign_dict[t].index)) {
                            error.add(unigram_extractsign_dict[t].index);
                            errorCost.add(2.0);
//                            System.out.println(" add new sign error 2: " + unigram_extractsign_dict[t].index);
                            continue;
                        }
                    }
                }
                count_temp1 = 0;
                count_temp2 = 0;
                if (textLast.compareTo("") != 0) {
                    //count_temp = count_temp + bigram.dict[bigram.indict((unigram_extractsign.dict[t].index<<13)|index2,bigram.start[unigram_extractsign.dict[t].index],bigram.end[unigram_extractsign.dict[t].index])].frequency;
                    count_temp2 = count_temp2 + getBigramFrequency(getstring(unigram_extractsign_dict[t].index) + " " + textLast, 0, bigramDictionarySize - 1);
                }
                if (textFirst.compareTo("") != 0) {
                    //count_temp = count_temp + bigram.dict[bigram.indict((index<<13)|unigram_extractsign.dict[t].index,bigram.start[index],bigram.end[index])].frequency;
                    count_temp1 = count_temp1 + getBigramFrequency(textFirst + " " + getstring(unigram_extractsign_dict[t].index), 0, bigramDictionarySize - 1);
                }
                //cout<<" "<<unigram_extractsign_dict[t].index<<",count_temp: "<<count_temp<<",count: "<<count<<endl;
                if (count_temp1 > count1 || count_temp2 > count2) {
                    if (!error.contains(unigram_extractsign_dict[t].index)) {
                        error.add(unigram_extractsign_dict[t].index);
                        errorCost.add(2.0);
//                        System.out.println(" add new sign error 3: " + unigram_extractsign_dict[t].index);
                    }
                }
            }
        }
        // loi am dau
        replaceStartError(temp_string, text, index_error, index_replace);
//        System.out.println("loi am dau");
        for (int i = 0; i < temp_string.size(); i++) {
            int t;
//            System.out.println(temp_string.get(i));

            t = unigramSearch(temp_string.get(i));
            if (t > -1) {
                if (textLast.compareTo("") != 0) {
                    if (Utility.myBinarySearch(vietdict, temp_string.get(i) + " " + textLast, 0, vietdictSize - 1) != -1) {
                        if (!error.contains(t)) {
                            error.add(t);
                            errorCost.add(2.0);
//                            System.out.println("add new first error 1: "+ t);
                            continue;
                        }
                    }
                }
                if (textFirst.compareTo("") != 0) {
                    if (Utility.myBinarySearch(vietdict, textFirst + " " + temp_string.get(i), 0, vietdictSize - 1) != -1) {
                        if (!error.contains(t)) {
                            error.add(t);
                            errorCost.add(2.0);
//                            System.out.println("add new first error 2: "+ t);
                            continue;
                        }
                    }
                }
                count_temp1 = 0;
                count_temp2 = 0;
                if (textLast.compareTo("") != 0) {
                    count_temp2 = count_temp2 + getBigramFrequency(temp_string.get(i) + " " + textLast, 0, bigramDictionarySize - 1);
                }
                if (textFirst.compareTo("") != 0) {
                    count_temp1 = count_temp1 + getBigramFrequency(textFirst + " " + temp_string.get(i), 0, bigramDictionarySize - 1);
                }
                //cout<<" t: "<<t<<", count_temp: "<<count_temp<<", count: "<<count<<endl;

                if (!error.contains(t)) {
                    //cout<<" t: "<<t<<", count_temp: "<<count_temp<<", count: "<<count<<endl;
                    if (count_temp1 > count1 || count_temp2 > count2) {
                        error.add(t);
                        errorCost.add(1.0);
//                        System.out.println("add new first error 3: "+ t);
                    } else {
                        if (checkStart(textFirst, index_error.get(i), index_replace.get(i), syllables, commonEnglishWord)) {
                            error.add(t);
                            errorCost.add(1.0);
//                            System.out.println("add new first error 4: "+ t);
                        } else if (checkStart(textLast, index_error.get(i), index_replace.get(i), syllables, commonEnglishWord)) {
                            error.add(t);
                            errorCost.add(1.0);
//                            System.out.println("add new first error 5: "+ t);
                        }
                    }
                }
            }
        }
        // loi am cuoi
        replaceEndError(temp_string, text, index_error, index_replace);
        //cout<<"loi am cuoi"<<endl;

        for (int i = 0; i < temp_string.size(); i++) {
            int t;
            t = unigramSearch(temp_string.get(i));
            //wprintf("%s\n",(*it).c_str());
            if (t > -1) {
                if (textLast.compareTo("") != 0) {
                    if (Utility.myBinarySearch(vietdict, temp_string.get(i) + " " + textLast, 0, vietdictSize - 1) != -1) {
                        if (!error.contains(t)) {
                            error.add(t);
                            errorCost.add(2.0);
//                            System.out.println("add new last error 1: "+ t);
                            continue;
                        }
                    }
                }
                if (textFirst.compareTo("") != 0) {
                    if (Utility.myBinarySearch(vietdict, textFirst + " " + temp_string.get(i), 0, vietdictSize - 1) != -1) {
                        if (!error.contains(t)) {
                            error.add(t);
                            errorCost.add(2.0);
//                            System.out.println("add new last error 2: "+ t);
                            continue;
                        }
                    }
                }
                count_temp1 = 0;
                count_temp2 = 0;
                if (textLast.compareTo("") != 0) {
                    count_temp2 = count_temp2 + getBigramFrequency(temp_string.get(i) + " " + textLast, 0, bigramDictionarySize - 1);
                }
                if (textFirst.compareTo("") != 0) {
                    count_temp1 = count_temp1 + getBigramFrequency(textFirst + " " + temp_string.get(i), 0, bigramDictionarySize - 1);
                }
                if (!error.contains(t)) {
                    if (count_temp1 > count1 || count_temp2 > count2) {
                        error.add(t);
                        errorCost.add(1.0);
//                        System.out.println("add new last error 3: "+ t);
                    } else {
                        if (checkEnd(textFirst, index_error.get(i), index_replace.get(i), syllables, commonEnglishWord)) {
                            error.add(t);
                            errorCost.add(1.0);
//                            System.out.println("add new last error 4: "+ t);
                        } else if (checkEnd(textLast, index_error.get(i), index_replace.get(i), syllables, commonEnglishWord)) {
                            error.add(t);
                            errorCost.add(1.0);
//                            System.out.println("add new last error 5: "+ t);
                        }
                    }
                }
            }
        }
        //Lỗi địa phương
        localError(temp_string, text);
        //cout<<"loi dia phuong"<<endl;
        for (int i = 0; i < temp_string.size(); i++) {
            int t;
            //wprintf("%s\n",(*it).c_str());
            t = unigramSearch(temp_string.get(i));
            if (t > -1) {
                if (textLast.compareTo("") != 0) {
                    if (Utility.myBinarySearch(vietdict, temp_string.get(i) + " " + textLast, 0, vietdictSize - 1) != -1) {
                        if (!error.contains(t)) {
                            error.add(t);
                            errorCost.add(2.0);
//                            System.out.println("add new local error 1: "+ t);
                            continue;
                        }
                    }
                }
                if (textFirst.compareTo("") != 0) {
                    if (Utility.myBinarySearch(vietdict, textFirst + " " + temp_string.get(i), 0, vietdictSize - 1) != -1) {
                        if (!error.contains(t)) {
                            error.add(t);
                            errorCost.add(2.0);
//                            System.out.println("add new local error 2: "+ t);
                            continue;
                        }
                    }
                }
                count_temp1 = 0;
                count_temp2 = 0;
                if (textLast.compareTo("") != 0) {
                    count_temp2 = count_temp2 + getBigramFrequency(temp_string.get(i) + " " + textLast, 0, bigramDictionarySize - 1);
                }
                if (textFirst.compareTo("") != 0) {
                    count_temp1 = count_temp1 + getBigramFrequency(textFirst + (String) " " + temp_string.get(i), 0, bigramDictionarySize - 1);
                }
                if (!error.contains(t)) {
                    if (count_temp1 > count1 || count_temp2 > count2) {
                        error.add(t);
                        errorCost.add(2.0);
//                        System.out.println("add new local error 3: "+ t);
                    }
                }
            }
        }
    }

    String process(String lastword2, String lastword1, String curword, String nextword1, String nextword2) {
        //int index2,index1,index;
        String luu = curword;
        double max = -1000000.0;
        double min_cost = 1000.0;
        if (curword.length() == 0) {
            return "";
        }
        boolean first_character_capital = (Character.isUpperCase(curword.charAt(0)));

        if (Utility.myBinarySearch(StringConstant.titleList, curword, 0, 59) != -1) {
            return curword;
        }
        if (symbolMark2 && symbolMark3) {
            return curword;
        }
        if (Utility.upperNumber(curword) >= 2) {
            if (Utility.upperNumber(lastword1) < 2 && Utility.upperNumber(nextword1) < 2) {
                return curword;
            }
        }
        if (Utility.upperNumber(curword) == 1 && curword.length() <= 2) {
            return curword;
        }

        String tmp_lastword1 = lastword1;
        String tmp_lastword2 = lastword2;
        String tmp_nextword1 = nextword1;
        String tmp_nextword2 = nextword2;

        tmp_lastword1 = tmp_lastword1.toLowerCase();
        tmp_lastword2 = tmp_lastword2.toLowerCase();
        tmp_nextword1 = tmp_nextword1.toLowerCase();
        tmp_nextword2 = tmp_nextword2.toLowerCase();
        curword = curword.toLowerCase();
        luu = luu.toLowerCase();

        if (curword.matches(StringConstant.numbercheck) || curword.contains("°") || curword.contains("%")) {
            return curword;
        }
        if (curword.matches(StringConstant.date)) {
            return curword;
        }
        if (curword.matches(StringConstant.emailcheck)) {
            return curword;
        }
        if (curword.matches(StringConstant.webcheck)) {
            return curword;
        }

        if (Utility.foreignTest(tmp_lastword1)) {
            tmp_lastword1 = "FOREIGNWORD";
        } else {
            if (!Utility.isFullCharacter(tmp_lastword1)) {
                //cout<<"not word"<<endl;
                if (tmp_lastword1.matches(StringConstant.numbercheck) || tmp_lastword1.contains("°") || tmp_lastword1.contains("%")) {
                    tmp_lastword1 = "NUMBER";
                } else if (tmp_lastword1.matches(StringConstant.date)) {
                    tmp_lastword1 = "DATE";
                } else if (tmp_lastword1.matches(StringConstant.emailcheck)) {
                    tmp_lastword1 = "EMAI";
                } else if (tmp_lastword1.matches(StringConstant.webcheck)) {
                    tmp_lastword1 = "WEBSITE";
                }
            }
        }

        if (Utility.foreignTest(tmp_lastword2)) {
            tmp_lastword2 = "FOREIGNWORD";
        } else {
            if (!Utility.isFullCharacter(tmp_lastword2)) {
                //cout<<"not word"<<endl;
                if (tmp_lastword2.matches(StringConstant.numbercheck) || tmp_lastword2.contains("°") || tmp_lastword2.contains("%")) {
                    tmp_lastword2 = "NUMBER";
                } else if (tmp_lastword2.matches(StringConstant.date)) {
                    tmp_lastword2 = "DATE";
                } else if (tmp_lastword2.matches(StringConstant.emailcheck)) {
                    tmp_lastword2 = "EMAI";
                } else if (tmp_lastword2.matches(StringConstant.webcheck)) {
                    tmp_lastword2 = "WEBSITE";
                }
            }
        }
        if (Utility.foreignTest(tmp_nextword1)) {
            tmp_nextword1 = "FOREIGNWORD";
        } else {
            if (!Utility.isFullCharacter(tmp_nextword1)) {
                //cout<<"not word"<<endl;
                if (tmp_nextword1.matches(StringConstant.numbercheck) || tmp_nextword1.contains("°") || tmp_nextword1.contains("%")) {
                    tmp_nextword1 = "NUMBER";
                } else if (tmp_nextword1.matches(StringConstant.date)) {
                    tmp_nextword1 = "DATE";
                } else if (tmp_nextword1.matches(StringConstant.emailcheck)) {
                    tmp_nextword1 = "EMAI";
                } else if (tmp_nextword1.matches(StringConstant.webcheck)) {
                    tmp_nextword1 = "WEBSITE";
                }
            }
        }

        if (Utility.foreignTest(tmp_nextword2)) {
            tmp_nextword2 = "FOREIGNWORD";
        } else {
            if (!Utility.isFullCharacter(tmp_nextword2)) {
                //cout<<"not word"<<endl;
                if (tmp_nextword2.matches(StringConstant.numbercheck) || tmp_nextword2.contains("°") || tmp_nextword2.contains("%")) {
                    tmp_nextword2 = "NUMBER";
                } else if (tmp_nextword2.matches(StringConstant.date)) {
                    tmp_nextword2 = "DATE";
                } else if (tmp_nextword2.matches(StringConstant.emailcheck)) {
                    tmp_nextword2 = "EMAI";
                } else if (tmp_nextword2.matches(StringConstant.webcheck)) {
                    tmp_nextword2 = "WEBSITE";
                }
            }
        }

//        System.out.println("process : '"+tmp_lastword2+"' '"+tmp_lastword1+"' '"+curword+"' '"+tmp_nextword1+"' '"+tmp_nextword2+"'");
        List<Integer> error;
        List<Double> errorCost;
        error = new ArrayList<>();
        errorCost = new ArrayList<>();
        int l = unigramSearch(curword);

        totalError(error, errorCost, curword, tmp_lastword1, tmp_nextword1);

        if (error.isEmpty() || (error.size() == 1 && error.get(0) == l)) {
            return luu;
        }

//        for(int i = 0;i < error.size();i++)
//            System.out.println("error : "+error.get(i));
        //cout<<"good"<<endl;
        int rec;
        for (int i = 0; i < error.size(); i++) {
            int add1 = 1;
            int add2 = 1;
            int add3 = 1;

            //wprintf("  %s\n",getstring(*it).c_str());
            //calculate add1
            if (Utility.myBinarySearch(vietdict, lastword2 + " " + lastword1 + " " + getstring(error.get(i)), 0, vietdictSize - 1) > -1) {
                add1 = 10;
            } else {
                if (Utility.myBinarySearch(vietdict, lastword1 + " " + getstring(error.get(i)), 0, vietdictSize - 1) > -1) {
                    add1 = 3;
                }
            }

            //calculate add2
            if (Utility.myBinarySearch(vietdict, lastword1 + " " + getstring(error.get(i)) + " " + nextword1, 0, vietdictSize - 1) > -1) {
                add2 = 10;
            } else {
                if (Utility.myBinarySearch(vietdict, getstring(error.get(i)) + " " + nextword1, 0, vietdictSize - 1) > -1) {
                    add2 = 3;
                }
            }

            //calculate add3
            if (Utility.myBinarySearch(vietdict, getstring(error.get(i)) + " " + nextword1 + " " + nextword2, 0, vietdictSize - 1) > -1) {
                add3 = 10;
            } else {
                if (Utility.myBinarySearch(vietdict, getstring(error.get(i)) + " " + nextword1, 0, vietdictSize - 1) > -1) {
                    add3 = 3;
                }
            }

            //cout<<"  add1: "<<add1<<", add2: "<<add2<<", add3: "<<add3<<endl;
            String it_word = getstring(error.get(i));

            double temp_rate;
            double temp_rate1, temp_rate2, temp_rate3;

            //cout<<"calculate temp rate 1"<<endl;
            //calculate temprate1
            if (!symbolMark2) {
                if (!symbolMark1) {
                    int tri_freq1 = getTrigramFrequency(tmp_lastword2 + " " + tmp_lastword1 + " " + it_word, 0, trigramDictionarySize - 1);
//                    System.out.println("  tri_freq1: "+tri_freq1);
//                    System.out.println("  temp_rate1: mau so to: "+getBigramFrequency(tmp_lastword2+" "+tmp_lastword1,0,bigramDictionarySize-1));
//                    System.out.println("  temp_rate1, tu so: "+((double)getBigramFrequency(tmp_lastword1+" "+it_word,0,bigramDictionarySize-1)+smooth)+", mau so: "+(double)getUnigramFrequency(tmp_lastword1,0,6770));
                    if (tri_freq1 > 0) {
                        temp_rate1 = Math.log(kk * (((double) tri_freq1 + smooth) * add1 / ((double) getBigramFrequency(tmp_lastword2 + " " + tmp_lastword1, 0, bigramDictionarySize - 1) + unigramDictionarySize * smooth))
                                + mm * (((double) getBigramFrequency(lastword1 + " " + it_word, 0, bigramDictionarySize - 1) + smooth) * add1 / (getUnigramFrequency(lastword1, 0, 6770) + (unigramDictionarySize * smooth))));
                    } else {
                        temp_rate1 = Math.log(mm * (((double) getBigramFrequency(tmp_lastword1 + " " + it_word, 0, bigramDictionarySize - 1) + smooth) * add1 / ((double) getUnigramFrequency(tmp_lastword1, 0, 6770) + (unigramDictionarySize * smooth))));
                    }
                } else {
                    //cout<<"  temp_rate1, tu so: "<<(double)getBigramFrequency(tmp_lastword1+" "+it_word,0,bigramDictionarySize-1)+smooth<<", mau so: "<<(double)getUnigramFrequency(tmp_lastword1,0,6770)/*+ ( max2(bi_hash_last1-bi_hash_first1+2,30)*smooth)*/<<endl;
                    temp_rate1 = Math.log(mm * (((double) getBigramFrequency(tmp_lastword1 + " " + it_word, 0, bigramDictionarySize - 1) + smooth) * add1 / ((double) getUnigramFrequency(tmp_lastword1, 0, 6770) + (unigramDictionarySize * smooth))));
                }
            } else {
                temp_rate1 = 0.0;
            }

            //calculate temprate3
            if (!symbolMark3) {
                if (!symbolMark4) {
                    int tri_freq3 = getTrigramFrequency(it_word + " " + tmp_nextword1 + " " + tmp_nextword2, 0, trigramDictionarySize - 1);
                    //cout<<"  tri_freq3: "<<tri_freq3<<endl;
                    //cout<<"  temp_rate3, mau so to: "<<getBigramFrequency(tmp_nextword1+" "+tmp_nextword2,0,bigramDictionarySize-1)/*+(tri_hash_last3-tri_hash_first3+2)*smooth*/<<endl;
                    //cout<<"  temp_rate3, tu so: "<<getBigramFrequency(it_word+" "+tmp_nextword1,0,bigramDictionarySize-1)<<", mau so: "<<(getUnigramFrequency(tmp_nextword1,0,6770)/*+max2((bi_hash_last3-bi_hash_first3+2),30)*smooth*/)<<endl;
                    if (tri_freq3 > 0) {
                        temp_rate3 = Math.log(kk * (((double) tri_freq3 + smooth) * add3 / (getBigramFrequency(tmp_nextword1 + " " + tmp_nextword2, 0, bigramDictionarySize - 1) + unigramDictionarySize * smooth))
                                + mm * (((double) getBigramFrequency(it_word + " " + tmp_nextword1, 0, bigramDictionarySize - 1) + smooth) * add3 / (getUnigramFrequency(tmp_nextword1, 0, 6770) + unigramDictionarySize * smooth)));
                    } else {
                        temp_rate3 = Math.log(mm * (((double) getBigramFrequency(it_word + " " + tmp_nextword1, 0, bigramDictionarySize - 1) + smooth) * add3 / (getUnigramFrequency(tmp_nextword1, 0, 6770) + unigramDictionarySize * smooth)));
                    }
                } else {
                    //cout<<"  temp_rate3, tu so: "<<getBigramFrequency(it_word+" "+tmp_nextword1,0,bigramDictionarySize-1)<<", mau so: "<<(getUnigramFrequency(tmp_nextword1,0,6770)/*+max2((bi_hash_last3-bi_hash_first3+2),30)*smooth*/)<<endl;
                    int bi_freq3 = getBigramFrequency(it_word + " " + tmp_nextword1, 0, bigramDictionarySize - 1);
                    temp_rate3 = Math.log(mm * (((double) bi_freq3 + smooth) * add3 / (getUnigramFrequency(tmp_nextword1, 0, 6770) + unigramDictionarySize * smooth)));
                }
                //cout<<"calculate temp rate 2"<<endl;
            } else {
                temp_rate3 = 0.0;
            }
            //calculate temp_rate2
            int tri_index2 = trigramSearch(tmp_lastword1 + " " + it_word + " " + tmp_nextword1, 0, trigramDictionarySize - 1);
            if (tri_index2 > -1) {
                int tri_freq2 = trigramDictionary[tri_index2].frequency;

                //cout<<"tri_index2: "<<tri_index2<<endl;
                int it_backward = tri_index2, it_forward = tri_index2 + 1;
                int bi_freq = 0, bi_count = 0;
                while (getstring(trigramDictionary[it_backward].first).equals(tmp_lastword1) && it_backward > 0) {
                    if (getstring(trigramDictionary[it_backward].last).equals(tmp_nextword1)) {
                        bi_freq += trigramDictionary[it_backward].frequency;
                        bi_count++;
                        //cout<<"good"<<endl;
                    }
                    it_backward--;
                    //cout<<it_backward<<endl;
                    //system("pause");
                }
                while (getstring(trigramDictionary[it_forward].first).equals(tmp_lastword1) && it_forward < (trigramDictionarySize - 1)) {
                    if (getstring(trigramDictionary[it_forward].last).equals(tmp_nextword1)) {
                        bi_freq += trigramDictionary[it_forward].frequency;
                        bi_count++;
                    }
                    it_forward++;
                    //cout<<it_forward<<endl;
                }

                //cout<<"bi_freq: "<<bi_freq<<", tri_freq2: "<<tri_freq2<<endl;
                if (!symbolMark2 && !symbolMark3) {
                    temp_rate2 = Math.log(0.6 * (((double) tri_freq2 + smooth) * add2 / (bi_freq + Utility.max2((bi_count + 2), 1200) * smooth))
                            + 0.2 * (((double) getBigramFrequency(it_word + " " + tmp_nextword1, 0, bigramDictionarySize - 1) + smooth) * add2 / (getUnigramFrequency(tmp_nextword1, 0, 6770) + unigramDictionarySize * smooth))
                            + 0.2 * (((double) getBigramFrequency(tmp_lastword1 + " " + it_word, 0, bigramDictionarySize - 1) + smooth) * add2 / (getUnigramFrequency(tmp_lastword1, 0, 6770) + unigramDictionarySize * smooth)));
                } else {
                    temp_rate2 = 0.0;
                }
            } else {
                temp_rate2 = 0.0;
            }

            //combination
            if (temp_rate2 != 0.0) {
                if (temp_rate1 == 0.0) {
                    if (Math.abs(temp_rate2 - temp_rate3) < 4.0) {
                        temp_rate = (temp_rate2 + temp_rate3) / 2;
                    } else {
                        temp_rate = -Math.sqrt(Math.abs(temp_rate2 * temp_rate3));
                    }
                } else if (temp_rate3 == 0.0) {
                    if (Math.abs(temp_rate2 - temp_rate1) < 4.0) {
                        temp_rate = (temp_rate1 + temp_rate2) / 2;
                    } else {
                        temp_rate = -Math.sqrt(Math.abs(temp_rate1 * temp_rate2));
                    }
                } else {
                    if (Math.abs(temp_rate1 / temp_rate2) > 3.0 || Math.abs(temp_rate1 / temp_rate3) > 3.0 || Math.abs(temp_rate2 / temp_rate1) > 3.0 || Math.abs(temp_rate2 / temp_rate3) > 3.0 || Math.abs(temp_rate3 / temp_rate1) > 3.0 || Math.abs(temp_rate3 / temp_rate2) > 3.0) {
                        temp_rate = -Math.pow(Math.abs(temp_rate1 * temp_rate2 * temp_rate3), 1.0 / 3) + 0.75;
                    } else {
                        temp_rate = (temp_rate1 + temp_rate2 + temp_rate3) / 3 + 0.75;
                    }
                }
            } else {
                if (temp_rate1 == 0.0) {
                    temp_rate = temp_rate3;
                } else if (temp_rate3 == 0.0) {
                    temp_rate = temp_rate1;
                } else if (Math.abs(temp_rate1 - temp_rate3) < 4.0) {
                    temp_rate = (temp_rate1 + temp_rate3) / 2;
                } else {
                    temp_rate = -Math.sqrt(Math.abs(temp_rate1 * temp_rate3));
                }
            }

            if (error.get(i) != l && first_character_capital) {
                temp_rate -= 1.6;
            }

            if (error.get(i) != l && temp_rate < -0.4) {
                if (symbolMark2) {
                    if (temp_rate > -2.0) {
                        temp_rate -= 3.5;
                    } else {
                        temp_rate -= 2.15;
                    }
                } else if (symbolMark1) {
                    temp_rate -= 1.0;
                }
                if (symbolMark3) {

                    if (temp_rate > -2.0) {
                        temp_rate -= 3.5;
                    } else {
                        temp_rate -= 2.15;
                    }
                } else if (symbolMark4) {
                    temp_rate -= 1.0;
                }
            }

            if ((error.get(i) != l) && ((it_word.equals("và") || it_word.equals("của") || it_word.equals("là") || it_word.equals("nào")))) {
                temp_rate -= 3.5;
            }
//            System.out.print("symbolMark1: " + symbolMark1 + ", symbolMark2: " + symbolMark2 + ", symbolMark3: " + symbolMark3 + ", symbolMark4: " + symbolMark4 + ", ");
//            System.out.println(getstring(error.get(i)) + " , temp_rate: " + temp_rate + " , max: " + max);
//            System.out.println("temp_rate1: " + temp_rate1 + " , temp_rate2: " + temp_rate2 + " , temp_rate3: " + temp_rate3);
            if (temp_rate > max) {
                if (min_cost < errorCost.get(i)) {
                    //cout<<" "<<*it<<"  temp_rate: "<<temp_rate<<", max: "<<max<<endl;
                    if ((temp_rate > max + itCondition * (errorCost.get(i) - min_cost) + (errorCost.get(i) - min_cost)) && (temp_rate > -7.85 || (temp_rate - max) > 7.5)) {
                        max = temp_rate;
                        luu = getstring(error.get(i));
                        min_cost = errorCost.get(i);
//                        rec =  * it;
                    }
                } else {
                    //cout<<"  min_cost more "<<endl;
                    max = temp_rate;
                    luu = getstring(error.get(i));
                    min_cost = errorCost.get(i);
//                    rec =  * it;
                }
            } else if ((temp_rate > max + itCondition * (errorCost.get(i) - min_cost) + (errorCost.get(i) - min_cost)) && (temp_rate > -7.85 || (temp_rate - max) > 7.5)) {
                max = temp_rate;
                luu = getstring(error.get(i));
                min_cost = errorCost.get(i);
            }
        }
//        System.out.println();
        if (max < -8.25 && l == -1) {
            return curword;
        }
        return luu;
    }

    public ArrayList CheckSentence(String sentence) throws Exception {
        String resultSentence = "";
        ArrayList<SpellingError> listcheck = new ArrayList();
        ArrayList<String> resultCheck = new ArrayList();
        int count, error_count;
        count = 0;
        error_count = 0;
//        System.out.println(process("mới", "mua", "dung", "ma", "sao"));
//        System.out.println(process("mua", "dùng", "ma", "sao", ""));

        String[] words = sentence.split(" ");
        if (words.length < 5) {
            //return sentence;
            return null;
        }

        StringBuilder tmp1_1, tmp1_2, tmp2_1, tmp2_2, tmp3_1, tmp3_2, tmp4_1, tmp4_2;
        tmp1_1 = new StringBuilder("");
        tmp1_2 = new StringBuilder("");
        tmp2_1 = new StringBuilder("");
        tmp2_2 = new StringBuilder("");
        tmp3_1 = new StringBuilder("");
        tmp3_2 = new StringBuilder("");
        tmp4_1 = new StringBuilder("");
        tmp4_2 = new StringBuilder("");
        String tmp_str1, tmp_str2, tmp_str3, tmp_str4, tmp_str5 = null;
        tmp_str1 = "";
        tmp_str2 = "";
        tmp_str3 = "";
        tmp_str4 = "";
//        tmp_str5 = "";
        //split punctuation from the word
        StringBuilder first = new StringBuilder(words[0]);
        tmp_str1 = first.toString();
        remove_symbol(first, tmp1_1, tmp1_2);
        StringBuilder second = new StringBuilder(words[1]);
        tmp_str2 = second.toString();
        remove_symbol(second, tmp2_1, tmp2_2);
        symbolMark1 = tmp1_2.length() > 0 || (tmp2_1.length() > 0 && first.charAt(0) != '@');
        StringBuilder third = new StringBuilder(words[2]);
        tmp_str3 = third.toString();
        char c3 = third.charAt(0);
        remove_symbol(third, tmp3_1, tmp3_2);
        symbolMark2 = (tmp2_2.length() > 0 || (tmp3_1.length() > 0 && c3 != '@'));
        StringBuilder fourth = new StringBuilder(words[3]);
        tmp_str4 = fourth.toString();
        char c4 = fourth.charAt(0);
        remove_symbol(fourth, tmp4_1, tmp4_2);
        symbolMark3 = (tmp3_2.length() > 0 || (tmp4_1.length() > 0 && c4 != '@'));
        //check the first two words
        String result = process("", "", first.toString(), second.toString(), third.toString());
        if (first.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
            String error = first.toString();
            first.replace(0, first.length(), result);
            //resultSentence += "" + tmp1_1 + error + "[" + first + "]" + tmp1_2 + " ";
            SpellingError a= new SpellingError(0,0,""+first);
            listcheck.add(a);
            error_count++;
            count++;
            resultCheck.add("[" + error + ", " + first + ", " + count + "] " );

        } else {
            //resultSentence += tmp_str1 + " ";
//            resultCheck.add(first + " ");
            count++;
        }
        result = process("", first.toString(), second.toString(), third.toString(), fourth.toString());
        if (second.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
            String error = second.toString();
            second.replace(0, second.length(), result);
            //resultSentence += "" + tmp2_1 + error + "[" + second + "]" + tmp2_2 + " ";
            SpellingError a= new SpellingError(1,1,""+second);
            listcheck.add(a);
            count++;
            resultCheck.add("[" + error + ", " + second + ", " + count + "] " );
            error_count++;
        } else {
            //resultSentence += tmp_str2 + " ";
//            resultCheck.add(second + " ");
            count++;
        }
        StringBuilder fifth = null;
        StringBuilder tmp5_1 = null, tmp5_2 = null;
        for (int i = 4; i < words.length; i++) {
            fifth = new StringBuilder(words[i]);
            tmp5_1 = new StringBuilder("");
            tmp5_2 = new StringBuilder("");
//                System.out.println("'"+fifth+"'");
            char c5;
            if (fifth.length() != 0) {
                c5 = fifth.charAt(0);
            } else {
                c5 = ' ';
            }
            tmp_str5 = fifth.toString();

//            System.out.println(first.toString() + " " + second.toString() + " " + third.toString() + " " + fourth.toString() + " " + fifth.toString());
            remove_symbol(fifth, tmp5_1, tmp5_2);
            symbolMark4 = (tmp4_2.length() > 0 || (tmp5_1.length() > 0 && c5 != '@'));
            result = process(first.toString(), second.toString(), third.toString(), fourth.toString(), fifth.toString());
//                System.out.println(tmp_str3);
            if (third.toString().toLowerCase().compareTo(result.toLowerCase()) != 0 && c3 == '@') {
//                    System.out.println(first.toString() + " " + second.toString() + " " + third.toString() + " " + fourth.toString() + " " + fifth.toString() + " : " + result);
//                    Thread.sleep(50000);
                System.out.println(third.toString() + " : " + result);
            }
            if (third.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
                //if (c3 == '@') {
                error_count++;
                // }
                String error = third.toString();
                third.replace(0, third.length(), result);
                //resultSentence += "" + tmp3_1 + error + "[" + third + "]" + tmp3_2 + " ";
                SpellingError a= new SpellingError(i-2,i-2,""+third);
                listcheck.add(a);
                count++;
                resultCheck.add("[" + error + ", " + third + ", " + count + "] " );
            } else {
                //resultSentence += tmp_str3 + " ";
//                resultCheck.add(third + " ");
                count++;
            }

            first = second;
            second = third;
            third = fourth;
            fourth = fifth;
            tmp_str1 = tmp_str2;
            tmp_str2 = tmp_str3;
            tmp_str3 = tmp_str4;
            tmp_str4 = tmp_str5;
            tmp1_1 = tmp2_1;
            tmp2_1 = tmp3_1;
            tmp3_1 = tmp4_1;
            tmp4_1 = tmp5_1;
            tmp1_2 = tmp2_2;
            tmp2_2 = tmp3_2;
            tmp3_2 = tmp4_2;
            tmp4_2 = tmp5_2;
//                System.out.println("assign tmp4_2 to " + tmp5_2 + "\n");
            symbolMark1 = symbolMark2;
            symbolMark2 = symbolMark3;
            symbolMark3 = symbolMark4;
            c3 = c4;
            c4 = c5;
            count++;
        }
//        check last two words
//        System.out.println("process word " + third);
        result = process(first.toString(), second.toString(), third.toString(), fourth.toString(), "");
        if (third.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
            String error = third.toString();
            third.replace(0, third.length(), result);
            //resultSentence += "" + tmp3_1 + error + "[" + third + "]" + tmp3_2 + " ";
            SpellingError a= new SpellingError(words.length-2,words.length-2,""+third);
            listcheck.add(a);
            count++;
            resultCheck.add("[" + error + ", " + third + ", " + count + "] " );
            error_count++;
        } else {
            //resultSentence += tmp_str3 + " ";
//            resultCheck.add(third + " ");
            count++;
        }
//        System.out.println("process word " + fourth);
        result = process(second.toString(), third.toString(), fourth.toString(), "", "");
        if (fourth.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
            String error = fourth.toString();
            fourth.replace(0, fourth.length(), result);
            //resultSentence += "" + tmp4_1 + error + "[" + fourth + "]" + tmp4_2 + " ";
            SpellingError a= new SpellingError(words.length-1,words.length-1,""+fourth);
            listcheck.add(a);
            count++;
            resultCheck.add("[" + error + ", " + fourth + ", " + count + "] " );
            error_count++;

        } else {
            //resultSentence += tmp_str4 + " ";
//            resultCheck.add(fourth + " ");
            count++;
        }
//        System.out.println("count: " + count);
        //System.out.println("error_count: " + error_count);
       /* ArrayList<Check> list = new ArrayList<>();
        String ou = "";
        String[] token = resultSentence.split(" ");
        for (int i = 0; i < token.length; i++) {
            if (token[i].indexOf("[") > -1) {
                Check a = new Check(i, token[i].substring(token[i].indexOf("[")+1, token[i].length()-1));
//                ou +=token[i]+",";
                list.add(a);
            }
//sonnv.ict@gmail.com
        }
        ou = list.toString();

        return ou;*/
        //return resultSentence.trim();
        return resultCheck;
    }

    String process_v2(String lastword2, String lastword1, String curword, String nextword1, String nextword2) {
        //int index2,index1,index;
        String luu = curword + "(";
        double max = -1000000.0;
        double min_cost = 1000.0;
        if (curword.length() == 0) {
            return "";
        }
        boolean first_character_capital = (Character.isUpperCase(curword.charAt(0)));

        if (Utility.myBinarySearch(StringConstant.titleList, curword, 0, 59) != -1) {
            return curword;
        }
        if (symbolMark2 && symbolMark3) {
            return curword;
        }
        if (Utility.upperNumber(curword) >= 2) {
            if (Utility.upperNumber(lastword1) < 2 && Utility.upperNumber(nextword1) < 2) {
                return curword;
            }
        }
        if (Utility.upperNumber(curword) == 1 && curword.length() <= 2) {
            return curword;
        }

        String tmp_lastword1 = lastword1;
        String tmp_lastword2 = lastword2;
        String tmp_nextword1 = nextword1;
        String tmp_nextword2 = nextword2;

        tmp_lastword1 = tmp_lastword1.toLowerCase();
        tmp_lastword2 = tmp_lastword2.toLowerCase();
        tmp_nextword1 = tmp_nextword1.toLowerCase();
        tmp_nextword2 = tmp_nextword2.toLowerCase();
        curword = curword.toLowerCase();
        luu = luu.toLowerCase();

        if (curword.matches(StringConstant.numbercheck) || curword.contains("°") || curword.contains("%")) {
            return curword;
        }
        if (curword.matches(StringConstant.date)) {
            return curword;
        }
        if (curword.matches(StringConstant.emailcheck)) {
            return curword;
        }
        if (curword.matches(StringConstant.webcheck)) {
            return curword;
        }

        if (Utility.foreignTest(tmp_lastword1)) {
            tmp_lastword1 = "FOREIGNWORD";
        } else {
            if (!Utility.isFullCharacter(tmp_lastword1)) {
                //cout<<"not word"<<endl;
                if (tmp_lastword1.matches(StringConstant.numbercheck) || tmp_lastword1.contains("°") || tmp_lastword1.contains("%")) {
                    tmp_lastword1 = "NUMBER";
                } else if (tmp_lastword1.matches(StringConstant.date)) {
                    tmp_lastword1 = "DATE";
                } else if (tmp_lastword1.matches(StringConstant.emailcheck)) {
                    tmp_lastword1 = "EMAI";
                } else if (tmp_lastword1.matches(StringConstant.webcheck)) {
                    tmp_lastword1 = "WEBSITE";
                }
            }
        }

        if (Utility.foreignTest(tmp_lastword2)) {
            tmp_lastword2 = "FOREIGNWORD";
        } else {
            if (!Utility.isFullCharacter(tmp_lastword2)) {
                //cout<<"not word"<<endl;
                if (tmp_lastword2.matches(StringConstant.numbercheck) || tmp_lastword2.contains("°") || tmp_lastword2.contains("%")) {
                    tmp_lastword2 = "NUMBER";
                } else if (tmp_lastword2.matches(StringConstant.date)) {
                    tmp_lastword2 = "DATE";
                } else if (tmp_lastword2.matches(StringConstant.emailcheck)) {
                    tmp_lastword2 = "EMAI";
                } else if (tmp_lastword2.matches(StringConstant.webcheck)) {
                    tmp_lastword2 = "WEBSITE";
                }
            }
        }
        if (Utility.foreignTest(tmp_nextword1)) {
            tmp_nextword1 = "FOREIGNWORD";
        } else {
            if (!Utility.isFullCharacter(tmp_nextword1)) {
                //cout<<"not word"<<endl;
                if (tmp_nextword1.matches(StringConstant.numbercheck) || tmp_nextword1.contains("°") || tmp_nextword1.contains("%")) {
                    tmp_nextword1 = "NUMBER";
                } else if (tmp_nextword1.matches(StringConstant.date)) {
                    tmp_nextword1 = "DATE";
                } else if (tmp_nextword1.matches(StringConstant.emailcheck)) {
                    tmp_nextword1 = "EMAI";
                } else if (tmp_nextword1.matches(StringConstant.webcheck)) {
                    tmp_nextword1 = "WEBSITE";
                }
            }
        }

        if (Utility.foreignTest(tmp_nextword2)) {
            tmp_nextword2 = "FOREIGNWORD";
        } else {
            if (!Utility.isFullCharacter(tmp_nextword2)) {
                //cout<<"not word"<<endl;
                if (tmp_nextword2.matches(StringConstant.numbercheck) || tmp_nextword2.contains("°") || tmp_nextword2.contains("%")) {
                    tmp_nextword2 = "NUMBER";
                } else if (tmp_nextword2.matches(StringConstant.date)) {
                    tmp_nextword2 = "DATE";
                } else if (tmp_nextword2.matches(StringConstant.emailcheck)) {
                    tmp_nextword2 = "EMAI";
                } else if (tmp_nextword2.matches(StringConstant.webcheck)) {
                    tmp_nextword2 = "WEBSITE";
                }
            }
        }

//        System.out.println("process : '"+tmp_lastword2+"' '"+tmp_lastword1+"' '"+curword+"' '"+tmp_nextword1+"' '"+tmp_nextword2+"'");
        List<Integer> error;
        List<Double> errorCost;
        error = new ArrayList<>();
        errorCost = new ArrayList<>();
        int l = unigramSearch(curword);

        totalError(error, errorCost, curword, tmp_lastword1, tmp_nextword1);

        if (error.isEmpty() || (error.size() == 1 && error.get(0) == l)) {
            return curword;
        }

//        for(int i = 0;i < error.size();i++)
//            System.out.println("error : "+error.get(i));
        //cout<<"good"<<endl;
        int rec;
        for (int i = 0; i < error.size(); i++) {
            int add1 = 1;
            int add2 = 1;
            int add3 = 1;

            //wprintf("  %s\n",getstring(*it).c_str());
            //calculate add1
            if (Utility.myBinarySearch(vietdict, lastword2 + " " + lastword1 + " " + getstring(error.get(i)), 0, vietdictSize - 1) > -1) {
                add1 = 10;
            } else {
                if (Utility.myBinarySearch(vietdict, lastword1 + " " + getstring(error.get(i)), 0, vietdictSize - 1) > -1) {
                    add1 = 3;
                }
            }

            //calculate add2
            if (Utility.myBinarySearch(vietdict, lastword1 + " " + getstring(error.get(i)) + " " + nextword1, 0, vietdictSize - 1) > -1) {
                add2 = 10;
            } else {
                if (Utility.myBinarySearch(vietdict, getstring(error.get(i)) + " " + nextword1, 0, vietdictSize - 1) > -1) {
                    add2 = 3;
                }
            }

            //calculate add3
            if (Utility.myBinarySearch(vietdict, getstring(error.get(i)) + " " + nextword1 + " " + nextword2, 0, vietdictSize - 1) > -1) {
                add3 = 10;
            } else {
                if (Utility.myBinarySearch(vietdict, getstring(error.get(i)) + " " + nextword1, 0, vietdictSize - 1) > -1) {
                    add3 = 3;
                }
            }

            //cout<<"  add1: "<<add1<<", add2: "<<add2<<", add3: "<<add3<<endl;
            String it_word = getstring(error.get(i));

            double temp_rate;
            double temp_rate1, temp_rate2, temp_rate3;

            //cout<<"calculate temp rate 1"<<endl;
            //calculate temprate1
            if (!symbolMark2) {
                temp_rate1 = Math.log(mm * (((double) getBigramFrequency(tmp_lastword1 + " " + it_word, 0, bigramDictionarySize - 1) + smooth) * add1 / ((double) getUnigramFrequency(tmp_lastword1, 0, 6770) + (unigramDictionarySize * smooth))));
            } else {
                temp_rate1 = 0.0;
            }

            //calculate temprate3
            if (!symbolMark3) {
                temp_rate3 = Math.log(mm * (((double) getBigramFrequency(it_word + " " + tmp_nextword1, 0, bigramDictionarySize - 1) + smooth) * add3 / (getUnigramFrequency(tmp_nextword1, 0, 6770) + unigramDictionarySize * smooth)));
                //cout<<"calculate temp rate 2"<<endl;
            } else {
                temp_rate3 = 0.0;
            }

            //combination
            temp_rate = 1 / 2 * (temp_rate1 + temp_rate3);

            if (error.get(i) != l && first_character_capital) {
                temp_rate -= 1.6;
            }

            if (error.get(i) != l && temp_rate < -0.4) {
                if (symbolMark2) {
                    if (temp_rate > -2.0) {
                        temp_rate -= 3.5;
                    } else {
                        temp_rate -= 2.15;
                    }
                } else if (symbolMark1) {
                    temp_rate -= 1.0;
                }
                if (symbolMark3) {

                    if (temp_rate > -2.0) {
                        temp_rate -= 3.5;
                    } else {
                        temp_rate -= 2.15;
                    }
                } else if (symbolMark4) {
                    temp_rate -= 1.0;
                }
            }

            if ((error.get(i) != l) && ((it_word.equals("và") || it_word.equals("của") || it_word.equals("là") || it_word.equals("nào")))) {
                temp_rate -= 3.5;
            }
//            System.out.print("symbolMark1: " + symbolMark1 + ", symbolMark2: " + symbolMark2 + ", symbolMark3: " + symbolMark3 + ", symbolMark4: " + symbolMark4 + ", ");
//            System.out.println(getstring(error.get(i)) + " , temp_rate: " + temp_rate + " , max: " + max);
//            System.out.println("temp_rate1: " + temp_rate1 + " , temp_rate2: " + temp_rate2 + " , temp_rate3: " + temp_rate3);
            if (temp_rate > max) {
                if (min_cost < errorCost.get(i)) {
                    //cout<<" "<<*it<<"  temp_rate: "<<temp_rate<<", max: "<<max<<endl;
                    if ((temp_rate > max + itCondition * (errorCost.get(i) - min_cost) + (errorCost.get(i) - min_cost)) && (temp_rate > -7.85 || (temp_rate - max) > 7.5)) {
                        max = temp_rate;
                        luu = getstring(error.get(i));
                        min_cost = errorCost.get(i);
//                        rec =  * it;
                    }
                } else {
                    //cout<<"  min_cost more "<<endl;
                    max = temp_rate;
                    luu = getstring(error.get(i));
                    min_cost = errorCost.get(i);
//                    rec =  * it;
                }
            } else if ((temp_rate > max + itCondition * (errorCost.get(i) - min_cost) + (errorCost.get(i) - min_cost)) && (temp_rate > -7.85 || (temp_rate - max) > 7.5)) {
                max = temp_rate;
                luu = getstring(error.get(i));
                min_cost = errorCost.get(i);
            }
        }
//        System.out.println();
        if (max < -8.25 && l == -1) {
            return curword;
        }
        return luu;
    }

    void remove_symbol(StringBuilder word, StringBuilder start_symbol, StringBuilder end_symbol) {
        int start = 0;
//        System.out.println(word);
        while (start < word.length() && (Utility.isSymbol(word.charAt(start)) || word.charAt(start) == '@')) {
            start++;
        }
        int end = word.length() - 1;
        if (start < end) {
            while (end > -1 && (Utility.isSymbol(word.charAt(end)) || word.charAt(end) == '@')) {
                end--;
            }
        }

        //cout<<start<<" "<<end<<endl;
        String tmp1 = word.substring(0, start);
        String tmp2 = word.substring(start, end + 1);
        String tmp3;
        if (start < end) {
            tmp3 = word.substring(end + 1, word.length());
        } else {
            tmp3 = word.substring(end, end + 1);
        }
        word.replace(0, word.length(), tmp2);
        start_symbol.replace(0, start_symbol.length(), tmp1);
        end_symbol.replace(0, end_symbol.length(), tmp3);

//        System.out.println(tmp1 + " " + tmp2 + " " + tmp3);
    }

    void processFile(String fileNameIn, String outputFileName) throws Exception {
        Scanner s;
        int count, error_count;
        count = 0;
        error_count = 0;
        s = null;
//        System.out.println(process("mới", "mua", "dung", "ma", "sao"));
        System.out.println(process("", "quyển", "xách", "văn", "học"));

        StringBuilder tmp1_1, tmp1_2, tmp2_1, tmp2_2, tmp3_1, tmp3_2, tmp4_1, tmp4_2;
        tmp1_1 = new StringBuilder("");
        tmp1_2 = new StringBuilder("");
        tmp2_1 = new StringBuilder("");
        tmp2_2 = new StringBuilder("");
        tmp3_1 = new StringBuilder("");
        tmp3_2 = new StringBuilder("");
        tmp4_1 = new StringBuilder("");
        tmp4_2 = new StringBuilder("");
        String tmp_str1, tmp_str2, tmp_str3, tmp_str4, tmp_str5;
        tmp_str1 = "";
        tmp_str2 = "";
        tmp_str3 = "";
        tmp_str4 = "";
//        tmp_str5 = "";
        try {
            s = new Scanner(new BufferedReader(new FileReader(fileNameIn)));
//            s.useDelimiter(" |\\u2000|\\u2001|\\u2002|\\u2003|\\u2004|\\u2005|\\u2006|\\u2007|\\u2008|\\u2009|\\u200A|\\u200B|\\u2028|\\u2029|\\u3000|\\u00A0|\\uFEFF|\\n");
            File outFile = new File(outputFileName);
            if (!outFile.exists()) {
                outFile.createNewFile();
            }
            FileWriter wr = new FileWriter(outFile, false);

            //split punctuation from the word
            StringBuilder first = new StringBuilder(s.next());
            tmp_str1 = first.toString();
            remove_symbol(first, tmp1_1, tmp1_2);
            StringBuilder second = new StringBuilder(s.next());
            tmp_str2 = second.toString();
            remove_symbol(second, tmp2_1, tmp2_2);
            symbolMark1 = tmp1_2.length() > 0 || (tmp2_1.length() > 0 && first.charAt(0) != '@');
            StringBuilder third = new StringBuilder(s.next());
            tmp_str3 = third.toString();
            char c3 = third.charAt(0);
            remove_symbol(third, tmp3_1, tmp3_2);
            symbolMark2 = (tmp2_2.length() > 0 || (tmp3_1.length() > 0 && c3 != '@'));
            StringBuilder fourth = new StringBuilder(s.next());
            tmp_str4 = fourth.toString();
            char c4 = fourth.charAt(0);
            remove_symbol(fourth, tmp4_1, tmp4_2);
            symbolMark3 = (tmp3_2.length() > 0 || (tmp4_1.length() > 0 && c4 != '@'));
            //check the first two words
            String result = process("", "", first.toString(), second.toString(), third.toString());
            if (first.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
                first.replace(0, first.length(), result);
                wr.append("" + tmp1_1 + first + tmp1_2 + " ");
                error_count++;
            } else {
                wr.append(tmp_str1 + " ");
            }
            result = process("", first.toString(), second.toString(), third.toString(), fourth.toString());
            if (second.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
                second.replace(0, second.length(), result);
                wr.append("" + tmp2_1 + second + tmp2_2 + " ");
                error_count++;
            } else {
                wr.append(tmp_str2 + " ");
            }
            while (s.hasNext()) {
                StringBuilder fifth = new StringBuilder(s.next());
                StringBuilder tmp5_1, tmp5_2;
                tmp5_1 = new StringBuilder("");
                tmp5_2 = new StringBuilder("");
//                System.out.println("'"+fifth+"'");
                char c5;
                if (fifth.length() != 0) {
                    c5 = fifth.charAt(0);
                } else {
                    c5 = ' ';
                }
                tmp_str5 = fifth.toString();
                remove_symbol(fifth, tmp5_1, tmp5_2);
                symbolMark4 = (tmp4_2.length() > 0 || (tmp5_1.length() > 0 && c5 != '@'));

                result = process(first.toString(), second.toString(), third.toString(), fourth.toString(), fifth.toString());
//                System.out.println(tmp_str3);

                if (third.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
                    System.out.println(first.toString() + " " + second.toString() + " " + third.toString() + " " + fourth.toString() + " " + fifth.toString() + " : " + result);
//                    Thread.sleep(50000);
//                    System.out.println(third.toString() + " : " + result);
                }

                if (third.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
                    //if (c3 == '@') {
                    error_count++;
                    // }

                    third.replace(0, third.length(), result);
                    wr.append("" + tmp3_1 + third + tmp3_2 + " ");
                } else {
                    wr.append(tmp_str3 + " ");
                }

                first = second;
                second = third;
                third = fourth;
                fourth = fifth;
                tmp_str1 = tmp_str2;
                tmp_str2 = tmp_str3;
                tmp_str3 = tmp_str4;
                tmp_str4 = tmp_str5;
                tmp4_2 = tmp5_2;
//                System.out.println("assign tmp4_2 to " + tmp5_2 + "\n");
                symbolMark1 = symbolMark2;
                symbolMark2 = symbolMark3;
                symbolMark3 = symbolMark4;
                c3 = c4;
                c4 = c5;
                count++;
            }
            wr.close();
            System.out.println("count: " + count);
            System.out.println("error_count: " + error_count);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

//    public void processFile_v2(String fileNameIn, String outputFileName) throws Exception {
//        Scanner s = null;
//        try {
//            s = new Scanner(new BufferedReader(new FileReader(fileNameIn)));
////            s.useDelimiter(" |\\u2000|\\u2001|\\u2002|\\u2003|\\u2004|\\u2005|\\u2006|\\u2007|\\u2008|\\u2009|\\u200A|\\u200B|\\u2028|\\u2029|\\u3000|\\u00A0|\\uFEFF|\\n");
//            File outFile = new File(outputFileName);
//            if (!outFile.exists()) {
//                outFile.createNewFile();
//            }
//            FileWriter wr = new FileWriter(outFile, false);
//            //split punctuation from the word
//
//            while (s.hasNextLine()) {
//                String line = s.nextLine();
//                //String result = processSentence(line);
//                ArrayList<SpellingError> result =processSentence(line);
//                wr.append(result + "\n");
//            }
//            wr.close();
//        } catch (Exception ex) {
//            throw ex;
//        } finally {
//            if (s != null) {
//                s.close();
//            }
//        }
//    }

    /*String processSentence(String sentence) throws Exception {
        String resultSentence = "";
        int count, error_count;
        count = 0;
        error_count = 0;
//        System.out.println(process("mới", "mua", "dung", "ma", "sao"));
//        System.out.println(process("mua", "dùng", "ma", "sao", ""));

        String[] words = sentence.split(" ");
        if (words.length < 5) {
            return sentence;
        }

        StringBuilder tmp1_1, tmp1_2, tmp2_1, tmp2_2, tmp3_1, tmp3_2, tmp4_1, tmp4_2;
        tmp1_1 = new StringBuilder("");
        tmp1_2 = new StringBuilder("");
        tmp2_1 = new StringBuilder("");
        tmp2_2 = new StringBuilder("");
        tmp3_1 = new StringBuilder("");
        tmp3_2 = new StringBuilder("");
        tmp4_1 = new StringBuilder("");
        tmp4_2 = new StringBuilder("");
        String tmp_str1, tmp_str2, tmp_str3, tmp_str4, tmp_str5 = null;
        tmp_str1 = "";
        tmp_str2 = "";
        tmp_str3 = "";
        tmp_str4 = "";
//        tmp_str5 = "";
        //split punctuation from the word
        StringBuilder first = new StringBuilder(words[0]);
        tmp_str1 = first.toString();
        remove_symbol(first, tmp1_1, tmp1_2);
        StringBuilder second = new StringBuilder(words[1]);
        tmp_str2 = second.toString();
        remove_symbol(second, tmp2_1, tmp2_2);
        symbolMark1 = tmp1_2.length() > 0 || (tmp2_1.length() > 0 && first.charAt(0) != '@');
        StringBuilder third = new StringBuilder(words[2]);
        tmp_str3 = third.toString();
        char c3 = third.charAt(0);
        remove_symbol(third, tmp3_1, tmp3_2);
        symbolMark2 = (tmp2_2.length() > 0 || (tmp3_1.length() > 0 && c3 != '@'));
        StringBuilder fourth = new StringBuilder(words[3]);
        tmp_str4 = fourth.toString();
        char c4 = fourth.charAt(0);
        remove_symbol(fourth, tmp4_1, tmp4_2);
        symbolMark3 = (tmp3_2.length() > 0 || (tmp4_1.length() > 0 && c4 != '@'));
        //check the first two words
        String result = process("", "", first.toString(), second.toString(), third.toString());
        if (first.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
            String error = first.toString();
            first.replace(0, first.length(), result);
            resultSentence += "" + tmp1_1 + error + "[" + first + "]" + tmp1_2 + " ";
            error_count++;
        } else {
            resultSentence += tmp_str1 + " ";
        }
        result = process("", first.toString(), second.toString(), third.toString(), fourth.toString());
        if (second.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
            String error = second.toString();
            second.replace(0, second.length(), result);
            resultSentence += "" + tmp2_1 + error + "[" + second + "]" + tmp2_2 + " ";
            error_count++;
        } else {
            resultSentence += tmp_str2 + " ";
        }
        StringBuilder fifth = null;
        StringBuilder tmp5_1 = null, tmp5_2 = null;
        for (int i = 4; i < words.length; i++) {
            fifth = new StringBuilder(words[i]);
            tmp5_1 = new StringBuilder("");
            tmp5_2 = new StringBuilder("");
//                System.out.println("'"+fifth+"'");
            char c5;
            if (fifth.length() != 0) {
                c5 = fifth.charAt(0);
            } else {
                c5 = ' ';
            }
            tmp_str5 = fifth.toString();

//            System.out.println(first.toString() + " " + second.toString() + " " + third.toString() + " " + fourth.toString() + " " + fifth.toString());
            remove_symbol(fifth, tmp5_1, tmp5_2);
            symbolMark4 = (tmp4_2.length() > 0 || (tmp5_1.length() > 0 && c5 != '@'));
            result = process(first.toString(), second.toString(), third.toString(), fourth.toString(), fifth.toString());
//                System.out.println(tmp_str3);
            if (third.toString().toLowerCase().compareTo(result.toLowerCase()) != 0 && c3 == '@') {
//                    System.out.println(first.toString() + " " + second.toString() + " " + third.toString() + " " + fourth.toString() + " " + fifth.toString() + " : " + result);
//                    Thread.sleep(50000);
                System.out.println(third.toString() + " : " + result);
            }
            if (third.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
                //if (c3 == '@') {
                error_count++;
                // }
                String error = third.toString();
                third.replace(0, third.length(), result);
                resultSentence += "" + tmp3_1 + error + "[" + third + "]" + tmp3_2 + " ";
            } else {
                resultSentence += tmp_str3 + " ";
            }

            first = second;
            second = third;
            third = fourth;
            fourth = fifth;
            tmp_str1 = tmp_str2;
            tmp_str2 = tmp_str3;
            tmp_str3 = tmp_str4;
            tmp_str4 = tmp_str5;
            tmp1_1 = tmp2_1;
            tmp2_1 = tmp3_1;
            tmp3_1 = tmp4_1;
            tmp4_1 = tmp5_1;
            tmp1_2 = tmp2_2;
            tmp2_2 = tmp3_2;
            tmp3_2 = tmp4_2;
            tmp4_2 = tmp5_2;
//                System.out.println("assign tmp4_2 to " + tmp5_2 + "\n");
            symbolMark1 = symbolMark2;
            symbolMark2 = symbolMark3;
            symbolMark3 = symbolMark4;
            c3 = c4;
            c4 = c5;
            count++;
        }
//        check last two words
//        System.out.println("process word " + third);
        result = process(first.toString(), second.toString(), third.toString(), fourth.toString(), "");
        if (third.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
            String error = third.toString();
            third.replace(0, third.length(), result);
            resultSentence += "" + tmp3_1 + error + "[" + third + "]" + tmp3_2 + " ";
            error_count++;
        } else {
            resultSentence += tmp_str3 + " ";
        }
//        System.out.println("process word " + fourth);
        result = process(second.toString(), third.toString(), fourth.toString(), "", "");
        if (fourth.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
            String error = fourth.toString();
            fourth.replace(0, fourth.length(), result);
            resultSentence += "" + tmp4_1 + error + "[" + fourth + "]" + tmp4_2 + " ";
            error_count++;

        } else {
            resultSentence += tmp_str4 + " ";
        }
//        System.out.println("count: " + count);
        //System.out.println("error_count: " + error_count);
       /* ArrayList<Check> list = new ArrayList<>();
        String ou = "";
        String[] token = resultSentence.split(" ");
        for (int i = 0; i < token.length; i++) {
            if (token[i].indexOf("[") > -1) {
                Check a = new Check(i, token[i].substring(token[i].indexOf("[")+1, token[i].length()-1));
//                ou +=token[i]+",";
                list.add(a);
            }
//sonnv.ict@gmail.com
        }
        ou = list.toString();
        
        return ou;
        return resultSentence.trim();
    }*/
    public static void main(String[] args) throws Exception {
        String s="D:/ngramdict/";
        SpellChecker checker = new SpellChecker(s);
        //checker.namefile=checker.name_file();
        //System.out.print(checker.namefile);
        ArrayList<SpellingError> list= new ArrayList<>();
//        list = checker.Check("Hôm nay hôm nay hôm nay chời dất đẹp đẹp");
        //System.out.println(list);
        for(int i=0;i<list.size();i++){
            list.get(i).getout();
        }
//           System.out.println(checker.processSentence("Hôm nay chời dất đẹp"));
       
        
    }

    public ArrayList processLine(String sentence) throws Exception {

        String resultSentence = "";

        ArrayList<SpellingError> listcheck = new ArrayList();
        ArrayList<String> resultCheck = new ArrayList();
        ArrayList<String> resultCheckPosition = new ArrayList();

        int count, error_count;
        count = 0;
        error_count = 0;
//        System.out.println(process("mới", "mua", "dung", "ma", "sao"));
//        System.out.println(process("mua", "dùng", "ma", "sao", ""));

        String[] words = sentence.split(" ");
        if (words.length < 5) {
            //return sentence;
            return null;
        }

        StringBuilder tmp1_1, tmp1_2, tmp2_1, tmp2_2, tmp3_1, tmp3_2, tmp4_1, tmp4_2;
        tmp1_1 = new StringBuilder("");
        tmp1_2 = new StringBuilder("");
        tmp2_1 = new StringBuilder("");
        tmp2_2 = new StringBuilder("");
        tmp3_1 = new StringBuilder("");
        tmp3_2 = new StringBuilder("");
        tmp4_1 = new StringBuilder("");
        tmp4_2 = new StringBuilder("");
        String tmp_str1, tmp_str2, tmp_str3, tmp_str4, tmp_str5 = null;
        tmp_str1 = "";
        tmp_str2 = "";
        tmp_str3 = "";
        tmp_str4 = "";
//        tmp_str5 = "";
        //split punctuation from the word
        StringBuilder first = new StringBuilder(words[0]);
        tmp_str1 = first.toString();
        remove_symbol(first, tmp1_1, tmp1_2);
        StringBuilder second = new StringBuilder(words[1]);
        tmp_str2 = second.toString();
        remove_symbol(second, tmp2_1, tmp2_2);
        symbolMark1 = tmp1_2.length() > 0 || (tmp2_1.length() > 0 && first.charAt(0) != '@');
        StringBuilder third = new StringBuilder(words[2]);
        tmp_str3 = third.toString();
        char c3 = third.charAt(0);
        remove_symbol(third, tmp3_1, tmp3_2);
        symbolMark2 = (tmp2_2.length() > 0 || (tmp3_1.length() > 0 && c3 != '@'));
        StringBuilder fourth = new StringBuilder(words[3]);
        tmp_str4 = fourth.toString();
        char c4 = fourth.charAt(0);
        remove_symbol(fourth, tmp4_1, tmp4_2);
        symbolMark3 = (tmp3_2.length() > 0 || (tmp4_1.length() > 0 && c4 != '@'));
        //check the first two words
        String result = process("", "", first.toString(), second.toString(), third.toString());
        if (first.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
            String error = first.toString();
            first.replace(0, first.length(), result);
            //resultSentence += "" + tmp1_1 + error + "[" + first + "]" + tmp1_2 + " ";
            SpellingError a= new SpellingError(0,0,""+first);
            listcheck.add(a);
            resultCheckPosition.add("0");
            resultCheck.add(error + " " + second + "," + "<span style='color:red;'>" + error +"</span>" + " " +second);
            error_count++;
        } else {
            //resultSentence += tmp_str1 + " ";
        }
        result = process("", first.toString(), second.toString(), third.toString(), fourth.toString());
        if (second.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
            String error = second.toString();
            second.replace(0, second.length(), result);
            //resultSentence += "" + tmp2_1 + error + "[" + second + "]" + tmp2_2 + " ";
            SpellingError a = new SpellingError(1,1,""+second);
            listcheck.add(a);
            resultCheckPosition.add("1");
            resultCheck.add(first + " " + error + " " + third + "," + first + " " + "<span style='color:red;'>" + error +"</span>" + " " +third);
            error_count++;
        } else {
            //resultSentence += tmp_str2 + " ";
        }
        StringBuilder fifth = null;
        StringBuilder tmp5_1 = null, tmp5_2 = null;
        for (int i = 4; i < words.length; i++) {
            fifth = new StringBuilder(words[i]);
            tmp5_1 = new StringBuilder("");
            tmp5_2 = new StringBuilder("");
//                System.out.println("'"+fifth+"'");
            char c5;
            if (fifth.length() != 0) {
                c5 = fifth.charAt(0);
            } else {
                c5 = ' ';
            }
            tmp_str5 = fifth.toString();

//            System.out.println(first.toString() + " " + second.toString() + " " + third.toString() + " " + fourth.toString() + " " + fifth.toString());
            remove_symbol(fifth, tmp5_1, tmp5_2);
            symbolMark4 = (tmp4_2.length() > 0 || (tmp5_1.length() > 0 && c5 != '@'));
            result = process(first.toString(), second.toString(), third.toString(), fourth.toString(), fifth.toString());
//                System.out.println(tmp_str3);
            if (third.toString().toLowerCase().compareTo(result.toLowerCase()) != 0 && c3 == '@') {
//                    System.out.println(first.toString() + " " + second.toString() + " " + third.toString() + " " + fourth.toString() + " " + fifth.toString() + " : " + result);
//                    Thread.sleep(50000);
                System.out.println(third.toString() + " : " + result);
            }
            if (third.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
                //if (c3 == '@') {
                error_count++;
                // }
                String error = third.toString();
                third.replace(0, third.length(), result);
                //resultSentence += "" + tmp3_1 + error + "[" + third + "]" + tmp3_2 + " ";
                SpellingError a= new SpellingError(i-2,i-2,""+third);
                resultCheckPosition.add((i-2) + "");
                resultCheck.add(second + " " + error + " " + fourth + "," + second + " " + "<span style='color:red;'>" + error +"</span>" + " " +fourth);
                listcheck.add(a);
            } else {
                //resultSentence += tmp_str3 + " ";
            }

            first = second;
            second = third;
            third = fourth;
            fourth = fifth;
            tmp_str1 = tmp_str2;
            tmp_str2 = tmp_str3;
            tmp_str3 = tmp_str4;
            tmp_str4 = tmp_str5;
            tmp1_1 = tmp2_1;
            tmp2_1 = tmp3_1;
            tmp3_1 = tmp4_1;
            tmp4_1 = tmp5_1;
            tmp1_2 = tmp2_2;
            tmp2_2 = tmp3_2;
            tmp3_2 = tmp4_2;
            tmp4_2 = tmp5_2;
//                System.out.println("assign tmp4_2 to " + tmp5_2 + "\n");
            symbolMark1 = symbolMark2;
            symbolMark2 = symbolMark3;
            symbolMark3 = symbolMark4;
            c3 = c4;
            c4 = c5;
            count++;
        }
//        check last two words
//        System.out.println("process word " + third);
        result = process(first.toString(), second.toString(), third.toString(), fourth.toString(), "");
        if (third.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
            String error = third.toString();
            third.replace(0, third.length(), result);
            //resultSentence += "" + tmp3_1 + error + "[" + third + "]" + tmp3_2 + " ";
            SpellingError a= new SpellingError(words.length-2,words.length-2,""+third);
            listcheck.add(a);
            resultCheckPosition.add((words.length-2) + "");
            resultCheck.add(second + " " + error + " " + fourth + "," + second + " " + "<span style='color:red;'>" + error +"</span>" + " " +fourth);
            error_count++;
        } else {
            //resultSentence += tmp_str3 + " ";
        }
//        System.out.println("process word " + fourth);
        result = process(second.toString(), third.toString(), fourth.toString(), "", "");
        if (fourth.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
            String error = fourth.toString();
            fourth.replace(0, fourth.length(), result);
            //resultSentence += "" + tmp4_1 + error + "[" + fourth + "]" + tmp4_2 + " ";
            SpellingError a= new SpellingError(words.length-1,words.length-1,""+fourth);
            resultCheckPosition.add((words.length-1) + "");
            resultCheck.add(second + " " + third + " " + error + "," + second + " " + third + "<span style='color:red;'>" + error +"</span>");
            listcheck.add(a);
            error_count++;

        } else {
            //resultSentence += tmp_str4 + " ";
        }

        return resultCheckPosition;
    }

    public ArrayList<String> processSentence(String sentence) throws Exception {

        String resultSentence = "";

        ArrayList<SpellingError> listcheck = new ArrayList();
        ArrayList<String> resultCheck = new ArrayList<>();

        int count, error_count;
        count = 0;
        error_count = 0;
//        System.out.println(process("mới", "mua", "dung", "ma", "sao"));
//        System.out.println(process("mua", "dùng", "ma", "sao", ""));

        String[] words = sentence.split(" ");
        if (words.length < 5) {
            //return sentence;
            return null;
        }

        StringBuilder tmp1_1, tmp1_2, tmp2_1, tmp2_2, tmp3_1, tmp3_2, tmp4_1, tmp4_2;
        tmp1_1 = new StringBuilder("");
        tmp1_2 = new StringBuilder("");
        tmp2_1 = new StringBuilder("");
        tmp2_2 = new StringBuilder("");
        tmp3_1 = new StringBuilder("");
        tmp3_2 = new StringBuilder("");
        tmp4_1 = new StringBuilder("");
        tmp4_2 = new StringBuilder("");
        String tmp_str1, tmp_str2, tmp_str3, tmp_str4, tmp_str5 = null;
        tmp_str1 = "";
        tmp_str2 = "";
        tmp_str3 = "";
        tmp_str4 = "";
//        tmp_str5 = "";
        //split punctuation from the word
        StringBuilder first = new StringBuilder(words[0]);
        tmp_str1 = first.toString();
        remove_symbol(first, tmp1_1, tmp1_2);
        StringBuilder second = new StringBuilder(words[1]);
        tmp_str2 = second.toString();
        remove_symbol(second, tmp2_1, tmp2_2);
        symbolMark1 = tmp1_2.length() > 0 || (tmp2_1.length() > 0 && first.charAt(0) != '@');
        StringBuilder third = new StringBuilder(words[2]);
        tmp_str3 = third.toString();
        char c3 = third.charAt(0);
        remove_symbol(third, tmp3_1, tmp3_2);
        symbolMark2 = (tmp2_2.length() > 0 || (tmp3_1.length() > 0 && c3 != '@'));
        StringBuilder fourth = new StringBuilder(words[3]);
        tmp_str4 = fourth.toString();
        char c4 = fourth.charAt(0);
        remove_symbol(fourth, tmp4_1, tmp4_2);
        symbolMark3 = (tmp3_2.length() > 0 || (tmp4_1.length() > 0 && c4 != '@'));
        //check the first two words
        String result = process("", "", first.toString(), second.toString(), third.toString());
        if (first.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
            String error = first.toString();
            first.replace(0, first.length(), result);
            //resultSentence += "" + tmp1_1 + error + "[" + first + "]" + tmp1_2 + " ";
            SpellingError a= new SpellingError(0,0,""+first);
            listcheck.add(a);

            resultCheck.add(error + " " + second + "-" + "<span style='color:red;'>" + error +"</span>" + " " +second);
            error_count++;
        } else {
            //resultSentence += tmp_str1 + " ";
        }
        result = process("", first.toString(), second.toString(), third.toString(), fourth.toString());
        if (second.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
            String error = second.toString();
            second.replace(0, second.length(), result);
            //resultSentence += "" + tmp2_1 + error + "[" + second + "]" + tmp2_2 + " ";
            SpellingError a = new SpellingError(1,1,""+second);
            listcheck.add(a);

            resultCheck.add(first + " " + error + " " + third + "-" + first + " " + "<span style='color:red;'>" + error +"</span>" + " " +third);
            error_count++;
        } else {
            //resultSentence += tmp_str2 + " ";
        }
        StringBuilder fifth = null;
        StringBuilder tmp5_1 = null, tmp5_2 = null;
        for (int i = 4; i < words.length; i++) {
            fifth = new StringBuilder(words[i]);
            tmp5_1 = new StringBuilder("");
            tmp5_2 = new StringBuilder("");
//                System.out.println("'"+fifth+"'");
            char c5;
            if (fifth.length() != 0) {
                c5 = fifth.charAt(0);
            } else {
                c5 = ' ';
            }
            tmp_str5 = fifth.toString();

//            System.out.println(first.toString() + " " + second.toString() + " " + third.toString() + " " + fourth.toString() + " " + fifth.toString());
            remove_symbol(fifth, tmp5_1, tmp5_2);
            symbolMark4 = (tmp4_2.length() > 0 || (tmp5_1.length() > 0 && c5 != '@'));
            result = process(first.toString(), second.toString(), third.toString(), fourth.toString(), fifth.toString());
//                System.out.println(tmp_str3);
            if (third.toString().toLowerCase().compareTo(result.toLowerCase()) != 0 && c3 == '@') {
//                    System.out.println(first.toString() + " " + second.toString() + " " + third.toString() + " " + fourth.toString() + " " + fifth.toString() + " : " + result);
//                    Thread.sleep(50000);
                System.out.println(third.toString() + " : " + result);
            }
            if (third.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
                //if (c3 == '@') {
                error_count++;
                // }
                String error = third.toString();
                third.replace(0, third.length(), result);
                //resultSentence += "" + tmp3_1 + error + "[" + third + "]" + tmp3_2 + " ";
                SpellingError a= new SpellingError(i-2,i-2,""+third);

                resultCheck.add(second + " " + error + " " + fourth + "-" + second + " " + "<span style='color:red;'>" + error +"</span>" + " " +fourth);
                listcheck.add(a);
            } else {
                //resultSentence += tmp_str3 + " ";
            }

            first = second;
            second = third;
            third = fourth;
            fourth = fifth;
            tmp_str1 = tmp_str2;
            tmp_str2 = tmp_str3;
            tmp_str3 = tmp_str4;
            tmp_str4 = tmp_str5;
            tmp1_1 = tmp2_1;
            tmp2_1 = tmp3_1;
            tmp3_1 = tmp4_1;
            tmp4_1 = tmp5_1;
            tmp1_2 = tmp2_2;
            tmp2_2 = tmp3_2;
            tmp3_2 = tmp4_2;
            tmp4_2 = tmp5_2;
//                System.out.println("assign tmp4_2 to " + tmp5_2 + "\n");
            symbolMark1 = symbolMark2;
            symbolMark2 = symbolMark3;
            symbolMark3 = symbolMark4;
            c3 = c4;
            c4 = c5;
            count++;
        }
//        check last two words
//        System.out.println("process word " + third);
        result = process(first.toString(), second.toString(), third.toString(), fourth.toString(), "");
        if (third.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
            String error = third.toString();
            third.replace(0, third.length(), result);
            //resultSentence += "" + tmp3_1 + error + "[" + third + "]" + tmp3_2 + " ";
            SpellingError a= new SpellingError(words.length-2,words.length-2,""+third);
            listcheck.add(a);

            resultCheck.add(second + " " + error + " " + fourth + "-" + second + " " + "<span style='color:red;'>" + error +"</span>" + " " +fourth);
            error_count++;
        } else {
            //resultSentence += tmp_str3 + " ";
        }
//        System.out.println("process word " + fourth);
        result = process(second.toString(), third.toString(), fourth.toString(), "", "");
        if (fourth.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
            String error = fourth.toString();
            fourth.replace(0, fourth.length(), result);
            //resultSentence += "" + tmp4_1 + error + "[" + fourth + "]" + tmp4_2 + " ";
            SpellingError a= new SpellingError(words.length-1,words.length-1,""+fourth);

            resultCheck.add(second + " " + third + " " + error + "-" + second + " " + third + "<span style='color:red;'>" + error +"</span>");
            listcheck.add(a);
            error_count++;

        } else {
            //resultSentence += tmp_str4 + " ";
        }
//        System.out.println("count: " + count);
        //System.out.println("error_count: " + error_count);
       /* ArrayList<Check> list = new ArrayList<>();
        String ou = "";
        String[] token = resultSentence.split(" ");
        for (int i = 0; i < token.length; i++) {
            if (token[i].indexOf("[") > -1) {
                Check a = new Check(i, token[i].substring(token[i].indexOf("[")+1, token[i].length()-1));
//                ou +=token[i]+",";
                list.add(a);
            }
//sonnv.ict@gmail.com
        }
        ou = list.toString();
        
        return ou;*/
        //return resultSentence.trim();
        return resultCheck;
    }

    public ArrayList<Suggest> processParagraph(String sentence) throws Exception {

        String resultSentence = "";

        ArrayList<SpellingError> listcheck = new ArrayList();
        ArrayList<Suggest> resultCheck = new ArrayList<>();

        int count, error_count;
        count = 0;
        error_count = 0;
//        System.out.println(process("mới", "mua", "dung", "ma", "sao"));
//        System.out.println(process("mua", "dùng", "ma", "sao", ""));

        String[] words = sentence.split(" ");
        if (words.length < 5) {
            //return sentence;
            return null;
        }

        StringBuilder tmp1_1, tmp1_2, tmp2_1, tmp2_2, tmp3_1, tmp3_2, tmp4_1, tmp4_2;
        tmp1_1 = new StringBuilder("");
        tmp1_2 = new StringBuilder("");
        tmp2_1 = new StringBuilder("");
        tmp2_2 = new StringBuilder("");
        tmp3_1 = new StringBuilder("");
        tmp3_2 = new StringBuilder("");
        tmp4_1 = new StringBuilder("");
        tmp4_2 = new StringBuilder("");
        String tmp_str1, tmp_str2, tmp_str3, tmp_str4, tmp_str5 = null;
        tmp_str1 = "";
        tmp_str2 = "";
        tmp_str3 = "";
        tmp_str4 = "";
//        tmp_str5 = "";
        //split punctuation from the word
        StringBuilder first = new StringBuilder(words[0]);
        tmp_str1 = first.toString();
        remove_symbol(first, tmp1_1, tmp1_2);
        StringBuilder second = new StringBuilder(words[1]);
        tmp_str2 = second.toString();
        remove_symbol(second, tmp2_1, tmp2_2);
        symbolMark1 = tmp1_2.length() > 0 || (tmp2_1.length() > 0 && first.charAt(0) != '@');
        StringBuilder third = new StringBuilder(words[2]);
        tmp_str3 = third.toString();
        char c3 = third.charAt(0);
        remove_symbol(third, tmp3_1, tmp3_2);
        symbolMark2 = (tmp2_2.length() > 0 || (tmp3_1.length() > 0 && c3 != '@'));
        StringBuilder fourth = new StringBuilder(words[3]);
        tmp_str4 = fourth.toString();
        char c4 = fourth.charAt(0);
        remove_symbol(fourth, tmp4_1, tmp4_2);
        symbolMark3 = (tmp3_2.length() > 0 || (tmp4_1.length() > 0 && c4 != '@'));
        //check the first two words
        String result = process("", "", first.toString(), second.toString(), third.toString());
        if (first.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
            String error = first.toString();
            first.replace(0, first.length(), result);
            //resultSentence += "" + tmp1_1 + error + "[" + first + "]" + tmp1_2 + " ";
            SpellingError a= new SpellingError(0,0,""+first);
            listcheck.add(a);

            Suggest suggest = new Suggest();
            suggest.setError(error);
            suggest.setSuggest(new String(first));
            suggest.setPosition(0);

            resultCheck.add(suggest);

            error_count++;
        } else {
            //resultSentence += tmp_str1 + " ";
        }
        result = process("", first.toString(), second.toString(), third.toString(), fourth.toString());
        if (second.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
            String error = second.toString();
            second.replace(0, second.length(), result);
            //resultSentence += "" + tmp2_1 + error + "[" + second + "]" + tmp2_2 + " ";
            SpellingError a = new SpellingError(1,1,""+second);
            listcheck.add(a);

            Suggest suggest = new Suggest();
            suggest.setError(error);
            suggest.setPosition(1);
            suggest.setSuggest(new String(second));

            resultCheck.add(suggest);

            error_count++;
        } else {
            //resultSentence += tmp_str2 + " ";
        }
        StringBuilder fifth = null;
        StringBuilder tmp5_1 = null, tmp5_2 = null;
        for (int i = 4; i < words.length; i++) {
            fifth = new StringBuilder(words[i]);
            tmp5_1 = new StringBuilder("");
            tmp5_2 = new StringBuilder("");
//                System.out.println("'"+fifth+"'");
            char c5;
            if (fifth.length() != 0) {
                c5 = fifth.charAt(0);
            } else {
                c5 = ' ';
            }
            tmp_str5 = fifth.toString();

//            System.out.println(first.toString() + " " + second.toString() + " " + third.toString() + " " + fourth.toString() + " " + fifth.toString());
            remove_symbol(fifth, tmp5_1, tmp5_2);
            symbolMark4 = (tmp4_2.length() > 0 || (tmp5_1.length() > 0 && c5 != '@'));
            result = process(first.toString(), second.toString(), third.toString(), fourth.toString(), fifth.toString());
//                System.out.println(tmp_str3);
            if (third.toString().toLowerCase().compareTo(result.toLowerCase()) != 0 && c3 == '@') {
//                    System.out.println(first.toString() + " " + second.toString() + " " + third.toString() + " " + fourth.toString() + " " + fifth.toString() + " : " + result);
//                    Thread.sleep(50000);
                System.out.println(third.toString() + " : " + result);
            }
            if (third.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
                //if (c3 == '@') {
                error_count++;
                // }
                String error = third.toString();
                third.replace(0, third.length(), result);
                //resultSentence += "" + tmp3_1 + error + "[" + third + "]" + tmp3_2 + " ";
                SpellingError a= new SpellingError(i-2,i-2,""+third);

                Suggest suggest = new Suggest();
                suggest.setError(error);
                suggest.setPosition(i-2);
                suggest.setSuggest(new String(third));
                resultCheck.add(suggest);
                listcheck.add(a);
            } else {
                //resultSentence += tmp_str3 + " ";
            }

            first = second;
            second = third;
            third = fourth;
            fourth = fifth;
            tmp_str1 = tmp_str2;
            tmp_str2 = tmp_str3;
            tmp_str3 = tmp_str4;
            tmp_str4 = tmp_str5;
            tmp1_1 = tmp2_1;
            tmp2_1 = tmp3_1;
            tmp3_1 = tmp4_1;
            tmp4_1 = tmp5_1;
            tmp1_2 = tmp2_2;
            tmp2_2 = tmp3_2;
            tmp3_2 = tmp4_2;
            tmp4_2 = tmp5_2;
//                System.out.println("assign tmp4_2 to " + tmp5_2 + "\n");
            symbolMark1 = symbolMark2;
            symbolMark2 = symbolMark3;
            symbolMark3 = symbolMark4;
            c3 = c4;
            c4 = c5;
            count++;
        }
//        check last two words
//        System.out.println("process word " + third);
        result = process(first.toString(), second.toString(), third.toString(), fourth.toString(), "");
        if (third.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
            String error = third.toString();
            third.replace(0, third.length(), result);
            //resultSentence += "" + tmp3_1 + error + "[" + third + "]" + tmp3_2 + " ";
            SpellingError a= new SpellingError(words.length-2,words.length-2,""+third);
            listcheck.add(a);

            Suggest suggest = new Suggest();
            suggest.setError(error);
            suggest.setPosition(words.length - 2);
            suggest.setSuggest(new String(third));
            resultCheck.add(suggest);
            error_count++;
        } else {
            //resultSentence += tmp_str3 + " ";
        }
//        System.out.println("process word " + fourth);
        result = process(second.toString(), third.toString(), fourth.toString(), "", "");
        if (fourth.toString().toLowerCase().compareTo(result.toLowerCase()) != 0) {
            String error = fourth.toString();
            fourth.replace(0, fourth.length(), result);
            //resultSentence += "" + tmp4_1 + error + "[" + fourth + "]" + tmp4_2 + " ";
            SpellingError a= new SpellingError(words.length-1,words.length-1,""+fourth);

            Suggest suggest = new Suggest();
            suggest.setError(error);
            suggest.setPosition(words.length - 1);
            suggest.setSuggest(new String(fourth));
            resultCheck.add(suggest);
            listcheck.add(a);
            error_count++;

        } else {
            //resultSentence += tmp_str4 + " ";
        }
//        System.out.println("count: " + count);
        //System.out.println("error_count: " + error_count);
       /* ArrayList<Check> list = new ArrayList<>();
        String ou = "";
        String[] token = resultSentence.split(" ");
        for (int i = 0; i < token.length; i++) {
            if (token[i].indexOf("[") > -1) {
                Check a = new Check(i, token[i].substring(token[i].indexOf("[")+1, token[i].length()-1));
//                ou +=token[i]+",";
                list.add(a);
            }
//sonnv.ict@gmail.com
        }
        ou = list.toString();

        return ou;*/
        //return resultSentence.trim();
        return resultCheck;
    }

    public String name_file(String s){
        //this.namefile=s;
        return s;
    }
//    public ArrayList Check(String s) {
//        ArrayList<SpellingError> list = new ArrayList();
//        try {
//            list = processSentence(s);
//        } catch (Exception ex) {
//
//        }
//
//        return list;
//    }
}
