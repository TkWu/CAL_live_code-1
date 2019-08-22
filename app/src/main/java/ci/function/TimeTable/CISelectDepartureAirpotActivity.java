package ci.function.TimeTable;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import java.util.Calendar;
import java.util.List;

import ci.function.Core.CIApplication;
import ci.function.Core.Location.GpsReceiver;
import ci.function.Main.BaseActivity;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.AppInfo;
import ci.ui.object.CIAirportDataManager;
import ci.ui.object.CIProgressDialog;
import ci.ui.toast.CIToastView;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Presenter.CIInquiryFlightStationPresenter;
import ci.ws.Presenter.CISelectDepartureAirportPresenter;
import ci.ws.Presenter.Listener.CIAirportListener;

import static ci.function.Core.Location.SSingleLocationUpdater.requestPermission;
import static ci.function.TimeTable.CISelectDepartureListAdpater.EMode.NEAREST_OFFICE;
import static ci.function.TimeTable.CISelectDepartureListAdpater.EMode.NORMAL;
import static ci.function.TimeTable.CISelectDepartureListAdpater.EMode.RECENT;
import static ci.function.TimeTable.CISelectDepartureListAdpater.EMode.SEARCH;


public class CISelectDepartureAirpotActivity extends BaseActivity implements CIAirportListener, GpsReceiver.Callback, TextWatcher {

    private SharedPreferences settings;

    private NavigationBar           m_Navigationbar         = null;
    private ExpandableListView      m_expandableListView    = null;
    private EditText                m_etSearch;

    private ArrayList<GroupItem>    m_Items                 = null,
                                    m_ItemsForNearestOffice = null,
                                    m_ItemSearch            = null;

    private String[]                iaitArray, timeArray;

    private CISelectDepartureAirportPresenter m_presenter   = null;

    private CIInquiryFlightStationPresenter ciInquiryFlightStationPresenter;

    private List<CIFlightStationEntity> allDeparture, nearDepartue, searchDeparture;

    private CISelectDepartureListAdpater.EMode lastMode;

    private CISelectDepartureListAdpater        m_Adpater   = null;

    private int                                 m_iStartCollapseIndex = 0;

    private final int                   PERMISSIONS_REQUEST_CODE = 1;

    private boolean m_isHaveNearestOffice   = false;
    private boolean isFlightStationEmpty    = true;
    public  boolean isRecent                = false;
    public  boolean isFirstLBS              = true;

    public final static int     FLIGHT_STATUS               = 1;
    public final static int     BOOKT_TICKET                = 2;
    public final static int     BOOKT_TICKET_ISORIGINAL_Y   = 4;
    public final static int     TIME_TABLE                  = 3;

    public final static String ESOURCE                  = "ESOURCE";
    public final static String IS_TO_FRAGMENT           = "IS_TO_FRAGMENT";
    public final static String IAIT                     = "IAIT";
    public final static String LOCALIZATION_NAME        = "localization_name";
    public final static String TAG_RECENT               = ",recent";

    private GpsReceiver m_GpsReceiver;
    private CIAirportDataManager m_manager;

    public class GroupItem {
        public String country;
        public ArrayList<ChildItem> childItems;
    }

    public class ChildItem {
        public int index;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_select_departue_airpot;
    }

    @Override
    protected void initialLayoutComponent() {

        settings = getSharedPreferences("PREF_DEMO", 0);

        //SharePreference的格式

        iaitArray = new String[]{"abbreviation0", "abbreviation1", "abbreviation2", "abbreviation3"};
        timeArray = new String[]{"time0", "time1", "time2", "time3"};

        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        m_etSearch = (EditText) findViewById(R.id.et_search);

        m_expandableListView.setDivider(null);



        //撈取presenter
        m_presenter = new CISelectDepartureAirportPresenter(this);

        m_Items = new ArrayList<>();

        m_Items.clear();

        showProgressDialog(progressDlgListener);

        m_manager = new CIAirportDataManager(m_managerCallBack);

        m_manager.fetchAirportData(getIntent().getExtras().getBoolean(IS_TO_FRAGMENT, false),
                                getIntent().getExtras().getString(IAIT),
                                getIntent().getExtras().getInt(ESOURCE, 3));

        ciInquiryFlightStationPresenter = m_manager.getFlightStationPresenter();

    }

