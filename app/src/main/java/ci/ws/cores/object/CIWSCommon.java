package ci.ws.cores.object;

import android.text.TextUtils;

import com.chinaairlines.mobile30.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ci.function.Core.CIApplication;

/**
 * Created by Ryan on 16/6/20.
 * 共用Function
 */
public class CIWSCommon {

    private static final String UNKNOW_DATE = "0001-01-01";
    private static final String UNKNOW_TIME = "00:00";

    /**將航班編號調整為四位數, 不足補0*/
    public static String ConvertFlightNumber( String strFlight_number ){
        if ( TextUtils.isEmpty(strFlight_number) ){
            strFlight_number = "0";
        }
        //格式符合就直接return
        if ( 4 == strFlight_number.length() ){
            return strFlight_number;
        }

        return ConvertFlightNumber(Integer.valueOf(strFlight_number));
    }

    /**將航班編號調整為四位數, 不足補0*/
    public static String ConvertFlightNumber( int iFlight_number ){
        return String.format("%04d", iFlight_number);
    }


    public static CIDisplayDateTimeInfo ConvertDisplayDateTime( String stad, String stat , String etad, String etat, String atad, String atat ){

        CIDisplayDateTimeInfo disDateTime = new CIDisplayDateTimeInfo();

        //2018-09-12 調整邏輯為 只判斷日期格式
        // 判斷 實際  或 預計 時間
        //優先取實際時間, 取不到再取預計時間, 最後才抓取表定時間
        //表定一定會有時間
        //if ( false == TextUtils.equals( UNKNOW_DATE, atad ) && false == TextUtils.equals( UNKNOW_TIME, atat ) ) {
        if (!TextUtils.equals(UNKNOW_DATE, atad)) {

            disDateTime.strDisplayDate      = atad;
            disDateTime.strDisplayTime      = atat;
            disDateTime.strDisplayTagName   = CIApplication.getContext().getString(R.string.actual);

        } else if (!TextUtils.equals(UNKNOW_DATE, etad)) {
        //} else if ( false == TextUtils.equals( UNKNOW_DATE, etad ) && false == TextUtils.equals( UNKNOW_TIME, etat ) ) {

            disDateTime.strDisplayDate      = etad;
            disDateTime.strDisplayTime      = etat;
            disDateTime.strDisplayTagName   = CIApplication.getContext().getString(R.string.estimated_time);

        } else {

            disDateTime.strDisplayDate      = stad;
            disDateTime.strDisplayTime      = stat;
            disDateTime.strDisplayTagName   = CIApplication.getContext().getString(R.string.scheduled);
        }

        return disDateTime;
    }

    public static CIDisplayDateTimeInfo ConvertDisplayDateTimeForCPR( String stad, String stat , String etad, String etat, String atad, String atat ){

        CIDisplayDateTimeInfo disDateTime = new CIDisplayDateTimeInfo();

        //2018-09-12 調整邏輯為 只判斷日期格式
        // 判斷 實際  或 預計 時間
        //優先取實際時間, 取不到再取預計時間, 最後才抓取表定時間
        //表定一定會有時間
//        if ( (false == TextUtils.equals( UNKNOW_DATE, atad ) && false == TextUtils.equals( UNKNOW_TIME, atat )) &&
//                (false == TextUtils.isEmpty( atad ) && false == TextUtils.isEmpty(atat)  ) ) {
        if ( !TextUtils.equals(UNKNOW_DATE, atad) && !TextUtils.isEmpty(atad) ) {

            disDateTime.strDisplayDate = atad;
            disDateTime.strDisplayTime = atat;

//        } else if ( (false == TextUtils.equals( UNKNOW_DATE, etad ) && false == TextUtils.equals( UNKNOW_TIME, etat ))  &&
//                (false == TextUtils.isEmpty( etad ) && false == TextUtils.isEmpty( etat )  ) ) {
        } else if ( !TextUtils.equals(UNKNOW_DATE, etad) && !TextUtils.isEmpty(etad) ) {

            disDateTime.strDisplayDate      = etad;
            disDateTime.strDisplayTime      = etat;
        } else {

            disDateTime.strDisplayDate      = stad;
            disDateTime.strDisplayTime      = stat;
        }

        return disDateTime;
    }

    /** 轉換成WS要的日期字串格式 */
    public static String ConvertDatetoWSFormat( Calendar calendar ){

        SimpleDateFormat finalFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = calendar.getTime();
        return finalFormat.format(date);
    }
}
