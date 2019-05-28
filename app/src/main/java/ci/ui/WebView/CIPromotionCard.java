package ci.ui.WebView;

import android.os.Bundle;
import android.view.View;

import com.chinaairlines.mobile30.R;

/** 首頁廣告牌卡 要做圓角處理
 * Created by jlchen on 2016/6/6.
 */
public class CIPromotionCard extends CIWebViewFragment {
    @Override
    protected int getLayoutResourceId(){
        return R.layout.layout_webview_cipromotion;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        m_webSettings = m_webView.getSettings();
        m_webSettings.setSupportZoom(false);
        m_webSettings.setUseWideViewPort(false);
    }

    //首頁牌卡·調整為顯示Logo title
    public String getTitle(){
        return "";
    }
}
