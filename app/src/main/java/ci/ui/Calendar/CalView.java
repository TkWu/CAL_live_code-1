package ci.ui.Calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.text.format.Time;
import ci.function.Core.SLog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ci.ui.define.ViewScaleDef;

/**
 * Created by hamburger on 2016/3/29.
 */
public class CalView extends View implements View.OnTouchListener {

    private int column = 7;
    private int row = 6;

    private boolean isClickAbleDay = true;

    private int DayWidth;
    private int DayHeight;

    private int WeekTextSize = 30, DayTextSize = 25;

    private float WeekTextHeight = 0, DayTextHeight = 0;

    private PaintText paintText, paintDayText;

    private boolean FLAG_ONCLICK = false;

    private ArrayList<String> WeekTexts;

    private CalendarModule CM; // 萬年曆模組

    Date calToDayDate = null, calStartDate = null, calBackDate = null, calDay = null, calEndDate = null;

    private Time nowtime;
    private int year = 0, month = 0, today = 0;
    private int iEndYear = 0, iEndMonth = 0, iEndDay = 0;
    private int calYear = 0, calMonth = 0;
    private int startYear = 0, startMonth = 0, startDay = 0;
    private int backYear = 0, backMonth = 0, backDay = 0;
    private int startDate = 0, maxDate = 0;// 用來存放每個月的開始跟最大天數
    private int clickDate = 0;

    private boolean isStart = true;

    private int orow = 0, ocolumn = 0;

    private Context context;
    private ViewScaleDef viewScaleDef;

    private CalOnClickListener calOnClickListener;

    public interface CalOnClickListener {
        public void CalOnClick(CalView calView, int year, int month, int day);
    }

    public CalView(Context context) {
        super(context);
        this.context = context;
        this.setOnTouchListener(this);
        init();
    }

