package ci.ui.TextField;

import android.os.Bundle;
import android.text.Editable;

import com.chinaairlines.mobile30.R;

import ci.ui.TextField.Base.CITextFieldFragment;

import static ci.ui.TextField.Base.CIRegex.REGEX_ENGLISH_NUMBER;

/**
 * 2017-02-09 CR
 * 護照中文姓名需檢查使用者是否輸入中文
 * 此欄位不可輸入英文或數字
 * 檢查邏輯加在原輸入完成後判斷
 *
 * Created by jlchen on 2017/2/9.
 */

public class CIOnlyChineseTextFieldFragmnet extends CITextFieldFragment {

    public static CIOnlyChineseTextFieldFragmnet newInstance(String hint){
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, hint);
        CIOnlyChineseTextFieldFragmnet fragment = new CIOnlyChineseTextFieldFragmnet();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void afterTextChanged(Editable editable) {

        setErrorMsg(getString(R.string.member_login_input_correvt_format_msg));

        setIsFormatCorrect(true);

        String str = editable.toString();
        for ( int i = 0; i < str.length(); i ++ ){
            if(str.substring(i, i+1).matches(REGEX_ENGLISH_NUMBER)){
                setIsFormatCorrect(false);
                break;
            }
        }

        /**長度如果等於零就直接設為正確格式*/
        if(0 == editable.length()){
            setIsFormatCorrect(true);
        }

        /**最後才會判斷另設監聽（特殊）*/
        super.afterTextChanged(editable);
    }
}
