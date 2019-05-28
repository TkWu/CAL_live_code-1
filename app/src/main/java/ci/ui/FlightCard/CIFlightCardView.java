package ci.ui.FlightCard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseView;
import ci.function.Core.CIApplication;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Presenter.CIInquiryFlightStationPresenter;

/**
 * 航班小卡, 有大卡跟小卡兩種Style,
 * 大卡-CIFlightCardView 用在HomePage,小卡- CICheckInFlightCardView使用在CheckIn
 * Created by Ryan on 16/3/3.
 */
public class CIFlightCardView extends BaseView {

    RelativeLayout  m_rlayout_bg;
    RelativeLayout  m_rlayout_head;
    RelativeLayout  m_rlayout_body;
    TextView        m_tvFlightStatus;
    TextView        m_tvFlightNo;
    TextView        m_tvDate;
    TextView        m_tvFrom;
    TextView        m_tvTo;
    TextView        m_tvFromAirport;
    TextView        m_tvToAirport;
    TextView        m_tvTomorrowTag;
    RelativeLayout  m_rlayout_time;
    TextView        m_tvFromTime;
    TextView        m_tvToTime;
    ImageView       m_imgFlight_icon;
    View            m_vDiv;
    Context         m_Context = CIApplication.getContext();

    boolean         m_isShowTomorrowTag = false;

    private CITripListResp_Itinerary m_Itinerary_Info = null;
    private Boolean m_bonCreateOK = false;

