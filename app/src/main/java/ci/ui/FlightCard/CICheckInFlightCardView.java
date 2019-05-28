package ci.ui.FlightCard;

import android.content.Context;
import android.util.AttributeSet;

import ci.ui.define.ViewScaleDef;
import ci.ws.Models.entities.CITripListResp_Itinerary;

/**
 * 航班小卡, 有大卡跟小卡兩種Style,
 * 大卡-CIFlightCardView 用在HomePage,小卡- CICheckInFlightCardView使用在CheckIn
 * Created by Ryan on 16/3/3.
 */
public class CICheckInFlightCardView extends CIFlightCardView {


    public CICheckInFlightCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CICheckInFlightCardView(Context context) {
        super(context);
    }


    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {

        m_rlayout_head.getLayoutParams().height = vScaleDef.getLayoutHeight(29.2);
        int ipadding = vScaleDef.getLayoutWidth(16.2);
        m_rlayout_head.setPadding(ipadding, vScaleDef.getLayoutWidth(8), ipadding, vScaleDef.getLayoutWidth(7));
        vScaleDef.setTextSize(13, m_tvFlightNo);
        vScaleDef.setTextSize(13, m_tvDate);

        m_rlayout_body.getLayoutParams().height = vScaleDef.getLayoutHeight(84.8);
        m_rlayout_body.setPadding(ipadding, vScaleDef.getLayoutWidth(6.5), ipadding, vScaleDef.getLayoutWidth(6.3));
        vScaleDef.setTextSize(16.2, m_tvFrom);
        vScaleDef.setTextSize(16.2, m_tvTo);
        m_tvFromAirport.getLayoutParams().width = vScaleDef.getLayoutWidth(113.6);
        m_tvToAirport.getLayoutParams().width = vScaleDef.getLayoutWidth(113.6);
        vScaleDef.setTextSize(11.4, m_tvFromAirport);
        vScaleDef.setTextSize(11.4, m_tvToAirport);

        m_rlayout_time.getLayoutParams().height = vScaleDef.getLayoutHeight(38.7);
        vScaleDef.setTextSize(32.5, m_tvFromTime);
        vScaleDef.setTextSize(32.5, m_tvToTime);
        m_imgFlight_icon.getLayoutParams().height  = vScaleDef.getLayoutMinUnit(24);
        m_imgFlight_icon.getLayoutParams().width   = vScaleDef.getLayoutMinUnit(24);

        (m_vDiv.getLayoutParams()).height = vScaleDef.getLayoutHeight(0.3);
    }

    public void FlightInfoUpdate( CITripListResp_Itinerary Itinerary_Info ){
        super.FlightInfoUpdate(Itinerary_Info);

        ViewUpdate();
    }
}
