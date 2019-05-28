package ci.function.SpecialCondition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Main.BaseActivity;
import ci.ui.FlightCard.CIFlightCardView;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ui.view.NavigationBar;

/**
 * Created by jlchen on 2016/3/7.
 */
public class CIFlightCanceledActivity extends BaseActivity implements OnClickListener{

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return getString(R.string.change_flight);
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {}

        @Override
        public void onLeftMenuClick() {}

        @Override
        public void onBackClick() {}

        @Override
        public void onDeleteClick() {

        }
        @Override
        public void onDemoModeClick() {}
    };

    private NavigationBar.onNavigationbarInterface m_onNavigationbarInterface;
    public NavigationBar m_Navigationbar   = null;
    public FrameLayout m_flayout_Content = null;

    private Bitmap m_bitmap            = null;
    private RelativeLayout  m_rlBg              = null;

    private TextView m_tvTitle = null;
    private TextView m_tvMsg = null;
    private TextView m_tvYourNewFlight = null;

    private FrameLayout m_flayout_FlightInfo = null;

    private TextView m_tvIf = null;

    private LinearLayout m_llButton = null;
    private RelativeLayout m_rlDisagree = null;
    private Button m_btnDisagree = null;
    private RelativeLayout m_rlAgree = null;
    private Button m_btnAgree = null;

    private LinearLayout m_llLeter= null;
    private Button m_btnLeter = null;
    private ImageButton m_ibtnLeter = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initialLayoutComponent() {

        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_flayout_Content = (FrameLayout) findViewById(R.id.container);

        m_rlBg              = (RelativeLayout) findViewById(R.id.rl_bg);
        m_bitmap            = ImageHandle.getLocalBitmap(m_Context, R.drawable.bg_login, 1);
        Drawable drawable   = new BitmapDrawable(m_Context.getResources(), m_bitmap);
        m_rlBg.setBackground(drawable);

        View ViewContent = View.inflate(this, R.layout.fragment_flight_canceled, null);
        m_flayout_Content.addView(ViewContent);

        m_tvTitle = (TextView) ViewContent.findViewById(R.id.tv_prompt_title);
        m_tvMsg = (TextView) ViewContent.findViewById(R.id.tv_prompt_msg);
        m_tvYourNewFlight = (TextView) ViewContent.findViewById(R.id.tv_your_new_flight);

        m_flayout_FlightInfo = (FrameLayout) ViewContent.findViewById(R.id.fl_flightInfo);

        m_tvIf = (TextView) ViewContent.findViewById(R.id.tv_if);

        m_llButton = (LinearLayout) ViewContent.findViewById(R.id.ll_button);
        m_rlDisagree = (RelativeLayout) ViewContent.findViewById(R.id.rl_disagree);
        m_btnDisagree = (Button) ViewContent.findViewById(R.id.btn_disagree);
        m_btnDisagree.setOnClickListener(this);
        m_rlAgree = (RelativeLayout) ViewContent.findViewById(R.id.rl_agree);
        m_btnAgree = (Button) ViewContent.findViewById(R.id.btn_agree);
        m_btnAgree.setOnClickListener(this);

        m_llLeter = (LinearLayout) ViewContent.findViewById(R.id.ll_decide_leter);
        m_btnLeter = (Button) ViewContent.findViewById(R.id.btn_decide_leter);
        m_btnLeter.setOnClickListener(this);
        m_ibtnLeter = (ImageButton) ViewContent.findViewById(R.id.ibtn_decide_leter);
        m_ibtnLeter.setOnClickListener(this);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {

        vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_20, m_tvTitle);
        vScaleDef.setMargins(m_tvTitle, 30, 20, 30, 0);
        m_tvTitle.setMinHeight(vScaleDef.getLayoutHeight(24));

        vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, m_tvMsg);
        vScaleDef.setMargins(m_tvMsg, 30, 16, 30, 0);
        m_tvMsg.setLineSpacing(vScaleDef.getLayoutHeight(4.7), 1.0f);

        vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, m_tvYourNewFlight);
        vScaleDef.setMargins(m_tvYourNewFlight, 30, 20, 30, 0);
        m_tvYourNewFlight.setMinHeight(vScaleDef.getLayoutHeight(19.3));

        CIFlightCardView ciFlightCardView = new CIFlightCardView(m_Context);
        ciFlightCardView.onOnlyOneFlightCard();
        ciFlightCardView.onFlightStatus_Normal();
        m_flayout_FlightInfo.addView(ciFlightCardView);
        vScaleDef.setMargins(m_flayout_FlightInfo, 10, 10, 10, 0);

        vScaleDef.setTextSize(13, m_tvIf);
        vScaleDef.setMargins(m_tvIf, 30, 0, 30, 0);
        RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams)m_tvIf.getLayoutParams();
        rParams.bottomMargin = vScaleDef.getLayoutHeight(129.3);
        m_tvIf.setLineSpacing(vScaleDef.getLayoutHeight(6.7), 1.0f);

        rParams = (RelativeLayout.LayoutParams)m_llButton.getLayoutParams();
        rParams.bottomMargin = vScaleDef.getLayoutHeight(69.3);
        rParams.leftMargin = vScaleDef.getLayoutWidth(20);
        rParams.rightMargin = vScaleDef.getLayoutWidth(20);

        rParams = (RelativeLayout.LayoutParams)m_btnDisagree.getLayoutParams();
        rParams.height = vScaleDef.getLayoutHeight(40);
        rParams.width = vScaleDef.getLayoutWidth(110);
        vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, m_btnDisagree);

        rParams = (RelativeLayout.LayoutParams)m_btnAgree.getLayoutParams();
        rParams.height = vScaleDef.getLayoutHeight(40);
        rParams.width = vScaleDef.getLayoutWidth(190);
        vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, m_btnAgree);

        vScaleDef.setMargins(m_llLeter, 0, 0, 0, 30);

        vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, m_btnLeter);

        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams)m_ibtnLeter.getLayoutParams();
        lParams.width = vScaleDef.getLayoutMinUnit(16);
        lParams.height = vScaleDef.getLayoutMinUnit(16);

    }

    @Override
    protected void setOnParameterAndListener() {
        m_onNavigationbarInterface = m_Navigationbar.uiSetParameterListener(
                m_onNavigationParameter, m_onNavigationbarListener);
        m_onNavigationbarInterface.hideBackButton();
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {

    }

    @Override
    protected void onLanguageChangeUpdateUI() {

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

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_disagree:
                Intent intent = new Intent();
                intent.setClass(m_Context,CICustomerSupportActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_agree:
            case R.id.btn_decide_leter:
            case R.id.ibtn_decide_leter:
                CIFlightCanceledActivity.this.finish();
                break;
        }
    }
}
