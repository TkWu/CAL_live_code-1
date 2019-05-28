package ci.ui.TimeTable;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.object.CIFlightStatusManager;
import ci.ui.view.DashedLine;
import ci.ws.Models.entities.CIFlightStatus_infoEntity;
import ci.ws.Models.entities.CITimeTableDayOfFlightResp;
import ci.ws.Models.entities.CITimeTable_InfoEntity;

/**
 * Created by user on 2016/3/15.
 */
public class CIATtimeTableList extends ListView {

    private class IsHideFullWeek{

        boolean bIsHide;

        IsHideFullWeek(){
            //bIsHide = true;
            bIsHide = false;
        }
    }

    private ViewScaleDef    m_viewScaleDef;
    private LayoutInflater  m_layoutInflater;
    private Context         m_Context;

    private ArrayList<CIFlightStatus_infoEntity>    m_arFlightStatusItemInfo = new ArrayList<>();
    private ArrayList<CITimeTable_InfoEntity>       m_arTimeTableItemInfo = new ArrayList<>();

    private BaseAdapter     m_baseAdapter;
    private int             m_iViewType;
    private LinearLayout    m_llayout_header;
    private int             m_Stop = 0;

    private LinearLayout    m_llayout_date = null;

    //是否展開/收合FullWeek列
    private ArrayList<IsHideFullWeek> m_alHide = new ArrayList<>();

    private static final int VIEW_FROM_FIGHT_STATUS = CITimeTableFlightStatusDetailList.DEF_VIEW_TYPE_FIGHT_STATUS;
    private static final int VIEW_FROM_TIME_TABLE   = CITimeTableFlightStatusDetailList.DEF_VIEW_TYPE_TIME_TABLE;

    public CIATtimeTableList(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.m_Context = context;
        m_viewScaleDef = ViewScaleDef.getInstance(context);
        m_layoutInflater = LayoutInflater.from(context);
        setVerticalScrollBarEnabled(false);
        setDivider(null);
        m_baseAdapter = baseAdapter();
        setHeader();
        setAdapter(m_baseAdapter);
    }

