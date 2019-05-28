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
import ci.ui.TextField.Span.SCustomTypefaceSpan;
import ci.ui.TextField.TransformationMethod.AllCapTransformationMethod;

import static ci.ui.TextField.Base.CIRegex.REGEX_ENGLISH_NUMBER_EMIAL;


/**
 * Created by kevincheng on 2016/2/5.
 */
public class CIAccountTextFieldFragment extends CITextFieldFragment {

    private SpannableString        m_spannableString        = null;
    private SpannableStringBuilder m_spannableStringBuilder = null;
    private final String           COLOR_WRITE              = "#ffffff";
    private Type                   m_type                   = Type.NONE;
    public enum Type{
        /**NONE = 0,MEMBERSHIP_NO = 1,EMAIL = 2,MOBILE_NO =3*/
        NONE,MEMBERSHIP_NO,EMAIL,MOBILE_NO
    }

    public static CIAccountTextFieldFragment newInstance(Context context){
        Bundle bundle = new Bundle();
        String hint = context.getString(R.string.member_login_input_box_account_hint);
        bundle.putString(TEXT_HINT, hint);
        /**限制輸入為字母及數字及電子郵件用符號*/
        bundle.putString(FILTER_MODE, EFilterMode.REGEX.name());
        bundle.putString(REGEX, REGEX_ENGLISH_NUMBER_EMIAL);
        CIAccountTextFieldFragment fragment = new CIAccountTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        super.afterTextChanged(editable);

        if(null == m_spannableString){
            m_spannableString = new SpannableString(getString(R.string.member_login_input_box_account_hint));
            m_spannableStringBuilder = new SpannableStringBuilder(m_spannableString);
        }
        m_spannableStringBuilder.clearSpans();
        int length = m_spannableString.length();
//        setCustomTypefaceSpan(m_spannableStringBuilder, m_tfFont_Helvetica_light, 0, length);
        setErrorMsg(getString(R.string.member_login_input_correvt_format_msg));
        setIsFormatCorrect(true);
        int firstIndex = m_spannableStringBuilder.toString().indexOf("/",0);
        int secondIndex = m_spannableStringBuilder.toString().indexOf("/",firstIndex + 1);

        if (true == editable.toString().matches("[a-zA-Z0-9\\-_.]+@[a-zA-Z0-9\\-_.]+")) {
            setFormatAndHintForCorrect(firstIndex + 1, secondIndex);
            setAccountType(Type.EMAIL);
        } else if (true == editable.toString().matches("[a-zA-Z]{2}[0-9]{7}")) {
            setFormatAndHintForCorrect(0, firstIndex);
            setAccountType(Type.MEMBERSHIP_NO);
        } else if (true == editable.toString().matches("[(\\d)]+")) {
            setFormatAndHintForCorrect(secondIndex + 1, length);
            setAccountType(Type.MOBILE_NO);
        } else {
            setHint(getString(R.string.member_login_input_box_account_hint));
            if (0 < editable.length()) {
                setIsFormatCorrect(false);
            }
            setAccountType(Type.NONE);
        }
    }

    private void setFormatAndHintForCorrect(int start, int end){
        setTypefaceBoldSpan(m_spannableStringBuilder, start, end);
        setColorSpan(m_spannableStringBuilder, COLOR_WRITE, start, end);
        changeHint(m_spannableStringBuilder);
    }

    private void setAccountType(Type type){
        m_type = type;
    }

    private void setCustomTypefaceSpan(SpannableStringBuilder spanString,
                                       Typeface typeface,
                                       int start,
                                       int end){
        spanString.setSpan(new SCustomTypefaceSpan("", typeface),
                           start,
                           end,
                           Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
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
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
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
