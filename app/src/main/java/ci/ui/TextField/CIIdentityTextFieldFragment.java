package ci.ui.TextField;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;

import com.chinaairlines.mobile30.R;

import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.TransformationMethod.AllCapTransformationMethod;

import static ci.ui.TextField.Base.CIRegex.REGEX_ENGLISH_NUMBER;


/**
 * 身份證字號/護照號碼
 * Created by Ling - 2016.4.8
 */
public class CIIdentityTextFieldFragment extends CITextFieldFragment {

    private SpannableString        m_spannableString        = null;
    private SpannableStringBuilder m_spannableStringBuilder = null;
    private final String           COLOR_WRITE              = "#ffffff";
    private Type                   m_type                   = Type.NONE;
    public enum Type{
        NONE, IDENTITY, PASSPORT
    }

    public static CIIdentityTextFieldFragment newInstance(Context context){
        Bundle bundle = new Bundle();
        String hint = context.getString(R.string.input_detail_identity_hint);
        bundle.putString(TEXT_HINT, hint);
        /**限制輸入為字母及數字*/
        bundle.putString(FILTER_MODE, EFilterMode.REGEX.name());
        bundle.putString(REGEX, REGEX_ENGLISH_NUMBER);
        CIIdentityTextFieldFragment fragment = new CIIdentityTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        super.afterTextChanged(editable);

        /**長度如果等於零就不再判斷格式正確性*/
        if(0 == editable.length()){
            setIsFormatCorrect(true);
            return;
        }

        if(null == m_spannableString){
            m_spannableString = new SpannableString(getString(R.string.input_detail_identity_hint));
            m_spannableStringBuilder = new SpannableStringBuilder(m_spannableString);
        }
        m_spannableStringBuilder.clearSpans();
        int length = m_spannableString.length();
//        setCustomTypefaceSpan(m_spannableStringBuilder, m_tfFont_Helvetica_light, 0, length);
        setErrorMsg(getString(R.string.member_login_input_correvt_format_msg));
        setIsFormatCorrect(true);
        int iIndex = m_spannableStringBuilder.toString().indexOf("/",0);

        if (true == editable.toString().matches("[a-zA-Z]{1}[1-2]{1}[0-9]{8}")) {
            setFormatAndHintForCorrect(0, iIndex);
            setIdentityType(Type.IDENTITY);
        } else if (true == editable.toString().matches("[(\\d)]+")) {
            setFormatAndHintForCorrect(iIndex + 1, length);
            setIdentityType(Type.PASSPORT);
        } else {
            setHint(getString(R.string.input_detail_identity_hint));
            if (0 < editable.length()) {
                setIsFormatCorrect(false);
            }
            setIdentityType(Type.NONE);
        }
    }

    private void setFormatAndHintForCorrect(int start, int end){
        setTypefaceBoldSpan(m_spannableStringBuilder, start, end);
        setColorSpan(m_spannableStringBuilder, COLOR_WRITE, start, end);
        changeHint(m_spannableStringBuilder);
    }

    private void setIdentityType(Type type){
        m_type = type;
    }

    private void setTypefaceBoldSpan(SpannableStringBuilder spanString,int start,int end){
        spanString.setSpan(new StyleSpan(Typeface.BOLD),
                           start,
                           end,
                           Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    }

    private void setColorSpan(SpannableStringBuilder spanString,String color,int start,int end){
        spanString.setSpan(new ForegroundColorSpan(Color.parseColor(color)),
                           start,
                           end,
                           Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
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

    public Type getAccountType(){
        return m_type;
    }


}
