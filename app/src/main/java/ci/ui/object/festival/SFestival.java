package ci.ui.object.festival;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kevincheng on 2018/1/30.
 */

public abstract class SFestival implements IFestival{

    private static  ArrayList<IFestival> m_arrayList = new ArrayList<>();
    

    @Override
    public boolean isBetweenDate() {
        final String timePattern = "yyyy-MM-dd HH:mm";
        Date                 startDate   = getDate(getStartDateTime(), timePattern);
        Date                 endDate     = getDate(getEndDateTime(), timePattern);
        Date                 todayDate   = Calendar.getInstance().getTime();
        if(todayDate.after(startDate) && todayDate.before(endDate)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Date getDate(String date, String pattern) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
        simpleDateFormat.applyPattern(pattern);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<IFestival> getAll(){
        if(null == m_arrayList || m_arrayList.size() <= 0) {
            m_arrayList = new ArrayList<>();
            m_arrayList.add(new SMidAutumnFestival());
            m_arrayList.add(new SHalloweenFestival());
            m_arrayList.add(new SNewYearFestival());
            m_arrayList.add(new SChineseNewYearFestival());
        }
        return m_arrayList;
    }
}
