package ci.function.TimeTable;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.Core.Location.GpsReceiver;
import ci.function.Core.Location.SSingleLocationUpdater;
import ci.function.FlightStatus.CIFlightResultActivity;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.CIChooseAirportTextFieldFragment;
import ci.ui.TextField.CIChooseSearchDateTextFieldFragment;
import ci.ui.TimeTable.CITimeTableFlightStatusDetailList;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.object.CIAirportDataManager;
import ci.ui.object.CIProgressDialog;
import ci.ui.toast.CIToastView;
import ci.ui.view.TwoItemNavigationBar;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Models.entities.CITimeTableListResp;
import ci.ws.Models.entities.CITimeTableReq;
import ci.ws.Presenter.CIInquiryTimetablePresenter;
import ci.ws.Presenter.CISelectDepartureAirportPresenter;
import ci.ws.Presenter.Listener.CIAirportListener;
import ci.ws.Presenter.Listener.CIInquiryTimetableListener;
import ci.ws.cores.object.CIWSCommon;

import static ci.function.Core.Location.SSingleLocationUpdater.getRecentDistanceIndex;
import static ci.function.Core.Location.SSingleLocationUpdater.requestPermission;
import static ci.ui.TextField.CIChooseSearchDateTextFieldFragment.BUNDLE_PARA_BOOLEAN_IS_SINGLE;

/**
 * Created by flowmahuang on 2016/3/14.
 */