    private void showAirport(final Boolean isRecent) {

        this.isRecent = isRecent;

        if(null == allDeparture){
            return;
        }

        runOnUiThread(new Runnable() {
            public void run() {


                //判斷是否有recent模式
                if (isRecent) {
                    DataAnalysisForCity(allDeparture, m_Items);
                    initExpandableListView(m_Items, allDeparture, RECENT);
                } else {
                    DataAnalysisForCity(allDeparture, m_Items);
                    initExpandableListView(m_Items, allDeparture, NORMAL);
                }

                //當資料全部載完才可以讓gps動作
                isFlightStationEmpty = false;
                hideProgressDialog();
                if(true == requestPermission(CISelectDepartureAirpotActivity.this)){
                    m_presenter.fetchLocation();
                }
            }
        });
    }

    CIAirportDataManager.callBack m_managerCallBack = new CIAirportDataManager.callBack(){
        @Override
        public void onDataBinded(List<CIFlightStationEntity> datas) {
            showAirport(addRecent());
        }

        @Override
        public void onDownloadFailed(String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm),
                    null,
                    alertMsgDialogListener);

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

    private CIAlertDialog.OnAlertMsgDialogListener alertMsgDialogListener = new CIAlertDialog.OnAlertMsgDialogListener() {
        @Override
        public void onAlertMsgDialog_Confirm() {
            onBackPressed();
        }

        @Override
        public void onAlertMsgDialogg_Cancel() {
        }
    };

