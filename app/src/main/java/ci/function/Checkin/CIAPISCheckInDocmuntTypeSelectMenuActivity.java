package ci.function.Checkin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.ui.TextField.Adapter.CIMenusAdapter;
import ci.ui.TextField.CIApisDocmuntTextFieldFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIApisDocmuntTypeEntity;
import ci.ws.Models.entities.CIApisQryRespEntity;
import ci.ws.Presenter.CIAPISPresenter;



public class CIAPISCheckInDocmuntTypeSelectMenuActivity extends BaseActivity {

    public static final String                  BUNDLE_ACTIVITY_DATA_FILTER_LIST = "ActivityFilterList";
    public static final String                  BUNDLE_ACTIVITY_DATA_SELECT_LIST = "ActivitySelectList";
    public static final String                  BUNDLE_ACTIVITY_DATA_SAVED_LIST  = "ActivitySavedList";

    private NavigationBar           m_Navigationbar    = null;
    private ExpandableListView m_expandableListView    = null;
    private EditText                m_etSearch;

    public  boolean haveSavedApis                        = false;

    private ArrayList<GroupItem>    m_Items            = null;

    private ArrayList<CIApisQryRespEntity.ApisRespDocObj> SavedDocs;

    private ArrayList<CIApisDocmuntTypeEntity> AllDocs;

    private ArrayList<String>                   m_arString              = null;

    private String                              m_strHint           = "";
    private CIApisDocmuntTextFieldFragment.EType m_apisType         = null;
    public static final String                  VALUE               = "VALUE";
    public static final String                  DOCUMUNT_TYPE       = "DOCUMUNT_TYPE";

    private HashSet<CIApisDocmuntTypeEntity> m_SelectList = null;
    private HashSet<CIApisQryRespEntity.ApisRespDocObj> m_SelectSavedList = null;

    public class GroupItem {
        public String  apis_group_name;
        public ArrayList<Object> childItems;
    }

    public class ChildItem {
        public Object childObj;
    }

    private CIAPISPresenter apis_presenter = null;

