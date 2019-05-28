package ci.ui.TextField;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.View;

import com.chinaairlines.mobile30.R;

import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.TransformationMethod.AllCapTransformationMethod;

import static ci.ui.TextField.Base.CIRegex.REGEX_ENGLISH_NUMBER;


/**
 * 會員卡號
 * Created by Ling - 2016.4.28
 */
public class CIMemberNoTextFieldFragment extends CITextFieldFragment {

    public static CIMemberNoTextFieldFragment newInstance(Context context){
        Bundle bundle = new Bundle();
        String hint = context.getString(R.string.member_no);
        bundle.putString(TEXT_HINT, hint);
        /**限制輸入為字母及數字 字數限制 9*/
        setFilterData(bundle);
        CIMemberNoTextFieldFragment fragment = new CIMemberNoTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static CIMemberNoTextFieldFragment newInstance(String hint){
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, hint);
        /**限制輸入為字母及數字 字數限制 9*/
        setFilterData(bundle);
        CIMemberNoTextFieldFragment fragment = new CIMemberNoTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private static void setFilterData(Bundle bundle){
        bundle.putString(FILTER_MODE, EFilterMode.REGEX.name());
        bundle.putString(REGEX, REGEX_ENGLISH_NUMBER);
        bundle.putInt(TEXT_MAX_LENGHT, 9);
    }

    @Override
    public void afterTextChanged(Editable editable) {
        super.afterTextChanged(editable);

        /**長度如果等於零就不再判斷格式正確性*/
        if(0 == editable.length()){
            setIsFormatCorrect(true);
            return;
        }

        setErrorMsg(getString(R.string.member_login_input_correvt_format_msg));

        if (true == editable.toString().matches("[a-zA-Z]{2}[0-9]{7}")) {
            setIsFormatCorrect(true);
        } else {
            setIsFormatCorrect(false);
        }
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
