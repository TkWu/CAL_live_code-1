package ci.ui.TimeTable;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.ArrayList;

import ci.function.Core.CIApplication;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.object.CIFlightStatusManager;
import ci.ws.Models.entities.CIFlightStatus_infoEntity;
import ci.ws.Models.entities.CITimeTable_InfoEntity;

/**
 * Created by user on 2016/3/28.
 */
//備註：FlightStatus & TimeTable 共用航班的內容頁面
public class CITimeTableFlightStatusDetailList extends ListView {

    private Context         m_context;
    private LayoutInflater  m_LayoutInflater;

    private ArrayList<CIFlightStatus_infoEntity>    m_arFlightStatusItemInfoList = new ArrayList<>();
    private ArrayList<CITimeTable_InfoEntity>       m_arTimeTableItemInfoList = new ArrayList<>();
    private ArrayList<Integer>                      m_arGroupList = new ArrayList<>();

    private String      m_strTrackData;
    private int         m_iPosition = 0;
    private int         m_iViewType = 0;
    private BaseAdapter m_baseAdapter;


    public static final int DEF_VIEW_TYPE_FIGHT_STATUS  = 100;
    public static final int DEF_VIEW_TYPE_TIME_TABLE    = 101;

    public CITimeTableFlightStatusDetailList(Context context, AttributeSet attrs, int position) {
        super(context, attrs);
        this.m_context = context;
        this.m_iPosition = position;
        m_LayoutInflater = LayoutInflater.from(m_context);

        setVerticalScrollBarEnabled(false);
        setDivider(null);
        setSelector(R.color.transparent);
        m_baseAdapter = baseAdapter();
        setHeader();
        setAdapter(m_baseAdapter);
    }

