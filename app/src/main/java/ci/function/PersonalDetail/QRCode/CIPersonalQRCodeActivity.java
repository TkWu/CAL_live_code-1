package ci.function.PersonalDetail.QRCode;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.UnsupportedEncodingException;

import ci.function.Base.BaseNoToolbarActivity;
import ci.function.Core.CIApplication;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ws.define.CICardType;

/**
 * Created by jlchen on 2016/3/10.
 */
public class CIPersonalQRCodeActivity extends BaseNoToolbarActivity {
    private RelativeLayout  m_rlBackground  = null;
    private LinearLayout    m_llBackground  = null;
    private LinearLayout    m_llCard        = null;
    private ImageView       m_ivQRCode      = null;
    private ImageView       m_ivCard        = null;
    private ImageView       m_ivLogo        = null;
    private ImageButton     m_ibtnClose     = null;

    private TextView        m_tvName        = null;
    private TextView        m_tvCardType    = null;
    private TextView        m_tvCardNo      = null;

    private Bitmap          m_bitmapBlur    = null;

    private final int BLACK = Color.BLACK;
    private final int WHITE = Color.WHITE;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.popupwindow_personail_member_card_detail;
    }

    @Override
    protected void initialLayoutComponent() {
        Bundle bundle   = getIntent().getExtras();
        //背景
        m_bitmapBlur    = bundle.getParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG);
        String strQRcodeContent = bundle.getString(UiMessageDef.BUNDLE_PERSONAL_QRCODE);

        m_rlBackground = (RelativeLayout)findViewById(R.id.rl_bg);
        m_llBackground = (LinearLayout) findViewById(R.id.ll_bg);
        m_llCard = (LinearLayout) findViewById(R.id.ll_card);
        m_ivQRCode = (ImageView) findViewById(R.id.iv_qrcode);
        m_ivCard = (ImageView) findViewById(R.id.iv_card);
        m_ivLogo = (ImageView) findViewById(R.id.iv_logo);
        m_ibtnClose = (ImageButton) findViewById(R.id.ibtn_close);
        m_ibtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        m_tvName    = (TextView) findViewById(R.id.tv_name_data);
        m_tvCardType= (TextView) findViewById(R.id.tv_type_data);
        m_tvCardNo  = (TextView) findViewById(R.id.tv_number_data);

        //設定模糊背景
        BitmapDrawable drawable = new BitmapDrawable(m_Context.getResources(), m_bitmapBlur);
        m_rlBackground.setBackground(drawable);

        //設定自適應與qrcode圖
        UpdateView(strQRcodeContent);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {}

    public void UpdateView( final String strQRCodeContent ){
        ViewScaleDef viewScaleDef = ViewScaleDef.getInstance(m_Context);

        viewScaleDef.selfAdjustAllView(m_llBackground);

        String strCardType = CIApplication.getLoginInfo().GetCardType();
        switch (strCardType) {
            case CICardType.DYNA:
                m_ivCard.setImageResource(R.drawable.img_gray_card);
                m_tvCardType.setText(m_Context.getString(R.string.card_type_gray));
                break;
            case CICardType.EMER:
                m_ivCard.setImageResource(R.drawable.img_green_card);
                m_tvCardType.setText(m_Context.getString(R.string.card_type_green));
                break;
            case CICardType.GOLD:
                m_ivCard.setImageResource(R.drawable.img_gold_card);
                m_tvCardType.setText(m_Context.getString(R.string.card_type_gold));
                break;
            case CICardType.PARA:
                m_ivCard.setImageResource(R.drawable.img_blue_card);
                m_tvCardType.setText(m_Context.getString(R.string.card_type_blue));
                break;
            default:
                m_tvCardType.setText("");
                break;
        }

        m_tvName.setText(CIApplication.getLoginInfo().GetUserName());
        m_tvCardNo.setText(CIApplication.getLoginInfo().GetUserMemberCardNo());

        RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams)m_ibtnClose.getLayoutParams();
        rp.height = viewScaleDef.getLayoutMinUnit(40);
        rp.width = viewScaleDef.getLayoutMinUnit(40);
        rp.bottomMargin = viewScaleDef.getLayoutMinUnit(16);

        rp = (RelativeLayout.LayoutParams)m_ivCard.getLayoutParams();
        rp.height = viewScaleDef.getLayoutMinUnit(18);
        rp.width = viewScaleDef.getLayoutMinUnit(26.7);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)m_ivQRCode.getLayoutParams();
        lp.height = viewScaleDef.getLayoutMinUnit(186.7);
        lp.width = viewScaleDef.getLayoutMinUnit(186.7);

        final int iWidth = lp.width;

        lp = (LinearLayout.LayoutParams)m_ivLogo.getLayoutParams();
        lp.height = viewScaleDef.getLayoutMinUnit(18);
        lp.width = viewScaleDef.getLayoutMinUnit(125);

        m_ivQRCode.setImageBitmap(encodeToQRCode(strQRCodeContent,iWidth));
    }

    private Bitmap encodeToQRCode(final String strData , final int iWidth) {
        BitMatrix result;
        try {
            String strUtf8Data = new String(strData.getBytes("UTF-8"),"ISO-8859-1");;
            result = new MultiFormatWriter().encode(strUtf8Data, BarcodeFormat.QR_CODE,iWidth,iWidth);
        } catch (WriterException e) {
            // Unsupported format
            return null;
        } catch ( UnsupportedEncodingException e) {
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        bitmap.setPixels(pixels, 0, iWidth, 0, 0, w, h);
        return bitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ImageHandle.recycleBitmap(m_bitmapBlur);
        System.gc();
    }
}
