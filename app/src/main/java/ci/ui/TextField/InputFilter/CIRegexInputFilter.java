package ci.ui.TextField.InputFilter;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by KevinCheng on 2016/12.
 */

public class CIRegexInputFilter implements InputFilter {

    private String m_Regex = "";

    public CIRegexInputFilter(String regex){
        m_Regex = regex;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        boolean isFilter = false;

        String newString = source.toString();
        Set<Character> strSet = new HashSet<>();
        for(Character c: newString.toCharArray()){
            if(false == strSet.add(c)){
                continue;
            }
            if(false == c.toString().matches(m_Regex)){
                newString = newString.replace(c.toString(),"");
                isFilter = true;
            }
        }
        if(true == isFilter){
            return newString;
        } else {
            return null;
        }
    }
}
