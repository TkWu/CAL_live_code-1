package ci.function.BoardingPassEWallet.BoardingPass;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.Locale;

import ci.function.Core.CIApplication;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.view.ImageHandle;
import ci.ws.Models.entities.CIBoardPassResp_Itinerary;
import ci.ws.Models.entities.CIBoardPassResp_PaxInfo;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Presenter.CIInquiryFlightStationPresenter;

import android.view.WindowManager;


/** 登機證卡片前景內容
 * Created by jlchen on 2016/3/24.
 */
public class CIBoardingPassInfoForegroundCardFragment extends Fragment {

    public static CIBoardingPassInfoForegroundCardFragment newInstance(CIBoardPassResp_Itinerary itinerary,
                                                                       CIBoardPassResp_PaxInfo paxInfo,
                                                                       boolean bUsed) {
        Bundle args = new Bundle();
        args.putSerializable(UiMessageDef.BUNDLE_BOARDING_PASS_DATA, itinerary);
        args.putSerializable(BOARDING_PASS_CARD_PAX_INFO_TAG, paxInfo);
        args.putBoolean(BOARDING_PASS_CARD_USE_TAG, bUsed);
        CIBoardingPassInfoForegroundCardFragment fragment = new CIBoardingPassInfoForegroundCardFragment();
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
            this.m_bUsed = bundle.getBoolean(BOARDING_PASS_CARD_USE_TAG);
        }
    }

    private RelativeLayout  m_rl_boarding_pass  = null;
    private RelativeLayout  m_rlHead            = null;
    private RelativeLayout  m_rlHeadSP          = null;
//    private RelativeLayout  m_rlVipParent       = null;
    private ImageView       m_ivHold            = null;
    private ImageView       m_ivUsed            = null;
    private ImageView       m_ivAircraft        = null;
    private ImageView       m_ivBottom          = null;

    private TextView        m_tvName            = null;
    private TextView        m_tvSeatTitle       = null;
    private TextView        m_tvZoneTitle       = null;
    private TextView        m_tvSeat            = null;
    private TextView        m_tvZone            = null;
    private TextView        m_tvFrom            = null;
    private TextView        m_tvFromAirport     = null;
    private TextView        m_tvFromTime        = null;
    private TextView        m_tvAirNo           = null;
    private TextView        m_tvTo              = null;
    private TextView        m_tvToAirport       = null;
    private TextView        m_tvToTime          = null;
    private TextView        m_tvBoardingTime    = null;
    private TextView        m_tvBoardingDate    = null;
    private TextView        m_tvTerminal        = null;
    private TextView        m_tvGate            = null;
    private TextView        m_tvClass           = null;
    private TextView        m_tvSeq             = null;
    private TextView        m_tvVip             = null;
    //2019-01-03 新增 TSA_PRE Tag
    private TextView        m_tvTsaPre          = null;

    private ImageView       m_ivQRCode          = null;

    private LinearLayout    m_llVip             = null;
    private LinearLayout    m_llSky             = null;

    private CIBoardPassResp_Itinerary m_Itinerary = null;
    private CIBoardPassResp_PaxInfo m_PaxInfo   = null;
    private CIInquiryFlightStationPresenter     m_presenter = null;
    private boolean         m_bSky              = false;
    private boolean         m_bVip              = false;
    private boolean         m_bUsed             = false;
    private String          m_strQRCodeContent  = null;

    private final String    DESH                = "-";
    private Context         m_context           = null;

    private static final String BOARDING_PASS_CARD_PAX_INFO_TAG = "BOARDING_PASS_CARD_PAX_INFO_TAG";
    private static final String BOARDING_PASS_CARD_USE_TAG      = "BOARDING_PASS_CARD_USE_TAG";
    //2018-09-12 不應該寫死，這樣加一組圖都要包板，
    private static final String SP_TAG_KEY_TOP                  = "bg_top_";
    private static final String SP_TAG_KEY_BOTH                 = "bg_both_";
    private static final String SP_TAG_KEY_BOTTOM               = "bg_bottom_";
