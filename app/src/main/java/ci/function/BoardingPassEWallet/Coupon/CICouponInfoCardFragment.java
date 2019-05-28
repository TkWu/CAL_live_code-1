package ci.function.BoardingPassEWallet.Coupon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import ci.function.Core.SLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.DashedLine;
import ci.ui.view.ImageHandle;
import ci.ui.view.ShadowBar.ShadowBarScrollview;
import ci.ws.Models.entities.CIInquiryCoupon_Info;

/** 優惠券卡片內容
 * Created by jlchen on 2016/3/24.
 */
public class CICouponInfoCardFragment extends BaseFragment{

    public interface CouponInfoCardParameter{
        CIInquiryCoupon_Info getCouponInfo();
    }

    private CouponInfoCardParameter m_Parameter;

    private CIInquiryCoupon_Info m_Coupon   = null;

    private ShadowBarScrollview m_shadowScrollView  = null;
    private ScrollView          m_ScrollView        = null;
    private LinearLayout        m_llContent         = null;

    private ImageView           m_ivCoupon          = null;
    private ImageView           m_ivBarcode         = null;

    private TextView            m_tvNumber          = null;
    private TextView            m_tvOff             = null;
    private TextView            m_tvTitle           = null;
    private TextView            m_tvSubTitle        = null;
    private TextView            m_tvDate            = null;
    private TextView            m_tvFlightNo        = null;
    //新增加上 額外資訊
    private LinearLayout        m_llayout_msg       = null;
    private TextView            m_tvMsg             = null;
    private TextView            m_tvExcludedItem    = null;

    private DashedLine          m_dlTop             = null,
                                m_dlBottom          = null;

    private View                m_vLine             = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_coupon_card;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        if ( null != m_Parameter ){
            m_Coupon = m_Parameter.getCouponInfo();
        }

        m_shadowScrollView  = (ShadowBarScrollview) view.findViewById(R.id.shadowlayout);
        m_ScrollView        = m_shadowScrollView.getScrollView();
        m_llContent         = m_shadowScrollView.getContentView();

        View ViewContent    = m_layoutInflater.inflate( R.layout.layout_view_coupon_card, null);

        m_ivCoupon          = (ImageView) ViewContent.findViewById(R.id.iv_coupon);
        m_ivBarcode         = (ImageView) ViewContent.findViewById(R.id.iv_barcode);

        m_tvNumber          = (TextView) ViewContent.findViewById(R.id.tv_number);
        m_tvOff             = (TextView) ViewContent.findViewById(R.id.tv_off);
        m_tvTitle           = (TextView) ViewContent.findViewById(R.id.tv_title);
        m_tvSubTitle        = (TextView) ViewContent.findViewById(R.id.tv_sub_title);
        m_tvDate            = (TextView) ViewContent.findViewById(R.id.tv_date);
        m_tvFlightNo        = (TextView) ViewContent.findViewById(R.id.tv_flight_no_data);
        m_tvMsg             = (TextView) ViewContent.findViewById(R.id.tv_msg);

        m_llayout_msg       = (LinearLayout) ViewContent.findViewById(R.id.ll_msg);
        m_tvExcludedItem    = (TextView) ViewContent.findViewById(R.id.tv_excludeditem);

        m_dlTop             = (DashedLine) ViewContent.findViewById(R.id.dl_top);
        m_dlBottom          = (DashedLine) ViewContent.findViewById(R.id.dl_bottom);

