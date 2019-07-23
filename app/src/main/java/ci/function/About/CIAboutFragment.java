package ci.function.About;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.BuildConfig;
import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.List;

import ci.function.About.Adapter.CIAboutListAdapter;
import ci.function.About.item.CIAboutChildItem;
import ci.function.About.item.CIAboutGroupItem;
import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.Core.Location.GpsReceiver;
import ci.function.Login.api.GooglePlusLoginApi;
import ci.ui.WebView.CIWithoutInternetActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.object.CIDialPhoneNumberManager;
import ci.ui.object.CIProgressDialog;
import ci.ui.toast.CIToastView;
import ci.ws.Models.entities.CIGlobalServiceEntity;
import ci.ws.Models.entities.CIGlobalServiceList;
import ci.ws.Presenter.CIAIServicePresenter;
import ci.ws.Presenter.CIGlobalServicePresenter;
import ci.ws.Presenter.Listener.IGlobalServiceListener;

import static android.widget.ExpandableListView.OnChildClickListener;
import static android.widget.ExpandableListView.OnGroupClickListener;
import static ci.function.About.Adapter.CIAboutListAdapter.OnAboutListAdapterListener;
import static ci.function.Core.Location.SSingleLocationUpdater.LocationItem;
import static ci.function.Core.Location.SSingleLocationUpdater.PERMISSIONS_REQUEST_CODE;
import static ci.function.Core.Location.SSingleLocationUpdater.getRecentDistanceIndex;
import static ci.function.Core.Location.SSingleLocationUpdater.requestPermission;

/** 關於華航
 * zeplin: 18.2-1
 * wireframe: p.116
 * Created by jlchen on 2016/4/7.
 */
