package ci.function.PersonalDetail.APIS;

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
import android.widget.ListView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.ui.TextField.Adapter.CIMenusAdapter;
import ci.ui.TextField.CIApisDocmuntTextFieldFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIApisDocmuntTypeEntity;
import ci.ws.Presenter.CIAPISPresenter;

/**
 * Created by joannyang on 16/5/26.
 */
public class CIAPISDocmuntTypeSelectMenuActivity extends BaseActivity implements TextWatcher{

    public static final String                  BUNDLE_ACTIVITY_DATA_FILTER_LIST = "ActivityFilterList";
    public static final String                  BUNDLE_ACTIVITY_DATA_SELECT_LIST = "ActivitySelectList";

    private ListView m_listView          = null;
    private EditText m_etSearch          = null;
    private ArrayList<CIApisDocmuntTypeEntity> m_arDocmuntType          = null;
    private ArrayList<CIApisDocmuntTypeEntity> m_arRetainDocmuntType    = null;
    private ArrayList<String>                   m_arString              = null;
    public NavigationBar m_Navigationbar     = null;
    private CIMenusAdapter m_adapter           = null,
            m_retainAdapter     = null;
    private String                              m_strHint           = "";
    private CIApisDocmuntTextFieldFragment.EType m_apisType         = null;
    public static final String                  VALUE               = "VALUE";
    public static final String                  DOCUMUNT_TYPE       = "DOCUMUNT_TYPE";

    private HashSet<String> m_filter = null;
    private HashSet<CIApisDocmuntTypeEntity> m_SelectList = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        if(null != bundle){
            m_strHint = bundle.getString(UiMessageDef.BUNDLE_ACTIVITY_DATA_HINT);
            if(!TextUtils.isEmpty(m_strHint)){
                m_strHint = m_strHint.replace("*", "");
            }

            m_apisType = (CIApisDocmuntTextFieldFragment.EType)bundle.getSerializable(CIApisDocmuntTextFieldFragment.APIS_TYPE);

            m_filter = (HashSet<String>)bundle.getSerializable(BUNDLE_ACTIVITY_DATA_FILTER_LIST);
            m_SelectList = (HashSet<CIApisDocmuntTypeEntity>)bundle.getSerializable(BUNDLE_ACTIVITY_DATA_SELECT_LIST);

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
        return R.layout.activity_sign_up_select_menu;
    }

    @Override
    protected void initialLayoutComponent() {

        m_listView      = (ListView) findViewById(R.id.activity_list_view_sign_up_select_menu);
        if(m_apisType == CIApisDocmuntTextFieldFragment.EType.Personal) {
            m_arDocmuntType = CIAPISPresenter.getInstance().fetchAllApisList();
        } else if( m_apisType == CIApisDocmuntTextFieldFragment.EType.CheckIn ){
            m_arDocmuntType = CIAPISPresenter.getInstance().fetchAllApisList();
            //m_arDocmuntType = CIAPISPresenter.getInstance().fetchApisList();
        } else {
            m_arDocmuntType = new ArrayList<>();
        }


        m_arRetainDocmuntType = (ArrayList<CIApisDocmuntTypeEntity>)m_arDocmuntType.clone();

        m_arString      = new ArrayList<>();

        if( null != m_filter ) {
            for( Iterator<CIApisDocmuntTypeEntity> iterator = m_arDocmuntType.iterator(); iterator.hasNext(); ) {

                CIApisDocmuntTypeEntity data = iterator.next();

                if( m_filter.contains(data.code_1A) ) {
                    iterator.remove();
                }
            }
        } else if ( null != m_SelectList ){
            for( Iterator<CIApisDocmuntTypeEntity> iterator = m_arDocmuntType.iterator(); iterator.hasNext(); ) {

                CIApisDocmuntTypeEntity data = iterator.next();
                boolean bContains = false;
                for ( CIApisDocmuntTypeEntity enity : m_SelectList ){
                    if( TextUtils.equals(enity.code_1A, data.code_1A) ) {
                        //沒帶國籍，則不過濾國籍
                        if ( TextUtils.isEmpty(enity.issued_country) ){
                            bContains = true;
                            break;
                        } else if ( TextUtils.equals(enity.issued_country, data.issued_country) ){
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
        for( CIApisDocmuntTypeEntity data : m_arDocmuntType ) {
                m_arString.add(data.getName(locale));
        }

        m_adapter = new CIMenusAdapter(this,
                m_arString,
                R.layout.list_item_textfeild_fullpage_menu);
        m_retainAdapter = (CIMenusAdapter) m_adapter.clone();
        m_listView.setAdapter(m_adapter);
        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_etSearch = (EditText) findViewById(R.id.et_search);
        m_etSearch.setHint(m_strHint);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        m_listView.setDividerHeight(vScaleDef.getLayoutWidth(0.7));
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        m_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                String text = (String) m_adapter.getItem(position);


                intent.putExtra(VALUE, text);

                intent.putExtra(DOCUMUNT_TYPE, m_arDocmuntType.get(position).code_1A);

                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
            }
        });
        m_etSearch.addTextChangedListener(this);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
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
    protected void onLanguageChangeUpdateUI() {}

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.length() > 0){
            ArrayList<String> al= new ArrayList<>();
            m_arDocmuntType.clear();
            for(int loop = 0; loop < m_arString.size(); loop++){
                if(true == m_arString.get(loop).contains(s.toString())){
                    al.add(m_arString.get(loop));
                    m_arDocmuntType.add(m_arRetainDocmuntType.get(loop));
                }
            }

            m_adapter = new CIMenusAdapter(this,
                    al,
                    R.layout.list_item_textfeild_fullpage_menu);
            m_listView.setAdapter(m_adapter);
        } else {
            m_adapter = (CIMenusAdapter)m_retainAdapter.clone();
            m_arDocmuntType = (ArrayList<CIApisDocmuntTypeEntity>) m_arRetainDocmuntType.clone();
            m_listView.setAdapter(m_adapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if( m_filter != null ) {
            m_filter.clear();
        }

        if( m_SelectList != null ) {
            m_SelectList.clear();
        }

        if( m_arDocmuntType != null ) {
            m_arDocmuntType.clear();
        }

        if( m_arRetainDocmuntType != null ) {
            m_arRetainDocmuntType.clear();
        }
    }
}