package ci.function.BoardingPassEWallet;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseNoToolbarActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.view.ImageHandle;

/**
 * Created by Ryan on 16/3/14.
 * zeplin: 11.11-2
 * wireframe: p.81
 * 使用時機以及條件為 在Trip Detail先判斷使用者是否已經勾選不要提醒,
 * 未勾選擇則預設要顯示, 可使用
 * {@link AppInfo#GetBoardingPassIsCloseRemind()} () } 來確認是否要顯示該畫面
 */
public class CIBoardingWithQRCodeActivity extends BaseNoToolbarActivity {

    // check-in後 將得知登機證是否為有效. 當BoardingPass無效時, user須親自至機場取得登機證
    // 詳請見wireframe: p.81 -by Ling

    // UI WireFrame上面有分 登機證有效與無效時 要顯示的畫面不同
    // 但是現在的邏輯是 只要能線上check-in 就一定會有電子登機證
    public enum BoardingPassType{
        AVAILABLE, INVALID;
    }

    private BoardingPassType m_type         = BoardingPassType.AVAILABLE;

    private RelativeLayout  m_rlBackground  = null;
    private ImageView       m_ivCheckBox    = null;
    private ImageView       m_ivImage       = null;
    private ImageButton     m_ivbtn_close   = null;

    private Bitmap          m_bitmapBlur    = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.popupwindow_boarding_with_qrcode_view;
    }

    @Override
    protected void initialLayoutComponent() {
        Bundle bundle   = getIntent().getExtras();
        //背景
        m_bitmapBlur    = bundle.getParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG);
//        m_type          = BoardingPassType.valueOf(bundle.getString(UiMessageDef.BUNDLE_BOARDING_QRCODE_TYPE));

        m_rlBackground  = (RelativeLayout)findViewById(R.id.rl_bg);
        m_ivCheckBox    = (ImageView)findViewById(R.id.iv_checkbox);
        m_ivImage       = (ImageView)findViewById(R.id.iv_Image);
        m_ivbtn_close   = (ImageButton)findViewById(R.id.ivbtn_close);
        m_ivbtn_close.setOnClickListener(m_onClick);
        m_ivCheckBox.setOnClickListener(m_onClick);

        TextView tvTitle = (TextView)findViewById(R.id.tv_title);
        TextView tvContent = (TextView)findViewById(R.id.tv_content);

        //當登機證無效時, 需顯示以下訊息
        if (m_type.toString().equals(BoardingPassType.INVALID.name())){
            tvTitle.setText(m_Context.getString(R.string.get_boarding_pass_at_airport));
            tvContent.setText(m_Context.getString(R.string.due_to_local_government_regulations));
        }

        //設定模糊背景
        BitmapDrawable drawable = new BitmapDrawable(m_Context.getResources(), m_bitmapBlur);
        m_rlBackground.setBackground(drawable);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        ViewScaleDef vScale = ViewScaleDef.getInstance(m_Context);
        vScale.selfAdjustAllView(findViewById(R.id.rlayout_card));
        vScale.selfAdjustSameScaleView(m_ivCheckBox, 24, 24);
        vScale.selfAdjustSameScaleView(m_ivImage, 200, 304);
        vScale.setMargins(m_ivbtn_close, 0, 0, 0, 16);

        m_ivbtn_close.getLayoutParams().height= vScale.getLayoutMinUnit(40);
        m_ivbtn_close.getLayoutParams().width = vScale.getLayoutMinUnit(40);
    }

    View.OnClickListener m_onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if ( v.getId() == m_ivbtn_close.getId() ){

                CIBoardingWithQRCodeActivity.this.finish();

            } else if ( v.getId() == m_ivCheckBox.getId() ) {

                if ( m_ivCheckBox.isSelected() ){
                    m_ivCheckBox.setSelected(false);
                } else {
                    m_ivCheckBox.setSelected(true);
                }

                AppInfo.getInstance(m_Context).SetBoardingPassNoRemind(m_ivCheckBox.isSelected());
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ImageHandle.recycleBitmap(m_bitmapBlur);
        System.gc();
    }
}