    public BaseAdapter baseAdapter() {
        return new BaseAdapter() {
            @Override
            public int getCount() {
                if (m_iViewType == VIEW_FROM_FIGHT_STATUS) {
                    return m_arFlightStatusItemInfo.size();

                } else if (m_iViewType == VIEW_FROM_TIME_TABLE) {
                    return m_arTimeTableItemInfo.size();
                }
                return 0;
            }

            @Override
            public Object getItem(int position) {
                //return null;
                if (m_iViewType == VIEW_FROM_FIGHT_STATUS) {
                    return m_arFlightStatusItemInfo.get(position);

                } else if (m_iViewType == VIEW_FROM_TIME_TABLE) {
                    return m_arTimeTableItemInfo.get(position);
                } else {
                    return null;
                }
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View card = null;
                if (convertView == null) {
                    //2016-02-15 CR Timetable UI 修改
                    card = m_layoutInflater.inflate(R.layout.layout_timetable_flight_card, null);

                    setViewScaleDef(card);
                } else {
                    card = convertView;
                    ViewGroup cardTitle = (ViewGroup) card.findViewById(R.id.flight_title);
                    cardTitle.setBackgroundResource(R.color.white_four);
                    ViewGroup cardContent = (ViewGroup) card.findViewById(R.id.card_content);
                    ViewGroup cardContentBg = (ViewGroup) card.findViewById(R.id.card_content_bg);
                    cardContent.setBackgroundResource(R.color.white_six);
                    cardContentBg.setBackgroundResource(R.color.white_four);
                }
                ViewGroup dash = (ViewGroup) card.findViewById(R.id.dash_part);

                ViewGroup divider = (ViewGroup) card.findViewById(R.id.divider);

                dash.setVisibility(GONE);

                //增加航班的飛行日期，固定顯示七天，預設顯示未展開，點擊Full Week 展開飛行日期，再次點擊則收合飛行日期
                //day_of_flight的jsonObject 最多顯示七天，如果Server出現異常提供超過七天則只抓取前七天，如果不滿七天則依照Serve提供的天數顯示
                //如果day_of_flight 回空的或者是沒有這個tag, 則 不顯示 Full Week 該列，所以牌卡將長的跟原來一樣
                //day_of_fligh的資訊請抓S這張牌卡，其餘非轉機的航班就是抓D

                //測試資料
//                ArrayList<CITimeTableDayOfFlightResp> alDayOfFlight = new ArrayList<>();
//                for (int i = 0; i < 7; i ++){
//                    CITimeTableDayOfFlightResp resp = new CITimeTableDayOfFlightResp();
//                    resp.date = "2017-03-0" + (i+1);
//                    if (i % 3 != 0){
//                        resp.is_flight = CITimeTableDayOfFlightResp.IS_FLIGHT_Y;
//                    }else {
//                        resp.is_flight = CITimeTableDayOfFlightResp.IS_FLIGHT_N;
//                    }
//                    alDayOfFlight.add(resp);
//                }

//               ArrayList<CITimeTableDayOfFlightResp> alDayOfFlight = m_arTimeTableItemInfo.get(position).day_of_flight;

                ViewGroup vFullweek = (ViewGroup) card.findViewById(R.id.ll_fullweek);
                ViewGroup vText = (ViewGroup) card.findViewById(R.id.ll_text_week);
                ViewGroup vIcon = (ViewGroup) card.findViewById(R.id.ll_icon_week);
                ViewGroup vWeek = (ViewGroup) card.findViewById(R.id.ll_week);

                vText.removeAllViews();
                vIcon.removeAllViews();

                if (m_iViewType == VIEW_FROM_FIGHT_STATUS) {
                    ViewGroup cardTitle = (ViewGroup) card.findViewById(R.id.flight_title);
                    cardTitle.setBackgroundResource(R.drawable.timetable_title_bg_corner);
                    ViewGroup cardContent = (ViewGroup) card.findViewById(R.id.card_content);
                    cardContent.setBackgroundResource(R.drawable.timetable_content_bg_corner);
                    ViewGroup cardContentBg = (ViewGroup) card.findViewById(R.id.card_content_bg);
                    cardContentBg.setBackgroundResource(R.drawable.bg_timetable_content_border_corner);

                    vFullweek.setVisibility(View.GONE);
                    vText.setVisibility(View.GONE);
                    vWeek.setVisibility(View.GONE);
                } else {

                    ArrayList<CITimeTableDayOfFlightResp> alDayOfFlight = m_arTimeTableItemInfo.get(position).day_of_flight;

                    TextView layover = (TextView) dash.findViewById(R.id.arrival_time);

                    //2017-03-02 CR TimeTable新增顯示承運廠商 若Server 未回傳資料請顯示空白 如： 由  承運
                    String strOperating;
                    if (TextUtils.isEmpty(m_arTimeTableItemInfo.get(position).operating_Company)){
                        strOperating = String.format(
                                getResources().getString(R.string.timetable_operated_by),
                                " ");
                    }else {
                        strOperating = String.format(
                                getResources().getString(R.string.timetable_operated_by),
                                m_arTimeTableItemInfo.get(position).operating_Company);
                    }
                    ViewGroup cardOperating = (ViewGroup) card.findViewById(R.id.ll_operating);
                    cardOperating.setVisibility(VISIBLE);
                    TextView tvOperating = (TextView) card.findViewById(R.id.tv_operating);
                    tvOperating.setText(strOperating);

                    //2016-09-08 modify by ryan for 修正轉機時間計算錯誤的問題，轉機時間應該抓取該班抵達時間以及下一班出發時間的時間差
                    if (m_arTimeTableItemInfo.get(position).Status.equals(CITimeTable_InfoEntity.JOURNEY_STATUS_S)) {
                        //確保有下一張行班牌卡
                        if ( position + 1 < getCount() ){
                            m_Stop = 1; //轉機點的計算，S代表起始點一律從1開始，代表一個轉機點
                            CalculateOverStopTime(layover, m_Stop, m_arTimeTableItemInfo.get(position+1), m_arTimeTableItemInfo.get(position) );
                        }
                        //
                        ViewGroup cardTitle = (ViewGroup) card.findViewById(R.id.flight_title);
                        cardTitle.setBackgroundResource(R.drawable.timetable_title_bg_corner);
                        dash.setVisibility(VISIBLE);
                        divider.setVisibility(GONE);

                        vFullweek.setVisibility(View.GONE);
                        vText.setVisibility(View.GONE);
                        vWeek.setVisibility(View.GONE);
                    } else if (m_arTimeTableItemInfo.get(position).Status.equals(CITimeTable_InfoEntity.JOURNEY_STATUS_C)) {
                        //確保有下一張行班牌卡
                        if ( position + 1 < getCount() ){
                            m_Stop++; //轉機點的計算，C代表中間的轉機點，所以計算上要加1
                            CalculateOverStopTime(layover, m_Stop, m_arTimeTableItemInfo.get(position+1), m_arTimeTableItemInfo.get(position) );
                        }
                        //
                        dash.setVisibility(VISIBLE);
                        divider.setVisibility(GONE);

                        vFullweek.setVisibility(View.GONE);
                        vText.setVisibility(View.GONE);
                        vWeek.setVisibility(View.GONE);
                    } else if (m_arTimeTableItemInfo.get(position).Status.equals(CITimeTable_InfoEntity.JOURNEY_STATUS_E)) {
                        ViewGroup cardContent = (ViewGroup) card.findViewById(R.id.card_content);
                        cardContent.setBackgroundResource(R.drawable.timetable_content_bg_corner);
                        ViewGroup cardContentBg = (ViewGroup) card.findViewById(R.id.card_content_bg);
                        cardContentBg.setBackgroundResource(R.drawable.bg_timetable_content_border_corner);
                        dash.setVisibility(GONE);
                        divider.setVisibility(VISIBLE);

                        setFullWeekView(position, card, alDayOfFlight);

                    }else {
                        ViewGroup cardContent = (ViewGroup) card.findViewById(R.id.card_content);
                        cardContent.setBackgroundResource(R.drawable.timetable_content_bg_corner);
                        ViewGroup cardContentBg = (ViewGroup) card.findViewById(R.id.card_content_bg);
                        cardContentBg.setBackgroundResource(R.drawable.bg_timetable_content_border_corner);
                        ViewGroup cardTitle = (ViewGroup) card.findViewById(R.id.flight_title);
                        cardTitle.setBackgroundResource(R.drawable.timetable_title_bg_corner);

                        dash.setVisibility(GONE);
                        divider.setVisibility(VISIBLE);

                        setFullWeekView(position, card, alDayOfFlight);
                    }
                }
                setCardText(card, position);
                return card;
            }
        };
    }

