package ci.ws.cores.object;

import com.chinaairlines.mobile30.R;

import ci.function.Core.CIApplication;

/**
 * Created by Ryan on 16/9/14.
 */
public class CIWSBookingClass {

    /**商務艙*/
    public static final String BOOKING_CLASS_BUSINESS = "C";
    /**豪華經濟艙*/
    public static final String BOOKING_CLASS_PREMIUM_ECONOMY  = "W";
    /**經濟艙*/
    public static final String BOOKING_CLASS_ECONOMY  = "Y";

    /**定位艙等*/
    private static final String[] Business      = new String[]{"J","C","D","I","O"};
    private static final String[] Premiun       = new String[]{"W","U","E","P","Z","A"};
    private static final String[] Economy       = new String[]
            {"Y","B","M","Q","V","G","S","H","K","L", "T","N","X","R"};

    /**
     * 客艙對應表
     * C：J、C、D、I、O
     * W：W、U、E、P、Z
     * Y：Y、B、M、Q、V、G、S、H、K、L、T、N、X、R
     */
    public static String ParseBookingClass( String strBookingClass ){

        if ( null == strBookingClass )
            return BOOKING_CLASS_ECONOMY;

        //檢查是否為商務艙
        for ( String strTag : Business ){
            if ( strTag.equals(strBookingClass) ){
                return BOOKING_CLASS_BUSINESS;
            }
        }

        //檢查是否為豪華經濟艙
        for ( String strTag : Premiun ){
            if ( strTag.equals(strBookingClass) ){
                return BOOKING_CLASS_PREMIUM_ECONOMY;
            }
        }

        //檢查是否為經濟艙
        for ( String strTag : Economy ){
            if ( strTag.equals(strBookingClass) ){
                return BOOKING_CLASS_ECONOMY;
            }
        }

        return BOOKING_CLASS_ECONOMY;
    }

    //C商務艙：C、J、D、I、O
    //W豪華經濟艙：W、U、E、P、Z
    //Y經濟艙：Y、B、M、Q、V、G、S、H、K、L、T、N、X、R
    public static String BookingClassCodeToName(String strCode){
        if ( null == strCode )
            return "";
        switch (strCode){
            case BOOKING_CLASS_BUSINESS:
                return CIApplication.getContext().getString(R.string.trips_detail_business);
            case BOOKING_CLASS_PREMIUM_ECONOMY:
                return CIApplication.getContext().getString(R.string.trips_detail_premium_economy);
            case BOOKING_CLASS_ECONOMY:
                return CIApplication.getContext().getString(R.string.economy);
            default:
                return "";
        }
    }
}
