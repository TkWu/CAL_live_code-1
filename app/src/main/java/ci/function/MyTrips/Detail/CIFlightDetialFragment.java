package ci.function.MyTrips.Detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.AppInfo;
import ci.ui.object.CIFlightStatusManager;
import ci.ws.GoogleApi.URLShortenerApi;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Presenter.CIInquiryFlightStationPresenter;
import ci.ws.Presenter.Listener.CIInquiryFlightStatusStationListener;

/**
 * Created by kevin on 2016/3/3.
 */
public class CIFlightDetialFragment extends BaseFragment
    implements View.OnClickListener{

    private TextView
            m_tvFlightStatus               = null,
            m_tvDepartureLocation          = null,
            m_tvArriveLocation             = null,
            m_tvDepartureTime              = null,
            m_tvArriveTime                 = null,
            m_tvDepartureDate              = null,
            m_tvArriveDate                 = null,
            m_tvDepartureabbr              = null,
            m_tvArriveabbr                 = null,
            m_tvDepartureLocationDetial    = null,
            m_tvArriveLocationDetial       = null,
            m_tvDepartureTerminal          = null,
            m_tvArriveTerminal             = null,
            m_tvAirlinesValue              = null,
            m_tvFlightNumberValue          = null,
            m_tvTravelTimeValue            = null,
            m_tvReservationNo = null;
    private ImageView m_ivFlightFromTo = null;
    private ImageView m_ivFlightStatus = null;
    private RelativeLayout m_rlFlightStatus = null;
    private RelativeLayout m_rlShare        = null;
    private CITripListResp_Itinerary m_data         = null;
    private CIInquiryFlightStationPresenter m_presenter = null;
    private final String    CI_SHARE_URL     = "https://www.china-airlines.com/";
    private final String    CI_SHARE_URL_PARAMS  = "fly/flight-status/flight-status-result?";
    private final String    WS_DATE_FORMAT   = "yyyy-MM-dd";
    private final String    SHARE_DATE_FORMAT= "yyyMMdd";
    private final String    NULL= "null";
    private final String    DESH= "-";

    private URLShortenerApi m_urlshorener = null;

    //start of 644336 2019 2月3月 AI/行事曆/截圖/注意事項
    private CIAlertDialog m_dialog = null;
    //end  of 644336 2019 2月3月 AI/行事曆/截圖/注意事項

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        m_data = (CITripListResp_Itinerary)getArguments().getSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST);
        super.onCreate(savedInstanceState);
    }

    /**
     * BaseFragment在
     * {@link BaseFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)
     * onCreateView()}時 設定{@link LayoutInflater#inflate(int, ViewGroup, boolean)
     * inflate()}用
     *
     * @return 此畫面的 Layout Resource Id
     */
    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_flight_tab_flight_detial;
    }

    /**
     * 元件初始化，靜態取得元件實體、動態建製元件實體…等
     *
     * @param inflater
     * @param view
     */
    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_rlFlightStatus            = (RelativeLayout)view.findViewById(R.id.rl_flight_status_click);
        m_ivFlightStatus            = (ImageView)view.findViewById(R.id.iv_on_time);
        m_tvFlightStatus            = (TextView) view.findViewById(R.id.tv_flight_status);
        m_rlShare                   = (RelativeLayout)view.findViewById(R.id.rl_share_click);
        m_tvDepartureLocation       = (TextView) view.findViewById(R.id.tv_departure_location);
        m_tvArriveLocation          = (TextView) view.findViewById(R.id.tv_arrive_location);
        m_tvDepartureTime           = (TextView) view.findViewById(R.id.tv_departure_time);
        m_tvArriveTime              = (TextView) view.findViewById(R.id.tv_arrive_time);
        m_tvDepartureDate           = (TextView) view.findViewById(R.id.tv_departure_date);
        m_tvArriveDate              = (TextView) view.findViewById(R.id.tv_arrive_date);
        m_tvDepartureabbr           = (TextView) view.findViewById(R.id.tv_departure_abbreviation);
        m_tvArriveabbr              = (TextView) view.findViewById(R.id.tv_arrive_abbreviation);
        m_tvDepartureLocationDetial = (TextView) view.findViewById(R.id.tv_departure_location_detial);
        m_tvArriveLocationDetial    = (TextView) view.findViewById(R.id.tv_arrive_location_detial);
        m_tvDepartureTerminal       = (TextView)view.findViewById(R.id.tv_departure_terminal);
        m_tvArriveTerminal          = (TextView)view.findViewById(R.id.tv_arrive_terminal);
        m_tvAirlinesValue           = (TextView)view.findViewById(R.id.tv_flight_detial_airlines_value);
        m_tvFlightNumberValue       = (TextView)view.findViewById(R.id.tv_flight_detial_flight_number_value);
        m_tvTravelTimeValue         = (TextView)view.findViewById(R.id.tv_flight_detial_travel_time_value);
        m_tvReservationNo           = (TextView)view.findViewById(R.id.tv_flight_detial_reservation_no_value);
        m_ivFlightFromTo            = (ImageView)view.findViewById(R.id.iv_ic_flight_from_to);

