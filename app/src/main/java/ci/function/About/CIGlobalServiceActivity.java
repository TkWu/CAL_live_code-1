package ci.function.About;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.List;

import ci.function.About.Adapter.CIGlobalServiceListAdpater;
import ci.function.Core.CIApplication;
import ci.ui.object.AppInfo;
import ci.ws.Models.entities.CIGlobalServiceEntity;
import ci.ws.Models.entities.CIGlobalServiceList;
import ci.function.Core.Location.GpsReceiver;
import ci.function.Main.BaseActivity;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.CIProgressDialog;
import ci.ui.toast.CIToastView;
import ci.ui.view.NavigationBar;
import ci.ws.Presenter.CIGlobalServicePresenter;
import ci.ws.Presenter.Listener.IGlobalServiceListener;
import static ci.function.About.Adapter.CIGlobalServiceListAdpater.*;
import static ci.function.About.Adapter.CIGlobalServiceListAdpater.EMode.*;
import static ci.function.Core.Location.SSingleLocationUpdater.LocationItem;
import static ci.function.Core.Location.SSingleLocationUpdater.getRecentDistanceIndex;
import static ci.function.Core.Location.SSingleLocationUpdater.requestPermission;

/**
 * 主要取得全球服務辦事處資料
 * 有三種基礎功能：
 * （1）顯示全部辦事處資料（2）搜尋辦事處資料（3）GPS搜尋最近據點資料
 * Created by kevincheng on 2016/3/6.
 */
