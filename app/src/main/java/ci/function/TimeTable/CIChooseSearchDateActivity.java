package ci.function.TimeTable;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.ui.Calendar.CalView;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.view.NavigationBar;

import static ci.ui.TextField.CIChooseSearchDateTextFieldFragment.BUNDLE_PARA_BOOLEAN_IS_SINGLE;
import static ci.ui.TextField.CIChooseSearchDateTextFieldFragment.BUNDLE_PARA_BOOLEAN_IS_START;
import static ci.ui.TextField.CIChooseSearchDateTextFieldFragment.BUNDLE_PARA_LONG_DATE;
import static ci.ui.TextField.CIChooseSearchDateTextFieldFragment.BUNDLE_PARA_LONG_DEPARTURE_DATE;
import static ci.ui.TextField.CIChooseSearchDateTextFieldFragment.BUNDLE_PARA_LONG_RETURN_DATE;
import static ci.ui.TextField.CIChooseSearchDateTextFieldFragment.BUNDLE_PARA_STRING_DEPARTURE_AIRPORT;
import static ci.ui.TextField.CIChooseSearchDateTextFieldFragment.BUNDLE_PARA_STRING_DESTINATION_AIRPORT;

/**
 * Created by flowmahuang on 2016/3/16.
 */
public class CIChooseSearchDateActivity extends BaseActivity {

    private TextView        m_tvTitleLeft   = null;
    private TextView        m_tvTitleRight  = null;
    private TextView        m_tvDetailLeft  = null;
    private TextView        m_tvDetailRight = null;
    private TextView        m_tvTimeLeft    = null;
    private TextView        m_tvTimeRight   = null;

    private NavigationBar   m_Navigationbar = null;

    private ListView        m_lvCalendarPick = null;

    private ImageView       m_tvTitleImage  = null;
    private Intent          m_putIntent     = null;

    private ViewScaleDef    m_vScaleDef;

    private int             m_iTotalMonth   = 12;

    private BaseAdapter     m_baseAdapter = null;

    private ArrayList<CalView>          m_arCalList;
    private ArrayList<RelativeLayout>   m_arrLayoutList;

    private int clickYear = 0, clickMonth = 0, clickDay = 0;
    private int backYear = 0, backMonth = 0, backDay = 0;
    private int startYear = 0, startMonth = 0, startDay = 0;
    private int endYear = 0, endMonth = 0, endDay = 0;

    private boolean m_bIsStart  = true;
    private boolean m_bIsSingle = false;

    private final String DATE_FORMAT_3 = "MMM, yyyy";

