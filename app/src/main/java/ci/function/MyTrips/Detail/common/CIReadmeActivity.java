package ci.function.MyTrips.Detail.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;

/**
 * Created by kevincheng on 2017/9/28.
 */

public class CIReadmeActivity extends BaseActivity{
    private NavigationBar m_Navigationbar   = null;
    private String        m_strTitle        = "";
    private String        m_strMsg          = "";

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_strTitle;
        }
    };

    private NavigationBar.AboutBtn m_onNavigationbarListener = new NavigationBar.AboutBtn() {

        @Override
        public void onRightMenuClick() {}

        @Override
        public void onLeftMenuClick() {}

        @Override
        public void onBackClick() {
            onBackPressed();
        }

        @Override
        public void onDeleteClick() {}

        @Override
        public void onDemoModeClick() {}

        @Override
        public void onAboutClick() {
        }
    };

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_readme;
    }

    @Override
    protected void initialLayoutComponent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(null != bundle) {
            m_strTitle  = bundle.getString(UiMessageDef.BUNDLE_ACTIVITY_DATA_TITLE);
            m_strMsg    = bundle.getString(UiMessageDef.BUNDLE_ACTIVITY_DATA);
        }

        TextView tvMsg = (TextView) findViewById(R.id.tv_msg);
        tvMsg.setText(m_strMsg);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar = (NavigationBar)findViewById(R.id.toolbar);
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
    protected void onLanguageChangeUpdateUI() {

    }
}
