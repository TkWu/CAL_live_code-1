package ci.function.Checkin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import ci.function.Checkin.ADC.CICheckInVISAActivity;
import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewIdDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.CIPNRStatusManager;
import ci.ui.view.NavigationBar;
import ci.ui.view.StepHorizontalView;
import ci.ws.Models.entities.CIApisQryRespEntity;
import ci.ws.Models.entities.CICheckInPax_ItineraryInfoEntity;
import ci.ws.Models.entities.CICheckIn_ItineraryInfo_Req;
import ci.ws.Models.entities.CICheckIn_ItineraryInfo_Resp;
import ci.ws.Models.entities.CICheckIn_Req;
import ci.ws.Models.entities.CICheckIn_Resp;
import ci.ws.Models.entities.CICheckInApisEntity;
import ci.ws.Models.entities.CICheckInDocaEntity;
import ci.ws.Models.entities.CICheckInEditAPIS_ItineraryInfo_Req;
import ci.ws.Models.entities.CICheckInEditAPIS_Req;
import ci.ws.Models.entities.CICheckInEditAPIS_Resp;
import ci.ws.Models.entities.CICheckInPax_InfoEntity;
import ci.ws.Models.entities.CICheckInAllPaxResp;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Models.entities.CIMarkBPAsPrintedEntity;
import ci.ws.Models.entities.CIMarkBPAsPrinted_Pax_Info;
import ci.ws.Models.entities.CIMarkBP_Pax_ItineraryInfo;
import ci.ws.Presenter.CIAPISPresenter;
import ci.ws.Presenter.CICheckInPresenter;
import ci.ws.Presenter.CIInquiryCheckInPresenter;
import ci.ws.Presenter.CIInquiryFlightStationPresenter;
import ci.ws.Presenter.CIMarkBPAsPrintedPresenter;
import ci.ws.Presenter.Listener.CICheckInListener;
import ci.ws.Presenter.Listener.CIInquiryApisListListener;
import ci.ws.Presenter.Listener.CIInquiryCheckInListener;
import ci.ws.Presenter.Listener.CIInquiryFlightStatusStationListener;
import ci.ws.Presenter.Listener.CIMarkBPAsPrintedListener;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.CIWSResultCode;

import static ci.function.Checkin.CISelectPassengersFragment.SelectPassengersType;

/**
 * Created by Ryan on 16/3/10.
 * 由於畫面Fragment切換需有翻牌動畫, 但V4的Fragment 不支援 ObjectAnimator,
 * 故使用View的方式翻牌
 */
public class CICheckInActivity extends BaseActivity {

    public static final String  BUNDLE_ENTRY                    = "Check_in_Entry";
    public static final String  BUNDLE_PARA_ENTRY_TRIP_DETAIL   = "from_Trip_detail";
    public static final String  BUNDLE_PARA_ENTRY_HOME_PAGE     = "from_home_Page";

    private static final String TAG = CICheckInActivity.class.getCanonicalName();

    private static final int STEP_SELECT_FLIGHT     = 1;
    private static final int STEP_SELECT_PASSENGERS = 2;
    private static final int STEP_PROHIBIT_PRODUCT  = 3;
    private static final int STEP_INPUT_APIS        = 4;
    private static final int STEP_COMPLETED         = 5;

    private static final int FIRST_STEP = STEP_SELECT_FLIGHT;
    private static final int TOTAL_STEP = STEP_COMPLETED;

    public NavigationBar        m_Navigationbar         = null;
    private StepHorizontalView  m_vStepHorizontalView   = null;
    private FrameLayout         m_flayout_content1      = null;

    private TextView            m_tvTitle               = null;
    private Button              m_btnConfirm            = null;

    private int                 m_iCurrStep             = FIRST_STEP;
    private String              m_strDeparture          = "TPE";
    private String              m_strArrive             = "FUK";

    private String              m_strStartFrom          = BUNDLE_PARA_ENTRY_HOME_PAGE;

    private CISelectFlightFragment          m_SelectFlightFragment          = null;
    private CISelectPassengersFragment      m_SelectPassengersFragment      = null;
    private CIProhibitProductClaimFragment  m_ProhibitProductClaimFragment  = null;
    private CIInputAPISFragment             m_InputAPISFragment             = null;
    private CICheckInCompletesFragment      m_CheckInCompletesFragment      = null;

    private CICheckInPax_InfoEntity m_arSelectedFlights     = null;
    private CICheckInAllPaxResp m_arPassenger           = null;
    private ArrayList<CICheckIn_Resp> m_arCheckInPaxInfoResp =null;
    private String                  m_strErrorMsg           = null;
    private boolean                 m_bArrivalUSA           = false;
    private int                     m_iViewId               = 0;

    /**2018-08-22 調整航線邏輯*/
    private CIAPISDef.CIRouteType   m_enRouteType           = CIAPISDef.CIRouteType.normal;

    private ArrayList<CICheckInEditAPIS_Resp> m_arEditAPISPaxInfoResp = null;

    private ArrayList<CICheckIn_Req> m_arInputApisPaxInfo = null;

    //2016-07-15 ryan 調整Check-in Presenter 使用方式
    private CIInquiryCheckInPresenter m_InquiryCheckInPresenter = null;

    private CIAPISPresenter apis_presenter = null;
    private CIApisQryRespEntity saved_apis_resonse = null;

    private CIInquiryFlightStationPresenter m_inquiryFlightStationPresenter = null;

    //2016-10-28 ryan , 新增記錄Mark的航段
    private CIMarkBPAsPrintedEntity m_markBPAsPrintedEntity = null;
    //2016-10-28 ryan , 記錄可以Mark的航段資訊
    private LinkedHashMap<String, CICheckInPax_ItineraryInfoEntity> m_mapMarkList = new LinkedHashMap<>();

    private CIInquiryFlightStatusStationListener flightStatusStationListener = new CIInquiryFlightStatusStationListener() {
        @Override
        public void onAllStationSuccess(String rt_code, String rt_msg, CIInquiryFlightStationPresenter presenter) {
            m_InquiryCheckInPresenter.InquiryCheckInAllPaxByPNRFromWS(m_arSelectedFlights.Pnr_Id, m_SelectFlightFragment.getSelectedFlightSegmentNoList());
        }
        @Override
        public void onStationError(String rt_code, String rt_msg) {
            m_InquiryCheckInPresenter.InquiryCheckInAllPaxByPNRFromWS(m_arSelectedFlights.Pnr_Id, m_SelectFlightFragment.getSelectedFlightSegmentNoList());

        }
        @Override
        public void onODStationSuccess(String rt_code, String rt_msg, CIInquiryFlightStationPresenter presenter) {}
        @Override
        public void showProgress() {
            showProgressDialog();
        }
        @Override
        public void hideProgress() {}
    };

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            if( STEP_SELECT_FLIGHT == m_iCurrStep ) {
                return getString(R.string.check_in);
            } else {
                return String.format(getString(R.string.check_in_have_flight), m_strDeparture, m_strArrive);
            }
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

