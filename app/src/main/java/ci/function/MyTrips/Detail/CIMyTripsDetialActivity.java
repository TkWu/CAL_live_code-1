package ci.function.MyTrips.Detail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import ci.function.BoardingPassEWallet.CIBoardingWithQRCodeActivity;
import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.function.Main.BaseActivity;
import ci.function.MyTrips.Adapter.CIMyDetialFragmentAdapter;
import ci.ui.CAL_Map.FlightLocationManager;
import ci.ui.CAL_Map.MapFragment;
import ci.ui.define.HomePage_Status;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.AppInfo;
import ci.ui.object.CIPNRStatusManager;
import ci.ui.view.ImageHandle;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CICheckInPax_ItineraryInfoEntity;
import ci.ws.Models.entities.CICheckInPax_InfoEntity;
import ci.ws.Models.entities.CICheckInAllPaxResp;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Models.entities.CIMarkBPAsPrintedEntity;
import ci.ws.Models.entities.CIMarkBPAsPrinted_Pax_Info;
import ci.ws.Models.entities.CIMarkBP_Pax_ItineraryInfo;
import ci.ws.Models.entities.CIPNRItineraryStatusResp;
import ci.ws.Models.entities.CIPassengerByPNRReq;
import ci.ws.Models.entities.CIPassengerListResp;
import ci.ws.Models.entities.CIPassengerListResp_PaxInfo;
import ci.ws.Models.entities.CITripListResp;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Models.entities.CITripbyPNRReq;
import ci.ws.Presenter.CIFindMyBookingPresenter;
import ci.ws.Presenter.CIInquiryCheckInPresenter;
import ci.ws.Presenter.CIInquiryFlightStationPresenter;
import ci.ws.Presenter.CIInquiryPNRItineraryStatusPresenter;
import ci.ws.Presenter.CIInquiryPassengerByPNRPresenter;
import ci.ws.Presenter.CIMarkBPAsPrintedPresenter;
import ci.ws.Presenter.Listener.CIFindMyBookingListener;
import ci.ws.Presenter.Listener.CIInquiryCheckInListener;
import ci.ws.Presenter.Listener.CIInquiryFlightStatusStationListener;
import ci.ws.Presenter.Listener.CIInquiryPNRItineraryStatusListener;
import ci.ws.Presenter.Listener.CIInquiryPassengerByPNRListener;
import ci.ws.Presenter.Listener.CIMarkBPAsPrintedListener;
import ci.ws.define.CIWSHomeStatus_Code;
import ci.ws.define.CIWSResultCode;

import static ci.function.MyTrips.Detail.CIFlightTabFragment.OnFlightTabFragmentParameter;
import static ci.function.MyTrips.Detail.CIPassengerTabFragment.OnPassengerTabFragmentInterface;
import static ci.function.MyTrips.Detail.CIPassengerTabFragment.OnPassengerTabFragmentListener;
import static ci.function.MyTrips.Detail.CIPassengerTabFragment.OnPassengerTabFragmentParameter;

/**
 * Created by kevincheng on 2016/3/2.
 */
public class CIMyTripsDetialActivity extends BaseActivity
{
    private NavigationBar           m_navigationBar         = null;
    private ViewPager               m_viewPager             = null;
    private TabLayout               m_tabLayout             = null;
    private ArrayList<String>       m_strLists              = null;
    private ViewScaleDef            m_vScaleDef             = null;

    private CIFlightTabFragment     m_FlightTabFragment     = null;
    private CIPassengerTabFragment  m_PassengerTabFragment  = null;

    //頁面title
    private String                  m_strTitle                  = "";
    //是否可以線上CheckIn
    private String                  m_strIsOnlineCheck          = null;
    private boolean                 m_bIsOnlineCheckIn          = false;
    //2016-08-10 add for 需要判斷該航班是否可以線上列印電子登機證
    private boolean                 m_bIsvPass                  = false;
    //是否繼續顯示ProBar
//    private boolean                 m_bIsShowPro                = false;
    //當前顯示的頁面 0:flight tab, 1:passenger tab
    private int                     m_iCurrentPage              = 0;
    //乘客數
    private int                     m_iPassengerCount           = 0;

    private int                     m_iShowProgressCount        = -1;

    //航段資料
    private CITripListResp_Itinerary m_tripData                 = null;
    //航段狀態
    private CIPNRItineraryStatusResp m_PNRStatusResp            = null;
    //乘客資料
    private CIPassengerListResp     m_PassengerListResp         = null;
    private ArrayList<CICheckInPax_InfoEntity> m_CheckInResp    = null;

    private FlightLocationManager   m_manager                   = null;

    //2016-07-15 ryan 調整Check-in Presenter 使用方式
    private CIInquiryCheckInPresenter m_InquiryCheckInPresenter = null;

    //2016-08-09 ryan for 新增航班丁選首頁的功能
    private Button          m_btnHomeTripTrack  = null;
    //從db取的的航段資訊
    private CITripListResp  m_PNRTripList       = null;
    //該行段是否已經結束, 已飛完不能丁選
    private Boolean         m_bIsTripfinish     = false;

    private int m_iMapW;
    private int m_iMapH;

    private String m_strFirstName = "";
    private String m_strLastName  = "";

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
        public void onDeleteClick() {}

