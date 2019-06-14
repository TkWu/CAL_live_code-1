package ci.function.PersonalDetail.APIS;


import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
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
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIApisDocmuntTypeEntity;
import ci.ws.Presenter.CIAPISPresenter;

public class CIAddSaveAPISDocTypeActivity extends BaseActivity {

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            switch (m_type){
                case ADD_MY_APIS:
                case ADD_COMPANAIONS_APIS:
                    return m_Context.getString(R.string.add_apis);
                case EDIT_MY_APIS:
                case EDIT_COMPANAIONS_APIS:
                    //return m_Context.getString(R.string.edit) +" "+ m_strAPISName;
                    return m_Context.getString(R.string.edit) +" ";
                default:
                    return m_Context.getString(R.string.add_apis);
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

    public static final String                  BUNDLE_ACTIVITY_DATA_SELECT_LIST = "ActivitySelectList";

    private ListView m_listView          = null;

    private ArrayList<CIApisDocmuntTypeEntity> m_arDocmuntType          = null;
    private ArrayList<CIApisDocmuntTypeEntity> m_arRetainDocmuntType    = null;

    private ArrayList<String>                   m_arString              = null;
    public  NavigationBar m_Navigationbar     = null;
    private CIMenusAdapter m_adapter           = null;

    private String                              m_strHint           = "";
    public static final String                  AOIS_OBJ_VALUE      = "AOIS_OBJ_VALUE";
    public static final String                  APIS_TYPE           = "APIS_TYPE";
    public static final String                  APIS_FUN_ENTRANCE   = "APIS_FUN_ENTRANCE";

    public enum EType{
        Personal, CheckIn
    }

    private HashSet<String> m_filter = null;
    private HashSet<CIApisDocmuntTypeEntity> m_SelectList = null;

    private CIPersonalAddAPISActivity.CIPersonalAddAPISType m_type = CIPersonalAddAPISActivity.CIPersonalAddAPISType.ADD_MY_APIS;
    private CIAddSaveAPISDocTypeActivity.EType m_apisType = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String mode = getIntent().getStringExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE);
        m_apisType = (CIAddSaveAPISDocTypeActivity.EType)getIntent()
                .getSerializableExtra(CIAddSaveAPISDocTypeActivity.APIS_TYPE);
        if (null != mode) {
            m_type = CIPersonalAddAPISActivity.CIPersonalAddAPISType.valueOf(mode);
        }

        Bundle bundle = getIntent().getExtras();
        if(null != bundle){
            m_strHint = bundle.getString(UiMessageDef.BUNDLE_ACTIVITY_DATA_HINT);
            if(!TextUtils.isEmpty(m_strHint)){
                m_strHint = m_strHint.replace("*", "");
            }

            m_SelectList = (HashSet<CIApisDocmuntTypeEntity>)bundle.getSerializable(BUNDLE_ACTIVITY_DATA_SELECT_LIST);

        }
        super.onCreate(savedInstanceState);
    }





    @Override
    protected int getLayoutResourceId() { return R.layout.fragment_add_save_apis; }

    @Override
    protected void initialLayoutComponent() {

        m_listView      = (ListView) findViewById(R.id.activity_list_view_sign_up_select_menu);
//        if(m_apisType == CIApisDocmuntTextFieldFragment.EType.Personal) {
//            m_arDocmuntType = CIAPISPresenter.getInstance().fetchAllApisList();
//        } else if( m_apisType == CIApisDocmuntTextFieldFragment.EType.CheckIn ){
//            m_arDocmuntType = CIAPISPresenter.getInstance().fetchAllApisList();
//            //m_arDocmuntType = CIAPISPresenter.getInstance().fetchApisList();
//        } else {
//            m_arDocmuntType = new ArrayList<>();
//        }
        m_arDocmuntType = CIAPISPresenter.getInstance().fetchAllApisList();
        m_arRetainDocmuntType = (ArrayList<CIApisDocmuntTypeEntity>)m_arDocmuntType.clone();

        m_arString      = new ArrayList<>();

        if( null != m_filter ) {
            for(Iterator<CIApisDocmuntTypeEntity> iterator = m_arDocmuntType.iterator(); iterator.hasNext(); ) {

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
        m_listView.setAdapter(m_adapter);
        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
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
                String text = (String) m_adapter.getItem(position);
                Intent intent = new Intent();

                intent.putExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE,
                        CIPersonalAddSaveAPISActivity.CIPersonalAddAPISType.ADD_MY_APIS.name()); //功能
                Bundle bundle = new Bundle();
                //要改
                bundle.putSerializable(APIS_FUN_ENTRANCE, CIAddSaveAPISDocTypeActivity.EType.Personal);//個人／checkin入口分類
                bundle.putSerializable(AOIS_OBJ_VALUE, text);                                          //文件語系名稱
                bundle.putSerializable(APIS_TYPE, m_arDocmuntType.get(position).code_1A);              //文件類型(代碼)

                intent.putExtras(bundle);
                intent.setClass(CIAddSaveAPISDocTypeActivity.this, CIPersonalAddSaveAPISActivity.class);
                //要改
                startActivityForResult(intent, UiMessageDef.REQUEST_CODE_PERSONAL_ADD_APIS_TAG);
                finish();
                overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
            }
        });
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
