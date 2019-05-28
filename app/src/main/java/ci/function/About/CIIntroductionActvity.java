package ci.function.About;

import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Main.BaseActivity;
import ci.ui.WebView.CIWebViewActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;

/**
 * Created by kevincheng on 2016/4/8.
 * 有關華航：YouTube 播放清單及連結到華航 YouTube 首頁
 */
public class CIIntroductionActvity extends BaseActivity
    implements View.OnClickListener{
    private NavigationBar       m_Navigationbar     = null;
    private TextView            m_tvChinaAirlines   = null;
    private final String        DEF_YOUTUBE_URL     = "https://www.youtube.com/user/TheCHINAAIRLINES";
    private CIYouTubeListFragment m_fragment        = null;
    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return getString(R.string.china_airlines_instroduction);
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
        public void onDemoModeClick() {

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_introduction;
    }

    @Override
    protected void initialLayoutComponent() {
        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_tvChinaAirlines = (TextView) findViewById(R.id.tv_china_airlines);

        SpannableString spannableString = new SpannableString(getString(R.string.china_airlines));
        spannableString.setSpan(new UnderlineSpan(),0,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        m_tvChinaAirlines.setText(spannableString);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_youtube), 47, 19.7);
        vScaleDef.setTextSize(14.7, m_tvChinaAirlines);
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        findViewById(R.id.iv_youtube).setOnClickListener(this);
        m_tvChinaAirlines.setOnClickListener(this);
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
    protected void onLanguageChangeUpdateUI() {

    }

    @Override
    public void onClick(View v) {
        Intent data ;
        switch (v.getId()){
            case R.id.iv_youtube:
            case R.id.tv_china_airlines:
                data = new Intent();
                data.setClass(this, CIWebViewActivity.class);
                data.putExtra(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG, getString(R.string.china_airlines_instroduction));
                data.putExtra(UiMessageDef.BUNDLE_WEBVIEW_URL_TAG,DEF_YOUTUBE_URL);
                startActivity(data);
                overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
                break;
        }
    }

    @Override
    public void onNetworkConnect() {
        super.onNetworkConnect();
        fetchYoutubeList();

    }

    private void fetchYoutubeList(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        m_fragment = new CIYouTubeListFragment();
        transaction.replace(findViewById(R.id.fl_youtube).getId(), m_fragment, m_fragment.getClass().getSimpleName());
        transaction.commit();
    }
}
