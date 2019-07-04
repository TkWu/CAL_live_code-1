package ci.function.Checkin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.ui.TextField.Adapter.CIMenusAdapter;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.CIAirportDataManager;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIApisDocmuntTypeEntity;
import ci.ws.Models.entities.CIApisQryRespEntity;
import ci.ws.Presenter.CIAPISPresenter;
import ci.ws.Presenter.Listener.CIInquiryApisListListener;


public class CIAPISCheckInDocmuntTypeSelectMenuActivity
{
//    extends
//} BaseActivity implements TextWatcher {
//
//
//    private NavigationBar           m_Navigationbar    = null;
//    private ExpandableListView m_expandableListView    = null;
//    private EditText                m_etSearch;
//
//    public  boolean haveSavedApis                        = false;
//
//    private ArrayList<GroupItem>    m_Items            = null,
//                                    m_ItemSearch       = null;
//
//    private List<CIApisQryRespEntity.CIApispaxInfo> savedDocs;
//    private List<CIApisDocmuntTypeEntity> allDocs;
//
//    public class GroupItem {
//        public String  apis_group;
//        public ArrayList<ChildItem> childItems;
//    }
//
//    public class ChildItem {
//        public int index;
//    }
//
//    private CIAPISPresenter apis_presenter_= null;
//
//    private CIAPISCheckInDocmuntTypeListAdapter m_Adpater   = null;
//
//    @Override
//    protected int getLayoutResourceId() {
//        return R.layout.activity_select_checkin_apis;
//    }
//
//
//    @Override
//    protected void initialLayoutComponent() {
//
//
//        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
//        m_expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
//        m_etSearch = (EditText) findViewById(R.id.et_search);
//
//        m_expandableListView.setDivider(null);
//
//        //撈取presenter
//        apis_presenter_ = new CIAPISPresenter();
//
//        m_Items = new ArrayList<>();
//
//        m_Items.clear();
//
//        showProgressDialog(progressDlgListener);
////
////        m_manager = new CIAirportDataManager(m_managerCallBack);
////
////        m_manager.fetchAirportData(getIntent().getExtras().getBoolean(IS_TO_FRAGMENT, false),
////                getIntent().getExtras().getString(IAIT),
////                getIntent().getExtras().getInt(ESOURCE, 3));
////
////        ciInquiryFlightStationPresenter = m_manager.getFlightStationPresenter();
//
//    }
//
//    private void showApis(final Boolean isRecent) {
//
//        if(null == allDocs){
//            return;
//        }
//
//        runOnUiThread(new Runnable() {
//            public void run() {
//            //判斷是否有 SAVED DOCS
//            if (haveSavedApis) {
//                DataAnalysisForCity(m_Items, allDocs, savedDocs);
//                initExpandableListView(m_Items, allDocs, savedDocs);
//            } else {
//                initExpandableListView(m_Items, allDocs, null);
//            }
//
//            }
//        });
//    }
//
//    //CIAPISPresenter.getInstance().InquiryMyApisListNewFromWS(CIApplication.getLoginInfo().GetUserMemberCardNo(), m_InquiryApisListListener);
//
//    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {
//
//        @Override
//        public Boolean GetToolbarType() {
//            return false;
//        }
//
//        @Override
//        public String GetTitle() {
//            return m_Context.getString(R.string.select_error_msg) + " "+ m_Context.getString(R.string.my_apis);
//
//        }
//    };
//
//    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {
//
//        @Override
//        public void onRightMenuClick() {
//        }
//
//        @Override
//        public void onLeftMenuClick() {
//        }
//
//        @Override
//        public void onBackClick() {
//            onBackPressed();
//        }
//
//        @Override
//        public void onDeleteClick() {
//
//        }
//
//        @Override
//        public void onDemoModeClick() {
//        }
//    };
//
//    public void onBackPressed() {
//        HidekeyBoard();
//
//        this.finish();
//        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
//        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
//    }
//
//    @Override
//    protected void setOnParameterAndListener() {
//        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
//        m_etSearch.addTextChangedListener(this);
//        m_expandableListView.setOnGroupClickListener(m_OnGroupClicklistener);
//        m_expandableListView.setOnChildClickListener(m_OnChildClickListener);
//    }
//
//    @Override
//    protected void registerFragment(FragmentManager fragmentManager) {
//
//    }
//
//    @Override
//    protected boolean bOtherHandleMessage(Message msg) {
//        return false;
//    }
//
//    @Override
//    protected void removeOtherHandleMessage() {
//
//    }
//
//    @Override
//    protected void onLanguageChangeUpdateUI() {
//
//    }
//
//    ExpandableListView.OnGroupClickListener m_OnGroupClicklistener = new ExpandableListView.OnGroupClickListener() {
//        @Override
//        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//            return false;
//        }
//    };
//
//    ExpandableListView.OnChildClickListener m_OnChildClickListener = new ExpandableListView.OnChildClickListener() {
//
//        @Override
//        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//
//            Intent intent = new Intent();
//
////            String iata , localization_name ;
////
////            List<CIFlightStationEntity> datas;
////            ArrayList<GroupItem>        items;
////            int                         index;
////            //透過不同的mode抓不同的child data
////            if (lastMode == RECENT || lastMode == NORMAL) {
////                datas   = allDeparture;
////                items   = m_Items;
////            } else if (lastMode == SEARCH) {
////                datas   = searchDeparture;
////                items   = m_ItemSearch;
////            } else if (lastMode == NEAREST_OFFICE) {
////                datas   = nearDepartue;
////                items   = m_ItemsForNearestOffice;
////            }else {
////                datas   = allDeparture;
////                items   = m_Items;
////            }
////            index               = items.get(groupPosition).childItems.get(childPosition).index;
////            iata                = datas.get(index).IATA;
////            localization_name   = datas.get(index).localization_name;
////
////            addRecentAndCheck(iata);
////
////            //送出機場區名稱
////            intent.putExtra(LOCALIZATION_NAME, localization_name);
////
////            //送出機場代號
////            intent.putExtra(IAIT, iata);
////
////            //排序
////            recentSequence();
//
//            setResult(RESULT_OK, intent);
//            finish();
//            overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
//
//            return true;
//        }
//    };
//
//    private void DataAnalysisForCity(ArrayList<GroupItem>, List<CIApisQryRespEntity.CIApispaxInfo>, List<CIApisDocmuntTypeEntity>) {
//
//    }
//
//    private void addChildItem(int groupIndex, int childIndex, ArrayList<GroupItem> items) {
//
//        CISelectDepartureAirpotActivity CISelectDepartureAirpotActivity = new CISelectDepartureAirpotActivity();
//
//        ChildItem childItem = CISelectDepartureAirpotActivity.new ChildItem();
//
//        //利用index判斷是資料的哪一筆
//        childItem.index = childIndex;
//
//        if (null == items.get(groupIndex).childItems) {
//            items.get(groupIndex).childItems = new ArrayList();
//        }
//        items.get(groupIndex).childItems.add(childItem);
//    }
//
//    private void addGroupItem(CIFlightStationEntity data, ArrayList<GroupItem> items) {
//        CISelectDepartureAirpotActivity CISelectDepartureAirpotActivity = new CISelectDepartureAirpotActivity();
//        GroupItem item = CISelectDepartureAirpotActivity.new GroupItem();
//
//        //把新增的Group設定country
//        item.country = data.country;
//        items.add(item);
//    }
//
//    private void initExpandableListView(final ArrayList<GroupItem> items,
//                                        final List<CIFlightStationEntity> datas,
//                                        final CISelectDepartureListAdpater.EMode mode) {
//        /**ExpandableListView*/
//
//
//        if(mode == NEAREST_OFFICE && SEARCH != lastMode){
//            if(null == m_Adpater){
//                return;
//            }
//            ArrayList<Integer> arInt = new ArrayList<>();
//            /*
//             * 如果是第一次取得最近機場，則記錄已經打開的群組並加一，因為資料更新而新增了最近機場會增加一個群組
//             * ，導致notifyDataSetChanged()後，原本List所記錄的開啟的群組位置會因為新增群組而展開了錯誤的群組
//             * ，所以必需判斷是否有展開的群組並加一，先收合，並利用記錄並調整過的展開群組位置，重新展開，才不會
//             * 展開錯誤的群組，而且也只有進入頁面後，第一次取得最近機場時才需要做這個動作。
//             */
//            if(true == isFirstLBS){
//
//                int itemSize = m_Adpater.getItem().size();
//                for(int loop = 0 ; loop < itemSize ; loop++){
//                    if(true == m_expandableListView.isGroupExpanded(loop)){
//                        arInt.add(loop + 1);
//                        m_expandableListView.collapseGroup(loop);
//                    }
//                }
//            }
//            m_Adpater.setItem(items);
//            m_Adpater.setDatas(datas);
//            m_Adpater.setMode(mode);
//            m_Adpater.notifyDataSetChanged();
//            /*第一次取得最近機場資料時，重新使用調整過的展開群組位置去展開*/
//            if(true == isFirstLBS){
//                for(Integer loop:arInt){
//                    m_expandableListView.expandGroup(loop);
//                }
//            }
//            isFirstLBS = false;
//        } else {
//            m_Adpater = new CISelectDepartureListAdpater(CISelectDepartureAirpotActivity.this, items, datas, mode);
//            m_expandableListView.setAdapter(m_Adpater);
//        }
//
//        /*此行須在需要放在判斷上次模式的位置之後*/
//        lastMode = mode;
//
//        //初始化開始收和群組的地方
//        m_iStartCollapseIndex = 0;
//
//        //依據目前顯示清單的模式，決定開始收合群組的位置
//        if (mode == RECENT) {
//            m_iStartCollapseIndex = 1;
//            if (true == getIsHaveNearestOffice()) {
//                m_iStartCollapseIndex = 2;
//            }
//        } else if (mode == NORMAL) {
//            m_iStartCollapseIndex = 0;
//            if (true == getIsHaveNearestOffice()) {
//                m_iStartCollapseIndex = 1;
//            }
//        } else if (mode == NEAREST_OFFICE) {
//            m_iStartCollapseIndex = 1;
//            if(true == isRecent){
//                m_iStartCollapseIndex = 2;
//            }
//        } else if (mode == SEARCH) {
//            m_iStartCollapseIndex = items.size();
//        }
//
//        if(0 >= items.size()){
//            return;
//        }
//
//        for (int loop = 0; loop < m_iStartCollapseIndex; loop++) {
//            m_expandableListView.expandGroup(loop);
//        }
//
//    }
//
//    @Override
//    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//    }
//
//    @Override
//    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//    }
//
//    @Override
//    public void afterTextChanged(Editable s) {
//        if (s.length() > 0) {
//
//            List<CIFlightStationEntity> searchDepartue;
//            ArrayList<GroupItem> items;
//
//            //判斷是否為抵達地
//            if (getIntent().getExtras().getBoolean(IS_TO_FRAGMENT, false)) {
//                searchDepartue = ciInquiryFlightStationPresenter.getArrivalSrtationListByDeparture(getIntent().getExtras().get(IAIT).toString(), s.toString());
//                items = new ArrayList<>();
//            } else {
//                searchDepartue = ciInquiryFlightStationPresenter.getDepatureStationListByKeyword(s.toString(), false);
//                items = new ArrayList<>();
//            }
//
//            //是否找不到資料
//            if (searchDepartue == null) {
//
//                DataAnalysisForCity(new ArrayList<CIFlightStationEntity>(), items);
//                initExpandableListView(items, searchDepartue, SEARCH);
//
//            }else{
//
//                DataAnalysisForCity(searchDepartue, items);
//                initExpandableListView(items, searchDepartue, SEARCH);
//
//            }
//
//            searchDeparture = searchDepartue;
//            m_ItemSearch = items;
//
//        } else {
//
//            if (true == getIsHaveNearestOffice()) {
//                initExpandableListView(m_ItemsForNearestOffice,
//                        nearDepartue,
//                        NEAREST_OFFICE);
//            } else {
//
//                if (isRecent) {
//                    initExpandableListView(m_Items, allDeparture, RECENT);
//                } else {
//                    initExpandableListView(m_Items, allDeparture, NORMAL);
//                }
//            }
//        }
//
//    }


}
