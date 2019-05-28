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

import ci.function.Main.BaseActivity;
import ci.ui.TextField.Adapter.CIMenusAdapter;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.CIProgressDialog;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CICodeNameEntity;
import ci.ws.Presenter.CIInquiryAddressPresenter;
import ci.ws.Presenter.Listener.CIInquiryAddressListner;

public class CIAddressSelectMenuActivity extends BaseActivity
        implements TextWatcher{

    public enum EMode{
        National, City, County, Street, ZipCode, CurrArea
    }

    private ListView                            m_listView          = null;
    private EditText                            m_etSearch          = null;
    private ArrayList<CICodeNameEntity>         m_arAddressList     = null;
    private ArrayList<CICodeNameEntity>         m_arRetainNational  = null;
    private ArrayList<String>                   m_arString          = null;
    public  NavigationBar                       m_Navigationbar     = null;
    private CIMenusAdapter                      m_adapter           = null,
                                                m_retainAdapter     = null;
    private String                              m_strHint           = "";
    private String                              m_strMode           = "";
    private EMode                               m_mode              = EMode.National;
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
            CIInquiryAddressPresenter.getInstance(m_Listener).interrupt();
            CIAddressSelectMenuActivity.this.onBackPressed();
        }
    };

    CIInquiryAddressListner m_Listener = new CIInquiryAddressListner(){

        @Override
        public void onInquirySuccess(String rt_code, String rt_msg) {
            m_arAddressList = getDataList();
            if(null == m_arAddressList){
                showDialog(getString(R.string.warning),
                        "ERROR",
                        getString(R.string.confirm),
                        null,
                        m_AlertListner);
                CIInquiryAddressPresenter.getInstance(m_Listener).interrupt();
            } else {
                initList();
            }
        }

        @Override
        public void onInquiryError(String rt_code, String rt_msg) {
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
        m_arAddressList = getDataList();

        initList();
    }

    private void initList(){
        if(null == m_arAddressList){
            return;
        }
        m_arRetainNational = (ArrayList<CICodeNameEntity>) m_arAddressList.clone();
        m_arString      = new ArrayList<>();

        for (CICodeNameEntity data : m_arAddressList) {
            m_arString.add(data.Name);
        }

        m_adapter = new CIMenusAdapter(this,
                m_arString,
                R.layout.list_item_textfeild_fullpage_menu);
        m_retainAdapter = (CIMenusAdapter) m_adapter.clone();
        m_listView.setAdapter(m_adapter);

        m_etSearch.setHint(m_strHint);
    }

    private ArrayList<CICodeNameEntity> getDataList(){

        if ( m_mode == EMode.National ){
            return CIInquiryAddressPresenter.getInstance(m_Listener).getNationalList();
        } else if ( m_mode == EMode.City ){
            return CIInquiryAddressPresenter.getInstance(m_Listener).getCityList();
        } else if ( m_mode == EMode.County ){
            return CIInquiryAddressPresenter.getInstance(m_Listener).getCountyList();
        } else if ( m_mode == EMode.Street ){
            return CIInquiryAddressPresenter.getInstance(m_Listener).getStreetList();
        } else if ( m_mode == EMode.ZipCode ){
            return CIInquiryAddressPresenter.getInstance(m_Listener).getZipCodeList();
        } else if ( m_mode == EMode.CurrArea ){
            return CIInquiryAddressPresenter.getInstance(m_Listener).getCurrAreaCityList();
        }

        return new ArrayList<>();
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

                intent.putExtra(VALUE, text);
                intent.putExtra(COUNTRY_CD, m_arAddressList.get(position).Code);

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

            m_arAddressList.clear();
            for (int loop = 0; loop < m_arString.size(); loop++) {
                if (true == m_arString.get(loop).toUpperCase().contains(s.toString().toUpperCase())) {
                    al.add(m_arString.get(loop));
                    m_arAddressList.add(m_arRetainNational.get(loop));
                }
            }

            m_adapter = new CIMenusAdapter(this,
                                          al,
                                          R.layout.list_item_textfeild_fullpage_menu);
            m_listView.setAdapter(m_adapter);
        } else {

            m_adapter = (CIMenusAdapter) m_retainAdapter.clone();
            m_arAddressList = (ArrayList<CICodeNameEntity>) m_arRetainNational.clone();
            m_listView.setAdapter(m_adapter);
        }
    }
}
