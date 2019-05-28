package ci.function.PersonalDetail.SocialNetwork;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.view.ImageHandle;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIConnSocialResp;
import ci.ws.Presenter.CIConnSocialNetworkPresenter;
import ci.ws.Presenter.Listener.CIConnSocialNetworkListener;

/**
 * Created by jlchen on 2016/3/17.
 */
public class CISocialNetworkDisconnectActivity extends BaseActivity implements View.OnClickListener{

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.disconnect_social_account);
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {}

        @Override
        public void onLeftMenuClick() {}

        @Override
        public void onBackClick() {
            CISocialNetworkDisconnectActivity.this.finish();
        }

        @Override
        public void onDeleteClick() {}

        @Override
        public void onDemoModeClick() {}
    };

    public NavigationBar 	m_Navigationbar     = null;
    public FrameLayout      m_flContent         = null;

    private Bitmap          m_bitmap            = null;
    private RelativeLayout  m_rlBg              = null;

    private ImageView       m_ivAsk             = null;
    private LinearLayout    m_llAsk             = null;
    private LinearLayout    m_llDisconnect      = null;
    private TextView        m_tvDissconnect     = null;

    private String m_strSocialNetwork = "";

    private String m_strSocialType = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initialLayoutComponent() {

        m_strSocialNetwork = this.getIntent().getStringExtra(
                UiMessageDef.BUNDLE_SOCIAL_NETWORK_DISCONNECT_TAG);

        m_Navigationbar	= (NavigationBar) findViewById(R.id.toolbar);
        m_flContent 	= (FrameLayout) findViewById(R.id.container);

        m_rlBg          = (RelativeLayout) findViewById(R.id.rl_bg);
        m_bitmap        = ImageHandle.getLocalBitmap(m_Context, R.drawable.bg_login, 1);
        Drawable drawable = new BitmapDrawable(m_Context.getResources(), m_bitmap);
        m_rlBg.setBackground(drawable);

        View ViewContent = View.inflate(this,  R.layout.fragment_add_passenger_ask_with_other_companions, null);
        m_flContent.addView(ViewContent);

        m_ivAsk         = (ImageView) ViewContent.findViewById(R.id.iv_ask_with_companions_img);
        m_ivAsk.setVisibility(View.GONE);

        m_llAsk         = (LinearLayout) ViewContent.findViewById(R.id.ll_ask);
        m_llAsk.setVisibility(View.GONE);
        m_llDisconnect  = (LinearLayout) ViewContent.findViewById(R.id.ll_social_disconnect);
        m_llDisconnect.setVisibility(View.VISIBLE);

        m_tvDissconnect = (TextView) ViewContent.findViewById(R.id.tv_disconnect);

        if ( 0 < m_strSocialNetwork.length() ){
            m_tvDissconnect.setText(
                    String.format(getString(R.string.are_you_sure_you_want_to_disconnect), m_strSocialNetwork) );
        }

        ViewContent.findViewById(R.id.btn_no).setOnClickListener(this);
        ViewContent.findViewById(R.id.btn_yes).setOnClickListener(this);
        ((Button)ViewContent.findViewById(R.id.btn_no)).setText(R.string.cancel);
        ((Button)ViewContent.findViewById(R.id.btn_yes)).setText(R.string.disconnect);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(m_flContent);

        vScaleDef.selfAdjustSameScaleView(m_ivAsk, 160, 127);
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
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
    protected void onLanguageChangeUpdateUI() {}

    @Override
    protected void onDestroy() {
        if (null != m_rlBg) {
            m_rlBg.setBackground(null);
        }
        if (null != m_bitmap) {
            ImageHandle.recycleBitmap(m_bitmap);
        }
        System.gc();
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_yes:

                String strId;
                String strType;
                if (m_strSocialNetwork.equals(getString(R.string.facebook))){
                    strId = CIApplication.getLoginInfo().GetFbLoginId();
                    strType = UiMessageDef.BUNDLE_LOGIN_TYPE_FACEBOOK;

                }else {
                    strId = CIApplication.getLoginInfo().GetGoogleLoginId();
                    strType = UiMessageDef.BUNDLE_LOGIN_TYPE_GOOGLE;

                }

                m_strSocialType = strType;

                CIConnSocialNetworkPresenter.getInstance(m_ConnSocialNetworkListener).DisConnSocialNetworkFromWS(
                    CIApplication.getLoginInfo().GetUserMemberCardNo(),
                    strId,
                    strType);

//                setResult(UiMessageDef.RESULT_CODE_SOCIAL_NETWORK_DISCONNECT_OK_TAG);
//                CISocialNetworkDisconnectActivity.this.finish();
                break;
            case R.id.btn_no:
                CISocialNetworkDisconnectActivity.this.finish();
                break;

        }
    }

    private CIConnSocialNetworkListener m_ConnSocialNetworkListener = new CIConnSocialNetworkListener() {
        @Override
        public void onSocialConnSuccess(String rt_code, String rt_msg, CIConnSocialResp connSocialResp) { }

        @Override
        public void onSocialConnError(final String rt_code, final String rt_msg) { }

        @Override
        public void onDisConnSocialConnSuccess(String rt_code, String rt_msg) {

            if( m_strSocialType.equals(getString(R.string.facebook)) ) {
                CIApplication.getLoginInfo().SetFbCombineStatus(false);
                CIApplication.getLoginInfo().SetFbEmail("");
                CIApplication.getLoginInfo().SetFbLoginId("");
            } else {
                CIApplication.getLoginInfo().SetGoogleCombineStatus(false);
                CIApplication.getLoginInfo().SetGoogleEmail("");
                CIApplication.getLoginInfo().SetGoogleLoginId("");
            }

            m_BaseHandler.post(new Runnable() {
                @Override
                public void run() {
                    setResult(UiMessageDef.RESULT_CODE_SOCIAL_NETWORK_DISCONNECT_OK_TAG);
                    CISocialNetworkDisconnectActivity.this.finish();
                }
            });
        }

        @Override
        public void onDisConnSocialConnError(final String rt_code, final String rt_msg) {

            m_BaseHandler.post(new Runnable() {
                @Override
                public void run() {

                    CIAlertDialog dialog = new CIAlertDialog(m_Context, new CIAlertDialog.OnAlertMsgDialogListener() {
                        @Override
                        public void onAlertMsgDialog_Confirm() {}

                        @Override
                        public void onAlertMsgDialogg_Cancel() {}
                    });

                    dialog.uiSetTitleText("Warning");
                    dialog.uiSetContentText(rt_msg);
                    dialog.uiSetConfirmText(getString(R.string.confirm));
                    dialog.show();
                }
            });
        }

        @Override
        public void showProgress() {
            showProgressDialog();
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
        }

        @Override
        public void onAuthorizationFailedError(String rt_code, String rt_msg) {
            isProcessWSErrorCodeByOtherActivity(rt_code, rt_msg);
        }
    };
}
