package ci.function.SpecialCondition;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Main.BaseActivity;
import ci.function.SpecialCondition.Adapter.CITravelAlertAdapter;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ui.view.NavigationBar;

/**
 * Created by jlchen on 2016/3/7.
 */
public class CITravelAlertActivity extends BaseActivity implements OnClickListener {

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return getString(R.string.travel_alert);
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {}

        @Override
        public void onLeftMenuClick() {}

        @Override
        public void onBackClick() {
            onBackPressed();
        }

        @Override
        public void onDeleteClick() {

        }
        @Override
        public void onDemoModeClick() {}
    };

    public NavigationBar    m_Navigationbar     = null;
    public FrameLayout      m_flayout_Content   = null;

    private Bitmap          m_bitmap            = null;
    private RelativeLayout  m_rlBg              = null;

    private ListView        m_lv                = null;
    private CITravelAlertAdapter m_adapter      = null;

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

        View ViewContent = View.inflate(this, R.layout.fragment_emergency_announcement, null);
        m_flayout_Content.addView(ViewContent);

        m_lv = (ListView)ViewContent.findViewById(R.id.lv_travel_alert);
        m_adapter = new CITravelAlertAdapter(m_Context, new ArrayList<String>(2));
        m_lv.setAdapter(m_adapter);

    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        m_lv.setDividerHeight(vScaleDef.getLayoutHeight(10));
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

    }
}
