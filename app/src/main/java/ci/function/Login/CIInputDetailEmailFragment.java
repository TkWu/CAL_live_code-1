package ci.function.Login;

import android.graphics.Typeface;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
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
import ci.ui.TextField.CIEmailTextFieldFragment;
import ci.ui.define.ViewScaleDef;

/**
 * Created by jlchen on 2016/4/8.
 */
public class CIInputDetailEmailFragment extends BaseFragment implements View.OnClickListener{

    public interface OnInputDetailEmailFragmentListener{
        void OnSendClick(String strEmail);
    }

    public interface OnInputDetailEmailFragmentParameter{
        String getUserName();
        String getUserData();
    }

    private OnInputDetailEmailFragmentListener   m_Listener  = null;
    private OnInputDetailEmailFragmentParameter  m_Parameter = null;

    View.OnTouchListener m_onBackgroundTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            HidekeyBoard();
            return false;
        }
    };

    private RelativeLayout      m_rlBg              = null;
    private TextView            m_tvUserName        = null;
    private TextView            m_tvEmailMsg        = null;
    private Button              m_btnSend           = null;

    private CITextFieldFragment m_fragmentEmail     = null;
    private String              m_strMsg            = "";

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_input_detail_email;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_rlBg      = (RelativeLayout) view.findViewById(R.id.rl_bg);
        m_rlBg.setOnTouchListener(m_onBackgroundTouchListener);
        m_tvUserName= (TextView) view.findViewById(R.id.tv_username);
        m_tvEmailMsg= (TextView) view.findViewById(R.id.tv_update_email);
        m_btnSend   = (Button) view.findViewById(R.id.btn_send);
        m_btnSend.setOnClickListener(this);

        if ( null != m_Parameter ){

            m_tvUserName.setText( m_Parameter.getUserName() );

            m_strMsg = m_Parameter.getUserData();
            m_tvEmailMsg.setText( String.format( getString(R.string.input_detail_update_email_msg) , m_strMsg ) );
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(m_rlBg);

        SpannableString spannableString = new SpannableString(m_tvEmailMsg.getText().toString());
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

        m_tvEmailMsg.setText(spannableStringBuilder);
    }

    @Override
    protected void setOnParameterAndListener(View view) {}

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

        m_fragmentEmail = CIEmailTextFieldFragment.newInstance(
                getString(R.string.input_detail_email_hint));

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fl_new_email, m_fragmentEmail).commit();
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
                //尚未輸入資料
                String strEmail  = m_fragmentEmail.getText().toString();
                if ( 0 >= strEmail.length() ){
                    showDialog(getString(R.string.warning),
                            String.format(getString(R.string.please_input_field), m_fragmentEmail.getHint()),
                            getString(R.string.confirm));
                    return;
                }

                if ( null != m_Listener ){
                    m_Listener.OnSendClick(strEmail);
                }
                break;
        }
    }

    public void uiSetParameterListener(OnInputDetailEmailFragmentListener onListener,
                                       OnInputDetailEmailFragmentParameter onParameter) {
        m_Listener  = onListener;
        m_Parameter = onParameter;
    }
}
