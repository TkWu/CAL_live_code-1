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
import java.util.Locale;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.ui.TextField.Adapter.CIMenusAdapter;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.CIAirportDataManager;
import ci.ui.view.NavigationBar;


public class CIAPISCheckInDocmuntTypeSelectMenuActivity extends BaseActivity implements TextWatcher {

    private NavigationBar           m_Navigationbar    = null;
    private ExpandableListView m_expandableListView    = null;
    private EditText                m_etSearch;

    public  boolean isSavedApis                = false;

    private ArrayList<GroupItem>    m_Items            = null,
            m_ItemSearch            = null;

    public class GroupItem {
        public String  apis_group;
        public ArrayList<ChildItem> childItems;
    }

    public class ChildItem {
        public int index;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_select_checkin_apis;
    }


    @Override
    protected void initialLayoutComponent() {


        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        m_etSearch = (EditText) findViewById(R.id.et_search);

        m_expandableListView.setDivider(null);

        m_Items = new ArrayList<>();

        m_Items.clear();

//        showProgressDialog(progressDlgListener);
//
//        m_manager = new CIAirportDataManager(m_managerCallBack);
//
//        m_manager.fetchAirportData(getIntent().getExtras().getBoolean(IS_TO_FRAGMENT, false),
//                getIntent().getExtras().getString(IAIT),
//                getIntent().getExtras().getInt(ESOURCE, 3));
//
//        ciInquiryFlightStationPresenter = m_manager.getFlightStationPresenter();

    }

    private void showSavedApis(final Boolean isSavedApis) {

        this.isSavedApis = isRecent;

        if(null == allDeparture){
            return;
        }

        runOnUiThread(new Runnable() {
            public void run() {

                //判斷是否有WS取得的APIS
                if (isSavedApis) {
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

}
