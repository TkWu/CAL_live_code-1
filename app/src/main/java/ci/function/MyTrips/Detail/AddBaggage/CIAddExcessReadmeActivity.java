package ci.function.MyTrips.Detail.AddBaggage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.chinaairlines.mobile30.R;

import ci.function.Main.BaseActivity;
import ci.function.MyTrips.Detail.common.CIReadmeActivity;
import ci.ui.WebView.CIWebViewActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;

/**
 * Created by kevincheng on 2017/9/26.
 * 加購說明頁
 */

public class CIAddExcessReadmeActivity extends BaseActivity implements View.OnClickListener{
    private NavigationBar m_Navigationbar = null;

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return getString(R.string.trip_detail_passenger_bag_pay_add_excess_readme);
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
        return R.layout.activity_trip_detail_passenger_add_excess_bag_readme;
    }

    @Override
    protected void initialLayoutComponent() {

    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_prod), 24, 24);
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_notes), 24, 24);
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_rules), 24, 24);
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar = (NavigationBar)findViewById(R.id.toolbar);
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        findViewById(R.id.rl_prod).setOnClickListener(this);
        findViewById(R.id.rl_notes).setOnClickListener(this);
        findViewById(R.id.rl_rules).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_prod:
                changeToWebActivity(getString(R.string.trip_detail_passenger_bag_pay_add_excess_production_readme), getString(R.string.trip_detail_passenger_bag_pay_add_excess_production_content_url));
                break;
            case R.id.rl_notes:
                changeToReadmeActivity(getString(R.string.trip_detail_passenger_bag_pay_add_excess_notes), getString(R.string.trip_detail_passenger_bag_pay_add_excess_notes_content));
                break;
            case R.id.rl_rules:
                changeToReadmeActivity(getString(R.string.trip_detail_passenger_bag_pay_add_excess_returned_purchase_rules), getString(R.string.trip_detail_passenger_bag_pay_add_excess_returned_purchase_rules_content));
                break;
        }
    }

    private void changeToReadmeActivity(String title, String msg){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(UiMessageDef.BUNDLE_ACTIVITY_DATA_TITLE, title);
        bundle.putString(UiMessageDef.BUNDLE_ACTIVITY_DATA, msg);
        intent.putExtras(bundle);
        intent.setClass(this, CIReadmeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    private void changeToWebActivity(String title, String url){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG, title);
        bundle.putString(UiMessageDef.BUNDLE_WEBVIEW_URL_TAG, url);
        intent.putExtras(bundle);
        intent.setClass(this, CIWebViewActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
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
