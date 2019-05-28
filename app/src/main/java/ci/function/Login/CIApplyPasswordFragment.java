package ci.function.Login;

import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.CIDateOfBirthdayTextFieldFragment;
import ci.ui.TextField.CIMemberNoTextFieldFragment;
import ci.ui.TextField.CIOnlyEnglishTextFieldFragment;
import ci.ui.define.ViewScaleDef;

/**密碼申請
 * Created by jlchen on 2016/2/19.
 */
public class CIApplyPasswordFragment extends BaseFragment
        implements View.OnClickListener {

    public interface onApplyPasswordListener{
        //按下送出按鈕
        void onSendClick(String strCardNo, String strFirstName, String strLastName, String strBirthDay);
    }

    View.OnTouchListener m_onBackgroundTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            HidekeyBoard();
            return false;
        }
    };

    private onApplyPasswordListener m_Listner   = null;

    private RelativeLayout  m_rlBg              = null;

    private LinearLayout    m_llTextMsg         = null;
    private TextView        m_tvMsg             = null;

    private LinearLayout    m_llTextFeild       = null;
    private FrameLayout     m_flAccount         = null;
    private FrameLayout     m_flFirstName       = null;
    private FrameLayout     m_flLastName        = null;
    private FrameLayout     m_flDate            = null;

    private RelativeLayout  m_rlSend            = null;
    private Button          m_btnSend           = null;

    private CITextFieldFragment m_fragmentAcc   = null;
    private CITextFieldFragment m_fragmentFirstN= null;
    private CITextFieldFragment m_fragmentLastN = null;
    private CITextFieldFragment m_fragmentDate  = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_forget_card_number;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_rlBg          = (RelativeLayout) view.findViewById(R.id.rl_bg);
        m_rlBg.setOnTouchListener(m_onBackgroundTouchListener);

        m_llTextMsg     = (LinearLayout) view.findViewById(R.id.ll_textview);
        m_tvMsg         = (TextView) view.findViewById(R.id.tv_prompt_msg);
        m_tvMsg.setText(getString(R.string.apply_password_success_note));

        m_llTextFeild   = (LinearLayout) view.findViewById(R.id.ll_textfeild);
        m_flAccount     = (FrameLayout) view.findViewById(R.id.fl_account);
        m_flAccount.setVisibility(View.VISIBLE);
        m_flFirstName   = (FrameLayout) view.findViewById(R.id.fl_first_name);
        m_flLastName    = (FrameLayout) view.findViewById(R.id.fl_last_name);
        m_flDate        = (FrameLayout) view.findViewById(R.id.fl_date);

        m_rlSend        = (RelativeLayout) view.findViewById(R.id.rl_send);
        m_btnSend       = (Button) view.findViewById(R.id.btn_send);
        m_btnSend.setOnClickListener(this);

        setupViewComponents(view);
    }

    private void setupViewComponents(View view) {

        FragmentManager manager = getChildFragmentManager();
        m_fragmentAcc = CIMemberNoTextFieldFragment.newInstance(getActivity());
        m_fragmentFirstN = CIOnlyEnglishTextFieldFragment.newInstance(
                getString(R.string.inquiry_input_box_first_name_hint));
        m_fragmentLastN = CIOnlyEnglishTextFieldFragment.newInstance(
                getString(R.string.inquiry_input_box_last_name_hint));
        m_fragmentDate = CIDateOfBirthdayTextFieldFragment.newInstance(getString(R.string.inquiry_input_box_date_of_birth_hint));
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(m_flAccount.getId(), m_fragmentAcc)
                .replace(m_flFirstName.getId(), m_fragmentFirstN)
                .replace(m_flLastName.getId(), m_fragmentLastN)
                .replace(m_flDate.getId(), m_fragmentDate)
                .commit();
        view.post(new Runnable() {
            @Override
            public void run() {
                //2016-11-14 Modifly By Ryan, 因應xml調整名字與姓氏的順序
                m_fragmentFirstN.setImeOptions(EditorInfo.IME_ACTION_DONE);
                //m_fragmentLastN.setImeOptions(EditorInfo.IME_ACTION_DONE);
            }
        });
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

        //說明文字欄 上間隔20, 左右間隔30
        RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) m_llTextMsg.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(20);
        rParams.leftMargin = vScaleDef.getLayoutWidth(30);
        rParams.rightMargin = vScaleDef.getLayoutWidth(30);

        //說明文字大小16
        vScaleDef.setTextSize( vScaleDef.DEF_TEXT_SIZE_16, m_tvMsg);

