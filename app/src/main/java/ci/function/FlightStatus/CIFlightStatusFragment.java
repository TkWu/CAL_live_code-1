package ci.function.FlightStatus;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import ci.function.Core.SLog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.Core.Location.GpsReceiver;
import ci.function.Core.Location.SSingleLocationUpdater;
import ci.ui.TimeTable.CITimeTableFlightStatusDetailList;
import ci.ui.object.AppInfo;
import ci.ws.Presenter.Listener.CIAirportListener;
import ci.ws.Presenter.CISelectDepartureAirportPresenter;
import ci.function.TimeTable.CISelectDepartureAirpotActivity;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.CIAirportDataManager;
import ci.ui.object.CIProgressDialog;
import ci.ui.toast.CIToastView;
import ci.ui.view.TwoItemNavigationBar;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Models.entities.CIFlightStatusReq;
import ci.ws.Models.entities.CIFlightStatusResp;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Presenter.CIInquiryFlightStatusPresenter;
import ci.ws.Presenter.Listener.CIInquiryFlightStatusListener;
import ci.ws.cores.object.CIWSCommon;

import static ci.function.Core.Location.SSingleLocationUpdater.getRecentDistanceIndex;
import static ci.function.Core.Location.SSingleLocationUpdater.requestPermission;

/**
 * Created by flowmahuang on 2016/3/28.
 */
