package ci.function.FlightStatus;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.Calendar;

import ci.ui.define.ViewScaleDef;

/**
 * Created by flowmahuang on 2016/4/8.
 */
public class CISelectDateEightDayAdapter extends BaseAdapter {
    private ViewScaleDef m_vScaleDef = null;
    private LayoutInflater inflater;
    private int clickTemp;
    private Context context;
    private String[] eightDay;
    private int[] week;

    public CISelectDateEightDayAdapter(Context context) {
        this.context = context;
        m_vScaleDef = ViewScaleDef.getInstance(context);
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        clickTemp = 1;
        initDateAndTitle();
    }

    @Override
    public int getCount() {
        return 8;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_eight_day_button, null);
        }
        GroupHolder groupHolder = new GroupHolder();
        groupHolder.m_layout = (RelativeLayout) convertView.findViewById(R.id.eight_layout);
        groupHolder.m_layout.getLayoutParams().width = m_vScaleDef.getLayoutWidth(52);
        groupHolder.m_layout.getLayoutParams().height = m_vScaleDef.getLayoutHeight(60);
        m_vScaleDef.setPadding(groupHolder.m_layout, 6, 0, 6, 0);

        groupHolder.m_WeekDay = (TextView) convertView.findViewById(R.id.tv_week_day);
        m_vScaleDef.setTextSize(13, groupHolder.m_WeekDay);
        m_vScaleDef.setMargins(groupHolder.m_WeekDay, 0, 7.4, 0, 0);
        groupHolder.m_WeekDay.setText(setWeekDay(week[position]));

        groupHolder.m_DateDay = (TextView) convertView.findViewById(R.id.tv_date_day);
        m_vScaleDef.setTextSize(20, groupHolder.m_DateDay);
        m_vScaleDef.setMargins(groupHolder.m_DateDay, 0, 8, 0, 4.7);
        groupHolder.m_DateDay.setText(eightDay[position]);

        if (clickTemp == position) {
            if (Build.VERSION.SDK_INT > 22) {
                groupHolder.m_WeekDay.setTextColor(context.getResources().getColor(R.color.french_blue, null));
                groupHolder.m_DateDay.setTextColor(context.getResources().getColor(R.color.french_blue, null));
            } else {
                groupHolder.m_WeekDay.setTextColor(context.getResources().getColor(R.color.french_blue));
                groupHolder.m_DateDay.setTextColor(context.getResources().getColor(R.color.french_blue));
            }
            groupHolder.m_layout.setBackgroundResource(R.drawable.bg_select_date_radius3_white);

        } else {
            groupHolder.m_layout.setBackgroundColor(Color.TRANSPARENT);
            if (Build.VERSION.SDK_INT > 22) {
                groupHolder.m_DateDay.setTextColor(context.getResources().getColor(R.color.white_four, null));
                groupHolder.m_WeekDay.setTextColor(context.getResources().getColor(R.color.white_50, null));
            } else {
                groupHolder.m_DateDay.setTextColor(context.getResources().getColor(R.color.white_four));
                groupHolder.m_WeekDay.setTextColor(context.getResources().getColor(R.color.white_50));
            }
        }

        if (position == 7) {
            groupHolder.m_layout.setBackgroundColor(Color.TRANSPARENT);
            if (Build.VERSION.SDK_INT > 22) {
                groupHolder.m_DateDay.setTextColor(context.getResources().getColor(R.color.white_15, null));
                groupHolder.m_WeekDay.setTextColor(context.getResources().getColor(R.color.white_15, null));
            } else {
                groupHolder.m_DateDay.setTextColor(context.getResources().getColor(R.color.white_15));
                groupHolder.m_WeekDay.setTextColor(context.getResources().getColor(R.color.white_15));
            }
        }

        return convertView;
    }

    public static class GroupHolder {
        TextView m_WeekDay,
                m_DateDay;
        RelativeLayout m_layout;
    }

    public void setSeclection(int position) {
        clickTemp = position;
    }

    private void initDateAndTitle() {
        final Calendar c = Calendar.getInstance();
        //SimpleDateFormat month = new SimpleDateFormat("MMM");

        c.add(Calendar.DAY_OF_MONTH, -1);
        eightDay = new String[8];
        week = new int[8];

        for (int i = 0; i < 8; i++) {
            eightDay[i] = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
            week[i] = c.get(Calendar.DAY_OF_WEEK);
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private String setWeekDay(int day) {
        String weekDay = null;
        switch (day) {
            case 1:
                weekDay = context.getString(R.string.weather_day_Sun);
                break;
            case 2:
                weekDay = context.getString(R.string.weather_day_Mon);
                break;
            case 3:
                weekDay = context.getString(R.string.weather_day_Tue);
                break;
            case 4:
                weekDay = context.getString(R.string.weather_day_Wed);
                break;
            case 5:
                weekDay = context.getString(R.string.weather_day_Thu);
                break;
            case 6:
                weekDay = context.getString(R.string.weather_day_Fri);
                break;
            case 7:
                weekDay = context.getString(R.string.weather_day_Sat);
                break;
        }
        return weekDay;
    }
}
