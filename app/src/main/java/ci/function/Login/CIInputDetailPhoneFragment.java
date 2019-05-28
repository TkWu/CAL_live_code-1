package ci.function.Login;

import android.graphics.Typeface;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.CIMobilePhoneCountryCodeTextFieldFragment;
import ci.ui.TextField.CIPhoneNumberFieldTextFragment;
import ci.ui.define.ViewScaleDef;

/**
 * Created by jlchen on 2016/4/8.
 */
public class CIInputDetailPhoneFragment extends BaseFragment implements View.OnClickListener{

    public interface OnInputDetailPhoneFragmentListener{
        void OnSendClick(String strCountry, String strPhone);
    }

    public interface OnInputDetailPhoneFragmentParameter{
        String getUserName();
        String getUserData();
    }

    private OnInputDetailPhoneFragmentListener   m_Listener  = null;
    private OnInputDetailPhoneFragmentParameter  m_Parameter = null;

    View.OnTouchListener m_onBackgroundTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            HidekeyBoard();
            return false;
        }
    };

    private RelativeLayout      m_rlBg              = null;
    private TextView            m_tvUserName        = null;
    private TextView            m_tvPhoneMsg        = null;
    private Button              m_btnSend           = null;

    private CITextFieldFragment m_fragmentPhoneC    = null;
    private CITextFieldFragment m_fragmentPhone     = null;
    private String              m_strMsg            = "";

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_input_detail_phone;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_rlBg      = (RelativeLayout) view.findViewById(R.id.rl_bg);
        m_rlBg.setOnTouchListener(m_onBackgroundTouchListener);
        m_tvUserName= (TextView) view.findViewById(R.id.tv_username);
        m_tvPhoneMsg= (TextView) view.findViewById(R.id.tv_update_phone);
        m_btnSend   = (Button) view.findViewById(R.id.btn_send);
        m_btnSend.setOnClickListener(this);

        if ( null != m_Parameter ){

            m_tvUserName.setText( m_Parameter.getUserName() );

            m_strMsg = m_Parameter.getUserData();
            m_tvPhoneMsg.setText( String.format( getString(R.string.input_detail_update_phone_msg) , m_strMsg ) );
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(m_rlBg);

        SpannableString spannableString = new SpannableString(m_tvPhoneMsg.getText().toString());
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spannableString);

        int index = spannableStringBuilder.toString().indexOf(m_strMsg, 0);

        spannableStringBuilder.setSpan(
                new AbsoluteSizeSpan(vScaleDef.getTextSize(18)),
                index,
                index + m_strMsg.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableStringBuilder.setSpan(
                new StyleSpan(Typeface.BOLD),
                index,
                index + m_strMsg.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        m_tvPhoneMsg.setText(spannableStringBuilder);
    }

    @Override
    protected void setOnParameterAndListener(View view) {}

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
        m_fragmentPhoneC    = CIMobilePhoneCountryCodeTextFieldFragment.newInstance(
                getString(R.string.sign_up_mobile_num_country_code));
        m_fragmentPhone = CIPhoneNumberFieldTextFragment.newInstance(
                getString(R.string.input_detail_phone_hint));

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fl_new_phone_country, m_fragmentPhoneC)
                .replace(R.id.fl_new_phone, m_fragmentPhone)
                .commit();
    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

    @Override
    public void onClick(View v) {
        switch ( v.getId() ){
            case R.id.btn_send:
                //尚未選擇國碼
                String strPhoneC = ((CIMobilePhoneCountryCodeTextFieldFragment)m_fragmentPhoneC).getPhoneCd();
                if (true == TextUtils.isEmpty(strPhoneC)){
                    showDialog(getString(R.string.warning),
                            String.format(getString(R.string.please_input_field), m_fragmentPhoneC.getHint()),
                            getString(R.string.confirm));
                    return;
                }

                //尚未輸入行動電話
                String strPhone  = m_fragmentPhone.getText().toString();
                if ( true == TextUtils.isEmpty(strPhone)){
                    showDialog(getString(R.string.warning),
                            String.format(getString(R.string.please_input_field), m_fragmentPhone.getHint()),
                            getString(R.string.confirm));
                    return;
                }

                if ( null != m_Listener ){
                    m_Listener.OnSendClick(strPhoneC, strPhone);
                }
                break;
        }
    }

    public void uiSetParameterListener(OnInputDetailPhoneFragmentListener onListener,
                                       OnInputDetailPhoneFragmentParameter onParameter) {
        m_Listener  = onListener;
        m_Parameter = onParameter;
    }
}
