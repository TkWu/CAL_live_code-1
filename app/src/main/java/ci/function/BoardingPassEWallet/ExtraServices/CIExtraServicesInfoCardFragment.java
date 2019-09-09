package ci.function.BoardingPassEWallet.ExtraServices;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Locale;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.view.DashedLine;
import ci.ui.view.ShadowBar.ShadowBarScrollview;
import ci.ws.Models.CIInquiryAllPassengerByPNRModel;
import ci.ws.Models.entities.CIEWallet_ExtraService_Info;
import ci.ws.Models.entities.BaggageEntity.FlightInfo;

import android.view.WindowManager;


import static ci.function.BoardingPassEWallet.Adapter.CIExtraServiceRecyclerViewAdapter.EServiceType;

/** 其他服務明細卡內容
 * Created by jlchen on 2016/3/24.
 */
public class CIExtraServicesInfoCardFragment extends BaseFragment{

    public static CIExtraServicesInfoCardFragment newInstance(
            CIEWallet_ExtraService_Info resp
            //EServiceType type, boolean bUsed
    ) {
        Bundle args = new Bundle();
        args.putSerializable(UiMessageDef.BUNDLE_EWALLET_EXTRA_SERVICES_DATA, resp);
//        args.putString(EXTRA_SERVICES_CARD_TYPE_TAG, type.name());
//        args.putBoolean(EXTRA_SERVICES_CARD_USE_TAG, bUsed);
        CIExtraServicesInfoCardFragment fragment = new CIExtraServicesInfoCardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(null != bundle){
            m_ExtraServiceData = (CIEWallet_ExtraService_Info)
                    bundle.getSerializable(UiMessageDef.BUNDLE_EWALLET_EXTRA_SERVICES_DATA);

//            this.m_type = EServiceType.valueOf(bundle.getString(EXTRA_SERVICES_CARD_TYPE_TAG));
//            this.m_bUsed = bundle.getBoolean(EXTRA_SERVICES_CARD_USE_TAG);
        }
    }

//    private EServiceType    m_type;
//    private boolean         m_bUsed         = false;
    private CIEWallet_ExtraService_Info m_ExtraServiceData = null;

//    private static final String EXTRA_SERVICES_DATA_TAG = "EXTRA_SERVICES_DATA_TAG";
    private static final String EXTRA_SERVICES_CARD_TYPE_TAG = "EXTRA_SERVICES_CARD_TYPE_TAG";
    private static final String EXTRA_SERVICES_CARD_USE_TAG = "EXTRA_SERVICES_CARD_USE_TAG";


    private ShadowBarScrollview m_shadowScrollView = null;
    private ScrollView      m_ScrollView    = null;
    private LinearLayout    m_llContent     = null;

    private RelativeLayout  m_rlHead        = null;
    private ImageView       m_ivHold        = null;
    private ImageView       m_ivUsed        = null;
    private ImageView       m_ivBlue        = null;
    private ImageView       m_ivLogo        = null;
    private ImageView       m_ivService     = null;
    private ImageView       m_ivQRCode      = null;

    private TextView        m_tvName        = null,
                            m_tvNo          = null,
                            m_tvService     = null,
                            m_tvline1       = null,
                            m_tvline1Data   = null,
                            m_tvline2       = null,
                            m_tvline2Data   = null,
                            m_tvline3       = null,
                            m_tvline3Data   = null,
                            m_tvline4       = null,
                            m_tvline4Data   = null,
                            m_tvline5       = null,
                            m_tvline5Data   = null,
                            m_tvNotice      = null;

    private RelativeLayout  m_rlLine4       = null,
                            m_rlLine6       = null;
    private LinearLayout    m_llLine4       = null,
                            m_llLine5       = null,
                            m_llBarcode     = null,
                            m_list          = null;

    private View            m_vLine3        = null,
                            m_vLine4        = null;

    private DashedLine      m_dl            = null;

    private final int BLACK = Color.BLACK;
    private final int WHITE = Color.WHITE;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_extra_services_card;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_shadowScrollView  = (ShadowBarScrollview) view.findViewById(R.id.shadowlayout);
        m_ScrollView        = m_shadowScrollView.getScrollView();
        m_llContent         = m_shadowScrollView.getContentView();

        View ViewContent    = m_layoutInflater.inflate( R.layout.layout_view_extra_services_card, null);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        m_llContent.addView(ViewContent, params);

