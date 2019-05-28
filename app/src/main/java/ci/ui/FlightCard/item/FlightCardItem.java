package ci.ui.FlightCard.item;

/**
 * Created by JobsNo5 on 16/3/7.
 */
public class FlightCardItem {

    public String  strFlightNo;
    public String  strAirlines;
    public String  strDate;
    public String  strDeparture;
    public String  strArrival;
    public String  strDepartureAirport;
    public String  strArrivalAirport;
    public String  strDepartureTime;
    public String  strArrivalTime;
    public Boolean bIsTransition;
    public Boolean bSelect;
    public Boolean bFirstSegment;//是否為第一個Segment
    public int iGroupIndex; //第幾個PNR
    public int iChildIndex; //第幾個 Itinerary_Info


    public FlightCardItem(){
        strFlightNo = "";
        strAirlines = "";
        strDate     = "";
        strDeparture= "";
        strArrival  = "";
        strDepartureAirport = "";
        strArrivalAirport   = "";
        strDepartureTime    = "";
        strArrivalTime      = "";
        bIsTransition       = false;
        bSelect     = false;
        bFirstSegment = false;
        iGroupIndex = 0;
        iChildIndex = 0;
    }
}