public class CITimeTableFragment extends BaseFragment
        implements TwoItemNavigationBar.ItemClickListener, View.OnClickListener {
    /**
     * 目的地監聽，主要是點擊欄位時會被觸發，如果判斷出發地(m_FromFragment)尚未被選擇，
     * 則會跳出提示視窗要求選擇，則isOpenSelectPage()會回傳false，反之亦然；如果已經選
     * 擇出發地了，則會將跳出選單，另外目的地欄位開啟時會呼叫getFromIAIT()去取得出發地的IAIT
     */
    CIChooseAirportTextFieldFragment.OnCIChooseAirportTextFragmentClick onCIChooseAirportTextFragmentClick
            = new CIChooseAirportTextFieldFragment.OnCIChooseAirportTextFragmentClick() {
        @Override
        public boolean isOpenSelectPage() {
            if (m_FromFragment.getText().equals("")) {

                showDialog(getString(R.string.warning)
                        //2016-11-09 Ryan , 更換錯誤訊息顯示方式
                        ,String.format(getResources().getString(R.string.please_input_field), getString(R.string.from))
                        //,getResources().getString(R.string.select_error_msg) + " " + getString(R.string.from)
                        ,getString(R.string.confirm));

                return false;
            } else {
                return true;
            }
        }

        @Override
        public String getFromIAIT() {
            return m_FromFragment.getIAIT();
        }
    };

    CIChooseSearchDateTextFieldFragment.OnSearchDataTextFeildParams onSearchDataTextFeildParams
            = new CIChooseSearchDateTextFieldFragment.OnSearchDataTextFeildParams() {

        @Override
        public boolean isOpenSelectPage() {
            if (TextUtils.isEmpty(m_ToFragment.getText())) {
                showDialog(getString(R.string.warning)
                        //2016-11-09 Ryan , 更換錯誤訊息顯示方式
                        ,String.format(getResources().getString(R.string.please_input_field), getString(R.string.to))
                        //,getResources().getString(R.string.select_error_msg) + " " + getString(R.string.to)
                        ,getString(R.string.confirm));
                return false;
            }else{
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
            return m_FromFragment.getText();
        }

        @Override
        public String getDestinationAirport() {
            return m_ToFragment.getText();
        }
    };

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
            boolean bIsHavePermission = requestPermission(CITimeTableFragment.this);
            if (true == bIsLocationServiceAvailable && true == bIsHavePermission) {
                m_selectAirportPresenter.fetchLocation();
            } else if (false == bIsLocationServiceAvailable && true == bIsHavePermission && !AppInfo.getInstance(getContext()).GetIsShowLocationService()){
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
            if(null != m_FromFragment){
                m_FromFragment.setIAIT(data.IATA);
                m_FromFragment.setText(text);
            }
            if(null != m_ToFragment){
                m_ToFragment.setIAIT("");
                m_ToFragment.setText("");
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

    private CIChooseAirportTextFieldFragment    m_FromFragment;
    private CIChooseAirportTextFieldFragment    m_ToFragment;
    private CITextFieldFragment                 m_DepartureFragment;
    private CITextFieldFragment                 m_ReturnFragment;

    private FrameLayout                         m_flSelect                  = null;
    private RelativeLayout                      m_rlContent                 = null;

    private FrameLayout                         m_ReturnLayout;
    private ImageButton                         m_ChangeButton;
    private Button                              m_nextButton                = null;
    private CIAirportDataManager                m_manager                   = null;
    private CISelectDepartureAirportPresenter   m_selectAirportPresenter    = null;
    private List<CIFlightStationEntity>         m_allDeparture              = null;
    private GpsReceiver                         m_GpsReceiver               = null;
    private CIInquiryTimetablePresenter         timetablePresenter          = null;

    private static final int                    FROM_TIME_TABLE             = CITimeTableFlightStatusDetailList.DEF_VIEW_TYPE_TIME_TABLE;
    private final int                           PERMISSIONS_REQUEST_CODE    = 1;
    private String                              departureData               = null;
    private String                              returnData                  = null;
    private boolean                             m_bFragment                 = true;
    private boolean                             m_bIsSingle                 = false;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_timetable;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_flSelect = (FrameLayout) view.findViewById(R.id.fl_select);
        m_rlContent = (RelativeLayout) view.findViewById(R.id.rl_content);

        m_ChangeButton = (ImageButton) view.findViewById(R.id.btn_change_airport);
        m_ReturnLayout = (FrameLayout) view.findViewById(R.id.fragment4);

        m_nextButton = (Button) view.findViewById(R.id.btn_to_search_time);
        m_nextButton.setVisibility(View.GONE);

        m_selectAirportPresenter = new CISelectDepartureAirportPresenter(m_selectAirportListener);
        m_manager = new CIAirportDataManager(m_managerCallBack);
    }

    private void setupViewComponents() {
        FragmentManager manager = getChildFragmentManager();
        m_FromFragment = CIChooseAirportTextFieldFragment.newInstance(
                getString(R.string.from),
                R.drawable.ic_departure_2,
                R.drawable.ic_departure_4,
                false,
                CISelectDepartureAirpotActivity.TIME_TABLE);
        m_ToFragment = CIChooseAirportTextFieldFragment.newInstance(
                getString(R.string.to),
                R.drawable.ic_arrival_2,
                R.drawable.ic_arrival_4,
                true,
                CISelectDepartureAirpotActivity.TIME_TABLE);

        m_DepartureFragment = CIChooseSearchDateTextFieldFragment.newInstance(getString(R.string.departure_date), m_bIsSingle, true);
        m_DepartureFragment.setLock(true);

        m_ReturnFragment = CIChooseSearchDateTextFieldFragment.newInstance(getString(R.string.return_date), m_bIsSingle, false);
        m_ReturnFragment.setLock(true);

        /**
         * 出發地監聽，主要是點擊欄位時會被觸發，固定都會開啟選擇機場的視窗，所以isOpenSelectPage()會回傳true
         * 出發地欄位開啟選單時不會去調用getFromIAIT()
         */
        m_FromFragment.setOnCIChooseAirportTextFragmentClick(new CIChooseAirportTextFieldFragment.OnCIChooseAirportTextFragmentClick() {
            @Override
            public boolean isOpenSelectPage() {
                if (!m_ToFragment.getText().equals("")) {
                    (m_ToFragment).setEditText("");
                }
                return true;
            }

            @Override
            public String getFromIAIT() {
                return ( m_FromFragment).getIAIT();
            }
        });

        m_ToFragment.setOnCIChooseAirportTextFragmentClick(onCIChooseAirportTextFragmentClick);

        ((CIChooseSearchDateTextFieldFragment)m_DepartureFragment).setOnSearchDataTextFeildParams(onSearchDataTextFeildParams);
        ((CIChooseSearchDateTextFieldFragment)m_ReturnFragment).setOnSearchDataTextFeildParams(onSearchDataTextFeildParams);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment1, m_FromFragment)
                .replace(R.id.fragment2, m_ToFragment)
                .replace(R.id.fragment3, m_DepartureFragment)
                .replace(R.id.fragment4, m_ReturnFragment)
                .commitAllowingStateLoss();

        m_FromFragment.setWidth(268);
        m_ToFragment.setWidth(268);
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

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root));

        vScaleDef.selfAdjustSameScaleView(m_ChangeButton, 32, 32);

//        vScaleDef.setMargins(m_flSelect, 20, 20, 20, 0);
    }

    @Override
    protected void setOnParameterAndListener(View view) {
        m_nextButton.setOnClickListener(this);
        m_ChangeButton.setOnClickListener(this);
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
                m_bIsSingle = false;

                m_ReturnLayout.setVisibility(View.VISIBLE);
                m_DepartureFragment.getArguments().putBoolean(BUNDLE_PARA_BOOLEAN_IS_SINGLE, m_bIsSingle);

                //2016.07.20 Ling - 如果已選過回程日 就不更動原本選擇的日期 (同iOS)
                if (!TextUtils.isEmpty(m_ReturnFragment.getText()) &&
                        getReturnDepartureCalender().getTimeInMillis() < getDepartureCalender().getTimeInMillis()){
                    m_ReturnFragment.setText(m_DepartureFragment.getText());
                    ((CIChooseSearchDateTextFieldFragment)m_ReturnFragment).setSelectDate(getDepartureCalender());
                }

                m_bFragment = true;
                break;
            case R.id.rl_right_bg:
                m_bIsSingle = true;

                m_ReturnLayout.setVisibility(View.INVISIBLE);
                m_DepartureFragment.getArguments().putBoolean(BUNDLE_PARA_BOOLEAN_IS_SINGLE, m_bIsSingle);
                m_bFragment = false;
                break;
        }
    }

    private void changeActivity(Class clazz) {
        Intent intent = new Intent();
        //統一時間顯示格式為 yyyy-MM-dd by Ryan 2016-08-16
        if (m_bFragment) {
            intent.putExtra(CIFlightResultActivity.BUNDEL_PARA_DEPARTDATE, AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMdd(getDepartureCalender()));
            intent.putExtra(CIFlightResultActivity.BUNDEL_PARA_RETURNDATE, AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMdd(getReturnDepartureCalender()));

        } else if (!m_bFragment) {
            intent.putExtra(CIFlightResultActivity.BUNDEL_PARA_DEPARTDATE, AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMdd(getDepartureCalender()));
        }
        intent.putExtra(CIFlightResultActivity.BUNDEL_PARA_TIMETABLE_TO_CODE, m_ToFragment.getIAIT());
        intent.putExtra(CIFlightResultActivity.BUNDEL_PARA_TIMETABLE_FROM_CODE, m_FromFragment.getIAIT());
        intent.putExtra(CIFlightResultActivity.BUNDEL_PARA_TIMETABLE_RETURN_DATA, returnData);
        intent.putExtra(CIFlightResultActivity.BUNDEL_PARA_TIMETABLE_DEPARTURE_DATA, departureData);
        intent.putExtra(CIFlightResultActivity.BUNDLE_PARA_FROM_VIEW, FROM_TIME_TABLE);
        intent.putExtra(CIFlightResultActivity.BUNDEL_PARA_RETURN_OR_NOT, m_bFragment);
        intent.setClass(getContext(), clazz);
        startActivity(intent);

        //測試切換activity滑入滑出動畫
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_change_airport:

                String from     = m_FromFragment.getText();
                String to       = m_ToFragment.getText();
                String fromIAIT = m_FromFragment.getIAIT();
                String toIAIT   = m_ToFragment.getIAIT();

                m_FromFragment.setText(to);
                m_ToFragment.setText(from);
                m_FromFragment.setIAIT(toIAIT);
                m_ToFragment.setIAIT(fromIAIT);
                break;
            case R.id.btn_to_search_time:
                //2016-07-27 調整判斷邏輯, 不需要顯示Dialog 應該就不要new 出來
                String strMessage = "";
                if ( m_FromFragment.getText().equals("") ) {
                    //2016-11-09 Ryan , 更換錯誤訊息顯示方式
                    strMessage = String.format(getResources().getString(R.string.please_input_field), m_FromFragment.getArguments().getString("A_TEXT_HINT"));

                } else if (m_ToFragment.getText().equals("")) {
                    //2016-11-09 Ryan , 更換錯誤訊息顯示方式
                    strMessage = String.format(getResources().getString(R.string.please_input_field), m_ToFragment.getArguments().getString("A_TEXT_HINT"));

                } else if (m_DepartureFragment.getText().equals("")) {
                    //2016-11-09 Ryan , 更換錯誤訊息顯示方式
                    strMessage = String.format(getResources().getString(R.string.please_input_field), m_DepartureFragment.getHint());

                } else if (m_ReturnFragment.getText().equals("") && m_bFragment) {
                    //2016-11-09 Ryan , 更換錯誤訊息顯示方式
                    strMessage = String.format(getResources().getString(R.string.please_input_field), m_ReturnFragment.getHint());
                }

                if ( strMessage.length() > 0 ){
                    showDialog(getString(R.string.warning), strMessage, getString(R.string.confirm));
                } else {
                    setTimeTableApi();
                }
                break;
        }
    }

    //Call  TimeTable Api
    private void setTimeTableApi() {
        CITimeTableReq timeTableReq = new CITimeTableReq();

        if (m_bFragment) {
            timeTableReq.departure = m_FromFragment.getIAIT();
            timeTableReq.departure_date = CIWSCommon.ConvertDatetoWSFormat(getDepartureCalender());
            timeTableReq.arrival = m_ToFragment.getIAIT();
            timeTableReq.return_date = CIWSCommon.ConvertDatetoWSFormat(getReturnDepartureCalender());
        } else {
            timeTableReq.departure = m_FromFragment.getIAIT();
            timeTableReq.departure_date = CIWSCommon.ConvertDatetoWSFormat(getDepartureCalender());
            timeTableReq.arrival = m_ToFragment.getIAIT();
            timeTableReq.return_date = "";

        }

        timetablePresenter = new CIInquiryTimetablePresenter(new CIInquiryTimetableListener() {
            @Override
            public void onTimeTableSuccess(String rt_code, String rt_msg, final CITimeTableListResp timetableList) {
                Gson gson = new Gson();
                departureData = gson.toJson(timetableList.arDepartureList);
                returnData = gson.toJson(timetableList.arReturnList);
                if (departureData.equals("[]")) {
                    showDialog( getResources().getString(R.string.warning),
                                getResources().getString(R.string.no_match_data),
                                getResources().getString(R.string.confirm));
                } else {
                    changeActivity(CIFlightResultActivity.class);
                }
            }

            @Override
            public void onTimeTableError(String rt_code, String rt_msg) {
//                Dialog 顯示錯誤訊息
                showDialog( getResources().getString(R.string.warning),
                            rt_msg,
                            getResources().getString(R.string.confirm));
            }

            @Override
            public void showProgress() {
                showProgressDialog(new CIProgressDialog.CIProgressDlgListener() {
                    @Override
                    public void onBackPressed() {
                        if (timetablePresenter != null) {
                            timetablePresenter.CancelInquiryTimetable();
                        }
                    }
                });
            }

            @Override
            public void hideProgress() {
                hideProgressDialog();
            }
        });
        timetablePresenter.InquiryTimetableFromWS(timeTableReq);
    }

    public Calendar getDepartureCalender(){
        return ((CIChooseSearchDateTextFieldFragment)m_DepartureFragment).getSelectDate();
    }

    public Calendar getReturnDepartureCalender(){
        return ((CIChooseSearchDateTextFieldFragment)m_ReturnFragment).getSelectDate();
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
        m_bIsSingle = false;

        m_nextButton.setText(R.string.search);
        TwoItemNavigationBar bar = TwoItemNavigationBar.newInstance(
                        getString(R.string.round_trip),
                        getString(R.string.one_way));
        bar.setListener(CITimeTableFragment.this);

        setupViewComponents();

        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_select, bar);
        fragmentTransaction.commitAllowingStateLoss();

        m_FragmentHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                m_FragmentHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        m_rlContent.setVisibility(View.VISIBLE);
                        m_nextButton.setVisibility(View.VISIBLE);
                        m_ReturnLayout.setVisibility(View.VISIBLE);
                    }
                }, 50);

                //取得機場資料
                m_manager.fetchAirportData(false, "", CISelectDepartureAirpotActivity.TIME_TABLE);

            }
        }, 300);
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
    public void onFragmentHide() {

        if ( null != m_rlContent )
            m_rlContent.setVisibility(View.GONE);

        if ( null != m_nextButton )
            m_nextButton.setVisibility(View.GONE);

        //註銷GPS開啟通知
        try{
            getActivity().unregisterReceiver(m_GpsReceiver);
            m_selectAirportPresenter.interrupt();
            m_manager.cancelTask();
        }catch (Exception e){}
        m_GpsReceiver = null;
    }

    @Override
    protected void onFragmentPause() {
        super.onFragmentPause();
        if (timetablePresenter != null) {
            timetablePresenter.CancelInquiryTimetable();
        }

        //註銷GPS開啟通知
        try{
            getActivity().unregisterReceiver(m_GpsReceiver);
            m_selectAirportPresenter.interrupt();
            m_manager.cancelTask();
        }catch (Exception e){}
        m_GpsReceiver = null;
    }
}
