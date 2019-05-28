package ci.ui.TextField;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.View;

import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.TransformationMethod.AllCapTransformationMethod;

import static ci.ui.TextField.Base.CIRegex.REGEX_ENGLISH_SPACE;

/**
 * Created by kevin on 2016/2/20.
 */
public class CIOnlyEnglishTextFieldFragment extends CITextFieldFragment {
    public static CIOnlyEnglishTextFieldFragment newInstance(String hint){
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, hint);
        /**限制輸入為字母及空格*/
        bundle.putString(FILTER_MODE, EFilterMode.REGEX.name());
        bundle.putString(REGEX, REGEX_ENGLISH_SPACE);
        CIOnlyEnglishTextFieldFragment fragment = new CIOnlyEnglishTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public String getText() {
        //帳號欄位輸出的英文字母必需全部為大寫
        return m_editText.getText().toString().trim().toUpperCase();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        m_editText.setTransformationMethod(new AllCapTransformationMethod());
    }

}
