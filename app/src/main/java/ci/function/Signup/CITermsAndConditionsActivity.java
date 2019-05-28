package ci.function.Signup;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;
import ci.ws.cores.object.GsonTool;

/**
 * Created by jlchen on 2016/2/19.
 * Last Edited By Kevin Cheng
 * 使用端範例代碼
 *
 ---創建 CITermsAndConditionsActivity.ContentList()---
 CITermsAndConditionsActivity.ContentList() m_TermsAndConditionsContentList = new CITermsAndConditionsActivity.ContentList();
 ---創建 CITermsAndConditionsActivity.Content()---
 CITermsAndConditionsActivity.Content data1 = new CITermsAndConditionsActivity.Content();
 --- 設定標題 ---
 data1.itemTitle = getString(R.string.Qualifications_title);
 --- 設定內容 ---
 data1.itemContent = getString(R.string.Qualifications_content);
 --- 第二個或更多的項目 ---
 CITermsAndConditionsActivity.Content data2 = new CITermsAndConditionsActivity.Content();
 data2.itemTitle = getString(R.string.Conditions_Membership_Privileges_and_Obligations_title);
 data2.itemContent = getString(R.string.Conditions_Membership_Privileges_and_Obligations_content);
 --- 加入List ---
 m_TermsAndConditionsContentList.add(data1);
 m_TermsAndConditionsContentList.add(data2);


 Intent data = new Intent();
 --- 轉成Json格式 ---
 String jsData = GsonTool.toJson(m_TermsAndConditionsContentList);
 --- 放置主標題 ---
 data.putExtra(UiMessageDef.BUNDLE_ACTIVITY_DATA_TITLE,getString(R.string.sign_up_terms_and_conditions_title));
 --- 放置欲顯示的事項json ---
 data.putExtra(UiMessageDef.BUNDLE_ACTIVITY_DATA,jsData);
 --- 跳轉Activity ---
 changeActivity(CITermsAndConditionsActivity.class, data);
 *
 *
 *
 */
public class CITermsAndConditionsActivity extends BaseActivity implements View.OnClickListener{

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_title;
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {

        }

        @Override
        public void onLeftMenuClick() {

        }

        @Override
        public void onBackClick() {
            onBackPressed();
        }

        @Override
        public void onDeleteClick() {

        }
        @Override
        public void onDemoModeClick() {}
    };

    @Override
    public void onBackPressed() {

        CITermsAndConditionsActivity.this.finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        String jsData = null;
        String title  = null;
        if(null != bundle){
            jsData = bundle.getString(UiMessageDef.BUNDLE_ACTIVITY_DATA);
            title = bundle.getString(UiMessageDef.BUNDLE_ACTIVITY_DATA_TITLE);
        }
        if(!TextUtils.isEmpty(jsData)){
            m_datas = GsonTool.toObject(jsData, ContentList.class);
        }
        if(!TextUtils.isEmpty(title)){
            m_title = title;
        }
        super.onCreate(savedInstanceState);
    }

    public NavigationBar 	m_Navigationbar		= null;
    private ContentList     m_datas             = null;
    private String          m_title             = "TITLE";
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_terms_and_conditions;
    }

    @Override
    protected void initialLayoutComponent() {
        ViewScaleDef viewScaleDef = ViewScaleDef.getInstance(this);
        m_Navigationbar		= (NavigationBar) findViewById(R.id.toolbar);
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.ll_content);
        viewScaleDef.selfAdjustAllView(findViewById(R.id.root));
        LayoutInflater inflater =  getLayoutInflater();
        if(null == m_datas){
            return;
        }
        for(Content data:m_datas){
            View view  = inflater.inflate(R.layout.layout_terms_and_conditions_content,null);
            viewScaleDef.selfAdjustAllView(view.findViewById(R.id.ll));
            ((TextView)view.findViewById(R.id.tv_item_title)).setText(data.itemTitle);
            ((TextView)view.findViewById(R.id.tv_item_content)).setText(data.itemContent);
            int iLineSpacing = viewScaleDef.getLayoutHeight(3);
            ((TextView)view.findViewById(R.id.tv_item_content)).setLineSpacing(iLineSpacing,1);
            linearLayout.addView(view);
        }

    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
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
    public void onClick(View v) {


    }
    public static class ContentList extends ArrayList<Content>{}

    public static class Content{
        public String itemTitle;
        public String itemContent;
    }

}
