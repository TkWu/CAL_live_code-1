package ci.ui.object;

import android.location.Location;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.ws.Models.entities.CITripListResp;
import ci.ws.Models.entities.CITripListResp_Itinerary;

/**
 * Created by kevincheng on 2017/3/8.
 */

public class CITripUtils {

    private static final String timePattern = "yyyy-MM-dd HH:mm";

    /**
     * 計算所有行程後排序由比較近的日期到比較遠的日期
     * */
    public static ArrayList<CITripListResp_Itinerary> getSortedTripsByDate(ArrayList<CITripListResp_Itinerary> datas){

        if(datas == null || datas.size() <=0) {
            return datas;
        }

        Comparator<CITripListResp_Itinerary> comparator = new Comparator<CITripListResp_Itinerary>() {

            @Override
            public int compare(CITripListResp_Itinerary o1, CITripListResp_Itinerary o2) {

                Long o1Data = getTime(o1.getDisplayDepartureDate() + " " + o1.getDisplayDepartureTime());
                Long o2Date = getTime(o2.getDisplayDepartureDate() + " " + o2.getDisplayDepartureTime());
                SLog.d(o1Data + "-" + o2Date + "=" + ( o1Data - o2Date));
                return o1Data.compareTo(o2Date);
            }

        };

        Collections.sort(datas, comparator);
        return datas;
    }

    public static ArrayList<CITripListResp_Itinerary> filterTripsBeforeDate(ArrayList<CITripListResp_Itinerary> datas){
        if(datas == null || datas.size() <=0) {
            return datas;
        }
        ArrayList<CITripListResp_Itinerary> datasBeforeToday = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        String strToday = calendar.get(Calendar.YEAR)
                + "-" + (calendar.get(Calendar.MONTH) + 1)
                + "-" + calendar.get(Calendar.DATE)
                + " 00:00";
        Date today = getDate(strToday);
        for(CITripListResp_Itinerary data: datas) {
            Date date = getDate(data.getDisplayDepartureDate() + " " + data.getDisplayDepartureTime());
            if(date != null && today != null
                    && date.before(today)){
                datasBeforeToday.add(data);
            }
        }

        for(CITripListResp_Itinerary data: datasBeforeToday) {
            datas.remove(data);
        }

        return datasBeforeToday;
    }

    public static long getTime(String data){
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
        simpleDateFormat.applyPattern(timePattern);
        try {
            return simpleDateFormat.parse(data).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static Date getDate(String date){
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
        simpleDateFormat.applyPattern(timePattern);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
