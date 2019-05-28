package ci.function.MyTrips.Detail.AddBaggage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.view.NavigationBar;
import ci.ui.view.ShadowBar.ShadowBarScrollview;
import ci.ws.Models.entities.CICCPageResp;
import ci.ws.Models.entities.CIEBPaymentReq;
import ci.ws.Models.entities.CIEBPaymentResp;
import ci.ws.Models.entities.CIInquiryExcessBaggageInfoReq;
import ci.ws.Models.entities.CIInquiryExcessBaggageInfoResp;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Presenter.CICCPagePresenter;
import ci.ws.Presenter.CIEBPaymentPresenter;
import ci.ws.Presenter.Listener.CICCPageListener;
import ci.ws.Presenter.Listener.CIEBPaymentListener;

/**
 * Created by kevincheng on 2017/9/15.
 * 加購信用卡付款頁面
 */

public class CICCPageActivity extends BaseActivity implements View.OnClickListener{
    private NavigationBar                  m_Navigationbar    = null;
    private CICCPageFragment               m_fragment         = null;
    private CIInquiryExcessBaggageInfoReq  m_iebReq           = null;
    private CIInquiryExcessBaggageInfoResp m_iebResp          = null;
    private CICCPageResp                   m_ccpResp          = null;
    private Button                         m_btnNext          = null;
    private CICCPagePresenter              m_presenter        = null;
    private CITripListResp_Itinerary       m_tripData         = null;
    private ShadowBarScrollview            m_shadowScrollView = null;
    private LinearLayout                   m_llContent        = null;
    private int                            m_iFrameLayoutId   = 0;

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return getString(R.string.trip_detail_passenger_bag_pay_title);
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
        return R.layout.activity_trip_detail_passenger_ccpage;
    }

    @Override
    protected void initialLayoutComponent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(null != bundle) {
            m_iebReq =      (CIInquiryExcessBaggageInfoReq)bundle.getSerializable(UiMessageDef.BUNDLE_INQUIRY_EXCESS_BAGGAGE_INFO_REQ);
            m_iebResp =     (CIInquiryExcessBaggageInfoResp)bundle.getSerializable(UiMessageDef.BUNDLE_INQUIRY_EXCESS_BAGGAGE_INFO_RESP);
            m_tripData = (CITripListResp_Itinerary)bundle.getSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST);
        }


        if(null != m_iebReq) {
            m_presenter = CICCPagePresenter.getInstance(m_ciccPageListener);
            m_presenter.getInfo();
        }
        m_btnNext = (Button) findViewById(R.id.btn_next);
        m_shadowScrollView  = (ShadowBarScrollview) findViewById(R.id.shadowlayout);
        m_llContent         = m_shadowScrollView.getContentView();
        ScrollView.LayoutParams svLayoutParams = new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(svLayoutParams);

        RelativeLayout.LayoutParams rLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        FrameLayout frameLayout = new FrameLayout(this);
        m_iFrameLayoutId = View.generateViewId();
        frameLayout.setLayoutParams(rLayoutParams);
        frameLayout.setId(m_iFrameLayoutId);

        relativeLayout.addView(frameLayout);
        m_llContent.addView(relativeLayout);
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
        findViewById(R.id.btn_next).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                sendEBPaymentsRequest();
                break;
        }
    }

    private void sendEBPaymentsRequest(){

        if(null != m_ccpResp) {
            CIEBPaymentReq req = new CIEBPaymentReq();
            req.token = m_ccpResp.Token;
            req.eb = m_iebReq.eb;
            req.pnr_id = m_iebReq.pnr_id;
            req.pnr_seq = m_iebReq.pnr_seq;
            CIEBPaymentPresenter paymentPresenter = CIEBPaymentPresenter.getInstance(m_ciebPaymentListener);
            paymentPresenter.getInfo(req);
        }
    }

    private void changeToEBPaymentPage(CIEBPaymentResp data,
                                       CIPaymentsResultActivity.EMode mode){
        changeToEBPaymentPage(data, mode, "");
    }

    private void changeToEBPaymentPage(CIEBPaymentResp data,
                                       CIPaymentsResultActivity.EMode mode,
                                       String msg){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(UiMessageDef.BUNDLE_EBPAYMENTS_RESP, data);
        bundle.putSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST, m_tripData);
        bundle.putString(UiMessageDef.BUNDLE_PAYMENTS_RESULT_MODE, mode.name());
        bundle.putString(UiMessageDef.BUNDLE_FRAGMENT_DATA, msg);
        intent.putExtras(bundle);
        intent.setClass(this, CIPaymentsResultActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    private CIEBPaymentListener m_ciebPaymentListener = new CIEBPaymentListener() {
        @Override
        public void onSuccess(String rt_code, String rt_msg, CIEBPaymentResp data) {
            if(null != data) {
                changeToEBPaymentPage(data , CIPaymentsResultActivity.EMode.Success);
            }
        }

        @Override
        public void onError(String rt_code, String rt_msg) {
            changeToEBPaymentPage(null, CIPaymentsResultActivity.EMode.Fail, rt_msg);
        }

        @Override
        public void showProgress() {
            showProgressDialog();
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
        }
    };

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
    }


    CICCPageListener m_ciccPageListener = new CICCPageListener(){
        @Override
        public void onSuccess(String rt_code, String rt_msg, CICCPageResp data) {
            if(null != data ) {
                m_ccpResp = data;
                setButtonEnable(true);
                m_fragment = CICCPageFragment.newInstance(m_ccpResp, m_iebResp);
                getSupportFragmentManager().beginTransaction().replace(m_iFrameLayoutId, m_fragment).commitAllowingStateLoss();
            }
        }

        @Override
        public void onError(String rt_code, String rt_msg) {
            showDialogAndBackHomeOnClick(rt_msg);
        }

        @Override
        public void showProgress() {
            showProgressDialog();
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
        }
    };

    private void showDialogAndBackHomeOnClick(String rt_msg){
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

    private void setButtonEnable(boolean bEnable){
        m_btnNext.setEnabled(bEnable);
        if(bEnable) {
            m_btnNext.setAlpha(1.0f);
        } else {
            m_btnNext.setAlpha(0.5f);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(null != m_presenter) {
            m_presenter.interrupt();
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

    private CICCPageResp getCcpResp(){
        CICCPageResp req = new CICCPageResp();
        req.Token = "123456";
        req.url = "https://www.google.com.tw/";
        return req;
    }


    private CIEBPaymentResp getPaySuccessData(){
        // TODO: 2017/9/18 暫時測試用
        CIEBPaymentResp datas = new CIEBPaymentResp();
        datas.pnr_id = "SULWNW";
        datas.pnr_seq = "1";
        datas.ttl_amount = "4200";
        datas.ttl_currency = "TWD";
        datas.purchase_date = "2017-10-01";
        datas.Pax_Info = new ArrayList<>();

        CIEBPaymentResp.FlightInfo flightInfo1 = new CIEBPaymentResp.FlightInfo();
        flightInfo1.flight_date = "2017-10-10";
        flightInfo1.flight_num = "CI001";

        CIEBPaymentResp.FlightInfo flightInfo2 = new CIEBPaymentResp.FlightInfo();
        flightInfo2.flight_date = "2017-10-20";
        flightInfo2.flight_num = "CI002";

        CIEBPaymentResp.PaxInfo paxInfo1 = new CIEBPaymentResp.PaxInfo();
        paxInfo1.pax_num = "1";
        paxInfo1.ebAmount = "1800";
        paxInfo1.ebCurrency = "TWD";
        paxInfo1.ssrAmount = "30";
        paxInfo1.ssrType = "EXWG";
        paxInfo1.first_name = "ChihHua";
        paxInfo1.last_name = "Huang";
        paxInfo1.emd_num = "111-111111111";
        paxInfo1.ticket_num = "111111111111";
        paxInfo1.flight_info = new ArrayList<>();
        paxInfo1.flight_info.add(flightInfo1);
        paxInfo1.flight_info.add(flightInfo2);

        CIEBPaymentResp.PaxInfo paxInfo2 = new CIEBPaymentResp.PaxInfo();
        paxInfo2.pax_num = "2";
        paxInfo2.ebAmount = "600";
        paxInfo2.ebCurrency = "TWD";
        paxInfo2.ssrAmount = "10";
        paxInfo2.ssrType = "EXWG";
        paxInfo2.first_name = "ChihHua";
        paxInfo2.last_name = "Huang";
        paxInfo2.emd_num = "222-222222222";
        paxInfo2.ticket_num = "222222222222";
        paxInfo2.flight_info = new ArrayList<>();
        paxInfo2.flight_info.add(flightInfo1);

        CIEBPaymentResp.PaxInfo paxInfo3 = new CIEBPaymentResp.PaxInfo();
        paxInfo3.pax_num = "2";
        paxInfo3.ebAmount = "600";
        paxInfo3.ebCurrency = "TWD";
        paxInfo3.ssrAmount = "10";
        paxInfo3.ssrType = "EXWG";
        paxInfo3.first_name = "ChihHua";
        paxInfo3.last_name = "Huang";
        paxInfo3.emd_num = "333-333333333";
        paxInfo3.ticket_num = "333333333333";
        paxInfo3.flight_info = new ArrayList<>();
        paxInfo3.flight_info.add(flightInfo1);
        paxInfo3.flight_info.add(flightInfo2);

        CIEBPaymentResp.PaxInfo paxInfo4 = new CIEBPaymentResp.PaxInfo();
        paxInfo4.pax_num = "2";
        paxInfo4.ebAmount = "600";
        paxInfo4.ebCurrency = "TWD";
        paxInfo4.ssrAmount = "10";
        paxInfo4.ssrType = "EXWG";
        paxInfo4.first_name = "ChihHua";
        paxInfo4.last_name = "Huang";
        paxInfo4.emd_num = "444-444444444";
        paxInfo4.ticket_num = "444444444444";
        paxInfo4.flight_info = new ArrayList<>();
        paxInfo4.flight_info.add(flightInfo1);
        paxInfo4.flight_info.add(flightInfo2);
        paxInfo4.flight_info.add(flightInfo2);

        CIEBPaymentResp.PaxInfo paxInfo5 = new CIEBPaymentResp.PaxInfo();
        paxInfo5.pax_num = "2";
        paxInfo5.ebAmount = "600";
        paxInfo5.ebCurrency = "TWD";
        paxInfo5.ssrAmount = "10";
        paxInfo5.ssrType = "EXWG";
        paxInfo5.first_name = "ChihHua";
        paxInfo5.last_name = "Huang";
        paxInfo5.emd_num = "555-555555";
        paxInfo5.ticket_num = "555555555";
        paxInfo5.flight_info = new ArrayList<>();
        paxInfo5.flight_info.add(flightInfo1);
        paxInfo5.flight_info.add(flightInfo2);
        paxInfo5.flight_info.add(flightInfo2);

        datas.Pax_Info.add(paxInfo1);
        datas.Pax_Info.add(paxInfo2);
        datas.Pax_Info.add(paxInfo3);
        datas.Pax_Info.add(paxInfo4);
        datas.Pax_Info.add(paxInfo5);

        return datas;
    }

    private void sendCCPageReq(){
        CICCPagePresenter presenter = CICCPagePresenter.getInstance(m_ciccPageListener);
        presenter.getInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //釋放引用，避免memory leak
        CICCPagePresenter.getInstance(null);
        CIEBPaymentPresenter.getInstance(null);
    }
}
