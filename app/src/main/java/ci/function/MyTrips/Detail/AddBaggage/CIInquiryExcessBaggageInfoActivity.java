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
import ci.ws.Models.entities.CIInquiryExcessBaggageInfoReq;
import ci.ws.Models.entities.CIInquiryExcessBaggageInfoResp;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Presenter.CIInquiryExcessBaggageInfoPresenter;
import ci.ws.Presenter.Listener.CIInquiryExcessBaggageInfoListener;

/**
 * Created by kevincheng on 2017/9/15.
 * 加購行李
 */

public class CIInquiryExcessBaggageInfoActivity extends BaseActivity implements View.OnClickListener,
                                                                                CIInquiryExcessBaggageInfoListener {
    private NavigationBar                       m_Navigationbar = null;
    private String                              m_strTitle      = "";
    private CIInquiryExcessBaggageInfoFragment  m_fragment      = null;
    private CIInquiryExcessBaggageInfoReq       m_req           = null;
    private Button                              m_btnNext       = null;
    private CIInquiryExcessBaggageInfoPresenter m_presenter     = null;
    private CIInquiryExcessBaggageInfoResp      m_resp          = null;
    private CITripListResp_Itinerary            m_tripData      = null;

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
        return R.layout.activity_trip_detail_passenger_inquiry_excess_baggage_info;
    }

    @Override
    protected void initialLayoutComponent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(null != bundle) {
            m_req =      (CIInquiryExcessBaggageInfoReq)bundle.getSerializable(UiMessageDef.BUNDLE_INQUIRY_EXCESS_BAGGAGE_INFO_REQ);
            m_tripData = (CITripListResp_Itinerary)bundle.getSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST);
        }

        if(null != m_tripData) {
            m_strTitle = String.format(getString(R.string.my_trips_goto),
                                       m_tripData.Departure_Station,
                                       m_tripData.Arrival_Station);
        }


        if(null != m_req) {
            m_presenter = CIInquiryExcessBaggageInfoPresenter.getInstance(this);
            m_presenter.getInfo(m_req);
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
        listener.showAboutButton();
        findViewById(R.id.btn_next).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(UiMessageDef.BUNDLE_INQUIRY_EXCESS_BAGGAGE_INFO_REQ, m_req);
                bundle.putSerializable(UiMessageDef.BUNDLE_INQUIRY_EXCESS_BAGGAGE_INFO_RESP, m_resp);
                bundle.putSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST, m_tripData);
                intent.putExtras(bundle);
                intent.setClass(this, CICCPageActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
                break;
        }
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

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
    public void onSuccess(String rt_code, String rt_msg, CIInquiryExcessBaggageInfoResp data) {
        if(null != data && null != data.Pax_info && data.Pax_info.size() > 0) {
            m_resp = data;
            paserRespName(data.Pax_info);
            m_fragment = CIInquiryExcessBaggageInfoFragment.newInstance(data);
            m_fragment.setListener(new CIInquiryExcessBaggageInfoFragment.ICallBack() {
                @Override
                public void onClickTermsAndConditionsBtn(boolean bEnable) {
                    setButtonEnable(bEnable);
                }
            });
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, m_fragment).commitAllowingStateLoss();
        }
    }

    private void paserRespName(ArrayList<CIInquiryExcessBaggageInfoResp.PaxInfo> paxInfos){
        for(CIInquiryExcessBaggageInfoResp.PaxInfo data : paxInfos){
            for(CIInquiryExcessBaggageInfoReq.EB eb : m_req.eb) {
                if(data.pax_num.equals(eb.pax_num)) {
                    data.first_name = eb.first_name;
                    data.last_name = eb.last_name;
                    break;
                }
            }
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
    public void onPause() {
        super.onPause();
        if(null != m_presenter) {
            m_presenter.interrupt();
        }
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

    private CIInquiryExcessBaggageInfoResp getTestData(){
        // TODO: 2017/9/18 暫時測試用
        CIInquiryExcessBaggageInfoResp datas = new CIInquiryExcessBaggageInfoResp();
        datas.pnr_id = "SULWNW";
        datas.pnr_seq = "1";
        datas.ttl_amount = "4200";
        datas.ttl_currency = "TWD";
        datas.Pax_info = new ArrayList<>();
        
        CIInquiryExcessBaggageInfoResp.PaxInfo paxInfo1 = new CIInquiryExcessBaggageInfoResp.PaxInfo();
        paxInfo1.pax_num = "1";
        paxInfo1.ebAmount = "1800";
        paxInfo1.ebCurrency = "TWD";
        paxInfo1.ssrAmount = "30";
        paxInfo1.ssrType = "EXWG";
        paxInfo1.first_name = "ChihHua";
        paxInfo1.last_name = "Huang";

        CIInquiryExcessBaggageInfoResp.PaxInfo paxInfo2 = new CIInquiryExcessBaggageInfoResp.PaxInfo();
        paxInfo2.pax_num = "2";
        paxInfo2.ebAmount = "600";
        paxInfo2.ebCurrency = "TWD";
        paxInfo2.ssrAmount = "10";
        paxInfo2.ssrType = "EXWG";
        paxInfo2.first_name = "ChihHua";
        paxInfo2.last_name = "Huang";

        CIInquiryExcessBaggageInfoResp.PaxInfo paxInfo3 = new CIInquiryExcessBaggageInfoResp.PaxInfo();
        paxInfo3.pax_num = "2";
        paxInfo3.ebAmount = "600";
        paxInfo3.ebCurrency = "TWD";
        paxInfo3.ssrAmount = "10";
        paxInfo3.ssrType = "EXWG";
        paxInfo3.first_name = "ChihHua";
        paxInfo3.last_name = "Huang";

        CIInquiryExcessBaggageInfoResp.PaxInfo paxInfo4 = new CIInquiryExcessBaggageInfoResp.PaxInfo();
        paxInfo4.pax_num = "2";
        paxInfo4.ebAmount = "600";
        paxInfo4.ebCurrency = "TWD";
        paxInfo4.ssrAmount = "10";
        paxInfo4.ssrType = "EXWG";
        paxInfo4.first_name = "ChihHua";
        paxInfo4.last_name = "Huang";

        CIInquiryExcessBaggageInfoResp.PaxInfo paxInfo5 = new CIInquiryExcessBaggageInfoResp.PaxInfo();
        paxInfo5.pax_num = "2";
        paxInfo5.ebAmount = "600";
        paxInfo5.ebCurrency = "TWD";
        paxInfo5.ssrAmount = "10";
        paxInfo5.ssrType = "EXWG";
        paxInfo5.first_name = "ChihHua";
        paxInfo5.last_name = "Huang";
        
        datas.Pax_info.add(paxInfo1);
        datas.Pax_info.add(paxInfo2);
        datas.Pax_info.add(paxInfo3);
        datas.Pax_info.add(paxInfo4);
        datas.Pax_info.add(paxInfo5);

        return datas;
    }

    private void sendReq(){
        CIInquiryExcessBaggageInfoPresenter presenter = CIInquiryExcessBaggageInfoPresenter.getInstance(this);
        CIInquiryExcessBaggageInfoReq       req       = new CIInquiryExcessBaggageInfoReq();
        
        
        CIInquiryExcessBaggageInfoReq.EB eb1 = new CIInquiryExcessBaggageInfoReq.EB();
        eb1.ssrType = "EXWG";
        eb1.pax_num = "1";
        eb1.ssrAmount = "30";

        CIInquiryExcessBaggageInfoReq.EB eb2 = new CIInquiryExcessBaggageInfoReq.EB();
        eb2.ssrType = "EXWG";
        eb2.pax_num = "2";
        eb2.ssrAmount = "10";
        
        req.eb = new ArrayList<>();
        
        req.pnr_id  = "SULWNW";
        req.pnr_seq = "1";
        presenter.getInfo(req);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CIInquiryExcessBaggageInfoPresenter.getInstance(null);
    }
}
