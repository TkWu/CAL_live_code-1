package ci.function.MyTrips.Detail.AddBaggage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;

import com.chinaairlines.mobile30.R;

import ci.function.Main.BaseActivity;
import ci.function.MyTrips.Detail.CIMyTripsDetialActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIEBPaymentResp;
import ci.ws.Models.entities.CITripListResp_Itinerary;

/**
 * Created by kevincheng on 2017/9/22.
 * 付款結果頁
 */

public class CIPaymentsResultActivity extends BaseActivity implements View.OnClickListener{
    private NavigationBar                   m_Navigationbar = null;
    private CIEBPaymentResp                 m_ebpResp       = null;
    private Button                          m_btnNext       = null;
    private CITripListResp_Itinerary        m_tripData      = null;
    private EMode                           m_mode          = null;
    private String                          m_strFailMsg    = "";

    public enum EMode {
        Success,Fail
    }

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return getString(R.string.trip_detail_passenger_bag_pay_final_page);
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
        backTripDetail();
    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_trip_detail_passenger_payments_result;
    }

    @Override
    protected void initialLayoutComponent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(null != bundle) {
            m_ebpResp = (CIEBPaymentResp)bundle.getSerializable(UiMessageDef.BUNDLE_EBPAYMENTS_RESP);
            m_tripData = (CITripListResp_Itinerary)bundle.getSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST);
            m_mode = EMode.valueOf(bundle.getString(UiMessageDef.BUNDLE_PAYMENTS_RESULT_MODE));
            m_strFailMsg = bundle.getString(UiMessageDef.BUNDLE_FRAGMENT_DATA);
        }

        m_btnNext = (Button) findViewById(R.id.btn_next);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar = (NavigationBar)findViewById(R.id.toolbar);
        NavigationBar.onNavigationbarInterface listener
                = m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        listener.hideBackButton();
        m_btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                backTripDetail();
                break;
        }
    }

    private void backTripDetail(){
        Intent intent = new Intent();
        intent.setClass(this, CIMyTripsDetialActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
        if(EMode.Success == m_mode) {
            CIPaymentsResultSuccessFragment fragment = CIPaymentsResultSuccessFragment.newInstance(m_ebpResp, m_tripData);
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, fragment).commitAllowingStateLoss();
        } else if(EMode.Fail == m_mode){
            CIPaymentsResultFailFragment fragment = CIPaymentsResultFailFragment.newInstance(m_strFailMsg);
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, fragment).commitAllowingStateLoss();
        }
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
