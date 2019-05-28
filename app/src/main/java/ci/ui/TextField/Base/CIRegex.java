package ci.ui.TextField.Base;

/**
 * Created by kevincheng on 2016/11/25.
 * 提供輸入欄位作為判斷所需的正規表達式條件
 */

public class CIRegex {

    /**字母及數字及電子郵件用符號*/
    public final static String REGEX_ENGLISH_NUMBER_EMIAL = "[0-9a-zA-Z@\\-_.]+";

    /**字母及數字*/
    public final static String REGEX_ENGLISH_NUMBER = "[0-9a-zA-Z]+";

    /**字母及空格*/
    public final static String REGEX_ENGLISH_SPACE = "[a-zA-Z\\u0020]+";

    /**數字*/
    public final static String REGEX_NUMBER = "[0-9]+";

    /**同時擁有子母及數字*/
    public final static String REGEX_ENGLISH_AND_NUMBER = "[0-9]+|[a-zA-Z]+";


}
