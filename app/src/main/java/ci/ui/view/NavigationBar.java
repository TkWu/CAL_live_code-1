package ci.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Core.CIApplication;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;

/**
 * toolbar
 * Created by jlchen
 */
public class NavigationBar extends RelativeLayout implements View.OnClickListener{

    public interface onNavigationbarParameter {
        /**要顯示哪一類型的toolbar-Home:true, 其他:false*/
        Boolean GetToolbarType();

        /**取得Title字串*/
        String GetTitle();
    }

    public interface onNavigationbarListener {
        /**開啟右側SideMenu*/
        void onRightMenuClick();

        /**開啟左側SideMenu*/
        void onLeftMenuClick();

        /**返回上一頁*/
        void onBackClick();

        /**刪除APIS按鈕*/
        void onDeleteClick();

        //提供測試模式
        /**測試模式*/
        void onDemoModeClick();
    }

    public interface AboutBtn extends onNavigationbarListener{
        void onAboutClick();
    }

    public interface onNavigationbarInterface{
        /**首頁logo拿掉改字串 或反過來*/
        void changeHomeTitle(String strText);

        /**變更語系*/
        void changeLanguageText(String strText);

        /**隱藏back按鈕*/
        void hideBackButton();

        //644336 2019 2月3月 AI/行事曆/截圖/注意事項
        /**顯示back按鈕*/
        void revealBackButton();
        //end 644336 2019 2月3月 AI/行事曆/截圖/注意事項

        /**顯示垃圾桶*/
        void showGarbageButton();

        /**顯示說明*/
        void showAboutButton();

        //644336 2019 2月3月 AI/行事曆/截圖/注意事項
        /**隱藏說明*/
        void hideAboutButton();
        //644336 2019 2月3月 AI/行事曆/截圖/注意事項

        /**左上方back更換成close圖案*/
        void showCloseButton();

        //提供測試模式
        /**Demo模式*/
        void onDemoMode();
    }

    private onNavigationbarInterface m_onNavigationbarInterface = new onNavigationbarInterface() {
        @Override
        public void changeHomeTitle(String strText) {
            if ( null != strText && strText.length() > 0 ){
                m_tvTitleHome.setText( strText );
                m_tvTitleHome.setVisibility(VISIBLE);
                m_ibtnLogo.setVisibility(GONE);
            }else {
                m_tvTitleHome.setVisibility(GONE);
                m_ibtnLogo.setVisibility(VISIBLE);
            }
        }

        @Override
        public void changeLanguageText(String strText) {
            m_tvTitle.setText(strText);
        }

        @Override
        public void hideBackButton() {
            m_ibtnBack.setVisibility(GONE);
        }

        //644336 2019 2月3月 AI/行事曆/截圖/注意事項
        @Override
        public void revealBackButton() {
            m_ibtnBack.setVisibility(VISIBLE);
        }
        //end 644336 2019 2月3月 AI/行事曆/截圖/注意事項

        @Override
        public void showGarbageButton() {
            m_ibtnGarbage.setVisibility(VISIBLE);
        }

        @Override
        public void showAboutButton() {
            m_ibtnRightAbout.setVisibility(VISIBLE);
        }

        //644336 2019 2月3月 AI/行事曆/截圖/注意事項
        @Override
        public void hideAboutButton() {
            m_ibtnRightAbout.setVisibility(GONE);
        }
        //644336 2019 2月3月 AI/行事曆/截圖/注意事項

        @Override
        public void onDemoMode() {
            m_ivbtnSetting.setVisibility(VISIBLE);
        }

        @Override
        public void showCloseButton() {
            if(null != m_ibtnBack) {
                m_ibtnBack.setImageResource(R.drawable.btn_iframe_close);
            }
        }

    };

    public NavigationBar(Context context) {
        this(context, null);
    }

