package ci.function.BookTicket;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ci.function.Core.CIApplication;
import ci.function.Core.Encryption;
import ci.function.Core.Location.GpsReceiver;
import ci.function.Core.Location.SSingleLocationUpdater;
import ci.function.Core.SLog;
import ci.function.Main.BaseActivity;
import ci.function.TimeTable.CISelectDepartureAirpotActivity;
import ci.ui.TextField.Base.CIBaseTextFieldFragment;
import ci.ui.TextField.CIChooseAirportTextFieldFragment;
import ci.ui.TextField.CIChooseSearchDateTextFieldFragment;
import ci.ui.TextField.CIDropDownMenuTextFieldFragment;
import ci.ui.TextField.CIOnlyEnglishAndNumberTextFieldFragment;
import ci.ui.WebView.CIWithoutInternetActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.object.CIAirportDataManager;
import ci.ui.object.CILoginInfo;
import ci.ui.object.CIProgressDialog;
import ci.ui.toast.CIToastView;
import ci.ui.view.NavigationBar;
import ci.ui.view.StepHorizontalView;
import ci.ui.view.TwoItemNavigationBar;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Models.entities.CIInquiryCoporateIdTokenResp;
import ci.ws.Models.entities.CIInquiryPromoteCodeTokenResp;
import ci.ws.Presenter.CIBookTicketPresenter;
import ci.ws.Presenter.CISelectDepartureAirportPresenter;
import ci.ws.Presenter.Listener.CIAirportListener;

import static ci.function.Core.Location.SSingleLocationUpdater.getRecentDistanceIndex;
import static ci.function.Core.Location.SSingleLocationUpdater.requestPermission;
import static ci.ui.TextField.CIChooseSearchDateTextFieldFragment.BUNDLE_PARA_BOOLEAN_IS_SINGLE;
/**
 * Created by kevincheng on 2016/3/30.
 * 頁面主要功能是提供訂票
 */