    private void init() {
        viewScaleDef = ViewScaleDef.getInstance(context);

        CM = new CalendarModule();// 年曆模組

        nowtime = new Time();

        this.WeekTexts = new ArrayList<String>();
        this.WeekTexts.add(getResources().getString(R.string.departure_date_Sun));
        this.WeekTexts.add(getResources().getString(R.string.departure_date_Mon));
        this.WeekTexts.add(getResources().getString(R.string.departure_date_Tue));
        this.WeekTexts.add(getResources().getString(R.string.departure_date_Wed));
        this.WeekTexts.add(getResources().getString(R.string.departure_date_Thu));
        this.WeekTexts.add(getResources().getString(R.string.departure_date_Fri));
        this.WeekTexts.add(getResources().getString(R.string.departure_date_Sat));

        nowtime.setToNow();// 取得系統時間。
        startYear = nowtime.year;
        startMonth = nowtime.month + 1; // 取出的時間+1為正確時間
        startDay = nowtime.monthDay;

        backYear = 0;
        backMonth = 0; // 取出的時間+1為正確時間
        backDay = 0;

        year = nowtime.year;
        month = nowtime.month + 1; // 取出的時間+1為正確時間
        today = nowtime.monthDay;

        //根據現在時間計算
        Calendar calendar = Calendar.getInstance();
        //取361天後的日期
        calendar.add(Calendar.DATE, 361);
        iEndYear = calendar.get(Calendar.YEAR);
        iEndMonth = calendar.get(Calendar.MONTH) + 1;
        iEndDay = calendar.get(Calendar.DATE);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.DayWidth = getWidth();
        this.DayHeight = getHeight();

        startDate = CM.getFirstDayWeek(calYear, calMonth);
        maxDate = CM.getMaxDay(calYear, calMonth);

        {//日曆
            for (int i = 0; i < WeekTexts.size(); i++) {

                paintText = new PaintText(WeekTexts.get(i));

                paintText.setColor(getResources().getColor(R.color.white_50));

                this.paintText.setTextSize(viewScaleDef.getTextSize(13));
                WeekTextHeight = paintText.getHeight();

                float weekWeidthHalf = ((float) DayWidth / (float) column) / (float) 2;// 取每個天數的中點
                float weekHeightHalf = ((float) DayHeight / (float) row) / (float) 2;// 取每個天數的中點
                float textHalf = (float) this.paintText.getWidth() / (float) 2;// 校正文字用，取字的中點

                canvas.drawText(WeekTexts.get(i), getXPointDay(i) + weekWeidthHalf - textHalf,
                        0 + this.paintText.getCurrent(), this.paintText);
            }
        }

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {

                int number = (i * 7) + j + 1;

                if (number > (startDate - 1) && number <= (startDate + maxDate - 1)) { // 經過萬年曆模組計算本月的日曆範圍

                    int day = number - (this.startDate - 1);

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

                    try {

                        calDay =  dateFormat.parse(calYear + "/" + calMonth + "/" + day);
                        calToDayDate = dateFormat.parse(year + "/" + month + "/" + today);
                        calStartDate = dateFormat.parse(startYear + "/" + startMonth + "/" + startDay);
                        calBackDate = dateFormat.parse(backYear + "/" + backMonth + "/" + backDay);
                        calEndDate = dateFormat.parse(iEndYear + "/" + iEndMonth + "/" + iEndDay);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (isStart){
                        //日曆日期小於today //2017-02-09 CR 大於一年後的日期也不能選
                        if (calToDayDate.after(calDay) || calEndDate.before(calDay)) {

                            paintDayText = new PaintText(String.valueOf(day));
                            paintDayText.setTextSize(viewScaleDef.getTextSize(20));

                            float dayWeidthHalf = ((float) DayWidth / (float) column) / (float) 2;// 取每個天數的中點
                            float dayHeightHalf = ((float) DayHeight / (float) row) / (float) 2;// 取每個天數的中點
                            float textHalf = (float) this.paintDayText.getWidth() / (float) 2;// 校正文字用，取字的中點

                            paintDayText.setColor(getResources().getColor(R.color.white_15));

                            canvas.drawText(String.valueOf(day),
                                    getXPointDay(j
                                    ) + dayWeidthHalf - textHalf, getYPointDay(i) + dayHeightHalf, this.paintDayText);
                        } else { // 日曆日期大於當天

                            paintDayText = new PaintText(String.valueOf(day));
                            paintDayText.setTextSize(viewScaleDef.getTextSize(20));

                            float dayWeidthHalf = ((float) DayWidth / (float) column) / (float) 2;// 取每個天數的中點
                            float dayHeightHalf = ((float) DayHeight / (float) row) / (float) 2;// 取每個天數的中點
                            float textHalf = (float) this.paintDayText.getWidth() / (float) 2;// 校正文字用，取字的中點

                            if (calDay.equals(calStartDate)) {//day == Start

                                if (calStartDate.equals(calBackDate)){ //day == backDay the same day

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawCircle(getXPointDay(j) + dayWeidthHalf, getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2, viewScaleDef.getLayoutHeight(35) / 2, this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.french_blue));

                                    canvas.drawCircle(getXPointDay(j) + dayWeidthHalf, getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2, viewScaleDef.getLayoutHeight(32) / 2, this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawCircle(getXPointDay(j) + dayWeidthHalf, getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2, viewScaleDef.getLayoutHeight(30) / 2, this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.french_blue));

                                    canvas.drawText(String.valueOf(day),
                                            getXPointDay(j) + dayWeidthHalf - textHalf, getYPointDay(i) + dayHeightHalf, this.paintDayText);

                                }else if (calStartDate.after(calBackDate)){ //day > backday

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawCircle(getXPointDay(j) + dayWeidthHalf, getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2, viewScaleDef.getLayoutHeight(35) / 2, this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.french_blue));

                                    canvas.drawText(String.valueOf(day),
                                            getXPointDay(j) + dayWeidthHalf - textHalf, getYPointDay(i) + dayHeightHalf, this.paintDayText);
                                }else{ //day != back && day > backday

                                    float rectTop = getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2 - viewScaleDef.getLayoutHeight(35) / 2;
                                    float rectLeft = getXPointDay(j) + dayWeidthHalf;
                                    float rectRight = getXPointDay(j + 1);
                                    float rectBottom = getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2 + viewScaleDef.getLayoutHeight(35) / 2;

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawRect(rectLeft, rectTop, rectRight, rectTop + viewScaleDef.getLayoutHeight(1), this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawRect(rectLeft, rectBottom - viewScaleDef.getLayoutHeight(1), rectRight, rectBottom, this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.white_15));

                                    canvas.drawRect(rectLeft, rectTop + viewScaleDef.getLayoutHeight(1), rectRight, rectBottom - viewScaleDef.getLayoutHeight(1), this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawCircle(getXPointDay(j) + dayWeidthHalf, getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2, viewScaleDef.getLayoutHeight(35) / 2, this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.french_blue));

                                    canvas.drawText(String.valueOf(day),
                                            getXPointDay(j) + dayWeidthHalf - textHalf, getYPointDay(i) + dayHeightHalf, this.paintDayText);
                                }



                            }else if (calDay.after(calStartDate)){//出發日期>start

//
                                if (calDay.equals(calBackDate)) {

                                    float rectTop = getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2 - viewScaleDef.getLayoutHeight(35) / 2;
                                    float rectLeft = getXPointDay(j);
                                    float rectRight = getXPointDay(j + 1) - dayWeidthHalf;
                                    float rectBottom = getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2 + viewScaleDef.getLayoutHeight(35) / 2;

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawRect(rectLeft, rectTop, rectRight, rectTop + viewScaleDef.getLayoutHeight(1), this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawRect(rectLeft, rectBottom - viewScaleDef.getLayoutHeight(1), rectRight, rectBottom, this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.white_15));

                                    canvas.drawRect(rectLeft, rectTop + viewScaleDef.getLayoutHeight(1), rectRight, rectBottom - viewScaleDef.getLayoutHeight(1), this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawCircle(getXPointDay(j) + dayWeidthHalf, getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2, viewScaleDef.getLayoutHeight(35) / 2, this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.french_blue));

                                    canvas.drawText(String.valueOf(day),
                                            getXPointDay(j) + dayWeidthHalf - textHalf, getYPointDay(i) + dayHeightHalf, this.paintDayText);
                                }else if(calDay.before(calBackDate)) {//出發日期<回來日期

                                    float rectTop = getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2 - viewScaleDef.getLayoutHeight(35) / 2;
                                    float rectLeft = getXPointDay(j);
                                    float rectRight = getXPointDay(j + 1);
                                    float rectBottom = getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2 + viewScaleDef.getLayoutHeight(35) / 2;

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawRect(rectLeft, rectTop, rectRight, rectTop + viewScaleDef.getLayoutHeight(1), this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawRect(rectLeft, rectBottom - viewScaleDef.getLayoutHeight(1), rectRight, rectBottom, this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.white_15));

                                    canvas.drawRect(rectLeft, rectTop + viewScaleDef.getLayoutHeight(1), rectRight, rectBottom - viewScaleDef.getLayoutHeight(1), this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawText(String.valueOf(day),
                                            getXPointDay(j) + dayWeidthHalf - textHalf, getYPointDay(i) + dayHeightHalf, this.paintDayText);
                                }else{

                                    if (backYear != 0){

                                        paintDayText.setColor(getResources().getColor(R.color.white_15));

                                        canvas.drawText(String.valueOf(day),
                                                getXPointDay(j) + dayWeidthHalf - textHalf, getYPointDay(i) + dayHeightHalf, this.paintDayText);

                                    }else {

                                        paintDayText.setColor(getResources().getColor(R.color.white_four));

                                        canvas.drawText(String.valueOf(day),
                                                getXPointDay(j) + dayWeidthHalf - textHalf, getYPointDay(i) + dayHeightHalf, this.paintDayText);
                                    }
                                }

                            }else{
                                paintDayText.setColor(getResources().getColor(R.color.white_four));

                                canvas.drawText(String.valueOf(day),
                                        getXPointDay(j) + dayWeidthHalf - textHalf, getYPointDay(i) + dayHeightHalf, this.paintDayText);
                            }

                        }
                    }else{
                        //日曆日期小於today //2017-02-09 CR 大於一年後的日期也不能選
                        if (calStartDate.after(calDay) || calEndDate.before(calDay)) {

                            paintDayText = new PaintText(String.valueOf(day));
                            paintDayText.setTextSize(viewScaleDef.getTextSize(20));

                            float dayWeidthHalf = ((float) DayWidth / (float) column) / (float) 2;// 取每個天數的中點
                            float dayHeightHalf = ((float) DayHeight / (float) row) / (float) 2;// 取每個天數的中點
                            float textHalf = (float) this.paintDayText.getWidth() / (float) 2;// 校正文字用，取字的中點

                            paintDayText.setColor(getResources().getColor(R.color.white_15));

                            canvas.drawText(String.valueOf(day),
                                    getXPointDay(j
                                    ) + dayWeidthHalf - textHalf, getYPointDay(i) + dayHeightHalf, this.paintDayText);

                        } else { // 日曆日期大於當天

                            paintDayText = new PaintText(String.valueOf(day));
                            paintDayText.setTextSize(viewScaleDef.getTextSize(20));

                            float dayWeidthHalf = ((float) DayWidth / (float) column) / (float) 2;// 取每個天數的中點
                            float dayHeightHalf = ((float) DayHeight / (float) row) / (float) 2;// 取每個天數的中點
                            float textHalf = (float) this.paintDayText.getWidth() / (float) 2;// 校正文字用，取字的中點

                            if (calDay.equals(calStartDate)) {//日曆=Start

                                if (calStartDate.equals(calBackDate)){

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawCircle(getXPointDay(j) + dayWeidthHalf, getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2, viewScaleDef.getLayoutHeight(35) / 2, this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.french_blue));

                                    canvas.drawCircle(getXPointDay(j) + dayWeidthHalf, getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2, viewScaleDef.getLayoutHeight(32) / 2, this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawCircle(getXPointDay(j) + dayWeidthHalf, getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2, viewScaleDef.getLayoutHeight(30) / 2, this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.french_blue));

                                    canvas.drawText(String.valueOf(day),
                                            getXPointDay(j) + dayWeidthHalf - textHalf, getYPointDay(i) + dayHeightHalf, this.paintDayText);

                                }else if (calStartDate.after(calBackDate)){

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawCircle(getXPointDay(j) + dayWeidthHalf, getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2, viewScaleDef.getLayoutHeight(35) / 2, this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.french_blue));

                                    canvas.drawText(String.valueOf(day),
                                            getXPointDay(j) + dayWeidthHalf - textHalf, getYPointDay(i) + dayHeightHalf, this.paintDayText);
                                }else{

                                    float rectTop = getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2 - viewScaleDef.getLayoutHeight(35) / 2;
                                    float rectLeft = getXPointDay(j) + dayWeidthHalf;
                                    float rectRight = getXPointDay(j + 1);
                                    float rectBottom = getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2 + viewScaleDef.getLayoutHeight(35) / 2;

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawRect(rectLeft, rectTop, rectRight, rectTop + viewScaleDef.getLayoutHeight(1), this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawRect(rectLeft, rectBottom - viewScaleDef.getLayoutHeight(1), rectRight, rectBottom, this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.white_15));

                                    canvas.drawRect(rectLeft, rectTop + viewScaleDef.getLayoutHeight(1), rectRight, rectBottom - viewScaleDef.getLayoutHeight(1), this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawCircle(getXPointDay(j) + dayWeidthHalf, getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2, viewScaleDef.getLayoutHeight(35) / 2, this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.french_blue));

                                    canvas.drawText(String.valueOf(day),
                                            getXPointDay(j) + dayWeidthHalf - textHalf, getYPointDay(i) + dayHeightHalf, this.paintDayText);
                                }

                            }else if (calDay.after(calStartDate)){//出發日期>start

//
                                if (calDay.equals(calBackDate)) {

                                    float rectTop = getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2 - viewScaleDef.getLayoutHeight(35) / 2;
                                    float rectLeft = getXPointDay(j);
                                    float rectRight = getXPointDay(j + 1) - dayWeidthHalf;
                                    float rectBottom = getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2 + viewScaleDef.getLayoutHeight(35) / 2;

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawRect(rectLeft, rectTop, rectRight, rectTop + viewScaleDef.getLayoutHeight(1), this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawRect(rectLeft, rectBottom - viewScaleDef.getLayoutHeight(1), rectRight, rectBottom, this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.white_15));

                                    canvas.drawRect(rectLeft, rectTop + viewScaleDef.getLayoutHeight(1), rectRight, rectBottom - viewScaleDef.getLayoutHeight(1), this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawCircle(getXPointDay(j) + dayWeidthHalf, getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2, viewScaleDef.getLayoutHeight(35) / 2, this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.french_blue));

                                    canvas.drawText(String.valueOf(day),
                                            getXPointDay(j) + dayWeidthHalf - textHalf, getYPointDay(i) + dayHeightHalf, this.paintDayText);

                                }else if(calDay.before(calBackDate)) {//出發日期<回來日期

                                    float rectTop = getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2 - viewScaleDef.getLayoutHeight(35) / 2;
                                    float rectLeft = getXPointDay(j);
                                    float rectRight = getXPointDay(j + 1);
                                    float rectBottom = getYPointDay(i) + dayHeightHalf - paintDayText.getHeight() / 2 + viewScaleDef.getLayoutHeight(35) / 2;

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawRect(rectLeft, rectTop, rectRight, rectTop + viewScaleDef.getLayoutHeight(1), this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawRect(rectLeft, rectBottom - viewScaleDef.getLayoutHeight(1), rectRight, rectBottom, this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.white_15));

                                    canvas.drawRect(rectLeft, rectTop + viewScaleDef.getLayoutHeight(1), rectRight, rectBottom - viewScaleDef.getLayoutHeight(1), this.paintDayText);

                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawText(String.valueOf(day),
                                            getXPointDay(j) + dayWeidthHalf - textHalf, getYPointDay(i) + dayHeightHalf, this.paintDayText);
                                }else{
                                    paintDayText.setColor(getResources().getColor(R.color.white_four));

                                    canvas.drawText(String.valueOf(day),
                                            getXPointDay(j) + dayWeidthHalf - textHalf, getYPointDay(i) + dayHeightHalf, this.paintDayText);
                                }

                            }else{
                                paintDayText.setColor(getResources().getColor(R.color.white_four));

                                canvas.drawText(String.valueOf(day),
                                        getXPointDay(j) + dayWeidthHalf - textHalf, getYPointDay(i) + dayHeightHalf, this.paintDayText);
                            }

                        }
                    }
                }