public class CIGlobalServiceActivity extends BaseActivity
        implements TextWatcher,
        GpsReceiver.Callback,
        IGlobalServiceListener {

    private EditText                    m_etSearch               = null;
    private NavigationBar               m_Navigationbar          = null;
    private CIGlobalServicePresenter    m_presenter              = null;
    private ExpandableListView          m_expandableListView     = null;
    private CIGlobalServiceListAdpater  m_Adpater                = null;
    private ArrayList<GroupItem>        m_Items                  = null;
    private ArrayList<GroupItem>        m_ItemsForNearestOffice  = null;
    private GpsReceiver                 m_GpsReceiver            = null;
    private CIGlobalServiceList         m_list                   = null;
    private CIGlobalServiceList         m_listForNearestOffice   = null;
    private EMode                       m_lastMode               = NORMAL;
    private boolean                     m_isHaveNearestOffice    = false;
    private final int                   PERMISSIONS_REQUEST_CODE = 1;

    public class GroupItem {
        public String               category;
        public ArrayList<ChildItem> childItems;
    }

    public class ChildItem {
        public int index;
    }

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return getString(R.string.global_customer_service);
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
    protected int getLayoutResourceId() {
        return R.layout.activity_about_global_service;
    }

    @Override
    protected void initialLayoutComponent() {

        m_Navigationbar         = (NavigationBar) findViewById(R.id.toolbar);
        m_etSearch              = (EditText) findViewById(R.id.et_search);
        m_presenter             = CIGlobalServicePresenter.getInstance(this);
        m_expandableListView    = (ExpandableListView) findViewById(R.id.expandableListView);

        m_list = m_presenter.fetchData();
        if(null == m_list || m_list.size() <= 0){
            m_presenter.downloadDataFromWS();
        } else {
            initBranchList();
        }
    }

    private void initBranchList(){
        if(null == m_Items){
            m_Items = new ArrayList<>();
        }
        m_Items.clear();
        DataAnalysisForCity(m_list, m_Items);
        initExpandableListView(m_Items, m_list, NORMAL);
        if(true == requestPermission(this)){
            m_presenter.fetchLocation();
        }
    }


    @Override
    public void onGpsModeChangeReceive() {
        m_presenter.interruptGPS();
        if (true == CIApplication.getSysResourceManager().isLocationServiceAvailable()
                && true == requestPermission(this)) {
            m_presenter.fetchLocation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_GpsReceiver = new GpsReceiver(this);
        IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        registerReceiver(m_GpsReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(m_GpsReceiver);
        m_GpsReceiver = null;
        m_presenter.interrupt();
    }

    private void initExpandableListView(ArrayList<GroupItem> items,
                                        CIGlobalServiceList datas,
                                        CIGlobalServiceListAdpater.EMode mode) {
        if(mode == NEAREST_OFFICE){
            if(null == m_Adpater){
                return;
            }
            m_Adpater.setDatas(datas);
            m_Adpater.setItem(items);
            m_Adpater.setMode(mode);
            m_Adpater.notifyDataSetChanged();
            expandAllGroup(items);
        } else {
            m_Adpater = new CIGlobalServiceListAdpater(this, items, datas, mode);
            m_expandableListView.setAdapter(m_Adpater);
            expandAllGroup(items);
        }
        m_lastMode = mode;
    }

    private void expandAllGroup(ArrayList<GroupItem> items){
        if(null == items || 0 >= items.size()){
            return;
        }
        for (int loop = 0; loop < items.size(); loop++) {
            m_expandableListView.expandGroup(loop);
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_ic_search_2), 24, 24);
        m_expandableListView.setDividerHeight(vScaleDef.getLayoutWidth(0));
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        m_etSearch.addTextChangedListener(this);
        m_expandableListView.setOnChildClickListener(m_OnChildClickListener);
        m_expandableListView.setOnGroupClickListener(m_onGroupClicklistener);
    }



    /**
     * 獲取是否有取得最近辦事處資料
     * @return if true 就是有取得
     */
    private boolean getIsHaveNearestOffice(){
       return m_isHaveNearestOffice;
    }

    /**
     * 設定是否有取得最近辦事處資料
     * @param isHaveNearestOffice 是否取得
     */
    private void setIsHaveNearestOffice(boolean isHaveNearestOffice){
        m_isHaveNearestOffice = isHaveNearestOffice;
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
                    m_presenter.fetchLocation();

                } else {

                    CIToastView.makeText(this,getString(R.string.gps_permissions_msg)).show();
                }
                return;
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        if(s.length() > 0){
            CIGlobalServiceList list = m_presenter.findDataByBranch(s.toString());
            ArrayList<GroupItem>  items = new ArrayList<>();
            DataAnalysisForCity(list, items);
            initExpandableListView(items, list, SEARCH);
        } else {
            if(true == getIsHaveNearestOffice()){
                initExpandableListView(m_ItemsForNearestOffice,
                                       m_listForNearestOffice,
                                       NEAREST_OFFICE);
            } else {
                initExpandableListView(m_Items,
                                       m_list,
                                       NORMAL);
            }
        }



    }
    ExpandableListView.OnGroupClickListener m_onGroupClicklistener = new ExpandableListView.OnGroupClickListener(){

        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            return true;
        }
    };
    ExpandableListView.OnChildClickListener m_OnChildClickListener = new ExpandableListView.OnChildClickListener() {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            return true;
        }
    };


    /**
     * 資料分析，依不同城市做分類至群組項目，將屬於相同
     * 城市的資料輸出至相同群組項目中的的子項目，並將分
     * 析結果輸出至items，以用於ExpandableListView顯
     * 示畫面及擷取資料用
     * @param list  CIGlobalServiceList 原始資料
     * @param items 輸出分析結構後的結果
     */
    private void DataAnalysisForCity(CIGlobalServiceList list, ArrayList<GroupItem> items){
        items.clear();
        //Log.e("size", list.size() + "");
        for(int childIndex = 0;childIndex < list.size();childIndex++ ){
            CIGlobalServiceEntity data = list.get(childIndex);
            if(0 == items.size()){
                addGroupItem(data,items);
                addChildItem(0,childIndex,items);
            } else {
                int unMatchCount = 0;
                data.continene = data.continene.replace("\n","").replace("\r","");
                for(int groupIndex = 0;groupIndex < items.size();groupIndex++){
                    if(data.continene.equals(items.get(groupIndex).category)){
                        addChildItem(groupIndex,childIndex,items);
                        break;
                    } else {
                        unMatchCount++;
                        //Log.e("unmatch",unMatchCount+"|"+items.size());
                        if(unMatchCount == items.size()){
                            //Log.e("childIndex",childIndex+"");
                            addGroupItem(data,items);
                            addChildItem(groupIndex + 1, childIndex,items);
                            break;
                        }
                    }
                }

            }
        }
    }

    private void addChildItem(int groupIndex,int childIndex,ArrayList<GroupItem> items){
        ChildItem childItem = new ChildItem();
        childItem.index = childIndex;
        if(null ==  items.get(groupIndex).childItems){
            items.get(groupIndex).childItems = new ArrayList<>();
        }
        items.get(groupIndex).childItems.add(childItem);
    }

    private void addGroupItem(CIGlobalServiceEntity data,ArrayList<GroupItem> items){
        GroupItem item = new GroupItem();
        item.category = data.continene;
        items.add(item);
    }

    private CIProgressDialog.CIProgressDlgListener progressDlgListener = new CIProgressDialog.CIProgressDlgListener() {
        @Override
        public void onBackPressed() {//by kevin
            CIGlobalServiceActivity.this.onBackPressed();
        }
    };

    /************************** IGlobalServiceListener ****************************/

    @Override
    public void showProgress() {
        showProgressDialog(progressDlgListener);
    }

    @Override
    public void onLocationBinding() {
        CIToastView.makeText(this, getString(R.string.gps_positioning)).show();
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
        CIToastView.makeText(this, getString(R.string.gps_position_fail)).show();
    }

    @Override
    public void onNoAvailableLocationProvider() {
        if ( !AppInfo.getInstance(this).GetIsShowLocationService() ){
            CIToastView.makeText(this, getString(R.string.gps_press_enable_gps_service)).show();
            //2018-06-22 新增GPS定位服務訊息僅顯示一次
            AppInfo.getInstance(this).SetIsShowLocationService(true);
        }
    }

    @Override
    public void onDownloadSuccess() {
        m_list = m_presenter.fetchData();
        if(null == m_list || m_list.size() <= 0){
            showDownloadErrorDialog("ERROR");
        } else {
            initBranchList();
        }
    }

    @Override
    public void onDownloadError(String rt_code, String rt_msg) {
        showDownloadErrorDialog(rt_msg);
    }

    private void showDownloadErrorDialog(String msg){
        showDialog(getString(R.string.warning),
                msg,
                getString(R.string.confirm),
                null,
                m_alertDialoglistener);
    }

    CIAlertDialog.OnAlertMsgDialogListener m_alertDialoglistener = new CIAlertDialog.OnAlertMsgDialogListener() {
        @Override
        public void onAlertMsgDialog_Confirm() {onBackPressed();}
        @Override
        public void onAlertMsgDialogg_Cancel() {}
    };

    /**
     * 當取得GPS Location時
     * @param gpsLocationData
     */
    @Override
    public void onLocationBinded(Location gpsLocationData) {
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
        //初始化最近辦事處資料索引
        initRecentOfficeData(list, index);
        //初始化清單(如果搜尋中則不更新清單)
        if(SEARCH != m_lastMode){
            initExpandableListView(m_ItemsForNearestOffice, m_listForNearestOffice, NEAREST_OFFICE);
        }
    }
    /**************************************************************************/

    private void initRecentOfficeData(CIGlobalServiceList list,int index){
        //將拷貝一份最近的地點資料
        CIGlobalServiceEntity data = (CIGlobalServiceEntity)list.get(index).clone();
        //建立一個新的list
        CIGlobalServiceList newList = new CIGlobalServiceList();
        //修改城市分類名稱，以便分類顯示最近的辦事處
        data.continene = getString(R.string.nearest_office);
        //再加到第一個位置
        newList.add(data);
        //將原本沒被刪除的資料後續加入
        for(CIGlobalServiceEntity entity:list){
            newList.add(entity);
        }
        ArrayList<GroupItem> items = new ArrayList<>();
        //將資料做分類分析並輸出至items
        DataAnalysisForCity(newList, items);

        m_ItemsForNearestOffice = items;
        m_listForNearestOffice  = newList;
        //設定取得最近辦事處資料
        setIsHaveNearestOffice(true);
    }

}
