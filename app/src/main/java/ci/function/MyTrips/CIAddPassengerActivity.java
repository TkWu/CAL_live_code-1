package ci.function.MyTrips;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;

/**
 * Created by kevin on 2016/2/27.
 */
public class CIAddPassengerActivity extends BaseActivity {

    private EMode               m_mode  = EMode.PRECURSOR;
    public enum EMode{
        PRECURSOR, BASE_SINGLE, BASE_MORE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String name = getIntent().getStringExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE);
        if(null != name){
            m_mode = EMode.valueOf(name);
        }
        super.onCreate(savedInstanceState);
    }

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            switch (m_mode) {
                case PRECURSOR:
                    return m_Context.getString(R.string.find_my_booking_title);

                case BASE_SINGLE:
                case BASE_MORE:
                    return m_Context.getString(R.string.add_passenager_title);
            }
            return null;
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {

        }

        @Override
        public void onLeftMenuClick() {

        }

        @Override
        public void onBackClick() {
            CIAddPassengerActivity.this.finish();
        }

        @Override
        public void onDeleteClick() {

        }
        @Override
        public void onDemoModeClick() {}
    };

    View.OnTouchListener m_onBackgroundTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            HidekeyBoard();
            return false;
        }
    };

    private RelativeLayout m_relativeLayout  = null;
    public  NavigationBar  m_Navigationbar   = null;
    public  FrameLayout    m_flayout_Content = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_add_passenger;
    }

    @Override
    protected void initialLayoutComponent() {
        m_relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout);
        m_relativeLayout.setOnTouchListener(m_onBackgroundTouchListener);

        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_flayout_Content = (FrameLayout) findViewById(R.id.container);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

        switch (m_mode) {
            case PRECURSOR:
                ChangeFragment(new CIAskOtherPassengerFragment());
                break;
            case BASE_MORE:
                ChangeFragment(CIAddPassengerFragment.newInstance(CIAddPassengerFragment.EMode.MORE));
                break;
            case BASE_SINGLE:
                ChangeFragment(CIAddPassengerFragment.newInstance(CIAddPassengerFragment.EMode.SINGLE));
                break;
        }

    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {

    }

    /** 顯示送出結果畫面*/
    protected void ChangeFragment(android.support.v4.app.Fragment fragment) {

        // 開始Fragment的事務Transaction
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // 替换容器(container)原来的Fragment
        fragmentTransaction.replace(m_flayout_Content.getId(),
                                    fragment,
                                    fragment.getTag());

        // 提交事務
        fragmentTransaction.commitAllowingStateLoss();

    }

    @Override
    protected void onLanguageChangeUpdateUI() {}

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }
}
