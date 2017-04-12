/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spellcheck;

/**
 *
 * @author TUNG
 */
public class StringConstant {
    //    private static final String number_char = "01123456789";
//    private static final String normal_chars_str =
//            "0-9A-ZĂÂĐÊÔƠƯÀẢÃÁẠĂẰẲẴẮẶÂẦẨẪẤẬĐÈẺẼÉẸÊỀỂỄẾỆÌỈĨÍỊÒỎÕÓỌÔỒỔỖỐỘƠỜỞỠỚỢÙỦŨÚỤƯỪỬỮỨỰỲỶỸÝỴa-zăâđêôơưàảãáạăằẳẵắặâầẩẫấậđèẻẽéẹêềểễếệìỉĩíịòỏõóọôồổỗốộơờởỡớợùủũúụưừửữứựỳỷỹýỵ";
//    private static final String normal_chars_str_nonumber =
//            "A-ZĂÂĐÊÔƠƯÀẢÃÁẠĂẰẲẴẮẶÂẦẨẪẤẬĐÈẺẼÉẸÊỀỂỄẾỆÌỈĨÍỊÒỎÕÓỌÔỒỔỖỐỘƠỜỞỠỚỢÙỦŨÚỤƯỪỬỮỨỰỲỶỸÝỴa-zăâđêôơưàảãáạăằẳẵắặâầẩẫấậđèẻẽéẹêềểễếệìỉĩíịòỏõóọôồổỗốộơờởỡớợùủũúụưừửữứựỳỷỹýỵ";
//    private static final String normal_chars_str_withsymbols =
//            "\\.,-_0123456789ABCDEFGHIJKLMNOPQRSTXYUWZVĂÂĐÊÔƠƯÀẢÃÁẠĂẰẲẴẮẶÂẦẨẪẤẬĐÈẺẼÉẸÊỀỂỄẾỆÌỈĨÍỊÒỎÕÓỌÔỒỔỖỐỘƠỜỞỠỚỢÙỦŨÚỤƯỪỬỮỨỰỲỶỸÝỴzxcvbnmasdfghjklqwertyuiopăâđêôơưàảãáạăằẳẵắặâầẩẫấậđèẻẽéẹêềểễếệìỉĩíịòỏõóọôồổỗốộơờởỡớợùủũúụưừửữứựỳỷỹýỵ";
    //string contain most common symbols