    private Long    m_lDeparture            = 0L;
    private Long    m_lReturnDeparture      = 0L;
    private String  m_strDepartureAirport   = "";
    private String  m_strDestinationAirport = "";

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_search_date;
    }

    @Override
    protected void initialLayoutComponent() {
        m_tvTitleLeft = (TextView) findViewById(R.id.title_text_left);
        m_tvTitleRight = (TextView) findViewById(R.id.title_text_right);
        m_tvDetailLeft = (TextView) findViewById(R.id.title_name_left);
        m_tvDetailRight = (TextView) findViewById(R.id.title_name_right);
        m_tvTimeLeft = (TextView) findViewById(R.id.title_date_left);
        m_tvTimeRight = (TextView) findViewById(R.id.title_date_right);
        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_lvCalendarPick = (ListView) findViewById(R.id.calendar_view);
        m_tvTitleImage = (ImageView) findViewById(R.id.title_flight_image);

        m_putIntent = getIntent();

        setStartAndBackDate();

        m_vScaleDef = ViewScaleDef.getInstance(m_Context);

        m_arCalList = new ArrayList<>();
        m_arrLayoutList = new ArrayList<>();

//        String dt = "2008-01-01";  // Start date
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        //2017-02-09 CR 需顯示當天到未來361天日期的月曆, 故不可僅顯示12個月份
        //根據現在時間計算
        Calendar calendar = Calendar.getInstance();
        //取現在月份
        int iNowMonth = calendar.get(Calendar.MONTH) + 1;
        //取361天後的日期
        calendar.add(Calendar.DATE, 361);
        //取一年後的月份
        int iEndMonth = calendar.get(Calendar.MONTH) + 1 + 12;
        //顯示幾個月數的月曆
        m_iTotalMonth = iEndMonth - iNowMonth + 1;

        //取一年後的日期
        endYear = calendar.get(Calendar.YEAR);
        endMonth = calendar.get(Calendar.MONTH);
        endDay = calendar.get(Calendar.DATE);

        addCalendar();

        setCalendar();

        setListView();
    }

    private void setStartAndBackDate() {

        Bundle bundle = m_putIntent.getExtras();

        Calendar calendar = Calendar.getInstance();

        //判斷出回日
        m_bIsStart = bundle.getBoolean(BUNDLE_PARA_BOOLEAN_IS_START);
        //判來回程
        m_bIsSingle = bundle.getBoolean(BUNDLE_PARA_BOOLEAN_IS_SINGLE);

        //拿取時刻表的值
        m_lDeparture = bundle.getLong(BUNDLE_PARA_LONG_DEPARTURE_DATE);
        m_lReturnDeparture = bundle.getLong(BUNDLE_PARA_LONG_RETURN_DATE);
        m_strDepartureAirport = bundle.getString(BUNDLE_PARA_STRING_DEPARTURE_AIRPORT);
        m_strDestinationAirport = bundle.getString(BUNDLE_PARA_STRING_DESTINATION_AIRPORT);

        if (m_lDeparture > 0 && m_lDeparture != null) {//當有資料時

            calendar.setTimeInMillis(m_lDeparture);

            m_tvTimeLeft.setText(AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddEEE(calendar));

            //設定出發日期
            startYear = calendar.get(Calendar.YEAR);
            startMonth = calendar.get(Calendar.MONTH);
            startDay = calendar.get(Calendar.DATE);
        } else {

            Calendar today = Calendar.getInstance();

            m_tvTimeLeft.setText(AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddEEE(today));

            startYear = calendar.get(Calendar.YEAR);
            startMonth = calendar.get(Calendar.MONTH);
            startDay = calendar.get(Calendar.DATE);
        }

        if (m_lReturnDeparture > 0 && m_lReturnDeparture != null && !m_bIsSingle) {//當有資料時 && 判斷是否來回時

            calendar.setTimeInMillis(m_lReturnDeparture);

            m_tvTimeRight.setText(AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddEEE(calendar));

            //設定回程
            backYear = calendar.get(Calendar.YEAR);
            backMonth = calendar.get(Calendar.MONTH);
            backDay = calendar.get(Calendar.DATE);

        } else {
            backYear = 0;
            backMonth = 0;
            backDay = 0;
        }


        setTimeTableValue();
    }

    private void setTimeTableValue() {

        int leftBrackets, rightBrackets;

        leftBrackets = m_strDepartureAirport.lastIndexOf("(");
        rightBrackets = m_strDepartureAirport.lastIndexOf(")");

        if (leftBrackets != -1 && rightBrackets != -1) {
            m_tvTitleLeft.setText(m_strDepartureAirport.substring(leftBrackets + 1, rightBrackets));
            m_tvDetailLeft.setText(m_strDepartureAirport.substring(0, leftBrackets));
        }

        leftBrackets = m_strDestinationAirport.lastIndexOf("(");
        rightBrackets = m_strDestinationAirport.lastIndexOf(")");

        if (leftBrackets != -1 && rightBrackets != -1) {
            m_tvTitleRight.setText(m_strDestinationAirport.substring(leftBrackets + 1, rightBrackets));
            m_tvDetailRight.setText(m_strDestinationAirport.substring(0, leftBrackets));
        }
    }

    private void addCalendar() {

            for (int i = 0; i < m_iTotalMonth; i++) {

            //加入日曆上方的layout
            RelativeLayout calendarLayout = (RelativeLayout) LayoutInflater.from(m_Context).inflate(R.layout.layout_calendar, null);

            RelativeLayout calendarTitle = (RelativeLayout) calendarLayout.findViewById(R.id.rl_title);

            RelativeLayout calendarDayLayout = (RelativeLayout) calendarLayout.findViewById(R.id.rl_day);

            m_vScaleDef.selfAdjustAllView(calendarDayLayout);

            TextView calendarTitleText = (TextView) calendarLayout.findViewById(R.id.tv_centerline);

            if (i == 0) {
                //第一個日曆上方有固定margin
                m_vScaleDef.selfAdjustAllView(calendarTitle);
            } else {
                //第一個之後的沒有margin
                m_vScaleDef.selfAdjustAllView(calendarTitle);
                m_vScaleDef.setMargins(calendarTitle, 0, 0, 0, 16);
            }


            {//加入行事曆
                CalView calView = new CalView(this);

                calView.setStartYear(startYear);
                calView.setStartMonth(startMonth + 1);
                calView.setStartDay(startDay);

                calView.setBackYear(backYear);
                calView.setBackMonth(backMonth + 1);
                calView.setBackDay(backDay);

                calView.setIsStart(m_bIsStart);

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, m_vScaleDef.getLayoutHeight(253));

                final int calMonth, nowMonth;

                Calendar calendar = Calendar.getInstance();

                nowMonth = calendar.get(Calendar.MONTH);

                if (nowMonth + i > 12) {

                    calMonth = nowMonth + i - 12;

                    calView.setCalMonth(calMonth + 1);
                    calView.setCalYear(calendar.get(Calendar.YEAR) + 1);

                    calendar.add(Calendar.YEAR, 1);
                    calendar.set(Calendar.MONTH, calMonth);
                    //為了避免有的月份沒有31日
                    //所以需要先將日期初始化，假設是1號就ok了
                    calendar.set(Calendar.DAY_OF_MONTH, 1);

                    calendarTitleText.setText(convertDateFormat(DATE_FORMAT_3, calendar));
                } else {
                    //直接將月份+1, 如果當天是31日會有問題，因為不是每個月都有31日
                    //所以需要先將日期初始化，假設是1號就ok了
                    calMonth = nowMonth + i;

                    calView.setCalYear(calendar.get(Calendar.YEAR));

                    calendar.set(Calendar.MONTH, calMonth);

                    calView.setCalMonth(calMonth + 1);// 將月份+1

                    calendar.set(Calendar.MONTH, calMonth);
                    //初始化為每月1日
                    calendar.set(Calendar.DAY_OF_MONTH, 1);

                    calendarTitleText.setText(convertDateFormat(DATE_FORMAT_3, calendar));
                }

                calView.invalidate();

                calView.setLayoutParams(layoutParams);

                m_arCalList.add(calView);
                calendarDayLayout.addView(calView);
            }

            m_arrLayoutList.add(calendarLayout);

        }
    }


    private void setCalendar() {

        for (int i = 0; i < m_arCalList.size(); i++) {
            m_arCalList.get(i).setCalOnClickListener(new CalView.CalOnClickListener() {
                @Override
                public void CalOnClick(CalView calView, int year, int month, int day) {

                    Calendar toDayDate = null, clickDate = null, startDate = null;

                    Calendar calendar = Calendar.getInstance();

                    toDayDate = Calendar.getInstance();
                    clickDate = Calendar.getInstance();

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

                    Date date = null;

                    int nowYear = calendar.get(Calendar.YEAR);
                    int nowMonth = calendar.get(Calendar.MONTH) + 1; // Note: zero based!
                    int nowDay = calendar.get(Calendar.DAY_OF_MONTH);

                    try {
                        date = dateFormat.parse(nowYear + "/" + nowMonth + "/" + nowDay);

                        toDayDate.setTime(date);

                        date = dateFormat.parse(year + "/" + month + "/" + day);

                        clickDate.setTime(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (m_bIsStart) {

                        if (clickDate.after(toDayDate) || clickDate.equals(toDayDate)) { //小於當天天數

                            if (calView.getBackYear() != 0) {

                                Calendar calBack = Calendar.getInstance();

                                calBack = Calendar.getInstance();

                                calBack.setTime(calView.getCalBackDate());

                                if (clickDate.equals(calBack) || clickDate.before(calBack)) {

                                    clickYear = year;
                                    clickMonth = month;
                                    clickDay = day;

                                    calendar.set(clickYear, clickMonth - 1, clickDay);

                                    m_tvTimeLeft.setText(AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddEEE(calendar));


                                    for (int j = 0; j < m_arCalList.size(); j++) {

                                        m_arCalList.get(j).setStartYear(clickYear);
                                        m_arCalList.get(j).setStartMonth(clickMonth);
                                        m_arCalList.get(j).setStartDay(clickDay);
                                        m_arCalList.get(j).invalidate();
                                    }

                                }
                            }else{
                                clickYear = year;
                                clickMonth = month;
                                clickDay = day;

                                calendar.set(clickYear, clickMonth - 1, clickDay);

                                m_tvTimeLeft.setText(AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddEEE(calendar));

                                for (int j = 0; j < m_arCalList.size(); j++) {

                                    m_arCalList.get(j).setStartYear(clickYear);
                                    m_arCalList.get(j).setStartMonth(clickMonth);
                                    m_arCalList.get(j).setStartDay(clickDay);
                                    m_arCalList.get(j).invalidate();
                                }

                            }
                        }
                    } else {

                        Calendar calStart = Calendar.getInstance();

                        calStart = Calendar.getInstance();

                        calStart.setTime(calView.getCalStartDate());

                        if (clickDate.equals(calStart) || clickDate.after(calStart)) { //小於當天天數

                            clickYear = year;
                            clickMonth = month;
                            clickDay = day;

                            calendar.set(clickYear, clickMonth - 1, clickDay);

                            m_tvTimeRight.setText(AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddEEE(calendar));

                            for (int j = 0; j < m_arCalList.size(); j++) {

                                m_arCalList.get(j).setBackYear(clickYear);
                                m_arCalList.get(j).setBackMonth(clickMonth);
                                m_arCalList.get(j).setBackDay(clickDay);
                                m_arCalList.get(j).invalidate();

                            }
                            m_baseAdapter.notifyDataSetChanged();
                        }
                    }

                    //2016-08-05 需求變更
                    //時刻表修改單程來回均用於點擊日曆後即返回上頁
                    onBackPressed();
                }
            });
        }
    }

    private void setListView() {

        setBaseAdapter();

        m_lvCalendarPick.setAdapter(m_baseAdapter);
        m_lvCalendarPick.setDivider(null);

    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(m_tvTitleImage, 30, 30);
    }

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            if (m_bIsStart){
                return m_Context.getString(R.string.select_departure_date_title);
            } else {
                return m_Context.getString(R.string.select_arrival_date_title);
            }
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {
        }

        @Override
        public void onLeftMenuClick() {
        }

        @Override
        public void onBackClick() {

            onBackPressed();

        }

        @Override
        public void onDeleteClick() {

        }

        @Override
        public void onDemoModeClick() {
        }
    };

    public void onBackPressed() {

        Calendar    calendar    = Calendar.getInstance();
        Calendar    end         = Calendar.getInstance();
        end.set(endYear, endMonth, endDay);
        boolean     isSetResult = false;
        if(true == m_bIsStart){
            if (clickMonth == 0 || clickDay == 0 || clickYear == 0) {
                calendar.set(startYear, startMonth, startDay);
                isSetResult = true;
            } else {
                calendar.set(clickYear, clickMonth - 1 , clickDay);
                if ( !end.before(calendar) ){
                    isSetResult = true;
                }
            }
        } else {
            if (clickMonth == 0 || clickDay == 0 || clickYear == 0) {
                if(backYear != 0 && backMonth != 0 && backDay != 0){
                    calendar.set(backYear, backMonth , backDay);
                    if ( !end.before(calendar) ){
                        isSetResult = true;
                    }
                }
            } else {
                calendar.set(clickYear, clickMonth - 1 , clickDay);
                if ( !end.before(calendar) ){
                    isSetResult = true;
                }
            }
        }

        if(true == isSetResult){
            m_putIntent.putExtra(BUNDLE_PARA_LONG_DATE, calendar.getTimeInMillis());
            setResult(RESULT_OK, m_putIntent);
        }
        finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {

    }

    @Override
    protected void onLanguageChangeUpdateUI() {

    }


    private String convertDateFormat(String format, Calendar date){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        //return simpleDateFormat.format(date.getTime());

        String strDate = simpleDateFormat.format(date.getTime());

        //Log Test
        //int iyear = date.get(Calendar.YEAR);
        //int iMon = date.get(Calendar.MONTH);

        return strDate;
    }

    public void setBaseAdapter() {
        m_baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return m_iTotalMonth;
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

                return m_arrLayoutList.get(position);

            }
        };

    }

}
