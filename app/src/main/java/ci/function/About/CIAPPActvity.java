package ci.function.About;

import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.chinaairlines.mobile30.R;

import ci.function.Main.BaseActivity;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;

/**
 * Created by kevincheng on 2016/4/8.
 * 其他APP：華航相關APP
 */
public class CIAPPActvity extends BaseActivity{
    private NavigationBar       m_Navigationbar     = null;

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return getString(R.string.china_airlines_app);
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
            onBackPressed();
        }

        @Override
        public void onDeleteClick() {

        }

        @Override
        public void onDemoModeClick() {

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_china_airlines_app;
    }

    @Override
    protected void initialLayoutComponent() {
        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);

    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        CIAPPFragment fragment = new CIAPPFragment();
        transaction.replace(findViewById(R.id.fl_app).getId(), fragment, fragment.getClass().getSimpleName());
        transaction.commit();
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

}