    static final String symbolString = "\",\\.'\\*?/:;\\-_+=#$%&Z*!<>\\[{}\\]()&^•€“”…...";
    //list of common abbreviations or titles in Vietnamese
    static final String titleList[] = {"A1", "GD&ĐT", "GDĐT", "GS", "GS.TS", "GS.TS.", "GS.TSKH", "GS.TSKH.", "HCM", "HN", "HĐND", "I", "II",
        "III", "IV", "IX", "Mr", "Mr.", "Mrs.", "Ms", "NV1", "NV2", "PGS", "PGS.TS", "PGS.TS.", "PGS.TSKH",
        "PGS.TSKH.", "TP.", "TP.HCM", "TS", "TS.", "ThS.", "Tp.", "U.N", "UBND", "V", "VI", "VII", "VIII", "X",
        "XHNV", "XI", "XII", "XIII", "XIV", "XIX", "XV", "XVI", "XVII", "XVIII", "XX", "XXI", "a.m", "gs.ts",
        "p.m", "pgs.ts", "tp.", "ĐH", "ĐHQG", "ĐHQGHN",};
    // end-sentence symbols
    static final String endSentenceCharacter = ".!?;:…";
    //middle symbols
    static final String middleSymbol = "-/\\><[],";
    // uppercase characters
    static final String upperCharacter = "ABCDEFGHIJKLMNOPQRSTUVWXYZÀẢÃÁẠĂẰẲẴẮẶÂẦẨẪẤẬĐÈẺẼÉẸÊỀỂỄẾỆÌỈĨÍỊÒỎÕÓỌÔỒỔỖỐỘƠỜỞỠỚỢÙỦŨÚỤƯỪỬỮỨỰỲỶỸÝỴ";
    // lowercase characters
//    private static final String lower_char_str = "abcdefghijklmnopqrstuvwxyzàảãáạăằẳẵắặâầẩẫấậđèẻẽéẹêềểễếệìỉĩíịòỏõóọôồổỗốộơờởỡớợùủũúụưừửữứựỳỷỹýỵ";
    //regular expression for date
    static final String date = "(([0-9]{2}|[0-9])(-|/)([0-9]{2}|[0-9])(-|/)([0-9]{4}))(.*)";
    //regular expression for email
    static final String emailcheck = "[a-zA-Z0-9\",\\.'\\*?/:;\\-_+=#$%&*!<>\\[{}\\]()&^•€“”…]+" + "@" + "[a-zA-Z0-9\",\\.'\\*?/:;\\-_+=#$%&Z*!<>\\[{}\\]()&^•€“”…]+[a-zA-Z]+(.*)";
    //regular expression for website
    static final String webcheck = "(http://|https://|www.|//|ftp).*(.com|.vn|.org)?(.*)";
    //regular expression for number
    static final String numbercheck = "[" + symbolString + "]*" + "\\-?\\d+\\.?(\\d+)?(k|g|MB|GB|Mb|,|...|%|h)?" + "[" + symbolString + "]*";
    // Số lượng ký tự có dấu VD: ô,ơ,ò....
    static final int _length = 60;
    // Các chữ cái có dấu
    static final String source = "àảãáạằẳẵắặầẩẫấậèẻẽéẹềểễếệìỉĩíịòỏõóọồổỗốộờởỡớợùủũúụừửữứựỳỷỹýỵ";
    // các chữ cái thu được sau khi bỏ dấu ở chữ cái có dấu trên
    static final String dest = "aaaaaăăăăăâââââeeeeeêêêêêiiiiioooooôôôôôơơơơơuuuuuưưưưưyyyyy";
    // Dấu thu được ở các chữ cái có dấu trên
    static final String sign = "frxsjfrxsjfrxsjfrxsjfrxsjfrxsjfrxsjfrxsjfrxsjfrxsjfrxsjfrxsj";
    //all characters
    static final String fullCharacter = "ABCDEGHIKLMNOPQRSTUVXYÀẢÃÁẠĂẰẲẴẮẶÂẦẨẪẤẬĐÈẺẼÉẸÊỀỂỄẾỆÌỈĨÍỊÒỎÕÓỌÔỒỔỖỐỘƠỜỞỠỚỢÙỦŨÚỤƯỪỬỮỨỰỲỶỸÝỴabcdeghiklmnopqrstuvxyàảãáạăằẳẵắặâầẩẫấậđèẻẽéẹêềểễếệìỉĩíịòỏõóọôồổỗốộơờởỡớợùủũúụưừửữứựỳỷỹýỵ";
    //all vietnamese vowel
    static final String vowel = "aăâeêioôơuưy";
    // Số lượng chữ cái loại tiếng việt vd: ă,â...
//    private static final int _length_str = 7;
    //vietnamese-only characters
    static final String vnCharacter = "ăâêôơưđ";
    //string of combination of character to create vietnamese-only characters (Telex)
    static final String vnCharacterExtractsign[] = {"aw", "aa", "ee", "oo", "ow", "uw", "dd"};
    // Vị trí các kí tự ở trên bàn phím
    static final String keyboard[] = {"qwertyuiop[]", "asdfghjkl;'", "zxcvbnm,./"};
    // Số lượng lỗi phụ âm đầu
    static final int startErrorLength = 31;
    // Số lượng lỗi phụ âm hoặc nguyên âm cuối
    static final int endErrorLength = 106;
    // Lỗi thường gặp ở phụ âm đầu
    static final String startError[] = {"ch", "tr", "d", "gi", "d", "gi", "nh", "d", "gi", "r",
        "d", "gi", "v", "hw", "ng", "qu", "w", "l", "n", "s", "x", "dì", "gì",
        "dĩ", "gĩ", "dỉ", "gỉ", "dị", "gị", "dí", "gí"};
    // Đánh dấu các phụ âm đầu thường nhầm lẫn với nhau
    // Với những phụ âm nào thường nhần lẫn thì giá trị của nó bằng nhau
    static final int startErrorCheck[] = {1, 1, 2, 2, 3, 3, 3, 4, 4, 4,
        5, 5, 5, 6, 6, 6, 6, 7, 7, 8, 8, 9, 9,
        10, 10, 11, 11, 12, 12, 13, 13};
    // Lỗi thường gặp ở nguyên âm hoặc phụ âm cuối
    static final String end_error[] = {"c", "t", "n", "ng", "ai", "ay", "ài", "ày", "ái", "áy", "ải", "ảy", "ãi", "ãy", "ại", "ạy",
        "em", "êm", "èm", "ềm", "ém", "ếm", "ẻm", "ểm", "ẽm", "ễm", "ẹm", "ệm",
        "êch", "êt", "ềch", "ềt", "ếch", "ết", "ểch", "ểt", "ễch", "ễt", "ệch", "ệt",
        "im", "iêm", "ìm", "iềm", "ím", "iếm", "ỉm", "iểm", "ĩm", "iễm", "ịm", "iệm",
        "iêu", "iu", "iều", "ìu", "iếu", "íu", "iểu", "ỉu", "iễu", "ĩu", "iệu", "ịu",
        "iêu", "ươu", "iều", "ườu", "iếu", "ướu", "iểu", "ưởu", "iễu", "ưỡu", "iệu", "ượu",
        "oai", "oi", "oài", "òi", "oái", "ói", "oải", "ỏi", "oãi", "õi", "oại", "ọi",
        "om", "ôm", "ơm", "òm", "ồm", "ờm", "óm", "ốm", "ớm", "ỏm", "ổm", "ởm", "õm", "ỗm", "ỡm", "ọm", "ộm", "ợm"
    };
    // Đánh dấu các phụ âm nguyên âm cuối thường nhầm lẫn với nhau
    // Với những phụ âm nguyên âm nào thường nhần lẫn thì giá trị của nó bằng nhau
    static final int endErrorCheck[] = {1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8,
        9, 9, 10, 10, 11, 11, 12, 12, 13, 13, 14, 14,
        15, 15, 16, 16, 17, 17, 18, 18, 19, 19, 20, 20,
        21, 21, 22, 22, 23, 23, 24, 24, 25, 25, 26, 26,
        27, 27, 28, 28, 29, 29, 30, 30, 31, 31, 32, 32,
        33, 33, 34, 34, 35, 35, 36, 36, 37, 37, 38, 38,
        39, 39, 40, 40, 41, 41, 42, 42, 43, 43, 44, 44,
        45, 45, 45, 46, 46, 46, 47, 47, 47, 48, 48, 48, 49, 49, 49, 50, 50, 50
    };
    //Số lượng lỗi phát âm dấu 
    static final int signErrorLength = 5;
    //Các kí tự có dấu thường lỗi
    // String signError = "ảãẳẵẩẫẻẽểễỉĩỏõổỗởỡủũửữỷỹãạẵặẫậẽẹễệĩịõọỗộỡợũụữựỹỵ";
    static final String signError = "fsrxj";
    // Lỗi từ địa phương
    static final String localCheck[] = {"in", "ìn", "ín", "ỉn", "ĩn", "ịn", "ưng", "ừng", "ứng", "ững", "ửng", "ựng", "iu", "ìu", "íu", "ỉu", "ĩu", "ịu"};
    static final String localReplace[] = {"iên", "iền", "iến", "iển", "iễn", "iện", "âng", "ầng", "ấng", "ẫng", "ẩng", "ậng", "ưu", "ừu", "ứu", "ửu", "ữu", "ựu"};
    static final int localErrorLength = 18;
}