    public NavigationBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        m_Context = context;
    }

    private Context m_Context = null;

    /**判斷要顯示Home版還是back版的toolbar*/
    private Boolean m_bIsShowHomeToolbar = true; //true為home

    private RelativeLayout m_rlLayout = null;

    private RelativeLayout m_rlLayoutHome = null;
    private ImageButton m_ibtnLeftSidemenu = null;
    private ImageButton m_ibtnLogo = null;
    private TextView m_tvTitleHome = null;
    private ImageButton m_ibtnRightSidemenu = null;
    private ImageButton m_ibtnRightAbout = null;

    private RelativeLayout m_rlLayoutOther = null;
    private ImageButton m_ibtnBack = null;
    private TextView m_tvTitle = null;
    private ImageButton m_ibtnGarbage = null;
    //for Test mode
    private ImageButton m_ivbtnSetting = null;
    //title 模式下，顯示ｌｏｇｏ使用
    private ImageButton m_ibtnLogo_ii = null;


    private View m_vDiv = null;

    private onNavigationbarParameter m_onParameter = null;
    private onNavigationbarListener m_onListener = null;

    public onNavigationbarInterface uiSetParameterListener(onNavigationbarParameter onParameter, onNavigationbarListener onListener) {
        m_onParameter = onParameter;
        m_onListener = onListener;

        initialLayoutComponent();
        setTextSizeAndLayoutParams();

        return m_onNavigationbarInterface;
    }

    private void initialLayoutComponent() {
        LayoutInflater.from(m_Context).inflate(R.layout.layout_custom_toolbar, this, true);

        m_rlLayout = (RelativeLayout) findViewById(R.id.rlayout_bg);

        m_rlLayoutHome = (RelativeLayout) findViewById(R.id.rl_home_toolbar);
        m_ibtnLeftSidemenu = (ImageButton) findViewById(R.id.img_personal);
        m_tvTitleHome = (TextView) findViewById(R.id.tv_title_home);
        m_ibtnLogo = (ImageButton) findViewById(R.id.img_logo);
        m_ibtnRightSidemenu = (ImageButton) findViewById(R.id.img_menu);
        m_ibtnRightAbout = (ImageButton) findViewById(R.id.img_about);

        m_rlLayoutOther = (RelativeLayout) findViewById(R.id.rl_other_toolbar);
        m_ibtnBack = (ImageButton) findViewById(R.id.img_back);
        m_tvTitle = (TextView) findViewById(R.id.tv_title);
        m_ibtnGarbage = (ImageButton) findViewById(R.id.img_delete);
        m_ivbtnSetting= (ImageButton)findViewById(R.id.iv_setting);

        m_ibtnLogo_ii = (ImageButton) findViewById(R.id.img_logo_ii);

        m_vDiv = (View) findViewById(R.id.vline);

        m_ibtnRightSidemenu.setOnClickListener(this);
        m_ibtnLeftSidemenu.setOnClickListener(this);
        m_ibtnBack.setOnClickListener(this);
        m_ibtnGarbage.setOnClickListener(this);
        m_ivbtnSetting.setOnClickListener(this);
        m_ibtnRightAbout.setOnClickListener(this);
    }

    private void setTextSizeAndLayoutParams() {
        ViewScaleDef viewScaleDef = ViewScaleDef.getInstance(m_Context);

        if( null!= m_onParameter){
            m_bIsShowHomeToolbar = m_onParameter.GetToolbarType();

            if ( m_onParameter.GetTitle().length() > 0 ) {
                m_tvTitle.setText(m_onParameter.GetTitle());
            }
        }

        if (true == m_bIsShowHomeToolbar) {
            m_rlLayoutHome.setVisibility(VISIBLE);
            m_rlLayoutOther.setVisibility(GONE);

        } else {
            m_rlLayoutHome.setVisibility(GONE);
            m_rlLayoutOther.setVisibility(VISIBLE);
            if ( TextUtils.isEmpty( m_onParameter.GetTitle() ) ){
                m_ibtnLogo_ii.setVisibility(VISIBLE);
                m_tvTitle.setVisibility(GONE);
            }
        }

        //增加測試模式 ryan
        if ( AppInfo.getInstance(CIApplication.getContext()).GetDemoMode() ){
            m_ivbtnSetting.setVisibility(VISIBLE);
        }

        m_rlLayout.getLayoutParams().height = viewScaleDef.getLayoutHeight(56);

        RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) m_rlLayoutHome.getLayoutParams();
        rParams.height = viewScaleDef.getLayoutHeight(55.7);

        rParams = (RelativeLayout.LayoutParams) m_ibtnLeftSidemenu.getLayoutParams();
        rParams.leftMargin = viewScaleDef.getLayoutWidth(16);
        rParams.height = viewScaleDef.getLayoutMinUnit(32);
        rParams.width = viewScaleDef.getLayoutMinUnit(32);

        rParams = (RelativeLayout.LayoutParams) m_ibtnLogo.getLayoutParams();
        rParams.height = viewScaleDef.getLayoutMinUnit(26);
        rParams.width = viewScaleDef.getLayoutMinUnit(180.6);

        rParams = (RelativeLayout.LayoutParams) m_tvTitleHome.getLayoutParams();
        rParams.leftMargin = viewScaleDef.getLayoutWidth(50);
        rParams.rightMargin = viewScaleDef.getLayoutWidth(50);
        viewScaleDef.setTextSize(20, m_tvTitleHome);

        rParams = (RelativeLayout.LayoutParams) m_ibtnRightSidemenu.getLayoutParams();
        rParams.rightMargin = viewScaleDef.getLayoutWidth(16);
        rParams.height = viewScaleDef.getLayoutMinUnit(32);
        rParams.width = viewScaleDef.getLayoutMinUnit(32);

        rParams = (RelativeLayout.LayoutParams) m_ibtnRightAbout.getLayoutParams();
        rParams.rightMargin = viewScaleDef.getLayoutWidth(16);
        rParams.height = viewScaleDef.getLayoutMinUnit(32);
        rParams.width = viewScaleDef.getLayoutMinUnit(32);

        rParams = (RelativeLayout.LayoutParams) m_rlLayoutOther.getLayoutParams();
        rParams.height = viewScaleDef.getLayoutHeight(55.7);

        rParams = (RelativeLayout.LayoutParams) m_ibtnBack.getLayoutParams();
        rParams.leftMargin = viewScaleDef.getLayoutWidth(16);
        rParams.height = viewScaleDef.getLayoutMinUnit(32);
        rParams.width = viewScaleDef.getLayoutMinUnit(32);

        rParams = (RelativeLayout.LayoutParams) m_tvTitle.getLayoutParams();
        rParams.leftMargin = viewScaleDef.getLayoutWidth(50);
        rParams.rightMargin = viewScaleDef.getLayoutWidth(50);
        viewScaleDef.setTextSize(20, m_tvTitle);

        rParams = (RelativeLayout.LayoutParams) m_ibtnGarbage.getLayoutParams();
        rParams.rightMargin = viewScaleDef.getLayoutWidth(16);
        rParams.height = viewScaleDef.getLayoutMinUnit(32);
        rParams.width = viewScaleDef.getLayoutMinUnit(32);

        rParams = (RelativeLayout.LayoutParams) m_vDiv.getLayoutParams();
        if ( 0 < viewScaleDef.getLayoutHeight(0.3) ){
            rParams.height = viewScaleDef.getLayoutHeight(0.3);
        }else {
            rParams.height = 1;
        }
    }

    public void switchBackBtn(boolean isEnable){
        if(isEnable) {
            m_ibtnBack.setVisibility(View.VISIBLE);
        } else {
            m_ibtnBack.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        if ( null == m_onListener )
            return;

        switch ( v.getId() ){
            case R.id.img_personal :
                m_onListener.onLeftMenuClick();
                break;
            case R.id.img_menu :
                m_onListener.onRightMenuClick();
                break;
            case R.id.img_back :
                m_onListener.onBackClick();
                break;
            case R.id.img_delete :
                m_onListener.onDeleteClick();
                break;
            case R.id.iv_setting :
                m_onListener.onDemoModeClick();
                break;
            case R.id.img_about :
                ((AboutBtn)m_onListener).onAboutClick();
                break;
        }
    }

    /** 通知NavigationBar 更新Title*/
    public void updateTitle() {
        if( null!= m_onParameter){
            m_bIsShowHomeToolbar = m_onParameter.GetToolbarType();

            if ( m_onParameter.GetTitle().length() > 0 ) {
                m_tvTitle.setText(m_onParameter.GetTitle());
            }
        }
    }
}
