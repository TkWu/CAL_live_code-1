package ci.function.FlightStatus;

/**
 * Created by user on 2016/4/11.
 */

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseNoToolbarActivity;
import ci.function.Core.CIApplication;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.CILanguageInfo;
import ci.ui.view.ImageHandle;

public class CITrackActivity extends BaseNoToolbarActivity {

    private RelativeLayout  m_rlBackground  = null;
    private ImageView       m_ivImage       = null;
    private ImageButton     m_ivbtn_close   = null;

    private Bitmap          m_bitmapBlur    = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_track_popupwindow;
    }

    @Override
    protected void initialLayoutComponent() {
        Bundle bundle   = getIntent().getExtras();
        //背景
        m_bitmapBlur    = bundle.getParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG);

        m_rlBackground  = (RelativeLayout)findViewById(R.id.rl_bg);
        m_ivImage       = (ImageView) findViewById(R.id.iv_Image);
        m_ivbtn_close   = (ImageButton) findViewById(R.id.ivbtn_close);
        m_ivbtn_close.setOnClickListener(m_onClick);

        //設定模糊背景
        BitmapDrawable drawable = new BitmapDrawable(m_Context.getResources(), m_bitmapBlur);
        m_rlBackground.setBackground(drawable);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        //自適應
        ViewScaleDef vScale = ViewScaleDef.getInstance(m_Context);
        vScale.selfAdjustAllView(findViewById(R.id.rlayout_card));
        vScale.selfAdjustSameScaleView(m_ivImage, 200, 344);
        vScale.selfAdjustSameScaleView(m_ivbtn_close, 40, 40);
        vScale.setMargins(m_ivbtn_close, 0, 0, 0, 16);


        //因繼承 FragmentActivity 所以語系要另外更新一次
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.locale = CIApplication.getLanguageInfo().getLanguage_Locale();
        resources.updateConfiguration(config, dm);
        //

        TextView tvTitle = (TextView)findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.check_the_tracking));
        TextView tvContent = (TextView)findViewById(R.id.tv_content);
        tvContent.setText(getString(R.string.check_the_tracking_notice));

    }

    View.OnClickListener m_onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CITrackActivity.this.finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ImageHandle.recycleBitmap(m_bitmapBlur);
        System.gc();
    }
}

