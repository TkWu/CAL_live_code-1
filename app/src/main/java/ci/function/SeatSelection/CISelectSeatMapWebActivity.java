package ci.function.SeatSelection;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseNoToolbarActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ws.define.BaseWSConfig;

/**
 * 選位頁面按下右上角之PopupWindow
 * Created by flowmahuang on 2016/5/9.
 */
public class CISelectSeatMapWebActivity extends BaseNoToolbarActivity {
    private RelativeLayout m_rlBackground = null;
    private ImageButton m_ivbtn_close   = null;
    private WebView     m_webView       = null;
    private Bitmap      m_bitmapBlur    = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.popupwindow_select_seat ;
    }

    @Override
    protected void initialLayoutComponent() {
        Bundle bundle   = getIntent().getExtras();
        //背景
        m_bitmapBlur    = bundle.getParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG);
        String strCode  = bundle.getString(UiMessageDef.BUNDLE_SELECT_SEAT_WEBVIEW_URL);

        m_rlBackground  = (RelativeLayout)findViewById(R.id.rl_bg);
        m_ivbtn_close   = (ImageButton) findViewById(R.id.ivbtn_close);
        m_webView       = (WebView) findViewById(R.id.select_seat_webview);

        //設定模糊背景
        BitmapDrawable drawable = new BitmapDrawable(m_Context.getResources(), m_bitmapBlur);
        m_rlBackground.setBackground(drawable);

        m_ivbtn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CISelectSeatMapWebActivity.this.finish();
            }
        });

        m_webView.setWebViewClient(new WebViewClient());
        //2016-12-14 Modify by Ryan
        //參照 mail 變更3.0 App 所使用的URL連結
        //yichien.chen@china-airlines.com
        //m_webView.loadUrl("http://mweb.china-airlines.com/aircraft_web/" + strCode + ".html");
        m_webView.loadUrl("https://" + BaseWSConfig.DEF_MOBILE30_BASE_WS_SITE + "/aircraft_web/" + strCode + ".html");
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams)m_ivbtn_close.getLayoutParams();
        rp.height = vScaleDef.getLayoutMinUnit(40);
        rp.width = vScaleDef.getLayoutMinUnit(40);
        rp.bottomMargin = vScaleDef.getLayoutHeight(16);

        rp = (RelativeLayout.LayoutParams)(findViewById(R.id.rlayout_card)).getLayoutParams();
        rp.height = vScaleDef.getLayoutHeight(504);
        rp.width = vScaleDef.getLayoutWidth(340);
        rp.leftMargin = vScaleDef.getLayoutWidth(10);
        rp.rightMargin = vScaleDef.getLayoutWidth(10);
        rp.topMargin = vScaleDef.getLayoutHeight(40);
        rp.bottomMargin = vScaleDef.getLayoutHeight(72);
        vScaleDef.setPadding(findViewById(R.id.rlayout_card), 3, 3, 3, 3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ImageHandle.recycleBitmap(m_bitmapBlur);
        System.gc();
    }
}
