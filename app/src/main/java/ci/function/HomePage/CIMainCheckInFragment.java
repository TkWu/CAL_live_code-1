package ci.function.HomePage;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import ci.function.Core.SLog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import ci.function.Base.BaseFragment;
import ci.function.Checkin.CICheckInActivity;
import ci.function.Core.CIApplication;
import ci.ui.WebView.CIWithoutInternetActivity;
import ci.ui.define.HomePage_Status;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewIdDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.item.CIHomeStatusEntity;
import ci.ui.view.ImageHandle;
import ci.ws.Models.entities.CICheckInPax_ItineraryInfoEntity;
import ci.ws.Models.entities.CITripListResp_Itinerary;

/**
 * 首頁狀態 D~H
 * Created by Ryan on 2016/2/28.
 */
public class CIMainCheckInFragment extends BaseFragment {

    private RelativeLayout m_rlayout_bg = null;
    private CIMainInfoView m_vInfoView = null;
    private CIMainBoardingPassInfoView m_vBoardingPassView = null;

    private Button m_btnCheckIn = null;

    private View m_vCheckInInfoView = null;
    private RelativeLayout m_rlayout_info_title = null;
    private RelativeLayout m_rlayout_info = null;
    private TextView m_tvLeft = null;
    private TextView m_tvCenter = null;
    private TextView m_tvRight = null;
    private TextView m_tvLeft_title = null;
    private TextView m_tvCenter_title = null;
    private TextView m_tvRight_title = null;
    private Context m_context = CIApplication.getContext();

    private static final String DESH = "-";
    private static final String NULL = "null";

    private int m_QRCodeWidth = 0;

    //private int             m_iHomeStatus   = HomePage_Status.TYPE_D_ONLINE_CHECKIN;
    private CIHomeStatusEntity m_HomeStatusEntity = null;
    private CITripListResp_Itinerary m_PNRItinerary_Info = null;
    private CICheckInPax_ItineraryInfoEntity m_CPRItineraryInfo = null;
    private Boolean m_bonCreateOK = false;

    class datetime {

        String minute;
        String hours;

        public datetime() {
            minute = DESH;
            hours = DESH;
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_main_check_in;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_rlayout_bg = (RelativeLayout) view.findViewById(R.id.rlayout_bg);
        m_vInfoView = (CIMainInfoView) view.findViewById(R.id.vInfoView);
        m_vBoardingPassView = (CIMainBoardingPassInfoView) view.findViewById(R.id.vBoardingPassView);

        m_btnCheckIn = (Button) view.findViewById(R.id.btn_checkin);
        m_btnCheckIn.setOnClickListener(m_onClick);

        m_vCheckInInfoView = view.findViewById(R.id.vCheckInInfoView);
        m_rlayout_info = (RelativeLayout) m_vCheckInInfoView.findViewById(R.id.rlayout_info);
        m_rlayout_info_title = (RelativeLayout) m_vCheckInInfoView.findViewById(R.id.rlayout_info_title);

        m_tvLeft = (TextView) m_vCheckInInfoView.findViewById(R.id.tv_Left);
        m_tvCenter = (TextView) m_vCheckInInfoView.findViewById(R.id.tv_center);
        m_tvRight = (TextView) m_vCheckInInfoView.findViewById(R.id.tv_right);

        m_tvLeft_title = (TextView) m_vCheckInInfoView.findViewById(R.id.tv_Left_title);
        m_tvCenter_title = (TextView) m_vCheckInInfoView.findViewById(R.id.tv_center_title);
        m_tvRight_title = (TextView) m_vCheckInInfoView.findViewById(R.id.tv_right_title);
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

        m_rlayout_bg.getLayoutParams().height = vScaleDef.getLayoutHeight(361.3);
        m_btnCheckIn.getLayoutParams().height = vScaleDef.getLayoutHeight(40);
        m_btnCheckIn.getLayoutParams().width = vScaleDef.getLayoutHeight(260);
        ((RelativeLayout.LayoutParams) m_btnCheckIn.getLayoutParams()).topMargin = vScaleDef.getLayoutHeight(18);
        vScaleDef.setTextSize(16, m_btnCheckIn);

        //Check in info view
        m_rlayout_info.getLayoutParams().height = vScaleDef.getLayoutHeight(28);
        ((LinearLayout.LayoutParams) m_rlayout_info.getLayoutParams()).bottomMargin = vScaleDef.getLayoutHeight(11.7);
        m_rlayout_info.setPadding(vScaleDef.getLayoutWidth(30), 0, vScaleDef.getLayoutWidth(30), 0);

        m_tvLeft.getLayoutParams().width = vScaleDef.getLayoutWidth(80);
        m_tvCenter.getLayoutParams().width = vScaleDef.getLayoutWidth(80);
        m_tvRight.getLayoutParams().width = vScaleDef.getLayoutWidth(80);
        vScaleDef.setTextSize(24, m_tvLeft);
        vScaleDef.setTextSize(24, m_tvCenter);
        vScaleDef.setTextSize(24, m_tvRight);

        m_rlayout_info_title.getLayoutParams().height = vScaleDef.getLayoutHeight(32);
        m_rlayout_info_title.setPadding(vScaleDef.getLayoutWidth(30), 0, vScaleDef.getLayoutWidth(30), 0);

        m_tvLeft_title.getLayoutParams().width = vScaleDef.getLayoutWidth(80);
        m_tvCenter_title.getLayoutParams().width = vScaleDef.getLayoutWidth(80);
        m_tvRight_title.getLayoutParams().width = vScaleDef.getLayoutWidth(80);
        vScaleDef.setTextSize(13, m_tvLeft_title);
        vScaleDef.setTextSize(13, m_tvCenter_title);
        vScaleDef.setTextSize(13, m_tvRight_title);

        m_QRCodeWidth = vScaleDef.getLayoutMinUnit(156.7);

        m_bonCreateOK = true;
    }

