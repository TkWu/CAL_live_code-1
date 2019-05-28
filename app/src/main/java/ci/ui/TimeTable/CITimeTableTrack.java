package ci.ui.TimeTable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ci.function.Core.CIApplication;
import ci.function.FlightStatus.CIFlightResultDetialActivity;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.object.CIFlightStatusManager;
import ci.ui.object.CIFlightTrackInfo;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Models.entities.CIFlightStatusReq;
import ci.ws.Models.entities.CIFlightStatusResp;
import ci.ws.Models.entities.CIFlightStatus_infoEntity;
import ci.ws.Presenter.CIInquiryFlightStationPresenter;
import ci.ws.Presenter.CIInquiryFlightStatusPresenter;
import ci.ws.Presenter.Listener.CIInquiryFlightStatusListener;
import ci.ws.Presenter.Listener.CIInquiryFlightStatusStationListener;
import ci.ws.cores.object.CIDisplayDateTimeInfo;
import ci.ws.cores.object.CIWSCommon;
import ci.ws.cores.object.GsonTool;

import static ci.ui.object.CIFlightTrackInfo.SHAREPRE_TAG_TRACKDATA;
import static ci.ui.object.CIFlightTrackInfo.SHAREPRE_TAG_TRACKDATA_0;
import static ci.ui.object.CIFlightTrackInfo.SHAREPRE_TAG_TRACKDATA_1;
import static ci.ui.object.CIFlightTrackInfo.SHAREPRE_TAG_TRACKDATA_2;

/**
 * Created by user on 2016/4/3.
 */
public class CITimeTableTrack extends LinearLayout {

    CIInquiryFlightStatusStationListener m_FlightStatusStationListener = new CIInquiryFlightStatusStationListener() {
        @Override
        public void onAllStationSuccess(String rt_code, String rt_msg, CIInquiryFlightStationPresenter presenter) {
            handleCallBack();
        }

        @Override
        public void onStationError(String rt_code, String rt_msg) {
            handleCallBack();
        }

        /**
         * 用來處理callback後要做的事
         */
        private void handleCallBack(){
            List<CIFlightStationEntity> datas;
            if(null != m_presenter){
                datas =  m_presenter.getAllDepatureStationList();
                if(null != datas && datas.size() > 0){
                    setTrackData();
                    deleteTrack();
                    sendTrackReqFromNewThread();
                }
            }
        }

        @Override
        public void onODStationSuccess(String rt_code, String rt_msg, CIInquiryFlightStationPresenter presenter) {}

        @Override
        public void showProgress() {}

        @Override
        public void hideProgress() {}
    };

    private Context         m_Context;
    private LayoutInflater  m_layoutInflater;
    private String          m_strOrgTrackData;
    private View            m_view;


    private final int FLIGHT_STATUS = CITimeTableFlightStatusDetailList.DEF_VIEW_TYPE_FIGHT_STATUS;

    private ViewScaleDef viewScaleDef;
    private ArrayList<CIFlightStatus_infoEntity> respData;
    private String[] m_strDepatureStation;
    private String[] m_strArrivalStation;
    private CIInquiryFlightStationPresenter m_presenter;
    CIFlightTrackInfo info;
    public CITimeTableTrack(Context context) {
        super(context);
        info = new CIFlightTrackInfo(context);
        this.m_Context = context;
        m_layoutInflater = LayoutInflater.from(context);

        setOrientation(VERTICAL);

        //因應多語系轉換問題, 須重新取得機場名稱後再重新設定view - by Ling 2016.08.02
        m_presenter = CIInquiryFlightStationPresenter.getInstance(
                m_FlightStatusStationListener,
                CIInquiryFlightStationPresenter.ESource.FlightStatus);
        m_presenter.InquiryAllStationListFromWS();
        setTrackData();
        deleteTrack();
    }

