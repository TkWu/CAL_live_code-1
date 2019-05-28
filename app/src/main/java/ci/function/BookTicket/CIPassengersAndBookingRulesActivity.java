package ci.function.BookTicket;

import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Main.BaseActivity;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;

/**
 * Created by kevincheng on 2016/4/4.
 *
 * 棄用，改使用CITermsAndConditionsActivity
 */
@Deprecated
public class CIPassengersAndBookingRulesActivity extends BaseActivity implements View.OnClickListener{

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.passengers_and_booking_rules);
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
        public void onDemoModeClick() {}
    };

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);

    }

    public NavigationBar 	m_Navigationbar		= null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_terms_and_conditions;
    }

    @Override
    protected void initialLayoutComponent() {
        m_Navigationbar		= (NavigationBar) findViewById(R.id.toolbar);
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

    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {

    }


    @Override
    protected void onLanguageChangeUpdateUI() {}

    @Override
    public void onClick(View v) {


    }

}