//    private static final String SP_TAG_CAERULEA                 = "caerulea";
//    private static final String SP_TAG_THREEBEARS               = "threebears";
//    private static final String SP_TAG_MIKADOPHEASANT           = "mikadopheasant";
//    private static final String SP_TAG_FLORATAICHUNG           = "floraTaichung";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_boarding_pass_foreground_card, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        m_context           = CIApplication.getContext();

        m_rl_boarding_pass  = (RelativeLayout) view.findViewById(R.id.rl_boarding_pass);
        m_rlHead            = (RelativeLayout) view.findViewById(R.id.rl_head);
//        m_rlVipParent       = (RelativeLayout) view.findViewById(R.id.rl_vip_parent);
        m_rlHeadSP          = (RelativeLayout) view.findViewById(R.id.rl_head_sp);
        m_ivHold            = (ImageView) view.findViewById(R.id.iv_hole);
        m_ivUsed            = (ImageView) view.findViewById(R.id.iv_used);
        m_ivAircraft        = (ImageView) view.findViewById(R.id.iv_aircraft_img);
        m_ivBottom          = (ImageView) view.findViewById(R.id.iv_bottom);

        m_tvName            = (TextView) view.findViewById(R.id.tv_name);
        m_tvSeatTitle       = (TextView) view.findViewById(R.id.tv_seat);
        m_tvZoneTitle       = (TextView) view.findViewById(R.id.tv_zone);
        m_tvSeat            = (TextView) view.findViewById(R.id.tv_seat_data);
        m_tvZone            = (TextView) view.findViewById(R.id.tv_zone_data);
        m_tvFrom            = (TextView) view.findViewById(R.id.tv_from);
        m_tvFromAirport     = (TextView) view.findViewById(R.id.tv_from_airport);
        m_tvFromTime        = (TextView) view.findViewById(R.id.tv_from_time);
        m_tvAirNo           = (TextView) view.findViewById(R.id.tv_aircraft_no);
        m_tvTo              = (TextView) view.findViewById(R.id.tv_to);
        m_tvToAirport       = (TextView) view.findViewById(R.id.tv_to_airport);
        m_tvToTime          = (TextView) view.findViewById(R.id.tv_to_time);
        m_tvBoardingTime    = (TextView) view.findViewById(R.id.tv_boarding_time);
        m_tvBoardingDate    = (TextView) view.findViewById(R.id.tv_boarding_date);
        m_tvTerminal        = (TextView) view.findViewById(R.id.tv_terminal_data);
        m_tvGate            = (TextView) view.findViewById(R.id.tv_gate_data);
        m_tvClass           = (TextView) view.findViewById(R.id.tv_class_data);
        m_tvSeq             = (TextView) view.findViewById(R.id.tv_seq_data);
        m_tvVip             = (TextView) view.findViewById(R.id.tv_vip);
        m_tvTsaPre          = (TextView) view.findViewById(R.id.tv_tsa_pre);

        m_ivQRCode          = (ImageView) view.findViewById(R.id.iv_qrcode);

        m_llVip             = (LinearLayout) view.findViewById(R.id.ll_vip);
        m_llSky             = (LinearLayout) view.findViewById(R.id.ll_sky);

        m_presenter         = CIInquiryFlightStationPresenter.getInstance(null, CIInquiryFlightStationPresenter.ESource.TimeTable);

        setTextData();
        setViewVisibility();
        setTextSizeAndLayoutParams(view, ViewScaleDef.getInstance(getActivity()));
    }

    private void setTextData(){
        if ( null != m_PaxInfo && null != m_Itinerary ){
            if ( null == m_PaxInfo.Sky_Priority )
                m_PaxInfo.Sky_Priority = "";

            if ( "Y".equals(m_PaxInfo.Sky_Priority) )
                m_bSky = true;

            //調整貴賓室以及SkyPriority 邏輯
            //只要有顯示VIP Lounge，皆需顯示，Sky Priority
            //if ( null != m_PaxInfo.Lounge && 0 < m_PaxInfo.Lounge.length() )
            //    m_bVip = true;
            if ( null != m_PaxInfo.Is_Lounge && TextUtils.equals("Y", m_PaxInfo.Is_Lounge) ){
                m_bVip = true;
                m_bSky = true;
            }
            if ( TextUtils.isEmpty( m_PaxInfo.Lounge ) ){
                m_tvVip.setText(m_context.getString(R.string.vip_lounge));
            } else {
                m_tvVip.setText(m_context.getString(R.string.vip_lounge) + " - " + m_PaxInfo.Lounge);
            }

            if ( null == m_PaxInfo.First_Name )
                m_PaxInfo.First_Name = "";
            if ( null == m_PaxInfo.Last_Name )
                m_PaxInfo.Last_Name = "";
            m_tvName.setText(m_PaxInfo.First_Name+" "+m_PaxInfo.Last_Name);

            if ( null == m_PaxInfo.Seat_Number )
                m_PaxInfo.Seat_Number = "";
            m_tvSeat.setText(m_PaxInfo.Seat_Number);

            if ( null == m_Itinerary.Departure_Station )
                m_Itinerary.Departure_Station = "";
            m_tvFrom.setText(m_Itinerary.Departure_Station);

            if ( TextUtils.isEmpty(m_Itinerary.Departure_Station) ){
                m_tvFromAirport.setText(DESH);
            } else {
                CIFlightStationEntity data = m_presenter.getStationInfoByIATA(m_Itinerary.Departure_Station);
                if(null != data){
                    m_tvFromAirport.setText(data.airport_name);
                } else {
                    m_tvFromAirport.setText(DESH);
                }
            }

            if ( null == m_Itinerary.Departure_Time )
                m_Itinerary.Departure_Time = "";
            m_tvFromTime.setText(m_Itinerary.Departure_Time);

            if ( null == m_Itinerary.Airlines )
                m_Itinerary.Airlines = "";
            if ( null == m_Itinerary.Flight_Number )
                m_Itinerary.Flight_Number = "";
            m_tvAirNo.setText(m_Itinerary.Airlines+" "+m_Itinerary.Flight_Number);

            if ( null == m_Itinerary.Arrival_Station )
                m_Itinerary.Arrival_Station = "";
            m_tvTo.setText(m_Itinerary.Arrival_Station);

            if ( TextUtils.isEmpty(m_Itinerary.Arrival_Station) ){
                m_tvToAirport.setText(DESH);
            } else {
                CIFlightStationEntity data =m_presenter.getStationInfoByIATA(m_Itinerary.Arrival_Station);
                if(null != data){
                    m_tvToAirport.setText(data.airport_name);
                } else {
                    m_tvToAirport.setText(DESH);
                }
            }

            if ( null == m_Itinerary.Arrival_Time )
                m_Itinerary.Arrival_Time = "";
            m_tvToTime.setText(m_Itinerary.Arrival_Time);

            if ( null == m_Itinerary.Boarding_Time )
                m_Itinerary.Boarding_Time = "";
            m_tvBoardingTime.setText(m_Itinerary.Boarding_Time);

            if ( null == m_Itinerary.Boarding_Date )
                m_Itinerary.Boarding_Date = "";
            m_tvBoardingDate.setText(AppInfo.getInstance(getActivity()).ConvertDateFormatToyyyyMMddEEE(
                    m_Itinerary.Boarding_Date));

            if ( null == m_Itinerary.Departure_Terminal )
                m_Itinerary.Departure_Terminal = "";
            m_tvTerminal.setText(m_Itinerary.Departure_Terminal);

            if ( null == m_Itinerary.Boarding_Gate )
                m_Itinerary.Boarding_Gate = "";
            m_tvGate.setText(m_Itinerary.Boarding_Gate);

            if ( null == m_Itinerary.Class_Of_Service_Desc )
                m_Itinerary.Class_Of_Service_Desc = "";
            m_tvClass.setText(m_Itinerary.Class_Of_Service_Desc);

            if ( null == m_PaxInfo.Seat_Zone )
                m_PaxInfo.Seat_Zone = "";
            m_tvZone.setText(m_PaxInfo.Seat_Zone);


            if ( null == m_PaxInfo.Seq_No )
                m_PaxInfo.Seq_No = "";
            m_tvSeq.setText(m_PaxInfo.Seq_No);

            if ( null != m_PaxInfo.Boarding_Pass )
                m_strQRCodeContent = m_PaxInfo.Boarding_Pass;

            if (TextUtils.equals("Y", m_PaxInfo.TSA_PRE) ){
                m_tvTsaPre.setVisibility(View.VISIBLE);
            } else {
                m_tvTsaPre.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setViewVisibility(){

        if (!m_bVip){
            m_llVip.setVisibility(View.GONE);
        }else {
            m_llVip.setVisibility(View.VISIBLE);
        }

        if (!m_bSky){
            m_llSky.setVisibility(View.GONE);
        }else {
            m_llSky.setVisibility(View.VISIBLE);
        }

        if (m_bUsed){
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
            m_rlHead.setBackgroundResource(R.drawable.bg_boardimg_pass_head_radius_blue);
            m_ivHold.setImageResource(R.drawable.img_boarding_used_2);

            m_tvName.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_four));
            m_tvSeatTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.powder_blue));
            m_tvSeat.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_four));
        }else {
            m_ivUsed.setVisibility(View.GONE);
        }
        //調整邏輯判斷，此處不應該寫死，不然每次加一組圖檔都要改Code
        if ( null != m_PaxInfo && !TextUtils.isEmpty(m_PaxInfo.Boarding_Pass_SP) ){

            //特殊邏輯，因Tag 誤植大寫英文，會導致Android 對應圖檔失敗，所以此處強轉小寫
            if ( TextUtils.equals("60Anniversarys", m_PaxInfo.Boarding_Pass_SP)){
                m_PaxInfo.Boarding_Pass_SP = m_PaxInfo.Boarding_Pass_SP.toLowerCase();
            }
            int iImgResTop      = AppInfo.getInstance(getContext()).GetIconResourceId(SP_TAG_KEY_TOP, m_PaxInfo.Boarding_Pass_SP );
            int iImgResBootom   = AppInfo.getInstance(getContext()).GetIconResourceId(SP_TAG_KEY_BOTTOM, m_PaxInfo.Boarding_Pass_SP );
            int iImgResBoth     = AppInfo.getInstance(getContext()).GetIconResourceId(SP_TAG_KEY_BOTH, m_PaxInfo.Boarding_Pass_SP );
            if ( iImgResTop > 0 && iImgResBootom > 0 && iImgResBoth > 0 ){

                m_rlHeadSP.setBackgroundResource(iImgResTop);
                m_ivAircraft.setImageResource(iImgResBoth);
                m_ivBottom.setImageResource(iImgResBootom);

            } else {
                m_rlHeadSP.setBackground(null);
                m_ivAircraft.setImageResource(R.drawable.bg_both_flight);
                m_ivBottom.setImageDrawable(null);
            }
        } else {
            m_rlHeadSP.setBackground(null);
            m_ivAircraft.setImageResource(R.drawable.bg_both_flight);
            m_ivBottom.setImageDrawable(null);
        }
        //