    private CIInquiryCheckInListener m_InquiryCheckInListener = new CIInquiryCheckInListener() {
        @Override
        public void onInquiryCheckInSuccess(String rt_code, String rt_msg, CICheckInAllPaxResp CheckInList) {}
        @Override
        public void onInquiryCheckInError(String rt_code, String rt_msg) {}

        @Override
        public void onInquiryCheckInAllPaxSuccess(String rt_code, String rt_msg, CICheckInAllPaxResp CheckInList) {

            filterPaxInfo(CheckInList);

            m_arPassenger = CheckInList;
            //
            setFlightInfo(m_arSelectedFlights);
            m_iCurrStep = m_vStepHorizontalView.setNextSteps();
            onNextStep(getSupportFragmentManager(), m_iCurrStep);
        }

        @Override
        public void onInquiryCheckInAllPaxError(String rt_code, String rt_msg) {
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

    private CICheckInListener m_CheckInListener = new CICheckInListener() {
        @Override
        public void onCheckInSuccess(String rt_code, String rt_msg, ArrayList<CICheckIn_Resp> arPaxInfo) {

            StringBuffer sbErrorMsg = new StringBuffer();

            if( null != m_arCheckInPaxInfoResp ) {
                m_arCheckInPaxInfoResp.clear();
            } else {
                m_arCheckInPaxInfoResp = new ArrayList<>();
            }

            //檢查是否CheckIn成功
            if( null != arPaxInfo ) {
                for( CICheckIn_Resp paxInfo_resp : arPaxInfo ) {

                    for(Iterator<CICheckIn_ItineraryInfo_Resp> iterator = paxInfo_resp.Itinerary_Info.iterator(); iterator.hasNext(); ) {
                        CICheckIn_ItineraryInfo_Resp entity = iterator.next();

                        //刪除 rt_code != "000"的 Itinerary_Info
                        if( !CIWSResultCode.IsSuccess(entity.rt_code) ) {
                            if( 0 < sbErrorMsg.length() ) {
                                sbErrorMsg.append("\n\n");
                            }

                            final String strFullName = paxInfo_resp.First_Name+" " + paxInfo_resp.Last_Name;
                            sbErrorMsg.append(strFullName).append("\n").append(entity.rt_msg);

                            iterator.remove();
                        }
                    }

                    if( null != paxInfo_resp.Itinerary_Info && 0 < paxInfo_resp.Itinerary_Info.size() ) {
                        m_arCheckInPaxInfoResp.add(paxInfo_resp);
                    }
                }
            }


            if( 0 < m_arCheckInPaxInfoResp.size() ) {

                //把CheckIn Req的部分資料帶給 CheckIn Resp
                setCheckInReqData(m_arCheckInPaxInfoResp);

                //發送 MarkBPAsPrinted(看過登機證註記)
                sendMarkBPAsPrinedRequest(sbErrorMsg.toString());

            } else {
                hideProgressDialog();
                showDialog(getString(R.string.warning),
                        sbErrorMsg.toString(),
                        getString(R.string.confirm));
            }
        }

        @Override
        public void onCheckInError(String rt_code, String rt_msg) {
            hideProgressDialog();

            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void onEditAPISSuccess(String rt_code, String rt_msg, String strNeedVISA, ArrayList<CICheckInEditAPIS_Resp> arPaxInfo) {

            if ( null == m_arEditAPISPaxInfoResp ){
                m_arEditAPISPaxInfoResp = new ArrayList<>();
            } else {
                m_arEditAPISPaxInfoResp.clear();
            }

            m_arEditAPISPaxInfoResp = arPaxInfo;

            if ( TextUtils.equals("Y", strNeedVISA) ){

                hideProgressDialog();

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString( CICheckInVISAActivity.BUNDLE_PARA_DEPARTURE, m_strDeparture);
                bundle.putString( CICheckInVISAActivity.BUNDLE_PARA_ARRIVE, m_strArrive);

                bundle.putSerializable( CICheckInVISAActivity.BUNDLE_PARA_PAXINFO_RESP, arPaxInfo);
                intent.putExtras(bundle);
                intent.setClass( CICheckInActivity.this, CICheckInVISAActivity.class );
                startActivityForResult(intent, UiMessageDef.REQUEST_CODE_CHECK_IN_EDIT_APIS_VISA);

            } else {
                //不需要簽證，則可以直接執行 Check-in
                onEditAPIS_Success(arPaxInfo);
            }
        }

        @Override
        public void onEditAPISError(String rt_code, String rt_msg) {
            hideProgressDialog();

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
            //2018-09-10 多次開關 ProgressDialog 會出現異常，當多支ws 需要重複使用 ProgressDialog，改由流程控制是否顯示與關閉
            //hideProgressDialog();
        }
    };

    private CIProhibitProductClaimFragment.onCIProhibitProductClaimListener onProhibitProductClaimListener = new CIProhibitProductClaimFragment.onCIProhibitProductClaimListener() {
        @Override
        public void onCheckBoxClick(boolean bClick) {
            if(bClick) {
                m_btnConfirm.setAlpha(1.0f);
            } else {
                m_btnConfirm.setAlpha(0.5f);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //取得進入點 by Kevin 2016/06/28
        m_iViewId = getIntent().getIntExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, 0);

        String strFrom = getIntent().getStringExtra(BUNDLE_ENTRY);
        if (null != strFrom) {
            m_strStartFrom = strFrom;
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_check_in;
    }

    @Override
    protected void initialLayoutComponent() {

        m_Navigationbar         = (NavigationBar) findViewById(R.id.toolbar);
        m_vStepHorizontalView   = (StepHorizontalView)findViewById(R.id.llayout_setp);
        m_tvTitle               = (TextView)findViewById(R.id.tv_Title);
        m_flayout_content1      = (FrameLayout)findViewById(R.id.flayout_content1);
        m_btnConfirm            = (Button)findViewById(R.id.imgbtn_Confirm);
        m_btnConfirm.setOnClickListener(m_onClick);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {

        m_vStepHorizontalView.getLayoutParams().height  = vScaleDef.getLayoutHeight(39);
        m_btnConfirm.getLayoutParams().height           = vScaleDef.getLayoutHeight(40);
        int iWidth  = vScaleDef.getLayoutWidth(20);
        int iHeight = vScaleDef.getLayoutHeight(30);
        ((RelativeLayout.LayoutParams)m_btnConfirm.getLayoutParams()).leftMargin    = iWidth;
        ((RelativeLayout.LayoutParams)m_btnConfirm.getLayoutParams()).rightMargin   = iWidth;
        ((RelativeLayout.LayoutParams)m_btnConfirm.getLayoutParams()).topMargin     = iHeight;
        ((RelativeLayout.LayoutParams)m_btnConfirm.getLayoutParams()).bottomMargin  = iHeight;

        ((RelativeLayout.LayoutParams)m_tvTitle.getLayoutParams()).topMargin = vScaleDef.getLayoutHeight(24);
        ((RelativeLayout.LayoutParams)m_tvTitle.getLayoutParams()).bottomMargin = vScaleDef.getLayoutHeight(20);
        vScaleDef.setTextSize(20, m_tvTitle);

    }

    @Override
    protected void setOnParameterAndListener() {

        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        m_vStepHorizontalView.initialView(TOTAL_STEP);

//        Intent intent = getIntent();
//        if ( null != intent ){
//            m_strStartFrom = intent.getStringExtra(BUNDLE_ENTRY);
//        }

        //2016-07-15 ryan 調整Check-in Presenter 使用方式
        m_InquiryCheckInPresenter = new CIInquiryCheckInPresenter(m_InquiryCheckInListener);

        m_inquiryFlightStationPresenter = CIInquiryFlightStationPresenter.getInstance(flightStatusStationListener,CIInquiryFlightStationPresenter.ESource.TimeTable);

        apis_presenter = new CIAPISPresenter();
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

        onNextStep(fragmentManager, m_iCurrStep);
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

    public void onNextStep( FragmentManager fragmentManager, int iStep ){

        //FlipAnimation flipAnimation = null;
        SLog.d("onNextStep(FragmentManager) + iStep: "+iStep);
        boolean bFirst = true;

        Fragment    fragment = null;
        String      strTag = "";

        switch(iStep){
            case STEP_SELECT_FLIGHT :
                {
                    m_tvTitle.setText(R.string.select_flight);
                    m_SelectFlightFragment = new CISelectFlightFragment();
                    ArrayList<CICheckInPax_InfoEntity> arCheckInList =
                            (ArrayList<CICheckInPax_InfoEntity>) getIntent().getSerializableExtra(UiMessageDef.BUNDLE_CHECK_IN_FLIGHT_LIST);
                    m_SelectFlightFragment.setCheckInResp(arCheckInList);
                    fragment = m_SelectFlightFragment;
                    strTag = m_SelectFlightFragment.toString();
                    bFirst = false;
                }
                break;
            case STEP_SELECT_PASSENGERS :
                {
                    //紀錄選擇的航班
                    if( null != m_SelectFlightFragment ) {
                        m_arSelectedFlights = m_SelectFlightFragment.getSelectedFlightList();

                        if( null != m_arSelectedFlights && 0 < m_arSelectedFlights.m_Itinerary_InfoList.size()) {
                            m_strDeparture = m_arSelectedFlights.m_Itinerary_InfoList.get(0).Departure_Station;
                            m_strArrive = m_arSelectedFlights.m_Itinerary_InfoList.get( (m_arSelectedFlights.m_Itinerary_InfoList.size()-1) ).Arrival_Station;
                        }
                    }

                    m_tvTitle.setText(R.string.select_passenager);
//                    m_SelectPassengersFragment = CISelectPassengersFragment.newInstance(SelectPassengersType.ADD_PASSENGER);
                    m_SelectPassengersFragment = CISelectPassengersFragment.newInstance(SelectPassengersType.ADD_PASSENGER, m_arSelectedFlights.Pnr_Id, m_SelectFlightFragment.getSelectedFlightSegmentNoList(),m_arSelectedFlights.Uci );
                    m_SelectPassengersFragment.addPassengerList(m_arPassenger);

                    fragment= m_SelectPassengersFragment;
                    strTag  = m_SelectPassengersFragment.toString();
                    bFirst = true;
                }
                break;
            case STEP_PROHIBIT_PRODUCT :
                {

                    m_tvTitle.setText(R.string.check_in_dabgerous_goods_info);
                    //
                    m_ProhibitProductClaimFragment = new CIProhibitProductClaimFragment();
                    fragment= m_ProhibitProductClaimFragment;
                    strTag  = m_ProhibitProductClaimFragment.toString();
                    m_ProhibitProductClaimFragment.onSetListener(onProhibitProductClaimListener);
                    bFirst = true;
                }
                break;
            case STEP_INPUT_APIS :
                {
                    m_tvTitle.setText(R.string.input_apis);
                    //
                    m_InputAPISFragment = new CIInputAPISFragment();
                    m_InputAPISFragment.setPassengerInfoList(m_arPassenger, m_enRouteType);
                    m_InputAPISFragment.setSavedAPIS(saved_apis_resonse);
                    //m_InputAPISFragment.setPassengerInfoList(m_arPassenger,m_bArrivalUSA);
                    fragment = m_InputAPISFragment;
                    strTag  = m_InputAPISFragment.toString();
                    bFirst = true;
                }
                break;
            case STEP_COMPLETED :
                {

                    //設定 CheckIn Complete 按鈕文字
                    m_btnConfirm.setText(R.string.done);
//                    if ( m_strStartFrom.equals(BUNDLE_PARA_ENTRY_TRIP_DETAIL) ){
//                        m_btnConfirm.setText(R.string.back_to_trip_detail);
//                    } else {
//                        m_btnConfirm.setText(R.string.back_to_home);
//                    }

                    m_tvTitle.setText(R.string.check_in_completed);
                    //
                    m_CheckInCompletesFragment = new CICheckInCompletesFragment();
                    m_CheckInCompletesFragment.setPaxInfoList(m_arCheckInPaxInfoResp,m_arPassenger);
                    m_CheckInCompletesFragment.setMarkInfo(m_markBPAsPrintedEntity, m_mapMarkList);

                    fragment= m_CheckInCompletesFragment;
                    strTag  = m_CheckInCompletesFragment.toString();
                    bFirst = true;
                }
                break;
            default:
                break;
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if ( bFirst ){
            fragmentTransaction.setCustomAnimations(R.anim.anim_right_in, R.anim.anim_left_out);
        }

        fragmentTransaction.replace(m_flayout_content1.getId(), fragment, strTag);

        fragmentTransaction.commitAllowingStateLoss();
        m_Navigationbar.updateTitle();
    }

//    /**2017-03-23 Modifly by Ryan , 新增不能呼叫remark的場站*/
//    private boolean isNonRemarkStation(String strArrivalStation) {
//
//        String[] strAlltationList = new String[]{
//                //澳洲
//                "SYD", "BNE", "MEL",
//                //紐西蘭
//                "AKL", "CHC",
//                //關島
//                "GUM",
//                //帛琉
//                "ROR",
//                //美國站
//                "HNL","LAX","JFX","SFO","ORD","DFW","DAL","SEA","LAS","SAN","FLL","BOS","IAD","AUS","MCO","DEN","PDX","SLL","BWI","ANC","IAH"
//        };
//
//        for( String strStation: strAlltationList ) {
//            if( strStation.equals(strArrivalStation)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private boolean isUSAStation(String strArrivalStation) {
//        /**2017-03-23 Modifly by Ryan , 更新美國線機場*/
//        //String[] strUSAStationList = new String[]{"LAX","SFO","JFK","HNL","ANC","GUM","SEA","IAH"};
//        String[] strUSAStationList = new String[]{"HNL","LAX","JFX","SFO","ORD","DFW","DAL","SEA","LAS","SAN","FLL","BOS","IAD","AUS","MCO","DEN","PDX","SLL","BWI","GUM","ANC","IAH"};
//
//        for( String strUSAStation: strUSAStationList ) {
//            if( strUSAStation.equals(strArrivalStation)) {
//                return true;
//            }
//        }
//        return false;
//    }

    private void onNextStep() {
        SLog.d("onNextStep() + m_iCurrStep: "+m_iCurrStep);
        if (STEP_SELECT_FLIGHT == m_iCurrStep) {

            //紀錄選擇的航班
            if (null != m_SelectFlightFragment) {
                m_arSelectedFlights = m_SelectFlightFragment.getSelectedFlightList();

                //2018-08-22 新增五大航線組合，移至APIS內判斷
//                m_enRouteType = getRouteType(m_arSelectedFlights.m_Itinerary_InfoList);
//                for( CICheckInPax_ItineraryInfoEntity itineraryInfoEntity : m_arSelectedFlights.m_Itinerary_InfoList ) {
//                    if( isUSAStation(itineraryInfoEntity.Arrival_Station) ) {
//                        m_bArrivalUSA = true;
//                        break;
//                    }
//                }
                if (null != m_arSelectedFlights && 0 < m_arSelectedFlights.m_Itinerary_InfoList.size()) {
                    m_strDeparture = m_arSelectedFlights.m_Itinerary_InfoList.get(0).Departure_Station;
                    m_strArrive = m_arSelectedFlights.m_Itinerary_InfoList.get((m_arSelectedFlights.m_Itinerary_InfoList.size() - 1)).Arrival_Station;
                }
            }

            if (null == m_inquiryFlightStationPresenter.getAllDepatureStationList()) {
                getAllDepatureStationListFromDB();
            } else {
                m_InquiryCheckInPresenter.InquiryCheckInAllPaxByPNRFromWS(m_arSelectedFlights.Pnr_Id, m_SelectFlightFragment.getSelectedFlightSegmentNoList());
            }

        }
        else if( STEP_PROHIBIT_PRODUCT == m_iCurrStep ) {
            if ( true == CIApplication.getLoginInfo().GetLoginStatus()) {
                apis_presenter.getInstance().InquiryMyApisListNewFromWS(CIApplication.getLoginInfo().GetUserMemberCardNo(), m_InquiryApisListListener);
            }else{
                saved_apis_resonse = null;
                m_iCurrStep = m_vStepHorizontalView.setNextSteps();
                onNextStep(getSupportFragmentManager(), m_iCurrStep);
            }

        }
        else if( STEP_INPUT_APIS == m_iCurrStep ) {

            if( null != m_InputAPISFragment ) {
                ArrayList<HashMap<String,Object>> arInputApis = m_InputAPISFragment.getInputApisList();

                ArrayList<CICheckIn_Req> arInputApisPaxInfo = new ArrayList<>();
                ArrayList<CICheckInEditAPIS_Req> arEditApisPaxInfo = new ArrayList<>();
                for( int iPos = 0 ; iPos < m_arPassenger.size() ; iPos++ ) {
                    ArrayList<CICheckInPax_ItineraryInfoEntity> arInItineraryInfo = m_arPassenger.get(iPos).m_Itinerary_InfoList;
                    ArrayList<CICheckInApisEntity> apisEntity = (ArrayList<CICheckInApisEntity>)arInputApis.get(iPos).get("Apis");
                    CICheckInDocaEntity docaEntity = (CICheckInDocaEntity)arInputApis.get(iPos).get("Doca");
                    String strNationality                     = (String) arInputApis.get(iPos).get("Nationality");
                    if( null == arInItineraryInfo ){
                        continue;
                    }

                    arInputApisPaxInfo.add( getPaxInfo( m_arPassenger.get(iPos),strNationality , apisEntity , docaEntity) );
                    //EDITAPIS
                    arEditApisPaxInfo.add( getEditAPIS_PaxInfo( m_arPassenger.get(iPos),strNationality , apisEntity , docaEntity) );
                }

                if( null != m_arInputApisPaxInfo ) {
                    m_arInputApisPaxInfo.clear();
                }
                m_arInputApisPaxInfo = arInputApisPaxInfo;

                //2018-09-03 調整為使用EditAPIS 上傳APIS資料
                SLog.d("arEditApisPaxInfo: "+GsonTool.toJson(arEditApisPaxInfo));
                SLog.d("m_arInputApisPaxInfo: "+GsonTool.toJson(m_arInputApisPaxInfo));
                CICheckInPresenter.getInstance(m_CheckInListener).EditAPISFromWS(arEditApisPaxInfo);
                //CICheckInPresenter.getInstance(m_CheckInListener).CheckInFromWS(arInputApisPaxInfo);
            }
        } else {

            //刪除未選擇的旅客
            if( STEP_SELECT_PASSENGERS == m_iCurrStep ) {
                removeUnSelectPassenger();
            }

            m_iCurrStep = m_vStepHorizontalView.setNextSteps();
            onNextStep(getSupportFragmentManager(), m_iCurrStep);
        }
    }

    private CICheckIn_Req getPaxInfo(CICheckInPax_InfoEntity entity, String strNationality , ArrayList<CICheckInApisEntity> ar_apisEntity , CICheckInDocaEntity docaEntity ) {
        CICheckIn_Req paxInfo_req = new CICheckIn_Req();
        paxInfo_req.First_Name  = entity.First_Name;
        paxInfo_req.Last_Name   = entity.Last_Name;
        paxInfo_req.Uci         = entity.Uci;
        paxInfo_req.Pnr_Id      = entity.Pnr_Id;
        paxInfo_req.Pax_Type    = entity.Pax_Type;

        for( CICheckInPax_ItineraryInfoEntity itineraryInfoEntity : entity.m_Itinerary_InfoList ) {

            CICheckIn_ItineraryInfo_Req apisItineraryInfoEntity = new CICheckIn_ItineraryInfo_Req();
            apisItineraryInfoEntity.Departure_Station   = itineraryInfoEntity.Departure_Station;
            apisItineraryInfoEntity.Arrival_Station     = itineraryInfoEntity.Arrival_Station;
            apisItineraryInfoEntity.Did                 = itineraryInfoEntity.Did;
            apisItineraryInfoEntity.Seat_Number         = itineraryInfoEntity.Seat_Number;
            apisItineraryInfoEntity.Segment_Number      = itineraryInfoEntity.Segment_Number;
//            apisItineraryInfoEntity.Apis                = ar_apisEntity;
//            apisItineraryInfoEntity.Doca                = docaEntity;
//            apisItineraryInfoEntity.BookingClass        = itineraryInfoEntity.BookingClass;
            apisItineraryInfoEntity.Flight_Number       = itineraryInfoEntity.Flight_Number;
//            apisItineraryInfoEntity.Airlines            = itineraryInfoEntity.Airlines;
            apisItineraryInfoEntity.Departure_Date      = itineraryInfoEntity.Departure_Date;
//            apisItineraryInfoEntity.Is_Change_Seat      = itineraryInfoEntity.Is_Change_Seat;
            apisItineraryInfoEntity.Is_Do_Check_Document= itineraryInfoEntity.Is_Do_Check_Document;
            apisItineraryInfoEntity.Nationality         = strNationality;

            paxInfo_req.Itinerary_Info.add(apisItineraryInfoEntity);
        }

        return paxInfo_req;
    }

    private CICheckInEditAPIS_Req getEditAPIS_PaxInfo(CICheckInPax_InfoEntity entity, String strNationality , ArrayList<CICheckInApisEntity> ar_apisEntity , CICheckInDocaEntity docaEntity ) {
        CICheckInEditAPIS_Req paxInfo_req = new CICheckInEditAPIS_Req();
        paxInfo_req.First_Name  = entity.First_Name;
        paxInfo_req.Last_Name   = entity.Last_Name;
        paxInfo_req.Uci         = entity.Uci;
        paxInfo_req.Pnr_Id      = entity.Pnr_Id;

        for( CICheckInPax_ItineraryInfoEntity itineraryInfoEntity : entity.m_Itinerary_InfoList ) {

            CICheckInEditAPIS_ItineraryInfo_Req apisItineraryInfoEntity = new CICheckInEditAPIS_ItineraryInfo_Req();
            apisItineraryInfoEntity.Departure_Station   = itineraryInfoEntity.Departure_Station;
            apisItineraryInfoEntity.Arrival_Station     = itineraryInfoEntity.Arrival_Station;
            apisItineraryInfoEntity.Did                 = itineraryInfoEntity.Did;
            apisItineraryInfoEntity.Seat_Number         = itineraryInfoEntity.Seat_Number;
            apisItineraryInfoEntity.Segment_Number      = itineraryInfoEntity.Segment_Number;
            apisItineraryInfoEntity.Apis                = ar_apisEntity;
            apisItineraryInfoEntity.Doca                = docaEntity;
            apisItineraryInfoEntity.Is_Do_Check_Document= itineraryInfoEntity.Is_Do_Check_Document;
            apisItineraryInfoEntity.Nationality         = strNationality;

            paxInfo_req.Itinerary_Info.add(apisItineraryInfoEntity);
        }

        return paxInfo_req;
    }

    View.OnClickListener m_onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if ( v.getId() == m_btnConfirm.getId() ){

                if ( m_iCurrStep == STEP_PROHIBIT_PRODUCT ){
                    if ( m_ProhibitProductClaimFragment.IsCheckBoxClick() == false ){
                        return;
                    }
                }

                if ( m_iCurrStep < TOTAL_STEP ){

                    if( isFillCompleteAndCorrect() ) {
//                        m_iCurrStep = m_vStepHorizontalView.setNextSteps();
//                        onNextStep(getSupportFragmentManager(), m_iCurrStep);
                        SLog.d("onClick");
                        onNextStep();
                    } else {
                        showDialog(getString(R.string.warning), m_strErrorMsg);
                    }
                } else {
                    CICheckInActivity.this.finish();
                    //by kevin 2016/6/29
                }

                if ( m_iCurrStep == STEP_INPUT_APIS ){
                    m_btnConfirm.setText(R.string.confirm);
                } else if ( m_iCurrStep == TOTAL_STEP ){
                    m_btnConfirm.setText(R.string.done);
//                    if ( m_strStartFrom.equals(BUNDLE_PARA_ENTRY_TRIP_DETAIL) ){
//                        m_btnConfirm.setText(R.string.back_to_trip_detail);
//                    } else {
//                        m_btnConfirm.setText(R.string.back_to_home);
//                    }
                }
            }
        }
    };

    private boolean isFillCompleteAndCorrect() {

        boolean bIsComplete = true;
        if( STEP_SELECT_FLIGHT == m_iCurrStep ) {

            bIsComplete = m_SelectFlightFragment.isFlighCardSelectd();
            if( false == bIsComplete ) {
                m_strErrorMsg = getString(R.string.please_select_flight);
            }

        } else if( STEP_SELECT_PASSENGERS == m_iCurrStep ) {

            bIsComplete = m_SelectPassengersFragment.isPassengerSelected();
            if( false == bIsComplete ) {
//                m_strErrorMsg = getString(R.string.please_select_flight);
                m_strErrorMsg = getString(R.string.please_select_passenger);
            }

        } else if( STEP_PROHIBIT_PRODUCT == m_iCurrStep ) {

            bIsComplete = m_ProhibitProductClaimFragment.IsCheckBoxClick();
//            if( false == bIsComplete ) {
//                m_strErrorMsg = "請確認旅客行李須知";
//            }

        } else if( STEP_INPUT_APIS == m_iCurrStep ) {

            bIsComplete = m_InputAPISFragment.isFillCompleteAndCorrect();
            if( false == bIsComplete ) {
                //2018-09-19 調整為內部判斷提供錯誤訊息
                //m_strErrorMsg = getString(R.string.please_fill_all_text_field_that_must_to_fill);
                m_strErrorMsg = m_InputAPISFragment.getErrorMsg();
            }

        } else if( STEP_COMPLETED == m_iCurrStep ) {

        }
        return bIsComplete;
    }

    @Override
    public void onBackPressed() {
        //統一都使用finish 關閉畫面 by ryan
        //super.onBackPressed();
        //overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
        finish();
    }

    @Override
    public void finish() {
        //如果有報到成功，資料數會大於零 by Kevin 2016/06/28
        if(null != m_arCheckInPaxInfoResp && 0 < m_arCheckInPaxInfoResp.size()){
            //報到成功後，告知首頁必須更新資料
            CIPNRStatusManager.getInstance(null).setHomePageTripIsChange(true);
            //
            Intent data = new Intent();
            data.putExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, m_iViewId);
            data.putExtra(UiMessageDef.BUNDLE_CHECK_IN_RESULT, true);
            setResult(RESULT_OK, data);
        }

        super.finish();

        m_SelectFlightFragment          = null;
        m_SelectPassengersFragment      = null;
        m_ProhibitProductClaimFragment  = null;
        m_InputAPISFragment             = null;
        m_CheckInCompletesFragment      = null;

        if( null != m_arSelectedFlights ) {

            if( null != m_arSelectedFlights.m_Itinerary_InfoList ) {
                m_arSelectedFlights.m_Itinerary_InfoList.clear();
                m_arSelectedFlights.m_Itinerary_InfoList = null;
            }
            m_arSelectedFlights = null;
        }

        if( null != m_arPassenger ) {
            m_arPassenger.clear();
            m_arPassenger = null;
        }

        if( null != m_arCheckInPaxInfoResp ) {
            m_arCheckInPaxInfoResp.clear();
            m_arCheckInPaxInfoResp = null;
        }

        System.gc();

        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if( UiMessageDef.REQUEST_CODE_CHECK_IN_ADD_SINGLE_PASSENGER == requestCode ) {

                String strAccount = data.getStringExtra(CIAddPassengerSingleActivity.BUNDLE_PARA_ACCOUNT);
//                String strPwd = data.getStringExtra(CIAddPassengerSingleActivity.BUNDLE_PARA_PASSWORD);

                CICheckInAllPaxResp arPassenger = GsonTool.toObject(strAccount,CICheckInAllPaxResp.class);

                if( null != arPassenger ) {

                    m_arPassenger.addAll( arPassenger);

                    for (CICheckInPax_InfoEntity entity : arPassenger) {
                        if (!TextUtils.isEmpty(entity.First_Name) && !TextUtils.isEmpty(entity.Last_Name)) {
                            final String strFullName = entity.First_Name + " " + entity.Last_Name;
                            m_SelectPassengersFragment.addPassenger(entity.Uci, strFullName);
                        }
                    }
                }
            } else if ( UiMessageDef.REQUEST_CODE_CHECK_IN_EDIT_APIS_VISA == requestCode ){

                //Toast.makeText(m_Context, "onActivityResult VISA", Toast.LENGTH_SHORT).show();

                ArrayList<CICheckInEditAPIS_Resp> arPaxInfo = (ArrayList<CICheckInEditAPIS_Resp>)data.getSerializableExtra(CICheckInVISAActivity.BUNDLE_PARA_PAXINFO_RESP);

                //無資料表示有可能按下略過。所以直接執行Check-in
                if ( null == arPaxInfo || arPaxInfo.size() <= 0 ){

                    arPaxInfo = m_arEditAPISPaxInfoResp;
                }

                onEditAPIS_Success(arPaxInfo);
            }
        }
    }

    private void filterPaxInfo( CICheckInAllPaxResp CheckInList ) {

        for( Iterator<CICheckInPax_InfoEntity> iterator = CheckInList.iterator() ; iterator.hasNext(); ) {
            CICheckInPax_InfoEntity entity = iterator.next();

            boolean bNeedRemove = true;
            for (CICheckInPax_ItineraryInfoEntity itEntity : entity.m_Itinerary_InfoList ) {

                if( false == itEntity.Is_Check_In && false == itEntity.Is_Black && true == itEntity.Is_Do_Check_In ) {
                    bNeedRemove = false;
                    break;
                }
            }

            if( true == bNeedRemove ) {
                iterator.remove();
                continue;
            }

        }
    }

    private void removeUnSelectPassenger() {

        if( null == m_arPassenger ) {
            return;
        }

        ArrayList<Integer> arSelectedPos = m_SelectPassengersFragment.getSelectedPositionList();

        CICheckInAllPaxResp arSelectPassenger = new CICheckInAllPaxResp();
        for(int iPos = 0 ; iPos < arSelectedPos.size() ; iPos++ ) {
//            m_arPassenger.remove(arSelectedPos.get(iPos));
            arSelectPassenger.add( m_arPassenger.get(arSelectedPos.get(iPos)) );
        }

        m_arPassenger = arSelectPassenger;
    }

    /** 把CheckIn Req的部分資料帶給 CheckIn Resp */
    private void setCheckInReqData(ArrayList<CICheckIn_Resp> arPaxInfo) {

        for( CICheckIn_Resp entity : arPaxInfo ) {

            for( CICheckIn_ItineraryInfo_Resp itineraryInfoEntity : entity.Itinerary_Info ) {
                setPassengerData(itineraryInfoEntity, entity.Pnr_Id, entity.Uci );
            }
        }

    }

    private void setPassengerData(CICheckIn_ItineraryInfo_Resp respItineraryInfoEntity, String strPnrId , String strUci) {

        //for( CICheckIn_Req entity : m_arInputApisPaxInfo ) {
        for( CICheckInPax_InfoEntity entity : m_arPassenger ) {
            if( !entity.Pnr_Id.equals(strPnrId) || !entity.Uci.equals(strUci) ) {
                continue;
            }

            for( CICheckInPax_ItineraryInfoEntity pax_Entity : entity.m_Itinerary_InfoList ) {
                if( pax_Entity.Did.equals(respItineraryInfoEntity.Did) ) {
                    //2016-10-28 Ryan, 如果Check-in Resp沒有座位, 再把AllPax的座位塞給他
                    if ( TextUtils.isEmpty(respItineraryInfoEntity.Seat_Number) ){
                        respItineraryInfoEntity.Seat_Number = pax_Entity.Seat_Number;
                    }
                    respItineraryInfoEntity.Flight_Number = pax_Entity.Flight_Number;
                    respItineraryInfoEntity.Departure_Date = pax_Entity.Departure_Date;
                    respItineraryInfoEntity.BookingClass = pax_Entity.BookingClass;
                    respItineraryInfoEntity.Airlines     = pax_Entity.Airlines;
                    respItineraryInfoEntity.Is_Change_Seat = pax_Entity.Is_Change_Seat;
                }
            }
        }

    }

    /** 發送 MarkBPAsPrinted(看過登機證註記)
     *
     * @param strMsg 未CheckIn成功的旅客列表(提示訊息)
     */
    private void sendMarkBPAsPrinedRequest(String strMsg) {

//        CIInquiryFlightStationPresenter flightStationPresenter = CIInquiryFlightStationPresenter.getInstance(null,null);
//
//        m_strErrorMsg = strMsg;
//
//        CIMarkBPAsPrintedEntity entity = new CIMarkBPAsPrintedEntity();
//
//        for( CICheckIn_Resp checkInPaxInfo : m_arCheckInPaxInfoResp ) {
//
//            CIMarkBPAsPrinted_Pax_Info pax_info = new CIMarkBPAsPrinted_Pax_Info();
//            pax_info.First_Name = checkInPaxInfo.First_Name;
//            pax_info.Last_Name  = checkInPaxInfo.Last_Name;
//            pax_info.Pnr_id     = checkInPaxInfo.Pnr_Id;
//            pax_info.Uci        = checkInPaxInfo.Uci;
//
//            //判斷當前航段是否能做Marked
//            boolean bIsCanMarked = true;
//
//            if( null != checkInPaxInfo.Itinerary_Info ) {
//
//                for (CICheckIn_ItineraryInfo_Resp checkInItineraryEntity : checkInPaxInfo.Itinerary_Info) {
//
//                    if( !bIsCanMarked ) {
//                        continue;
//                    }
//
//                    CIFlightStationEntity stationInfo = flightStationPresenter.getStationInfoByIATA(checkInItineraryEntity.Departure_Station);
//
//                    //檢查出發站可以列印線上登機證且到達站不為美國的機場
//                    if( null == stationInfo || (stationInfo.is_vpass.equals("Y") && !isUSAStation(checkInItineraryEntity.Arrival_Station)) ) {
//                        CIMarkBP_Pax_ItineraryInfo itineraryInfo = new CIMarkBP_Pax_ItineraryInfo();
//                        itineraryInfo.Arrival_Station = checkInItineraryEntity.Arrival_Station;
//                        itineraryInfo.Departure_Station = checkInItineraryEntity.Departure_Station;
//                        itineraryInfo.Did = checkInItineraryEntity.Did;
//                        pax_info.Itinerary_Info.add(itineraryInfo);
//                    } else {
//                        bIsCanMarked = false;
//                    }
//                }
//            }
//
//            //所有航段都可接受列印線上登機證且到達站不包含美國的機場
//            if( true == bIsCanMarked ) {
//                entity.Pax_Info.add(pax_info);
//            }
//        }

        m_strErrorMsg = strMsg;

        m_markBPAsPrintedEntity = checkIsNeedMarkBPAsPrined( m_arSelectedFlights.m_Itinerary_InfoList, m_arCheckInPaxInfoResp );

        //有需要Mark 在做
        if ( null != m_markBPAsPrintedEntity && m_markBPAsPrintedEntity.Pax_Info.size() > 0 ){

            CIMarkBPAsPrintedPresenter.getInstance(new CIMarkBPAsPrintedListener() {
                @Override
                public void onMarkBPAsPrintSuccess(String rt_code, String rt_msg) {
                    //不論成功/失敗，切換至CheckInComplete畫面
                    onMarkBPAsPrintFinish();
                }

                @Override
                public void onMarkBPAsPrintError(String rt_code, String rt_msg) {
                    //不論成功/失敗，切換至CheckInComplete畫面
                    onMarkBPAsPrintFinish();
                }

                @Override
                public void showProgress() {
                    showProgressDialog();
                }

                @Override
                public void hideProgress() {
                    hideProgressDialog();
                }

            }).MarkBPAsPrinted(m_markBPAsPrintedEntity);

        } else {
            hideProgressDialog();
            onMarkBPAsPrintFinish();
        }
    }


    /**將Check-in成功的乘客以及航段資料, 比對可以Mark 的航段整理出需要做Mark的資料 */
    private CIMarkBPAsPrintedEntity checkCheckInRespMark(LinkedHashMap<String, CICheckInPax_ItineraryInfoEntity> mapMarkList, ArrayList<CICheckIn_Resp> arCheckInPaxInfoResp ){

        CIMarkBPAsPrintedEntity entity = new CIMarkBPAsPrintedEntity();

        for( CICheckIn_Resp checkInPaxInfo : arCheckInPaxInfoResp ) {

            CIMarkBPAsPrinted_Pax_Info pax_info = new CIMarkBPAsPrinted_Pax_Info();
            pax_info.First_Name = checkInPaxInfo.First_Name;
            pax_info.Last_Name  = checkInPaxInfo.Last_Name;
            pax_info.Pnr_id     = checkInPaxInfo.Pnr_Id;
            pax_info.Uci        = checkInPaxInfo.Uci;

            pax_info.Itinerary_Info.clear();

            for (CICheckIn_ItineraryInfo_Resp checkInItineraryEntity : checkInPaxInfo.Itinerary_Info)  {

                if ( null != mapMarkList.get(checkInItineraryEntity.Segment_Number) ) {
                    CIMarkBP_Pax_ItineraryInfo itineraryInfo = new CIMarkBP_Pax_ItineraryInfo();
                    itineraryInfo.Arrival_Station = checkInItineraryEntity.Arrival_Station;
                    itineraryInfo.Departure_Station = checkInItineraryEntity.Departure_Station;
                    itineraryInfo.Did = checkInItineraryEntity.Did;
                    pax_info.Itinerary_Info.add(itineraryInfo);
                }
            }

            if( pax_info.Itinerary_Info.size() > 0 ) {
                entity.Pax_Info.add(pax_info);
            }
        }

        return entity;
    }


    private CICheckInPax_ItineraryInfoEntity mappingOidDid(CICheckInPax_ItineraryInfoEntity entity,
                                                           Map<String, CICheckInPax_ItineraryInfoEntity> mapAllItinerary,
                                                           LinkedHashMap<String, CICheckInPax_ItineraryInfoEntity> arGroupItinerary,
                                                           ArrayList<LinkedHashMap<String, CICheckInPax_ItineraryInfoEntity>> armapGroupItinerary ){

        //已經加上的行程則不需要再加入一次！
        if ( null != arGroupItinerary.get(entity.Did) ){
            return null;
        }

        //每次有一個新的航段要被Group時，需要檢查是否已經被其他航段Group起來，
        boolean bFind = false;
        for(Iterator<LinkedHashMap<String, CICheckInPax_ItineraryInfoEntity>> iterator = armapGroupItinerary.iterator(); iterator.hasNext(); ) {
            LinkedHashMap<String, CICheckInPax_ItineraryInfoEntity> mapGroup = iterator.next();

            if ( null != mapGroup.get(entity.Did) ){
                arGroupItinerary.putAll(mapGroup);
                iterator.remove();
                bFind = true;
                break;
            }
        }
        if ( bFind ){
            return null;
        }

        //先將自己Group起來，並且往下找檢查Oid是否有連結其他航段
        arGroupItinerary.put( entity.Did, entity );
        //將Oid裡面連結的Did航段找出來往外丟，沒有連結航段表示這是結尾也有可能是單一航段
        if ( null != entity.Oids && entity.Oids.size() > 0 ){
            return mapAllItinerary.get(entity.Oids.get(0).Oid);
        } else {
            return null;
        }
    }

    /**檢查航段是否已經被Group起來*/
    private Boolean checkDidIsExistGroup(CICheckInPax_ItineraryInfoEntity entity, ArrayList<LinkedHashMap<String, CICheckInPax_ItineraryInfoEntity>> armapGroupItinerary ){

        for ( HashMap<String, CICheckInPax_ItineraryInfoEntity> mapGroup : armapGroupItinerary ){

            if ( null != mapGroup.get(entity.Did) ){
                return true;
            }
        }

        return false;
    }

    /** 檢查航段是否可以Mark*/
    private CIMarkBPAsPrintedEntity checkIsNeedMarkBPAsPrined(ArrayList<CICheckInPax_ItineraryInfoEntity> arItinerary_InfoList, ArrayList<CICheckIn_Resp> arCheckInPaxInfoResp ) {

        Map<String, CICheckInPax_ItineraryInfoEntity> mapAllItinerary = new HashMap<>();
        ArrayList<LinkedHashMap<String, CICheckInPax_ItineraryInfoEntity>> armapGroupItinerary = new ArrayList<>();
        //將航班資訊轉為Key-value 結構
        for ( CICheckInPax_ItineraryInfoEntity entity : arItinerary_InfoList ){
            mapAllItinerary.put( entity.Did, entity );
        }

        for ( CICheckInPax_ItineraryInfoEntity entity : arItinerary_InfoList ){

            //檢查航段是否已經被Group起來
            if ( checkDidIsExistGroup(entity, armapGroupItinerary) ){
                continue;
            }

            CICheckInPax_ItineraryInfoEntity nextEntity = entity;
            LinkedHashMap<String, CICheckInPax_ItineraryInfoEntity> arGroupItinerary = new LinkedHashMap<>();
            //找尋航班是否有連結關係，只要航班不為null 表示還有找到連結航班
            while ( null != nextEntity ) {
                nextEntity = mappingOidDid( nextEntity, mapAllItinerary, arGroupItinerary, armapGroupItinerary);
            }
            //將漲完的航班Group起來
            armapGroupItinerary.add(arGroupItinerary);
        }

        m_mapMarkList.clear();

        //根據美國站邏輯以及是否可以顯示電子登機證, 來把不能顯示電子登機證的航段 剔除掉
        for(Iterator<LinkedHashMap<String, CICheckInPax_ItineraryInfoEntity>> iterator = armapGroupItinerary.iterator(); iterator.hasNext(); ) {
            boolean bIsRemove = false;
            LinkedHashMap<String, CICheckInPax_ItineraryInfoEntity> mapGroup = iterator.next();
            for ( Map.Entry<String, CICheckInPax_ItineraryInfoEntity> entry : mapGroup.entrySet() ){

                //不能呼叫remark的場站
                //if ( isNonRemarkStation( entry.getValue().Arrival_Station ) ){
                if ( CIAPISDef.bIsNonReMarkStation( entry.getValue().Arrival_Station ) ){
                    iterator.remove();
                    bIsRemove = true;
                    break;
                }
                //不支援線上電子登機證
                CIFlightStationEntity stationInfo = CIInquiryFlightStationPresenter.getInstance(null,null).getStationInfoByIATA(entry.getValue().Departure_Station);
                if ( null == stationInfo || (stationInfo.is_vpass.equals("N")) ){
                    iterator.remove();
                    bIsRemove = true;
                    break;
                }
            }
            //將航段mapping表整合成Seg為Key的對應表
            if ( false == bIsRemove ){
                for ( Map.Entry<String, CICheckInPax_ItineraryInfoEntity> entry : mapGroup.entrySet() ){
                    m_mapMarkList.put( entry.getValue().Segment_Number, entry.getValue() );
                }
            }
        }

        CIMarkBPAsPrintedEntity entity = checkCheckInRespMark( m_mapMarkList, arCheckInPaxInfoResp );

        return entity;
    }

    private void getAllDepatureStationListFromDB() {

        if(null == m_inquiryFlightStationPresenter.getAllDepatureStationList()){
            m_inquiryFlightStationPresenter.initAllStationDB();
            m_inquiryFlightStationPresenter.InquiryAllStationListFromWS();
        } else {
            m_InquiryCheckInPresenter.InquiryCheckInAllPaxByPNRFromWS(m_arSelectedFlights.Pnr_Id, m_SelectFlightFragment.getSelectedFlightSegmentNoList());
        }
    }

    /**
     * MarkBPAsPrint(看過登機證註記) 結束，切換至CheckInComplete畫面
     */
    private void onMarkBPAsPrintFinish() {
        m_iCurrStep = m_vStepHorizontalView.setNextSteps();
        onNextStep(getSupportFragmentManager(), m_iCurrStep);

        if( !TextUtils.isEmpty(m_strErrorMsg) ) {
            showDialog(getString(R.string.warning),
                    m_strErrorMsg,
                    getString(R.string.confirm));
        }
    }


    private void setFlightInfo( CICheckInPax_InfoEntity arFlightList ){

        int iFlightSize = arFlightList.m_Itinerary_InfoList.size();
        int iSize = m_arPassenger.size();
        for ( int iIdx = 0; iIdx < iSize; iIdx++ ){
            int iInfoSize = m_arPassenger.get(iIdx).m_Itinerary_InfoList.size();
            if ( iInfoSize != iFlightSize ){
                continue;
            }
            for ( int iJx = 0; iJx <iInfoSize; iJx++ ){
                if ( m_arPassenger.get(iIdx).m_Itinerary_InfoList.get(iJx).Segment_Number == arFlightList.m_Itinerary_InfoList.get(iJx).Segment_Number ){

                    m_arPassenger.get(iIdx).m_Itinerary_InfoList.get(iJx).Display_Departure_Date        = arFlightList.m_Itinerary_InfoList.get(iJx).Display_Departure_Date;
                    m_arPassenger.get(iIdx).m_Itinerary_InfoList.get(iJx).Display_Departure_Time        = arFlightList.m_Itinerary_InfoList.get(iJx).Display_Departure_Time;
                    m_arPassenger.get(iIdx).m_Itinerary_InfoList.get(iJx).Display_Departure_Date_Gmt    = arFlightList.m_Itinerary_InfoList.get(iJx).Display_Departure_Date_Gmt;
                    m_arPassenger.get(iIdx).m_Itinerary_InfoList.get(iJx).Display_Departure_Time_Gmt    = arFlightList.m_Itinerary_InfoList.get(iJx).Display_Departure_Time_Gmt;

                    m_arPassenger.get(iIdx).m_Itinerary_InfoList.get(iJx).Display_Arrival_Date        = arFlightList.m_Itinerary_InfoList.get(iJx).Display_Arrival_Date;
                    m_arPassenger.get(iIdx).m_Itinerary_InfoList.get(iJx).Display_Arrival_Time        = arFlightList.m_Itinerary_InfoList.get(iJx).Display_Arrival_Time;
                    m_arPassenger.get(iIdx).m_Itinerary_InfoList.get(iJx).Display_Arrival_Date_Gmt    = arFlightList.m_Itinerary_InfoList.get(iJx).Display_Arrival_Date_Gmt;
                    m_arPassenger.get(iIdx).m_Itinerary_InfoList.get(iJx).Display_Arrival_Time_Gmt    = arFlightList.m_Itinerary_InfoList.get(iJx).Display_Arrival_Time_Gmt;

                }
            }
        }

    }

    /**EditAPIS 成功後，將資料整理後 進行Check-in */
    private void onEditAPIS_Success( ArrayList<CICheckInEditAPIS_Resp> arEditAPISInfo ){

        if ( null == arEditAPISInfo || arEditAPISInfo.size() <= 0 ){
            return;
        }

        int iSize = arEditAPISInfo.size();
        for ( int iPassIdx = 0; iPassIdx < iSize; iPassIdx++ ){
            m_arInputApisPaxInfo.get(iPassIdx).Pax_Type = arEditAPISInfo.get(iPassIdx).Pax_Type;
            int iInfoSize = arEditAPISInfo.get(iPassIdx).Itinerary_Info.size();
            for ( int iIdx = 0; iIdx < iInfoSize; iIdx++ ){
                m_arInputApisPaxInfo.get(iPassIdx).Itinerary_Info.get(iIdx).Carrier = arEditAPISInfo.get(iPassIdx).Itinerary_Info.get(iIdx).Carrier;
                m_arInputApisPaxInfo.get(iPassIdx).Itinerary_Info.get(iIdx).Flight_Number = arEditAPISInfo.get(iPassIdx).Itinerary_Info.get(iIdx).Flight_number;
                m_arInputApisPaxInfo.get(iPassIdx).Itinerary_Info.get(iIdx).Departure_Date = arEditAPISInfo.get(iPassIdx).Itinerary_Info.get(iIdx).Departure_Date;
                m_arInputApisPaxInfo.get(iPassIdx).Itinerary_Info.get(iIdx).CPR_BoardPoint = arEditAPISInfo.get(iPassIdx).Itinerary_Info.get(iIdx).CPR_BoardPoint;
                m_arInputApisPaxInfo.get(iPassIdx).Itinerary_Info.get(iIdx).CPR_OffPoint = arEditAPISInfo.get(iPassIdx).Itinerary_Info.get(iIdx).CPR_OffPoint;
            }
        }

        CICheckInPresenter.getInstance(m_CheckInListener).CheckInFromWS(m_arInputApisPaxInfo);

    }

    /**
     * 修改流程
     * 於危險品畫面取得已儲存的ＡＰＩＳ資料
     * **/
    private CIInquiryApisListListener m_InquiryApisListListener = new CIInquiryApisListListener() {
//        @Override
//        public void InquiryApisSuccess(String rt_code, String rt_msg, CIApisResp apis) {
//
//            saveMyApisFromDB(apis.arApisList);
//
//            updateMyApisView(apis.arApisList);
//        }

        @Override
        public void InquiryApisSuccess(String rt_code, String rt_msg, CIApisQryRespEntity apis) {
            if (apis != null) {
                saved_apis_resonse = apis;
            }

            m_iCurrStep = m_vStepHorizontalView.setNextSteps();
            onNextStep(getSupportFragmentManager(), m_iCurrStep);
            //onNextStep(getSupportFragmentManager(), m_iCurrStep);
            //saveMyApisFromDB(apis.paxInfo);
            //updateMyApisView(apis.paxInfo);
            //queryAndUpdateCompanionsApisView(apis.paxInfo);
        }

        @Override
        public void InquiryApisError(String rt_code, String rt_msg) {
            //updateMyApisView(new ArrayList<CIApisQryRespEntity.CIApispaxInfo>());

//            showDialog(getString(R.string.warning),
//                    rt_msg,
//                    getString(R.string.confirm));
            saved_apis_resonse = null;
            m_iCurrStep = m_vStepHorizontalView.setNextSteps();
            onNextStep(getSupportFragmentManager(), m_iCurrStep);
        }

        @Override
        public void InsertApidSuccess(String rt_code, String rt_msg) {

        }

        @Override
        public void InsertApisError(String rt_code, String rt_msg) {

        }

        @Override
        public void UpdateApisSuccess(String rt_code, String rt_msg) {

        }

        @Override
        public void UpdateApisError(String rt_code, String rt_msg) {

        }

        @Override
        public void InsertUpdateApisSuccess(String rt_code, String rt_msg) {

        }

        @Override
        public void InsertUpdateApisError(String rt_code, String rt_msg) {

        }

        @Override
        public void DeleteApisSuccess(String rt_code, String rt_msg) {

        }

        @Override
        public void DeleteApisError(String rt_code, String rt_msg) {

        }

        @Override
        public void showProgress() {
            //showProgress();
        }

        @Override
        public void hideProgress() {

        }

        @Override
        public void onAuthorizationFailedError(String rt_code, String rt_msg) {
//            isProcessWSErrCode(rt_code, rt_msg);
        }
    };
}