        m_rlHead        = (RelativeLayout) ViewContent.findViewById(R.id.rl_head);
        m_ivHold        = (ImageView) ViewContent.findViewById(R.id.iv_hole);
        m_ivUsed        = (ImageView) ViewContent.findViewById(R.id.iv_used);
        m_ivBlue        = (ImageView) ViewContent.findViewById(R.id.iv_blue);
        m_ivQRCode      = (ImageView) ViewContent.findViewById(R.id.iv_qrcode);

        m_tvName        = (TextView) ViewContent.findViewById(R.id.tv_name);
        String strName  = "";
        if ( null != m_ExtraServiceData.FIRSTNAME )
            strName = m_ExtraServiceData.FIRSTNAME;
        if ( null != m_ExtraServiceData.LASTNAME )
            strName = strName +" "+ m_ExtraServiceData.LASTNAME;
        m_tvName.setText(strName);

        m_tvNo          = (TextView) ViewContent.findViewById(R.id.tv_no);
        if ( null != m_ExtraServiceData.TICKETNO )
            m_tvNo.setText(m_ExtraServiceData.TICKETNO);

        m_tvService     = (TextView) ViewContent.findViewById(R.id.tv_service);
        m_ivService     = (ImageView) ViewContent.findViewById(R.id.iv_service);

        m_dl            = (DashedLine) ViewContent.findViewById(R.id.dl);
        //清單最上層
        m_list     = (LinearLayout)ViewContent.findViewById(R.id.ll_list);
        //第一行資料
        m_tvline1       = (TextView) ViewContent.findViewById(R.id.tv_line1);
        m_tvline1Data   = (TextView) ViewContent.findViewById(R.id.tv_line1_data);
        //第二行資料
        m_tvline2       = (TextView) ViewContent.findViewById(R.id.tv_line2);
        m_tvline2Data   = (TextView) ViewContent.findViewById(R.id.tv_line2_data);
        //第三行資料
        m_tvline3       = (TextView) ViewContent.findViewById(R.id.tv_line3);
        m_tvline3Data   = (TextView) ViewContent.findViewById(R.id.tv_line3_data);
        //第四行資料
        m_tvline4       = (TextView) ViewContent.findViewById(R.id.tv_line4);
        m_tvline4Data   = (TextView) ViewContent.findViewById(R.id.tv_line4_data);
        m_rlLine4       = (RelativeLayout) ViewContent.findViewById(R.id.rl_line4);
        m_llLine4       = (LinearLayout) ViewContent.findViewById(R.id.ll_line4);
        //放條碼的layout
        m_llBarcode     = (LinearLayout)ViewContent.findViewById(R.id.ll_barcode);
        //第五行資料
        m_tvline5       = (TextView) ViewContent.findViewById(R.id.tv_line5);
        m_tvline5Data   = (TextView) ViewContent.findViewById(R.id.tv_line5_data);
        m_llLine5       = (LinearLayout)ViewContent.findViewById(R.id.ll_line5);

        m_rlLine6       = (RelativeLayout)ViewContent.findViewById(R.id.rl_line6);

        //第三條分隔線
        m_vLine3        = (View) ViewContent.findViewById(R.id.v_line3);
        //第四條分隔線
        m_vLine4        = (View) ViewContent.findViewById(R.id.v_line4);

        m_tvNotice      = (TextView) ViewContent.findViewById(R.id.tv_msg);

