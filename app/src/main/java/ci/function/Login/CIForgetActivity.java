package ci.function.Login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Main.BaseActivity;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ui.view.NavigationBar;

import static ci.ui.view.NavigationBar.onNavigationbarListener;
import static ci.ui.view.NavigationBar.onNavigationbarParameter;

/**登入查詢清單(忘記卡號.忘記密碼.申請密碼)
 * zeplin: 3.4-7
 * wireframe: p.22
 * Created by jlchen on 2016/2/19.
 */
public class CIForgetActivity extends BaseActivity implements View.OnClickListener{

    private onNavigationbarParameter m_onNavigationParameter = new onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.recover_account_title);
        }
    };

    private onNavigationbarListener m_onNavigationbarListener = new onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {}

        @Override
        public void onLeftMenuClick() {}

        @Override
        public void onBackClick() {
            CIForgetActivity.this.finish();
        }

        @Override
        public void onDeleteClick() {}
        @Override
        public void onDemoModeClick() {}
    };

    private NavigationBar 	m_Navigationbar		= null;
    private FrameLayout     m_flContent         = null;

    private Bitmap          m_bitmap            = null;
    private RelativeLayout  m_rlBg              = null;
    private ImageView       m_ivCard            = null;
    private TextView        m_tvTitle           = null;
    private TextView        m_tvMsg             = null;

    private LinearLayout    m_llList            = null;
    private View            m_vLine1            = null;

    private RelativeLayout  m_rlForgotCardNum   = null;
    private ImageView       m_ivForgotCardNum   = null;
    private TextView        m_tvForgotCardNum   = null;
    private View            m_vLine2            = null;

    private RelativeLayout  m_rlForgotPW        = null;
    private ImageView       m_ivForgotPW        = null;
    private TextView        m_tvForgotPW        = null;
    private View            m_vLine3            = null;

    private RelativeLayout  m_rlApplyPW         = null;
    private ImageView       m_ivApplyPW         = null;
    private TextView        m_tvApplyPW         = null;
    private View            m_vLine4            = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initialLayoutComponent() {
        m_Navigationbar		= (NavigationBar) findViewById(R.id.toolbar);
        m_flContent         = (FrameLayout) findViewById(R.id.container);

        m_rlBg              = (RelativeLayout) findViewById(R.id.rl_bg);
        m_bitmap            = ImageHandle.getLocalBitmap(m_Context, R.drawable.bg_login, 1);
        Drawable drawable   = new BitmapDrawable(m_Context.getResources(), m_bitmap);
        m_rlBg.setBackground(drawable);

        View ViewContent    = View.inflate(this, R.layout.fragment_forget, null);
        m_flContent.addView(ViewContent);

        m_ivCard            = (ImageView) ViewContent.findViewById(R.id.iv_forget);
        m_tvTitle           = (TextView) ViewContent.findViewById(R.id.tv_prompt_title);
        m_tvMsg             = (TextView) ViewContent.findViewById(R.id.tv_prompt_msg);

        m_llList            = (LinearLayout) ViewContent.findViewById(R.id.ll_list);
        m_vLine1            = ViewContent.findViewById(R.id.vline1);

        m_rlForgotCardNum   = (RelativeLayout) ViewContent.findViewById(R.id.rl_forget_card_number);
        m_rlForgotCardNum.setOnClickListener(this);
        m_ivForgotCardNum   = (ImageView) ViewContent.findViewById(R.id.img_forget_card_number_arrow);
        m_tvForgotCardNum   = (TextView) ViewContent.findViewById(R.id.tv_forget_card_number);
        m_vLine2            = ViewContent.findViewById(R.id.vline2);

        m_rlForgotPW        = (RelativeLayout) ViewContent.findViewById(R.id.rl_forget_password);
        m_rlForgotPW.setOnClickListener(this);
        m_ivForgotPW        = (ImageView) ViewContent.findViewById(R.id.img_forget_password_arrow);
        m_tvForgotPW        = (TextView) ViewContent.findViewById(R.id.tv_forget_password);
        m_vLine3            = ViewContent.findViewById(R.id.vline3);

        m_rlApplyPW         = (RelativeLayout) ViewContent.findViewById(R.id.rl_apply_password);
        m_rlApplyPW.setOnClickListener(this);
        m_ivApplyPW         = (ImageView) ViewContent.findViewById(R.id.img_apply_password_arrow);
        m_tvApplyPW         = (TextView) ViewContent.findViewById(R.id.tv_apply_password);
        m_vLine4            = ViewContent.findViewById(R.id.vline4);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        //圖片 上間隔40, 寬160, 高110
        RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) m_ivCard.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(40);
        rParams.width = vScaleDef.getLayoutMinUnit(160);
        rParams.height = vScaleDef.getLayoutMinUnit(110);

        //說明文字(粗體) 上間隔20, 左右間隔30, 字體大小20
        rParams = (RelativeLayout.LayoutParams) m_tvTitle.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(20);
        rParams.leftMargin = vScaleDef.getLayoutWidth(30);
        rParams.rightMargin= vScaleDef.getLayoutWidth(30);
        vScaleDef.setTextSize( vScaleDef.DEF_TEXT_SIZE_20, m_tvTitle );

        //說明文字 上間隔16, 左右間隔30, 字體大小16
        rParams = (RelativeLayout.LayoutParams) m_tvMsg.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(16);
        rParams.leftMargin = vScaleDef.getLayoutWidth(30);
        rParams.rightMargin= vScaleDef.getLayoutWidth(30);
        vScaleDef.setTextSize( vScaleDef.DEF_TEXT_SIZE_16, m_tvMsg );

        //選擇清單 下間隔59.3
        rParams = (RelativeLayout.LayoutParams) m_llList.getLayoutParams();
        rParams.bottomMargin = vScaleDef.getLayoutHeight(59.3);

        //分隔線 高0.7
        int iline = 1;
        if ( 0 < vScaleDef.getLayoutHeight(1) ){
            iline = vScaleDef.getLayoutHeight(1);
        }
        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams)m_vLine1.getLayoutParams();
        lParams.height = iline ;

        rParams = (RelativeLayout.LayoutParams) m_vLine2.getLayoutParams();
        rParams.height = iline ;

        rParams = (RelativeLayout.LayoutParams) m_vLine3.getLayoutParams();
        rParams.height = iline ;

        rParams = (RelativeLayout.LayoutParams) m_vLine4.getLayoutParams();
        rParams.height = iline ;

        //按鈕選項 高60
        lParams = (LinearLayout.LayoutParams)m_rlForgotCardNum.getLayoutParams();
        lParams.height = vScaleDef.getLayoutHeight(60);

        lParams = (LinearLayout.LayoutParams)m_rlForgotPW.getLayoutParams();
        lParams.height = vScaleDef.getLayoutHeight(60);

        lParams = (LinearLayout.LayoutParams)m_rlApplyPW.getLayoutParams();
        lParams.height = vScaleDef.getLayoutHeight(60);

        //箭頭圖 右間隔23, 寬高24
        rParams = (RelativeLayout.LayoutParams) m_ivForgotCardNum.getLayoutParams();
        rParams.rightMargin = vScaleDef.getLayoutWidth(23);
        rParams.width = vScaleDef.getLayoutMinUnit(24);
        rParams.height = vScaleDef.getLayoutMinUnit(24);

        rParams = (RelativeLayout.LayoutParams) m_ivForgotPW.getLayoutParams();
        rParams.rightMargin = vScaleDef.getLayoutWidth(23);
        rParams.width = vScaleDef.getLayoutMinUnit(24);
        rParams.height = vScaleDef.getLayoutMinUnit(24);

        rParams = (RelativeLayout.LayoutParams) m_ivApplyPW.getLayoutParams();
        rParams.rightMargin = vScaleDef.getLayoutWidth(23);
        rParams.width = vScaleDef.getLayoutMinUnit(24);
        rParams.height = vScaleDef.getLayoutMinUnit(24);

        //按鈕選項文字 左間隔30, 字體大小16
        rParams = (RelativeLayout.LayoutParams) m_tvForgotCardNum.getLayoutParams();
        rParams.leftMargin = vScaleDef.getLayoutWidth(30);
        vScaleDef.setTextSize( vScaleDef.DEF_TEXT_SIZE_16 ,m_tvForgotCardNum);

        rParams = (RelativeLayout.LayoutParams) m_tvForgotPW.getLayoutParams();
        rParams.leftMargin = vScaleDef.getLayoutWidth(30);
        vScaleDef.setTextSize( vScaleDef.DEF_TEXT_SIZE_16 ,m_tvForgotPW);

        rParams = (RelativeLayout.LayoutParams) m_tvApplyPW.getLayoutParams();
        rParams.leftMargin = vScaleDef.getLayoutWidth(30);
        vScaleDef.setTextSize( vScaleDef.DEF_TEXT_SIZE_16 ,m_tvApplyPW);

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
    public void onClick(View v) {

        switch ( v.getId() ){
            case R.id.rl_forget_card_number:
                changeActivity(CIForgetCardNumberActivity.class);
                break;
            case R.id.rl_forget_password:
                changeActivity(CIForgetPasswordActivity.class);
                break;
            case R.id.rl_apply_password:
                changeActivity(CIApplyPasswordActivity.class);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (null != m_rlBg) {
            m_rlBg.setBackground(null);
        }
        if (null != m_bitmap) {
            m_bitmap.recycle();
        }
        System.gc();
        super.onDestroy();
    }

    private void changeActivity(Class clazz){
        Intent intent = new Intent();
        intent.setClass(m_Context, clazz);
        startActivity(intent);

        //測試切換activity滑入滑出動畫
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }
}