public class CIAboutFragment extends BaseFragment
        implements GpsReceiver.Callback,
        IGlobalServiceListener {

    private static final String                     TYPE                        = "TYPE";
    private CIGlobalServicePresenter                m_presenter                 = null;
    private ProgressBar                             m_progressBar               = null;
    private CIGlobalServiceList                     m_list                      = null;
    private CIDialPhoneNumberManager                m_dialPhoneNumberManager    = null;
    //建議使用 newInstance By KC
    public static CIAboutFragment newInstance(boolean bType) {
        Bundle args = new Bundle();
        args.putBoolean(TYPE,bType);
        CIAboutFragment fragment = new CIAboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(null != bundle){
            this.m_bIsShowAboutCIList = bundle.getBoolean(TYPE);
        }
    }

    OnAboutListAdapterListener m_OnAboutListAdapterListener = new OnAboutListAdapterListener() {
        @Override
        public void OnChildItemClick(CIAboutChildItem childItem) {
            selectChildItem(childItem);
        }
    };

    //群組Click不作收合動作
    OnGroupClickListener m_onGroupClicklistener = new OnGroupClickListener(){

        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            return true;
        }
    };

    OnChildClickListener m_onChildClickListener = new OnChildClickListener() {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            return true;
        }
    };


    private RelativeLayout              m_rlBg        = null;
    private ExpandableListView          m_elvAbout    = null;
    private CIAboutListAdapter          m_adapter     = null;
    private GpsReceiver                 m_GpsReceiver = null;
    private ArrayList<CIAboutGroupItem> m_alData      = new ArrayList<>();
    private String                      m_strOffice   = null;
    private String                      m_strCall     = null;

    private final String                DEF_OFFICE    = "台北";
    private final String                DEF_CALL      = "02-27152233";

    //是否顯示關於華航所有清單, 預設為true. (當為false時,僅顯示聯繫我們的選項)
    private boolean m_bIsShowAboutCIList = true;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_about;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        //定義預設辨公室電話
        m_strOffice = DEF_OFFICE;
        m_strCall   = DEF_CALL;

        initialListData();

        m_rlBg              = view.findViewById(R.id.rl_bg);
        m_elvAbout          = view.findViewById(R.id.elv);
        m_adapter           = new CIAboutListAdapter(getActivity(), m_alData, m_OnAboutListAdapterListener);
        m_progressBar       = view.findViewById(R.id.progressBar);
        m_elvAbout.setAdapter(m_adapter);
        m_elvAbout.setVerticalScrollBarEnabled(false);
        m_elvAbout.setOnChildClickListener(m_onChildClickListener);
        m_elvAbout.setOnGroupClickListener(m_onGroupClicklistener);
        //強制將群組清單展開
        for (int loop = 0; loop < m_alData.size(); loop++) {
            m_elvAbout.expandGroup(loop);
        }
        m_dialPhoneNumberManager = new CIDialPhoneNumberManager();
    }

    @Override
    public void onFragmentShow() {
        super.onFragmentShow();

        if (    null == m_alData        ||
                0 == m_alData.size()    ||
                !TextUtils.equals( getString(R.string.contact_us) , m_alData.get(0).getTitle()) ){

            resetDataList();
            m_adapter.notifyDataSetChanged();

            //強制將群組清單展開
            for (int loop = 0; loop < m_alData.size(); loop++) {
                m_elvAbout.expandGroup(loop);
            }
        }

        m_FragmentHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //初始化資料及畫面後才進行營業處資料的取得及GPS定位，避免資料仍是初始資料
                if(null == m_presenter){
                    m_presenter = CIGlobalServicePresenter.getInstance(CIAboutFragment.this);
                }


                m_list = m_presenter.fetchData();
                if(null == m_list || m_list.size() <= 0){
                    m_presenter.downloadDataFromWS();
                } else {
                    initBranch();
                }

            }
        }, 300);

        try{
            if(null == m_GpsReceiver){
                m_GpsReceiver = new GpsReceiver(this);
            }
            IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
            getActivity().registerReceiver(m_GpsReceiver, filter);
        }catch (Exception e){

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null != m_rlBg){
            m_rlBg.setBackgroundResource(R.drawable.bg_about);
        }
    }

    @Override
    protected void onFragmentResume() {
        super.onFragmentResume();
        try{
            if(null == m_GpsReceiver){
                m_GpsReceiver = new GpsReceiver(this);
            }
            IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
            getActivity().registerReceiver(m_GpsReceiver, filter);
        }catch (Exception e){
        }
    }

    @Override
    protected void onFragmentPause() {
        super.onFragmentPause();
        try{
            getActivity().unregisterReceiver(m_GpsReceiver);
            m_GpsReceiver = null;
            m_presenter.interrupt();
        }catch (Exception e){}
    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != m_rlBg) {
            if (m_rlBg.getBackground() instanceof BitmapDrawable) {
                m_rlBg.setBackground(null);
            }
        }
    }

    private void initBranch(){
        for(CIGlobalServiceEntity data:m_list){
            if(TextUtils.equals("TPE", data.branch_name)){
                m_adapter.setServiceInfo(data.branch, data.ticket_op_tel);
                break;
            }
        }

        if(null != getActivity()){
            m_adapter.notifyDataSetChanged();
        }
        if(requestPermission(CIAboutFragment.this)){
            m_presenter.fetchLocation();
        }
    }

    @Override
    public void onFragmentHide() {
        super.onFragmentHide();

        m_alData.clear();

        //edited by Kevincheng
        try{
            getActivity().unregisterReceiver(m_GpsReceiver);
            m_GpsReceiver = null;
            m_presenter.interrupt();
        }catch (Exception e){}

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
            }

        }
    }

    private void initialListData(){
        m_alData.clear();

        CIAboutGroupItem groupItem1 = new CIAboutGroupItem(getString(R.string.contact_us));
        groupItem1.addChildItem(
                CIAboutChildItem.DEF_ABOUT_CHILD_ITEM_CALL,
                m_strOffice,
                m_strCall, 0);
        groupItem1.addChildItem(
                CIAboutChildItem.DEF_ABOUT_CHILD_ITEM_GLOBAL,
                getString(R.string.global_customer_service),
                null, R.drawable.ic_locate_service);
// UAT 打開 ＡＩ功能
        //if ( !TextUtils.isEmpty(getContext().getString(R.string.about_ai_service)) ){
        if( BuildConfig.isLoggable && BuildConfig.DEBUG) {
            groupItem1.addChildItem(
                    CIAboutChildItem.DEF_ABOUT_CHILD_ITEM_AI,
                    getString(R.string.about_ai_service),
                    null, R.drawable.ic_ai_service);
        }
        groupItem1.addChildItem(
                CIAboutChildItem.DEF_ABOUT_CHILD_ITEM_FB,
                getString(R.string.fb_message),
                null, R.drawable.ic_fb_messenger);
        m_alData.add(groupItem1);

        if (m_bIsShowAboutCIList){
            CIAboutGroupItem groupItem2 = new CIAboutGroupItem(getString(R.string.about_us));
            groupItem2.addChildItem(
                    CIAboutChildItem.DEF_ABOUT_CHILD_ITEM_CI,
                    getString(R.string.china_airlines_instroduction),
                    null, 0);
            groupItem2.addChildItem(
                    CIAboutChildItem.DEF_ABOUT_CHILD_ITEM_APP,
                    getString(R.string.china_airlines_app),
                    null, 0);
            m_alData.add(groupItem2);

            CIAboutGroupItem groupItem3 = new CIAboutGroupItem(getString(R.string.partner_skyteam));
            groupItem3.addChildItem(
                    CIAboutChildItem.DEF_ABOUT_CHILD_ITEM_SKY,
                    getString(R.string.route_map_3d),
                    null, 0);
            m_alData.add(groupItem3);
        }
    }

    private void resetDataList(){
        if ( null == m_alData || 0 == m_alData.size() ){
            initialListData();
            return;
        }

        String[] strItem1 = { m_strOffice,
                getString(R.string.global_customer_service),
                getString(R.string.fb_message) };
        m_alData.get(0).resetText(getString(R.string.contact_us), strItem1);

        String[] strItem2 = { getString(R.string.china_airlines_instroduction),
                getString(R.string.china_airlines_app) };
        m_alData.get(1).resetText(getString(R.string.about_us), strItem2);

        String[] strItem3 = {getString(R.string.route_map_3d) };
        m_alData.get(2).resetText(getString(R.string.partner_skyteam), strItem3);
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        vScaleDef.setMargins(progressBar,0,66,0,0);
    }

    @Override
    protected void setOnParameterAndListener(View view) {}

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

    private void selectChildItem(CIAboutChildItem childItem) {
        Intent intent = new Intent();
        switch (childItem.getChildId()) {
            case CIAboutChildItem.DEF_ABOUT_CHILD_ITEM_CALL:
                if ( 0 < childItem.getCallNumber().length() ){
                    m_dialPhoneNumberManager.showAlertDialogForDialPhoneNumber(getContext(),
                                                                                childItem.getCallNumber());
                }
                break;
            case CIAboutChildItem.DEF_ABOUT_CHILD_ITEM_GLOBAL:
                changeActivity(CIGlobalServiceActivity.class, intent);
                break;
            case CIAboutChildItem.DEF_ABOUT_CHILD_ITEM_FB:
                sendFbMsg();
                break;
            case CIAboutChildItem.DEF_ABOUT_CHILD_ITEM_CI:
                changeActivity(CIIntroductionActvity.class, intent);
                break;
            case CIAboutChildItem.DEF_ABOUT_CHILD_ITEM_APP:
                changeActivity(CIAPPActvity.class, intent);
                break;
            case CIAboutChildItem.DEF_ABOUT_CHILD_ITEM_SKY:
                intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG, childItem.getText());
                intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_URL_TAG, getString(R.string.skyteam_url));
                changeActivity(CIWithoutInternetActivity.class, intent);