public class CIBookTicketActivity extends BaseActivity
    implements TwoItemNavigationBar.ItemClickListener,
               AdapterView.OnItemClickListener,
               View.OnClickListener,
               CIBookTicketPresenter.CallBack,
               CIBookTicketFlightInputFragment.onFragmentDeletedListener{

    private              NavigationBar                              m_Navigationbar              = null;
    private              CIBookTicketPresenter                      m_presenter                  = null;
    private              StepHorizontalView                         m_vStepHorizontalView        = null;
    private              TwoItemNavigationBar                       m_twoItemNavigationBar       = null;
    private              CIChooseAirportTextFieldFragment           m_fromFragment               = null;
    private              CIChooseAirportTextFieldFragment           m_toFragment                 = null;
    private              CIChooseSearchDateTextFieldFragment        m_departureDateFragment      = null;
    private              CIChooseSearchDateTextFieldFragment        m_returnDateFragment         = null;
    private              CIDropDownMenuTextFieldFragment            m_cabinFragment              = null;

    //20190412 新增企業會員欄位
    private              CIOnlyEnglishAndNumberTextFieldFragment    m_corporateidFragment          = null;

    private              CIOnlyEnglishAndNumberTextFieldFragment    m_promotionFragment          = null;
    private              FrameLayout                                m_flTwoItemNaviBar           = null;
    private              FrameLayout                                m_flReturnDate               = null;
    private              ImageView                                  m_ivAdultsDecrease           = null;
    private              ImageView                                  m_ivAdultsAdd                = null;
    //20190412 新增青少年欄位
    private              ImageView                                  m_ivYoungAdultsDecrease           = null;
    private              ImageView                                  m_ivYoungAdultsAdd                = null;

    private              ImageView                                  m_ivChildrenDecrease         = null;
    private              ImageView                                  m_ivChildrenAdd              = null;
    private              ImageView                                  m_ivInfantsDecrease          = null;
    private              ImageView                                  m_ivInfantsAdd               = null;
    private              Button                                     m_btnFindFlight              = null;
    private              LinearLayout                               m_llSingleFlightInfo         = null;
    private              LinearLayout                               m_llMultiFlightInfo          = null;
    private              LinearLayout                               m_llAddFlightInfo            = null;
    private              LinearLayout                               m_llMultiFlightContent       = null;
    private              LinearLayout                               m_llAddFlightAlertMsg        = null;
    private              ScrollView                                 m_scrollView                 = null;
    private              TextView                                   m_tvAdultsValue              = null;
    private              TextView                                   m_tvYoungAdultsValue              = null;
    private              TextView                                   m_tvChildrenValue            = null;
    private              TextView                                   m_tvInfantsValue             = null;
    private              TextView                                   m_tvPassengersMsg            = null;
    private              TextView                                   m_tvMultiCity                = null;
    private              TextView                                   m_tvBookingAndPassengerRules = null;
    private              TextView                                   m_tvPromotionCodeMsg         = null;
    private              TextView                                   m_tvAddFlight                = null;
    private              View                                       m_vGradient                  = null;
    private              LinearLayout                               m_rlPassengersMsg            = null;
    private              ArrayList<String>                          m_alString                   = null;
    private              ArrayList<Integer>                         m_alViewId                   = null;
    private              ArrayList<CIBookTicketFlightInputFragment> m_alFragment                 = null;
    private              Map<String, String>                        m_mapParams                  = null;
    private              CIAirportDataManager                       m_manager                    = null;
    private              CISelectDepartureAirportPresenter          m_selectAirportPresenter     = null;
    private              List<CIFlightStationEntity>                m_allDeparture               = null;
    private              EMode                                      m_mode                       = EMode.Base;
    private              String                                     m_errorMsg                   = "";
    private              GpsReceiver                                m_GpsReceiver                = null;
    private              CIFlightStationEntity                      m_gpsData                    = null;
    private              int                                        m_iAdultsTotal               = 0;
    private              int                                        m_iYoungAdultsTotal               = 0;
    private              int                                        m_iChildrenTotal             = 0;
    private              int                                        m_iInfantsTotal              = 0;
    private              int                                        m_iPassengerTotal            = 0;
    private              int                                        m_iFragmentCount             = 0;
    private              int                                        m_iFrameLayoutHeight         = 0;
    private              int                                        m_iScrollHeight              = 0;
    private              int                                        m_iCabinPosition             = 0;
    private              boolean                                    m_isSingle                   = false;
    private              boolean                                    m_isPassengerRuleComform     = true;
    private              boolean                                    m_isMulti                    = false;
    private              boolean                                    m_isPromoCodeValid           = false;
    private              boolean                                    m_isCorporateIdValid         = false;
    private              boolean                                    m_isPromoCodeNeedCheck       = false;
    private              String                                     m_PromoCodeValue             = "";
    private              String                                     m_CorporateIdValue             = "";
    private static final int                                        TOTAL_STEP                   = 5;
    private static final int                                        MAX_TOTAL_NUMBER             = 9;
    private static final int                                        MIN_ADULTS_NUMBER            = 1;

    private static final int                                        MAX_FLIGHT                   = 6;
    private static final int                                        DEF_MULTI_FIRST_VIEW_ID      = 11;
    private static final String                                     DATE_FORMAT_POST             = "yyyyMMddHHmm";
    private final int                                               PERMISSIONS_REQUEST_CODE     = 1;

    enum EMode {
        Base, Multi_stop_flight
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (null != savedInstanceState) {
            m_mode = EMode.valueOf(savedInstanceState.getString(UiMessageDef.BUNDLE_ACTIVITY_MODE));
        }
        super.onCreate(savedInstanceState);

        if (EMode.Multi_stop_flight == m_mode) {
            changeToMultiStopFlightMode();
        }
    }

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return getString(R.string.book_ticket);
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
        public void onDemoModeClick() {
        }
    };

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_book_ticket;
    }

    @Override
    protected void initialLayoutComponent() {

        m_Navigationbar         = (NavigationBar) findViewById(R.id.toolbar);
        m_vStepHorizontalView   = (StepHorizontalView) findViewById(R.id.llayout_setp);
        m_flTwoItemNaviBar      = (FrameLayout) findViewById(R.id.fl_two_item_navigation_bar);
        m_flReturnDate          = (FrameLayout) findViewById(R.id.fl_return_date);
        m_vGradient             = findViewById(R.id.vGradient);

        m_tvMultiCity           = (TextView) findViewById(R.id.tv_multi_city);
        m_tvAddFlight           = (TextView) findViewById(R.id.tv_add_flight);
        m_tvBookingAndPassengerRules = (TextView) findViewById(R.id.tv_passengers_and_booking_rules_msg);
        m_tvPromotionCodeMsg    = (TextView) findViewById(R.id.tv_promotion_code_msg);

        //TODO: 因目前暫無資料源, 進入點移除, 畫面請勿刪掉, 避免日後要加回
        m_tvBookingAndPassengerRules.setVisibility(View.GONE);

        m_llSingleFlightInfo    = (LinearLayout) findViewById(R.id.ll_single_flight_info);
        m_llMultiFlightInfo     = (LinearLayout) findViewById(R.id.ll_multi_flight_info);
        m_llMultiFlightContent  = (LinearLayout) findViewById(R.id.ll_multi_flight_info_content);
        m_llAddFlightInfo       = (LinearLayout) findViewById(R.id.ll_add_flight);
        m_llAddFlightAlertMsg   = (LinearLayout) findViewById(R.id.rl_flight_alert_msg);
        m_scrollView            = (ScrollView) findViewById(R.id.scrollView);

        /*乘客人數UI*/
        m_ivAdultsDecrease      = (ImageView) findViewById(R.id.iv_adults_decrease);
        m_ivAdultsAdd           = (ImageView) findViewById(R.id.iv_adults_add);

        m_ivYoungAdultsDecrease      = (ImageView) findViewById(R.id.iv_young_adults_decrease);
        m_ivYoungAdultsAdd           = (ImageView) findViewById(R.id.iv_young_adults_add);

        m_ivChildrenDecrease    = (ImageView) findViewById(R.id.iv_children_decrease);
        m_ivChildrenAdd         = (ImageView) findViewById(R.id.iv_children_add);
        m_ivInfantsDecrease     = (ImageView) findViewById(R.id.iv_infants_decrease);
        m_ivInfantsAdd          = (ImageView) findViewById(R.id.iv_infants_add);
        m_btnFindFlight         = (Button) findViewById(R.id.btn_find_flights);
        m_tvAdultsValue         = (TextView) findViewById(R.id.tv_adults_value);

        m_tvYoungAdultsValue         = (TextView) findViewById(R.id.tv_young_adults_value);

        m_tvChildrenValue       = (TextView) findViewById(R.id.tv_children_value);
        m_tvInfantsValue        = (TextView) findViewById(R.id.tv_infants_value);

        /*初始化乘客人數*/
        m_tvAdultsValue     .setText(String.valueOf(m_iAdultsTotal));
        m_tvYoungAdultsValue     .setText(String.valueOf(m_iYoungAdultsTotal));
        m_tvChildrenValue   .setText(String.valueOf(m_iChildrenTotal));
        m_tvInfantsValue    .setText(String.valueOf(m_iInfantsTotal));

        /*乘客人數超出限制提示UI*/
        m_rlPassengersMsg = (LinearLayout) findViewById(R.id.rl_passengers_msg);
        m_tvPassengersMsg = (TextView) findViewById(R.id.tv_passengers_msg);

        m_alFragment = new ArrayList<>();
        m_alString = new ArrayList<>();
        m_alViewId = new ArrayList<>();
        String[] cabin = getResources().getStringArray(R.array.book_ticket_cabin_list);
        Collections.addAll(m_alString, cabin);

        m_twoItemNavigationBar  = TwoItemNavigationBar.newInstance(getString(R.string.round_trip),getString(R.string.one_way));
        m_fromFragment          = CIChooseAirportTextFieldFragment.newInstance(
                getString(R.string.from),
                R.drawable.ic_departure_2,
                R.drawable.ic_departure_4,
                false,
                CISelectDepartureAirpotActivity.BOOKT_TICKET_ISOriginal_Y);
        m_toFragment            = CIChooseAirportTextFieldFragment.newInstance(
                getString(R.string.to),
                R.drawable.ic_arrival_2,
                R.drawable.ic_arrival_4,
                true,
                CISelectDepartureAirpotActivity.BOOKT_TICKET);
        m_departureDateFragment = CIChooseSearchDateTextFieldFragment.newInstance(getString(R.string.departure_date), m_isSingle, true);
        m_returnDateFragment    = CIChooseSearchDateTextFieldFragment.newInstance(getString(R.string.return_date), m_isSingle, false);
        m_cabinFragment         = CIDropDownMenuTextFieldFragment.newInstance(getString(R.string.cabin),R.array.book_ticket_cabin_list );
        m_promotionFragment     = CIOnlyEnglishAndNumberTextFieldFragment.newInstance(getString(R.string.bookticket_promotion_code));

        m_corporateidFragment     = CIOnlyEnglishAndNumberTextFieldFragment.newInstance(getString(R.string.bookticket_corporate_id_code));


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_two_item_navigation_bar, m_twoItemNavigationBar);
        transaction.replace(R.id.fl_from, m_fromFragment);
        transaction.replace(R.id.fl_to, m_toFragment);
        transaction.replace(R.id.fl_departure_date, m_departureDateFragment);
        transaction.replace(R.id.fl_return_date, m_returnDateFragment);
        transaction.replace(R.id.fl_economy, m_cabinFragment);
        transaction.replace(R.id.fl_promotion_code, m_promotionFragment);

        transaction.replace(R.id.fl_corporate_id, m_corporateidFragment);

        transaction.commit();

        /*初始化人數增減按鈕，且不顯示警示*/
        isOverPassengerRuleLimit(null);

        m_mapParams = new LinkedHashMap<>();

        m_presenter = new CIBookTicketPresenter(this);

        m_selectAirportPresenter = new CISelectDepartureAirportPresenter(m_selectAirportListener);

        m_manager = new CIAirportDataManager(m_managerCallBack);
        //取得機場資料
        m_manager.fetchAirportData(false, "", CISelectDepartureAirpotActivity.BOOKT_TICKET);

        //初始化先將成人加一
        m_iAdultsTotal = addPassengerValue(m_iAdultsTotal, m_tvAdultsValue);
        //2017-02-09 CR 成人旅客不可少於0
        m_ivAdultsDecrease.setEnabled(false);
        m_ivAdultsDecrease.setBackgroundResource(R.drawable.btn_decrease_d);
        isOverPassengerRuleLimit(m_ivAdultsAdd);

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                m_cabinFragment.setText(m_alString.get(m_iCabinPosition));
                m_promotionFragment.setImeOptions(EditorInfo.IME_ACTION_DONE);
                m_corporateidFragment.setImeOptions(EditorInfo.IME_ACTION_DONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        m_GpsReceiver = new GpsReceiver(m_GpsCallback);
        IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        registerReceiver(m_GpsReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        m_selectAirportPresenter.interrupt();
        m_manager.cancelTask();

        //註銷GPS開啟通知
        try {
            unregisterReceiver(m_GpsReceiver);
        } catch (Exception e){}

        m_GpsReceiver = null;
        m_selectAirportPresenter.interrupt();
    }

    /**
     * 當執行ActivityCompat.requestPermissions()後，會callbackonRequestPermissionsResult()
     * @param requestCode   requestPermissions()設定的requestCode
     * @param permissions   permission權限
     * @param grantResults  要求權限結果，如果等於[PackageManager.PERMISSION_GRANTED]就是同意
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    m_selectAirportPresenter.fetchLocation();

                } else {

                    CIToastView.makeText(this,getString(R.string.gps_permissions_msg)).show();
                }
                return;
            }

        }
    }

    GpsReceiver.Callback m_GpsCallback = new GpsReceiver.Callback(){
        @Override
        public void onGpsModeChangeReceive() {
            //當gps開啟會觸發
            m_selectAirportPresenter.interrupt();
            if (true == CIApplication.getSysResourceManager().isLocationServiceAvailable()
                    && true == requestPermission(CIBookTicketActivity.this)) {
                    m_selectAirportPresenter.fetchLocation();
            }
        }
    };

    CIAirportDataManager.callBack m_managerCallBack = new CIAirportDataManager.callBack(){
        @Override
        public void onDataBinded(List<CIFlightStationEntity> datas) {
            m_allDeparture = datas;
            m_selectAirportPresenter.interrupt();
            boolean bIsHavePermission = requestPermission(CIBookTicketActivity.this);
            boolean bIsLocationServiceAvailable = CIApplication.getSysResourceManager().isLocationServiceAvailable();
            if (true == bIsLocationServiceAvailable && true == bIsHavePermission) {
                m_selectAirportPresenter.fetchLocation();
            } else if(false == bIsLocationServiceAvailable && true == bIsHavePermission && !AppInfo.getInstance(m_Context).GetIsShowLocationService() ){
                CIToastView.makeText(CIBookTicketActivity.this, getString(R.string.gps_press_enable_gps_service)).show();
                //2018-06-22 新增GPS定位服務訊息僅顯示一次
                AppInfo.getInstance(m_Context).SetIsShowLocationService(true);
            }
        }

        @Override
        public void onDownloadFailed(String rt_msg) {
            showDialog(getString(R.string.warning), rt_msg);
        }

        @Override
        public void showProgress() {
            showProgressDialog(progressDlgListener);
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
        }
    };

    private CIProgressDialog.CIProgressDlgListener progressDlgListener = new CIProgressDialog.CIProgressDlgListener() {
        @Override
        public void onBackPressed() {
            CIBookTicketActivity.this.onBackPressed();
        }
    };

    private CIAirportListener m_selectAirportListener = new CIAirportListener() {
        @Override
        public void showProgress() {
            CIToastView.makeText(CIBookTicketActivity.this, getString(R.string.gps_positioning)).show();
        }

        @Override
        public void hideProgress() {

        }

        @Override
        public void onLocationBinded(Location location) {
            if(null == m_allDeparture || 0 >= m_allDeparture.size()){
                return;
            }

            List<SSingleLocationUpdater.LocationItem> locationDatas = new ArrayList<>();
            for(CIFlightStationEntity data:m_allDeparture){
                SSingleLocationUpdater.LocationItem locationItem = new SSingleLocationUpdater.LocationItem();
                try{
                    locationItem.Lat = Double.valueOf(data.latitude);
                    locationItem.Long = Double.valueOf(data.longitude);
                }catch (Exception e){
                    locationItem.Lat = 0.0;
                    locationItem.Long = 0.0;
                }
                locationDatas.add(locationItem);
            }

            //取得最近位置資料索引
            int index = getRecentDistanceIndex(location, locationDatas);
            m_gpsData = m_allDeparture.get(index);
            m_fromFragment.setIAIT(m_gpsData.IATA);
            String text = m_gpsData.localization_name + "(" + m_gpsData.IATA + ")";
            m_fromFragment.setText(text);
            m_toFragment.setIAIT("");
            m_toFragment.setText("");

            if(null != m_alFragment && 0 < m_alFragment.size()){
                m_alFragment.get(0).setFromIataAndText(m_gpsData.IATA, text);
                m_alFragment.get(0).setToIataAndText("","");
            }
        }

        @Override
        public void onfetchLocationFail() {
            CIToastView.makeText(CIBookTicketActivity.this,  getString(R.string.gps_position_fail)).show();
        }

        @Override
        public void onNoAvailableLocationProvider() {
            //2018-06-22 新增GPS定位服務訊息僅顯示一次
            if ( !AppInfo.getInstance(CIBookTicketActivity.this).GetIsShowLocationService() ){
                CIToastView.makeText(CIBookTicketActivity.this, getString(R.string.gps_press_enable_gps_service)).show();
                AppInfo.getInstance(CIBookTicketActivity.this).SetIsShowLocationService(true);
            }
        }
    };

    public void setIsSingle(Boolean isSingle) {
        this.m_isSingle = isSingle;
        //設定時刻表傳給行事曆之型態
        m_departureDateFragment.getArguments().putBoolean(BUNDLE_PARA_BOOLEAN_IS_SINGLE, isSingle);

        if (false == isSingle) {//是否為單程
            if(!TextUtils.isEmpty(m_returnDateFragment.getText()) &&
                    getReturnDepartureCalender().getTimeInMillis() < getDepartureCalender().getTimeInMillis()){
                m_returnDateFragment.setText(m_departureDateFragment.getText());
                setReturnDepartureCalender((Calendar) getDepartureCalender().clone());
            }
        }
    }

    public Calendar getDepartureCalender(){
        return m_departureDateFragment.getSelectDate();
    }

    public Calendar getReturnDepartureCalender(){
        return m_returnDateFragment.getSelectDate();
    }

    public void setReturnDepartureCalender(Calendar selectDate){
        m_returnDateFragment.setSelectDate(selectDate);
    }

    public String getToText(){
        return m_toFragment.getText();
    }

    public String getFromText(){
        return m_fromFragment.getText();
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_adults_add), 48, 32);
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_adults_decrease), 48, 32);

        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_young_adults_add), 48, 32);
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_young_adults_decrease), 48, 32);

        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_children_add), 48, 32);
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_children_decrease), 48, 32);
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_infants_add), 48, 32);
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_infants_decrease), 48, 32);
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_add), 24, 24);
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_travel_alerts), 24, 24);
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_flight_travel_alerts), 24, 24);
        m_vStepHorizontalView.getLayoutParams().height = vScaleDef.getLayoutHeight(39);
        m_tvPromotionCodeMsg.setTranslationY(-vScaleDef.getLayoutHeight(10));
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar         .uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        m_vStepHorizontalView   .initialView(TOTAL_STEP);
        m_twoItemNavigationBar  .setListener(this);
        m_ivAdultsDecrease      .setOnClickListener(this);
        m_ivAdultsAdd           .setOnClickListener(this);
        m_ivYoungAdultsDecrease      .setOnClickListener(this);
        m_ivYoungAdultsAdd           .setOnClickListener(this);
        m_ivChildrenDecrease    .setOnClickListener(this);
        m_ivChildrenAdd         .setOnClickListener(this);
        m_ivInfantsDecrease     .setOnClickListener(this);
        m_ivInfantsAdd          .setOnClickListener(this);
        m_tvMultiCity           .setOnClickListener(this);
        m_tvBookingAndPassengerRules.setOnClickListener(this);
        m_llAddFlightInfo       .setOnClickListener(this);
        m_btnFindFlight         .setOnClickListener(this);
        m_cabinFragment         .setOnItemClickListener(this);
        m_scrollView            .getViewTreeObserver().addOnScrollChangedListener(m_onScroll);
        m_tvBookingAndPassengerRules.setOnClickListener(this);

        /**
         * 點擊選擇日期欄位時，如果目的地欄位尚未選擇，則會跳出提示視窗；如果已經選擇，則要帶入出發地及目的地日期毫秒、出發地及目的地的代碼
         */
        CIChooseSearchDateTextFieldFragment.OnSearchDataTextFeildParams onSearchDataTextFeildParams
                = new CIChooseSearchDateTextFieldFragment.OnSearchDataTextFeildParams() {
            @Override
            public boolean isOpenSelectPage() {
                if (TextUtils.isEmpty(m_toFragment.getIAIT())) {
                    showDialog(getString(R.string.warning)
                            ,String.format(getString(R.string.please_input_field), getString(R.string.to))
                            //,getResources().getString(R.string.select_error_msg) + " " + getString(R.string.to)
                            ,getString(R.string.confirm));
                    return false;//false 就是不打開選擇機場頁面
                } else {
                    return true;
                }
            }

            @Override
            public long getDepartureDate() {
                return getDepartureCalender().getTimeInMillis();
            }

            @Override
            public long getRetureDate() {
                return getReturnDepartureCalender().getTimeInMillis();
            }

            @Override
            public String getDepartureAirport() {
                return getFromText();
            }

            @Override
            public String getDestinationAirport() {
                return getToText();
            }
        };

        m_departureDateFragment.setOnSearchDataTextFeildParams(onSearchDataTextFeildParams);
        m_returnDateFragment.setOnSearchDataTextFeildParams(onSearchDataTextFeildParams);
        m_fromFragment.setOnCIChooseAirportTextFragmentClick(onCIChooseAirportTextFragmentClickForFrom);
        m_toFragment.setOnCIChooseAirportTextFragmentClick(onCIChooseAirportTextFragmentClickForTo);

        /**
         * 監聽選擇出發地後欄位文字改變就初始化目的地欄位物件
         */
        m_fromFragment.setAfterTextChangedListener(new CIBaseTextFieldFragment.afterTextChangedListener() {
            @Override
            public void afterTextChangedListener(Editable editable) {
                m_toFragment.setText("");
                m_toFragment.setIAIT("");
            }
        });

    }
    /**
     * 出發地監聽，主要是點擊欄位時會被觸發，然後去設定「是否為目的地欄位 false 」、「取得出發地代碼」、「設定機場資料來源為BOOK_TICKET」
     */
    CIChooseAirportTextFieldFragment.OnCIChooseAirportTextFragmentClick onCIChooseAirportTextFragmentClickForFrom = new CIChooseAirportTextFieldFragment.OnCIChooseAirportTextFragmentClick() {
        @Override
        public boolean isOpenSelectPage() {
            return true;//false 就是不打開選擇機場頁面
        }

        @Override
        public String getFromIAIT() {
            return m_fromFragment.getIAIT();
        }
    };


    /**
     * 目的地監聽，主要是點擊欄位時會被觸發，如果出發地尚未被選擇，則會跳出提示視窗要求選擇；如果已經選擇出發地了，則會將跳出選單
     * ，且需設定「是否為目的地欄位 true 」、「取得出發地代碼」、「設定機場資料來源為BOOK_TICKET」
     */
    CIChooseAirportTextFieldFragment.OnCIChooseAirportTextFragmentClick onCIChooseAirportTextFragmentClickForTo = new CIChooseAirportTextFieldFragment.OnCIChooseAirportTextFragmentClick() {
        @Override
        public boolean isOpenSelectPage() {
            if ((m_fromFragment).getIAIT().equals("")) {

                showDialog(getString(R.string.warning)
                        ,String.format(getString(R.string.please_input_field), getString(R.string.from))
                        //,getResources().getString(R.string.select_error_msg) + " " + getString(R.string.from)
                        ,getString(R.string.confirm));

                return false;//false 就是不打開選擇機場頁面
            } else {
                return true;
            }
        }

        @Override
        public String getFromIAIT() {
            return m_fromFragment.getIAIT();
        }
    };

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

    @Override
    public void onItemClick(View v) {
        switch (v.getId()){
            case R.id.rl_left_bg:
                setIsSingle(false);
                m_flReturnDate.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_right_bg:
                setIsSingle(true);
                m_flReturnDate.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_adults_decrease:
                m_iAdultsTotal = decreasePassengerValue(m_iAdultsTotal, m_tvAdultsValue);
                isOverPassengerRuleLimit(v);
                break;
            case R.id.iv_adults_add:
                m_iAdultsTotal = addPassengerValue(m_iAdultsTotal, m_tvAdultsValue);
                isOverPassengerRuleLimit(v);
                break;

            case R.id.iv_young_adults_decrease:
                m_iYoungAdultsTotal = decreasePassengerValue(m_iYoungAdultsTotal, m_tvYoungAdultsValue);
                isOverPassengerRuleLimit(v);
                break;
            case R.id.iv_young_adults_add:
                m_iYoungAdultsTotal = addPassengerValue(m_iYoungAdultsTotal, m_tvYoungAdultsValue);
                isOverPassengerRuleLimit(v);
                break;

            case R.id.iv_children_decrease:
                m_iChildrenTotal = decreasePassengerValue(m_iChildrenTotal, m_tvChildrenValue);
                isOverPassengerRuleLimit(v);
                break;
            case R.id.iv_children_add:
                m_iChildrenTotal = addPassengerValue(m_iChildrenTotal, m_tvChildrenValue);
                isOverPassengerRuleLimit(v);
                break;
            case R.id.iv_infants_decrease:
                m_iInfantsTotal = decreasePassengerValue(m_iInfantsTotal, m_tvInfantsValue);
                isOverPassengerRuleLimit(v);
                break;
            case R.id.iv_infants_add:
                m_iInfantsTotal = addPassengerValue(m_iInfantsTotal, m_tvInfantsValue);
                isOverPassengerRuleLimit(v);
                break;
            case R.id.tv_multi_city:
                changeToMultiStopFlightMode();
                break;
            case R.id.tv_passengers_and_booking_rules_msg:
                changeActivity(CIPassengersAndBookingRulesActivity.class,null);
                break;
            case R.id.ll_add_flight:
                if(isFillRequiredFields()){
                    addMultiStopFlight();
                    m_llAddFlightAlertMsg.setVisibility(View.GONE);
                } else {
                    m_llAddFlightAlertMsg.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.btn_find_flights:
                findFlight();
                break;
        }
    }

    private void findFlight(){
        m_isCorporateIdValid = false;
        m_isPromoCodeValid = false;
        m_isPromoCodeNeedCheck = false;

        m_CorporateIdValue = "";
        m_PromoCodeValue = "";

        m_mapParams.clear();
        if(!isFillCompleteAndCorrect()){
            showDialog(getString(R.string.warning),
                       m_errorMsg);
            return;
        }


        if (null != m_corporateidFragment && null != m_promotionFragment) {
            m_CorporateIdValue = m_corporateidFragment.getText();
            m_PromoCodeValue = m_promotionFragment.getText();

            /*
            * 企業會員跟促銷代碼邏輯
            * 企業會員跟促銷代碼同時都有輸入：都要檢查，都正確則 token帶 promote code token，CRPID帶明碼
            * 只有輸入企業會員：token帶 企會 token，CRPID帶明碼
            * 只有輸入促銷代碼：token帶 promote code token，CRPID不要帶在參數裡
            * */

            if(!TextUtils.isEmpty(m_PromoCodeValue) && !TextUtils.isEmpty(m_CorporateIdValue)) {        //企會／促銷都有填
                m_isPromoCodeNeedCheck = true;
                m_presenter.fetchCoporateIdToken(m_CorporateIdValue);
            }else if(TextUtils.isEmpty(m_PromoCodeValue) && !TextUtils.isEmpty(m_CorporateIdValue)){    //企會有填
                m_presenter.fetchCoporateIdToken(m_CorporateIdValue);
            }else if(!TextUtils.isEmpty(m_PromoCodeValue) && TextUtils.isEmpty(m_CorporateIdValue)) {   //促銷有填
                m_presenter.fetchRromoteCodeToken(m_PromoCodeValue);
            }else{                                                                                       //企會／促銷都沒填
                m_presenter.fetchBookTicketWebData(getPostData());
            }
        }
    }

    private void changeActivity(Class clazz,Intent data) {
        if(null == data){
            data = new Intent();
        }
        data.setClass(m_Context, clazz);
        startActivity(data);

        //測試切換activity滑入滑出動畫
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    private void changeToMultiStopFlightMode(){
        m_isMulti = true;
        m_mode = EMode.Multi_stop_flight;
        m_flTwoItemNaviBar  .setVisibility(View.GONE);
        m_flReturnDate      .setVisibility(View.GONE);
        m_llSingleFlightInfo.setVisibility(View.GONE);
        m_llMultiFlightInfo .setVisibility(View.VISIBLE);
        addMultiStopFlight();
    }

    public int addPassengerValue(int passengerTotal, TextView textView){
        if(MAX_TOTAL_NUMBER > passengerTotal){
            passengerTotal++;
        }
        textView.setText(String.valueOf(passengerTotal));
        return passengerTotal;
    }

    public int decreasePassengerValue(int passengerTotal, TextView textView){
        if(0 < passengerTotal){
            passengerTotal--;
        }
        textView.setText(String.valueOf(passengerTotal));
        return passengerTotal;
    }

    public void isOverPassengerRuleLimit(View v){
        /**
         * 旅客人數規則：
         * 嬰兒旅客人數不可大於成人旅客人數
         * 小孩人數超過三人
         * 成人人數超過四人
         * 總人數不可超過七人
         *
         * 2017-02-09 CR 購票規則調整：
         * 至少1位成人數，旅客總人數(包括嬰兒)不可超過9人，嬰兒旅客人數不可大於成人人數
         *
         * 提示訊息顯示的模式類似Toast
         * 也就是說只有剛觸發的時候顯示
         * 之後如果有其他提示訊息被觸發
         * 則要顯示其他提示訊息
         *
         * 2019-04-12 新增青少年
         * 旅客總人數（成人+青少年+兒童+嬰兒)不超過9人
         * */
        int ViewId = 0;
        /**初始化*/
        if(null != v){
            ViewId = v.getId();
        }
        m_iPassengerTotal = m_iAdultsTotal + m_iYoungAdultsTotal + m_iChildrenTotal + m_iInfantsTotal;
        m_rlPassengersMsg.setVisibility(View.INVISIBLE);
        m_tvPassengersMsg.setText("");
        m_isPassengerRuleComform = true;
        m_ivAdultsAdd.setEnabled(true);
        m_ivYoungAdultsAdd.setEnabled(true);
        m_ivChildrenAdd.setEnabled(true);
        m_ivInfantsAdd.setEnabled(true);
        m_ivAdultsAdd.setBackgroundResource(R.drawable.btn_add_2_n);
        m_ivYoungAdultsAdd.setBackgroundResource(R.drawable.btn_add_2_n);
        m_ivChildrenAdd.setBackgroundResource(R.drawable.btn_add_2_n);
        m_ivInfantsAdd.setBackgroundResource(R.drawable.btn_add_2_n);
        m_ivInfantsDecrease.setEnabled(true);
        m_ivChildrenDecrease.setEnabled(true);
        m_ivAdultsDecrease.setEnabled(true);
        m_ivYoungAdultsDecrease.setEnabled(true);
        m_ivInfantsDecrease.setBackgroundResource(R.drawable.btn_decrease_n);
        m_ivChildrenDecrease.setBackgroundResource(R.drawable.btn_decrease_n);
        m_ivAdultsDecrease.setBackgroundResource(R.drawable.btn_decrease_n);
        m_ivYoungAdultsDecrease.setBackgroundResource(R.drawable.btn_decrease_n);

        if(0 >= m_iYoungAdultsTotal){
            m_ivYoungAdultsDecrease.setBackgroundResource(R.drawable.btn_decrease_d);
            m_ivYoungAdultsDecrease.setEnabled(false);
        }

        if(0 >= m_iInfantsTotal){
            m_ivInfantsDecrease.setBackgroundResource(R.drawable.btn_decrease_d);
            m_ivInfantsDecrease.setEnabled(false);
        }

        if(0 >= m_iChildrenTotal){
            m_ivChildrenDecrease.setBackgroundResource(R.drawable.btn_decrease_d);
            m_ivChildrenDecrease.setEnabled(false);
        }

        //2017-02-09 CR 成人旅客不可少於1
        if(MIN_ADULTS_NUMBER >= m_iAdultsTotal){
            m_ivAdultsDecrease.setBackgroundResource(R.drawable.btn_decrease_d);
            m_ivAdultsDecrease.setEnabled(false);
        }

        /**相等時提醒並disable增加按鈕*/
        if(m_iAdultsTotal <= m_iInfantsTotal ){
            if((ViewId == R.id.iv_adults_add || ViewId == R.id.iv_adults_decrease
                    || ViewId == R.id.iv_infants_add || ViewId == R.id.iv_infants_decrease)
                    && (m_iAdultsTotal > 0 || m_iInfantsTotal > 0)) {
                m_rlPassengersMsg.setVisibility(View.VISIBLE);
                m_tvPassengersMsg.setText(getString(R.string.book_ticket_max_number_of_infants));
            }
            m_ivInfantsAdd.setEnabled(false);
            m_ivInfantsAdd.setBackgroundResource(R.drawable.btn_add_2_d);
            m_ivAdultsDecrease.setEnabled(false);
            m_ivAdultsDecrease.setBackgroundResource(R.drawable.btn_decrease_d);
        }

        if(MAX_TOTAL_NUMBER <= m_iPassengerTotal){
            if(ViewId == R.id.iv_adults_add || ViewId == R.id.iv_adults_decrease
                    || ViewId == R.id.iv_young_adults_add || ViewId == R.id.iv_young_adults_decrease
                    || ViewId == R.id.iv_children_add || ViewId == R.id.iv_children_decrease
                    || ViewId == R.id.iv_infants_add || ViewId == R.id.iv_infants_decrease){
                m_rlPassengersMsg.setVisibility(View.VISIBLE);
                m_tvPassengersMsg.setText(getString(R.string.book_ticket_max_number_of_passengers));
            }
            m_ivAdultsAdd.setEnabled(false);
            m_ivYoungAdultsAdd.setEnabled(false);
            m_ivChildrenAdd.setEnabled(false);
            m_ivInfantsAdd.setEnabled(false);
            m_ivAdultsAdd.setBackgroundResource(R.drawable.btn_add_2_d);
            m_ivYoungAdultsAdd.setBackgroundResource(R.drawable.btn_add_2_d);
            m_ivChildrenAdd.setBackgroundResource(R.drawable.btn_add_2_d);
            m_ivInfantsAdd.setBackgroundResource(R.drawable.btn_add_2_d);
        }

        /**判斷是否有符合旅客人數規則*/
        if(m_iAdultsTotal < MIN_ADULTS_NUMBER
                || m_iAdultsTotal < m_iInfantsTotal
                || MAX_TOTAL_NUMBER < m_iPassengerTotal){
            m_isPassengerRuleComform = false;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        m_iCabinPosition = position;
        m_cabinFragment.setText(m_alString.get(position));
    }

    public void addMultiStopFlight(){
        //獲取目前count數
        m_iFragmentCount =  getCount();
        final int viewID = getViewId();
        //初始化frameLayout
        final FrameLayout layout = new FrameLayout(this);
        final ViewScaleDef viewScaleDef = ViewScaleDef.getInstance(this);
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if(2 <= m_iFragmentCount){
            params.setMargins(0,viewScaleDef.getLayoutHeight(24),0,0);
        }
        layout.setLayoutParams(params);
        layout.setId(viewID);
        m_llMultiFlightContent.addView(layout);

        Bundle bundle = new Bundle();
        if(m_iFragmentCount == 1){
            bundle.putBoolean(UiMessageDef.BUNDLE_BOOKING_ISORIGINAL_Y, true);
        }else{
            bundle.putBoolean(UiMessageDef.BUNDLE_BOOKING_ISORIGINAL_Y, false);
        }

        final CIBookTicketFlightInputFragment fragment = new CIBookTicketFlightInputFragment();

        fragment.setArguments(bundle);
        fragment.setOnFragmentDeletedListener(this);
        //使用supportFragment去加入fragment到framelayout
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, fragment, String.valueOf(viewID)).commit();
        m_alFragment.add(fragment);

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                if(null == fragment.getView()){return;}
                fragment.getView().post(new Runnable() {
                    @Override
                    public void run() {
                        m_iFrameLayoutHeight = layout.getHeight() + params.topMargin;
                        m_scrollView.smoothScrollTo(0, m_iFrameLayoutHeight * (m_iFragmentCount - 1));
                        setFragmentConfig(m_iFragmentCount, fragment);
                        Calendar date = getFlightDate();
                        String strDateFormat = AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddEEE(date);
                        fragment.setDepartureDateAndText(date, strDateFormat);
                        if(null != m_gpsData && DEF_MULTI_FIRST_VIEW_ID == viewID){
                            fragment.setFromIataAndText(m_gpsData.IATA, m_gpsData.localization_name + "(" + m_gpsData.IATA + ")" );
                        }
                    }
                });
            }
        });
        String str = String.format(getString(R.string.add_multi_flight), m_iFragmentCount, MAX_FLIGHT);
        m_tvAddFlight.setText(str);
        if(MAX_FLIGHT <= m_iFragmentCount){
            m_llAddFlightInfo.setVisibility(View.GONE);
        }
    }

    private void setFragmentConfig(int id, CIBookTicketFlightInputFragment fragment){
        fragment.setNumber(id);
        if(1 < id){
            fragment.setGarbageIconVisible(View.VISIBLE);
        } else {
            fragment.setGarbageIconVisible(View.GONE);
        }

    }

    private int getCount(){
        m_iFragmentCount++;
        return m_iFragmentCount;
    }

    @Override
    public void onFragmentDeleted(Fragment deletedfragment) {
        getSupportFragmentManager().beginTransaction().remove(deletedfragment).commit();
        releaseViewId(deletedfragment.getId());
        m_llMultiFlightContent.removeView(findViewById(deletedfragment.getId()));
        m_alFragment.remove(deletedfragment);
        int count = 1;
        for (CIBookTicketFlightInputFragment fragment : m_alFragment) {
            fragment.setNumber(count);
            count++;
        }

        //減少一個Fragment數量
        m_iFragmentCount--;
        //重新設定新增按鈕文字
        String str = String.format(getString(R.string.add_multi_flight), m_iFragmentCount, MAX_FLIGHT);
        m_tvAddFlight.setText(str);
        //判斷是否顯示新增按鈕文字
        if(MAX_FLIGHT > m_iFragmentCount){
            m_llAddFlightInfo.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 對View Id 做資源管理，避免使用到整數的極限值造成異常
     * @return view id
     */
    private int getViewId(){
        int iViewId = DEF_MULTI_FIRST_VIEW_ID;
        while(true){
            if(!m_alViewId.contains(iViewId)){
                break;
            }
            iViewId++;
        }
        m_alViewId.add(iViewId);
       SLog.d("viewId", iViewId + "");
        return iViewId;
    }

    /**
     * 釋放加入管理的view id，可再次被利用
     * @return view id
     */
    private void releaseViewId(Integer viewId){
        m_alViewId.remove(viewId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(UiMessageDef.BUNDLE_ACTIVITY_MODE, m_mode.name());
    }

    ViewTreeObserver.OnScrollChangedListener m_onScroll = new ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {

            int iCnt = m_scrollView.getChildCount();
            if (iCnt <= 0) {

            } else {
                m_iScrollHeight = m_scrollView.getChildAt(0).getHeight() - m_scrollView.getHeight();
            }

            //利用百分比決定 blurr 效果
            //減少分母, 提高Blur的效果
            float fAlpha = 0f;
            fAlpha = (float)m_scrollView.getScrollY() / m_iScrollHeight ;
            fAlpha = 1 - fAlpha;

            m_vGradient.setAlpha(fAlpha);
        }
    };

    private Calendar getFlightDate(){
        Calendar calendar = (Calendar)Calendar.getInstance().clone();
        int size = m_alFragment.size();
        if(1 >= size){
            return calendar;
        } else {
            calendar = (Calendar) m_alFragment.get(size - 2).getDepartureCalender().clone();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            return calendar;
        }
    }

    private String convertDateFormat(String format, Calendar date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date.getTime());
    }

    private boolean isFillCompleteAndCorrect(){
        if(false == m_isMulti){
            return isFillCompleteAndCorrectForNotMulti();
        } else {
            return isFillCompleteAndCorrectForMulti();
        }
    }

    /**
     * 判斷非多航段欄位是否填寫且是否正確
     * @return ture 代表是正確且必填都有填寫
     */
    private boolean isFillCompleteAndCorrectForNotMulti(){
        String      fromIAIT         = m_fromFragment.getIAIT();
        String      toIAIT           = m_toFragment.getIAIT();
        Calendar    departureDate    = adjustCalendarForDate(m_departureDateFragment.getSelectDate());
        Calendar    returnDate       = adjustCalendarForDate(m_returnDateFragment.getSelectDate());
        long        lDepartureDate   = departureDate.getTimeInMillis();
        long        lReturnDate      = returnDate.getTimeInMillis();
        String      strDepartureDate = convertDateFormat(DATE_FORMAT_POST,departureDate);
        String      strReturnDate    = convertDateFormat(DATE_FORMAT_POST,returnDate);

        if(false == m_isSingle){
            /*
            * 來回航班：
            * 判斷出發及回程日期是否有選擇，且出發日期毫秒不得大於回程日期
            * 判斷出發地及目的地機場是否有選擇
            * 判斷所有航段出發地機場是否不同於抵達機場
            * 乘客人數應大於零且符合乘客人數規則
            * */
            if(TextUtils.isEmpty(fromIAIT) || TextUtils.isEmpty(toIAIT)
                    || lDepartureDate <= 0 || lReturnDate <= 0){
                m_errorMsg = getString(R.string.please_fill_all_text_field_that_must_to_fill);
                return false;
            }

            if(fromIAIT.equals(toIAIT)){
                m_errorMsg = getString(R.string.book_ticket_error_5);
                return false;
            }

            if(lDepartureDate > lReturnDate){
                m_errorMsg = getString(R.string.book_ticket_error_6);
                return false;
            }

            if(m_iPassengerTotal <= 0){
                m_errorMsg = getString(R.string.book_ticket_error_3);
                return false;
            }

            if(false == m_isPassengerRuleComform){
                m_errorMsg = getString(R.string.book_ticket_error_4);
                return false;
            }

            generatePostDataForRoundTrip(fromIAIT,
                    toIAIT,
                    strDepartureDate,
                    strReturnDate);
            return true;
        } else {
            /*
            * 單程航班：
            * 判斷出發日期是否有選擇
            * 判斷出發地機場是否有選擇
            * 判斷所有航段出發地機場是否不同於抵達機場
            * 乘客人數應大於零且符合乘客人數規則
            * */

            if(TextUtils.isEmpty(fromIAIT) || TextUtils.isEmpty(toIAIT)
                    || lDepartureDate <= 0){
                m_errorMsg = getString(R.string.please_fill_all_text_field_that_must_to_fill);
                return false;
            }

            if(fromIAIT.equals(toIAIT)){
                m_errorMsg = getString(R.string.book_ticket_error_5);
                return false;
            }

            if(m_iPassengerTotal <= 0){
                m_errorMsg = getString(R.string.book_ticket_error_3);
                return false;
            }

            if(false == m_isPassengerRuleComform){
                m_errorMsg = getString(R.string.book_ticket_error_4);
                return false;
            }

            generatePostDataForOneWay(fromIAIT, toIAIT, strDepartureDate);
        }
        return true;
    }

    /**
     * 判斷多航段欄位是否填寫且是否正確
     * @return ture 代表是正確且必填都有填寫
     */
    private boolean isFillCompleteAndCorrectForMulti(){
        /*
         * 多個航班：
         * 判斷所有航段出發日期是否有選擇
         * 判斷所有航段出發地機場是否有選擇
         * 乘客人數應大於零且符合乘客人數規則
         * 判斷所有航段出發地機場是否不同於抵達機場
         * 判斷所有航段是否符合沒有相同的航段（ 始發地/目的地 ）
         * 判斷所有航段是否符合啟程時間晚於前一航班的到達時間
         * */

        if(false == isFillRequiredFields()){
            return false;
        }

        m_mapParams.put("IsMultiCity","Y");
        Calendar    departureDate    ;
        String      strDepartureDate ;
        List<String> fromList = new ArrayList<>();
        List<String> toList   = new ArrayList<>();
        long   lastData = 0;
        int count = 1;
        for (CIBookTicketFlightInputFragment fragment : m_alFragment) {
            String fromIAIT = fragment.getFromIAIT();
            String toIAIT   = fragment.getToIAIT();

            if(fromIAIT.equals(toIAIT)){
                m_errorMsg = getString(R.string.book_ticket_error_5);
                return false;
            }

            long data   = adjustCalendarForDate(fragment.getDepartureCalender()).getTimeInMillis();
            if(lastData > data){
                m_errorMsg = getString(R.string.book_ticket_error_1);
                return false;
            }

            for(String fromData: fromList){
                if(true == fromIAIT.equals(fromData)){
                    for(String toData: toList){
                        if(true == toIAIT.equals(toData)){
                            m_errorMsg = getString(R.string.book_ticket_error_2);
                            return false;
                        }
                    }
                }
            }

            fromList.add(fromIAIT);
            toList.add(toIAIT);
            lastData = data;

            departureDate       = adjustCalendarForDate(fragment.getDepartureCalender());
            strDepartureDate    = convertDateFormat(DATE_FORMAT_POST,departureDate);
            generatePostDataForMulti(fromIAIT, toIAIT, strDepartureDate, count);
            count++;
        }

        if(m_iPassengerTotal <= 0){
            m_errorMsg = getString(R.string.book_ticket_error_3);
            return false;
        }

        if(false == m_isPassengerRuleComform){
            m_errorMsg = getString(R.string.book_ticket_error_4);
            return false;
        }
        return true;

    }

    private boolean isFillRequiredFields(){
        for (CIBookTicketFlightInputFragment fragment : m_alFragment) {
            String from     = fragment.getFromIAIT();
            String to       = fragment.getToIAIT();
            long data       = adjustCalendarForDate(fragment.getDepartureCalender()).getTimeInMillis();

            if(TextUtils.isEmpty(from)
                    || TextUtils.isEmpty(to)
                    || data <= 0){
                m_errorMsg = getString(R.string.please_fill_all_text_field_that_must_to_fill);
                return false;
            }
        }
        return true;
    }

    private Calendar adjustCalendarForDate(Calendar cale){
        cale.set(Calendar.HOUR_OF_DAY, 0);
        cale.set(Calendar.MINUTE, 0);
        cale.set(Calendar.SECOND, 0);
        cale.set(Calendar.MILLISECOND, 0);
        return cale;
    }

    private void generatePostDataForRoundTrip(String from,String to,String depart,String reture){
        m_mapParams.put("B_LOCATION_1",from);
        m_mapParams.put("E_LOCATION_1",to);
        m_mapParams.put("B_LOCATION_2",to);
        m_mapParams.put("E_LOCATION_2",from);
        m_mapParams.put("TRIP_TYPE","R");
        m_mapParams.put("B_DATE_1",depart);
        m_mapParams.put("B_DATE_2",reture);
    }

    private void generatePostDataForOneWay(String from,String to,String depart){
        m_mapParams.put("B_LOCATION_1",from);
        m_mapParams.put("E_LOCATION_1",to);
        m_mapParams.put("TRIP_TYPE","O");
        m_mapParams.put("B_DATE_1",depart);
    }

    private void generatePostDataForMulti(String from, String to, String depart,int count){
        final String DEF_FROM = "B_LOCATION_";
        final String DEF_TO   = "E_LOCATION_";
        final String DEF_DEPART   = "B_DATE_";
        m_mapParams.put(DEF_FROM   + count,from);
        m_mapParams.put(DEF_TO     + count,to);
        m_mapParams.put(DEF_DEPART + count, depart);
    }

    private String getPostData(){
        /**
         * 文件：CA_I-001C_LateLogin_v3.0(mobile).docx
         * 版本：2016/05/12
         * postData = "B_LOCATION_1=TPE&E_LOCATION_1=HKG&TRIP_TYPE=O&ADULTS=1&CHILDS=0&INFANTS=0&LANG=TW&CABIN=Y&B_DATE_1=201609290000";
         * */

        Locale locale = CIApplication.getLanguageInfo().getLanguage_Locale();
        switch (locale.toString()){
            case "zh_TW":
                m_mapParams.put("LANG","TW");
                //2017-01-05 add by Ryan for 參照 mail "新增版號回傳MRQ  & booking portal 參數新增"
                // 新增post data EBA: 跟目前LANG相同值即可, SessionID: 用MRQ device ID為回傳值
                m_mapParams.put("EBA" ,"TW");
                break;
            case "zh_CN":
                m_mapParams.put("LANG","CN");
                //2017-01-05 add by Ryan for 參照 mail "新增版號回傳MRQ  & booking portal 參數新增"
                // 新增post data EBA: 跟目前LANG相同值即可, SessionID: 用MRQ device ID為回傳值
                m_mapParams.put("EBA" ,"CN");
                break;
            case "en":
                m_mapParams.put("LANG","GB");
                //2017-01-05 add by Ryan for 參照 mail "新增版號回傳MRQ  & booking portal 參數新增"
                // 新增post data EBA: 跟目前LANG相同值即可, SessionID: 用MRQ device ID為回傳值
                m_mapParams.put("EBA" ,"GB");
                break;
            case "ja_JP":
                m_mapParams.put("LANG","JP");
                //2017-01-05 add by Ryan for 參照 mail "新增版號回傳MRQ  & booking portal 參數新增"
                // 新增post data EBA: 跟目前LANG相同值即可, SessionID: 用MRQ device ID為回傳值
                m_mapParams.put("EBA" ,"JP");
                break;
        }

        /**2016-11-24 Ryan, 訂票參數
         * 商務艙 - C
         * 豪華經濟 - W
         * 經濟艙 - Y
         * 對應選擇字串檔
         * R.string.book_ticket_cabin_list*/
        switch (m_iCabinPosition){
            case 0:
                m_mapParams.put("CABIN","Y");
                break;
            case 1:
                m_mapParams.put("CABIN","W");
                //m_mapParams.put("CABIN","PY");
                break;
            case 2:
                m_mapParams.put("CABIN","C");
                break;
        }

        m_mapParams.put("ADULTS" , String.valueOf(m_iAdultsTotal));

        m_mapParams.put("YOUNGADTS" , String.valueOf(m_iYoungAdultsTotal));

        m_mapParams.put("CHILDS" ,String.valueOf(m_iChildrenTotal));
        m_mapParams.put("INFANTS" ,String.valueOf(m_iInfantsTotal));
        //2016-11-16 add by Ryan for 新增post data Channel=ANDROID
        m_mapParams.put("Channel" ,"ANDROID");

        //2017-08-30 modify by kevin 調整使用android id to md5 前取 30 字元 作為 session id
        String androidId    = CIApplication.getDeviceInfo().getAndroidId();
        String sessionId    = Encryption.getInstance().MD5(androidId).substring(0, 30);
        m_mapParams.put("SessionID" , sessionId);

        CILoginInfo info = CIApplication.getLoginInfo();
        if(info.isDynastyFlyerMember()) {
            m_mapParams.put("DFPNo" , info.GetUserMemberCardNo());
            m_mapParams.put("DFPToken" , info.GetMemberToken());
        }

        String postData ="";
        Iterator<Map.Entry<String, String>> iter = m_mapParams.entrySet().iterator();
        int count = 0;
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            String key = entry.getKey();
            String val = entry.getValue();
            if(0 == count){
                postData+= key+"="+val;
            } else {
                postData+= "&"+key+"="+val;
            }
            count++;
        }

        SLog.d("postData: "+postData);
        return postData;
    }

    @Override
    public void showProgress() {
        showProgressDialog(progressDlgListener);
        m_btnFindFlight.setEnabled(false);
    }

    @Override
    public void hideProgress() {
        hideProgressDialog();
        m_btnFindFlight.setEnabled(true);
    }

    @Override
    public void onDataBinded(String webData) {
        Intent data = new Intent();
        data.putExtra(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG, getString(R.string.book_ticket));
        data.putExtra(UiMessageDef.BUNDLE_WEBVIEW_WEB_DATA_TAG, webData);
        data.putExtra(UiMessageDef.BUNDLE_WEBVIEW_WEB_IS_SHOW_CLOSE_BTN_TAG, true);
        changeActivity(CIWithoutInternetActivity.class,data);
    }

    @Override
    public void  onCoporateIdTokenBinded(CIInquiryCoporateIdTokenResp data) {
        if(null != data) {
            m_mapParams.put("CRPID", m_CorporateIdValue);
            m_isCorporateIdValid = true;
        } else {
            m_isCorporateIdValid = false;
        }

        if (m_isPromoCodeNeedCheck) {
            m_presenter.fetchRromoteCodeToken(m_PromoCodeValue);
        }else{
            m_mapParams.put("Token",data.token);

            m_presenter.fetchBookTicketWebData(getPostData());
        }
    }

    @Override
    public void onPromoteCodeTokenBinded(CIInquiryPromoteCodeTokenResp data) {
        if(null != data) {
            m_mapParams.put("Token",data.token);

            m_isPromoCodeValid = true;
        }else{
            m_isPromoCodeValid = false;
        }

        if (m_isPromoCodeValid){
            m_presenter.fetchBookTicketWebData(getPostData());
        }
    }

    @Override
    public void onDataFetchFeild(String msg) {
        showDialog(getString(R.string.warning), msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_scrollView.getViewTreeObserver().removeOnScrollChangedListener(m_onScroll);
    }
}
