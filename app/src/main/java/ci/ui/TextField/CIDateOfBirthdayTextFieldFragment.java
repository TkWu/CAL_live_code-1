package ci.ui.TextField;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ci.function.Core.CIApplication;
import ci.ui.TextField.Base.CITextFieldFragment;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;

/**
 * Created by kevin on 2016/2/20.
 */
public class CIDateOfBirthdayTextFieldFragment extends CITextFieldFragment {

    private DatePickerDialog m_datePickerDailog = null;
    private int m_iDate = 0;
    private Calendar m_caleNow = Calendar.getInstance();
    private String   m_strFormatedData = "";
    private final String WS_FORMAT = "yyyy-MM-dd";
    private final String UI_FORMAT = "yyyy/MM/dd";

    public static CIDateOfBirthdayTextFieldFragment newInstance(String hint) {
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, hint);
        bundle.putString(TYPE_MODE, TypeMode.MENU_PULL_DOWN.name());
        CIDateOfBirthdayTextFieldFragment fragment = new CIDateOfBirthdayTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        setDropDownListener(listener);
    }

    dropDownListener listener = new dropDownListener() {
        @Override
        public void onDropDown(TypeMode mode, View v, String tag) {
            if (null != m_datePickerDailog) {
                m_datePickerDailog.dismiss();
            }
            m_datePickerDailog
                    = new DatePickerDialog(getActivity(),
                                           THEME_HOLO_LIGHT,
                                           datePickerDlgOnDateSet,
                                           m_caleNow.get(Calendar.YEAR),
                                           m_caleNow.get(Calendar.MONTH),
                                           m_caleNow.get(Calendar.DAY_OF_MONTH));
            m_datePickerDailog.getDatePicker().setCalendarViewShown(false);

            //2016-10-21 modifly by Ryan , 修正一下判斷方式, 調整為一開始就不讓使用者選
            //生日最大日期不能大於今天
            Calendar calendar = Calendar.getInstance();
            int iMaxDay = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, iMaxDay);
            DatePicker datePicker = m_datePickerDailog.getDatePicker();
            datePicker.setMaxDate(calendar.getTime().getTime());
            //
            m_datePickerDailog.show();
        }
    };

    DatePickerDialog.OnDateSetListener datePickerDlgOnDateSet = new DatePickerDialog.OnDateSetListener() {


        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (view.isShown()) {//控制只跑ㄧ次
                m_caleNow.set(Calendar.YEAR, year);
                m_caleNow.set(Calendar.MONTH, monthOfYear);
                m_caleNow.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                m_caleNow.set(Calendar.HOUR_OF_DAY, 0);
                m_caleNow.set(Calendar.MINUTE, 0);
                m_caleNow.set(Calendar.SECOND, 0);
                m_caleNow.set(Calendar.MILLISECOND, 0);
                //2016-10-21 modifly by Ryan , 修正一下判斷方式, 調整為一開始就不讓使用者選

                /**須在setText前設定值，否則觸發文字變化事件callback內會取不到長整數或日期格式化字串*/
                m_strFormatedData = convertDateFormat(WS_FORMAT, m_caleNow);
                m_iDate = (int)(m_caleNow.getTimeInMillis()/1000);
                /**設定欄位文字*/
                m_editText.setText(m_strFormatedData);
            }
        }
    };

    public int getIntForDate(){
        return m_iDate;
    }

    /**
     * 用來發送request資料的文字格式
     * @return 字串
     */
    public String getFormatedDate(){
        return m_strFormatedData;
    }

    /**
     * 設定request資料的文字格式
     * @return 字串
     */
    public void setFormatedDate(String dateText){
        m_strFormatedData = dateText;
        Date date = paserWSDateFormatText(dateText);
        if(null == date){
            return;
        }
        m_caleNow.setTime(date);
        m_iDate = (int)(m_caleNow.getTimeInMillis()/1000);
    }

    /**
     * 將Calendar物件格式化成文字
     * @param calendar
     * @return 字串
     */
    private String convertDateFormat(String template ,Calendar calendar) {
        // 定義時間格式
        SimpleDateFormat sdf = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
        sdf.applyPattern(template);
        return sdf.format(calendar.getTime());
    }

    /**
     * 轉換WS日期格式成UI
     * @param date
     * @return
     */
    public String convertWSToUIDateFormat(String date) {

        return convertDateFormat(WS_FORMAT, UI_FORMAT, date);
    }

    /**
     * 轉換UI日期格式成WS
     * @param date
     * @return
     */
    public String convertUIToWSDateFormat(String date) {
        return convertDateFormat(UI_FORMAT, WS_FORMAT, date);
    }

    /**
     * 轉換日期文字
     * @param template_a 轉換前的日期格式
     * @param template_b 轉換後的日期格式
     * @param date       轉換前的日期文字
     * @return           轉換後的日期文字
     */
    private String convertDateFormat(String template_a , String template_b, String date) {
        // 定義時間格式
        SimpleDateFormat sdfA = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
        SimpleDateFormat sdfB = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
        sdfA.applyPattern(template_a);
        sdfB.applyPattern(template_b);
        try {
            return sdfB.format(sdfA.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private Date paserWSDateFormatText(String date){
        SimpleDateFormat sdf = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
        sdf.applyPattern(WS_FORMAT);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