    private BaseAdapter baseAdapter() {
        return new BaseAdapter() {
            @Override
            public int getCount() {
                return checkNumber();
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
            public View getView(int position, View convertView, ViewGroup parent) {
                View item = null;
                if (convertView == null) {
                    item = m_LayoutInflater.inflate(R.layout.layout_timetable_flight_resultcard, null);
                    setViewScaleDef(item);
                } else {
                    item = convertView;
                    item.findViewById(R.id.divider).setVisibility(VISIBLE);
                }
                if (position == getCount() - 1) {
                    item.findViewById(R.id.divider).setVisibility(VISIBLE);
                }
                setCardText(item, position);
                return item;
            }
        };
    }

    /*
    確定數量
     */
    private int checkNumber() {
        if (m_iViewType == DEF_VIEW_TYPE_FIGHT_STATUS) {
            return 1;
        } else {
            return timeTableHandle().size();
        }
    }

    /*
    挑選同一個group的item
     */
    private ArrayList<CIFlightStatus_infoEntity> flightStatusHandle(ArrayList<CIFlightStatus_infoEntity> input) {
        ArrayList<CIFlightStatus_infoEntity> output = new ArrayList<>();
        output.add(input.get(m_iPosition));
        return output;
    }

    private ArrayList<CITimeTable_InfoEntity> timeTableHandle() {
        ArrayList<CITimeTable_InfoEntity> output = new ArrayList<>();
        int groupNum = 0;
        for (int i = 0; i < m_arTimeTableItemInfoList.size(); i++) {
            if (m_arTimeTableItemInfoList.get(i).Status.equals(CITimeTable_InfoEntity.JOURNEY_STATUS_D)) {
                m_arGroupList.add(groupNum);
                groupNum++;
            } else if (m_arTimeTableItemInfoList.get(i).Status.equals(CITimeTable_InfoEntity.JOURNEY_STATUS_E)) {
                m_arGroupList.add(groupNum);
                groupNum++;
            } else {
                m_arGroupList.add(groupNum);
            }
        }
        for (int i = 0; i < m_arTimeTableItemInfoList.size(); i++) {
            if (m_arGroupList.get(i) == m_arGroupList.get(m_iPosition)) {
                output.add(m_arTimeTableItemInfoList.get(i));
            }
        }
        return output;
    }

    /*
    設定資料
     */
    public void setFlightStatusData(ArrayList<CIFlightStatus_infoEntity> inputItem) {
        if (inputItem != null) {
            this.m_arFlightStatusItemInfoList = inputItem;
            converData(inputItem);
        }
    }

    public void setTimeTableData(ArrayList<CITimeTable_InfoEntity> inputItem) {
        if (inputItem != null) {
            this.m_arTimeTableItemInfoList = inputItem;
        }
    }

    private void converData(ArrayList<CIFlightStatus_infoEntity> input) {
        ArrayList<CIFlightStatus_infoEntity> converData = new ArrayList<>();
        converData.add(input.get(m_iPosition));
        try {
            convertJson(converData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setCardText(View viewGroup, int position) {
        TextView flightNumber = (TextView) viewGroup.findViewById(R.id.flight_number_result);
        TextView startPlace = (TextView) viewGroup.findViewById(R.id.flight_start_place_result);
        TextView arrivalPlace = (TextView) viewGroup.findViewById(R.id.flight_arrival_place_result);
        TextView depature_terminal = (TextView) viewGroup.findViewById(R.id.flight_left_terminal_result);
        TextView arrival_terminal = (TextView) viewGroup.findViewById(R.id.flight_right_terminal_result);
        TextView flight_time = (TextView) viewGroup.findViewById(R.id.flight_flytime_result);
        TextView time_remaining = (TextView) viewGroup.findViewById(R.id.flight_flydate_result);
        TextView flight_type = (TextView) viewGroup.findViewById(R.id.flight_type_value);
        TextView actual_depart_time = (TextView) viewGroup.findViewById(R.id.startTime_left_textview_result_content);
        TextView actual_arrival_time = (TextView) viewGroup.findViewById(R.id.startTime_right_textview_result_content);
        TextView actual_depart_day = (TextView) viewGroup.findViewById(R.id.startPlace_left_textview_result_content);
        TextView actual_arrival_day = (TextView) viewGroup.findViewById(R.id.startPlace_right_textview_result_content);

        if (m_iViewType == DEF_VIEW_TYPE_FIGHT_STATUS) {
            ArrayList<CIFlightStatus_infoEntity> list = flightStatusHandle(m_arFlightStatusItemInfoList);
            //2016-06-21 modifly by ryan for 統一由 WS Model層解析為4碼, ui 不用特別處理
            flightNumber.setText(list.get(position).carrier + " " + list.get(position).flight_number);
            //
            startPlace.setText(list.get(position).depature_station_code);
            arrivalPlace.setText(list.get(position).arrival_station_code);

            RelativeLayout flightNumberContent = (RelativeLayout) viewGroup.findViewById(R.id.flight_number_result_content);
            flightNumberContent.setBackgroundResource(R.color.white_six);
            RelativeLayout iconLayout = (RelativeLayout) viewGroup.findViewById(R.id.flight_icon);
            RelativeLayout content2 = (RelativeLayout) viewGroup.findViewById(R.id.flight_time_content2);
            iconLayout.setBackgroundResource(R.drawable.timetable_result_tilte_bg_corner);
            iconLayout.setVisibility(VISIBLE);
            content2.setVisibility(VISIBLE);
            ImageView flightStatusImg = (ImageView) viewGroup.findViewById(R.id.flight_status_icon_resault);
            TextView flightStatusText = (TextView) viewGroup.findViewById(R.id.flight_status_text_resault);

            // 管理WS回傳的航班動態
            CIFlightStatusManager.transferIconAndTextByColorCode(
                    flightStatusImg,
                    flightStatusText,
                    list.get(position).color_code,
                    list.get(position).flight_status
            );
            // 設定航班動態改   CIFlightStatusManager

            TextView flightTime = (TextView) viewGroup.findViewById(R.id.flight_flytime);
            flightTime.setText(R.string.time_in_flight);
            TextView flightDatr = (TextView) viewGroup.findViewById(R.id.flight_flydate);
            flightDatr.setText(R.string.time_remaining);
            TextView leftText1 = (TextView) viewGroup.findViewById(R.id.scheduled_left_textview_result_content);
            leftText1.setText(getResources().getString(R.string.actual));
            TextView rightText1 = (TextView) viewGroup.findViewById(R.id.scheduled_right_textview_result_content);
            rightText1.setText(getResources().getString(R.string.actual));
            TextView leftText2 = (TextView) viewGroup.findViewById(R.id.scheduled_left_textview_result_content2);
            leftText2.setText(getResources().getString(R.string.scheduled));
            TextView rightText2 = (TextView) viewGroup.findViewById(R.id.scheduled_right_textview_result_content2);
            rightText2.setText(getResources().getString(R.string.scheduled));
            if (!TextUtils.isEmpty(list.get(position).depature_terminal)) {
                depature_terminal.setText(getResources().getString(R.string.terminal, list.get(position).depature_terminal));
            }
            if (!TextUtils.isEmpty(list.get(position).arrival_terminal)) {
                arrival_terminal.setText(getResources().getString(R.string.terminal, list.get(position).arrival_terminal));
            }
            flight_time.setText(timeFormat(list.get(position).flight_time));
            time_remaining.setText(timeFormat(list.get(position).time_remaining));
            String strTypeOfAircraft = list.get(position).AC_Type;
            if(TextUtils.isEmpty(strTypeOfAircraft)) strTypeOfAircraft = "";
            flight_type.setText(strTypeOfAircraft);

            //2016-06-29 ryan for 調整為由WS Model做時間判斷, ui改拿顯示用Tag
            rightText1.setText( list.get(position).strDisArrivalName );
            actual_arrival_time.setText(    list.get(position).strDisArrivalTime );
            actual_arrival_day.setText(     AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddEEE(list.get(position).strDisArrivalDate));
            leftText1.setText(  list.get(position).strDisDepartureName );
            actual_depart_time.setText(     list.get(position).strDisDepartureTime );
            actual_depart_day.setText(      AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddEEE(list.get(position).strDisDepartureDate));

            TextView std = (TextView) viewGroup.findViewById(R.id.startTime_left_textview_result_content2);
            std.setText(list.get(position).stdt);
            TextView sta = (TextView) viewGroup.findViewById(R.id.startTime_right_textview_result_content2);
            sta.setText(list.get(position).stat);
        } else {
            ArrayList<CITimeTable_InfoEntity> list = timeTableHandle();
            //2016-06-21 modifly by ryan for 統一由 WS Model層解析為4碼, ui 不用特別處理
            flightNumber.setText(list.get(position).company + " " + list.get(position).flight_number);
            startPlace.setText(list.get(position).departure_air_port);
            arrivalPlace.setText(list.get(position).arrival_air_port);

            if (!list.get(position).departure_terimal.equals("")) {
                depature_terminal.setText(getResources().getString(R.string.terminal, list.get(position).departure_terimal));
            }
            if (!list.get(position).arrival_terimal.equals("")) {
                arrival_terminal.setText(getResources().getString(R.string.terminal, list.get(position).arrival_terimal));
            }
            flight_time.setText(timeTableDateFormat(list.get(position).leg_duration));
            time_remaining.setText(formatWeek_Day(list.get(position).day_of_operation));
            String strTypeOfAircraft = list.get(position).type_of_aircraft;
            if(TextUtils.isEmpty(strTypeOfAircraft)) strTypeOfAircraft = "";
            flight_type.setText(strTypeOfAircraft);
            actual_depart_time.setText(list.get(position).departure_time);
            actual_arrival_time.setText(list.get(position).arrival_time);
            //2017-08-02 統一時間格式的邏輯
            actual_depart_day.setText( AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddEEE(list.get(position).departure_date));
            actual_arrival_day.setText(AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddEEE(list.get(position).arrival_date));

            //2017-03-02 CR TimeTable新增顯示承運廠商
            String strOperating;
            if (TextUtils.isEmpty(list.get(position).operating_Company)){
                strOperating = "";
            }else {
                strOperating = list.get(position).operating_Company;
            }

            RelativeLayout rlOperating = (RelativeLayout) viewGroup.findViewById(R.id.rl_operating);
            rlOperating.setVisibility(VISIBLE);

            TextView tvOperatingCompany = (TextView) viewGroup.findViewById(R.id.tv_operating);
            tvOperatingCompany.setText(strOperating);

        }
    }

    /*
          自適鹰
            */
    private void setViewScaleDef(View item) {
        ViewScaleDef viewScaleDef = ViewScaleDef.getInstance(m_context);
        viewScaleDef.setPadding(item, 10, 0, 10, 0);
        viewScaleDef.selfAdjustAllView(item.findViewById(R.id.flight_number_result_content));
        viewScaleDef.selfAdjustAllView(item.findViewById(R.id.flight_time_content));
        viewScaleDef.selfAdjustAllView(item.findViewById(R.id.timetable_bottom));
        viewScaleDef.selfAdjustAllView(item.findViewById(R.id.divider));
        if (m_iViewType == DEF_VIEW_TYPE_FIGHT_STATUS) {
            viewScaleDef.selfAdjustAllView(item.findViewById(R.id.flight_icon));
        }
    }

    //判斷從TimeTable  或 Flight Status 進入
    public void setViewType(int iViewType) {
        this.m_iViewType = iViewType;
    }


    private void setHeader() {
        //2016-07-20 Ling 根據zeplin規格設定header高度

        RelativeLayout layout = new RelativeLayout(m_context);
        //2017.1.4 CR - 時刻表地圖背景的高度，統一調整為同Trip Detail的高度
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewScaleDef.getInstance(m_context).getLayoutHeight(300));
        layout.setLayoutParams(params);
        layout.setBackgroundResource(R.color.transparent);
        addHeaderView(layout);
    }

    //把拿到的資料轉換成顯示字串格式
    private String timeFormat(String time) {
        int timeInt = Integer.parseInt(time);
        int hour;
        int min;
        String result = "";
        if (time != null) {
            hour = timeInt / 60;
            min = timeInt % 60;
            if (hour == 0) {
                result = getResources().getString(R.string.minute_unit, min);
            } else {
                result = getResources().getString(R.string.hours_unit, hour) + " " + getResources().getString(R.string.minute_unit, min);
            }
        }
        return result;
    }

    private String timeTableDateFormat(String date) {
        String resault = "";
        if (date != null) {
            String[] dateSplite = date.split(":");
            if (dateSplite.length == 2) {
                resault = getResources().getString(R.string.hours_unit, Integer.parseInt(dateSplite[0])) + " " + getResources().getString(R.string.minute_unit, Integer.parseInt(dateSplite[1]));
            }
        }
        return resault;
    }

    /*
    數字轉換成String
     */
    private String formatWeek_Day(String date) {
        //
        //char[] day = date.toCharArray();
        String output = "";
        if (date != null) {
            char[] day = date.toCharArray();
            for (int i = 0; i < day.length; i++) {
                if (day[i] == '1') {
                    output = output + getResources().getString(R.string.weather_day_Mon);
                } else if (day[i] == '2') {
                    output = output + getResources().getString(R.string.weather_day_Tue);
                } else if (day[i] == '3') {
                    output = output + getResources().getString(R.string.weather_day_Wed);
                } else if (day[i] == '4') {
                    output = output + getResources().getString(R.string.weather_day_Thu);
                } else if (day[i] == '5') {
                    output = output + getResources().getString(R.string.weather_day_Fri);
                } else if (day[i] == '6') {
                    output = output + getResources().getString(R.string.weather_day_Sat);
                } else if (day[i] == '7') {
                    output = output + getResources().getString(R.string.weather_day_Sun);
                } else {
                    output = output + "";
                }
                if (i != day.length - 1) {
                    output = output + " / ";
                }
            }
        }
        return output;

    }

    /*
    將資料轉換成json儲存
     */
    private void convertJson(ArrayList<CIFlightStatus_infoEntity> input) throws JSONException {
        Gson gson = new Gson();
        m_strTrackData = gson.toJson(input);
    }

    /*
    獲得儲存的json
     */
    public String getTrackData() {
        return m_strTrackData;
    }
}
