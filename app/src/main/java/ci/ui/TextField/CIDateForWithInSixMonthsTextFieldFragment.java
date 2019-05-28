package ci.ui.TextField;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.DatePicker;

import com.chinaairlines.mobile30.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.define.ViewScaleDef;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;

/**
 * Created by kevin on 2016/2/20.
 */
public class CIDateForWithInSixMonthsTextFieldFragment extends CITextFieldFragment {

    private DatePickerDialog m_datePickerDailog = null;
    private Calendar         m_caleSelect       = (Calendar) Calendar.getInstance().clone();
    private String          m_strFormatedData   = "";
    private final String    WS_FORMAT           = "yyyy-MM-dd";

    public static CIDateForWithInSixMonthsTextFieldFragment newInstance(String hint) {
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, hint);
        bundle.putString(TYPE_MODE, TypeMode.MENU_PULL_DOWN.name());
        CIDateForWithInSixMonthsTextFieldFragment fragment = new CIDateForWithInSixMonthsTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void setupViewComponents(View rootview) {
        super.setupViewComponents(rootview);
        ViewScaleDef scaleDef = ViewScaleDef.getInstance(getContext());
        if(null != m_strHint && 0 < m_strHint.length()){
            int headIndex = m_strHint.indexOf("（");
            int tailIndex = m_strHint.indexOf("）");
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(m_strHint);
            if(-1 != headIndex && -1 != tailIndex){
                spannableStringBuilder.setSpan(new TextAppearanceSpan(null,
                                                                        0,
                                                                        scaleDef.getTextSize(13.33),
                                                                        null,
                                                                        null),
                                                                        headIndex,
                                                                        tailIndex + 1,
                                                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            m_textInputLayout.setHint(null);
            m_textInputLayout.getEditText().setTextSize(scaleDef.getTextSize(18));
            m_textInputLayout.getEditText().setHintTextColor(getResources().getColor(R.color.white_80));
            m_textInputLayout.getEditText().setHint(spannableStringBuilder);
            m_textInputLayout.setHintAnimationEnabled(true);
        } else {
            m_textInputLayout.setHintAnimationEnabled(false);
        }
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
                                           m_caleSelect.get(Calendar.YEAR),
                                           m_caleSelect.get(Calendar.MONTH),
                                           m_caleSelect.get(Calendar.DAY_OF_MONTH));
            m_datePickerDailog.getDatePicker().setCalendarViewShown(false);
            Calendar caleNow = Calendar.getInstance();
            Calendar caleBeforeSixMonths = Calendar.getInstance();
            adjustTime(caleBeforeSixMonths);
            caleBeforeSixMonths.add(Calendar.MONTH, -6);
            m_datePickerDailog.getDatePicker().setMaxDate(caleNow.getTimeInMillis());
            m_datePickerDailog.getDatePicker().setMinDate(caleBeforeSixMonths.getTimeInMillis());
            m_datePickerDailog.show();
        }
    };

    DatePickerDialog.OnDateSetListener datePickerDlgOnDateSet = new DatePickerDialog.OnDateSetListener() {


        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (view.isShown()) {//控制只跑ㄧ次
                m_caleSelect.set(Calendar.YEAR, year);
                m_caleSelect.set(Calendar.MONTH, monthOfYear);
                m_caleSelect.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                adjustTime(m_caleSelect);

                //格式化WS用日期文字
                m_strFormatedData = convertDateFormat(WS_FORMAT, m_caleSelect);

                //當選取前六個月內的日期時，就將日期填入欄位，因此hint必須浮上去
                // ，這時候要設定m_textInputLayout才會有作用，但SpannableStringBuilder對他沒有作用
                m_textInputLayout.setHint(m_strHint);
                m_editText.setText(m_strFormatedData);
            }
        }
    };

    private void adjustTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * 用來發送request資料的文字格式
     * @return 字串
     */
    public String getFormatedDate(){
        return m_strFormatedData;
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

}
