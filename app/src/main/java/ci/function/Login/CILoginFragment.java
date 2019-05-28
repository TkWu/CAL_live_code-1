package ci.function.Login;

import android.content.Intent;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.Signup.CISignUpActivity;
import ci.ui.TextField.Base.CIBaseTextFieldFragment;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.CIAccountTextFieldFragment;
import ci.ui.TextField.CIPasswordTextFieldFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;

public class CILoginFragment extends BaseFragment
        implements View.OnClickListener {

    public interface onLoginListener{
        //華夏會員登入 for 串接ws
        void onMemberLoginClick(String strAccout, String strPassword, boolean bKeep);
        //google登入
        void onGoogleLoginClick(boolean bKeep);
        //fb登入
        void onFacebookLoginClick(boolean bKeep);
    }

    View.OnTouchListener m_onBackgroundTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            HidekeyBoard();
            return false;
        }
    };

    private onLoginListener m_listener      = null;

    private RelativeLayout  m_rlBg          = null;

    private LinearLayout    m_llTextfeild   = null;
    private FrameLayout     m_flAccount     = null;
    private FrameLayout     m_flPassword    = null;

    private RelativeLayout  m_rlayout       = null;
    private ImageButton     m_ibtnKeep      = null;
    private Button          m_btnKeep       = null;
    private Button          m_btnForget     = null;
    private ImageButton     m_ibtnForget    = null;

    private Button          m_btnLogin      = null;

    private TextView        m_tvOr          = null;

    private LinearLayout    m_llOpenId      = null;
    private RelativeLayout  m_rlFacebook    = null;
    private ImageButton     m_ibtnFacebook  = null;
    private TextView        m_tvFacebook    = null;
    private RelativeLayout  m_rlGoogle      = null;
    private ImageButton     m_ibtnGoogle    = null;
    private TextView        m_tvGoogle      = null;

    private LinearLayout    m_llGoSignUp    = null;
    private TextView        m_tvSignUp      = null;
    private Button          m_btnSignUp     = null;
    private ImageButton     m_ibtnSignUp    = null;

    private CITextFieldFragment m_fragmentAcc;
    private CITextFieldFragment m_fragmentPw;

    private boolean         m_bKeep         = false;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_login;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View ViewContent) {

        setupViewComponents();

        m_rlBg          = (RelativeLayout)ViewContent.findViewById(R.id.rl_bg);
        m_rlBg.setOnTouchListener(m_onBackgroundTouchListener);

        m_llTextfeild   = (LinearLayout)ViewContent.findViewById(R.id.ll_textfeild);
        m_flAccount     = (FrameLayout)ViewContent.findViewById(R.id.fl_account);
        m_flPassword    = (FrameLayout)ViewContent.findViewById(R.id.fl_password);

        m_rlayout       = (RelativeLayout)ViewContent.findViewById(R.id.rlayout);
        m_ibtnKeep      = (ImageButton)ViewContent.findViewById(R.id.ibtn_keep);
        m_ibtnKeep.setOnClickListener(this);
        m_bKeep =  CIApplication.getLoginInfo().GetKeepLogin();
        if ( true == m_bKeep ){
            m_ibtnKeep.setImageResource(R.drawable.btn_checkbox_on);
        }else {
            m_ibtnKeep.setImageResource(R.drawable.btn_checkbox_off);
        }

        m_btnKeep       = (Button)ViewContent.findViewById(R.id.btn_keep);
        m_btnKeep.setOnClickListener(this);
        m_btnForget     = (Button)ViewContent.findViewById(R.id.btn_forget);
        m_btnForget.setOnClickListener(this);
        m_ibtnForget    = (ImageButton)ViewContent.findViewById(R.id.ibtn_forget);
        m_ibtnForget.setOnClickListener(this);

        m_btnLogin      = (Button)ViewContent.findViewById(R.id.btn_login);
        m_btnLogin.setOnClickListener(this);

        m_tvOr          = (TextView)ViewContent.findViewById(R.id.tv_or);

        m_llOpenId      = (LinearLayout)ViewContent.findViewById(R.id.ll_other_login);
        m_rlFacebook    = (RelativeLayout)ViewContent.findViewById(R.id.rl_facebook);
        m_ibtnFacebook  = (ImageButton)ViewContent.findViewById(R.id.ibtn_f_login);
        m_ibtnFacebook.setOnClickListener(this);
        m_tvFacebook    = (TextView)ViewContent.findViewById(R.id.tv_facebook);
        m_rlGoogle      = (RelativeLayout)ViewContent.findViewById(R.id.rl_google);
        m_ibtnGoogle    = (ImageButton)ViewContent.findViewById(R.id.ibtn_g_login);
        m_ibtnGoogle.setOnClickListener(this);
        m_tvGoogle      = (TextView)ViewContent.findViewById(R.id.tv_google);

        m_llGoSignUp    = (LinearLayout)ViewContent.findViewById(R.id.ll_goto_sign_up);
        m_tvSignUp      = (TextView)ViewContent.findViewById(R.id.tv_new);
        m_btnSignUp     = (Button)ViewContent.findViewById(R.id.btn_signup);
        m_btnSignUp.setOnClickListener(this);
        m_ibtnSignUp    = (ImageButton)ViewContent.findViewById(R.id.ibtn_signup);
        m_ibtnSignUp.setOnClickListener(this);

        /**如果有登入會員的話，隱藏下方特定的view by kevin*/
        if(true == CIApplication.getLoginInfo().GetLoginStatus()){
            m_tvOr.setVisibility(View.INVISIBLE);
            m_llOpenId.setVisibility(View.INVISIBLE);
            m_llGoSignUp.setVisibility(View.INVISIBLE);
        }

        ViewContent.post(new Runnable() {
            @Override
            public void run() {
                m_fragmentPw.setImeOptions(EditorInfo.IME_ACTION_DONE);
            }
        });
    }

    private void setupViewComponents() {
        //輸入框
        FragmentManager manager = getChildFragmentManager();
        m_fragmentAcc = CIAccountTextFieldFragment.newInstance(getActivity());
        m_fragmentPw = CIPasswordTextFieldFragment.newInstance(getString(R.string.member_login_input_box_password_hint));
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fl_account, m_fragmentAcc)
                .replace(R.id.fl_password, m_fragmentPw)
                .commit();
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

        vScaleDef.setMargins(m_flPassword,0,6,0,0);

        //輸入欄 上間隔20
        RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) m_llTextfeild.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(20);

        //2017-01-19 Modify by Ryan for 華航決議移除此字串
