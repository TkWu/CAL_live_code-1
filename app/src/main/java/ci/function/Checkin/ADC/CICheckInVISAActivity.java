package ci.function.Checkin.ADC;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.chinaairlines.mobile30.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ci.function.Main.BaseActivity;
import ci.ui.TextField.CIDateOfExpiryTextFieldFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;
import ci.ui.view.ShadowBar.ShadowBarScrollview;
import ci.ui.view.StepHorizontalView;
import ci.ws.Models.entities.CICheckInPax_ItineraryInfoEntity;
import ci.ws.Models.entities.CICheckIn_Resp;
import ci.ws.Models.entities.CICheckInEdiaAPIS_ItineraryInfo_Resp;
import ci.ws.Models.entities.CICheckInEditAPIS_ItineraryInfo_Req;
import ci.ws.Models.entities.CICheckInEditAPIS_Req;
import ci.ws.Models.entities.CICheckInEditAPIS_Resp;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Presenter.CICheckInPresenter;
import ci.ws.Presenter.CIInquiryFlightStationPresenter;
import ci.ws.Presenter.Listener.CICheckInListener;

/**
 * CHECK-in 步驟 4-2
 * 輸入VISA 資料*/
public class CICheckInVISAActivity extends BaseActivity {

    public static final String BUNDLE_PARA_DEPARTURE        = "Departure_Station";
    public static final String BUNDLE_PARA_ARRIVE           = "Arrive_Station";
    public static final String BUNDLE_PARA_PAXINFO_RESP     = "EditAPIS_Pax_Info_Resp";


    private static final int TOTAL_STEP     = 5;
    private static final int CURREN_STEP    = 4;

    private NavigationBar       m_Navigationbar         = null;
    private StepHorizontalView  m_vStepHorizontalView   = null;
    private Button              m_btnConfirm            = null;
    private Button              m_btn_cancel            = null;
    private RelativeLayout      m_rlayoutRoot           = null;

    private ShadowBarScrollview m_shadowScrollView  = null;
    private ScrollView          m_ScrollView        = null;
    private LinearLayout        m_llayout_Content   = null;
    private ProgressBar         m_progressBar       = null;

    private ArrayList<viewHolder>   m_arPassengerHolderList  = null;
    //private ArrayList<String>       m_arPassengerList   = null;
    private String                  m_strDeparture      = "TPE";
    private String                  m_strArrive         = "FUK";

    private ArrayList<CICheckInEditAPIS_Resp> m_arFirstPaxInfoResp = null;

    class viewHolder {

        String Uci;

        CIInputVISALayout vVisaLayout;

        ArrayList<childViewHolder> arChildList;
    }

    class childViewHolder {

        String              strSegment_Number   = "";
        FrameLayout         flayout             = null;
        CIVISAInfoFragment  fragment            = null;
    }

