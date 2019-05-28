package ci.function.Signup;

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

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.ui.TextField.Adapter.CIMenusAdapter;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.CIProgressDialog;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIApisNationalEntity;
import ci.ws.Models.entities.CINationalEntity;
import ci.ws.Presenter.CIAPISPresenter;
import ci.ws.Presenter.CIInquiryNationalPresenter;
import ci.ws.Presenter.Listener.CIInquiryNationalListner;

/**
 * Created by kevincheng on 2016/2/19.
 */
public class CINationalitySelectMenuActivity extends BaseActivity
        implements TextWatcher{

    public enum EMode{
        Base, PhoneCode, ResidentNational,IssueNational
    }

    private ListView                            m_listView          = null;
    private EditText                            m_etSearch          = null;
    private ArrayList<CINationalEntity>         m_arNational        = null;
    private ArrayList<CINationalEntity>         m_arRetainNational  = null;
    private ArrayList<CIApisNationalEntity>     m_arRetainApisNational    = null;
    private ArrayList<CIApisNationalEntity>     m_arApisNational    = null;
    private ArrayList<String>                   m_arString          = null;
    public  NavigationBar                       m_Navigationbar     = null;
    private CIMenusAdapter                      m_adapter           = null,
                                                m_retainAdapter     = null;
    private String                              m_strHint           = "";
    private String                              m_strMode           = "";
    private EMode                               m_mode              = EMode.Base;
    public static final String                  VALUE               = "VALUE";
    public static final String                  COUNTRY_CD          = "COUNTRY_CD";
    public static final String                  PHONE_CD            = "PHONE_CD";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        if(null != bundle){
            m_strHint = bundle.getString(UiMessageDef.BUNDLE_ACTIVITY_DATA_HINT);
            m_strMode = bundle.getString(UiMessageDef.BUNDLE_ACTIVITY_MODE);
            if(!TextUtils.isEmpty(m_strHint)){
                m_strHint = m_strHint.replace("*","");
            }

            if(!TextUtils.isEmpty(m_strMode)){
                m_mode = EMode.valueOf(m_strMode);
            }


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

    CIProgressDialog.CIProgressDlgListener m_dlgListener = new CIProgressDialog.CIProgressDlgListener() {
        @Override
        public void onBackPressed() {
            CIInquiryNationalPresenter.getInstance(m_listen).interrupt();
            CINationalitySelectMenuActivity.this.onBackPressed();
        }
    };

    CIInquiryNationalListner m_listen = new CIInquiryNationalListner() {
        @Override
        public void onInquiryNationalSuccess(String rt_code, String rt_msg) {
            m_arNational    = CIInquiryNationalPresenter.getInstance(m_listen).getNationalList();
            if(null == m_arNational){
                showDialog(getString(R.string.warning),
                        "ERROR",
                        getString(R.string.confirm),
                        null,
                        m_AlertListner);
                CIInquiryNationalPresenter.getInstance(m_listen).interrupt();
            } else {
                initList();
            }
        }

        @Override
        public void onInquiryNationalError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm),
                    null,
                    m_AlertListner);
        }

        @Override
        public void showProgress() {
            showProgressDialog(m_dlgListener);
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
        }
    };

    CIAlertDialog.OnAlertMsgDialogListener m_AlertListner = new CIAlertDialog.OnAlertMsgDialogListener() {
        @Override
        public void onAlertMsgDialog_Confirm() {
            onBackPressed();
        }

        @Override
        public void onAlertMsgDialogg_Cancel() {}
    };

    @Override
    protected void initialLayoutComponent() {
        m_listView      = (ListView) findViewById(R.id.activity_list_view_sign_up_select_menu);
        m_etSearch      = (EditText) findViewById(R.id.et_search);
        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_arNational    = CIInquiryNationalPresenter.getInstance(m_listen).getNationalList();

        initList();
    }

    private void initList(){
        if(null == m_arNational){
            return;
        }
        m_arRetainNational = (ArrayList<CINationalEntity>) m_arNational.clone();
        m_arString      = new ArrayList<>();

        if( EMode.ResidentNational == m_mode || EMode.IssueNational == m_mode ) {
            m_arApisNational = CIAPISPresenter.getInstance().fetchApisNationalList();
            m_arRetainApisNational = (ArrayList<CIApisNationalEntity>) m_arApisNational.clone();

            for( CIApisNationalEntity data: m_arApisNational ) {
                m_arString.add( data.getCountryName(CIApplication.getLanguageInfo().getLanguage_Locale()) );
            }
        } else {

            for (CINationalEntity data : m_arNational) {
                m_arString.add(data.country_name);
            }
        }

        m_adapter = new CIMenusAdapter(this,
                m_arString,
                R.layout.list_item_textfeild_fullpage_menu);
        m_retainAdapter = (CIMenusAdapter) m_adapter.clone();
        m_listView.setAdapter(m_adapter);

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
                String text = (String)m_adapter.getItem(position);

                if(EMode.PhoneCode == m_mode){
                    intent.putExtra(VALUE,  m_arNational.get(position).country_phone_cd);

                } else if( EMode.ResidentNational == m_mode ||  EMode.IssueNational == m_mode ) {
                    intent.putExtra(VALUE,  m_arApisNational.get(position).getCountryName(CIApplication.getLanguageInfo().getLanguage_Locale()));

                } else {
                    intent.putExtra(VALUE, text);

                }

                if( EMode.ResidentNational == m_mode ) {
                    intent.putExtra(COUNTRY_CD,  m_arApisNational.get(position).resident_cd);

                } else if( EMode.IssueNational == m_mode ) {
                    intent.putExtra(COUNTRY_CD,  m_arApisNational.get(position).issue_cd);

                } else {
                    intent.putExtra(PHONE_CD, m_arNational.get(position).country_phone_cd);
                    intent.putExtra(COUNTRY_CD, m_arNational.get(position).country_cd);
                }
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

            if( EMode.ResidentNational == m_mode || EMode.IssueNational == m_mode ) {
                m_arApisNational.clear();
                for (int loop = 0; loop < m_arString.size(); loop++) {
                    if (true == m_arString.get(loop).toUpperCase().contains(s.toString().toUpperCase())) {
                        al.add(m_arString.get(loop));
                        m_arApisNational.add(m_arRetainApisNational.get(loop));
                    }
                }
            } else {
                m_arNational.clear();
                for (int loop = 0; loop < m_arString.size(); loop++) {
                    if (true == m_arString.get(loop).toUpperCase().contains(s.toString().toUpperCase())) {
                        al.add(m_arString.get(loop));
                        m_arNational.add(m_arRetainNational.get(loop));
                    }
                }
            }

            m_adapter = new CIMenusAdapter(this,
                                          al,
                                          R.layout.list_item_textfeild_fullpage_menu);
            m_listView.setAdapter(m_adapter);
        } else {

            if( EMode.ResidentNational == m_mode || EMode.IssueNational == m_mode ) {
                m_adapter = (CIMenusAdapter) m_retainAdapter.clone();
                m_arApisNational = (ArrayList<CIApisNationalEntity>) m_arRetainApisNational.clone();
                m_listView.setAdapter(m_adapter);
            } else {
                m_adapter = (CIMenusAdapter) m_retainAdapter.clone();
                m_arNational = (ArrayList<CINationalEntity>) m_arRetainNational.clone();
                m_listView.setAdapter(m_adapter);
            }
        }
    }
}
