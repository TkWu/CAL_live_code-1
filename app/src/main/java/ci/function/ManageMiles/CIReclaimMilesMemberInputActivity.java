package ci.function.ManageMiles;

import android.content.Intent;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.ui.TextField.CIEmailTextFieldFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIReclaimMileageReq;
import ci.ws.cores.object.GsonTool;

/**
 * Created by kevincheng on 2016/3/11.
 * 主要是作為里程補登的進入頁，可用來確認身份以及更改mail
 */
public class CIReclaimMilesMemberInputActivity extends BaseActivity
    implements View.OnClickListener,
        View.OnTouchListener{
    private             NavigationBar            m_Navigationbar          = null;
    private CIEmailTextFieldFragment m_emailTextFeildFragment = null;
    private             ImageView                m_ivCheckBox             = null;
    private             TextView                 m_tvMemberName           = null;
    private             TextView                 m_tvMemberNo             = null;
    private             boolean                  m_bIvCheckBox            = false;
    public              String                   m_strEmail               = null;
    public              String                   m_strMemberName          = null;
    public              String                   m_strMemberNo            = null;

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.reclaim_miles);
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
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_recliam_miles_member_input;
    }

    @Override
    protected void initialLayoutComponent() {

        m_strEmail      = CIApplication.getLoginInfo().GetUserEmail();
        m_strMemberName = CIApplication.getLoginInfo().GetUserName();
        m_strMemberNo   = CIApplication.getLoginInfo().GetUserMemberCardNo();

        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_ivCheckBox    = (ImageView) findViewById(R.id.iv_checkbox);
        m_tvMemberName  = (TextView) findViewById(R.id.tv_member_name);
        m_tvMemberNo    = (TextView) findViewById(R.id.tv_member_no);
        m_tvMemberName.setText(m_strMemberName);
        m_tvMemberNo.setText(m_strMemberNo);
        //初始化，勾選更新Email
        m_bIvCheckBox   = setViewIconSwitch(m_ivCheckBox, m_bIvCheckBox);


    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        //小數點部分特別設定
        vScaleDef.setMargins(findViewById(R.id.ll_member_no), 30, 19.7, 30, 0);
        vScaleDef.setMargins(findViewById(R.id.ll_member_name),30, 26.3, 30, 0);
        vScaleDef.setMargins(findViewById(R.id.text_feild_email),20, 49.7, 20, 0);
        //圖示部分特別設定
        vScaleDef.selfAdjustSameScaleView(m_ivCheckBox,24,24);
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        findViewById(R.id.btn_next).setOnClickListener(this);
        findViewById(R.id.ll_checkbox).setOnClickListener(this);
        findViewById(R.id.root).setOnTouchListener(this);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //2016-11-15 Modify by Ryan , 移除星號與iOS統一
        m_emailTextFeildFragment = new CIEmailTextFieldFragment().newInstance(getString(R.string.email));
        transaction.replace(R.id.text_feild_email, m_emailTextFeildFragment).commit();
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                m_emailTextFeildFragment.setText(m_strEmail);
                m_emailTextFeildFragment.setImeOptions(EditorInfo.IME_ACTION_DONE);
            }
        });
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_checkbox:
                m_bIvCheckBox = setViewIconSwitch(m_ivCheckBox, m_bIvCheckBox);
                break;
            case R.id.btn_next:
                Intent data = new Intent();
                CIReclaimMileageReq reqData = new CIReclaimMileageReq();
                if(true == m_bIvCheckBox){
                    reqData.update_email_chk = "True";
                } else {
                    reqData.update_email_chk = "False";
                }
                reqData.email = getEmailData();
                if(TextUtils.isEmpty(reqData.email)){
                    return;
                }
                String jsonData = GsonTool.toJson(reqData);
                data.putExtra(UiMessageDef.BUNDLE_ACTIVITY_DATA,jsonData);
                changeActivity(CIReclaimMilesFlightDetialInputActivity.class,data);
                break;

        }
    }

    private boolean setViewIconSwitch(View v, boolean isOn) {
        if (true == isOn) {
            ((ImageView) v).setImageResource(R.drawable.btn_checkbox_off);
            return false;
        } else {
            ((ImageView) v).setImageResource(R.drawable.btn_checkbox_on);
            return true;
        }
    }

    /**
     * 轉換Activity
     * @param clazz     目標activity名稱
     */
    private void changeActivity(Class clazz ,Intent data){
        data.setClass(this, clazz);
        startActivity(data);
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    private String getEmailData(){
        String email = m_emailTextFeildFragment.getText().trim();
        if(true == m_emailTextFeildFragment.getIsFormatCorrect()){
            if(!TextUtils.isEmpty(email)){
                return email;
            } else {
                showDialog(getString(R.string.warning),
                        getString(R.string.please_fill_all_text_field_that_must_to_fill));
            }
        } else {
            showDialog(getString(R.string.warning),
                    getString(R.string.member_login_input_correvt_format_msg));
        }
        return null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        HidekeyBoard();
        return false;
    }
}