    public CIFlightCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial();
    }

    public CIFlightCardView(Context context) {
        super(context);
        initial();
    }

    public CIFlightCardView(Context context, boolean isShowTomorrowTag) {
        super(context);
        m_isShowTomorrowTag = isShowTomorrowTag;
        initial();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_flight_info_view;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater) {

        m_rlayout_bg = (RelativeLayout)findViewById(R.id.rlayout_bg);
        m_rlayout_head = (RelativeLayout)findViewById(R.id.rlayout_head);
        m_tvFlightStatus = (TextView)findViewById(R.id.tv_flight_status);
        m_tvFlightNo   = (TextView)findViewById(R.id.tv_FlightNo);
        m_tvDate       = (TextView)findViewById(R.id.tv_Date);
        m_rlayout_body = (RelativeLayout)findViewById(R.id.rlayout_body);
        m_tvFrom       = (TextView)findViewById(R.id.tv_from);
        m_tvTo         = (TextView)findViewById(R.id.tv_to);
        m_tvFromAirport= (TextView)findViewById(R.id.tv_from_airport);
        m_tvToAirport  = (TextView)findViewById(R.id.tv_to_airport);
        m_rlayout_time = (RelativeLayout)findViewById(R.id.rlayout_time);
        m_tvFromTime   = (TextView)findViewById(R.id.tv_from_time);
        m_tvToTime     = (TextView)findViewById(R.id.tv_to_time);
        m_tvTomorrowTag= (TextView)findViewById(R.id.tv_tomorrow_tag);
        m_imgFlight_icon= (ImageView)findViewById(R.id.img_flight_from_to);
        m_vDiv         = findViewById(R.id.vDiv);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {

        vScaleDef.selfAdjustAllView(m_rlayout_bg);
        vScaleDef.selfAdjustSameScaleView(m_imgFlight_icon, 30, 30);
        onFlightStatus_Normal();

        m_bonCreateOK = true;
        ViewUpdate();
    }

    public void ViewUpdate(){

        if ( null != m_Itinerary_Info ){

            m_tvFlightNo.setText( m_Itinerary_Info.Airlines + " " + m_Itinerary_Info.Flight_Number );
            //2016-11-15 Modify by Ryan for 調整PNR時間邏輯, 不再抓取CPR的時間來更新, 調整為抓取DISPLAY TAG
            m_tvDate.setText(m_Itinerary_Info.getDisplayDepartureDate());
            //m_tvDate.setText(m_Itinerary_Info.Departure_Date);
            m_tvFrom.setText(m_Itinerary_Info.Departure_Station);
            m_tvTo.setText(m_Itinerary_Info.Arrival_Station);

            CIFlightStationEntity Filght_Departure = CIInquiryFlightStationPresenter.getInstance(null, CIInquiryFlightStationPresenter.ESource.TimeTable).getStationInfoByIATA(m_Itinerary_Info.Departure_Station);
            if ( null != Filght_Departure ){
                m_tvFromAirport.setText(Filght_Departure.airport_name);
            } else {
                m_tvFromAirport.setText("");
            }

            if(true ==  m_isShowTomorrowTag) {
                String  strDisplayDepartureDate = m_Itinerary_Info.getDisplayDepartureDate();
                String  strDisplayArrivalDate   = m_Itinerary_Info.getDisplayArrivalDate();
                AppInfo appInfo                 = AppInfo.getInstance(getContext());
                String day = appInfo.getShowTomorrowDay(strDisplayDepartureDate, strDisplayArrivalDate);
                m_tvTomorrowTag.setText(day);
            }

            CIFlightStationEntity Filght_Arrival = CIInquiryFlightStationPresenter.getInstance(null, CIInquiryFlightStationPresenter.ESource.TimeTable).getStationInfoByIATA(m_Itinerary_Info.Arrival_Station);
            if ( null != Filght_Arrival ) {
                m_tvToAirport.setText(Filght_Arrival.airport_name);
            } else {
                m_tvToAirport.setText("");
            }
            //2016-11-15 Modify by Ryan for 調整PNR時間邏輯, 不再抓取CPR的時間來更新, 調整為抓取DISPLAY TAG
            m_tvFromTime.setText(m_Itinerary_Info.getDisplayDepartureTime());
            m_tvToTime.setText(m_Itinerary_Info.getDisplayArrivalTime());
            //m_tvFromTime.setText(m_Itinerary_Info.Departure_Time);
            //m_tvToTime.setText(m_Itinerary_Info.Arrival_Time);
        }
    }

    /**只有一張航程卡，四個邊都是圓角*/
    public void onOnlyOneFlightCard(){
        m_rlayout_head.setBackgroundResource(R.drawable.bg_main_flight_info_head_radius);
        m_rlayout_body.setBackgroundResource(R.drawable.bg_main_flight_info_body_radius);
    }

    /**多張航程卡的第一張，頂部的邊是圓角*/
    public void onFirstFlightCard(){
        m_rlayout_head.setBackgroundResource(R.drawable.bg_main_flight_info_head_radius);
        m_rlayout_body.setBackgroundResource(R.color.white_six);
    }

    /**多張航程卡中用來表示中間部份的卡，四個邊都是直角*/
    public void onCenterFlightCard(){
        m_rlayout_head.setBackgroundResource(R.color.white_four);
        m_rlayout_body.setBackgroundResource(R.color.white_six);
    }

    /**多張航程卡中用來表示最後一張卡，底部的邊是圓角*/
    public void onLastFlightCard(){
        m_rlayout_head.setBackgroundResource(R.color.white_four);
        m_rlayout_body.setBackgroundResource(R.drawable.bg_main_flight_info_body_radius);
    }

    public void onFlightStatus_Delay(){
        //Delay
        m_tvFlightStatus.setText(R.string.delay);
        m_tvFlightStatus.setVisibility(View.VISIBLE);
        m_tvFlightStatus.setBackgroundResource(R.drawable.bg_main_flight_info_delay);
        m_tvFromTime.setTextColor(m_Context.getResources().getColor(R.color.pinkish_red));
        m_tvToTime.setTextColor(m_Context.getResources().getColor(R.color.pinkish_red));
        m_tvFromTime.setAlpha((float) 1);
        m_tvToTime.setAlpha((float) 1);
    }

    public void onFlightStatus_Cancel(){
        //Cancel
        m_tvFlightStatus.setText(R.string.cancel);
        m_tvFlightStatus.setVisibility(View.VISIBLE);
        m_tvFlightStatus.setBackgroundResource(R.drawable.bg_main_flight_info_cancel);
        m_tvFromTime.setTextColor(m_Context.getResources().getColor(R.color.french_blue));
        m_tvToTime.setTextColor(m_Context.getResources().getColor(R.color.french_blue));
        m_tvFromTime.setAlpha((float)0.5);
        m_tvToTime.setAlpha((float)0.5);
    }

    public void onFlightStatus_Normal(){

        m_tvFlightStatus.setVisibility(View.INVISIBLE);
        m_tvFromTime.setTextColor(m_Context.getResources().getColor(R.color.french_blue));
        m_tvToTime.setTextColor(m_Context.getResources().getColor(R.color.french_blue));
        m_tvFromTime.setAlpha((float)1);
        m_tvToTime.setAlpha((float)1);

    }

    public void FlightInfoUpdate( CITripListResp_Itinerary Itinerary_Info ){
        m_Itinerary_Info = Itinerary_Info;
        if ( m_bonCreateOK ){
            ViewUpdate();
        }
    }
}