//        //CR 加入藍鵲號判斷
//        if(null != m_PaxInfo && !TextUtils.isEmpty(m_PaxInfo.Boarding_Pass_SP) && m_PaxInfo.Boarding_Pass_SP.equals(SP_TAG_CAERULEA)) {
//            m_rlHeadSP.setBackgroundResource(R.drawable.bg_top_caerulea);
//            m_ivAircraft.setImageResource(R.drawable.bg_both_caerulea);
//            m_ivBottom.setImageResource(R.drawable.bg_bottom_caerulea);
//        } else if (null != m_PaxInfo && !TextUtils.isEmpty(m_PaxInfo.Boarding_Pass_SP) && m_PaxInfo.Boarding_Pass_SP.equals(SP_TAG_THREEBEARS)){
//            //加入三熊機判斷
//            m_rlHeadSP.setBackgroundResource(R.drawable.bg_top_threebears);
//            m_ivAircraft.setImageResource(R.drawable.bg_both_threebears);
//            m_ivBottom.setImageResource(R.drawable.bg_bottom_threebears);
//        } else if (null != m_PaxInfo && !TextUtils.isEmpty(m_PaxInfo.Boarding_Pass_SP) && m_PaxInfo.Boarding_Pass_SP.equals(SP_TAG_MIKADOPHEASANT)){
//            //加入帝雉機判斷
//            m_rlHeadSP.setBackgroundResource(R.drawable.bg_top_mikadopheasant);
//            m_ivAircraft.setImageResource(R.drawable.bg_both_mikadopheasant);
//            m_ivBottom.setImageResource(R.drawable.bg_bottom_mikadopheasant);
//        } else if (null != m_PaxInfo && !TextUtils.isEmpty(m_PaxInfo.Boarding_Pass_SP) && m_PaxInfo.Boarding_Pass_SP.equals(SP_TAG_FLORATAICHUNG)){
//            //加入加入台中花博判斷
//            m_rlHeadSP.setBackgroundResource(R.drawable.bg_top_florataichung);
//            m_ivAircraft.setImageResource(R.drawable.bg_both_florataichung);
//            m_ivBottom.setImageResource(R.drawable.bg_bottom_florataichung);
//        } else {
//            m_rlHeadSP.setBackground(null);
//            m_ivAircraft.setImageResource(R.drawable.bg_both_flight);
//            m_ivBottom.setImageDrawable(null);
//        }

        //643924 票卡亮度調亮
        try {
            WindowManager.LayoutParams tempParam = null;
            tempParam = this.getActivity().getWindow().getAttributes();
            tempParam.screenBrightness = 1F;
            this.getActivity().getWindow().setAttributes(tempParam);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //643924 票卡亮度調亮
    }

    protected void setTextSizeAndLayoutParams(final View view, final ViewScaleDef vScaleDef) {

//        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root));
        //vScaleDef.setPadding(view.findViewById(R.id.root), 10, 0, 10, 0);
        vScaleDef.setPadding(view.findViewById(R.id.root), 10, 0, 10, 0);

        LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) m_rl_boarding_pass.getLayoutParams();
        llp.height = vScaleDef.getLayoutHeight(483);

        RelativeLayout.LayoutParams rp  = (RelativeLayout.LayoutParams) m_rlHead.getLayoutParams();
        rp.height = vScaleDef.getLayoutHeight(74.5);

        rp = (RelativeLayout.LayoutParams) m_rlHeadSP.getLayoutParams();
        rp.height = vScaleDef.getLayoutHeight(74.5);

        rp = (RelativeLayout.LayoutParams) view.findViewById(R.id.rl_bottom).getLayoutParams();
        rp.height = vScaleDef.getLayoutHeight(380);

        rp = (RelativeLayout.LayoutParams) m_tvName.getLayoutParams();
        rp.width = vScaleDef.getLayoutWidth(240);
        rp.height = vScaleDef.getLayoutHeight(24);
        rp.topMargin = vScaleDef.getLayoutHeight(28);
        rp.leftMargin = vScaleDef.getLayoutWidth(20);
        vScaleDef.setTextSize(20, m_tvName);

        rp = (RelativeLayout.LayoutParams) view.findViewById(R.id.ll_seat).getLayoutParams();
        rp.width = vScaleDef.getLayoutWidth(70);
        rp.topMargin = vScaleDef.getLayoutHeight(16);
        rp.rightMargin = vScaleDef.getLayoutWidth(4);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) m_tvSeatTitle.getLayoutParams();
        lp.height = vScaleDef.getLayoutHeight(15.7);
        vScaleDef.setTextSize(13, m_tvSeatTitle);

        lp = (LinearLayout.LayoutParams) m_tvSeat.getLayoutParams();
        lp.height = vScaleDef.getLayoutHeight(28.7);
        lp.topMargin = vScaleDef.getLayoutHeight(4);
        vScaleDef.setTextSize(24, m_tvSeat);

        rp = (RelativeLayout.LayoutParams) view.findViewById(R.id.ll_zone).getLayoutParams();
        rp.width = vScaleDef.getLayoutWidth(70);
        rp.topMargin = vScaleDef.getLayoutHeight(16);
        rp.rightMargin = vScaleDef.getLayoutWidth(4);

        lp = (LinearLayout.LayoutParams) m_tvZoneTitle.getLayoutParams();
        lp.height = vScaleDef.getLayoutHeight(15.7);
        vScaleDef.setTextSize(13, m_tvZoneTitle);

        lp = (LinearLayout.LayoutParams) m_tvZone.getLayoutParams();
        lp.height = vScaleDef.getLayoutHeight(28.7);
        lp.topMargin = vScaleDef.getLayoutHeight(4);
        vScaleDef.setTextSize(24, m_tvZone);


        rp = (RelativeLayout.LayoutParams) view.findViewById(R.id.dl).getLayoutParams();
        int iHeight = vScaleDef.getLayoutHeight(1);
        if ( 1 > iHeight )
            iHeight = 1;
        rp.height = iHeight;
        rp.width = vScaleDef.getLayoutWidth(319.2);
        rp.topMargin = vScaleDef.getLayoutHeight(80);

        rp = (RelativeLayout.LayoutParams) view.findViewById(R.id.ll_from).getLayoutParams();
        rp.width = vScaleDef.getLayoutWidth(130);
        rp.leftMargin = vScaleDef.getLayoutWidth(20);
        rp.topMargin = vScaleDef.getLayoutHeight(95.7);

        lp = (LinearLayout.LayoutParams) m_tvFrom.getLayoutParams();
        lp.height = vScaleDef.getLayoutHeight(23.7);
        vScaleDef.setTextSize(18, m_tvFrom);

        m_tvFromAirport.setMinHeight(vScaleDef.getLayoutHeight(15.7));
        vScaleDef.setTextSize(13, m_tvFromAirport);

        lp = (LinearLayout.LayoutParams) m_tvFromTime.getLayoutParams();
        lp.height = vScaleDef.getLayoutHeight(19.3);
        lp.topMargin = vScaleDef.getLayoutHeight(1);
        vScaleDef.setTextSize(16, m_tvFromTime);

        rp = (RelativeLayout.LayoutParams) view.findViewById(R.id.ll_to).getLayoutParams();
        rp.width = vScaleDef.getLayoutWidth(130);
        rp.rightMargin = vScaleDef.getLayoutWidth(20);
        rp.topMargin = vScaleDef.getLayoutHeight(95.7);

        lp = (LinearLayout.LayoutParams) m_tvTo.getLayoutParams();
        lp.height = vScaleDef.getLayoutHeight(23.7);
        vScaleDef.setTextSize(18, m_tvTo);

        m_tvToAirport.setMinHeight(vScaleDef.getLayoutHeight(15.7));
        vScaleDef.setTextSize(13, m_tvToAirport);

        lp = (LinearLayout.LayoutParams) m_tvToTime.getLayoutParams();
        lp.height = vScaleDef.getLayoutHeight(19.3);
        lp.topMargin = vScaleDef.getLayoutHeight(1);
        vScaleDef.setTextSize(16, m_tvToTime);

        rp = (RelativeLayout.LayoutParams) m_tvAirNo.getLayoutParams();
        rp.height = vScaleDef.getLayoutHeight(19.3);
        m_tvAirNo.setTranslationY(-vScaleDef.getLayoutHeight(19.3/2));
        vScaleDef.setTextSize(16, m_tvAirNo);

        rp = (RelativeLayout.LayoutParams) view.findViewById(R.id.ll_boarding).getLayoutParams();
        rp.width = vScaleDef.getLayoutWidth(110);
        rp.leftMargin = vScaleDef.getLayoutWidth(20);
        rp.topMargin = vScaleDef.getLayoutHeight(356.3);

        TextView tvBoarding = (TextView) view.findViewById(R.id.tv_boarding);
        lp = (LinearLayout.LayoutParams) tvBoarding.getLayoutParams();
        lp.height = vScaleDef.getLayoutHeight(15.7);
        vScaleDef.setTextSize(13, tvBoarding);

        lp = (LinearLayout.LayoutParams) m_tvBoardingTime.getLayoutParams();
        lp.height = vScaleDef.getLayoutHeight(48);
        lp.topMargin = vScaleDef.getLayoutHeight(1.3);
        vScaleDef.setTextSize(40, m_tvBoardingTime);

        m_tvBoardingDate.setMinHeight(vScaleDef.getLayoutHeight(15.7));
        vScaleDef.setTextSize(13, m_tvBoardingDate);

        rp = (RelativeLayout.LayoutParams) view.findViewById(R.id.ll_terminal).getLayoutParams();
        rp.width = vScaleDef.getLayoutWidth(160);
        rp.leftMargin = vScaleDef.getLayoutWidth(170);
        rp.topMargin = vScaleDef.getLayoutHeight(356.3);

        TextView tvTerminal = (TextView) view.findViewById(R.id.tv_terminal);
        tvTerminal.setMinHeight(vScaleDef.getLayoutHeight(15.7));
        vScaleDef.setTextSize(13, tvTerminal);

        lp = (LinearLayout.LayoutParams) m_tvTerminal.getLayoutParams();
        lp.leftMargin = vScaleDef.getLayoutWidth(3);
        m_tvTerminal.setMinHeight(vScaleDef.getLayoutHeight(15.7));
        vScaleDef.setTextSize(13, m_tvTerminal);

        lp = (LinearLayout.LayoutParams) view.findViewById(R.id.ll_gate).getLayoutParams();
        lp.topMargin = vScaleDef.getLayoutHeight(6);

        TextView tvGate = (TextView) view.findViewById(R.id.tv_gate);
        tvGate.setMinHeight(vScaleDef.getLayoutHeight(15.7));
        vScaleDef.setTextSize(13, tvGate);

        lp = (LinearLayout.LayoutParams) m_tvGate.getLayoutParams();
        lp.leftMargin = vScaleDef.getLayoutWidth(3);
        m_tvGate.setMinHeight(vScaleDef.getLayoutHeight(15.7));
        vScaleDef.setTextSize(22, m_tvGate);

        lp = (LinearLayout.LayoutParams) view.findViewById(R.id.ll_class).getLayoutParams();
        lp.topMargin = vScaleDef.getLayoutHeight(6);

        TextView tvClass = (TextView) view.findViewById(R.id.tv_class);
        tvClass.setMinHeight(vScaleDef.getLayoutHeight(15.7));
        vScaleDef.setTextSize(13, tvClass);

        lp = (LinearLayout.LayoutParams) m_tvClass.getLayoutParams();
        lp.leftMargin = vScaleDef.getLayoutWidth(3);
        m_tvClass.setMinHeight(vScaleDef.getLayoutHeight(15.7));
        vScaleDef.setTextSize(13, m_tvClass);

        lp = (LinearLayout.LayoutParams) view.findViewById(R.id.ll_seq).getLayoutParams();
        lp.topMargin = vScaleDef.getLayoutHeight(6);

        TextView tvSeq = (TextView) view.findViewById(R.id.tv_seq);
        tvSeq.setMinHeight(vScaleDef.getLayoutHeight(15.7));
        vScaleDef.setTextSize(13, tvSeq);

        lp = (LinearLayout.LayoutParams) m_tvSeq.getLayoutParams();
        lp.leftMargin = vScaleDef.getLayoutWidth(3);
        m_tvSeq.setMinHeight(vScaleDef.getLayoutHeight(15.7));
        vScaleDef.setTextSize(13, m_tvSeq);