                if ((startDate + maxDate - 1) == number && number > 35) {
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, viewScaleDef.getLayoutHeight(300));
                    this.setLayoutParams(layoutParams);
                    row = 7;
                    this.invalidate();
                }
            }
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        float rx = event.getX();// 畫面的總寬
        float ry = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (ry > WeekTextHeight) {// 檔掉文字區，並從以下計算
                    this.ocolumn = (int) (rx / getDayWidth());
                    this.orow = (int) ((ry - WeekTextHeight * 2.5) / getDayHeight());// 判斷點擊範圍也要縮小，才不會有BUG

//                    clickDate = (orow*7+ocolumn+1)-(startDate-1); //計算按下日期

                    //Log.e("ocolumn", ocolumn+"");
                    //Log.e("orow", orow+"");
                    if (this.calOnClickListener != null) {
                        FLAG_ONCLICK = true;
//                        invalidate();
                    }
                    break;
                }

            case MotionEvent.ACTION_UP:
                int ncolumn = (int) (rx / getDayWidth());
                int nrow =  (int) ((ry - WeekTextHeight * 2.5) / getDayHeight());// 判斷點擊範圍也要縮小，才不會有BUG
                FLAG_ONCLICK = false;
//                invalidate();

                startDate = CM.getFirstDayWeek(calYear, calMonth);
                maxDate = CM.getMaxDay(calYear, calMonth);

                clickDate = (orow*7+ocolumn+1)-(startDate-1); //計算按下日期

                if (clickDate > 0 && clickDate <= maxDate) { // 經過萬年曆模組計算本月的日曆範圍

                    //選超過範圍的日期 不更新畫面也不callback
                    Calendar calendarStart = Calendar.getInstance();
                    if (isStart){
                        calendarStart.set(year, month-1, today);
                    }else {
                        calendarStart.set(startYear, startMonth-1, startDay);
                    }
                    Calendar calendarClick = Calendar.getInstance();
                    calendarClick.set(calYear, calMonth-1, clickDate);
                    Calendar calendarEnd = Calendar.getInstance();
                    calendarEnd.set(iEndYear, iEndMonth-1, iEndDay);
                    if (calendarStart.after(calendarClick)||calendarEnd.before(calendarClick))
                        break;

                    if (ocolumn == ncolumn && orow == nrow) {
                        this.calOnClickListener.CalOnClick(this, calYear, calMonth, clickDate);
                         FLAG_ONCLICK = false;
                         invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            // 解三星面板觸控問題
            case MotionEvent.ACTION_CANCEL:
                FLAG_ONCLICK = false;
//                invalidate();
                break;
            default:
                break;
        }
        return true;// 若回傳false，匯跳轉到下面的OnCLlickListener之類的聆聽事件
    }

    private float getXPointDay(int x) {
        return ((float) this.DayWidth / (float) column) * x;
    }

    private float getYPointDay(int y) {
        return ((((float) this.DayHeight - WeekTextHeight) / (float) row) * y)
                + WeekTextHeight + viewScaleDef.getLayoutHeight(20);// 打點
    }

    public int getyear() {
        return year;
    }

    public void setyear(int year) {
        this.year = year;
    }

    public void setmonth(int month) {
        this.month = month;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public void setCalMonth(int calMonth) {
        this.calMonth = calMonth;
    }

    public void setCalYear(int calYear) {
        this.calYear = calYear;
    }

    public void setIsStart(boolean isStart) {
        this.isStart = isStart;
    }

    public void settoday(int today) {
        this.today = today;
    }

    public int getStartDate() {
        return startDate;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setBackMonth(int backMonth) {
        this.backMonth = backMonth;
    }

    public void setBackDay(int backDay) {
        this.backDay = backDay;
    }

    public void setBackYear(int backYear) {
        this.backYear = backYear;
    }

    public int getBackYear() {
        return backYear;
    }

    public Date getCalBackDate() {
        return calBackDate;
    }

    public Date getCalStartDate() {
        return calStartDate;
    }

    private float getDayWidth() {
        return (float) DayWidth / (float) column;
    }

    private float getDayHeight() {
        return ((float) DayHeight - (float) WeekTextHeight) / (float) row ;
    }

    public void setCalOnClickListener(CalOnClickListener calOnClickListener) {
        this.calOnClickListener = calOnClickListener;
    }
}
