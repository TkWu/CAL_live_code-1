package ci.ui.TextField;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.View;

import com.chinaairlines.mobile30.R;

import ci.ui.TextField.Base.CITextFieldFragment;

import static ci.ui.TextField.Base.CIRegex.REGEX_ENGLISH_NUMBER_EMIAL;

/**
 * Created by kevin on 2016/2/20.
 */
public class CIEmailTextFieldFragment extends CITextFieldFragment {
    public static CIEmailTextFieldFragment newInstance(String hint){
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, hint);
        /**限制輸入為字母及數字及電子郵件用符號*/
        bundle.putString(FILTER_MODE, EFilterMode.REGEX.name());
        bundle.putString(REGEX, REGEX_ENGLISH_NUMBER_EMIAL);
        CIEmailTextFieldFragment fragment = new CIEmailTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        super.afterTextChanged(editable);

        setErrorMsg(getString(R.string.member_login_input_correvt_format_msg));

        if (true == editable.toString().matches("[a-zA-Z0-9\\-_.]+@[a-zA-Z0-9\\-_.]+")) {
            setIsFormatCorrect(true);
        } else {
            if (0 < editable.length()) {
                setIsFormatCorrect(false);
            } else {
                setIsFormatCorrect(true);
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 2016-11-14 Modifly by Ryan , 取消Email 輸出轉大寫的邏輯
        //m_editText.setTransformationMethod(new AllCapTransformationMethod());
        setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }

// 2016-11-14 Modifly by Ryan , 取消Email 輸出轉大寫的邏輯
//    @Override
//    public String getText() {
//        //帳號欄位輸出的英文字母必需全部為大寫
//        return m_editText.getText().toString().trim().toUpperCase();
//    }
}
