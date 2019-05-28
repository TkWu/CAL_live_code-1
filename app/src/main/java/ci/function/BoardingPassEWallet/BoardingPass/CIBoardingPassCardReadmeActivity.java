package ci.function.BoardingPassEWallet.BoardingPass;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseNoToolbarActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ShadowBar.ShadowBarScrollview;

/**
 * 登機證背面注意事項
 * Created by kevin on 2017/12/15.
 */
public class CIBoardingPassCardReadmeActivity extends BaseNoToolbarActivity {


    //背景
    private Bitmap          m_bitmap        = null;

    private ImageButton     m_ibtnClose     = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_boarding_pass_card_readme;
    }

    @Override
    protected void initialLayoutComponent() {
        Bundle bundle   = getIntent().getExtras();

        //背景
        m_bitmap        = bundle.getParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG);
        m_ibtnClose     = (ImageButton)     findViewById(R.id.ibtn_close);

        TextView tvTitle = (TextView)findViewById(R.id.tv_title);

        ShadowBarScrollview shadowBarScrollview = (ShadowBarScrollview)findViewById(R.id.sv);
        LinearLayout content = shadowBarScrollview.getContentView();

        tvTitle.setText(getString(R.string.boardingpass_background_notes_title));

        TextView tvContent = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(320,
                                                                               LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.gravity = Gravity.CENTER;
        tvContent.setAutoLinkMask(Linkify.WEB_URLS);

        tvContent.setText(getString(R.string.boardingpass_background_notes_content));

        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, 16);
        content.addView(tvContent, layoutParams);

        m_ibtnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                CIBoardingPassCardReadmeActivity.this.finish();
            }
        });

        RelativeLayout rl_bg = (RelativeLayout) findViewById(R.id.rl_bg);
        BitmapDrawable drawable = new BitmapDrawable(m_Context.getResources(), m_bitmap);
        rl_bg.setBackground(drawable);

    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.ibtn_close), 40,40);
    }



}