    private void sendTrackReqFromNewThread(){
        HandlerThread handlerThread = new HandlerThread("track");
        handlerThread.start();
        Handler       handler       = new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                sendTrackReq();
            }
        });
    }

    private void sendTrackReq(){
        for (int cardCount = 0; cardCount < 3; cardCount++) {
            CIFlightStatus_infoEntity data = getTrackDataFromSP(cardCount);
            if(null == data) {
                continue;
            }
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final int spIndex = cardCount;
            CIInquiryFlightStatusPresenter presenter = new CIInquiryFlightStatusPresenter(new CIInquiryFlightStatusListener() {
                @Override
                public void onFlightStatusSuccess(String rt_code, String rt_msg, CIFlightStatusResp flightStatusResp) {

                    if(null != flightStatusResp && null != flightStatusResp.arFlightList && flightStatusResp.arFlightList.size() > 0) {
                        // TODO: 2017/10/16  arFlightList 應該要能取得單筆
                        setTrackDataToSP(flightStatusResp.arFlightList, spIndex);
                    }
                    setTrackData();
                    deleteTrack();
                    //不進行併發，所以用countDownLatch來控制處理
                    countDownLatch.countDown();
                }

                @Override
                public void onFlightStatusError(String rt_code, String rt_msg) {
                    countDownLatch.countDown();
                }

                @Override
                public void showProgress() {}

                @Override
                public void hideProgress() {}
            });

            CIFlightStatusReq req = new CIFlightStatusReq();

            req.search_type   = CIFlightStatusReq.BY_PIN_FLIGHT;
            req.flight_number = data.flight_number;
            req.flight_carrier = data.carrier;
            req.departure_station = data.depature_station_code;
            req.arrival_station = data.arrival_station_code;
            req.flight_date   = data.stad;
            req.by_depature_arrival_date   = "1";

            presenter.InquiryFlightStatusFromWS(req);
            try {
                countDownLatch.await(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private CIFlightStatus_infoEntity getTrackDataFromSP(int cardCount){
        String strOrgTrackData = info.getFlightTrackData(SHAREPRE_TAG_TRACKDATA + cardCount);
        Type dataType = new TypeToken<ArrayList<CIFlightStatus_infoEntity>>() {
        }.getType();
        ArrayList<CIFlightStatus_infoEntity> respData = GsonTool.getGson().fromJson(strOrgTrackData, dataType);
        CIFlightStatus_infoEntity data = null;
        if(respData != null && respData.size() > 0) {
            data = respData.get(0);
        }
        return data;
    }

    private void setTrackDataToSP(ArrayList<CIFlightStatus_infoEntity> data ,int cardCount){
        String jsonData = GsonTool.getGson().toJson(data);
        info.setFlightTrackData(SHAREPRE_TAG_TRACKDATA + cardCount, jsonData);
    }

    /*
    設定顯示的資料
     */
    private void convertData(int num, View view) {

        //因應多語系轉換問題, 字串需重新再設定一次 - by Ling 2016.08.02
        //轉換顯示時間
        CIDisplayDateTimeInfo ciDisDeparture = CIWSCommon.ConvertDisplayDateTime(
                respData.get(num).stdd, respData.get(num).stdt, respData.get(num).etdd,
                respData.get(num).etdt, respData.get(num).atdd, respData.get(num).atdt);
        respData.get(num).strDisDepartureDate = ciDisDeparture.strDisplayDate;
        respData.get(num).strDisDepartureTime = ciDisDeparture.strDisplayTime;
        respData.get(num).strDisDepartureName = ciDisDeparture.strDisplayTagName;

        CIDisplayDateTimeInfo ciDisArrival = CIWSCommon.ConvertDisplayDateTime(
                respData.get(num).stad, respData.get(num).stat, respData.get(num).etad,
                respData.get(num).etat, respData.get(num).atad, respData.get(num).atat);
        respData.get(num).strDisArrivalDate = ciDisArrival.strDisplayDate;
        respData.get(num).strDisArrivalTime = ciDisArrival.strDisplayTime;
        respData.get(num).strDisArrivalName = ciDisArrival.strDisplayTagName;
        //塞入對應語系的機場城市名
        if( null != m_strDepatureStation && num < m_strDepatureStation.length && !TextUtils.isEmpty(m_strDepatureStation[num])){
            respData.get(num).depature_station_desc = m_strDepatureStation[num];
        }
        if( null != m_strArrivalStation && num < m_strArrivalStation.length && !TextUtils.isEmpty(m_strArrivalStation[num])){
            respData.get(num).arrival_station_desc = m_strArrivalStation[num];
        }

        String startTime;
        String arrivalTime;
        String departureDate;
        String ArrivalDate;
        String startPlace = respData.get(num).depature_station_desc;

        TextView lefTextView = (TextView) view.findViewById(R.id.scheduled_left_textview);
        TextView rightTextView = (TextView) view.findViewById(R.id.scheduled_right_textview);
        //2016-06-29 ryan for 調整為由WS Model做時間判斷, ui改拿顯示用Tag
        departureDate   =       respData.get(num).strDisDepartureDate;
        ArrivalDate     =       respData.get(num).strDisArrivalDate;
        rightTextView.setText(  respData.get(num).strDisArrivalName );
        arrivalTime =           respData.get(num).strDisArrivalTime ;
        lefTextView.setText(    respData.get(num).strDisDepartureName );
        startTime   =           respData.get(num).strDisDepartureTime;


        String arrivalPlace = respData.get(num).arrival_station_desc;
        //2016-06-21 modifly by ryan for 統一由 WS Model層解析為4碼, ui 不用特別處理, flight number  四位數 ,不足補0
        String flightNumber = respData.get(num).carrier + " " + respData.get(num).flight_number;

        String colorCode = respData.get(num).color_code;

        TextView cardFlightNumber = (TextView) view.findViewById(R.id.flight_number);
        cardFlightNumber.setText(flightNumber);
        TextView cardStartTime = (TextView) view.findViewById(R.id.startTime_left_textview);
        cardStartTime.setText(startTime);
        TextView cardStartPlace = (TextView) view.findViewById(R.id.startPlace_left_textview);
        cardStartPlace.setText(startPlace);
        TextView cardArrivalTime = (TextView) view.findViewById(R.id.arrivalTime_right_textview);
        cardArrivalTime.setText(arrivalTime);
        TextView cardArrivalPlace = (TextView) view.findViewById(R.id.arrivalPlace_right_textview);
        cardArrivalPlace.setText(arrivalPlace);
        TextView trackRightHead = (TextView) view.findViewById(R.id.track_place);
        //右上角僅需顯示機場代碼 ex: TPE->HKG - by Ling 2016.08.02
        trackRightHead.setText(getResources().getString(R.string.trips_detail_title,
                respData.get(num).depature_station_code,
                respData.get(num).arrival_station_code));
        TextView trackLeftHead = (TextView) view.findViewById(R.id.track_time);
        //時間顯示格式調整為yyyy-mm-dd by Ryan 2016-08-16
        trackLeftHead.setText(departureDate);

        TextView    tomorrowTag = (TextView)m_view.findViewById(R.id.tv_tomorrow_tag);
        AppInfo appInfo     = AppInfo.getInstance(CIApplication.getContext());
        String day = appInfo.getShowTomorrowDay(departureDate, ArrivalDate);
        tomorrowTag.setText(day);

        ImageView statusIconImg = (ImageView) view.findViewById(R.id.flight_icon);
        TextView statusText = (TextView) view.findViewById(R.id.flight_status_text);
        statusIconImg.setVisibility(VISIBLE);
        statusText.setVisibility(VISIBLE);

        // 管理WS回傳的航班動態
        CIFlightStatusManager.transferIconAndTextByColorCode(
                statusIconImg,
                statusText,
                respData.get(num).color_code,
                respData.get(num).flight_status
        );
    }


    /*
    按照獲得的資料去設定 CIFlightResultDetialActivity
     */
    private OnClickListener cardClick(final int whichCount) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(m_Context, CIFlightResultDetialActivity.class);
                String whitchCountData = info.getFlightTrackData(SHAREPRE_TAG_TRACKDATA + whichCount);
                intent.putExtra(CIFlightResultDetialActivity.BUNDLE_PARA_ITINERARY_DATA, whitchCountData);
                intent.putExtra(CIFlightResultDetialActivity.BUNDLE_PARA_VIEW_TYPE, FLIGHT_STATUS);
                m_Context.startActivity(intent);
                ((Activity) m_Context).overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);

            }
        };
    }

    public void setTrackData() {

        viewScaleDef = ViewScaleDef.getInstance(m_Context);
        if ((!info.getFlightTrackData(SHAREPRE_TAG_TRACKDATA_0).equals("")
                || !info.getFlightTrackData(SHAREPRE_TAG_TRACKDATA_1).equals("")
                || !info.getFlightTrackData(SHAREPRE_TAG_TRACKDATA_2).equals(""))) {
            View showTrack = m_layoutInflater.inflate(R.layout.layout_track_header, null);
            addView(showTrack);
            viewScaleDef.selfAdjustAllView(showTrack.findViewById(R.id.track_title));
        }

        for (int cardCount = 0; cardCount < 3; cardCount++) {
            LinearLayout layout = new LinearLayout(m_Context);
            layout.setOnClickListener(cardClick(cardCount));
            layout.setOrientation(VERTICAL);
            m_strOrgTrackData = info.getFlightTrackData(SHAREPRE_TAG_TRACKDATA + cardCount);
            Gson gson = new Gson();
            Type dataType = new TypeToken<ArrayList<CIFlightStatus_infoEntity>>() {
            }.getType();
            if (!m_strOrgTrackData.equals("")) {
                respData = gson.fromJson(m_strOrgTrackData, dataType);

                m_strDepatureStation = new String[respData.size()];
                m_strArrivalStation = new String[respData.size()];
                CIFlightStationEntity depatureStationData = null,arrivalStationData = null;
                for (int itemCount = 0; itemCount < respData.size(); itemCount++) {

                    if ( null != m_presenter){
                        depatureStationData = m_presenter.getStationInfoByIATA( respData.get(itemCount).depature_station_code );
                        if(null == depatureStationData){
                            m_strDepatureStation[itemCount] = "";
                        } else {
                            m_strDepatureStation[itemCount] = depatureStationData.localization_name;
                        }


                        arrivalStationData = m_presenter.getStationInfoByIATA( respData.get(itemCount).arrival_station_code );
                        if(null == arrivalStationData){
                            m_strArrivalStation[itemCount] = "";
                        } else {
                            m_strArrivalStation[itemCount] = arrivalStationData.localization_name;
                        }
                    }

                    m_view = m_layoutInflater.inflate(R.layout.layout_timetable_flight_card, null);
                    convertData(itemCount, m_view);
                    setViewScaleDef(m_view);
                    View head = m_view.findViewById(R.id.track_head);
                    View title = m_view.findViewById(R.id.flight_title);
                    View content = m_view.findViewById(R.id.card_content);
                    View contentBg = m_view.findViewById(R.id.card_content_bg);

                    if (itemCount == 0) {
                        head.setVisibility(VISIBLE);
                        title.setBackgroundResource(R.drawable.timetable_title_bg_corner);
                    }
                    m_view.findViewById(R.id.divider).setVisibility(GONE);


                    if (itemCount == respData.size() - 1) {
                        m_view.findViewById(R.id.dash_part).setVisibility(GONE);
                        m_view.findViewById(R.id.divider).setVisibility(VISIBLE);
                        content.setBackgroundResource(R.drawable.timetable_content_bg_corner);
                        contentBg.setBackgroundResource(R.drawable.bg_timetable_content_border_corner);
                    }
                    layout.addView(m_view);
                }

                if(null == depatureStationData || null == arrivalStationData){
                    List<CIFlightStationEntity> datas;
                    if(null != m_presenter){
                        datas =  m_presenter.getAllDepatureStationList();
                        /*有機會因為機場代碼對應不到總表資料，所以除非總表資料為空，否則一律不去向server取資料
                       ，避免無窮迴圈*/
                        if(null == datas || 0 >= datas.size()){
                            m_presenter.InquiryAllStationListFromWS();
                        }
                    }
                }

                addView(layout);
            }
        }
    }

    private void setViewScaleDef(View item) {
        viewScaleDef.selfAdjustAllView(item.findViewById(R.id.flight_title));
        viewScaleDef.selfAdjustAllView(item.findViewById(R.id.card_content_bg));
        viewScaleDef.selfAdjustAllView(item.findViewById(R.id.dash_part_bg));
        viewScaleDef.selfAdjustAllView(item.findViewById(R.id.divider));
        viewScaleDef.selfAdjustAllView(item.findViewById(R.id.track_head));

    }

    /*
    每次進入homePage 都會執行
    判斷日期
   < 0 表示過期
     */
    public void deleteTrack() {
        //2017-03-02 modify by ryan 時間比對必須日期+時間
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String item;
        for (int count = 0; count < 3; count++) {
            item = info.getFlightTrackData(SHAREPRE_TAG_TRACKDATA+ count);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<CIFlightStatus_infoEntity>>() {
            }.getType();
            ArrayList<CIFlightStatus_infoEntity> arrayList = gson.fromJson(item, type);
            if (arrayList != null) {
                for (int itemCount = 0; itemCount < arrayList.size(); itemCount++) {
                    String target;

                    //2017-03-02 modify by ryan 時間比對必須日期+時間
                    target = String.format( "%s %s", arrayList.get(itemCount).strDisArrivalDate, arrayList.get(itemCount).strDisArrivalTime );

                    Date nowDate = new Date();
                    Date targetDate = null;
                    try {
                        targetDate = format.parse(target);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (targetDate != null) {
                        if ((targetDate.getTime() - nowDate.getTime()) / (1000 * 3600 * 24) < 0) {
                            info.removeFlightTrackData(SHAREPRE_TAG_TRACKDATA + count);
                        }
                    }
                }
            }
        }
        removeAllViews();
        setTrackData();
    }
}
