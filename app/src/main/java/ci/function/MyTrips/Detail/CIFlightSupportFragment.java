package ci.function.MyTrips.Detail;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.List;

import ci.function.About.CIGlobalServiceActivity;
import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.ui.object.AppInfo;
import ci.ws.Models.entities.CIGlobalServiceEntity;
import ci.ws.Models.entities.CIGlobalServiceList;
import ci.function.Core.Location.GpsReceiver;
import ci.function.Core.Location.SSingleLocationUpdater;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.CIDialPhoneNumberManager;
import ci.ui.toast.CIToastView;
import ci.ws.Presenter.CIGlobalServicePresenter;
import ci.ws.Presenter.Listener.IGlobalServiceListener;

import static ci.function.Core.Location.SSingleLocationUpdater.PERMISSIONS_REQUEST_CODE;
import static ci.function.Core.Location.SSingleLocationUpdater.getRecentDistanceIndex;
import static ci.function.Core.Location.SSingleLocationUpdater.requestPermission;

/**
 * Created by kevin on 2016/3/4.
 */
public class CIFlightSupportFragment extends BaseFragment
    implements View.OnClickListener,
        GpsReceiver.Callback,
        IGlobalServiceListener {
    private TextView m_tvPhone                     = null,
            m_tvSupportLoaction                    = null;
    private CIGlobalServicePresenter m_presenter   = null;
    private ProgressBar              m_progressBar = null;
    private CIGlobalServiceList      m_list        = null;
    private GpsReceiver              m_GpsReceiver = null;
    private CIDialPhoneNumberManager m_dialPhoneNumberManager   = null;
    private final String                DEF_OFFICE    = "台北";
    private final String                DEF_CALL      = "02-27152233";
    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_flight_tab_flight_support;
    }


    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_dialPhoneNumberManager = new CIDialPhoneNumberManager();

        m_tvPhone = (TextView) view.findViewById(R.id.tv_flight_support_phone);
        m_tvSupportLoaction = (TextView) view.findViewById(R.id.tv_flight_support_location);
        m_progressBar       = (ProgressBar) view.findViewById(R.id.progressBar);
        m_presenter = CIGlobalServicePresenter.getInstance(this);

        m_tvPhone.setText(DEF_CALL);
        m_tvSupportLoaction.setText(DEF_OFFICE);


        m_list = m_presenter.fetchData();
        if(null == m_list || m_list.size() <= 0){
            m_presenter.downloadDataFromWS();
        } else {
            initBranch();
        }

    }

    private void initBranch(){
        for(CIGlobalServiceEntity data:m_list){
            if(true == TextUtils.equals("TPE", data.branch_name)){
                m_tvSupportLoaction.setText(data.branch);
                m_tvPhone.setText(data.ticket_op_tel);
                break;
            }
        }
        if(true == requestPermission(CIFlightSupportFragment.this)){
            m_presenter.fetchLocation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        m_GpsReceiver = new GpsReceiver(this);
        IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        getActivity().registerReceiver(m_GpsReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(m_GpsReceiver);
        m_GpsReceiver = null;
        m_presenter.interrupt();
    }

    /**
     * 當執行ActivityCompat.requestPermissions()後，會callbackonRequestPermissionsResult()
     * @param requestCode   requestPermissions()設定的requestCode
     * @param permissions   permission權限
     * @param grantResults  要求權限結果，如果等於[PackageManager.PERMISSION_GRANTED]就是同意
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //edited by Kevincheng
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    m_presenter.fetchLocation();
                } else {
                    CIToastView.makeText(getActivity(),getString(R.string.gps_permissions_msg)).show();
                }
                return;
            }

        }
    }


    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(view.findViewById(R.id.iv_ic_phone_b), 24, 24);
        vScaleDef.selfAdjustSameScaleView(view.findViewById(R.id.iv_ic_list_arrow_g1), 24, 24);
    }


    @Override
    protected void setOnParameterAndListener(View view) {
        view.findViewById(R.id.rl_global_service_hotline_click).setOnClickListener(this);
        view.findViewById(R.id.rl_phone_click).setOnClickListener(this);
    }


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
    public void onLanguageChangeUpdateUI() {

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_global_service_hotline_click:
                changeActivity(CIGlobalServiceActivity.class);
                break;
            case R.id.rl_phone_click:
                m_dialPhoneNumberManager.showAlertDialogForDialPhoneNumber(getContext(), m_tvPhone.getText().toString());
                break;
        }
    }

    private void changeActivity(Class clazz){
        Intent intent = new Intent();
        intent.setClass(getActivity(),clazz);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    @Override
    public void onGpsModeChangeReceive() {
        //edited by Kevincheng
        m_presenter.interruptGPS();
        if (true == CIApplication.getSysResourceManager().isLocationServiceAvailable()
                && true == requestPermission(this)) {
            m_presenter.fetchLocation();
        }
    }
    /************************** IGlobalServiceListener ****************************/
    //edited by Kevincheng
    @Override
    public void showProgress() {
        showProgressDialog();
    }

    @Override
    public void hideProgress() {
        hideProgressDialog();
    }

    /**
     * 當取的GPS Location 失敗時
     */
    @Override
    public void onfetchLocationFail() {
        if(null != getActivity()) {
            CIToastView.makeText(getActivity(), getString(R.string.gps_position_fail)).show();
        }
    }

    @Override
    public void onNoAvailableLocationProvider() {
        if(null != getActivity() && !AppInfo.getInstance(getActivity()).GetIsShowLocationService() ) {
            CIToastView.makeText(getActivity(), getString(R.string.gps_press_enable_gps_service)).show();
            //2018-06-22 新增GPS定位服務訊息僅顯示一次
            AppInfo.getInstance(getActivity()).SetIsShowLocationService(true);
        }
    }

    /**
     * 當取得GPS Location時
     * @param gpsLocationData
     */
    @Override
    public void onLocationBinded(Location gpsLocationData) {
        m_progressBar.setVisibility(View.GONE);

        if(null == m_list || 0 >= m_list.size()){
            return;
        }

        CIGlobalServiceList list = (CIGlobalServiceList)m_list.clone();

        List<SSingleLocationUpdater.LocationItem> locationDatas = new ArrayList<>();
        for(CIGlobalServiceEntity data:list){
            SSingleLocationUpdater.LocationItem locationItem = new SSingleLocationUpdater.LocationItem();
            locationItem.Lat = data.lat;
            locationItem.Long = data.lng;
            locationDatas.add(locationItem);
        }

        //取得最近位置資料索引
        int index = getRecentDistanceIndex(gpsLocationData, locationDatas);

        m_tvSupportLoaction.setText(m_list.get(index).branch);
        m_tvPhone.setText(m_list.get(index).ticket_op_tel);

    }


    @Override
    public void onLocationBinding() {
        if(null != getActivity()) {
            CIToastView.makeText(getActivity(), getString(R.string.gps_positioning)).show();
        }
        m_progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDownloadSuccess() {
        m_list = m_presenter.fetchData();
        if(null != m_list && m_list.size() > 0){
            initBranch();
        }
    }

    @Override
    public void onDownloadError(String rt_code, String rt_msg) {}
    /**************************************************************************/
}