//        //輸入欄 置底 下間隔262 (扣掉格式錯誤欄16) 246
//        rParams = (RelativeLayout.LayoutParams) m_llTextFeild.getLayoutParams();
//        rParams.bottomMargin = vScaleDef.getLayoutHeight(246);

        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) m_flAccount.getLayoutParams();
        lParams.topMargin = vScaleDef.getLayoutHeight(20);

        //距離前一個輸入框 間隔6
        lParams = (LinearLayout.LayoutParams) m_flFirstName.getLayoutParams();
        lParams.topMargin = vScaleDef.getLayoutHeight(6);

        lParams = (LinearLayout.LayoutParams) m_flLastName.getLayoutParams();
        lParams.topMargin = vScaleDef.getLayoutHeight(6);

        lParams = (LinearLayout.LayoutParams) m_flDate.getLayoutParams();
        lParams.topMargin = vScaleDef.getLayoutHeight(6);

        //按鈕置底 下間隔30
        rParams = (RelativeLayout.LayoutParams) m_rlSend.getLayoutParams();
        rParams.bottomMargin = vScaleDef.getLayoutHeight(30);

        //按鈕大小 寬320, 高40
        //按鈕文字 大小16
        rParams = (RelativeLayout.LayoutParams) m_btnSend.getLayoutParams();
        rParams.width = vScaleDef.getLayoutWidth(320);
        rParams.height = vScaleDef.getLayoutHeight(40);
        vScaleDef.setTextSize( vScaleDef.DEF_TEXT_SIZE_16, m_btnSend);
    }

    @Override
    protected void setOnParameterAndListener(View view) {}

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {}

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                //尚未輸入卡號
                String strAcct = m_fragmentAcc.getText().toString();
                if ( 0 >= strAcct.length() ){

                    showDialog(getString(R.string.warning),
                            String.format(getString(R.string.please_input_field), m_fragmentAcc.getHint()),
                            getString(R.string.confirm));
                    return;
                }

                //尚未輸入護照英文名
                String strFirstN  = m_fragmentFirstN.getText().toString();
                if ( 0 >= strFirstN.length() ){
                    showDialog(getString(R.string.warning),
                            String.format(getString(R.string.please_input_field), m_fragmentFirstN.getHint()),
                            getString(R.string.confirm));
                    return;
                }

                //尚未輸入護照英文姓
                String strLastN = m_fragmentLastN.getText().toString();
                if ( 0 >= strLastN.length() ){
                    showDialog(getString(R.string.warning),
                            String.format(getString(R.string.please_input_field), m_fragmentLastN.getHint()),
                            getString(R.string.confirm));
                    return;
                }

                //尚未選擇生日
                String strDate = m_fragmentDate.getText().toString();
                if ( 0 >= strDate.length() ){
                    showDialog(getString(R.string.warning),
                            String.format(getString(R.string.please_input_field), m_fragmentDate.getHint()),
                            getString(R.string.confirm));
                    return;
                }

                if ( null != m_Listner ){
                    m_Listner.onSendClick(
                            strAcct,
                            strFirstN,
                            strLastN,
                            strDate);
                }
                break;
        }
    }

    public void uiSetParameterListener(onApplyPasswordListener onListenr) {
        m_Listner = onListenr;
    }

}
