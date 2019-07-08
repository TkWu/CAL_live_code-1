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
import android.widget.Switch;

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

    public enum CICheckInAPISGroupType {
        ALL, SAVED
    }

    private HashSet<CIApisDocmuntTypeEntity> m_SelectList = null;

    public class GroupItem {
        public String  apis_group_name;
        public String  apis_group_type;
        public ArrayList<CIApisQryRespEntity.ApisRespDocObj> docsObject;
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

            SavedDocs = (ArrayList<CIApisQryRespEntity.ApisRespDocObj>)bundle.getSerializable(BUNDLE_ACTIVITY_DATA_SAVED_LIST);

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
        if ( null != SavedDocs ){
            for( Iterator<CIApisQryRespEntity.ApisRespDocObj> iterator = SavedDocs.iterator(); iterator.hasNext(); ) {

                CIApisQryRespEntity.ApisRespDocObj data = iterator.next();
                boolean bContains = false;
                for ( CIApisDocmuntTypeEntity enity : m_SelectList ){
                    if( TextUtils.equals(enity.code_1A, data.documentType) ) {
                        bContains = true;
                        break;
                        //沒帶國籍，則不過濾國籍
//                        if ( TextUtils.isEmpty(enity.issued_country) ){
//                            bContains = true;
//                            break;
//                        } else if ( TextUtils.equals(enity.issued_country, data.documentType) ){
//                            //有帶國籍參數，才過濾國籍
//                            bContains = true;
//                            break;
//                        }
                    }
                }
                if ( !bContains ){
                    iterator.remove();
                }
            }
        }
        m_Items = new ArrayList<>();

        m_Items.clear();

        DataAnalysisDocs(SavedDocs,AllDocs);

        m_Adpater = new CIAPISCheckInDocmuntTypeListAdapter(this,
                m_Items,
                R.layout.activity_select_checkin_apis);
        m_expandableListView.setAdapter(m_Adpater);
        int iSize = m_Adpater.getGroupCount();
        for ( int iIdx = 0; iIdx < iSize; iIdx++ ){
            m_expandableListView.expandGroup(iIdx);
        }
    }


    private void DataAnalysisDocs( List<CIApisQryRespEntity.ApisRespDocObj> saved_list, List<CIApisDocmuntTypeEntity> all_list) {
        if (saved_list != null) {
            if (saved_list.size() > 0) {
                GroupItem m_groupItem = new GroupItem();
                m_groupItem.apis_group_name = m_Context.getString(R.string.check_in_input_apis_select_saved);
                m_groupItem.apis_group_type = CICheckInAPISGroupType.SAVED.name();
                m_groupItem.docsObject = new ArrayList<>();

                for (CIApisQryRespEntity.ApisRespDocObj tmp1 : SavedDocs) {
                    m_groupItem.docsObject.add(tmp1);
                }
                m_Items.add(m_groupItem);
            }
        }

        if (all_list != null) {
            if (all_list.size() > 0) {
                GroupItem m_groupItem = new GroupItem();
                m_groupItem.apis_group_name = m_Context.getString(R.string.check_in_input_apis_input_others);
                m_groupItem.apis_group_type = CICheckInAPISGroupType.ALL.name();
                m_groupItem.docsObject = new ArrayList<>();

                Locale locale = CIApplication.getLanguageInfo().getLanguage_Locale();
                CIApisQryRespEntity QRTmp = new CIApisQryRespEntity();
                CIApisQryRespEntity.CIApispaxInfo QRTmpPaxInfo = QRTmp.new CIApispaxInfo();

                for (CIApisDocmuntTypeEntity tmp1 : AllDocs) {
                    CIApisQryRespEntity.ApisRespDocObj objectTmp = QRTmp.new ApisRespDocObj();
                    objectTmp.documentType = tmp1.code_1A;
                    objectTmp.documentName = tmp1.getName(locale);
                    objectTmp.deviceId = CIApplication.getDeviceInfo().getAndroidId();
                    switch (tmp1.code_1A) {
                        case "A":
                            objectTmp.docas = QRTmpPaxInfo.new Docas();
                            break;
                        case "N":
                            objectTmp.basicDocuments = QRTmpPaxInfo.new BasicDocuments();
                            break;
                        default:
                            objectTmp.otherDocuments = QRTmpPaxInfo.new OtherDocuments();
                            break;
                    }
                    m_groupItem.docsObject.add(objectTmp);
                }
                m_Items.add(m_groupItem);
            }
        }
    }

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
        m_expandableListView.setOnGroupClickListener(m_OnGroupClickListener);
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

    ExpandableListView.OnGroupClickListener m_OnGroupClickListener = new ExpandableListView.OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            return true;
        }
    };

    ExpandableListView.OnChildClickListener m_OnChildClickListener = new ExpandableListView.OnChildClickListener() {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

            Intent intent = new Intent();

//            CIApisQryRespEntity.ApisRespDocObj

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
}