//                OpenBrowser(getString(R.string.skyteam_url));
                break;
            case CIAboutChildItem.DEF_ABOUT_CHILD_ITEM_AI:
                {
                    CIAIServicePresenter ciAIServicePresenter = new CIAIServicePresenter(new CIAIServicePresenter.CallBack(){

                        @Override
                        public void showProgress() {
                            showProgressDialog();
                        }

                        @Override
                        public void hideProgress() {
                            hideProgressDialog();
                        }

                        @Override
                        public void onDataBinded(String webData) {

                            Intent intent = new Intent();
                            intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG, getString(R.string.about_ai_service));
                            intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_WEB_DATA_TAG, webData);
                            intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_WEB_IS_SHOW_CLOSE_BTN_TAG, true);
                            changeActivity(CIWithoutInternetActivity.class, intent);
                        }

                        @Override
                        public void onDataFetchFeild(String msg) {
                            showDialog(getContext().getString(R.string.warning), msg);
                        }
                    });

                    ciAIServicePresenter.fetchAIServiceWebData();

                }
                break;
            default:
                break;
        }
    }

    /**傳入URL，外開Browser*/
    public void OpenBrowser(String strUrl) {
        if ( strUrl != null && strUrl.length() > 0 ) {
            try {
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(strUrl)));
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }

    private void changeActivity(Class clazz, Intent intent) {
        intent.setClass(getActivity(), clazz);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    private void sendFbMsg() {
        // fb-messenger://user/235288368941/
        // fb-messenger://user-thread/119474188105563
        Uri uri = Uri.parse("fb-messenger://user/119474188105563/");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            getActivity().startActivity(intent);
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            if (GooglePlusLoginApi.checkPlayServicesShowDialog(getActivity())){

                uri = Uri.parse("market://details?id=com.facebook.orca");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (null != m_rlBg) {
            m_rlBg.setBackground(null);
        }
        super.onDestroy();
    }

    @Override
    public void onGpsModeChangeReceive() {
        //edited by Kevincheng
        if(null == m_presenter){
            return;
        }
        m_presenter.interruptGPS();
        if (CIApplication.getSysResourceManager().isLocationServiceAvailable()
                && requestPermission(this)) {
            m_presenter.fetchLocation();
        }
    }

    private CIProgressDialog.CIProgressDlgListener progressDlgListener = new CIProgressDialog.CIProgressDlgListener() {
        @Override
        public void onBackPressed() {
            if (null != m_presenter) {
                m_presenter.interrupt(); //by kevin
            }
        }
    };
    /************************** IGlobalServiceListener ****************************/
    //edited by Kevincheng
    @Override
    public void showProgress() {
        showProgressDialog(progressDlgListener);
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
        if(null != getActivity() && !AppInfo.getInstance(getContext()).GetIsShowLocationService()) {
            CIToastView.makeText(getActivity(), getString(R.string.gps_press_enable_gps_service)).show();
            //2018-06-22 新增GPS定位服務訊息僅顯示一次
            AppInfo.getInstance(getContext()).SetIsShowLocationService(true);
        }
    }

    @Override
    public void onLocationBinding() {
        if(null != getActivity()) {
            CIToastView.makeText(getActivity(), getString(R.string.gps_positioning)).show();
        }
        m_progressBar.setVisibility(View.VISIBLE);
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

        List<LocationItem> locationDatas = new ArrayList<>();
        for(CIGlobalServiceEntity data:list){
            LocationItem locationItem = new LocationItem();
            locationItem.Lat = data.lat;
            locationItem.Long = data.lng;
            locationDatas.add(locationItem);
        }



        //取得最近位置資料索引
        int index = getRecentDistanceIndex(gpsLocationData, locationDatas);
        CIGlobalServiceEntity data = m_list.get(index);
        m_adapter.setServiceInfo(data.branch,data.ticket_op_tel);
        if(null != getActivity()){
            m_adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onDownloadSuccess() {
        m_list = m_presenter.fetchData();
        if(null != m_list && m_list.size() > 0
                && null != getActivity()){
            initBranch();
        }
    }

    @Override
    public void onDownloadError(String rt_code, String rt_msg) {
    }
    /**************************************************************************/
}