    private CIProgressDialog.CIProgressDlgListener progressDlgListener = new CIProgressDialog.CIProgressDlgListener() {
        @Override
        public void onBackPressed() {
            if (null != ciInquiryFlightStationPresenter) {
                ciInquiryFlightStationPresenter.cancel(); //by kevin
                CISelectDepartureAirpotActivity.this.onBackPressed();
            }
        }
    };
    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {

            if (getIntent().getExtras().getBoolean(IS_TO_FRAGMENT, false)) {
                return m_Context.getString(R.string.select_arrival_airport_title);
            } else {
                return m_Context.getString(R.string.select_departure_airport_title);
            }
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

    public void onBackPressed() {
        HidekeyBoard();

        this.finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }


    @Override
    protected void onResume() {
        super.onResume();

        //註冊GPS開啟通知

        m_GpsReceiver = new GpsReceiver(this);
        IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        registerReceiver(m_GpsReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();

        //註銷GPS開啟通知

        try {
            unregisterReceiver(m_GpsReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        m_GpsReceiver = null;
        m_presenter.interrupt();
    }

    @Override
    public void onGpsModeChangeReceive() {

        //當gps開啟會觸發
        m_presenter.interrupt();
        if (true == CIApplication.getSysResourceManager().isLocationServiceAvailable()
                && true == requestPermission(this)) {

            if (!isFlightStationEmpty) {
                m_presenter.fetchLocation();
            }
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
//        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.img_done), 48, 48);
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        m_etSearch.addTextChangedListener(this);
        m_expandableListView.setOnGroupClickListener(m_OnGroupClicklistener);
        m_expandableListView.setOnChildClickListener(m_OnChildClickListener);
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
    protected void onLanguageChangeUpdateUI() {

    }

    ExpandableListView.OnGroupClickListener m_OnGroupClicklistener = new ExpandableListView.OnGroupClickListener() {

        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            if(groupPosition < m_iStartCollapseIndex){
                return true;
            }
            return false;
        }
    };
    ExpandableListView.OnChildClickListener m_OnChildClickListener = new ExpandableListView.OnChildClickListener() {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

            Intent intent = new Intent();

            String iata , localization_name ;

            List<CIFlightStationEntity> datas;
            ArrayList<GroupItem>        items;
            int                         index;
            //透過不同的mode抓不同的child data
            if (lastMode == RECENT || lastMode == NORMAL) {
                datas   = allDeparture;
                items   = m_Items;
            } else if (lastMode == SEARCH) {
                datas   = searchDeparture;
                items   = m_ItemSearch;
            } else if (lastMode == NEAREST_OFFICE) {
                datas   = nearDepartue;
                items   = m_ItemsForNearestOffice;
            }else {
                datas   = allDeparture;
                items   = m_Items;
            }
            index               = items.get(groupPosition).childItems.get(childPosition).index;
            iata                = datas.get(index).IATA;
            localization_name   = datas.get(index).localization_name;

            addRecentAndCheck(iata);

            //送出機場區名稱
            intent.putExtra(LOCALIZATION_NAME, localization_name);

            //送出機場代號
            intent.putExtra(IAIT, iata);

            //排序
            recentSequence();

            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);

            return true;
        }
    };

    private void addRecentAndCheck(String IAIT) {

        Calendar calendar = Calendar.getInstance();

        int isTheSame = 0;

        //將四筆資料取出判斷是否重複
        for (int i = 0; i < 4; i++) {
            if (settings.getString(iaitArray[i], "").equals(IAIT)) {
                settings.edit()
                        .putString(iaitArray[i], IAIT)
                        .putLong(timeArray[i], calendar.getTimeInMillis())
                        .commit();
                isTheSame++;
            }
        }

        //沒有重複的話
        if (isTheSame == 0) {

            int isCount = 0;

            //是否為空值
            for (int i = 0; i < 4; i++) {
                if (settings.getString(iaitArray[i], "").equals("")) {
                    settings.edit()
                            .putString(iaitArray[i], IAIT)
                            .putLong(timeArray[i], calendar.getTimeInMillis())
                            .commit();
                    isCount++;
                    break;
                }
            }

            if (isCount == 0) {//如果等於零就是沒有存

                Long max = 0l;

                //換掉最舊的時間(因為每次結束都會排序)
                settings.edit()
                        .putString(iaitArray[3], IAIT)
                        .putLong(timeArray[3], calendar.getTimeInMillis())
                        .commit();


            }

        }
    }

    private void recentSequence() {

        String tampAbbreviation;

        long tampTime;

        //利用時間排序 最新最上面
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                if (settings.getLong(timeArray[i], 0) < settings.getLong(timeArray[j], 0)) {

                    tampAbbreviation = settings.getString(iaitArray[i], "");
                    tampTime = settings.getLong(timeArray[i], 0);

                    settings.edit()
                            .putString(iaitArray[i], settings.getString(iaitArray[j], ""))
                            .putLong(timeArray[i], settings.getLong(timeArray[j], 0))
                            .commit();

                    settings.edit()
                            .putString(iaitArray[j], tampAbbreviation)
                            .putLong(timeArray[j], tampTime)
                            .commit();

                }
            }
        }
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

    /**
     * 資料分析，依不同城市做分類至群組項目，將屬於相同
     * 城市的資料輸出至相同群組項目中的的子項目，並將分
     * 析結果輸出至items，以用於ExpandableListView顯
     * 示畫面及擷取資料用
     *
     * @param list 原始資料
     * @param items 輸出分析結構後的結果
     */
    private void DataAnalysisForCity(List<CIFlightStationEntity> list, ArrayList<GroupItem> items) {



        //將所有的list跑過一遍
        for (int childIndex = 0; childIndex < list.size(); childIndex++) {

            CIFlightStationEntity data = list.get(childIndex);

            if (0 == items.size()) {

                //預設第一個就是group
                addGroupItem(data, items);
                addChildItem(0, childIndex, items);
            } else {

                int unMatchCount = 0;

                //將現有加完的items檢查裡面的country是否相同，若是就只加child，若否就加新的item
                for (int groupIndex = 0; groupIndex < items.size(); groupIndex++) {

                    String[] childType = data.country.split(",");


                    //可能為recent or near airport格式會不一樣
                    if (childType.length > 1) {

                        String[] groupType = items.get(groupIndex).country.split(",");

                        if (groupType.length > 1) {
                            if (childType[1].equals(groupType[1])) {
                                addChildItem(groupIndex, childIndex, items);
                                break;

                            } else {
                                unMatchCount++;

                                if (unMatchCount == items.size()) {

                                    addGroupItem(data, items);

                                    if (childType[0].equals("title")) {
                                        items.get(groupIndex + 1).childItems = new ArrayList();
                                    } else {
                                        addChildItem(groupIndex + 1, childIndex, items);
                                    }

                                    break;
                                }
                            }
                        }

                    } else {

                        if (data.country.equals(items.get(groupIndex).country)) {
                            addChildItem(groupIndex, childIndex, items);
                            break;

                        } else {
                            unMatchCount++;

                            if (unMatchCount == items.size()) {
                                addGroupItem(data, items);
                                addChildItem(groupIndex + 1, childIndex, items);
                                break;
                            }
                        }
                    }

                }
            }


        }
    }

    private void addChildItem(int groupIndex, int childIndex, ArrayList<GroupItem> items) {

        CISelectDepartureAirpotActivity CISelectDepartureAirpotActivity = new CISelectDepartureAirpotActivity();

        ChildItem childItem = CISelectDepartureAirpotActivity.new ChildItem();

        //利用index判斷是資料的哪一筆
        childItem.index = childIndex;

        if (null == items.get(groupIndex).childItems) {
            items.get(groupIndex).childItems = new ArrayList();
        }
        items.get(groupIndex).childItems.add(childItem);
    }

    private void addGroupItem(CIFlightStationEntity data, ArrayList<GroupItem> items) {
        CISelectDepartureAirpotActivity CISelectDepartureAirpotActivity = new CISelectDepartureAirpotActivity();
        GroupItem item = CISelectDepartureAirpotActivity.new GroupItem();

        //把新增的Group設定country
        item.country = data.country;
        items.add(item);
    }

    /**************************
     * CIAirportListener
     ****************************/

    @Override
    public void showProgress() {
        CIToastView.makeText(this, getString(R.string.gps_positioning)).show();
    }


    @Override
    public void hideProgress() {
    }


    /**
     * 當取的GPS Location 失敗時
     */
    @Override
    public void onfetchLocationFail() {
        CIToastView.makeText(this,  getString(R.string.gps_position_fail)).show();
    }


    @Override
    public void onNoAvailableLocationProvider() {
        if ( !AppInfo.getInstance(this).GetIsShowLocationService() ){
            CIToastView.makeText(this, getString(R.string.gps_press_enable_gps_service)).show();
            //2018-06-22 新增GPS定位服務訊息僅顯示一次
            AppInfo.getInstance(this).SetIsShowLocationService(true);
        }
    }


    /**
     * 獲取是否有取得最近辦事處資料
     *
     * @return if true 就是有取得
     */
    private boolean getIsHaveNearestOffice() {
        return m_isHaveNearestOffice;
    }

    /**
     * 設定是否有取得最近辦事處資料
     *
     * @param isHaveNearestOffice 是否取得
     */
    private void setIsHaveNearestOffice(boolean isHaveNearestOffice) {
        m_isHaveNearestOffice = isHaveNearestOffice;
    }

    /**
     * 當取得GPS Location時
     *
     * @param location
     */
    @Override
    public void onLocationBinded(Location location) {
        m_ItemsForNearestOffice = new ArrayList<>();

        nearDepartue = (ArrayList<CIFlightStationEntity>) ((ArrayList<CIFlightStationEntity>) allDeparture).clone();
        //計算GPS距離
        calcDistance(location, nearDepartue);

        //初始化清單(如果搜尋中則不更新清單)
        if(lastMode != SEARCH){
            initExpandableListView(m_ItemsForNearestOffice, nearDepartue, NEAREST_OFFICE);
        }
    }
    /**************************************************************************/

    /**
     * 計算所有辦事處距離與GPS座標比較後重整資料，並初始化資料列表
     */
    private void calcDistance(Location location, List<CIFlightStationEntity> list) {

        //儲存單次計算出的距離
        float result[] = new float[1];
        //儲存所有計算出的距離
        float savedDistance[] = new float[list.size()];
        for (int loop = 0; loop < list.size(); loop++) {
            double  dLat , dLong ;
            try{
                dLat    = Double.valueOf(list.get(loop).latitude);
                dLong   = Double.valueOf(list.get(loop).longitude);
                location.distanceBetween(location.getLatitude(),
                        location.getLongitude(),
                        dLat,
                        dLong,
                        result);
                //Log.e("calcDistanceResult", result[0] + "");
                savedDistance[loop] = result[0];
            }catch (Exception e){
                savedDistance[loop] = Float.MAX_VALUE;
            }
        }
        //將第一筆儲存的距離資料設定給最小
        float minDistance = savedDistance[0];
        for (int loop = 0; loop < savedDistance.length; loop++) {
            //比較距離，將最小的設定給minDistance
            minDistance = Math.min(savedDistance[loop], minDistance);
            //Log.e("minF", minDistance + "");
        }
        int index = 0;
        for (float f : savedDistance) {
            if (f == minDistance) {
                //比對距離以取得距離最近距離的物件
                //Log.e("result", index + "");
                break;
            }
            index++;
        }
        //將拷貝一份最近的地點資料
        CIFlightStationEntity data = (CIFlightStationEntity) list.get(index).clone();
        //建立一個新的list
        List<CIFlightStationEntity> newList = new ArrayList<>();
        //修改城市分類名稱，以便分類顯示最近的辦事處

        String[] strArray = data.country.split(",");

        if (strArray.length > 1) {
            data.country = strArray[0] + ",near airport";
        } else {
            data.country += ",near airport";
        }

        //再加到第一個位置
        newList.add(data);
        //將原本沒被刪除的資料後續加入
        for (CIFlightStationEntity entity : list) {
            newList.add(entity);
        }

        nearDepartue = newList;

        ArrayList<GroupItem> items = new ArrayList<>();
        //將資料做分類分析並輸出至items
        DataAnalysisForCity(nearDepartue, m_ItemsForNearestOffice);

//        Log.e("Logic", new Gson().toJson(items));

        //設定取得最近辦事處資料
        setIsHaveNearestOffice(true);
    }

    private void initExpandableListView(final ArrayList<GroupItem> items,
                                        final List<CIFlightStationEntity> datas,
                                        final CISelectDepartureListAdpater.EMode mode) {
        /**ExpandableListView*/


        if(mode == NEAREST_OFFICE && SEARCH != lastMode){
            if(null == m_Adpater){
                return;
            }
            ArrayList<Integer> arInt = new ArrayList<>();
            /*
            * 如果是第一次取得最近機場，則記錄已經打開的群組並加一，因為資料更新而新增了最近機場會增加一個群組
            * ，導致notifyDataSetChanged()後，原本List所記錄的開啟的群組位置會因為新增群組而展開了錯誤的群組
            * ，所以必需判斷是否有展開的群組並加一，先收合，並利用記錄並調整過的展開群組位置，重新展開，才不會
            * 展開錯誤的群組，而且也只有進入頁面後，第一次取得最近機場時才需要做這個動作。
            */
            if(true == isFirstLBS){

                int itemSize = m_Adpater.getItem().size();
                for(int loop = 0 ; loop < itemSize ; loop++){
                    if(true == m_expandableListView.isGroupExpanded(loop)){
                        arInt.add(loop + 1);
                        m_expandableListView.collapseGroup(loop);
                    }
                }
            }
            m_Adpater.setItem(items);
            m_Adpater.setDatas(datas);
            m_Adpater.setMode(mode);
            m_Adpater.notifyDataSetChanged();
            /*第一次取得最近機場資料時，重新使用調整過的展開群組位置去展開*/
            if(true == isFirstLBS){
                for(Integer loop:arInt){
                    m_expandableListView.expandGroup(loop);
                }
            }
            isFirstLBS = false;
        } else {
            m_Adpater = new CISelectDepartureListAdpater(CISelectDepartureAirpotActivity.this, items, datas, mode);
            m_expandableListView.setAdapter(m_Adpater);
        }

        /*此行須在需要放在判斷上次模式的位置之後*/
        lastMode = mode;

        //初始化開始收和群組的地方
        m_iStartCollapseIndex = 0;

        //依據目前顯示清單的模式，決定開始收合群組的位置
        if (mode == RECENT) {
            m_iStartCollapseIndex = 1;
            if (true == getIsHaveNearestOffice()) {
                m_iStartCollapseIndex = 2;
            }
        } else if (mode == NORMAL) {
            m_iStartCollapseIndex = 0;
            if (true == getIsHaveNearestOffice()) {
                m_iStartCollapseIndex = 1;
            }
        } else if (mode == NEAREST_OFFICE) {
            m_iStartCollapseIndex = 1;
            if(true == isRecent){
                m_iStartCollapseIndex = 2;
            }
        } else if (mode == SEARCH) {
            m_iStartCollapseIndex = items.size();
        }

        if(0 >= items.size()){
            return;
        }

        for (int loop = 0; loop < m_iStartCollapseIndex; loop++) {
            m_expandableListView.expandGroup(loop);
        }

    }


    private boolean addRecent() {

        int totalRecent = 0;

        ArrayList<CIFlightStationEntity> newList = new ArrayList();


        //抵達地
        if (getIntent().getExtras().getBoolean(IS_TO_FRAGMENT, false)) {

            //接收arrival資料
            allDeparture = ciInquiryFlightStationPresenter.getArrivalSrtationListByDeparture(getIntent().getExtras().getString(IAIT));

            for (int i = 0; i < 4; i++) {


                CIFlightStationEntity data = new CIFlightStationEntity();

                //判斷是否最近點擊有再arrival範圍內
                ArrayList<CIFlightStationEntity> findedData
                        = ciInquiryFlightStationPresenter
                        .getArrivalSrtationListByIATA(
                                getIntent()
                                        .getExtras()
                                        .getString(IAIT),
                                settings.getString(iaitArray[i], "null"));

                if (findedData != null) {


                    data.IATA = settings.getString(iaitArray[i], "");
                    data.country = findedData.get(0).country + TAG_RECENT;
                    data.airport_name = findedData.get(0).airport_name;
                    data.localization_name = findedData.get(0).localization_name;

                    //再加到第一個位置
                    newList.add(data);

                    totalRecent++;
                }
            }
        } else {

            //接收depature資料
            allDeparture = ciInquiryFlightStationPresenter.getDepatureStationList();

            //判斷是否最近點擊有再departure範圍內
            for (int i = 0; i < 4; i++) {


                CIFlightStationEntity data = new CIFlightStationEntity();

                List<CIFlightStationEntity> findedData = ciInquiryFlightStationPresenter.getDepatureStationListByIATA(settings.getString(iaitArray[i], "null"));

                if (findedData != null) {


                    data.IATA = settings.getString(iaitArray[i], "");
                    data.country = findedData.get(0).country + TAG_RECENT;
                    data.airport_name = findedData.get(0).airport_name;
                    data.localization_name = findedData.get(0).localization_name;

                    //再加到第一個位置
                    newList.add(data);

                    totalRecent++;
                }
            }
        }

        if(null == allDeparture){
            showDialog(getString(R.string.warning),
                    getString(R.string.download_airport_data_fail),
                    getString(R.string.confirm),
                    null,
                    alertMsgDialogListener);
            hideProgressDialog();
            return false;
        }


        //將原本沒被刪除的資料後續加入
        for (CIFlightStationEntity entity : allDeparture) {
            newList.add(entity);
        }

        allDeparture = newList;

        if (totalRecent > 0) {
            return true;
        } else {
            return false;
        }

    }

    private void changeActivity(Class clazz) {
        Intent intent = new Intent();
        intent.setClass(m_Context, clazz);
        startActivity(intent);

        //測試切換activity滑入滑出動畫
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {

            List<CIFlightStationEntity> searchDepartue;
            ArrayList<GroupItem> items;

            //判斷是否為抵達地
            if (getIntent().getExtras().getBoolean(IS_TO_FRAGMENT, false)) {
                searchDepartue = ciInquiryFlightStationPresenter.getArrivalSrtationListByDeparture(getIntent().getExtras().get(IAIT).toString(), s.toString());
                items = new ArrayList<>();
            } else {
                searchDepartue = ciInquiryFlightStationPresenter.getDepatureStationListByKeyword(s.toString(), false);
                items = new ArrayList<>();
            }

            //是否找不到資料
            if (searchDepartue == null) {

                DataAnalysisForCity(new ArrayList<CIFlightStationEntity>(), items);
                initExpandableListView(items, searchDepartue, SEARCH);

            }else{

                DataAnalysisForCity(searchDepartue, items);
                initExpandableListView(items, searchDepartue, SEARCH);

            }

            searchDeparture = searchDepartue;
            m_ItemSearch = items;

        } else {

            if (true == getIsHaveNearestOffice()) {
                initExpandableListView(m_ItemsForNearestOffice,
                        nearDepartue,
                        NEAREST_OFFICE);
            } else {

                if (isRecent) {
                    initExpandableListView(m_Items, allDeparture, RECENT);
                } else {
                    initExpandableListView(m_Items, allDeparture, NORMAL);
                }
            }
        }

    }
}