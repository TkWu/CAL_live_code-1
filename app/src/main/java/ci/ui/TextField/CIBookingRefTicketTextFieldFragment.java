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

import static ci.ui.TextField.Base.CIRegex.REGEX_ENGLISH_NUMBER;


/**
 * Created by kevincheng on 2016/2/5.
 */
public class CIBookingRefTicketTextFieldFragment extends CITextFieldFragment {

    private SpannableString        m_spannableString        = null;
    private SpannableStringBuilder m_spannableStringBuilder = null;
    private final String           COLOR_WRITE              = "#ffffff";
    private Type                   m_type                   = Type.NONE;

    public enum Type{
        /**NONE = 0,MEMBERSHIP_NO = 1,EMAIL = 2,MOBILE_NO =3*/
        NONE, BOOKING_REF, TICKET
    }

    public static CIBookingRefTicketTextFieldFragment newInstance(Context context){
        Bundle bundle = new Bundle();
        String hint = context.getString(R.string.member_login_booking_ref_ticket_hint);
        bundle.putString(TEXT_HINT, hint);
        /**限制輸入為字母及數字*/
        bundle.putString(FILTER_MODE, EFilterMode.REGEX.name());
        bundle.putString(REGEX, REGEX_ENGLISH_NUMBER);
        CIBookingRefTicketTextFieldFragment fragment = new CIBookingRefTicketTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        super.afterTextChanged(editable);

        /**長度如果等於零就不再判斷格式正確性*/
        if(0 == editable.length()){
            setIsFormatCorrect(true);
            setTextFeildType(Type.NONE);
            setHint(getString(R.string.member_login_booking_ref_ticket_hint));
            return;
        }

        if(null == m_spannableString){
            m_spannableString = new SpannableString(getString(R.string.member_login_booking_ref_ticket_hint));
            m_spannableStringBuilder = new SpannableStringBuilder(m_spannableString);
        }
        m_spannableStringBuilder.clearSpans();
//        setCustomTypefaceSpan(m_spannableStringBuilder, m_tfFont_Helvetica_light, 0, length);
        setErrorMsg(getString(R.string.member_login_input_correvt_format_msg));
        setIsFormatCorrect(true);

        int length = m_spannableString.length();
        int firstIndex = m_spannableStringBuilder.toString().indexOf("/",0);

        //BOOKING_REF: 6碼英數字
        //if (false == editable.toString().matches("[0-9]+|[a-zA-Z]+")
        //2016-05-25 modifly by ryan for 調整為 6碼英文or數字
        if (false == editable.toString().matches("[0-9][a-zA-Z]")
                && 6 == editable.length() ) {
            setFormatAndHintForCorrect(0, firstIndex);
            setTextFeildType(Type.BOOKING_REF);

        //TICKET: 13碼數字
        } else if (true == editable.toString().matches("[0-9]{13}")) {
            setFormatAndHintForCorrect(firstIndex + 1, length);
            setTextFeildType(Type.TICKET);

        } else {
            setHint(getString(R.string.member_login_booking_ref_ticket_hint));
            if (0 < editable.length()) {
                setIsFormatCorrect(false);
            }
            setTextFeildType(Type.NONE);
        }
    }

    private void setFormatAndHintForCorrect(int start, int end){
        setTypefaceBoldSpan(m_spannableStringBuilder, start, end);
        setColorSpan(m_spannableStringBuilder, COLOR_WRITE, start, end);
        changeHint(m_spannableStringBuilder);
    }

    private void setTextFeildType(Type type){
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
        //顯示所有字為大寫
        m_editText.setTransformationMethod(new AllCapTransformationMethod());
    }

    public Type getTextFeildType(){
        return m_type;
    }

    @Override
    public String getText() {
        //帳號欄位輸出的英文字母必需全部為大寫
        return m_editText.getText().toString().trim().toUpperCase();
    }

}