    private CIAPISCheckInDocmuntTypeListAdapter m_Adpater = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        if(null != bundle){
            m_strHint = bundle.getString(UiMessageDef.BUNDLE_ACTIVITY_DATA_HINT);
            if(!TextUtils.isEmpty(m_strHint)){
                m_strHint = m_strHint.replace("*", "");
            }

            m_apisType = (CIApisDocmuntTextFieldFragment.EType)bundle.getSerializable(CIApisDocmuntTextFieldFragment.APIS_TYPE);

            m_SelectList = (HashSet<CIApisDocmuntTypeEntity>)bundle.getSerializable(BUNDLE_ACTIVITY_DATA_SELECT_LIST);
            m_SelectSavedList = (HashSet<CIApisQryRespEntity.ApisRespDocObj>)bundle.getSerializable(BUNDLE_ACTIVITY_DATA_SAVED_LIST);

        }
        super.onCreate(savedInstanceState);
    }

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            if(null != m_strHint){
                return m_strHint;
            } else {
                return "";
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
        public void onDeleteClick() {}

        @Override
        public void onDemoModeClick() {}
    };

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_select_checkin_apis;
    }


    @Override
    protected void initialLayoutComponent() {

        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);

        m_expandableListView.setDivider(null);

        if(m_apisType == CIApisDocmuntTextFieldFragment.EType.Personal) {
            AllDocs = CIAPISPresenter.getInstance().fetchAllApisList();
        } else if( m_apisType == CIApisDocmuntTextFieldFragment.EType.CheckIn ){
            AllDocs = CIAPISPresenter.getInstance().fetchAllApisList();
            //m_arDocmuntType = CIAPISPresenter.getInstance().fetchApisList();
        } else {
            AllDocs = new ArrayList<>();
        }

        m_arString      = new ArrayList<>();

        if ( null != m_SelectList ) {
            for (Iterator<CIApisDocmuntTypeEntity> iterator = AllDocs.iterator(); iterator.hasNext(); ) {

                CIApisDocmuntTypeEntity data = iterator.next();
                boolean bContains = false;
                for (CIApisDocmuntTypeEntity enity : m_SelectList) {
                    if (TextUtils.equals(enity.code_1A, data.code_1A)) {
                        //沒帶國籍，則不過濾國籍
                        if (TextUtils.isEmpty(enity.issued_country)) {
                            bContains = true;
                            break;
                        } else if (TextUtils.equals(enity.issued_country, data.issued_country)) {
                            //有帶國籍參數，才過濾國籍
                            bContains = true;
                            break;
                        }
                    }
                }
                if (!bContains) {
                    iterator.remove();
                }
            }
        }
        if ( null != m_SelectList ){
            for( Iterator<CIApisQryRespEntity.ApisRespDocObj> iterator = SavedDocs.iterator(); iterator.hasNext(); ) {

                CIApisQryRespEntity.ApisRespDocObj data = iterator.next();
                boolean bContains = false;
                for ( CIApisDocmuntTypeEntity enity : m_SelectList ){
                    if( TextUtils.equals(enity.code_1A, data.documentType) ) {
                        //沒帶國籍，則不過濾國籍
                        if ( TextUtils.isEmpty(enity.issued_country) ){
                            bContains = true;
                            break;
                        } else if ( TextUtils.equals(enity.issued_country, data.documentType) ){
                            //有帶國籍參數，才過濾國籍
                            bContains = true;
                            break;
                        }
                    }
                }
                if ( !bContains ){
                    iterator.remove();
                }
            }
        }

        Locale locale = CIApplication.getLanguageInfo().getLanguage_Locale();
        for( CIApisDocmuntTypeEntity data : AllDocs ) {
            m_arString.add(data.getName(locale));
        }

        m_Items = new ArrayList<>();

        m_Items.clear();

        DataAnalysisDocs(SavedDocs,AllDocs);

        m_Adpater = new CIAPISCheckInDocmuntTypeListAdapter(this,
                m_Items,
                R.layout.activity_select_checkin_apis);
        m_expandableListView.setAdapter(m_Adpater);

    }

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

    //CIAPISPresenter.getInstance().InquiryMyApisListNewFromWS(CIApplication.getLoginInfo().GetUserMemberCardNo(), m_InquiryApisListListener);

    private void DataAnalysisDocs( List<CIApisQryRespEntity.ApisRespDocObj> saved_list, List<CIApisDocmuntTypeEntity> all_list) {
        if (saved_list != null) {
            if (saved_list.size() > 0) {
                GroupItem saveGroup = new GroupItem();
                saveGroup.apis_group_name = m_Context.getString(R.string.check_in_input_apis_select_saved);
                saveGroup.childItems = new ArrayList<>();

                for (CIApisQryRespEntity.ApisRespDocObj tmp1 : SavedDocs) {
                    saveGroup.childItems.add(tmp1);
                }
                m_Items.add(saveGroup);
            }
        }

        if (all_list != null) {
            if (all_list.size() > 0) {
                GroupItem allGroup = new GroupItem();
                allGroup.apis_group_name = m_Context.getString(R.string.check_in_input_apis_input_others);
                allGroup.childItems = new ArrayList<>();

                Locale locale = CIApplication.getLanguageInfo().getLanguage_Locale();
                for( CIApisDocmuntTypeEntity data : all_list ) {
                    m_arString.add(data.getName(locale));
                }
                m_Items.add(allGroup);
            }
        }
    }

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

    public void onBackPressed() {
        HidekeyBoard();

        this.finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        //m_etSearch.addTextChangedListener(this);
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
            return false;
        }
    };

    ExpandableListView.OnChildClickListener m_OnChildClickListener = new ExpandableListView.OnChildClickListener() {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

            Intent intent = new Intent();

//            String iata , localization_name ;
//
//            List<CIFlightStationEntity> datas;
//            ArrayList<GroupItem>        items;
//            int                         index;
//            //透過不同的mode抓不同的child data
//            if (lastMode == RECENT || lastMode == NORMAL) {
//                datas   = allDeparture;
//                items   = m_Items;
//            } else if (lastMode == SEARCH) {
//                datas   = searchDeparture;
//                items   = m_ItemSearch;
//            } else if (lastMode == NEAREST_OFFICE) {
//                datas   = nearDepartue;
//                items   = m_ItemsForNearestOffice;
//            }else {
//                datas   = allDeparture;
//                items   = m_Items;
//            }
//            index               = items.get(groupPosition).childItems.get(childPosition).index;
//            iata                = datas.get(index).IATA;
//            localization_name   = datas.get(index).localization_name;
//
//            addRecentAndCheck(iata);
//
//            //送出機場區名稱
//            intent.putExtra(LOCALIZATION_NAME, localization_name);
//
//            //送出機場代號
//            intent.putExtra(IAIT, iata);
//
//            //排序
//            recentSequence();

            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);

            return true;
        }
    };



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
