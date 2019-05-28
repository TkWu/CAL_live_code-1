package ci.function.Login;

import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.CIIdentityTextFieldFragment;
import ci.ui.define.ViewScaleDef;

/**
 * Created by jlchen on 2016/4/8.
 */
public class CIInputDetailIdentityFragment extends BaseFragment implements View.OnClickListener{

    public interface OnInputDetailIdentityFragmentListener{
        void OnSendClick(String strIdentity);
    }

    private OnInputDetailIdentityFragmentListener m_Listener = null;

    View.OnTouchListener m_onBackgroundTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            HidekeyBoard();
            return false;
        }
    };

    private RelativeLayout      m_rlBg              = null;
    private TextView            m_tvMsg             = null;
    private Button              m_btnSend           = null;

    private CITextFieldFragment m_fragmentIdentity  = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_input_detail_identity;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_rlBg      = (RelativeLayout) view.findViewById(R.id.rl_bg);
        m_rlBg.setOnTouchListener(m_onBackgroundTouchListener);
        m_tvMsg     = (TextView) view.findViewById(R.id.tv_msg);
        m_btnSend   = (Button) view.findViewById(R.id.btn_send);
        m_btnSend.setOnClickListener(this);
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(m_rlBg);
    }

    @Override
    protected void setOnParameterAndListener(View view) {}

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

        m_fragmentIdentity = CIIdentityTextFieldFragment.newInstance(getActivity());

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fl_identity, m_fragmentIdentity).commit();
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
                String strIdentity  = m_fragmentIdentity.getText().toString();
                if ( 0 >= strIdentity.length() ){
                    showDialog(getString(R.string.warning),
                            String.format(getString(R.string.please_input_field), m_fragmentIdentity.getHint()),
                            getString(R.string.confirm));
                    return;
                }

                if ( null != m_Listener ){
                    m_Listener.OnSendClick(strIdentity);
                }
                break;
        }
    }

    public void uiSetParameterListener(OnInputDetailIdentityFragmentListener onListener) {
        m_Listener = onListener;
    }
}