        m_ivLogo        = (ImageView) ViewContent.findViewById(R.id.iv_logo);

//        if ( true == m_bUsed ){
        if ( "Y".equals(m_ExtraServiceData.STATUS) ){
            m_ivUsed.setVisibility(View.VISIBLE);
            Locale locale = CIApplication.getLanguageInfo().getLanguage_Locale();
            switch (locale.toString()){
                case "zh_TW":
                    m_ivUsed.setImageResource(R.drawable.img_used_zh_tw);
                    break;
                case "zh_CN":
                    m_ivUsed.setImageResource(R.drawable.img_used_zh_cn);
                    break;
                case "en":
                    m_ivUsed.setImageResource(R.drawable.img_used_en);
                    break;
                case "ja_JP":
                    m_ivUsed.setImageResource(R.drawable.img_used_ja);
                    break;
                default:
                    m_ivUsed.setImageResource(R.drawable.img_used_zh_tw);
                    break;
            }

            m_ivBlue.setImageResource(R.drawable.bg_extra_services_used_2_blue);

            m_rlHead.setBackgroundResource(R.drawable.bg_boardimg_pass_head_radius_blue);
            m_ivHold.setImageResource(R.drawable.img_boarding_used_2);

            m_tvName.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_four));
            m_tvNo.setTextColor(ContextCompat.getColor(getActivity(), R.color.powder_blue));
            m_tvService.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_four));
        }else {
            m_ivUsed.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view,final ViewScaleDef vScaleDef) {


        int iHeight = vScaleDef.getLayoutHeight(1);
        if (1 > iHeight) {
            iHeight = 1;
        }
        m_dl.getLayoutParams().height = iHeight;

        SetViewVisibility(vScaleDef);

        vScaleDef.selfAdjustAllView(view.findViewById(R.id.extra_service_card_root));
        vScaleDef.selfAdjustSameScaleView(m_ivUsed, 80.7, 68.7);
        //vScaleDef.selfAdjustSameScaleView(view.findViewById(R.id.iv_barcode), 270, 56);
        vScaleDef.selfAdjustSameScaleView(m_ivService, 32, 32);
        vScaleDef.selfAdjustSameScaleView(m_ivLogo, 130, 20);

        vScaleDef.selfAdjustSameScaleView(m_ivQRCode, 186.7, 186.7);

        if (EServiceType.valueOf(m_ExtraServiceData.SERVICETYPE).equals(EServiceType.EVENT))
        {
            LinearLayout.LayoutParams tvline1Data_rp = (LinearLayout.LayoutParams) m_tvline1Data.getLayoutParams();
            tvline1Data_rp.width = vScaleDef.getLayoutWidth(190);

            LinearLayout.LayoutParams tvline1_rp = (LinearLayout.LayoutParams) m_tvline1.getLayoutParams();
            tvline1_rp.width = vScaleDef.getLayoutWidth(110);

            LinearLayout.LayoutParams tvline2Data_rp = (LinearLayout.LayoutParams) m_tvline2Data.getLayoutParams();
            tvline2Data_rp.width = vScaleDef.getLayoutWidth(190);

            LinearLayout.LayoutParams tvline2_rp = (LinearLayout.LayoutParams) m_tvline2.getLayoutParams();
            tvline2_rp.width = vScaleDef.getLayoutWidth(110);

            LinearLayout.LayoutParams tvline3Data_rp = (LinearLayout.LayoutParams) m_tvline3Data.getLayoutParams();
            tvline3Data_rp.width = vScaleDef.getLayoutWidth(190);

            LinearLayout.LayoutParams tvline3_rp = (LinearLayout.LayoutParams) m_tvline3.getLayoutParams();
            tvline3_rp.width = vScaleDef.getLayoutWidth(110);
            //rp.gravity = 16;


            //vScaleDef.selfAdjustSameScaleView(m_tvline3Data, 170, 50);
        }


        ViewTreeObserver vto = m_shadowScrollView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                m_shadowScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int iShadowSvHeight = m_shadowScrollView.getHeight();
                int iContentHeight = m_llContent.getHeight();

                if ( iShadowSvHeight > iContentHeight ){
                    m_shadowScrollView.getLayoutParams().height = iContentHeight;
                }
            }
        });
    }

    private String valueFormat( String value ){
        float fValue = 0.0f;
        try {
            fValue = Float.valueOf(value);
        } catch (Exception e){
            return "error";
        }

        DecimalFormat df        = new DecimalFormat("#,###,###,##0.##");
        String        strformat = df.format(fValue);

        return strformat;
    }

    private void SetViewVisibility(ViewScaleDef vScaleDef){

        int iWifiRes = R.drawable.ic_wifi_b_services;
        int iVipRes = R.drawable.ic_vip_b_services;
        int iBagRes = R.drawable.ic_baggage_b_services;
        int iHsrRes = R.drawable.ic_vip_b_high_speed_rail;
        int iFamilyRes = R.drawable.ic_family_couch_b;


        if ( "Y".equals(m_ExtraServiceData.STATUS) ){
//        if( true == m_bUsed ) {
            iWifiRes = R.drawable.ic_wifi_services;
            iVipRes = R.drawable.ic_vip_services;
            iBagRes = R.drawable.ic_baggage_services;
            iHsrRes = R.drawable.ic_vip_b_high_speed_rail_w;
            iFamilyRes = R.drawable.ic_family_couch_w;
        }



        String strDate = "";
        //依據type不同 要設定的card 高度也不同
        //三行177.8, 四行228.5, 四行帶qrcode 274.5, 五行279.2
        switch (EServiceType.valueOf(m_ExtraServiceData.SERVICETYPE)){
            /**
             * 加購行李服務
             */
            case EB:
                m_tvService.setText(getString(R.string.eb_extra_baggage));
                setExtraBagUI(iBagRes);
                break;

                /**
                 * 樂購行李服務
                 */
            case ExtraBaggage:
                m_tvService.setText(getString(R.string.extra_baggage));
                setExtraBagUI(iBagRes);
                break;
            case WIFI:
                m_ivService.setImageResource(iWifiRes);
                m_tvService.setText(getString(R.string.wifi));

                m_tvline1.setText(getString(R.string.boarding_pass_wifi_item));
                if ( null != m_ExtraServiceData.WIFIITEM ){
                    //去掉空格
                    m_tvline1Data.setText(m_ExtraServiceData.WIFIITEM.replaceAll("\\s+", ""));
                }

                m_tvline2.setText(getString(R.string.id));
                if ( null != m_ExtraServiceData.WIFIID ){
                    //去掉空格
                    m_tvline2Data.setText(m_ExtraServiceData.WIFIID.replaceAll("\\s+", ""));
                }

                m_tvline3.setText(getString(R.string.password));
                if ( null != m_ExtraServiceData.WIFIPASSWORD ){
                    //去掉空格
                    m_tvline3Data.setText(m_ExtraServiceData.WIFIPASSWORD.replaceAll("\\s+", ""));
                }

                m_tvline4.setText(getString(R.string.boarding_pass_wifi_expiry_date));
                if ( null != m_ExtraServiceData.WIFIEXPIRYDATE )
                    strDate = AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddHHmm(m_ExtraServiceData.WIFIEXPIRYDATE);
                m_tvline4Data.setText(strDate);

                m_llBarcode.setVisibility(View.GONE);
                m_vLine4.setVisibility(View.GONE);
                m_llLine5.setVisibility(View.GONE);
                m_rlLine6.setVisibility(View.GONE);

                m_tvNotice.setText(getString(R.string.boarding_pass_extra_services_wifi_notice));
                break;
            case VIP:
                m_ivService.setImageResource(iVipRes);
                m_tvService.setText(getString(R.string.vip_lounge));

                m_tvline1.setText(getString(R.string.extra_services_award_no));
                if ( null != m_ExtraServiceData.AWARDNO )
                    m_tvline1Data.setText(m_ExtraServiceData.AWARDNO);

                m_tvline2.setText(getString(R.string.boarding_pass_airport));
                if ( null != m_ExtraServiceData.AIRPORTNAME ){
                    //去掉空格
                    m_tvline2Data.setText(m_ExtraServiceData.AIRPORTNAME.replaceAll("\\s+", ""));
                }

                m_tvline3.setText(getString(R.string.home_terminal));
                if ( null != m_ExtraServiceData.AIRPORTTERMINAL )
                    m_tvline3Data.setText(m_ExtraServiceData.AIRPORTTERMINAL);

                m_tvline4.setText(getString(R.string.boarding_pass_flight_no));
                if ( null != m_ExtraServiceData.FLIGHTNO )
                    m_tvline4Data.setText(m_ExtraServiceData.FLIGHTNO);

                m_tvline5.setText(getString(R.string.date));
                if ( null != m_ExtraServiceData.FLIGHTDATE )
                    strDate = AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddHHmm(m_ExtraServiceData.FLIGHTDATE);
                m_tvline5Data.setText(strDate);

                m_llBarcode.setVisibility(View.GONE);
                m_rlLine6.setVisibility(View.GONE);

                m_tvNotice.setText(getString(R.string.boarding_pass_extra_services_vip_notice));
                break;
            case THSR:
                m_ivService.setImageResource(iHsrRes);
                m_tvService.setText(getString(R.string.trips_detail_hsr));

                m_tvline1.setText(getString(R.string.boarding_pass_order_no));
                if ( null != m_ExtraServiceData.THSRBOOKINGNO )
                    m_tvline1Data.setText(m_ExtraServiceData.THSRBOOKINGNO);

                m_tvline2.setText(getString(R.string.boarding_pass_authorization_code));
                if ( null != m_ExtraServiceData.THSRAUTHNO )
                    m_tvline2Data.setText(m_ExtraServiceData.THSRAUTHNO);

                m_tvline3.setText(getString(R.string.boarding_pass_date_time));
                if ( null != m_ExtraServiceData.THSRDEPTIME )
                    m_tvline3Data.setText(AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddHHmm(m_ExtraServiceData.THSRDEPTIME));

                m_tvline4.setText(getString(R.string.boarding_pass_train_class));
                if ( null != m_ExtraServiceData.THSRINFO )
                    m_tvline4Data.setText(m_ExtraServiceData.THSRINFO);

                m_tvline5.setText(getString(R.string.boarding_pass_departure_station));
                if ( null != m_ExtraServiceData.THSRDEPSTN )
                    m_tvline5Data.setText(m_ExtraServiceData.THSRDEPSTN);

                m_llBarcode.setVisibility(View.GONE);
                m_rlLine6.setVisibility(View.GONE);

                m_tvNotice.setText(getString(R.string.boarding_pass_extra_services_thsr_notice));
                break;
            case FamilyCouch:
                m_ivService.setImageResource(iFamilyRes);
                m_tvService.setText(getString(R.string.family_couch));

                m_tvline1.setText(getString(R.string.boarding_pass_e_miscellaneous_no));
                if ( null != m_ExtraServiceData.EMDNO )
                    m_tvline1Data.setText(m_ExtraServiceData.EMDNO);

                m_tvline2.setText(getString(R.string.boarding_pass_seat_no));
                if ( null != m_ExtraServiceData.FAMILYSEATNO )
                    m_tvline2Data.setText(m_ExtraServiceData.FAMILYSEATNO);

                m_tvline3.setText(getString(R.string.boarding_pass_flight_no));
                if ( null != m_ExtraServiceData.FLIGHTNO )
                    m_tvline3Data.setText(m_ExtraServiceData.FLIGHTNO);

                m_tvline4.setText(getString(R.string.date));
                if ( null != m_ExtraServiceData.DEPTIME )
                    strDate = AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddHHmm(m_ExtraServiceData.DEPTIME);
                m_tvline4Data.setText(strDate);

                m_llBarcode.setVisibility(View.GONE);
                m_vLine4.setVisibility(View.GONE);
                m_llLine5.setVisibility(View.GONE);
                m_rlLine6.setVisibility(View.GONE);
                break;
            case EVENT:

                /**
                 * 645400 - EVENT, Gold卡以上為紅色
                 */

                if ( null != m_ExtraServiceData.CARD_NO)
                    m_tvNo.setText(m_ExtraServiceData.CARD_NO + "-" + m_ExtraServiceData.CARD_TYPE);

                //String  strCardType = CIApplication.getLoginInfo().GetCardType();

                if ("N".equals(m_ExtraServiceData.STATUS)) {
                    if (!("Y".equals(m_ExtraServiceData.IS_HIGH_PRIORITY))) {
                        m_ivBlue.setImageResource(R.drawable.bg_extra_services_card_red);
                        iVipRes = R.drawable.ic_vip_services;
                        m_tvService.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_four));
                    }
                }


                m_ivService.setImageResource(iVipRes);
                m_tvService.setText(getString(R.string.event_vip));

                m_tvline1.setText(getString(R.string.event_name));
                if ( null != m_ExtraServiceData.EVENTNAME )
                    m_tvline1Data.setText(m_ExtraServiceData.EVENTNAME);


                m_tvline2.setText(getString(R.string.event_location));
                if ( null != m_ExtraServiceData.EVENTPLACE ){
                    //去掉空格
                    m_tvline2Data.setText(m_ExtraServiceData.EVENTPLACE.replaceAll("\\s+", ""));
                }

                m_tvline3.setText(getString(R.string.event_time));
                if ( null != m_ExtraServiceData.EVENTDATE )
                    m_tvline3Data.setText(m_ExtraServiceData.EVENTDATE);

                m_rlLine4.setVisibility(View.GONE);

                m_llLine5.setVisibility(View.GONE);

                m_ivQRCode.setImageBitmap(encodeToQRCode(m_ExtraServiceData.CODE,186));

                String temp = m_ExtraServiceData.NOTICE.replace("\\n", "\n");
                m_tvNotice.setText(temp);
                m_rlLine6.setVisibility(View.VISIBLE);

                m_tvNotice.setVisibility(View.VISIBLE);

                //643924 票卡亮度調亮
                //try {
                //    WindowManager.LayoutParams tempParam = null;
                //    tempParam = this.getActivity().getWindow().getAttributes();
                //    tempParam.screenBrightness = 1F;
                //    this.getActivity().getWindow().setAttributes(tempParam);

                //} catch (Exception e) {
                //    e.printStackTrace();
                //}
                //643924 票卡亮度調亮

                break;
        }
    }

    private Bitmap encodeToQRCode(final String strData , final int iWidth) {
        BitMatrix result;
        try {
            String strUtf8Data = new String(strData.getBytes("UTF-8"),"ISO-8859-1");
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

    private  void setExtraBagUI(int iBagRes){
        m_ivService.setImageResource(iBagRes);
        String strDate = "";
        /**
         * 電子收費憑證編號
         */
        m_tvline1.setText(getString(R.string.boarding_pass_e_miscellaneous_no));
        if ( null != m_ExtraServiceData.EMDNO )
            m_tvline1Data.setText(m_ExtraServiceData.EMDNO);
        /**
         * 加購行李
         */
        m_tvline2.setText(getString(R.string.eb_bag));
        if ( !TextUtils.isEmpty(m_ExtraServiceData.SSRAMOUNT) && !TextUtils.isEmpty(m_ExtraServiceData.SSRTYPE))
            m_tvline2Data.setText(m_ExtraServiceData.SSRAMOUNT + CIInquiryAllPassengerByPNRModel.getBagUnit(m_ExtraServiceData.SSRTYPE));

        /**
         * 加購金額
         */
        m_tvline3.setText(getString(R.string.eb_amount));
        if ( !TextUtils.isEmpty(m_ExtraServiceData.EBAMOUNT) && !TextUtils.isEmpty(m_ExtraServiceData.EBCURRENCY))
            m_tvline3Data.setText(m_ExtraServiceData.EBCURRENCY + " " + valueFormat(m_ExtraServiceData.EBAMOUNT));

        /**
         * 加購日期
         */
        m_tvline4.setText(getString(R.string.purchase_date));
        if (!TextUtils.isEmpty(m_ExtraServiceData.PURCHASE_DATE))
            strDate = AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormat(m_ExtraServiceData.PURCHASE_DATE);
        m_tvline4Data.setText(strDate);

        /**
         * 適用航班
         */
        if(null != m_ExtraServiceData.Flight_Info &&
                m_ExtraServiceData.Flight_Info.size() > 0) {

            int size = m_ExtraServiceData.Flight_Info.size();
            for(int loop = 0; loop < size;loop ++) {
                FlightInfo data    = m_ExtraServiceData.Flight_Info.get(loop);
                View       view    = m_layoutInflater.inflate(R.layout.layout_view_extra_services_card_line_view, null);
                TextView   key     = (TextView)view.findViewById(R.id.tv_key);
                TextView   value   = (TextView)view.findViewById(R.id.tv_value);
                View       line    = view.findViewById(R.id.v_line);
                int        endLine = size - 1;
                if(loop == endLine) {
                    line.setVisibility(View.GONE);
                }
                key.setText(getString(R.string.eb_flight_info));
                String date = AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormat(data.flight_date);
                value.setText(data.flight_num + "   " + date);
                m_list.addView(view);
            }
        } else {
            m_vLine4.setVisibility(View.GONE);
        }
        m_tvNotice.setText(getString(R.string.ewallet_notice));

        m_llBarcode.setVisibility(View.GONE);
        m_llLine5.setVisibility(View.GONE);
        m_rlLine6.setVisibility(View.GONE);
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

//    /** 將日期字串 yyyy-MM-dd 轉換成zeplin定義的格式MMM d, yyyy */
//    public String ConvertDateFormat(String strDay) {
//        // 定義時間格式
//        SimpleDateFormat sdfA = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat sdfB = new SimpleDateFormat("MMM d, yyyy");
//
//        try {
//            String strDate = sdfB.format(sdfA.parse(strDay));
//
//            return strDate;
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return strDay;
//        }
//    }
}