    public void setFlightStatusData(ArrayList<CIFlightStatus_infoEntity> inputItem) {
        if (inputItem != null) {
            this.m_arFlightStatusItemInfo = inputItem;
            m_baseAdapter.notifyDataSetChanged();
        }
    }

    public void setTimeTableData(ArrayList<CITimeTable_InfoEntity> inputItem) {
        if (inputItem != null) {
            this.m_arTimeTableItemInfo = inputItem;

            m_alHide.clear();
            for (int i = 0; i < m_arTimeTableItemInfo.size(); i ++ ){
                m_alHide.add(new IsHideFullWeek());
            }

            m_baseAdapter.notifyDataSetChanged();
        }
    }

    public void CalculateOverStopTime( TextView vlayover, int iStop, CITimeTable_InfoEntity NextdepartureInfo, CITimeTable_InfoEntity LastArrivalInfo ){

        String strNextdepart = NextdepartureInfo.departure_date_gmt + " " + NextdepartureInfo.departure_time_gmt;
        String strLastarrival = LastArrivalInfo.arrival_date_gmt + " " + LastArrivalInfo.arrival_time_gmt;
        String[] time = setLayover(strNextdepart, strLastarrival);

        String hour = getContext().getString(R.string.hours_unit, Integer.parseInt(time[0]));
        String min = getResources().getString(R.string.minute_unit, Integer.parseInt(time[1]));
        String layoverStop = getContext().getString(R.string.my_trips_layover, iStop, hour + " " + min);
        vlayover.setText(layoverStop);
    }

