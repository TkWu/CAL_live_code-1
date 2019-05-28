package ci.ui.TextField;

import android.os.Bundle;
import android.view.View;

import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.TransformationMethod.AllCapTransformationMethod;

import static ci.ui.TextField.Base.CIRegex.REGEX_ENGLISH_NUMBER;


/**
 * Created by kevincheng on 2017/11/16.
 */
public class CIOnlyEnglishAndNumberTextFieldFragment extends CITextFieldFragment {

    public static CIOnlyEnglishAndNumberTextFieldFragment newInstance(String hint){
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, hint);
        /**限制輸入為字母及數字  */
        bundle.putString(FILTER_MODE, EFilterMode.REGEX.name());
        bundle.putString(REGEX, REGEX_ENGLISH_NUMBER);
        CIOnlyEnglishAndNumberTextFieldFragment fragment = new CIOnlyEnglishAndNumberTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        m_editText.setTransformationMethod(new AllCapTransformationMethod());
    }

    @Override
    public String getText() {
        //帳號欄位輸出的英文字母必需全部為大寫
        return m_editText.getText().toString().trim().toUpperCase();
    }
}
