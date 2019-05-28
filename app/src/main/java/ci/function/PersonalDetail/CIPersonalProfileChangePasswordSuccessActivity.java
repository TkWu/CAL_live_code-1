package ci.function.PersonalDetail;

import android.content.Intent;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.function.Main.BaseActivity;
import ci.function.Main.CIMainActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;

/**哩程補登成功頁面
 * Created by kevin on 2016/3/23.
 */
public class CIPersonalProfileChangePasswordSuccessActivity extends BaseActivity
        implements View.OnClickListener {


    private NavigationBar  m_Navigationbar = null;
    private ImageView      m_ivSuccess     = null;

    private LinearLayout m_llTextMsg = null;
    private TextView     m_tvMsg     = null;

    private RelativeLayout m_rlOk  = null;
    private Button         m_btnOk = null;


    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return getString(R.string.apply_password);
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
        Intent intent = new Intent(this, CIMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(UiMessageDef.BUNDLE_LOGOUT_REQUEST_TAG,false);
        startActivity(intent);

        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
        finish();
    }

    /**
     * 元件初始化，靜態取得元件實體、動態建製元件實體…等
     */
    @Override
    protected void initialLayoutComponent() {
        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_ivSuccess = (ImageView) findViewById(R.id.iv_success);
        m_llTextMsg = (LinearLayout) findViewById(R.id.ll_textview);
        m_tvMsg = (TextView) findViewById(R.id.tv_msg);

        m_btnOk = (Button) findViewById(R.id.btn_ok);
        m_btnOk.setOnClickListener(this);


        m_Navigationbar.post(new Runnable() {
            @Override
            public void run() {
                m_Navigationbar.findViewById(R.id.img_back).setVisibility(View.GONE);
            }
        });
    }

    /**
     * 設定字型大小及版面所需參數
     *
     * @param vScaleDef 請參閱{@link ViewScaleDef}
     */
    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        //Navigationbar
        m_Navigationbar.getLayoutParams().height = vScaleDef.getLayoutHeight(56);

        //圖片 上間隔51.3, 寬高82.7
        RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) m_ivSuccess.getLayoutParams();
        rParams.topMargin   = vScaleDef.getLayoutHeight(51.3);
        rParams.width       = vScaleDef.getLayoutMinUnit(82.7);
        rParams.height      = vScaleDef.getLayoutMinUnit(82.7);

        //說明文字欄 上間隔30, 左右間隔30
        rParams = (RelativeLayout.LayoutParams) m_llTextMsg.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(30);
        rParams.leftMargin = vScaleDef.getLayoutWidth(30);
        rParams.rightMargin = vScaleDef.getLayoutWidth(30);

        //說明文字 字體大小16, 行高 22.7(扣掉文字大小為6.7)
        vScaleDef.setTextSize(vScaleDef.DEF_TEXT_SIZE_16, m_tvMsg);
        vScaleDef.setViewSize(m_tvMsg, 300, ViewGroup.LayoutParams.WRAP_CONTENT);
        m_tvMsg.setLineSpacing(vScaleDef.getLayoutHeight(6.7), 1.0f);


        //按鈕大小 寬320, 高40
        //按鈕文字 大小16
        rParams = (RelativeLayout.LayoutParams) m_btnOk.getLayoutParams();
        rParams.width = vScaleDef.getLayoutWidth(320);
        rParams.height = vScaleDef.getLayoutHeight(40);
        vScaleDef.setTextSize(vScaleDef.DEF_TEXT_SIZE_16, m_btnOk);
    }

    /**
     * 通知父BaseActivity對子{@link BaseFragment}
     * 設定客製的「OnParameter(參數讀取)」跟「OnListener(動線通知)」介面
     */
    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_change_password_success;
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {}

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                onBackPressed();
                break;
        }
    }

}