    private void setFullWeekView(final int position, View card, ArrayList<CITimeTableDayOfFlightResp> alDayOfFlight){
        ViewGroup cardContentBg = (ViewGroup) card.findViewById(R.id.card_content_bg);
        final ViewGroup vFullweek = (ViewGroup) card.findViewById(R.id.ll_fullweek);
        final ViewGroup vText = (ViewGroup) card.findViewById(R.id.ll_text_week);
        final ViewGroup vIcon = (ViewGroup) card.findViewById(R.id.ll_icon_week);
        final ViewGroup vWeek = (ViewGroup) card.findViewById(R.id.ll_week);
        final DashedLine dl = (DashedLine) card.findViewById(R.id.dl_bottom);

        if ( null != alDayOfFlight && 0 < alDayOfFlight.size() ){
            cardContentBg.setBackgroundColor(ContextCompat.getColor(m_Context, R.color.white_six));

            vFullweek.setVisibility(VISIBLE);
            vFullweek.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( true == m_alHide.get(position).bIsHide ){
                        vFullweek.setBackgroundColor(ContextCompat.getColor(m_Context, R.color.white_six));
                        vText.setVisibility(VISIBLE);
                        vWeek.setVisibility(VISIBLE);
                        dl.setVisibility(VISIBLE);
                        m_alHide.get(position).bIsHide = false;
                    }else {
                        vFullweek.setBackgroundResource(R.drawable.timetable_content_bg_corner);
                        vText.setVisibility(GONE);
                        vWeek.setVisibility(GONE);
                        dl.setVisibility(GONE);
                        m_alHide.get(position).bIsHide = true;
                    }
                }
            });
            if ( true == m_alHide.get(position).bIsHide ){
                vFullweek.setBackgroundResource(R.drawable.timetable_content_bg_corner);
                vText.setVisibility(GONE);
                vWeek.setVisibility(GONE);
                dl.setVisibility(GONE);
            }else {
                vFullweek.setBackgroundColor(ContextCompat.getColor(m_Context, R.color.white_six));
                vText.setVisibility(VISIBLE);
                vWeek.setVisibility(VISIBLE);
                dl.setVisibility(VISIBLE);
            }

            int iWidth = m_viewScaleDef.getLayoutWidth(336) / alDayOfFlight.size();
            for( int i = 0; i < alDayOfFlight.size(); i ++ ){
                View text = m_layoutInflater.inflate(R.layout.view_timetable_week_text, null);
                vText.addView(text);

                TextView tvDay = (TextView)text.findViewById(R.id.tv_day);
                tvDay.setText(AppInfo.getInstance(m_Context).ConvertDateFormatByEEE(alDayOfFlight.get(i).date));
                TextView tvDate = (TextView)text.findViewById(R.id.tv_date);
                tvDate.setText(AppInfo.getInstance(m_Context).ConvertDateFormatBydd(alDayOfFlight.get(i).date));

                View icon = m_layoutInflater.inflate(R.layout.view_timetable_week_icon, null);
                vIcon.addView(icon);

                ImageView imageView = (ImageView)icon.findViewById(R.id.icon);

                if (CITimeTableDayOfFlightResp.IS_FLIGHT_Y.equals(alDayOfFlight.get(i).is_flight)){
                    imageView.setImageResource(R.drawable.ic_timetable_flight);
                }else {
                    imageView.setImageResource(R.drawable.ic_timetable_flight_none);
                }

                m_viewScaleDef.selfAdjustAllView(text);
                m_viewScaleDef.selfAdjustAllView(icon);
                m_viewScaleDef.selfAdjustSameScaleView(imageView, 27, 27);
                tvDate.getLayoutParams().width = iWidth;
                tvDay.getLayoutParams().width = iWidth;
                (icon.findViewById(R.id.ll_icon)).getLayoutParams().width = iWidth;

            }
        }else {
            vFullweek.setVisibility(GONE);
            vText.setVisibility(GONE);
            vWeek.setVisibility(GONE);
        }
    }

    /*
    設定文字
     */
    private void setCardText(View viewGroup, int position) {
        TextView flightNumber = (TextView) viewGroup.findViewById(R.id.flight_number);
        TextView startTime = (TextView) viewGroup.findViewById(R.id.startTime_left_textview);
        TextView arrivalTime = (TextView) viewGroup.findViewById(R.id.arrivalTime_right_textview);
        TextView startPlace = (TextView) viewGroup.findViewById(R.id.startPlace_left_textview);
        TextView arrivalPlace = (TextView) viewGroup.findViewById(R.id.arrivalPlace_right_textview);
        ImageView statusIconImg = (ImageView) viewGroup.findViewById(R.id.flight_icon);
        TextView statusText = (TextView) viewGroup.findViewById(R.id.flight_status_text);
        TextView scheduledLeft = (TextView) viewGroup.findViewById(R.id.scheduled_left_textview);
        TextView scheduledRight = (TextView) viewGroup.findViewById(R.id.scheduled_right_textview);
        TextView tomorrowTag = (TextView) viewGroup.findViewById(R.id.tv_tomorrow_tag);
        AppInfo appInfo              = AppInfo.getInstance(getContext());

        String strDepartureDate = "";
        String strArrivalDate = "";


        if (m_iViewType == VIEW_FROM_FIGHT_STATUS) {
            //2016-06-21 modifly by ryan for 統一由 WS Model層解析為4碼, ui 不用特別處理
            flightNumber.setText(m_arFlightStatusItemInfo.get(position).carrier + " " + m_arFlightStatusItemInfo.get(position).flight_number);
            //  判斷實際 或 預計

            //2016-06-29 ryan for 調整為由WS Model做時間判斷, ui改拿顯示用Tag
            //表定或實際由WS Model曾判斷
            scheduledRight.setText( m_arFlightStatusItemInfo.get(position).strDisArrivalName );
            arrivalTime.setText(    m_arFlightStatusItemInfo.get(position).strDisArrivalTime );
            scheduledLeft.setText(  m_arFlightStatusItemInfo.get(position).strDisDepartureName );
            startTime.setText(     m_arFlightStatusItemInfo.get(position).strDisDepartureTime );

            strDepartureDate = m_arFlightStatusItemInfo.get(position).strDisDepartureDate;
            strArrivalDate = m_arFlightStatusItemInfo.get(position).strDisArrivalDate;

            startPlace.setText(m_arFlightStatusItemInfo.get(position).depature_station_desc);
            arrivalPlace.setText(m_arFlightStatusItemInfo.get(position).arrival_station_desc);
            statusIconImg.setVisibility(VISIBLE);
            statusText.setVisibility(VISIBLE);

            // 管理WS回傳的航班動態
            CIFlightStatusManager.transferIconAndTextByColorCode(
                    statusIconImg,
                    statusText,
                    m_arFlightStatusItemInfo.get(position).color_code,
                    m_arFlightStatusItemInfo.get(position).flight_status
            );
            // 設定航班動態改   CIFlightStatusManager

        } else if (m_iViewType == VIEW_FROM_TIME_TABLE) {

            //2016-08-02 TimeTable應該顯示表定時間
            scheduledLeft.setText(getContext().getString(R.string.scheduled));
            scheduledRight.setText(getContext().getString(R.string.scheduled));

            //2016-06-21 modifly by ryan for 統一由 WS Model層解析為4碼, ui 不用特別處理
            flightNumber.setText(m_arTimeTableItemInfo.get(position).company + " " + m_arTimeTableItemInfo.get(position).flight_number);
            startTime.setText(m_arTimeTableItemInfo.get(position).departure_time);
            arrivalTime.setText(m_arTimeTableItemInfo.get(position).arrival_time);
            startPlace.setText(m_arTimeTableItemInfo.get(position).departure_name);
            arrivalPlace.setText(m_arTimeTableItemInfo.get(position).arrival_name);
            statusIconImg.setVisibility(GONE);
            statusText.setVisibility(GONE);

            strDepartureDate = m_arTimeTableItemInfo.get(position).departure_date;
            strArrivalDate = m_arTimeTableItemInfo.get(position).arrival_date;

        }

        String day = appInfo.getShowTomorrowDay(strDepartureDate, strArrivalDate);
        tomorrowTag.setText(day);
    }

    /*
    判斷進入的activity
     */
    public void setFromClass(int fromClass) {
        this.m_iViewType = fromClass;
    }


    private void setHeader() {
        m_llayout_header = (LinearLayout) m_layoutInflater.inflate(R.layout.layout_timetable_header, null);
        m_viewScaleDef.selfAdjustAllView(m_llayout_header.findViewById(R.id.country_name));
        TextView tvFrom = (TextView) m_llayout_header.findViewById(R.id.tv_country_from_detail);
        TextView tvTo = (TextView) m_llayout_header.findViewById(R.id.tv_country_to_detail);
        tvFrom.setMinimumHeight(m_viewScaleDef.getLayoutHeight(19.3));
        tvFrom.setMaxHeight(m_viewScaleDef.getLayoutHeight(38.7));
        tvTo.setMinimumHeight(m_viewScaleDef.getLayoutHeight(19.3));
        tvTo.setMaxHeight(m_viewScaleDef.getLayoutHeight(38.7));

        tvFrom.setMaxWidth(m_viewScaleDef.getLayoutWidth(150));
        tvTo.setMaxWidth(m_viewScaleDef.getLayoutWidth(150));

        //
        m_llayout_date = (LinearLayout) m_llayout_header.findViewById(R.id.llayout_date);
        m_viewScaleDef.selfAdjustAllView(m_llayout_date);

        //m_viewScaleDef.selfAdjustAllView(m_llayout_header.findViewById(R.id.llayout_date));
        m_viewScaleDef.selfAdjustAllView(m_llayout_header.findViewById(R.id.bottom_divider));
        m_viewScaleDef.selfAdjustSameScaleView(m_llayout_header.findViewById(R.id.airplane_image), 40, 40);
        m_llayout_header.setBackgroundResource(R.color.transparent);
        m_llayout_header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        addHeaderView(m_llayout_header);
    }

    /*
    自適應
     */
    private void setViewScaleDef(View item) {

        m_viewScaleDef.setPadding(item, 10, 0, 10, 0);
        m_viewScaleDef.selfAdjustAllView(item.findViewById(R.id.flight_title));
        m_viewScaleDef.selfAdjustAllView(item.findViewById(R.id.rl_operating));
        m_viewScaleDef.selfAdjustAllView(item.findViewById(R.id.card_content_bg));
        m_viewScaleDef.selfAdjustAllView(item.findViewById(R.id.dash_part_bg));
        m_viewScaleDef.selfAdjustAllView(item.findViewById(R.id.divider));

        if (m_iViewType == VIEW_FROM_TIME_TABLE) {
            m_viewScaleDef.selfAdjustAllView(item.findViewById(R.id.ll_fullweek));
            m_viewScaleDef.selfAdjustAllView(item.findViewById(R.id.ll_text_week));
            m_viewScaleDef.selfAdjustAllView(item.findViewById(R.id.ll_week));

            int i = m_viewScaleDef.getLayoutHeight(1);
            if ( i < 1 ){
                i = 1;
            }
            item.findViewById(R.id.dl_top).getLayoutParams().height = i;
            item.findViewById(R.id.dl_bottom).getLayoutParams().height = i;
//            item.findViewById(R.id.dl_week).getLayoutParams().height = i;
        }
    }

    /*
    設定 depart文字
     */
    public void setTextContent(String strText) {
        //2017-03-28 modifly by ryan for 如果沒有設定出發日或飛行日, 則不顯示
        //TimeTable 不顯示出發日
        if ( TextUtils.isEmpty(strText) ){
            m_llayout_date.setVisibility(View.INVISIBLE);
        } else {
            m_llayout_date.setVisibility(View.VISIBLE);
            TextView text = (TextView) m_llayout_header.findViewById(R.id.tv_depart_date);
            text.setText(strText);
        }
    }

    //計算下班飛機的出發時間距離上班飛機的抵達時間的時間差
    private String[] setLayover(String NextdepartTime, String LastarrivalTime) {
        String[] resault = new String[2];

        if (NextdepartTime != null && LastarrivalTime != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Long offset;
            try {
                Date NextdepartDate = format.parse(NextdepartTime);
                Date LastarrivalDate = format.parse(LastarrivalTime);
                //下班飛機的出發時間一定會大於上班飛機的抵達時間
                //offset = arrivalDate.getTime() - departDate.getTime();
                offset = (NextdepartDate.getTime() - LastarrivalDate.getTime()) / 1000;
                //Long min = (offset / (1000 * 60)) % 60;
                Long min = (offset % (60 * 60)) / 60 ;
                Long hour = offset / (60 * 60);
                resault[0] = hour.toString();
                resault[1] = min.toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return resault;
    }

}