        @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_check_in_input_visa;
    }

    @Override
    protected void initialLayoutComponent() {

        m_Navigationbar         = (NavigationBar) findViewById(R.id.toolbar);

        m_vStepHorizontalView   = (StepHorizontalView)findViewById(R.id.llayout_setp);

        m_rlayoutRoot       = (RelativeLayout)findViewById(R.id.rlayout_root);

        m_btn_cancel        = (Button)findViewById(R.id.btn_cancel);
        m_btnConfirm        = (Button)findViewById(R.id.btn_confirm);
        m_btnConfirm.setOnClickListener(m_onClicker);
        m_btn_cancel.setOnClickListener(m_onClicker);

        m_shadowScrollView  = (ShadowBarScrollview)findViewById(R.id.shadowlayout);
        m_progressBar       = (ProgressBar)findViewById(R.id.progress_bar);
        m_ScrollView        = m_shadowScrollView.getScrollView();
        m_llayout_Content   = m_shadowScrollView.getContentView();
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {

        vScaleDef.selfAdjustAllView(m_rlayoutRoot);

        m_btn_cancel.setBackgroundResource(R.drawable.bg_btn_blue_selector);
        m_btnConfirm.setBackgroundResource(R.drawable.bg_btn_pinkish_red_selector);

        //
        Intent intent = getIntent();
        if ( null != intent ){
            m_strDeparture  = intent.getStringExtra(BUNDLE_PARA_DEPARTURE);
            m_strArrive     = intent.getStringExtra(BUNDLE_PARA_ARRIVE);
            m_arFirstPaxInfoResp = (ArrayList<CICheckInEditAPIS_Resp>)intent.getSerializableExtra(BUNDLE_PARA_PAXINFO_RESP);
        }
    }

    @Override
    protected void setOnParameterAndListener() {

        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        m_vStepHorizontalView.initialView(TOTAL_STEP);
        m_vStepHorizontalView.setCurrentStep(CURREN_STEP);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

        onCreateVISAViews(fragmentManager);
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


    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return String.format(getString(R.string.check_in_have_flight), m_strDeparture, m_strArrive);
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

    private void onCreateVISAViews(FragmentManager fragmentManager) {

        if ( null == m_arPassengerHolderList ){
            m_arPassengerHolderList = new ArrayList<>();

            int iViewId = 0;
            int iPaxInfoSize = m_arFirstPaxInfoResp.size();
            for ( int iPass = 0; iPass < iPaxInfoSize; iPass++ ){

                boolean bNeedVISA = false;
                viewHolder vPassenger = new viewHolder();
                vPassenger.vVisaLayout = new CIInputVISALayout(this);
                vPassenger.arChildList = new ArrayList<>();

                CICheckInEditAPIS_Resp PaxInfo = m_arFirstPaxInfoResp.get(iPass);
                String strName = String.format("%s %s", PaxInfo.First_Name, PaxInfo.Last_Name);
                vPassenger.vVisaLayout.getTextView().setText(strName);
                vPassenger.Uci = PaxInfo.Uci;

                int iSize = PaxInfo.Itinerary_Info.size();
                //int iViewId = iPass + iSize;
                //vPassenger.vVisaLayout.setId(iViewId++);
                for ( int iIdx = 0; iIdx < iSize; iIdx++ ){

                    iViewId++;

                    if ( TextUtils.equals( "Y", PaxInfo.Itinerary_Info.get(iIdx).Is_Need_Visa) ){
                        bNeedVISA = true;
                    } else {
                        continue;
                    }

                    String strNational = "";
                    CIFlightStationEntity StationInfo = CIInquiryFlightStationPresenter.getInstance(null, CIInquiryFlightStationPresenter.ESource.TimeTable).getStationInfoByIATA(PaxInfo.Itinerary_Info.get(iIdx).Arrival_Station);
                    if ( null == StationInfo ){
                        strNational = PaxInfo.Itinerary_Info.get(iIdx).Arrival_Station;
                    } else {
                        strNational = StationInfo.country;
                    }

                    //2018-10-10 調整簽證資訊判斷邏輯，
                    String strVisaSection = "";
                    if ( TextUtils.isEmpty(PaxInfo.Itinerary_Info.get(iIdx).VISA_Wording) ){
                        strVisaSection = String.format("%s%d-%s", getString(R.string.check_in_visa_index_title), iIdx+1, strNational );
                    } else {
                        strVisaSection = String.format("%s%d-%s", PaxInfo.Itinerary_Info.get(iIdx).VISA_Wording, iIdx+1, strNational );
                    }

                    childViewHolder vholder = new childViewHolder();
                    vholder.strSegment_Number = PaxInfo.Itinerary_Info.get(iIdx).Segment_Number;
                    vholder.flayout = new FrameLayout(this);
                    vholder.flayout.setId(iViewId);

                    vholder.fragment = new CIVISAInfoFragment();
                    Bundle args = new Bundle();
                    args.putString(CIVISAInfoFragment.BUNDLE_PARA_TRIP_TEXT, strVisaSection);
                    args.putString(CIVISAInfoFragment.BUNDLE_PARA_DEPARTURE_DATE, PaxInfo.Itinerary_Info.get(iIdx).Departure_Date);
                    vholder.fragment.setArguments(args);

                    vPassenger.arChildList.add(vholder);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    vPassenger.vVisaLayout.getContentView().addView(vholder.flayout, params);

                }

                if ( bNeedVISA ){
                    m_arPassengerHolderList.add(vPassenger);
                    LinearLayout.LayoutParams paramsVISA = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    m_llayout_Content.addView( vPassenger.vVisaLayout , paramsVISA);
                }
            }

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            for ( viewHolder vholder : m_arPassengerHolderList ){
                for ( childViewHolder childHolder : vholder.arChildList ){
                    fragmentTransaction.replace( childHolder.flayout.getId(), childHolder.fragment, childHolder.fragment.toString() );
                }
            }

            fragmentTransaction.commitAllowingStateLoss();

            //滑到最上方
            m_ScrollView.smoothScrollTo(0, 0);
            m_progressBar.setVisibility(View.GONE);
        }

    }

    private ArrayList<CICheckInEditAPIS_Req> getPaxInfo(){

        ArrayList<CICheckInEditAPIS_Req> arEditAPIS_PaxInfoList = new ArrayList<>();

        int iPaxInfoSize = m_arFirstPaxInfoResp.size();
        for ( int iPax = 0; iPax < iPaxInfoSize; iPax++ ){

            CICheckInEditAPIS_Resp Pax_Resp = m_arFirstPaxInfoResp.get(iPax);
            CICheckInEditAPIS_Req newPax_Req = new CICheckInEditAPIS_Req();
            newPax_Req.Itinerary_Info = new ArrayList<>();

            newPax_Req.First_Name  = Pax_Resp.First_Name;
            newPax_Req.Last_Name   = Pax_Resp.Last_Name;
            newPax_Req.Uci         = Pax_Resp.Uci;
            newPax_Req.Pnr_Id      = Pax_Resp.Pnr_Id;

            int iSize = Pax_Resp.Itinerary_Info.size();
            for ( int iIdx = 0; iIdx < iSize; iIdx++ ){

                CICheckInEdiaAPIS_ItineraryInfo_Resp InfoResp = Pax_Resp.Itinerary_Info.get(iIdx);
                CICheckInEditAPIS_ItineraryInfo_Req newInfoReq = new CICheckInEditAPIS_ItineraryInfo_Req();

                newInfoReq.Arrival_Station     = InfoResp.Arrival_Station;
                newInfoReq.Departure_Station   = InfoResp.Departure_Station;
                newInfoReq.Did                 = InfoResp.Did;
                newInfoReq.Is_Do_Check_Document= InfoResp.Is_Do_Check_Document;
                newInfoReq.Nationality         = InfoResp.Nationality;
                newInfoReq.Seat_Number         = InfoResp.Seat_Number;
                newInfoReq.Segment_Number      = InfoResp.Segment_Number;
                newInfoReq.Apis                = InfoResp.Apis;

                if ( TextUtils.equals("Y", InfoResp.Is_Need_Visa) ){
                    //newInfoReq.Apis = InfoResp.Apis;
                    //VISA 固定A
                    newInfoReq.Apis.get(0).Document_Type = "V";

                    for ( viewHolder vpassenger : m_arPassengerHolderList ){
                        if ( TextUtils.equals( vpassenger.Uci, newPax_Req.Uci) ){

                            for ( childViewHolder child : vpassenger.arChildList ){
                                if ( TextUtils.equals(child.strSegment_Number, newInfoReq.Segment_Number) ){
                                    newInfoReq.Apis.get(0).Document_No = child.fragment.getDocumentNo();
                                    newInfoReq.Apis.get(0).Docuemnt_Eff_Date = child.fragment.getDocExpiryDate();
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
                newPax_Req.Itinerary_Info.add(newInfoReq);
            }
            arEditAPIS_PaxInfoList.add(newPax_Req);
        }

        return arEditAPIS_PaxInfoList;
    }

    public boolean isFillCompleteAndCorrect() {

        if( null == m_arPassengerHolderList ) {
            return false;
        }

        for( viewHolder vholder : m_arPassengerHolderList ) {
            for ( childViewHolder childHolder : vholder.arChildList ) {
                if (false == childHolder.fragment.isFillCompleteAndCorrect()) {
                    return false;
                }
            }
        }

        return true;
    }

    View.OnClickListener m_onClicker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int iViewId = v.getId();
            if ( iViewId == m_btnConfirm.getId() ){

                if ( isFillCompleteAndCorrect() ){

                    CICheckInPresenter.getInstance(m_onCheckInListener).EditAPISFromWS(getPaxInfo());

                } else {
                    String m_strErrorMsg = getString(R.string.please_fill_all_text_field_that_must_to_fill);
                    showDialog(getString(R.string.warning), m_strErrorMsg);
                }

            } else if ( iViewId == m_btn_cancel.getId() ){

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable( BUNDLE_PARA_PAXINFO_RESP, new ArrayList<>() );
                intent.putExtras(bundle);
                CICheckInVISAActivity.this.setResult(RESULT_OK, intent);
                finish();
            }
        }
    };

    CICheckInListener m_onCheckInListener = new CICheckInListener() {

        @Override
        public void onCheckInSuccess(String rt_code, String rt_msg, ArrayList<CICheckIn_Resp> arPaxInfo) {}

        @Override
        public void onCheckInError(String rt_code, String rt_msg) {}

        @Override
        public void onEditAPISSuccess(String rt_code, String rt_msg, String strNeedVISA, ArrayList<CICheckInEditAPIS_Resp> arPaxInfo) {

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable(BUNDLE_PARA_PAXINFO_RESP, arPaxInfo);
            intent.putExtras(bundle);
            CICheckInVISAActivity.this.setResult(RESULT_OK, intent);
            finish();
        }

        @Override
        public void onEditAPISError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
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

}