public class CIFlightStatusFragment extends BaseFragment implements
        View.OnTouchListener,
        TwoItemNavigationBar.ItemClickListener,
        View.OnClickListener {
    private FrameLayout                         m_flSelect                  = null;
    private FrameLayout                         m_flContent                 = null;
    private FrameLayout                         m_flSelectDate              = null;
    private CIFlightStatusByFlightFragment      m_ByFlightFragment          = null;
    private CIFlightStatusByRouteFragment       m_ByRouteFragment           = null;
    private CISelectDateEightDayFragment        m_SelectDate                = null;
    private RelativeLayout                      m_outsideRelativeLayout     = null;
    private RelativeLayout                      m_RadioDeparture            = null;
    private RelativeLayout                      m_RadioArrival              = null;
    private Button                              m_SearchButton              = null;
    //用於從詳細行程頁面點擊「航班動態」後所需要接的資料 edited by Kevin
    private TwoItemNavigationBar                m_bar                       = null;

    private CIAirportDataManager                m_manager                   = null;
    private CISelectDepartureAirportPresenter   m_selectAirportPresenter    = null;
    private List<CIFlightStationEntity>         m_allDeparture              = null;
    private GpsReceiver                         m_GpsReceiver               = null;
    private static CITripListResp_Itinerary     s_ItineraryInfoData         = null;

    private boolean                             m_bFragment;
    private final int                           PERMISSIONS_REQUEST_CODE    = 1;
    private static final int                    FROM_FLIGHT_STATUS          = CITimeTableFlightStatusDetailList.DEF_VIEW_TYPE_FIGHT_STATUS;
    private CIInquiryFlightStatusPresenter      flightStatusPresenter       = null;
    private String                              data                        = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_flight_status;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_flSelect = (FrameLayout) view.findViewById(R.id.fl_select);
        m_flContent = (FrameLayout) view.findViewById(R.id.fl_content);
        m_flSelectDate = (FrameLayout) view.findViewById(R.id.fl_date_select);
        m_SearchButton = (Button) view.findViewById(R.id.btn_to_search_time);
        m_SearchButton.setVisibility(View.GONE);
        m_RadioDeparture = (RelativeLayout) view.findViewById(R.id.rlayout_departure_date);
        m_RadioArrival = (RelativeLayout) view.findViewById(R.id.rlayout_arrival_date);
        m_outsideRelativeLayout = (RelativeLayout) view.findViewById(R.id.root);

        m_selectAirportPresenter = new CISelectDepartureAirportPresenter(m_selectAirportListener);
        m_manager = new CIAirportDataManager(m_managerCallBack);
    }

    @Override
    public void onFragmentResume() {
        if(null == m_GpsReceiver){
            m_GpsReceiver = new GpsReceiver(m_GpsCallback);
        }
        IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        getActivity().registerReceiver(m_GpsReceiver, filter);
    }

    @Override
    public void onFragmentPause() {
        if (flightStatusPresenter != null) {
            flightStatusPresenter.InquiryFlightStatusCancel();
        }



        //註銷GPS開啟通知
        try{
            getActivity().unregisterReceiver(m_GpsReceiver);
            m_selectAirportPresenter.interrupt();
            m_manager.cancelTask();
        }catch (Exception e){}
        m_GpsReceiver = null;

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

                    CIToastView.makeText(getContext() ,getString(R.string.gps_permissions_msg)).show();
                }
                return;
            }

        }
    }

    GpsReceiver.Callback m_GpsCallback = new GpsReceiver.Callback(){
        @Override
        public void onGpsModeChangeReceive() {
            if(null == m_selectAirportPresenter){
                return;
            }
            //當gps開啟會觸發
            m_selectAirportPresenter.interrupt();
            if (true == CIApplication.getSysResourceManager().isLocationServiceAvailable()
                    && true == requestPermission(getContext())) {
                m_selectAirportPresenter.fetchLocation();
            }
        }
    };

    CIAirportDataManager.callBack m_managerCallBack = new CIAirportDataManager.callBack(){
        @Override
        public void onDataBinded(List<CIFlightStationEntity> datas) {
            m_allDeparture = datas;
            m_selectAirportPresenter.interrupt();
            boolean bIsLocationServiceAvailable = CIApplication.getSysResourceManager().isLocationServiceAvailable();
            boolean bIsHavePermission = requestPermission(CIFlightStatusFragment.this);
            if (true == bIsLocationServiceAvailable && true == bIsHavePermission) {
                m_selectAirportPresenter.fetchLocation();
            } else if (false == bIsLocationServiceAvailable && true == bIsHavePermission && !AppInfo.getInstance(getContext()).GetIsShowLocationService() ){
                CIToastView.makeText(getContext(), getString(R.string.gps_press_enable_gps_service)).show();
                //2018-06-22 新增GPS定位服務訊息僅顯示一次
                AppInfo.getInstance(getContext()).SetIsShowLocationService(true);
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
            if (null != m_manager) {
                m_manager.cancelTask(); //by kevin
            }
        }
    };

    private CIAirportListener m_selectAirportListener = new CIAirportListener() {
        @Override
        public void showProgress() {
            CIToastView.makeText(getContext(), getString(R.string.gps_positioning)).show();
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
            final CIFlightStationEntity data = m_allDeparture.get(index);
            final String text = data.localization_name + "(" + data.IATA + ")";
            if(null != m_ByRouteFragment){
                m_ByRouteFragment.setFromFragmentIAIT(data.IATA);
                m_ByRouteFragment.setFromFragmentText(text);
                m_ByRouteFragment.setToFragmentIAIT("");
                m_ByRouteFragment.setToFragmentText("");
            }
        }

        @Override
        public void onfetchLocationFail() {
            CIToastView.makeText(getContext(),  getString(R.string.gps_position_fail)).show();
        }

        @Override
        public void onNoAvailableLocationProvider() {
            if ( !AppInfo.getInstance(getContext()).GetIsShowLocationService() ){
                CIToastView.makeText(getContext(), getString(R.string.gps_press_enable_gps_service)).show();
                //2018-06-22 新增GPS定位服務訊息僅顯示一次
                AppInfo.getInstance(getContext()).SetIsShowLocationService(true);
            }
        }
    };


    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root));
