package ci.function.BoardingPassEWallet.BoardingPass;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.List;

import ci.function.Core.CIApplication;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.CIPNRStatusManager;
import ci.ui.object.SUtils;
import ci.ui.view.ImageHandle;
import ci.ui.view.ShadowBar.ShadowBarScrollview;
import ci.ws.Models.entities.CIBoardPassResp_BaggageInfo;
import ci.ws.Models.entities.CIBoardPassResp_Itinerary;
import ci.ws.Models.entities.CIBoardPassResp_PaxInfo;
import ci.ws.Models.entities.CIInquiryTripEntity;

/** 登機證卡片背景內容
 * Created by Kevin on 2017/12/12.
 */
public class CIBoardingPassInfoBackgroundCardFragment extends Fragment
                implements View.OnClickListener{


    public interface OnBackgroundCardFragmentListener {
        void onBaggageInfoClick( String strBaggageNumber, String strBaggageShowNum );
    }

    public static CIBoardingPassInfoBackgroundCardFragment newInstance(CIBoardPassResp_Itinerary itinerary,
                                                                       CIBoardPassResp_PaxInfo paxInfo,
                                                                       OnBackgroundCardFragmentListener Listener) {
        Bundle args = new Bundle();
        args.putSerializable(UiMessageDef.BUNDLE_BOARDING_PASS_DATA, itinerary);
        args.putSerializable(BOARDING_PASS_CARD_PAX_INFO_TAG, paxInfo);
        CIBoardingPassInfoBackgroundCardFragment fragment = new CIBoardingPassInfoBackgroundCardFragment();
        fragment.setCallBackListener(Listener);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(null != bundle){
            this.m_Itinerary = (CIBoardPassResp_Itinerary)
                    bundle.getSerializable(UiMessageDef.BUNDLE_BOARDING_PASS_DATA);
            this.m_PaxInfo = (CIBoardPassResp_PaxInfo)
                            bundle.getSerializable(BOARDING_PASS_CARD_PAX_INFO_TAG);
        }
    }

    private TextView m_tvName         = null;
    private TextView m_tvBookticket   = null;
    private TextView m_tvBookingNo    = null;
    private TextView m_tvBookingCabin = null;
    private TextView m_tvMemberNo     = null;

    private LinearLayout        m_llNoHaveBag         = null;
    private LinearLayout        m_llMemberCardNo      = null;
    private LinearLayout        m_llBarcodeContent    = null;
    private ShadowBarScrollview m_shadowBarScrollview = null;

    private ImageView m_ivInfo = null;

    private View      m_vGradient = null;

    private CIBoardPassResp_Itinerary m_Itinerary = null;
    private CIBoardPassResp_PaxInfo m_PaxInfo   = null;

    private boolean                 m_isShowBagInfo = true;

    private final String    DESH                = "-";
    private Context         m_context           = null;

    private static final String BOARDING_PASS_CARD_PAX_INFO_TAG = "BOARDING_PASS_CARD_PAX_INFO_TAG";

    private OnBackgroundCardFragmentListener m_onListener = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_boarding_pass_background_card, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        m_context = CIApplication.getContext();

        m_tvName = (TextView) view.findViewById(R.id.tv_name);
        m_tvBookticket = (TextView) view.findViewById(R.id.tv_bookticket);

        m_ivInfo = (ImageView) view.findViewById(R.id.iv_boarding_pass_info);
        m_ivInfo.setOnClickListener(this);


        m_shadowBarScrollview = (ShadowBarScrollview) view.findViewById(R.id.sv_bottom);
        LinearLayout content = m_shadowBarScrollview.getContentView();

        m_vGradient         = m_shadowBarScrollview.findViewById(R.id.vGradient);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View receiptContent     = inflater.inflate(R.layout.fragment_boarding_pass_background_card_receipt_content, null);

        m_tvBookingNo = (TextView) receiptContent.findViewById(R.id.tv_bookticet_num_value);
        m_tvBookingCabin = (TextView) receiptContent.findViewById(R.id.tv_bookticet_cabin_value);
        m_tvMemberNo = (TextView) receiptContent.findViewById(R.id.tv_membercard_num_value);

        m_llMemberCardNo = (LinearLayout) receiptContent.findViewById(R.id.ll_membercard_num);
        m_llNoHaveBag = (LinearLayout) receiptContent.findViewById(R.id.ll_no_have_bag_info);
        m_llBarcodeContent = (LinearLayout) receiptContent.findViewById(R.id.ll_barcode_content);

        content.addView(receiptContent);

        m_ivInfo.postDelayed(new Runnable() {
            @Override
            public void run() {
                setTextData();
                setViewVisibility();

            }
        },600);
        setTextSizeAndLayoutParams(view, ViewScaleDef.getInstance(getActivity()));
    }

    private void setTextData(){
        if ( null != m_PaxInfo && null != m_Itinerary ){

            SUtils utils = new SUtils();

            m_PaxInfo.First_Name = utils.getFilteredText(m_PaxInfo.First_Name,"");
            m_PaxInfo.Last_Name = utils.getFilteredText(m_PaxInfo.Last_Name,"");
            m_PaxInfo.Ticket = utils.getFilteredText(m_PaxInfo.Ticket,"");
            m_Itinerary.Pnr_Id = utils.getFilteredText(m_Itinerary.Pnr_Id,"");
            m_Itinerary.Booking_Class = utils.getFilteredText(m_Itinerary.Booking_Class,"");
            m_PaxInfo.Card_Id = utils.getFilteredText(m_PaxInfo.Card_Id,"");
            m_PaxInfo.Card_Id = utils.getFilteredText(m_PaxInfo.Card_Id,"");

            m_tvName.setText(String.format("%1$s %2$s",m_PaxInfo.First_Name,m_PaxInfo.Last_Name));
            m_tvBookticket.setText(m_PaxInfo.Ticket);
            m_tvBookingNo.setText(m_Itinerary.Pnr_Id);
            m_tvBookingCabin.setText(m_Itinerary.Booking_Class);
            if(TextUtils.isEmpty(m_PaxInfo.Card_Id)) {
                m_llMemberCardNo.setVisibility(View.GONE);
                m_tvMemberNo.setText("");
            } else {
                m_llMemberCardNo.setVisibility(View.VISIBLE);
                m_tvMemberNo.setText(m_PaxInfo.Card_Id);
            }

            //會員登入
            //登機證的名字等同會員本人，登機證背面的行李條才會顯示箭頭跟行李追蹤的文字，
            //非本人不顯示，也不可點擊行李追蹤。
            boolean bIsOwn = false;
            if ( CIApplication.getLoginInfo().GetLoginStatus() ){

                if ( TextUtils.equals(CIApplication.getLoginInfo().GetUserFirstName(), m_PaxInfo.First_Name)    &&
                        TextUtils.equals(CIApplication.getLoginInfo().GetUserLastName(), m_PaxInfo.Last_Name)   ){
                    bIsOwn = true;
                } else {
                    bIsOwn = false;
                }
            } else {
                //非會員登入
                //findmybooㄗking 的名字須符合登機證姓名才可查詢。
                List<CIInquiryTripEntity> m_listPNR = (ArrayList) CIPNRStatusManager.getInstance(null).getAllTripData();
                if ( null != m_listPNR && m_listPNR.size() > 0 ){
                    if ( TextUtils.equals(m_listPNR.get(0).firstname, m_PaxInfo.First_Name)    &&
                            TextUtils.equals(m_listPNR.get(0).lastname, m_PaxInfo.Last_Name)   ){
                        bIsOwn = true;
                    } else {
                        bIsOwn = false;
                    }
                } else {
                    bIsOwn = false;
                }
            }
            //

            Animation animation = AnimationUtils.loadAnimation(CIApplication.getContext(), R.anim.anim_alpha_in_1000);

            m_tvName.startAnimation(animation);
            m_tvBookticket.startAnimation(animation);
            m_tvBookingNo.startAnimation(animation);
            m_tvBookingCabin.startAnimation(animation);
            m_tvMemberNo.startAnimation(animation);


            if (null == m_PaxInfo.Baggage_Info || m_PaxInfo.Baggage_Info.size() <= 0) {
                m_isShowBagInfo = false;
                return;
            }


            m_llBarcodeContent.startAnimation(animation);
            m_vGradient.startAnimation(animation);

            for(int loop = 0; loop < m_PaxInfo.Baggage_Info.size();loop++) {
                initBarcodeContent(m_PaxInfo.Baggage_Info.get(loop), animation, bIsOwn);
            }

            m_shadowBarScrollview.scrollBy(0, -1);
        }
    }

    private void initBarcodeContent(final CIBoardPassResp_BaggageInfo baggageInfo, Animation animation, boolean bIsOwn ){

        SUtils utils = new SUtils();
        final ViewScaleDef viewScaleDef = ViewScaleDef.getInstance(m_context);

        int max = baggageInfo.Baggage_Numbers.size();
        LayoutInflater layoutInflater = LayoutInflater.from(m_context);

        final View vTitle = layoutInflater.inflate(R.layout.fragment_boarding_pass_background_card_receipt_item_title,null);

        baggageInfo.Baggage_Date = utils.getFilteredText(baggageInfo.Baggage_Date,"");
        baggageInfo.Baggage_BoardPoint = utils.getFilteredText(baggageInfo.Baggage_BoardPoint,"");
        baggageInfo.Baggage_OffPoint = utils.getFilteredText(baggageInfo.Baggage_OffPoint,"");


        TextView tvDate = (TextView) vTitle.findViewById(R.id.tv_flight_date);
        TextView tvFlight = (TextView) vTitle.findViewById(R.id.tv_trip_goto);

        tvDate.setText(baggageInfo.Baggage_Date);
        tvFlight.setText(String.format("%1$s  %2$s",
                                         m_context.getString(R.string.bag_to),
                                         baggageInfo.Baggage_OffPoint));

        tvDate.startAnimation(animation);
        tvFlight.startAnimation(animation);

        m_llBarcodeContent.addView(vTitle);

        viewScaleDef.selfAdjustAllView(vTitle);
        
        for(int loop = 0; loop < max; loop++) {
            final CIBoardPassResp_BaggageInfo.NumbersInfo numberInfo = baggageInfo.Baggage_Numbers.get(loop);

            final View view = layoutInflater.inflate(R.layout.fragment_boarding_pass_background_card_receipt_item,null);
            numberInfo.Baggage_IsStatus = utils.getFilteredText(numberInfo.Baggage_IsStatus,"");
            numberInfo.Baggage_ShowNumber = utils.getFilteredText(numberInfo.Baggage_ShowNumber, "");
            numberInfo.Baggage_BarcodeNumber = utils.getFilteredText(numberInfo.Baggage_BarcodeNumber, "");
            View vLine = (View)view.findViewById(R.id.v_line);
            ImageView imgArrow = (ImageView)view.findViewById(R.id.iv_arrow);
            //2018-12-26 調整行李追蹤邏輯，同iOS
            if(     !TextUtils.isEmpty(numberInfo.Baggage_ShowNumber) &&
                    bIsOwn &&
                    !TextUtils.isEmpty(numberInfo.Baggage_BarcodeNumber)   ) {
                //如果有行李號碼以及是本人，就設定可點選
                view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if ( null != m_onListener ){
                                m_onListener.onBaggageInfoClick(numberInfo.Baggage_BarcodeNumber, numberInfo.Baggage_ShowNumber);
                            }
                        }
                    });
                imgArrow.setVisibility(View.VISIBLE);
                vLine.setVisibility(View.VISIBLE);
            } else {
                imgArrow.setVisibility(View.INVISIBLE);
                vLine.setVisibility(View.INVISIBLE);
            }

            TextView tvName = (TextView)view.findViewById(R.id.tv_name);
            TextView tvBagNum = (TextView)view.findViewById(R.id.tv_bag_num);
            tvBagNum.setText(numberInfo.Baggage_ShowNumber);
            tvName.setText(m_tvName.getText());

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300, 113);
            layoutParams.topMargin = 8;
            if(loop == max - 1) {
                layoutParams.bottomMargin = 6;
            }

            m_llBarcodeContent.addView(view, layoutParams);
            viewScaleDef.selfAdjustAllView(view);
            HandlerThread handlerThread = new HandlerThread("workThread");
            handlerThread.start();

            Handler workHandler = new Handler(handlerThread.getLooper());
            workHandler.post(new Runnable() {
                @Override
                public void run() {

                    if(TextUtils.isEmpty(numberInfo.Baggage_BarcodeNumber)) {
                        return;
                    }

                    final Bitmap bitmap = ImageHandle.encodeToBarcode(numberInfo.Baggage_BarcodeNumber,
                                                                      viewScaleDef.getLayoutWidth(236.7),
                                                                      viewScaleDef.getLayoutHeight(32));
                    m_llBarcodeContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ImageView    barcode = (ImageView) view.findViewById(R.id.iv_barcode);
                            barcode.setBackground(new BitmapDrawable(null , bitmap));
                            barcode.startAnimation(AnimationUtils.loadAnimation(CIApplication.getContext(), R.anim.anim_alpha_in));
                        }
                    },500);
                }
            });


        }
    }

    private void setViewVisibility(){

        if(true == m_isShowBagInfo) {
            m_llBarcodeContent.setVisibility(View.VISIBLE);
            m_llNoHaveBag.setVisibility(View.GONE);
            m_vGradient.setVisibility(View.VISIBLE);
        } else {
            m_llBarcodeContent.setVisibility(View.GONE);
            m_llNoHaveBag.setVisibility(View.VISIBLE);
            m_vGradient.setVisibility(View.GONE);
        }

    }

    protected void setTextSizeAndLayoutParams(final View view, final ViewScaleDef vScaleDef) {

        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root));

        vScaleDef.selfAdjustSameScaleView(m_ivInfo, 32, 32);
        vScaleDef.selfAdjustSameScaleView(view.findViewById(R.id.iv_no_have_bag), 31.7, 31.7);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == m_ivInfo.getId()) {
            Intent intent = new Intent(getContext(), CIBoardingPassCardReadmeActivity.class);
            Bitmap bitmap = ImageHandle.getScreenShot(getActivity());
            Bitmap blur = ImageHandle.BlurBuilder(m_context, bitmap, 13.5f, 0.15f);

            Bundle bundle = new Bundle();
            bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, blur);

            intent.putExtras(bundle);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.anim_alpha_in, 0);
        }
    }

    public void setCallBackListener( OnBackgroundCardFragmentListener onListener ){
        m_onListener = onListener;
    }

}
