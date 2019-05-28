package ci.function.MyTrips.Detail.AddBaggage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIInquiryEBBasicInfoReq;
import ci.ws.Models.entities.CIInquiryEBBasicInfoResp;
import ci.ws.Models.entities.CIInquiryExcessBaggageInfoReq;
import ci.ws.Models.entities.CIPassengerListResp_PaxInfo;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Presenter.CIInquiryEBBasicInfoPresenter;
import ci.ws.Presenter.Listener.CIInquiryEBBasicInfoListener;

/**
 * Created by kevincheng on 2017/9/15.
 * 加購行李
 */

public class CIAddExcessBaggageActivity extends BaseActivity implements View.OnClickListener,
                                                                        CIInquiryEBBasicInfoListener {
    private NavigationBar                 m_Navigationbar = null;
    private CIPassengerListResp_PaxInfo   m_passengerItem = null;
    private String                        m_strTitle      = "";
    private CITripListResp_Itinerary      m_tripData      = null;
    private CIAddExcessBaggageFragment    m_fragment      = null;
    private Button                        m_btnNext       = null;
    private CIInquiryEBBasicInfoPresenter m_presenter     = null;

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
            changeToReadme();
        }
    };

    private void changeToReadme(){
        Intent intent = new Intent();
        intent.setClass(this, CIAddExcessReadmeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_trip_detail_passenger_add_excess_baggage;
    }

    @Override
    protected void initialLayoutComponent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(null != bundle) {
            m_passengerItem = (CIPassengerListResp_PaxInfo)bundle.getSerializable(UiMessageDef.BUNDLE_PASSENGER_INFO_SINGLE);
            m_tripData = (CITripListResp_Itinerary)bundle.getSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST);
        }

        if(null != m_tripData) {
            m_strTitle = String.format(getString(R.string.my_trips_goto),
                                       m_tripData.Departure_Station,
                                       m_tripData.Arrival_Station);
        }

        if(null != m_passengerItem && null != m_tripData) {
            m_presenter = CIInquiryEBBasicInfoPresenter.getInstance(this);
            CIInquiryEBBasicInfoReq       req       = new CIInquiryEBBasicInfoReq();
            req.pax_num = m_passengerItem.Pax_Number;
            req.pnr_id  = m_tripData.Pnr_Id;
            req.pnr_seq = m_tripData.Pnr_Seq;
            m_presenter.getInfo(req);
        }

        m_btnNext = (Button)findViewById(R.id.btn_next);
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
        listener.showAboutButton();
        findViewById(R.id.btn_next).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_next:
                // TODO: 2017/9/18 取得購買選擇的量傳遞到下一頁
                CIInquiryExcessBaggageInfoReq req = m_fragment.getAddBagInfo();
                Bundle bundle = new Bundle();
                bundle.putSerializable(UiMessageDef.BUNDLE_INQUIRY_EXCESS_BAGGAGE_INFO_REQ, req);
                bundle.putSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST, m_tripData);
                intent.putExtras(bundle);
                intent.setClass(this, CIInquiryExcessBaggageInfoActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
                break;
        }
    }



    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

    }

    @Override
    public void onSuccess(String rt_code, String rt_msg, CIInquiryEBBasicInfoResp data) {

        m_fragment = CIAddExcessBaggageFragment.newInstance(data);
        m_fragment.setListener(new CIAddExcessBaggageFragment.ICallBack() {
            @Override
            public void onClickAddOrDecreaseButton(boolean isAddBag) {
                setButtonEnable(isAddBag);

            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, m_fragment).commitAllowingStateLoss();
    }


    private void setButtonEnable(boolean bEnable){
        m_btnNext.setEnabled(bEnable);
        if(bEnable) {
            m_btnNext.setAlpha(1.0f);
        } else {
            m_btnNext.setAlpha(0.5f);
        }
    }

    @Override
    public void onError(String rt_code, String rt_msg) {
        showDialog(getString(R.string.warning),
                   rt_msg,
                   getString(R.string.confirm),
                   null,
                   new CIAlertDialog.OnAlertMsgDialogListener() {
                       @Override
                       public void onAlertMsgDialog_Confirm() {
                           onBackPressed();
                       }

                       @Override
                       public void onAlertMsgDialogg_Cancel() {

                       }
                   });
    }

    @Override
    public void showProgress() {
        showProgressDialog();
    }

    @Override
    public void hideProgress() {
        hideProgressDialog();
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
    public void onPause() {
        super.onPause();
        if(null != m_presenter) {
            m_presenter.interrupt();
        }
    }

    private CIInquiryEBBasicInfoResp getTestData(){
        // TODO: 2017/9/18 暫時測試用
        CIInquiryEBBasicInfoResp datas = new CIInquiryEBBasicInfoResp();

        datas.ssrType = "EXWG";
        CIInquiryEBBasicInfoResp.PaxInfo  paxInfo1  = new CIInquiryEBBasicInfoResp.PaxInfo();
        CIInquiryEBBasicInfoResp.PaxInfo  paxInfo2  = new CIInquiryEBBasicInfoResp.PaxInfo();
        CIInquiryEBBasicInfoResp.EbOption ebOption1 = new CIInquiryEBBasicInfoResp.EbOption();
        CIInquiryEBBasicInfoResp.EbOption ebOption2 = new CIInquiryEBBasicInfoResp.EbOption();
        CIInquiryEBBasicInfoResp.EbOption ebOption3 = new CIInquiryEBBasicInfoResp.EbOption();
        CIInquiryEBBasicInfoResp.EbOption ebOption4 = new CIInquiryEBBasicInfoResp.EbOption();
        CIInquiryEBBasicInfoResp.EbOption ebOption5 = new CIInquiryEBBasicInfoResp.EbOption();
        CIInquiryEBBasicInfoResp.EbOption ebOption6 = new CIInquiryEBBasicInfoResp.EbOption();
        CIInquiryEBBasicInfoResp.EbOption ebOption7 = new CIInquiryEBBasicInfoResp.EbOption();
        CIInquiryEBBasicInfoResp.EbOption ebOption8 = new CIInquiryEBBasicInfoResp.EbOption();
        paxInfo1.first_name = "TEST1";
        paxInfo1.last_name = "CHEN";
        paxInfo1.is_add_excessBaggage = "Y";
        paxInfo1.pax_num = "1";

        paxInfo2.first_name = "TEST2";
        paxInfo2.last_name = "CHEN";
        paxInfo2.is_add_excessBaggage = "Y";
        paxInfo2.pax_num = "2";

        ebOption1.kgAmt = "0";
        ebOption2.kgAmt = "20";
        ebOption3.kgAmt = "40";
        ebOption4.kgAmt = "60";

        ebOption5.kgAmt = "0";
        ebOption6.kgAmt = "20";
        ebOption7.kgAmt = "40";
        ebOption8.kgAmt = "60";

        paxInfo1.eb_option  = new ArrayList<>();
        paxInfo1.eb_option.add(ebOption1);
        paxInfo1.eb_option.add(ebOption2);
        paxInfo1.eb_option.add(ebOption3);
        paxInfo1.eb_option.add(ebOption4);

        paxInfo2.eb_option  = new ArrayList<>();
        paxInfo2.eb_option.add(ebOption5);
        paxInfo2.eb_option.add(ebOption6);
        paxInfo2.eb_option.add(ebOption7);
        paxInfo2.eb_option.add(ebOption8);

        datas.Pax_Info  = new ArrayList<>();
        datas.Pax_Info.add(paxInfo1);
        datas.Pax_Info.add(paxInfo2);

        return datas;
    }

    private void sendReq(){
        CIInquiryEBBasicInfoPresenter presenter = CIInquiryEBBasicInfoPresenter.getInstance(this);
        CIInquiryEBBasicInfoReq       req       = new CIInquiryEBBasicInfoReq();
        req.pax_num = "1";
        req.pnr_id  = "SULWNW";
        req.pnr_seq = "1";
        presenter.getInfo(req);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CIInquiryEBBasicInfoPresenter.getInstance(null);
    }
}
