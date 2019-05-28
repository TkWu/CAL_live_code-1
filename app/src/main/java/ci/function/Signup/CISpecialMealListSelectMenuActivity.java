package ci.function.Signup;

import android.content.Intent;
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
import java.util.Locale;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.ui.TextField.Adapter.CIMenusAdapter;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIMealEntity;
import ci.ws.Models.entities.CIMealList;
import ci.ws.Presenter.CIInquiryMealListPresenter;

/**
 * Created by kevincheng on 2016/2/19.
 */
public class CISpecialMealListSelectMenuActivity extends BaseActivity
        implements TextWatcher{

    private ListView                            m_listView          = null;
    private EditText                            m_etSearch          = null;
    private CIMealList m_arMeal            = null;
    private CIMealList m_arRetainMeal = null;
    private ArrayList<String>                   m_arString          = null;
    public  NavigationBar                       m_Navigationbar     = null;
    private CIMenusAdapter                      m_adapter           = null,
                                                m_retainAdapter     = null;
    public static final String                  VALUE               = "VALUE";
    public static final String                  CODE                = "CODE";

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return getString(R.string.special_meal_preference);
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
        m_arMeal        = CIInquiryMealListPresenter.getInstance(null).fetchMealList();
        m_arRetainMeal  = (CIMealList) m_arMeal.clone();
        m_arString      = new ArrayList<>();
        Locale locale = CIApplication.getLanguageInfo().getLanguage_Locale();

        for(CIMealEntity data: m_arMeal){
            switch (locale.toString()){
                case "zh_TW":
                    m_arString.add(data.meal_name);
                    break;
                case "zh_CN":
                    m_arString.add(data.meal_name_cn);
                    break;
                case "en":
                    m_arString.add(data.meal_name_e);
                    break;
                case "ja_JP":
                    m_arString.add(data.meal_name_jp);
                    break;
            }

        }
        m_adapter = new CIMenusAdapter(this,
                m_arString,
                R.layout.list_item_textfeild_fullpage_menu);
        m_retainAdapter = (CIMenusAdapter) m_adapter.clone();
        m_listView.setAdapter(m_adapter);
        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_etSearch = (EditText) findViewById(R.id.et_search);
        m_etSearch.setHint(getString(R.string.special_meal_preference));
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
                intent.putExtra(CODE, m_arMeal.get(position).meal_code);
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
            m_arMeal.clear();
            for(int loop = 0; loop < m_arString.size(); loop++){
                if(true == m_arString.get(loop).toUpperCase().contains(s.toString().toUpperCase())){
                    al.add(m_arString.get(loop));
                    m_arMeal.add(m_arRetainMeal.get(loop));
                }
            }

            m_adapter = new CIMenusAdapter(this,
                                          al,
                                          R.layout.list_item_textfeild_fullpage_menu);
            m_listView.setAdapter(m_adapter);
        } else {
            m_adapter = (CIMenusAdapter)m_retainAdapter.clone();
            m_arMeal  = (CIMealList) m_arRetainMeal.clone();
            m_listView.setAdapter(m_adapter);
        }
    }
}