// 2016-08-01 統一時間格式邏輯
//        String uiDateFormat = "";
//
//        Locale locale = CIApplication.getLanguageInfo().getLanguage_Locale();
//        switch (locale.toString()){
//            case "zh_TW":
//            case "zh_CN":
//            case "ja_JP":
//                uiDateFormat = UI_DATE_FORMAT_2;
//                break;
//            case "en":
//                uiDateFormat = UI_DATE_FORMAT_1;
//                break;
//        }

        if(null != m_data){
            m_tvDepartureLocation.setText(m_data.Departure_Station_Name);
            m_tvArriveLocation.setText(m_data.Arrival_Station_Name);
            //2016-10-12 調整時間的顯示邏輯, by ryan
            //統一為 先檢查 Display 的欄位, 有資料就顯示, 無資料就抓取原來的 表定時間
            m_tvDepartureTime.setText( m_data.getDisplayDepartureTime() );
            m_tvArriveTime.setText( m_data.getDisplayArrivalTime() );
            //m_tvDepartureTime.setText(m_data.Departure_Time);
            //m_tvArriveTime.setText(m_data.Arrival_Time);
            //統一日期顯示格式
            m_tvDepartureDate.setText( AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddEEE( m_data.getDisplayDepartureDate() ) );
            m_tvArriveDate.setText( AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddEEE( m_data.getDisplayArrivalDate() ));
            //m_tvDepartureDate.setText( convertDateFormat(WS_DATE_FORMAT, uiDateFormat, m_data.Departure_Date) );
            //m_tvArriveDate.setText(convertDateFormat(WS_DATE_FORMAT, uiDateFormat, m_data.Arrival_Date));
            m_tvDepartureabbr.setText(m_data.Departure_Station);
            m_tvArriveabbr.setText(m_data.Arrival_Station);

            //改使用 CIFlightStatusManager 管理顯示狀態的邏輯及設定icon
            CIFlightStatusManager.transferIconAndTextByColorCode(
                    m_ivFlightStatus,
                    m_tvFlightStatus,
                    m_data.Color_Code,
                    m_data.Flight_Status);

            if(!TextUtils.isEmpty(m_data.Departure_Terminal) && !m_data.Departure_Terminal.equals(NULL)){
                String departureTerminal = String.format(getString(R.string.terminal),m_data.Departure_Terminal);
                m_tvDepartureTerminal.setText(departureTerminal);
            }

            if(!TextUtils.isEmpty(m_data.Arrival_Terminal) && !m_data.Arrival_Terminal.equals(NULL)){
                String arriveTerminal = String.format(getString(R.string.terminal),m_data.Arrival_Terminal);
                m_tvArriveTerminal.setText(arriveTerminal);
            }

            //換算取得的飛行時間為時與分
            m_tvTravelTimeValue.setText(getFlightTotalTime(m_data));

            m_tvAirlinesValue.setText(m_data.Airlines_Name);
            m_tvFlightNumberValue.setText(m_data.Airlines + " " + m_data.Flight_Number);
            m_tvReservationNo.setText(m_data.Pnr_Id);

            setupPresenter();
            if(null == m_presenter.getAllDepatureStationList()){
                m_tvDepartureLocationDetial.setText(DESH);
                m_tvArriveLocationDetial.setText(DESH);
                //如果總站沒有資料，就向ws要總站資料 by kevin
                m_presenter.initAllStationDB();
                m_presenter.InquiryAllStationListFromWS();
            } else {
                setDetialAirportTextView();
            }

        }
    }

    /**
     * 取得飛行總時間
     * @return String
     */
    private String getFlightTotalTime(CITripListResp_Itinerary data){

        SimpleDateFormat sdf = (SimpleDateFormat)SimpleDateFormat.getInstance();
        sdf.applyPattern("yyyy-MM-dd HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));

        //2016-11-18 調整時間的顯示邏輯, by ryan
        //統一為 先檢查 Display 的欄位, 有資料就顯示, 無資料就抓取原來的 表定時間
        if(null != data
                && !TextUtils.isEmpty(data.getDisplayDepartureDate_GMT())
                && !TextUtils.isEmpty(data.getDisplayDepartureTime_GMT())
                && !TextUtils.isEmpty(data.getDisplayArrivalDate_GMT())
                && !TextUtils.isEmpty(data.getDisplayArrivalTime_GMT())){
            //
            String strDepartureDate    = data.getDisplayDepartureDate_GMT() + " " + data.getDisplayDepartureTime_GMT();
            String strArrivalDate      = data.getDisplayArrivalDate_GMT() + " " + data.getDisplayArrivalTime_GMT();
            //String strDepartureDate    = data.Departure_Date_Gmt + " " + data.Departure_Time_Gmt;
            //String strArrivalDate      = data.Arrival_Date_Gmt + " " + data.Arrival_Time_Gmt;

            Date departureDate  = null;
            Date arrivaDate     = null;
            try {
                departureDate   = sdf.parse(strDepartureDate);
                arrivaDate      = sdf.parse(strArrivalDate);
            } catch (ParseException e) {
                e.printStackTrace();
                return DESH;
            }
            long totalTime  = 0;
            long mins       = 0;
            long hours      = 0;
            if(null != departureDate && null != arrivaDate){
                totalTime  = arrivaDate.getTime() - departureDate.getTime();
                //增加 小時時間的計算
                hours = totalTime / (1000 * 60 * 60);
                mins  = (totalTime / (1000 * 60)) - ( hours * 60 );
            } else {
                return DESH;
            }

            String strHour      = "";
            String strMinute    = "";
            //經過計算取得的小時數大於0才顯示
            if(hours > 0){
                strHour = String.format(getString(R.string.hours_unit), hours);
            }
            //經過計算取得的分鐘數大於等於0都顯示
            if(mins >= 0){
                strMinute = String.format(getString(R.string.minute_unit), mins);
            }

            return strHour + " " + strMinute;
        } else {
            return DESH;
        }

    }

    private void setDetialAirportTextView(){
        if(null == m_data){
            return;
        }
        String departureIATA =  m_data.Departure_Station;
        if(!TextUtils.isEmpty(m_data.Departure_Station)){
            CIFlightStationEntity data = m_presenter.getStationInfoByIATA(departureIATA);
            if(null != data && !TextUtils.isEmpty(data.airport_name) ){
//                    && !data.airport_name.equals(NULL)){  //isEmpty 會判斷null 以及 length <=0
                m_tvDepartureLocationDetial.setText(data.airport_name);
            } else {
                m_tvDepartureLocationDetial.setText(DESH);
            }
            //出發地名稱也須抓取StationTable 2016-09-30
            if ( null != data && !TextUtils.isEmpty(data.localization_name) ){
                m_tvDepartureLocation.setText(data.localization_name);
            } else {
                m_tvDepartureLocation.setText(DESH);
            }
        }

        String arrivalIATA =  m_data.Arrival_Station;
        if(!TextUtils.isEmpty(m_data.Departure_Station)){
            CIFlightStationEntity data = m_presenter.getStationInfoByIATA(arrivalIATA);
            if(null != data && !TextUtils.isEmpty(data.airport_name) ){
//                    && !data.airport_name.equals(NULL)){ //isEmpty 會判斷null 以及 length <=0
                m_tvArriveLocationDetial.setText(data.airport_name);
            } else {
                m_tvArriveLocationDetial.setText(DESH);
            }
            //抵達地名稱也須抓取StationTable 2016-09-30
            if ( null != data && !TextUtils.isEmpty(data.localization_name) ){
                m_tvArriveLocation.setText(data.localization_name);
            } else {
                m_tvArriveLocation.setText(DESH);
            }
        }
    }

    private void setupPresenter(){
        m_presenter = CIInquiryFlightStationPresenter.getInstance(new CIInquiryFlightStatusStationListener() {
            @Override
            public void onAllStationSuccess(String rt_code, String rt_msg, CIInquiryFlightStationPresenter presenter) {
                setDetialAirportTextView();
            }

            @Override
            public void onStationError(String rt_code, String rt_msg) {}

            @Override
            public void onODStationSuccess(String rt_code, String rt_msg, CIInquiryFlightStationPresenter presenter) {}

            @Override
            public void showProgress() {}

            @Override
            public void hideProgress() {}
        },CIInquiryFlightStationPresenter.ESource.TimeTable);
    }

    /**
     * 轉換日期文字
     * @param template_a 轉換前的日期格式
     * @param template_b 轉換後的日期格式
     * @param date       轉換前的日期文字
     * @return           轉換後的日期文字
     */
    private String convertDateFormat(String template_a , String template_b, String date) {
        // 定義時間格式
        SimpleDateFormat sdfA = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
        SimpleDateFormat sdfB = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
        sdfA.applyPattern(template_a);
        sdfB.applyPattern(template_b);
        try {
            return sdfB.format(sdfA.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 設定字型大小及版面大小
     *
     * @param view
     * @param vScaleDef 請參閱{@link ViewScaleDef}
     */
    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(m_ivFlightFromTo, 30, 30);
        vScaleDef.selfAdjustSameScaleView(m_ivFlightStatus,24,24);
    }

    /**
     * 通知父BaseActivity對子BaseFragment設定客製的「OnParameter(參數讀取)」跟「OnListener(
     * 動線通知)」介面
     *
     * @param view
     */
    @Override
    protected void setOnParameterAndListener(View view) {
        //CR 2017-10-12 by Kevin
        //trip detail 裡 航班那頁有個航班動態，如航班不是CI / AE 的不能進入航班動態，同樣不進行分享顯示的wroding 再提供
        if(!TextUtils.isEmpty(m_data.Airlines) &&
                (m_data.Airlines.toUpperCase().equals("AE") ||
                        m_data.Airlines.toUpperCase().equals("CI"))) {

            m_rlFlightStatus.setOnClickListener(this);
            m_rlShare.setOnClickListener(this);

            //2016-07-26 add by ryan for 新增短網址功能
            m_urlshorener = new URLShortenerApi();
            m_urlshorener.setListener(m_urlshortListener);
        } else {
            m_rlFlightStatus.setOnClickListener(onClickListenerForNonCIAndAE);
            m_rlShare.setOnClickListener(onClickListenerForNonCIAndAE);
        }
    }

    View.OnClickListener onClickListenerForNonCIAndAE = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            showDialog(getString(R.string.warning),getString(R.string.non_ci_and_ae_alert_dialog));
        }
    };

    /**
     * 依需求調用以下函式
     *
     * @param fragmentManager
     * @see FragmentTransaction FragmentTransaction相關操作
     */
    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

    }

    /**
     * 若收到Handle Message且BaseActivity不認得時，
     * 視為子class自訂Message，可經由此Function接收通知。
     *
     * @param msg
     * @return true：代表此Message已處理<p> false：代表此Message連子class也不認得<p>
     */
    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    /**
     * 若子class有自訂Message，請經由此Function清空Message。
     */
    @Override
    protected void removeOtherHandleMessage() {

    }

    /**
     * 當App語言變更後, 會呼叫此介面，藉此更新畫面UI,需要重新呼叫setText
     */
    @Override
    public void onLanguageChangeUpdateUI() {

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.rl_share_click:

                //shareMessage(getShareUrl());
                //2016-07-26 調整為分享網址為短網址
                m_urlshorener.getShoetenerURL(getShareUrl());
                break;
            case R.id.rl_flight_status_click:
                //start of 644336 2019 2月3月 AI/行事曆/截圖/注意事項
//                Intent data = new Intent();
//                data.putExtras(getArguments());
//                getActivity().setResult(UiMessageDef.RESULT_CODE_GO_FLIGHT_STATUS, data);
//                getActivity().finish();
//                getActivity().overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);

                showAlertDialogForAddCalendarEvent();
                //end start of 644336 2019 2月3月 AI/行事曆/截圖/注意事項
                break;
        }
    }

    private void shareMessage(String url){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, url);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    /**
     * 取得要分享飛航狀態的連結
     * @return String 網址
     * 官方提供 https://www.china-airlines.com/tw/zh/fly/flight-status/flight-status-result?FromAirportCode=TPE&ToAirportCode=AMS&dateType=Departure&FlightDate=20160414&FormType=ROUTE&SearchByDepartureOrArrival=Departure
     * 分享連結 https://www.china-airlines.com/tw/zh/fly/flight-status/flight-status-result?FromAirportCode=TPE&ToAirportCode=FUK&dateType=Departure&FlightDate=20151017&FormType=ROUTE&SearchByDepartureOrArrival=Departure
     */
    private String getShareUrl(){
        //2016-11-18 分享的連結參數時間不需要調整,
        Map<String, String> params = new LinkedHashMap<>();
        params.put("FromAirportCode", m_data.Departure_Station);
        params.put("ToAirportCode", m_data.Arrival_Station);
        params.put("dateType", "Departure");
        params.put("FlightDate", convertDateFormat(WS_DATE_FORMAT, SHARE_DATE_FORMAT, m_data.Departure_Date));
        params.put("FormType", "ROUTE");
        params.put("SearchByDepartureOrArrival", "Departure");
        String lang = "tw/zh/";
        Locale locale = CIApplication.getLanguageInfo().getLanguage_Locale();
        switch (locale.toString()){
            case "zh_TW":
                lang = "tw/zh/";
                break;
            case "zh_CN":
                lang = "cn/zh/";
                break;
            case "en":
                lang = "us/en/";
                break;
            case "ja_JP":
                lang = "jp/jp/";
                break;
        }

        String url = CI_SHARE_URL + lang + CI_SHARE_URL_PARAMS;
        Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();
        int count = 0;
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            String key = entry.getKey();
            String val = entry.getValue();
            if(0 == count){
                url+= key+"="+val;
            } else {
                url+= "&"+key+"="+val;
            }
            count++;
        }
        return url;
    }

    URLShortenerApi.URLShortenerListener m_urlshortListener = new URLShortenerApi.URLShortenerListener() {
        @Override
        public void Shortenerfinish(String rt_msg, String strShortURL) {
           SLog.v("CAL", "URL shortener " +rt_msg);
            shareMessage( getActivity().getString(R.string.trips_detail_share_info_description) + "\n" + strShortURL );
        }

        @Override
        public void showProgress() {
            showProgressDialog();
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
        }
    };

    //start of 644336 2019 2月3月 AI/行事曆/截圖/注意事項
    public void showAlertDialogForAddCalendarEvent(){

        final Context m_context = getContext();
        if(null != m_dialog && true == m_dialog.isShowing()){
            m_dialog.dismiss();
            return;
        }
        m_dialog = new CIAlertDialog(getContext(), new CIAlertDialog.OnAlertMsgDialogListener() {
            String eventTtitle = "";
            @Override
            public void onAlertMsgDialog_Confirm() {
                Calendar beginTime = Calendar.getInstance();


                if(null != m_data){
                    eventTtitle = getContext().getString(R.string.trips_detail_tab_flight)+":"+
                            m_data.Airlines + " " + m_data.Flight_Number + " " +
                            m_data.Departure_Station_Name + " - " +
                            m_data.Arrival_Station_Name+
                            m_data.getDisplayDepartureDate()+" "+
                            m_data.getDisplayDepartureTime();

                    Calendar m_calendar = Calendar.getInstance();

                    Date m_date = AppInfo.getInstance(CIApplication.getContext()).ConvertStringtoDate(m_data.getDisplayDepartureDate()+" "+
                            m_data.getDisplayDepartureTime());
                    //Log.e("Departure","m_date: "+m_date.toString());
                    m_calendar.setTime(m_date);


                    beginTime.set(m_calendar.get(Calendar.YEAR), m_calendar.get(Calendar.MONTH), m_calendar.get(Calendar.DAY_OF_MONTH), m_calendar.get(Calendar.HOUR_OF_DAY), m_calendar.get(Calendar.MINUTE));
                    m_date = AppInfo.getInstance(CIApplication.getContext()).ConvertStringtoDate(m_data.getDisplayArrivalDate()+" "+
                            m_data.getDisplayArrivalTime());
                    //Log.e("Arrival","m_date: "+m_date.toString());
                    m_calendar.setTime(m_date);

                    Calendar endTime = Calendar.getInstance();
                    endTime.set(m_calendar.get(Calendar.YEAR), m_calendar.get(Calendar.MONTH), m_calendar.get(Calendar.DAY_OF_MONTH), m_calendar.get(Calendar.HOUR_OF_DAY), m_calendar.get(Calendar.MINUTE));
                    Intent intent = new Intent(Intent.ACTION_INSERT)
                            .setData(CalendarContract.Events.CONTENT_URI)
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                            .putExtra(CalendarContract.Events.TITLE, getContext().getString(R.string.trips_detail_tab_flight)+":"+
                                    m_data.Airlines + " " + m_data.Flight_Number + " " +
                                    m_data.Departure_Station_Name + " - " +
                                    m_data.Arrival_Station_Name)
                            .putExtra(CalendarContract.Events.DESCRIPTION, R.string.add_to_calendar_notes)
                            .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_TENTATIVE)
                            .putExtra(CalendarContract.Reminders.MINUTES, 1440)
                            .putExtra(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
                            .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com");
                    startActivity(intent);
                }
            }

            @Override
            public void onAlertMsgDialogg_Cancel() {

            }
        });
        m_dialog.uiSetTitleText(getContext().getString(R.string.trips_detail_tab_flight)+":"+
                m_data.Airlines + " " + m_data.Flight_Number + " " +
                m_data.Departure_Station + " - " +
                m_data.Arrival_Station);
        m_dialog.uiSetContentText(m_context.getString(R.string.add_to_calendar_notes));
        m_dialog.uiSetConfirmText(m_context.getString(R.string.add_to_calendar_add_confirm));
        m_dialog.uiSetCancelText(m_context.getString(R.string.cancel));
        m_dialog.show();
    }
}