        @Override
        public void onDemoModeClick() {}
    };

    private OnFlightTabFragmentParameter m_onFlightTabFragmentParameter = new OnFlightTabFragmentParameter() {
        @Override
        public String GetAirName() {
            return m_strTitle.substring(m_strTitle.length()-3, m_strTitle.length());
        }
    };

    private OnPassengerTabFragmentListener m_onPassengerTabFragmentListener = new OnPassengerTabFragmentListener() {
        @Override
        public void showProDlg() {
            showProgressDialog();
        }

        @Override
        public void hideProDlg() {
            hideProgressDialog();
        }

        @Override
        public void ResetPassengerCount(int iCount) {
            //設定旅客數
//            m_iPassengerCount = m_Parameter.getPassengerCount();
            m_iPassengerCount = iCount;

            m_strLists.clear();
            m_strLists.add(getString(R.string.trips_detail_tab_flight));
            String strPassengerByFormat = String.format(
                    getString(R.string.trips_detail_tab_passenger),
                    m_iPassengerCount);
            m_tabLayout.getTabAt(1).setText(strPassengerByFormat);
            m_strLists.add(strPassengerByFormat);
//            m_strLists.add(getString(R.string.trips_detail_tab_information));
            selfAdjustForTabLayout(m_vScaleDef);

            setTabTextSpan(m_iCurrentPage);
        }

        @Override
        public void ReLoadPassenagerInfo() {
            loadPassengerData();
        }
    };

    private OnPassengerTabFragmentParameter m_onPassengerTabFragmentParameter = new OnPassengerTabFragmentParameter() {
        @Override
        public int getPNRStatusCode() {
            if ( null ==  m_PNRStatusResp || null == m_PNRStatusResp.PNR_Status ){
                return CIWSHomeStatus_Code.TYPE_A_NO_TICKET;
            }
            return m_PNRStatusResp.PNR_Status.iStatus_Code;
        }

        @Override
        public boolean getIsOnlineCheckInByStatuCode() {
            return m_bIsOnlineCheckIn;
        }

        @Override
        public boolean getIsvBoardingPassByStatuCode() { return m_bIsvPass; }

        @Override
        public CITripListResp_Itinerary getTripData() {
            return m_tripData;
        }

        @Override
        public ArrayList<CICheckInPax_InfoEntity> getCPRData() {
            return m_CheckInResp;
        }
    };

    private OnPassengerTabFragmentInterface m_onPassengerTabFragmentInterface;

    CIInquiryFlightStatusStationListener m_FlightStatusStationListener = new CIInquiryFlightStatusStationListener() {
        @Override
        public void onAllStationSuccess(String rt_code, String rt_msg, CIInquiryFlightStationPresenter presenter) {
//            m_bIsShowPro = true;
        }
        @Override
        public void onStationError(String rt_code, String rt_msg) {
            switch (rt_code) {
                case CIWSResultCode.NO_INTERNET_CONNECTION:
                    showNoWifiDialog();
                    break;
                default:
                    showDialog(getString(R.string.warning), rt_msg, getString(R.string.confirm));
                    break;
            }
        }

        @Override
        public void onODStationSuccess(String rt_code, String rt_msg, CIInquiryFlightStationPresenter presenter) {}

        @Override
        public void showProgress() {
            showProgressDialog();
        }

        @Override
        public void hideProgress() {
//            if ( true == m_bIsShowPro ){
//                m_bIsShowPro = false;
//                return;
//            }
            hideProgressDialog();
        }
    };

    CIInquiryPNRItineraryStatusListener m_InquiryPNRItineraryStatusListener = new CIInquiryPNRItineraryStatusListener() {
        @Override
        public void onInquiryPNRItineraryStatusSuccess(String rt_code, String rt_msg, CIPNRItineraryStatusResp resp) {
            m_PNRStatusResp = resp;

            if ( null != m_PassengerListResp ){
                SetPaxStatus();
            }else {
//                m_bIsShowPro = true;
            }
        }

        @Override
        public void onInquiryPNRItineraryStatusError(String rt_code, String rt_msg) {
            switch (rt_code) {
                case CIWSResultCode.NO_INTERNET_CONNECTION:
                    showNoWifiDialog();
                    break;
                default:
                    showDialog(getString(R.string.warning), rt_msg, getString(R.string.confirm));
                    break;
            }
        }

        @Override
        public void showProgress() {
            showProgressDialog();
        }

        @Override
        public void hideProgress() {
//            if ( true == m_bIsShowPro ){
//                m_bIsShowPro = false;
//                return;
//            }
            hideProgressDialog();
        }
    };

    CIInquiryPassengerByPNRListener m_PassengerWSListener = new CIInquiryPassengerByPNRListener() {
        @Override
        public void onInquiryPassengerByPNRSuccess(String rt_code, String rt_msg, CIPassengerListResp PassengerList) {
            m_PassengerListResp = PassengerList;

            if ( null == m_PNRStatusResp || null == m_PNRStatusResp.PNR_Status ){
                loadPnrStatus();
            }else {
                SetPaxStatus();
            }
        }

        @Override
        public void onInquiryPassengerByPNRError(String rt_code, String rt_msg) {

            m_onPassengerTabFragmentInterface.SetPassengerList(null);

            switch (rt_code) {
                case CIWSResultCode.NO_INTERNET_CONNECTION:
                    showNoWifiDialog();
                    break;
                default:
                    showDialog(getString(R.string.warning), rt_msg, getString(R.string.confirm));
                    break;
            }
        }

        @Override
        public void showProgress() {
            showProgressDialog();
        }

        @Override
        public void hideProgress() {
//            if ( true == m_bIsShowPro ){
//                m_bIsShowPro = false;
//                return;
//            }
            hideProgressDialog();
        }
    };

    CIInquiryCheckInListener m_CheckInWSListener = new CIInquiryCheckInListener() {
        @Override
        public void onInquiryCheckInSuccess(String rt_code,
                                            String rt_msg,
                                            CICheckInAllPaxResp CheckInList) {}

        @Override
        public void onInquiryCheckInError(String rt_code, String rt_msg) {}

        @Override
        public void onInquiryCheckInAllPaxSuccess(String rt_code, String rt_msg, CICheckInAllPaxResp CheckInList) {
            //比對CPR與乘客資料
            ComparisonPaxAndCprData(CheckInList);

            //刷新PassengerTab
            m_onPassengerTabFragmentInterface.SetPassengerList(m_PassengerListResp);
        }

        @Override
        public void onInquiryCheckInAllPaxError(String rt_code, String rt_msg) {
            //2017-03-31 modifly by ryan for 調整顯示乘客資訊邏輯
            //只要有PNR的資料就需要顯示乘客資訊
            //原邏輯為無CPR資料不顯示乘客資訊
            if ( null != m_PassengerListResp ){

                //比對CPR與乘客資料
                ComparisonPaxAndCprData(null);

                //刷新PassengerTab
                m_onPassengerTabFragmentInterface.SetPassengerList(m_PassengerListResp);

            } else {
                switch (rt_code) {
                    case CIWSResultCode.NO_INTERNET_CONNECTION:
                        showNoWifiDialog();
                        break;
                    default:
                        showDialog(getString(R.string.warning), rt_msg, getString(R.string.confirm));
                        break;
                }
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
    };

    View.OnClickListener m_onTrackClick = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            if ( m_bIsTripfinish ){

                showDialog(getResources().getString(R.string.warning), getResources().getString(R.string.trip_detail_home_page_trip_finish));
                return;
            }

            if (m_btnHomeTripTrack.isSelected()){
                m_btnHomeTripTrack.setSelected(false);
                //取消丁選
                CIPNRStatusManager.getInstance(null).UpdatePNRisVisibleAtHome(m_tripData.Pnr_Id, false);
                CIPNRStatusManager.getInstance(null).setHomePageTripIsChange(true);
            } else {
                //確定丁選行程則需要把原行程取消,
                if ( null != m_PNRTripList ){
                    CIPNRStatusManager.getInstance(null).UpdatePNRisVisibleAtHome(m_PNRTripList.PNR_Id, false);
                }
                //檢察行程是否存在DB
                if ( null != CIPNRStatusManager.getInstance(null).FindTripListByPNRId(m_tripData.Pnr_Id) ){
                    CIPNRStatusManager.getInstance(null).UpdatePNRisVisibleAtHome(m_tripData.Pnr_Id, true);
                    m_btnHomeTripTrack.setSelected(true);
                    CIPNRStatusManager.getInstance(null).setHomePageTripIsChange(true);
                } else {
                    // 不存在則須啟動回補
                    if ( null != m_PassengerListResp && m_PassengerListResp.Pax_Info.size() > 0  ){
                        CITripbyPNRReq tripbyPNRReq = new CITripbyPNRReq();
                        tripbyPNRReq.Pnr_id = m_tripData.Pnr_Id;
                        tripbyPNRReq.First_Name = m_PassengerListResp.Pax_Info.get(0).First_Name;
                        tripbyPNRReq.Last_Name  = m_PassengerListResp.Pax_Info.get(0).Last_Name;
                        CIFindMyBookingPresenter.getInstance(m_InquiryTripListener).InquiryTripByPNRFromWS(tripbyPNRReq);
                    } else {
                        loadPassengerData();
                    }
                    return;
                }
            }

            CheckTrackButtonStatus();
        }
    };

    CIFindMyBookingListener m_InquiryTripListener = new CIFindMyBookingListener() {
        @Override
        public void onInquiryTripsSuccess(String rt_code, String rt_msg, CITripListResp Tripslist) {
            CIPNRStatusManager.getInstance(null).insertOrUpdatePNRDataToDB( Tripslist, true);
            CIPNRStatusManager.getInstance(null).setHomePageTripIsChange(true);
            m_btnHomeTripTrack.setSelected(true);
            CheckTrackButtonStatus();
        }
        @Override
        public void onInquiryTripsError(String rt_code, String rt_msg) {}
        @Override
        public void showProgress() { showProgressDialog();}
        @Override
        public void hideProgress() { hideProgressDialog();}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            //初始化的page index
            m_iCurrentPage = bundle.getInt(UiMessageDef.BUNDLE_TRIPS_DETIAL_CURRENT_PAGE);

            //mytrip行程資料
            m_tripData = (CITripListResp_Itinerary) bundle.getSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST);

            //title文字為 出發地 -> 目的地
            m_strTitle = String.format(
                    getString(R.string.my_trips_goto),
                    m_tripData.Departure_Station,
                    m_tripData.Arrival_Station);

            m_strFirstName = bundle.getString(UiMessageDef.BUNDLE_FIRST_NAME, "");
            m_strLastName = bundle.getString(UiMessageDef.BUNDLE_LAST_NAME, "");
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_my_trips_detial;
    }

    @Override
    protected void initialLayoutComponent() {

        m_manager = new FlightLocationManager(new FlightLocationManager.Callback() {
            @Override
            public void onDataBinded(String result) {
                changeMapFragment(result, false);
            }
        });

        //2017.1.4 CR - flight status／time table 統一使用trip detail 的地圖高度
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        m_iMapW = displayMetrics.widthPixels;
        m_iMapH = displayMetrics.heightPixels;
        //預設地圖img 寬高與map一致
        ImageView imgMap = (ImageView) findViewById(R.id.img_bg_map);
        imgMap.setMinimumHeight(m_iMapH / 2);
        imgMap.setMaxHeight(m_iMapH / 2);
        imgMap.setMinimumWidth(m_iMapW);
        imgMap.setMaxWidth(m_iMapW);

        m_navigationBar = (NavigationBar) findViewById(R.id.toolbar);
        m_PassengerTabFragment = new CIPassengerTabFragment();
        m_onPassengerTabFragmentInterface = m_PassengerTabFragment.uiSetParameterListener(
                m_onPassengerTabFragmentParameter, m_onPassengerTabFragmentListener);

        m_FlightTabFragment = new CIFlightTabFragment();
        m_FlightTabFragment.setArguments(getIntent().getExtras());
        m_FlightTabFragment.uiSetParameterListener(m_onFlightTabFragmentParameter);

        initViewPager();
        initTabHost();

        m_InquiryCheckInPresenter = new CIInquiryCheckInPresenter(m_CheckInWSListener);

        m_viewPager.addOnPageChangeListener(m_onPageChangeListener);

        //預先秀轉圈圈進度圖
        showProgressDialog();

        m_BaseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadWSData();
            }
        }, 500);

        //2016-08-09 ryan 判斷該行段是否已經被丁選在首頁
        m_btnHomeTripTrack = (Button)findViewById(R.id.btn_track);
        m_btnHomeTripTrack.setOnClickListener(m_onTrackClick);
        if ( CheckTripIsTrackAtHome() ){
            m_btnHomeTripTrack.setSelected(true);
        } else {
            m_btnHomeTripTrack.setSelected(false);
        }
        CheckTrackButtonStatus();

        //2018-11-01 將inquiry的Name 傳入
        m_onPassengerTabFragmentInterface.setUserName(m_strFirstName, m_strLastName);
    }

    ViewPager.OnPageChangeListener m_onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            m_iCurrentPage = position;

            if (0 == position) {
                if (null != m_FlightTabFragment) {
                    m_FlightTabFragment.seachWeather();
                }
            }else if ( 1 == position ){
                //切換到乘客tab 如果尚未撈到資料 就再重撈
                loadWSData();
            }

            setTabTextSpan(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    };
    public void changeMapFragment(String result, boolean isfull) {

        //
        //2016-11-03 Ryan, 加上google play service 服務檢查, 針對大陸手機沒有安裝GoogleplayService
        //不顯示Mapview
        FrameLayout map_layout = (FrameLayout)findViewById(R.id.fl_map);
        //
        if ( false == AppInfo.getInstance(CIApplication.getContext()).isGooglePlayServicesAvailable() ){

            map_layout.setVisibility(View.GONE);
            return;
        }
        map_layout.setVisibility(View.VISIBLE);
        //

        if (isfull) {
            map_layout.getLayoutParams().height = m_iMapH;
            map_layout.getLayoutParams().width = m_iMapW;
            map_layout.requestLayout();
        } else {
            map_layout.getLayoutParams().height = m_iMapH / 2;
            map_layout.getLayoutParams().width = m_iMapW;
            map_layout.requestLayout();

        }
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fl_map, MapFragment.newInstance(result));
        transaction.commitAllowingStateLoss();
    }

    private void initTabHost() {
        m_tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        m_tabLayout.setupWithViewPager(m_viewPager);
        m_tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    private void initViewPager() {
        m_viewPager = (ViewPager) findViewById(R.id.viewPager);
        List<Fragment> listFragments = new ArrayList<>();
        listFragments.add(m_FlightTabFragment);
        listFragments.add(m_PassengerTabFragment);
//        listFragments.add(new CIUpComingTripsFragment());
        m_strLists = new ArrayList<>();
        m_strLists.add(getString(R.string.trips_detail_tab_flight));
        String strPassengerByFormat = String.format(getString(R.string.trips_detail_tab_passenger), m_iPassengerCount);
        m_strLists.add(strPassengerByFormat);
//        m_strLists.add(getString(R.string.trips_detail_tab_information));
        CIMyDetialFragmentAdapter adapter = new CIMyDetialFragmentAdapter(getSupportFragmentManager(), listFragments, m_strLists);
        m_viewPager.setAdapter(adapter);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        m_vScaleDef = vScaleDef;
        selfAdjustForTabLayout(vScaleDef);

        m_viewPager.setCurrentItem(m_iCurrentPage);
        setTabTextSpan(m_iCurrentPage);

        //設定tab高度為43.2(同zeplin)
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)m_tabLayout.getLayoutParams();
        params.height = ViewScaleDef.getInstance(m_Context).getLayoutHeight(43.2);
    }

    private void selfAdjustForTabLayout(ViewScaleDef vScaleDef) {
        ArrayList<View> viewLists = new ArrayList<>();
        for (int loop = 0; loop < m_strLists.size(); loop++) {
            m_tabLayout.findViewsWithText(viewLists, m_strLists.get(loop), ViewGroup.FIND_VIEWS_WITH_TEXT);
        }
        for (int loop = 0; loop < viewLists.size(); loop++) {
            vScaleDef.setMargins(viewLists.get(loop), 0, 7, 0, 0);
        }
        try {
            Field field = TabLayout.class.getDeclaredField("mTabTextSize");
            Field field2 = TabLayout.class.getDeclaredField("mTabTextMultiLineSize");
            field.setAccessible(true);
            field2.setAccessible(true);
            field.setInt(m_tabLayout, vScaleDef.getTextSize(16));
            field2.setInt(m_tabLayout, vScaleDef.getTextSize(16));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTabTextSpan(int iIndex){
        //將未被選取的tab恢復原本的字型
        for (int i = 0; i < m_tabLayout.getTabCount(); i++){
            if ( i != iIndex ){
                String str = m_tabLayout.getTabAt(i).getText().toString();
                SpannableString spannableString = new SpannableString(str);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spannableString);
                spannableStringBuilder.setSpan(
                        new StyleSpan(Typeface.NORMAL),
                        0,
                        str.length(),
                        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                m_tabLayout.getTabAt(i).setText(spannableStringBuilder);
            }
        }

        //將被選取的tab字體加粗
        String strSelect = m_tabLayout.getTabAt(iIndex).getText().toString();
        SpannableString spannableStringSelect = new SpannableString(strSelect);
        SpannableStringBuilder spannableStringBuilderSelect = new SpannableStringBuilder(spannableStringSelect);
        spannableStringBuilderSelect.setSpan(
                new StyleSpan(Typeface.BOLD),
                0,
                strSelect.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        m_tabLayout.getTabAt(iIndex).setText(spannableStringBuilderSelect);
    }

    //一進頁面,需取得是否可線上check-in, pnr狀態及乘客...等資料
    private void loadWSData(){

        if(null == m_tripData){
            showDialog(m_Context.getString(R.string.warning),
                    m_Context.getString(R.string.error),
                    m_Context.getString(R.string.confirm),
                    "",
                    new CIAlertDialog.OnAlertMsgDialogListener() {
                        @Override
                        public void onAlertMsgDialog_Confirm() {
                            onBackPressed();
                        }

                        @Override
                        public void onAlertMsgDialogg_Cancel() {

                        }
                    });

            return;
        }

        if ( null == m_strIsOnlineCheck ){
            loadFlightStationData();
        }
        if ( null == m_PNRStatusResp || null == m_PNRStatusResp.PNR_Status ){
            loadPnrStatus();
        }
        if ( null == m_PassengerListResp ){
            loadPassengerData();
        }
    }

    //取得機場是否可以線上check-in tag
    private void loadFlightStationData(){

        CIFlightStationEntity flightStationEntity = CIInquiryFlightStationPresenter
                .getInstance( null, CIInquiryFlightStationPresenter.ESource.FlightStatus )
                .getStationInfoByIATA( m_tripData.Departure_Station );

        if ( null == flightStationEntity){
            CIInquiryFlightStationPresenter.getInstance(
                    m_FlightStatusStationListener,
                    CIInquiryFlightStationPresenter.ESource.FlightStatus)
                    .InquiryAllStationListFromWS();
        } else {
            if ( null != flightStationEntity.is_vcheckin ){

                m_strIsOnlineCheck = flightStationEntity.is_vcheckin;

               SLog.d("is_vcheckin","此機場"+m_tripData.Departure_Station+"是否可以線上check-in:"+m_strIsOnlineCheck);

                //可以線上checkin
                if( "Y".equals(m_strIsOnlineCheck) ){
                    m_bIsOnlineCheckIn = true;
                }

                m_bIsvPass = "Y".equals(flightStationEntity.is_vpass)? true:false;
            }
        }
    }

    //取得pnr狀態
    private void loadPnrStatus(){

        CIInquiryPNRItineraryStatusPresenter.getInstance(m_InquiryPNRItineraryStatusListener)
                .InquiryPNRItineraryStatusFromWS(m_tripData.Pnr_Id, m_tripData.Segment_Number);
    }

    //取得乘客資料
    private void loadPassengerData(){

        CIPassengerByPNRReq req = new CIPassengerByPNRReq();
        req.pnr_Id              = m_tripData.Pnr_Id;
        req.departure_station   = m_tripData.Departure_Station;
        req.arrival_station     = m_tripData.Arrival_Station;
        //2016-07-25 ryan add  for PNR_Seq, 讓Server抓取正確的航段資料
        req.pnr_seq             = m_tripData.Pnr_Seq;
        //201801106 add Segment_Number，for 行李追蹤抓資料使用
        req.Segment_Number      = m_tripData.Segment_Number;

        CIInquiryPassengerByPNRPresenter.getInstance(m_PassengerWSListener)
                .InquiryAllPassengerByPNRFromWS(req);
    }

    //如果pnr狀態為2或3, 需另取cpr資料
    private void loadCPRData(){
        ArrayList<String> alSegmentNo = new ArrayList<>();
        alSegmentNo.add(m_tripData.Segment_Number);

        m_InquiryCheckInPresenter.InquiryCheckInAllPaxByPNRFromWS(m_tripData.Pnr_Id, alSegmentNo);
    }

    //根據取得的資料設定各個乘客的狀態
    private void SetPaxStatus(){

        if ( null == m_PNRStatusResp || null == m_PNRStatusResp.PNR_Status.Status_Code ){
            for ( int i = 0; i < m_PassengerListResp.Pax_Info.size(); i ++ ){
                m_PassengerListResp.Pax_Info.get(i).Status_Code = HomePage_Status.TYPE_UNKNOW;
            }

            m_onPassengerTabFragmentInterface.SetPassengerList(m_PassengerListResp);
            return;
        }

       SLog.i("Status_Code","此航段狀態:"+m_PNRStatusResp.PNR_Status.Status_Code);
       SLog.i("Status_Code", "此航段狀態int:" + m_PNRStatusResp.PNR_Status.iStatus_Code);

        //status code若為2或3 及機場是可以online check in 同時符合時才再取CPR
        if ( (m_PNRStatusResp.PNR_Status.iStatus_Code == CIWSHomeStatus_Code.TYPE_D_E_CHECKIN
                || m_PNRStatusResp.PNR_Status.iStatus_Code == CIWSHomeStatus_Code.TYPE_D_E_GATE_INFO )
                && true == m_bIsOnlineCheckIn ){
//            m_bIsShowPro = true;
            loadCPRData();

        } else {

            for ( int i = 0; i < m_PassengerListResp.Pax_Info.size(); i ++ ){
                m_PassengerListResp.Pax_Info.get(i).Status_Code = m_PNRStatusResp.PNR_Status.iStatus_Code;
            }

            m_onPassengerTabFragmentInterface.SetPassengerList(m_PassengerListResp);
        }
    }

    //如有取到cpr, 需比對乘客與CPR資料
    private void ComparisonPaxAndCprData(CICheckInAllPaxResp CheckInList){

        if(null == CheckInList){
            return;
        }

        if ( null != m_CheckInResp){
            m_CheckInResp.clear();
        }else {
            m_CheckInResp = new ArrayList<>();
        }

        for ( int i = 0; i < m_PassengerListResp.Pax_Info.size(); i ++ ){
            for ( int j = 0; j < CheckInList.size(); j ++ ){
                SetUciAndDid(i, CheckInList.get(j));
            }
        }
    }

    //取得各乘客對應的Uci.Did及乘客狀態...等資料
    private void SetUciAndDid(int iIndex, CICheckInPax_InfoEntity cpr_pax_info){

        String PaxName = m_PassengerListResp.Pax_Info.get(iIndex).First_Name + " " + m_PassengerListResp.Pax_Info.get(iIndex).Last_Name;
        String CprPaxName = cpr_pax_info.First_Name + " " + cpr_pax_info.Last_Name;

       SLog.i("乘客名:"+PaxName, "cpr乘客名:"+CprPaxName);

        //比對姓名, 塞入對應的uci
        if ( m_PassengerListResp.Pax_Info.get(iIndex).First_Name.equals(cpr_pax_info.First_Name)
                && m_PassengerListResp.Pax_Info.get(iIndex).Last_Name.equals(cpr_pax_info.Last_Name) ){

            m_PassengerListResp.Pax_Info.get(iIndex).Uci = cpr_pax_info.Uci;

            //比對Segment_Number, 塞入對應的did
            for ( int k = 0; k < cpr_pax_info.m_Itinerary_InfoList.size(); k ++ ){
                CICheckInPax_ItineraryInfoEntity cpr_Itinerary = cpr_pax_info.m_Itinerary_InfoList.get(k);

                if ( m_tripData.Segment_Number.equals(cpr_Itinerary.Segment_Number) ){

                    m_PassengerListResp.Pax_Info.get(iIndex).Did = cpr_Itinerary.Did;
                    m_PassengerListResp.Pax_Info.get(iIndex).Status_Code = getPaxStatusCode( cpr_Itinerary );
                    //將CPR的資料帶到乘客卡
                    m_PassengerListResp.Pax_Info.get(iIndex).CPR_Is_Check_In    = cpr_Itinerary.Is_Check_In;
                    m_PassengerListResp.Pax_Info.get(iIndex).CPR_Is_Change_Seat = cpr_Itinerary.Is_Change_Seat;
                    m_PassengerListResp.Pax_Info.get(iIndex).CPR_Is_Do_Cancel_Check_In = cpr_Itinerary.Is_Do_Cancel_Check_In;
                    m_PassengerListResp.Pax_Info.get(iIndex).CPR_Is_Do_Check_In = cpr_Itinerary.Is_Do_Check_In;
                    m_PassengerListResp.Pax_Info.get(iIndex).CPR_Is_Black       = cpr_Itinerary.Is_Black;
                    m_PassengerListResp.Pax_Info.get(iIndex).CPR_Boarding_Pass  = cpr_Itinerary.Boarding_Pass;
                    m_PassengerListResp.Pax_Info.get(iIndex).bHaveCPR = true;
                    //2016-10-28 Ryan, 加入 CPR 的資料, 目前用來判斷Check-in以後換為是否要做Mark
                    m_PassengerListResp.Pax_Info.get(iIndex).Is_Display_Boarding_pass = cpr_Itinerary.Is_Display_Boarding_pass;
                    //
                    //2018-11-7 調整邏輯，行李追蹤由InquiryAllPassenger取得
                    //2018-10-30 新增 行李追蹤
                    //m_PassengerListResp.Pax_Info.get(iIndex).BaggageInfoNum = cpr_Itinerary.Baggage_Number;

                    m_CheckInResp.add(cpr_pax_info);
                }
            }
        }

        //沒取到乘客狀態時 就塞入pnr原始狀態
        if ( -1 == m_PassengerListResp.Pax_Info.get(iIndex).Status_Code ){
            m_PassengerListResp.Pax_Info.get(iIndex).Status_Code = HomePage_Status.TYPE_UNKNOW;
        }
    }

    //回傳正確的乘客狀態
    private int getPaxStatusCode(CICheckInPax_ItineraryInfoEntity entity){
        //是否已報到 by Kevin 2016/6/29
        if(true == entity.Is_Do_Check_In
                && true == entity.Is_Check_In
                && false == entity.Is_Black){
            return CIWSHomeStatus_Code.TYPE_D_E_GATE_INFO;

        } else {
            //未報到
            return CIWSHomeStatus_Code.TYPE_D_E_CHECKIN;
        }
    }

    @Override
    protected void setOnParameterAndListener() {
        m_navigationBar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {}

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    protected void onLanguageChangeUpdateUI() {}

    @Override
    protected void onResume() {

        if( null != m_tripData ){
            m_manager.executeTask(m_tripData);
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();

        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    public void onPause() {

        CIInquiryFlightStationPresenter.getInstance
                (m_FlightStatusStationListener, CIInquiryFlightStationPresenter.ESource.FlightStatus)
                .cancel();

        CIInquiryPNRItineraryStatusPresenter.getInstance(m_InquiryPNRItineraryStatusListener)
                .InquiryPNRItineraryStatusCancel();

        CIInquiryPassengerByPNRPresenter.getInstance(m_PassengerWSListener)
                .InquiryAllPassengerByPNRCancel();

        m_InquiryCheckInPresenter.InquiryCheckInCancel();

        m_manager.cancleTask();

        super.onPause();
    }


    private void checkIsNeedMarkBPPrint(){

        // 加上判斷避免pax_info為null時閃退 by Kevin 2016/12
        if(null == m_PassengerListResp || null == m_PassengerListResp.Pax_Info){
            return;
        }

        CIMarkBPAsPrintedEntity entity = new CIMarkBPAsPrintedEntity();

        for (CIPassengerListResp_PaxInfo passenger_pax_info : m_PassengerListResp.Pax_Info ){

            CIMarkBPAsPrinted_Pax_Info pax_info = new CIMarkBPAsPrinted_Pax_Info();
            pax_info.First_Name = passenger_pax_info.First_Name;
            pax_info.Last_Name  = passenger_pax_info.Last_Name;
            pax_info.Pnr_id     = m_tripData.Pnr_Id;
            pax_info.Uci        = passenger_pax_info.Uci;

            pax_info.Itinerary_Info.clear();

            if ( TextUtils.equals( "Y", passenger_pax_info.Is_Display_Boarding_pass ) ){

                CIMarkBP_Pax_ItineraryInfo itineraryInfo = new CIMarkBP_Pax_ItineraryInfo();
                itineraryInfo.Arrival_Station   = m_tripData.Arrival_Station;
                itineraryInfo.Departure_Station = m_tripData.Departure_Station;
                itineraryInfo.Did               = passenger_pax_info.Did;
                pax_info.Itinerary_Info.add(itineraryInfo);
            }

            if( pax_info.Itinerary_Info.size() > 0 ) {
                entity.Pax_Info.add(pax_info);
            }
        }

        if ( entity.Pax_Info.size() > 0 ){

            CIMarkBPAsPrintedPresenter.getInstance(new CIMarkBPAsPrintedListener() {
                @Override
                public void onMarkBPAsPrintSuccess(String rt_code, String rt_msg) {
                   SLog.i("[CheckIn]", "onMarkBPAsPrintSuccess ");
                }

                @Override
                public void onMarkBPAsPrintError(String rt_code, String rt_msg) {
                   SLog.i("[CheckIn]", "onMarkBPAsPrintError "+ rt_msg );
                }

                @Override
                public void showProgress() {}

                @Override
                public void hideProgress() {}
            }).MarkBPAsPrinted(entity);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            //電子登機證提示訊息
            case UiMessageDef.REQUEST_CODE_TRIP_DETAIL_TAG:
            //舊站Check-in
            case UiMessageDef.REQUEST_CODE_TRIP_DETAIL_PASSENGER_CHECK_IN_WEB:
                //不知道怎麼判斷成功與否 所以一律重刷
                loadPassengerData();
                break;
            case UiMessageDef.REQUEST_CODE_ADD_EXCESS_BAG:
                loadPassengerData();
                break;
            //當選位.選餐.新站Check-in或取消Check-in成功時 需重刷乘客資訊
            case UiMessageDef.REQUEST_CODE_TRIP_DETAIL_PASSENGER_SELECT_MEAL:
            case UiMessageDef.REQUEST_CODE_TRIP_DETAIL_PASSENGER_SELECT_SEAT:
            case UiMessageDef.REQUEST_CODE_TRIP_DETAIL_PASSENGER_CANCEL_CHECK_IN:
                //成功才重刷乘客資料
                if ( RESULT_OK == resultCode ){
                    //2016-10-28 增加邏輯，換為以後需要檢查是否做Mark
                    if ( requestCode == UiMessageDef.REQUEST_CODE_TRIP_DETAIL_PASSENGER_SELECT_SEAT ){
                        checkIsNeedMarkBPPrint();
                    }
                    //
                    loadPassengerData();
                }
                break;
            case UiMessageDef.REQUEST_CODE_TRIP_DETAIL_PASSENGER_CHECK_IN:
                if ( RESULT_OK == resultCode ) {
                    //額外判斷Check-in 完成後帶回來的Tag, 確保Check-in有完成，才顯示提示訊息
                    Boolean bIsCheckInComplete = false;
                    if ( null != data ){
                        bIsCheckInComplete = data.getBooleanExtra(UiMessageDef.BUNDLE_CHECK_IN_RESULT, false);
                    }
                    //check-in成功後 要show電子登機證提示訊息
                    if ( false == AppInfo.getInstance(m_Context).GetBoardingPassIsCloseRemind() && bIsCheckInComplete ){

                        //將目前的畫面截圖下來, 當作背景
                        Bitmap bitmap = ImageHandle.getScreenShot((Activity) m_Context);
                        Bitmap blur = ImageHandle.BlurBuilder(m_Context, bitmap, 13.5f, 0.15f);

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, blur);
                        Intent intent = new Intent();
                        intent.putExtras(bundle);
                        intent.setClass(m_Context, CIBoardingWithQRCodeActivity.class);
                        startActivityForResult(intent, UiMessageDef.REQUEST_CODE_TRIP_DETAIL_TAG);

                        overridePendingTransition(R.anim.anim_alpha_in, 0);

                        bitmap.recycle();
                    }else {
                        loadPassengerData();
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_viewPager.removeOnPageChangeListener(m_onPageChangeListener);
        m_navigationBar         = null;
        m_viewPager             = null;
        m_tabLayout             = null;
        m_strLists              = null;
        m_vScaleDef             = null;
        m_FlightTabFragment     = null;
        m_PassengerTabFragment  = null;
        m_strTitle              = null;
        m_strIsOnlineCheck      = null;
        m_tripData              = null;
        m_PNRStatusResp         = null;
        m_PassengerListResp     = null;
        m_CheckInResp           = null;
        m_manager               = null;
        m_onPassengerTabFragmentListener = null;
        m_onFlightTabFragmentParameter = null;
        m_onPageChangeListener = null;
        m_onNavigationParameter = null;
        m_onNavigationbarListener = null;
        //CIInquiryCheckInPresenter.getInstance(null);
        m_InquiryCheckInPresenter.setCallBack(null);
        CIInquiryPNRItineraryStatusPresenter.getInstance(null);
        CIInquiryPassengerByPNRPresenter.getInstance(null);
        System.gc();
    }

    //2016-08-09 ryan 新增 行段丁選首頁功能
    private void CheckTrackButtonStatus(){

        if ( m_btnHomeTripTrack.isSelected() ){
            m_btnHomeTripTrack.setText(getResources().getString(R.string.trip_detail_home_page_trip_tracked));
            m_btnHomeTripTrack.setTextColor(getResources().getColor(R.color.french_blue));
        } else {
            m_btnHomeTripTrack.setText(getResources().getString(R.string.trip_detail_home_page_trip_track));
            m_btnHomeTripTrack.setTextColor(getResources().getColor(R.color.white_four));
        }
    }

    private boolean CheckTripIsTrackAtHome(){

        boolean bTrack = false;
        //檢查該航段是否同首頁丁選的航段
        m_PNRTripList = CIPNRStatusManager.getInstance(null).getHomePagePNRTrip();
        if ( null != m_PNRTripList ){
            if ( TextUtils.equals( m_PNRTripList.PNR_Id, m_tripData.Pnr_Id ) && TextUtils.equals( m_PNRTripList.Segment_Num, m_tripData.Segment_Number ) ){
                //已被丁選, 要顯示已丁選的狀態
                bTrack = true;
            }
        }

        //檢查該航段是否已經過期
        if ( CIPNRStatusManager.getInstance(null).CheckPNRTimeIsOver24hr(m_tripData) ){
            m_bIsTripfinish = true;
            //以丁選,又已經過期則取消丁選
            if ( bTrack ){
                bTrack = false;
                CIPNRStatusManager.getInstance(null).UpdatePNRisVisibleAtHome(m_tripData.Pnr_Id, false);
            }
        } else {
            m_bIsTripfinish = false;
        }

        return bTrack;
    }

    @Override
    public void showProgressDialog() {
        m_iShowProgressCount++;
        super.showProgressDialog();
    }

    @Override
    public void hideProgressDialog() {
        m_iShowProgressCount--;
        if(0 >= m_iShowProgressCount){
            super.hideProgressDialog();
            m_iShowProgressCount = 0;
        }
    }
}