        m_vLine             = (View) ViewContent.findViewById(R.id.v_line);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        m_llContent.addView(ViewContent, params);
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view.findViewById(R.id.coupon_card_root));
//        vScaleDef.selfAdjustAllView(m_llContent);
        vScaleDef.selfAdjustSameScaleView(m_ivBarcode , 270, 56);

        int iHeight = vScaleDef.getLayoutHeight(1);
        if (1 > iHeight) {
            iHeight = 1;
        }
        m_dlTop.getLayoutParams().height = iHeight;
        m_dlBottom.getLayoutParams().height = iHeight;
        m_vLine.getLayoutParams().height = iHeight;

        byte[] bInformationImage = Base64.decode(m_Coupon.InformationImage, Base64.DEFAULT);
        //圖片圓角處理
        Bitmap bitmap = BitmapFactory.decodeByteArray(
                bInformationImage,
                0,
                bInformationImage.length);
        Bitmap roundedBitmap = ImageHandle.getRoundedCornerBitmap(
                bitmap,
                vScaleDef.getLayoutMinUnit(3),
                true, true, false, false);
        m_ivCoupon.setImageBitmap(roundedBitmap);
        bitmap.recycle();
        System.gc();

        byte[] bBarCodeImage = Base64.decode(m_Coupon.BarCodeImage, Base64.DEFAULT);
        Bitmap bmBarCode = BitmapFactory.decodeByteArray(
                bBarCodeImage,
                0,
                bBarCodeImage.length);
        m_ivBarcode.setImageBitmap(bmBarCode);

        if ( null == m_Coupon.Title )
            m_Coupon.Title = "";
        m_tvTitle.setText(m_Coupon.Title);

        if ( null == m_Coupon.ExpiryDate )
            m_Coupon.ExpiryDate = "";
        m_tvDate.setText(m_Coupon.ExpiryDate);

        if ( null == m_Coupon.Discont )
            m_Coupon.Discont = "";
        m_tvNumber.setText(m_Coupon.Discont);

        if ( null == m_Coupon.DiscontUnit )
            m_Coupon.DiscontUnit = "";
        m_tvOff.setText(m_Coupon.DiscontUnit);

        //折扣文字變色
        String strDis = m_Coupon.Discont+m_Coupon.DiscontUnit;

        SpannableString spannableString = new SpannableString(
                String.format(
                        getString(R.string.enjoy_discount),
                        m_Coupon.Discont,
                        m_Coupon.DiscontUnit));
        SpannableStringBuilder spannableStringBuilder
                = new SpannableStringBuilder(spannableString);

        int index = spannableStringBuilder.toString()
                .indexOf(strDis, 0);
        int indexEnd = index + strDis.length();

        spannableStringBuilder.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.pinkish_red)),
                index,
                indexEnd,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        m_tvMsg.setText(spannableStringBuilder);
        m_llayout_msg.setMinimumHeight(vScaleDef.getLayoutHeight(65));

        if ( false == TextUtils.isEmpty(m_Coupon.ExcludedItem) ){
            m_tvExcludedItem.setVisibility(View.VISIBLE);
            m_tvExcludedItem.setText( String.format("(%s)", m_Coupon.ExcludedItem));
        } else {
            m_tvExcludedItem.setVisibility(View.GONE);
        }


        ViewTreeObserver vto = m_shadowScrollView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                m_shadowScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int iShadowSvHeight = m_shadowScrollView.getHeight();
                int iContentHeight = m_llContent.getHeight();

//                Log.e("iShadowSvHeight",""+m_shadowScrollView.getHeight());
//                Log.e("iContentHeight",""+m_llContent.getHeight());

                if ( iShadowSvHeight > iContentHeight ){
                    m_shadowScrollView.getLayoutParams().height = iContentHeight;
                }
            }
        });
    }

    @Override
    protected void setOnParameterAndListener(View view) {}

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

    public void uiSetParameterListener(CouponInfoCardParameter parameter) {
        m_Parameter = parameter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if ( null != m_llContent )
            m_llContent.removeAllViews();
        m_llContent = null;
        if ( null != m_ivBarcode )
            ImageHandle.recycleImageViewBitMap(m_ivBarcode);
        m_ivBarcode = null;
        if ( null != m_ivCoupon )
            ImageHandle.recycleImageViewBitMap(m_ivCoupon);
        m_ivCoupon = null;
    }
}