//        //請輸入有效的...文字
//        TextView tvPlease = (TextView)view.findViewById(R.id.tv_please_enter);
//        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) tvPlease.getLayoutParams();
//        lParams.width = vScaleDef.getLayoutWidth(324); //左右間距約18
//        tvPlease.setMinHeight(vScaleDef.getLayoutHeight(33)); //大小固定兩行
//        vScaleDef.setTextSize(14, tvPlease);

        //保持登入狀態&忘記密碼欄 上間隔2.3, 左間隔30, 右間隔16
        rParams = (RelativeLayout.LayoutParams) m_rlayout.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(0);
        rParams.leftMargin = vScaleDef.getLayoutWidth(30);
        rParams.rightMargin = vScaleDef.getLayoutWidth(16);

        //保持登入checkbox 寬高24
        rParams = (RelativeLayout.LayoutParams) m_ibtnKeep.getLayoutParams();
        rParams.width = vScaleDef.getLayoutMinUnit(24);
        rParams.height = vScaleDef.getLayoutMinUnit(24);

        //保持登入文字 右間隔8, 字體大小16
        rParams = (RelativeLayout.LayoutParams) m_btnKeep.getLayoutParams();
        rParams.rightMargin = vScaleDef.getLayoutWidth(8);
        vScaleDef.setTextSize(vScaleDef.DEF_TEXT_SIZE_16, m_btnKeep);

        //忘記密碼 字體大小16
        vScaleDef.setTextSize(vScaleDef.DEF_TEXT_SIZE_16, m_btnForget);

        //忘記密碼箭頭圖 寬高16
        rParams = (RelativeLayout.LayoutParams) m_ibtnForget.getLayoutParams();
        rParams.width = vScaleDef.getLayoutMinUnit(16);
        rParams.height = vScaleDef.getLayoutMinUnit(16);

        //登入按鈕 上間隔40, 寬320, 高40
        rParams = (RelativeLayout.LayoutParams) m_btnLogin.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(40);
        rParams.width = vScaleDef.getLayoutWidth(320);
        rParams.height = vScaleDef.getLayoutHeight(40);

        //登入按鈕文字 字體大小16
        vScaleDef.setTextSize(vScaleDef.DEF_TEXT_SIZE_16, m_btnLogin);

        //或文字 字體大小16
        vScaleDef.setTextSize(vScaleDef.DEF_TEXT_SIZE_16, m_tvOr);

        //fb按鈕 寬155, 高40
        rParams = (RelativeLayout.LayoutParams) m_ibtnFacebook.getLayoutParams();
        //2016-11-15 Modify by ryan ,應該調整為同步延伸,圖檔改成使用.9
        rParams.width = vScaleDef.getLayoutWidth(155);
        rParams.height = vScaleDef.getLayoutHeight(40);
        //rParams.width = vScaleDef.getLayoutMinUnit(155);
        //rParams.height = vScaleDef.getLayoutMinUnit(40);

        //fb按鈕文字 左間隔61.7,字體大小16
        rParams = (RelativeLayout.LayoutParams) m_tvFacebook.getLayoutParams();
        rParams.leftMargin = vScaleDef.getLayoutWidth(61.7);
        vScaleDef.setTextSize(vScaleDef.DEF_TEXT_SIZE_16, m_tvFacebook);

        //g+按鈕 左間隔10, 寬155, 高40
        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) m_rlGoogle.getLayoutParams();
        lParams.leftMargin = vScaleDef.getLayoutWidth(10);
        rParams = (RelativeLayout.LayoutParams) m_ibtnGoogle.getLayoutParams();
        //2016-11-15 Modify by ryan ,應該調整為同步延伸,圖檔改成使用.9
        rParams.width = vScaleDef.getLayoutWidth(155);
        rParams.height = vScaleDef.getLayoutHeight(40);
        //rParams.width = vScaleDef.getLayoutMinUnit(155);
        //rParams.height = vScaleDef.getLayoutMinUnit(40);

        //g+按鈕文字 左間隔61.7,字體大小16
        rParams = (RelativeLayout.LayoutParams) m_tvGoogle.getLayoutParams();
        rParams.leftMargin = vScaleDef.getLayoutWidth(61.7);
        vScaleDef.setTextSize(vScaleDef.DEF_TEXT_SIZE_16, m_tvGoogle);

        //加入會員欄 置底 下間隔30
        rParams = (RelativeLayout.LayoutParams) m_llGoSignUp.getLayoutParams();
        rParams.bottomMargin = vScaleDef.getLayoutHeight(30);

        //加入會員文字 字體大小16
        lParams = (LinearLayout.LayoutParams) m_tvSignUp.getLayoutParams();
        lParams.rightMargin = vScaleDef.getLayoutHeight(3);
        vScaleDef.setTextSize(vScaleDef.DEF_TEXT_SIZE_16, m_tvSignUp);

        //加入會員按鈕 字體大小16
        vScaleDef.setTextSize(vScaleDef.DEF_TEXT_SIZE_16, m_btnSignUp);

        //加入會員箭頭圖 寬高16
        lParams = (LinearLayout.LayoutParams) m_ibtnSignUp.getLayoutParams();
        lParams.width = vScaleDef.getLayoutMinUnit(16);
        lParams.height = vScaleDef.getLayoutMinUnit(16);
    }

    @Override
    protected void setOnParameterAndListener(View view) {

        //2016-11-02 Ryan, 登入畫面的密碼輸入，不顯示格式錯誤等訊息
        m_fragmentPw.setAfterTextChangedListener(new CIBaseTextFieldFragment.afterTextChangedListener() {
            @Override
            public void afterTextChangedListener(Editable editable) {
                //2016-11-02 Ryan, 永遠為正確格式, 將格式正確與否交由Server判斷
                m_fragmentPw.setIsFormatCorrect(true);
            }
        });
    }

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
    public void onClick(View v) {
        switch ( v.getId() ){
            case R.id.ibtn_keep:
            case R.id.btn_keep:
                if ( true == m_bKeep ){
                    m_bKeep = false;
                    m_ibtnKeep.setImageResource(R.drawable.btn_checkbox_off);
                }else {
                    m_bKeep = true;
                    m_ibtnKeep.setImageResource(R.drawable.btn_checkbox_on);
                }

                break;
            case R.id.btn_forget:
            case R.id.ibtn_forget:
                changeActivity(CIForgetActivity.class);
                break;
            case R.id.btn_login:
                //尚未輸入帳號
                String strAcc = m_fragmentAcc.getText().toString();
                if ( 0 >= strAcc.length() ){
                    showDialog(getString(R.string.warning),
                            String.format(getString(R.string.please_input_field), m_fragmentAcc.getHint()),
                            getString(R.string.confirm));
                    return;
                }

                //尚未輸入密碼
                String strPw  = m_fragmentPw.getText().toString();
                if ( 0 >= strPw.length() ){
                    showDialog(getString(R.string.warning),
                            String.format(getString(R.string.please_input_field), m_fragmentPw.getHint()),
                            getString(R.string.confirm));
                    return;
                }

                if ( null != m_listener ) {
                    m_listener.onMemberLoginClick(strAcc, strPw, m_bKeep);
                }
                break;
            case R.id.ibtn_f_login:
                m_listener.onFacebookLoginClick(m_bKeep);
                break;
            case R.id.ibtn_g_login:
                m_listener.onGoogleLoginClick(m_bKeep);
                break;
            case R.id.btn_signup:
            case R.id.ibtn_signup:

                Intent intent = new Intent();
                intent.setClass(getActivity(), CISignUpActivity.class);
                intent.putExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, ((CILoginActivity)getActivity()).m_iViewId);
                getActivity().startActivityForResult(intent, UiMessageDef.REQUEST_CODE_SIGN_UP);
                getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
                break;
        }
    }

    public void uiSetParameterListener(onLoginListener onListener) {
        m_listener = onListener;
    }

    private void changeActivity(Class clazz){
        Intent intent = new Intent();
        intent.setClass(getActivity(), clazz);
        startActivity(intent);

        //測試切換activity滑入滑出動畫
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data) {

        if( requestCode == UiMessageDef.REQUEST_CODE_LOGIN_INPUT_DETAIL){
            if ( responseCode == getActivity().RESULT_OK){
                m_listener.onMemberLoginClick(
                        m_fragmentAcc.getText().toString(),
                        m_fragmentPw.getText().toString(),
                        m_bKeep);
            }else {
                //error
            }
        }

        super.onActivityResult(requestCode, responseCode, data);
    }
}
