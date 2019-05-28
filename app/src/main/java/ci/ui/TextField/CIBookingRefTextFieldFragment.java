package ci.ui.TextField;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.View;

import com.chinaairlines.mobile30.R;

import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.TransformationMethod.AllCapTransformationMethod;

import static ci.ui.TextField.Base.CIRegex.REGEX_ENGLISH_NUMBER;


/**
 * Created by kevincheng on 2016/2/5.
 */
public class CIBookingRefTextFieldFragment extends CITextFieldFragment {

    private SpannableString        m_spannableString        = null;
    private SpannableStringBuilder m_spannableStringBuilder = null;
    private final String           COLOR_WRITE              = "#ffffff";
    private Type                   m_type                   = Type.NONE;

    public enum Type{
        /**NONE = 0,MEMBERSHIP_NO = 1,EMAIL = 2,MOBILE_NO =3*/
        NONE, BOOKING_REF, TICKET
    }

    public static CIBookingRefTextFieldFragment newInstance(Context context){
        Bundle bundle = new Bundle();
        String hint = context.getString(R.string.baggage_booking_ref_hint);
        bundle.putString(TEXT_HINT, hint);
        /**限制輸入為字母及數字*/
        bundle.putString(FILTER_MODE, EFilterMode.REGEX.name());
        bundle.putString(REGEX, REGEX_ENGLISH_NUMBER);
        CIBookingRefTextFieldFragment fragment = new CIBookingRefTextFieldFragment();
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
            return;
        }

        setErrorMsg(getString(R.string.member_login_input_correvt_format_msg));
        setIsFormatCorrect(true);

        if ( false == editable.toString().matches("[0-9][a-zA-Z]") && 6 == editable.length() ) {
            setTextFeildType(Type.BOOKING_REF);
        } else {
            if (0 < editable.length()) {
                setIsFormatCorrect(false);
            }
            setTextFeildType(Type.NONE);
        }
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

    private void setTextFeildType( Type type){
        m_type = type;
    }

    @Override
    public String getText() {
        //帳號欄位輸出的英文字母必需全部為大寫
        return m_editText.getText().toString().trim().toUpperCase();
    }

}