//        rp = (RelativeLayout.LayoutParams) m_rlVipParent.getLayoutParams();
//        rp.height = vScaleDef.getLayoutHeight(24);
        //rp.height = vScaleDef.getLayoutWidth(184);
        //rp.topMargin = vScaleDef.getLayoutHeight(286);

        rp = (RelativeLayout.LayoutParams) view.findViewById(R.id.rl_vip_bg).getLayoutParams();
        rp.height = vScaleDef.getLayoutHeight(24);

        rp = (RelativeLayout.LayoutParams) view.findViewById(R.id.rl_vip).getLayoutParams();
        rp.height = vScaleDef.getLayoutHeight(24);

        lp = (LinearLayout.LayoutParams) m_tvVip.getLayoutParams();
        lp.leftMargin = vScaleDef.getLayoutWidth(6);
        vScaleDef.setTextSize(13, m_tvVip);

        lp = (LinearLayout.LayoutParams) view.findViewById(R.id.rl_logo).getLayoutParams();
        lp.height = vScaleDef.getLayoutHeight(32);
        rp = (RelativeLayout.LayoutParams) view.findViewById(R.id.ll_sky).getLayoutParams();
        rp.width = vScaleDef.getLayoutWidth(72);

        rp = (RelativeLayout.LayoutParams) m_ivUsed.getLayoutParams();
        rp.topMargin = vScaleDef.getLayoutHeight(5.8);
        rp.leftMargin = vScaleDef.getLayoutWidth(180.7);
        vScaleDef.selfAdjustSameScaleView(m_ivUsed, 80.7, 68.7);

        rp = (RelativeLayout.LayoutParams) m_ivHold.getLayoutParams();
        rp.height = vScaleDef.getLayoutHeight(12);

        //vScaleDef.selfAdjustSameScaleView(m_ivAircraft, 340, 255);
        rp = (RelativeLayout.LayoutParams) m_ivAircraft.getLayoutParams();
        rp.height = vScaleDef.getLayoutHeight(255);


        vScaleDef.setViewSize(m_ivBottom, 340, 162);
        m_ivBottom.setTranslationY(vScaleDef.getLayoutHeight(56));

        rp = (RelativeLayout.LayoutParams) m_ivQRCode.getLayoutParams();
        rp.bottomMargin = vScaleDef.getLayoutHeight(7);
        vScaleDef.selfAdjustSameScaleView(m_ivQRCode, 160, 160);

        rp = (RelativeLayout.LayoutParams) m_tvTsaPre.getLayoutParams();
        rp.height = vScaleDef.getLayoutHeight(15.7);
        rp.width = vScaleDef.getLayoutHeight(59.6);
        vScaleDef.setTextSize(13, m_tvTsaPre);

        vScaleDef.selfAdjustSameScaleView(view.findViewById(R.id.iv_vip), 16, 16);
        vScaleDef.selfAdjustSameScaleView(view.findViewById(R.id.iv_logo), 130, 20);
        vScaleDef.selfAdjustSameScaleView(view.findViewById(R.id.iv_sky), 56, 16);
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if ( !TextUtils.isEmpty(m_strQRCodeContent) ){
                    m_ivQRCode.setImageBitmap(ImageHandle.encodeToQRCode(
                            m_strQRCodeContent,
                            ViewScaleDef.getInstance(getActivity()).getLayoutHeight(177)));
                    Animation animation = AnimationUtils.loadAnimation(CIApplication.getContext(), R.anim.anim_alpha_in_1000);
                    m_ivQRCode.startAnimation(animation);
                }
            }
        }, 500);
    }

}