//        vScaleDef.setMargins(m_flSelect, 20, 20, 20, 0);
    }

    @Override
    protected void setOnParameterAndListener(View view) {
        m_SearchButton.setOnClickListener(this);
        m_outsideRelativeLayout.setOnTouchListener(this);
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
    public void onLanguageChangeUpdateUI() {}

    @Override
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.rl_left_bg:
                if (true == m_bFragment)
                    return;

                m_bFragment = true;
                ChangeFragment();
                break;
            case R.id.rl_right_bg:
                if (false == m_bFragment)
                    return;

                m_bFragment = false;
                ChangeFragment();
                break;
        }
    }

    private void ChangeFragment() {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if ( true == m_bFragment ){
            fragmentTransaction.show(m_ByRouteFragment);
            fragmentTransaction.hide(m_ByFlightFragment);
//            fragmentTransaction.replace(R.id.fl_content, m_ByRouteFragment);
        }else {
            fragmentTransaction.hide(m_ByRouteFragment);
            fragmentTransaction.show(m_ByFlightFragment);
//            fragmentTransaction.replace(R.id.fl_content, m_ByFlightFragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void changeActivity(Class clazz) {
        final Calendar c = Calendar.getInstance();
        //獲得選擇日期
        c.add(Calendar.DAY_OF_MONTH, m_SelectDate.getSelectDateCheck() - 2);
        //2017-08-01 統一時間邏輯
        Date d = c.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Intent intent = new Intent();
        intent.putExtra(CIFlightResultActivity.BUNDEL_PARA_DEPARTDATE, formatter.format(d));
        //intent.putExtra(CIFlightResultActivity.BUNDEL_PARA_DEPARTDATE, AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddEEE(c));
        intent.putExtra(CIFlightResultActivity.BUNDLE_PARA_FROM_VIEW, FROM_FLIGHT_STATUS);
        intent.putExtra(CIFlightResultActivity.BUNDEL_PARA_RETURN_OR_NOT, m_bFragment);
        intent.putExtra(CIFlightResultActivity.BUNDEL_PARA_SEARCHWAY, m_SelectDate.getRadioCheck());
        intent.putExtra(CIFlightResultActivity.BUNDEL_PARA_FLIGHT_DATA, data);
        intent.putExtra(CIFlightResultActivity.BUNDEL_PARA_FLIGHT_FROM_CODE, m_ByRouteFragment.getFromFragmentIAIT());
        intent.putExtra(CIFlightResultActivity.BUNDEL_PARA_FLIGHT_TO_CODE, m_ByRouteFragment.getToFragmentIAIT());
        intent.setClass(getContext(), clazz);

        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    @Override
    public void onClick(View v) {
//        if (m_SelectDate.getSelectDateCheck() == 0 || !m_ByRouteFragment.checkFromToInput() ||!m_ByFlightFragment.checkNumInput()){
//            CIAlertDialog dialog = new CIAlertDialog(getContext(), new CIAlertDialog.OnAlertMsgDialogListener() {
//                @Override
//                public void onAlertMsgDialog_Confirm() {
//
//                }
//
//                @Override
//                public void onAlertMsgDialogg_Cancel() {
//
//                }
//            });
//            dialog.uiSetConfirmText(getString(R.string.yes));
//            dialog.uiSetContentText("請選擇選項。");
//            dialog.show();
//        }
//        else {
//            if (m_bFragment) {
//                if (m_ByRouteFragment.checkFromToInput())
//                    changeActivity(CIFlightResultActivity.class);
//            } else if (!m_bFragment) {
//                if (m_ByFlightFragment.checkNumInput())
//                    changeActivity(CIFlightResultActivity.class);
//            }
//        }
        CIAlertDialog dialog = new CIAlertDialog(getContext(), new CIAlertDialog.OnAlertMsgDialogListener() {
            @Override
            public void onAlertMsgDialog_Confirm() {
            }

            @Override
            public void onAlertMsgDialogg_Cancel() {
            }
        });
        dialog.uiSetTitleText(getString(R.string.warning));
        dialog.uiSetConfirmText(getString(R.string.confirm));

        if (m_bFragment) {
            if (m_ByRouteFragment.getFromFragmentIAIT().equals("")) {

                //2016-11-09 Ryan , 更換錯誤訊息顯示方式
                dialog.uiSetContentText(String.format(getResources().getString(R.string.please_input_field), getResources().getString(R.string.from)));
                //dialog.uiSetContentText(getResources().getString(R.string.select_error_msg) + " " + getResources().getString(R.string.from));
                dialog.show();
            } else {
                if (m_ByRouteFragment.getToFragmentIAIT().equals("")) {

                    //2016-11-09 Ryan , 更換錯誤訊息顯示方式
                    dialog.uiSetContentText(String.format(getResources().getString(R.string.please_input_field), getResources().getString(R.string.to)));
                    //dialog.uiSetContentText(getResources().getString(R.string.select_error_msg) + " " + getResources().getString(R.string.to));
                    dialog.show();
                } else {
                    setFlightStatusApi();
                }
            }
        } else {
            if (m_ByFlightFragment.getFlightNumber().equals("")) {
                //2016-11-09 Ryan , 更換錯誤訊息顯示方式
                dialog.uiSetContentText(String.format(getResources().getString(R.string.please_input_field), getResources().getString(R.string.flight_number)));
                //dialog.uiSetContentText(getResources().getString(R.string.select_error_msg) + " " + getResources().getString(R.string.flight_number));
                dialog.show();
            } else {
                setFlightStatusApi();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (R.id.root == v.getId()) {
            HidekeyBoard();
        }
        return false;
    }

    //Call API
    private void setFlightStatusApi() {

        final Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, m_SelectDate.getSelectDateCheck() - 2);
        //Date d = c.getTime();
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        CIFlightStatusReq flightStatusReq = new CIFlightStatusReq();
//        判斷以路線或是Number查詢
//         if m_bFragment= true  路線查詢
//         if m_bFragment= true   Number 查詢
        if (m_bFragment) {
            flightStatusReq.search_type = CIFlightStatusReq.BY_ROUTE;
            flightStatusReq.departure_station = m_ByRouteFragment.getFromFragmentIAIT();
            flightStatusReq.arrival_station = m_ByRouteFragment.getToFragmentIAIT();
        } else {
            flightStatusReq.search_type = CIFlightStatusReq.BY_FLIGHT;
            flightStatusReq.flight_number = m_ByFlightFragment.getFlightNumber();
            flightStatusReq.flight_carrier = m_ByFlightFragment.getSearchCIorAE() == true ? CIFlightStatusReq.FLIGHT_ARRIER_CI : CIFlightStatusReq.FLIGHT_ARRIER_AE;
        }
        flightStatusReq.by_depature_arrival_date = m_SelectDate.getRadioCheck() == true ? CIFlightStatusReq.DEPARTURE_DATE : CIFlightStatusReq.ARRIVAL_DATE;
        flightStatusReq.flight_date = CIWSCommon.ConvertDatetoWSFormat(c);//formatter.format(d);

       SLog.v("result", flightStatusReq.search_type);
       SLog.v("result", flightStatusReq.departure_station);
       SLog.v("result", flightStatusReq.arrival_station);
       SLog.v("result", flightStatusReq.flight_number);
       SLog.v("result", flightStatusReq.flight_carrier);
       SLog.v("result", flightStatusReq.by_depature_arrival_date);
       SLog.v("result", flightStatusReq.flight_date);

        flightStatusPresenter = new CIInquiryFlightStatusPresenter(new CIInquiryFlightStatusListener() {
            @Override
            public void onFlightStatusSuccess(String rt_code, String rt_msg, final CIFlightStatusResp flightStatusResp) {
                Gson gson = new Gson();
                data = gson.toJson(flightStatusResp.arFlightList);

                changeActivity(CIFlightResultActivity.class);
            }

            @Override
            public void onFlightStatusError(String rt_code, String rt_msg) {
                CIAlertDialog dialog = new CIAlertDialog(getActivity(), new CIAlertDialog.OnAlertMsgDialogListener() {
                    @Override
                    public void onAlertMsgDialog_Confirm() {

                    }

                    @Override
                    public void onAlertMsgDialogg_Cancel() {

                    }
                });
                dialog.uiSetTitleText(getString(R.string.warning));
                dialog.uiSetConfirmText(getString(R.string.confirm));
                dialog.uiSetContentText(rt_msg);
                dialog.show();
            }

            @Override
            public void showProgress() {
                showProgressDialog(new CIProgressDialog.CIProgressDlgListener() {
                    @Override
                    public void onBackPressed() {
                        if (flightStatusPresenter != null) {
                            flightStatusPresenter.InquiryFlightStatusCancel();
                        }
                    }
                });
            }

            @Override
            public void hideProgress() {
                hideProgressDialog();
            }
        });
        flightStatusPresenter.InquiryFlightStatusFromWS(flightStatusReq);
    }

    @Override
    public void onFragmentShow() {
        super.onFragmentShow();

        if(null == m_GpsReceiver){
            m_GpsReceiver = new GpsReceiver(m_GpsCallback);
        }
        IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        getActivity().registerReceiver(m_GpsReceiver, filter);

        m_bFragment = true;
        m_SelectDate = new CISelectDateEightDayFragment();
        m_SearchButton.setText(R.string.search);
        m_bar = TwoItemNavigationBar.newInstance(
                getString(R.string.by_route),
                getString(R.string.by_flight));
        m_bar.setListener(CIFlightStatusFragment.this);

        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_select, m_bar);
        fragmentTransaction.commitAllowingStateLoss();

        m_FragmentHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentManager manager = getChildFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                String routeFragmentTag = CIFlightStatusByRouteFragment.class.getSimpleName();
                String flightFragmentTag = CIFlightStatusByFlightFragment.class.getSimpleName();
                m_ByRouteFragment = (CIFlightStatusByRouteFragment)manager.findFragmentByTag(routeFragmentTag);
                m_ByFlightFragment = (CIFlightStatusByFlightFragment)manager.findFragmentByTag(flightFragmentTag);
                if ( null != m_ByRouteFragment){
                    transaction.remove(m_ByRouteFragment);
                }
                if ( null != m_ByFlightFragment){
                    transaction.remove(m_ByFlightFragment);
                }
                m_ByRouteFragment = new CIFlightStatusByRouteFragment();
                m_ByFlightFragment = new CIFlightStatusByFlightFragment();
                transaction.add(R.id.fl_content, m_ByRouteFragment, routeFragmentTag).show(m_ByRouteFragment);
                transaction.add(R.id.fl_content, m_ByFlightFragment, flightFragmentTag).hide(m_ByFlightFragment);
//                transaction.replace(R.id.fl_content, m_ByRouteFragment);
                transaction.replace(R.id.fl_date_select, m_SelectDate);
                transaction.commitAllowingStateLoss();

                m_FragmentHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        initUIByItineraryInfoData();// edited by Kevin
                    }
                });

                m_FragmentHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        m_flContent.setVisibility(View.VISIBLE);
                        m_flSelectDate.setVisibility(View.VISIBLE);
                        m_SearchButton.setVisibility(View.VISIBLE);
                    }
                }, 50);

                //取得機場資料
                m_manager.fetchAirportData(false, "", CISelectDepartureAirpotActivity.FLIGHT_STATUS);
            }
        }, 300);
    }

    @Override
    public void onFragmentHide() {
        super.onFragmentHide();
        if ( null != m_flContent )
            m_flContent.setVisibility(View.GONE);

        if ( null != m_flSelectDate )
            m_flSelectDate.setVisibility(View.GONE);

        if ( null != m_SearchButton )
            m_SearchButton.setVisibility(View.GONE);

        //註銷GPS接收廣播通知
        try{
            getActivity().unregisterReceiver(m_GpsReceiver);
            m_selectAirportPresenter.interrupt();
            m_manager.cancelTask();
        }catch (Exception e){}
        m_GpsReceiver = null;

    }

    //用於從詳細行程頁面點擊「航班動態」後，以導入的資料初始化畫面 edited by Kevin
    private void initUIByItineraryInfoData(){
        if(null == s_ItineraryInfoData || null == m_bar){
            return;
        }
        m_bar.setSelectType(TwoItemNavigationBar.EInitItem.RIGHT);
        m_FragmentHandler.post(new Runnable() {
            @Override
            public void run() {
                m_ByFlightFragment.setAirline(s_ItineraryInfoData.Airlines);
                m_ByFlightFragment.setFlightNumber(s_ItineraryInfoData.Flight_Number);
                s_ItineraryInfoData = null;
            }
        });

    }
    //用於從詳細行程頁面點擊「航班動態」後導入的資料 edited by Kevin
    public static void setTripData(CITripListResp_Itinerary data){
        s_ItineraryInfoData = data;
    }
}
