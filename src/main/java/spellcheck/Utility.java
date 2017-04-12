/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spellcheck;

/**
 *
 * @author TUNG
 */
public class Utility {
    static int addFunction(int a, int b){
        return a+b;
    }
    
    //check if a word is foreignword or not
    static boolean foreignTest(String word) {
        word = word.toLowerCase();
        if (word.indexOf("w") >= 0) {
            return true;
        }
        if (word.indexOf("f") >= 0) {
            return true;
        }
        if (word.indexOf("j") >= 0) {
            return true;
        }
        if (word.indexOf("z") >= 0 && word.compareTo(("bazan")) == 0) {
            return true;
        }

        if (word.indexOf("ar") >= 0) {
            return true;
        }
        if (word.indexOf("av") >= 0) {
            return true;
        }
        if (word.indexOf("as") >= 0) {
            return true;
        }
        if (word.indexOf("ag") >= 0) {
            return true;
        }

        if (word.indexOf("br") >= 0) {
            return true;
        }

        if (word.indexOf("ce") >= 0) {
            return true;
        }
        if (word.indexOf("ci") >= 0) {
            return true;
        }
        if (word.indexOf("ck") >= 0) {
            return true;
        }

        if (word.indexOf("ec") >= 0) {
            return true;
        }
        if (word.indexOf("er") >= 0) {
            return true;
        }
        if (word.indexOf("ev") >= 0) {
            return true;
        }
        if (word.indexOf("es") >= 0) {
            return true;
        }
        if (word.indexOf("ea") >= 0) {
            return true;
        }
        if (word.indexOf("et") >= 0) {
            return true;
        }
        if (word.indexOf("el") >= 0) {
            return true;
        }
        if (word.indexOf("ey") >= 0) {
            return true;
        }

        if (word.indexOf("gl") >= 0) {
            return true;
        }

        if (word.indexOf("ic") >= 0) {
            return true;
        }
        if (word.indexOf("ir") >= 0) {
            return true;
        }
        if (word.indexOf("is") >= 0) {
            return true;
        }
        if (word.indexOf("iv") >= 0) {
            return true;
        }
        if (word.indexOf("id") >= 0) {
            return true;
        }
        if (word.indexOf("il") >= 0) {
            return true;
        }

        if (word.indexOf("ka") >= 0) {
            return true;
        }

        if (word.indexOf("nn") >= 0) {
            return true;
        }
        if (word.indexOf("nd") >= 0) {
            return true;
        }
        if (word.indexOf("nc") >= 0) {
            return true;
        }

        if (word.indexOf("pp") >= 0) {
            return true;
        }
        if (word.indexOf("pl") >= 0) {
            return true;
        }

        if (word.indexOf("ov") >= 0) {
            return true;
        }
        if (word.indexOf("yl") >= 0) {
            return true;
        }

        if (word.indexOf("st") >= 0) {
            return true;
        }
        if (word.indexOf("ub") >= 0) {
            return true;
        }
        if (word.indexOf("ur") >= 0) {
            return true;
        }
        if (word.indexOf("us") >= 0) {
            return true;
        }
        if (word.indexOf("uv") >= 0) {
            return true;
        }
        if (word.indexOf("ud") >= 0) {
            return true;
        }

        if (word.indexOf("ion") >= 0) {
            return true;
        }
        return false;
    }
    
    //sorting an array of string in ascending order
    static void quickSort(String array[], int startIndex, int endIndex)//array sorting
    {
        if (startIndex >= endIndex) {
            return;
        }
        int i = startIndex;
        int j = endIndex;
        String x = array[(i + j) / 2];//x=A[random(l-r)]
        while (i <= j) {
            while (array[i].compareTo(x) < 0) {
                i++;
            }
            while (array[j].compareTo(x) < 0) {
                j--;
            }
            if (i <= j) {
                String temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                i++;
                j--;
            }
        }
        quickSort(array, startIndex, j);
        quickSort(array, i, endIndex);
//        return;
    }
    
