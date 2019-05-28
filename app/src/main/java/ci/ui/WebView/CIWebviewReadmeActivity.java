package ci.ui.WebView;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseNoToolbarActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;

/**
 * 注意事項
 * Created by kevin on 2018/1/15.
 */
public class CIWebviewReadmeActivity extends BaseNoToolbarActivity {


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_webview_readme;
    }

    @Override
    protected void initialLayoutComponent() {
        Bundle bundle   = getIntent().getExtras();

        //背景
        Bitmap      bitmap    = bundle.getParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG);
        ImageButton ibtnClose = (ImageButton) findViewById(R.id.ibtn_close);
        String strUrl = bundle.getString(UiMessageDef.BUNDLE_WEBVIEW_URL_TAG, "");

        CIWebView webView = (CIWebView)findViewById(R.id.webview);

        webView.loadUrl(strUrl);

        ibtnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                CIWebviewReadmeActivity.this.finish();
            }
        });

        RelativeLayout rl_bg = (RelativeLayout) findViewById(R.id.rl_bg);
        BitmapDrawable drawable = new BitmapDrawable(m_Context.getResources(), bitmap);
        rl_bg.setBackground(drawable);

    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.ibtn_close), 40,40);
    }



}
