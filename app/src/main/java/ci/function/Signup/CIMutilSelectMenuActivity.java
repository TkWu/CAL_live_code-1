package ci.function.Signup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.Collections;

import ci.function.Main.BaseActivity;
import ci.ui.TextField.Adapter.CIMenusAdapter;
import ci.ui.TextField.CINationalityTextFieldFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;

/**
 * Created by kevincheng on 2016/2/19.
 */
public class CIMutilSelectMenuActivity extends BaseActivity
        implements TextWatcher{

    private ListView          m_listView = null;
    private EditText          m_etSearch = null;
    private ArrayList<String> m_arString = null;
    public  NavigationBar     m_Navigationbar = null;
    private CIMenusAdapter    m_adapter       = null,
                              m_retainAdapter = null;
    private String            m_strHint       = "";
    private int               m_iFragmentNum  = 0;
    public static final String VALUE = "VALUE";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        if(null != bundle){
            m_strHint = bundle.getString(UiMessageDef.BUNDLE_ACTIVITY_DATA_HINT);
            m_iFragmentNum = bundle.getInt(UiMessageDef.BUNDLE_TEXT_FEILD_FRAGMENT_ID);
            if(null != m_strHint){
                m_strHint = m_strHint.replace("*","");
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

    @Override
    protected void initialLayoutComponent() {
        m_listView = (ListView) findViewById(R.id.activity_list_view_sign_up_select_menu);
        int      resArray = getIntent().getExtras().getInt(CINationalityTextFieldFragment.ITEM_ARRAY);
        String[] strArray = getResources().getStringArray(resArray);
        m_arString = new ArrayList<>();
        Collections.addAll(m_arString, strArray);
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
                intent.putExtra(VALUE, (String)m_adapter.getItem(position));
                intent.putExtra(UiMessageDef.BUNDLE_TEXT_FEILD_FRAGMENT_ID, m_iFragmentNum);
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
            for(int loop = 0;loop < m_arString.size();loop++){
                if(true == m_arString.get(loop).contains(s.toString())){
                    al.add(m_arString.get(loop));
                }
            }

            m_adapter = new CIMenusAdapter(this,
                                          al,
                                          R.layout.list_item_textfeild_fullpage_menu);
            m_listView.setAdapter(m_adapter);
        } else {
            m_adapter = (CIMenusAdapter)m_retainAdapter.clone();
            m_listView.setAdapter(m_adapter);
        }
    }
}