    @Override
    protected void setOnParameterAndListener(View view) {

        HomePageStatusUpdate();
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {
    }

    @Override
    public void onLanguageChangeUpdateUI() {
    }

    protected void onFragmentResume() {

        HomePageStatusUpdate();
    }

    public void setHomePageStatus(CIHomeStatusEntity StatusEntity) {
//        Boolean bHaveItinerary = false;
        m_HomeStatusEntity = StatusEntity;
        //2016-06-29 ryan for 直接抓取外面過濾好的CPR以及PNR資料,
        if (null != StatusEntity) {
            m_PNRItinerary_Info = StatusEntity.Itinerary_Info;
            m_CPRItineraryInfo = StatusEntity.CPR_Info;
        } else {
            m_PNRItinerary_Info = null;
            m_CPRItineraryInfo = null;
        }

        if (m_bonCreateOK) {
            HomePageStatusUpdate();
        }
    }


    private String getFilteredText(String originText, String newText) {
        return !TextUtils.isEmpty(originText)
                && !TextUtils.equals(originText, NULL)
                ? originText : newText;
    }

    public void HomePageStatusUpdate() {
        if (null == m_PNRItinerary_Info) {
            m_PNRItinerary_Info = new CITripListResp_Itinerary();
        }
        if (null == m_HomeStatusEntity) {
            m_HomeStatusEntity = new CIHomeStatusEntity();
        }
        if (null == m_CPRItineraryInfo) {
            m_CPRItineraryInfo = new CICheckInPax_ItineraryInfoEntity();
        }
        if (null != m_vBoardingPassView) {
            m_vBoardingPassView.setListener(new CIMainBoardingPassInfoView.listener() {
                public CIHomeStatusEntity onInquiryBoardingPassData() {
                    return m_HomeStatusEntity;
                }
            });
        }
        String strDepartureTermial = getFilteredText(m_PNRItinerary_Info.Departure_Terminal, DESH);
        String strArrivalTermial = getFilteredText(m_PNRItinerary_Info.Arrival_Terminal, DESH);
        String strCheckInCounter = getFilteredText(m_CPRItineraryInfo.Check_In_Counter, DESH);
        String strSeatNumber = getFilteredText(m_CPRItineraryInfo.Seat_Number, DESH);
        String strBoardingPass = getFilteredText(m_CPRItineraryInfo.Boarding_Pass, null);
        String strBoardingGate = getFilteredText(m_CPRItineraryInfo.Boarding_Gate, DESH);
        String strBoardingTime = getFilteredText(m_CPRItineraryInfo.Boarding_Time, DESH);
        String strBoardingDate = getFilteredText(m_CPRItineraryInfo.Boarding_Date, "").replace(DESH, "/");//TODO 登機日期要用嗎？
        String strStationName = getFilteredText(m_PNRItinerary_Info.Arrival_Station_Name, DESH);
        Bitmap bmpQRCode = ImageHandle.encodeToQRCode(strBoardingPass, m_QRCodeWidth);
        //2017-03-09 Add by Ryan for 增加行李轉盤的資訊
        String strCarousel = getFilteredText( m_CPRItineraryInfo.Carousel, DESH);


        ViewScaleDef vScaleDef = ViewScaleDef.getInstance(getActivity());
        //預設畫面畫面顯示欄位, 為三個欄位
        m_tvCenter.setVisibility(View.VISIBLE);
        m_tvCenter_title.setVisibility(View.VISIBLE);
        int iPadding = vScaleDef.getLayoutWidth(30);
        m_rlayout_info_title.setPadding(iPadding, 0, iPadding, 0);
        m_rlayout_info.setPadding(iPadding, 0, iPadding, 0);
        //
        if (m_HomeStatusEntity.iStatus_Code == HomePage_Status.TYPE_D_ONLINE_CHECKIN) {

            m_vInfoView.setVisibility(View.VISIBLE);
            m_vBoardingPassView.setVisibility(View.GONE);

            m_vInfoView.setIcon(R.drawable.ic_homepage_d);
            m_vInfoView.setTitleText(m_context.getString(R.string.online_check_in_is_now_open));
            m_btnCheckIn.setText(R.string.check_in);
            m_btnCheckIn.setVisibility(View.VISIBLE);

            m_tvLeft_title.setText(R.string.home_terminal);
            m_tvLeft.setText(strDepartureTermial);
            m_tvCenter_title.setText(R.string.check_in_counter);
            m_tvCenter.setText(strCheckInCounter);
            m_tvRight_title.setText(R.string.seat);
            m_tvRight.setText(strSeatNumber);

        } else if (m_HomeStatusEntity.iStatus_Code == HomePage_Status.TYPE_D_ONLINE_CHECKIN_FINISH) {

            m_vInfoView.setVisibility(View.GONE);
            m_btnCheckIn.setVisibility(View.GONE);

            m_vBoardingPassView.setVisibility(View.VISIBLE);
            m_vBoardingPassView.SetIconOnClick();
            m_vBoardingPassView.setTitleText(m_context.getString(R.string.boarding_pass));
            m_vBoardingPassView.setIcon(bmpQRCode);
            m_vBoardingPassView.setContentText(String.format(m_context.getString(R.string.please_arrive_at_the_airport_before), getTimeForCheckInDesk()));

            m_tvLeft_title.setText(R.string.check_in_counter);
            m_tvLeft.setText(strCheckInCounter);
            m_tvCenter_title.setText(R.string.gate);
            m_tvCenter.setText(strBoardingGate);
            m_tvRight_title.setText(R.string.seat);
            m_tvRight.setText(strSeatNumber);

        } else if (m_HomeStatusEntity.iStatus_Code == HomePage_Status.TYPE_D_ONLINE_CHECKIN_FINISH_AT_AIRPORT_1H5) {

            m_vInfoView.setVisibility(View.GONE);
            m_btnCheckIn.setVisibility(View.GONE);

            m_vBoardingPassView.setVisibility(View.VISIBLE);
            m_vBoardingPassView.SetIconOnClick();
            m_vBoardingPassView.setTitleText(m_context.getString(R.string.boarding_pass));
            m_vBoardingPassView.setIcon(bmpQRCode);
            m_vBoardingPassView.setContentText(String.format(m_context.getString(R.string.boarding_start_at_gate_xx), strBoardingTime, strBoardingGate));

            m_tvLeft_title.setText(R.string.boarding);
            m_tvLeft.setText(strBoardingTime);
            m_tvCenter_title.setText(R.string.gate);
            m_tvCenter.setText(strBoardingGate);
            m_tvRight_title.setText(R.string.seat);
            m_tvRight.setText(strSeatNumber);

        } else if (m_HomeStatusEntity.iStatus_Code == HomePage_Status.TYPE_D_ONLINE_CHECKIN_FINISH_BOARDING_PASS_NOT_AVAILABLE) {

            m_vInfoView.setVisibility(View.VISIBLE);
            m_vBoardingPassView.setVisibility(View.GONE);

            m_vInfoView.setIcon(R.drawable.ic_homepage_e_2);
            m_vInfoView.setTitleText(String.format(m_context.getString(R.string.collect_boardingpass_at_airport_before), getTimeForCheckInDesk()));
            m_btnCheckIn.setVisibility(View.GONE);

            m_tvLeft_title.setText(R.string.home_terminal);
            m_tvLeft.setText(strDepartureTermial);
            m_tvCenter_title.setText(R.string.check_in_counter);
            m_tvCenter.setText(strCheckInCounter);
            m_tvRight_title.setText(R.string.seat);
            m_tvRight.setText(strSeatNumber);


        } else if (m_HomeStatusEntity.iStatus_Code == HomePage_Status.TYPE_E_DESK_CHECKIN) {

            m_vInfoView.setVisibility(View.VISIBLE);
            m_vBoardingPassView.setVisibility(View.GONE);

            m_vInfoView.setIcon(R.drawable.ic_homepage_e_2);
            m_vInfoView.setTitleText(String.format(m_context.getString(R.string.please_arrive_at_the_airport_before), getTimeForCheckInDesk()));
            m_btnCheckIn.setVisibility(View.GONE);

            m_tvLeft_title.setText(R.string.home_terminal);
            m_tvLeft.setText(strDepartureTermial);
            m_tvCenter_title.setText(R.string.check_in_counter);
            m_tvCenter.setText(strCheckInCounter);
            m_tvRight_title.setText(R.string.seat);
            m_tvRight.setText(strSeatNumber);

        } else if (m_HomeStatusEntity.iStatus_Code == HomePage_Status.TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5) {

            m_vInfoView.setVisibility(View.VISIBLE);
            m_vBoardingPassView.setVisibility(View.GONE);

            m_vInfoView.setIcon(R.drawable.ic_homepage_e);
            m_vInfoView.setTitleText(String.format(m_context.getString(R.string.boarding_start_at_gate_xx), strBoardingTime, strBoardingGate));
            m_btnCheckIn.setVisibility(View.GONE);

            m_tvLeft_title.setText(R.string.boarding);
            m_tvLeft.setText(strBoardingTime);
            m_tvCenter_title.setText(R.string.gate);
            m_tvCenter.setText(strBoardingGate);
            m_tvRight_title.setText(R.string.seat);
            m_tvRight.setText(strSeatNumber);

        } else if (m_HomeStatusEntity.iStatus_Code == HomePage_Status.TYPE_F_IN_FLIGHT) {

            m_vInfoView.setVisibility(View.VISIBLE);
            m_vBoardingPassView.setVisibility(View.GONE);

            m_vInfoView.setIcon(R.drawable.ic_homepage_f);
            //調整顯示時間
            //
            datetime dt = getRemainingArriveTime();
            String strdt = "";
            if (false == TextUtils.equals(DESH, dt.hours)) {
                strdt += String.format("%s %s ", dt.hours, m_context.getString(R.string.hours));
            }
            if (false == TextUtils.equals(DESH, dt.minute)) {
                strdt += String.format("%s %s", dt.minute, m_context.getString(R.string.minute));
            }
            if (TextUtils.isEmpty(strdt)) {
                strdt = DESH;
                //2017-04-13 Modify by Ryan for 因應華航需求，修正時間顯示字串，不顯示0分鐘，改為已抵達
            } else if ( TextUtils.equals("0",dt.minute) && TextUtils.equals(DESH, dt.hours) ){
                strdt = m_context.getString(R.string.flight_status_arrived_on_time);
                //
            }
            //
            m_vInfoView.setTitleText(String.format(m_context.getString(R.string.time_to_destination_at_xx), strdt));
            m_btnCheckIn.setVisibility(View.GONE);

            m_tvLeft_title.setText(R.string.boarding);
            m_tvLeft.setText(strBoardingTime);
            m_tvCenter_title.setText(R.string.gate);
            m_tvCenter.setText(strBoardingGate);
            m_tvRight_title.setText(R.string.seat);
            m_tvRight.setText(strSeatNumber);

        } else if (m_HomeStatusEntity.iStatus_Code == HomePage_Status.TYPE_G_TRANSITION) {

            m_vInfoView.setVisibility(View.VISIBLE);
            m_vBoardingPassView.setVisibility(View.GONE);

            m_vInfoView.setIcon(R.drawable.ic_homepage_g);
            m_vInfoView.setTitleText(String.format(m_context.getString(R.string.transition_infomation),
                    strBoardingTime, strDepartureTermial, strBoardingGate));
            m_btnCheckIn.setVisibility(View.GONE);

            m_tvLeft_title.setText(R.string.home_terminal);
            m_tvLeft.setText(strDepartureTermial);
            m_tvCenter_title.setText(R.string.check_in_counter);
            m_tvCenter.setText(strCheckInCounter);
            m_tvRight_title.setText(R.string.seat);
            m_tvRight.setText(strSeatNumber);

        } else if (m_HomeStatusEntity.iStatus_Code == HomePage_Status.TYPE_H_ARRIVAL) {

            m_vInfoView.setVisibility(View.VISIBLE);
            m_vBoardingPassView.setVisibility(View.GONE);

            m_vInfoView.setIcon(R.drawable.ic_homepage_h);
            m_vInfoView.setTitleText(String.format(m_context.getString(R.string.welcome_xxx_baggage_at_xxx), strStationName, strCarousel));
            m_btnCheckIn.setVisibility(View.GONE);

            m_tvLeft_title.setText(R.string.home_terminal);
            m_tvLeft.setText(strArrivalTermial);
            m_tvRight_title.setText(R.string.baggage_carousel);
            m_tvRight.setText(strCarousel);

            //顯示欄位資訊不同另外處理
            m_tvCenter.setVisibility(View.GONE);
            m_tvCenter_title.setVisibility(View.GONE);
            iPadding = vScaleDef.getLayoutWidth(80);
            m_rlayout_info_title.setPadding(iPadding, 0, iPadding, 0);
            m_rlayout_info.setPadding(iPadding, 0, iPadding, 0);

        }
    }

    View.OnClickListener m_onClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (v.getId() == m_btnCheckIn.getId()) {
                if (true == m_HomeStatusEntity.bIsNewSiteOnlineCheckIn) {
                    Intent intent = new Intent();
                    intent.putExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, ViewIdDef.VIEW_ID_HOME);
                    intent.putExtra(UiMessageDef.BUNDLE_CHECK_IN_FLIGHT_LIST, m_HomeStatusEntity.CheckInResp);
                    intent.setClass(getActivity(), CICheckInActivity.class);
                    getActivity().startActivityForResult(intent, UiMessageDef.REQUEST_CODE_CHECK_IN);
                    getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
                } else {
                    Intent data = new Intent();
                    data.putExtra(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG, m_context.getString(R.string.check_in));
                    data.setClass(getActivity(), CIWithoutInternetActivity.class);
                    //2016-06-22, 調整作法,將參數調整為由外部帶入,
                    String strURL = "";
                    if (null != m_HomeStatusEntity) {
                        strURL = getWebOnlineCheckInUrl(m_HomeStatusEntity.PNR_Id, m_HomeStatusEntity.strFirst_name, m_HomeStatusEntity.strLast_name);
                    }
                    data.putExtra(UiMessageDef.BUNDLE_WEBVIEW_URL_TAG, strURL);
                    startActivity(data);
                    getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
                }
            }
        }
    };

    //2016-06-22 modifly by ryan for 調整作法,將參數調整為由外部帶入, 方便其它頁面呼叫
    public static String getWebOnlineCheckInUrl(String PNR_Id, String strFirst_name, String strLast_name) {
        /**
         * 舊站Check-in url 如下
         * 繁、簡：
         * https://calec.china-airlines.com/echeckin_t/echeckin_c.asp?q_pnr=pnrloc&q_name1=lastname&q_name2=firstname&q_type=home
         * 英、日：
         * https://calec.china-airlines.com/echeckin_t/echeckin_e.asp?q_pnr=pnrloc&q_name1=lastname&q_name2=firstname&q_type=home
         * */
        Map<String, String> m_mapParams = new LinkedHashMap<>();

        String url = "https://calec.china-airlines.com/echeckin_t/echeckin_";
        String lang = "e.asp?";

        Locale locale = CIApplication.getLanguageInfo().getLanguage_Locale();
        switch (locale.toString()) {
            case "zh_TW":
            case "zh_CN":
                lang = "c.asp?";
                break;
            case "en":
            case "ja_JP":
                lang = "e.asp?";
                break;
        }

        m_mapParams.put("q_pnr", PNR_Id);
        m_mapParams.put("q_name1", strLast_name);
        m_mapParams.put("q_name2", strFirst_name);

        m_mapParams.put("q_type", "home");

        url = url + lang;

        Iterator<Map.Entry<String, String>> iter = m_mapParams.entrySet().iterator();
        int count = 0;
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            String key = entry.getKey();
            String val = entry.getValue();
            if (0 == count) {
                url += key + "=" + val;
            } else {
                url += "&" + key + "=" + val;
            }
            count++;
        }
       SLog.i("web online check in url", url);
        return url;
    }

    /**
     * 取得臨櫃報到時間，需要再出發時間前二個小時
     *
     * @return 字串
     */
    private String getTimeForCheckInDesk() {
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getInstance();
        SimpleDateFormat sdfTime = (SimpleDateFormat) SimpleDateFormat.getInstance();
        sdf.applyPattern("yyyy-MM-dd HH:mm");
        sdfTime.applyPattern("MM-dd HH:mm");
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));

        //2016-11-24 Modify by Ryan for 時間計算調整為直接抓取Display Local Date Time, 不抓取GMT時間, 避免因為手機時區導致時間顯示錯誤
        //2016-11-15 Modify by Ryan for 調整PNR時間邏輯, 不再抓取CPR的時間來更新, 調整為抓取DISPLAY TAG
        if (null != m_PNRItinerary_Info
                && !TextUtils.isEmpty(m_PNRItinerary_Info.getDisplayDepartureDate())
                && !TextUtils.isEmpty(m_PNRItinerary_Info.getDisplayDepartureTime())) {
            String strDepartureDate = m_PNRItinerary_Info.getDisplayDepartureDate() + " " + m_PNRItinerary_Info.getDisplayDepartureTime();
            try {
                //2016-11-24 Modify by Ryan for 直接將時間轉成 milliseconds 扣掉兩小時 (7200 *1000)
                Date dateDeparture = sdf.parse(strDepartureDate);
                long lDeparture = dateDeparture.getTime() - 7200000;
                //將算完的時間,轉回ui顯示的格式
                Date date_Checkin = new Date();
                date_Checkin.setTime(lDeparture);
                return sdfTime.format(date_Checkin);

                //Calendar calendar = Calendar.getInstance();
                //calendar.setTime(sdf.parse(strDepartureDate));
                //2016-08-03 修正為提前兩小時
                //calendar.add(Calendar.HOUR,-2);
                //return sdfTime.format(calendar.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
                return DESH;
            }
        } else {
            return DESH;
        }
    }

    /**
     * 取得飛行剩餘時間
     * //2016-06-20 modifly by ryan for 調整顯示時間為 小時:分鐘
     *
     * @return datetime
     */
    private datetime getRemainingArriveTime() {

        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getInstance();
        sdf.applyPattern("yyyy-MM-dd HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));

        datetime dt = new datetime();
        //2016-11-15 Modify by Ryan for 調整PNR時間邏輯, 不再抓取CPR的時間來更新, 調整為抓取DISPLAY TAG
        if (null != m_PNRItinerary_Info
                && !TextUtils.isEmpty(m_PNRItinerary_Info.getDisplayArrivalDate_GMT())
                && !TextUtils.isEmpty(m_PNRItinerary_Info.getDisplayArrivalTime_GMT())) {
            String arrivalDate = m_PNRItinerary_Info.getDisplayArrivalDate_GMT() + " " + m_PNRItinerary_Info.getDisplayArrivalTime_GMT();
            Date nowDate = new Date(System.currentTimeMillis());
            Date arrivaDate = null;
            try {
                arrivaDate = sdf.parse(arrivalDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long remainingTime = 0;
            long remainingMins = 0;
            long remaininghours = 0;
            if (null != nowDate && null != arrivaDate) {
                remainingTime = arrivaDate.getTime() - nowDate.getTime();
                //增加 小時時間的計算
                remaininghours = remainingTime / (1000 * 60 * 60);
                remainingMins = (remainingTime / (1000 * 60)) - (remaininghours * 60);
            } else {
                return dt;
            }
            //return String.valueOf(remainingMins);
            if (remaininghours > 0) {
                dt.hours = String.valueOf(remaininghours);
            }
            if (remainingMins > 0) {
                dt.minute = String.valueOf(remainingMins);
            } else {
                dt.minute = "0";
            }
            return dt;
        } else {
            return dt;
        }

    }
}