    //check if a character is a symbol
    static boolean isSymbol(char ch) {
        return StringConstant.symbolString.indexOf(ch) >= 0;
    }

    // Kiểm tra phải là ký hiệu hết câu hay không
    static boolean isEndSymbol(char ch) {
        if (StringConstant.endSentenceCharacter.indexOf(ch) >= 0) {
            return true;
        } else {
            return false;
        }
    }
    // Kiểm tra chữ viết hoa hay không
    static boolean isUpperCharacter(char ch) {
        int tmp = StringConstant.upperCharacter.indexOf(ch);
        if (tmp >= 0) {
            return true;
        } else {
            return false;
        }
    }

    // Kiểm tra ký tự có phải là kí tự nối (-/..) hay không
    static boolean isMiddleSymbol(char ch) {
        if (StringConstant.middleSymbol.indexOf(ch) >= 0) {
            return true;
        } else {
            return false;
        }
    }
    
    //check if a character is a vowel
    static boolean isVowel(char ch) {
        int temp = StringConstant.vowel.indexOf(ch);
        if (temp >= 0) {
            return true;
        } else {
            return false;
        }
    }

    //check if a string only contains character or not
    static boolean isFullCharacter(String word) {
        int n = word.length();
        if (n <= 1) {
            return false;
        }
        for (int i = 0; i < n - 1; i++) {
            int tmp = StringConstant.fullCharacter.indexOf(word.charAt(i));
            //if(  fullCharacter.indexOf(word[i]) < 0 || fullCharacter.indexOf(word[i]) >= fullCharacter.length())
            if (tmp < 0) {
                return false;
            }
        }
        return false;
    }
    
    //extract sign of a word. "biếc" -> "biêcs"
    static String extractsign(String text) {
        String temp = "";
        char textsign = 0;
        boolean kt = false;
        int k, t;
        for (int i = 0; i < text.length(); i++) {
            k = StringConstant.source.indexOf(text.charAt(i));
            if ((k >= 0) && (k < StringConstant._length)) {
                t = StringConstant.vnCharacter.indexOf(StringConstant.dest.charAt(k));
                if ((t >= 0) && (t < StringConstant._length)) {
                    temp = temp + StringConstant.vnCharacterExtractsign[t];
                } else {
                    temp = temp + StringConstant.dest.charAt(k);
                }
                textsign = StringConstant.sign.charAt(k);
                kt = true;
            } else {
                t = StringConstant.vnCharacter.indexOf(text.charAt(i));
                if ((t >= 0) && (t < StringConstant._length)) {
                    temp = temp + StringConstant.vnCharacterExtractsign[t];
                } else {
                    temp = temp + text.charAt(i);
                }
            }
        }
        if (kt) {
            temp = temp + textsign;
        }
        return temp;
    }
    
    //calculate number of uppercase characters in text
    static int upperNumber(String text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (StringConstant.upperCharacter.indexOf(text.charAt(i)) >= 0) {
                count++;
            }
            if (count >= 2) {
                return 2;
            }
        }
        return count;
    }

    //binary search to get index of a word in array
    static int myBinarySearch(String syllable[], String word, int first, int last) {
        if (last < first) {
            return -1;
        }
        int mid = (first + last) / 2;
        if (syllable[mid].equals(word)) {
            return mid;
        }
        if (word.compareTo(syllable[mid]) < 0) {
            return myBinarySearch(syllable, word, first, mid - 1);
        } else {
            return myBinarySearch(syllable, word, mid + 1, last);
        }
    }
    
     // max of two integers
    static int max2(int a, int b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
    }
    // min 3 số
    static int min3number(int a, int b, int c) {
        if (a > b) {
            if (b > c) {
                return c;
            } else {
                return b;
            }
        } else {
            if (a > c) {
                return c;
            } else {
                return a;
            }
        }
    }

    // min của 2 số
    static int min2number(int a, int b) {
        if (a > b) {
            return b;
        }
        return a;
    }
}
