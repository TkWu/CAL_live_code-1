package ci.ui.TextField;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chinaairlines.mobile30.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ci.function.Core.CIApplication;
import ci.function.TimeTable.CIChooseSearchDateActivity;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.object.AppInfo;

/**
 * Created by flowmahuang on 2016/3/16.
 */
public class CIChooseSearchDateTextFieldFragment extends CITextFieldFragment {

    private OnSearchDataTextFeildParams onSearchDataTextFeildParams = null;
    private Calendar        selectDate = null;

    public static final String BUNDLE_PARA_BOOLEAN_IS_SINGLE            = "isSingle";
    public static final String BUNDLE_PARA_BOOLEAN_IS_START             = "isStart";
    public static final String BUNDLE_PARA_LONG_DATE                    = "date";
    public static final String BUNDLE_PARA_LONG_DEPARTURE_DATE          = "Departure_date";
    public static final String BUNDLE_PARA_LONG_RETURN_DATE             = "Return_date";
    public static final String BUNDLE_PARA_STRING_DEPARTURE_AIRPORT     = "Departure_Airport";
    public static final String BUNDLE_PARA_STRING_DESTINATION_AIRPORT   = "Destination_Airport";
//    private final String DATE_FORMAT_1 = "EEE, MMM dd, yyyy";
//    private final String DATE_FORMAT_2 = "EEEE, MMM dd, yyyy";

    public interface OnSearchDataTextFeildParams {
        boolean isOpenSelectPage();
        long    getDepartureDate();
        long    getRetureDate();
        String  getDepartureAirport();
        String  getDestinationAirport();
    }
    private static final int FROM_TIME_TABLE = 100;

    public static CIChooseSearchDateTextFieldFragment newInstance(String hint, boolean isSingle, boolean isStart) {
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, hint);
        bundle.putString(TYPE_MODE, CITextFieldFragment.TypeMode.MENU_FULL_PAGE.name());
        bundle.putBoolean(BUNDLE_PARA_BOOLEAN_IS_START, isStart);
        bundle.putBoolean(BUNDLE_PARA_BOOLEAN_IS_SINGLE, isSingle);
        CIChooseSearchDateTextFieldFragment fragment = new CIChooseSearchDateTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSelectDate();
    }

    public void initSelectDate(){
        selectDate = Calendar.getInstance();
        selectDate.set(1, 1, 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        setDropDownListener(listener);
    }

    CITextFieldFragment.dropDownListener listener = new CITextFieldFragment.dropDownListener() {
        @Override
        public void onDropDown(CITextFieldFragment.TypeMode mode, View v, String tag) {

            if (onSearchDataTextFeildParams != null) {

                Bundle bundle = getArguments();
                bundle.putLong(BUNDLE_PARA_LONG_DEPARTURE_DATE, onSearchDataTextFeildParams.getDepartureDate());
                bundle.putLong(BUNDLE_PARA_LONG_RETURN_DATE, onSearchDataTextFeildParams.getRetureDate());
                bundle.putString(BUNDLE_PARA_STRING_DEPARTURE_AIRPORT, onSearchDataTextFeildParams.getDepartureAirport());
                bundle.putString(BUNDLE_PARA_STRING_DESTINATION_AIRPORT, onSearchDataTextFeildParams.getDestinationAirport());
                //給實作的人傳遞數值
                boolean isChange = onSearchDataTextFeildParams.isOpenSelectPage();

                //實作的人決定是否換畫面
                if (isChange){
                    fullPageMenu();
                }

            }else{
                //沒有實作
                fullPageMenu();
            }

        }
    };

    public void fullPageMenu() {
        changeActivity(CIChooseSearchDateActivity.class);
    }

    public void changeActivity(Class clazz) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), clazz);

        Bundle bundle = this.getArguments();

        intent.putExtras(bundle);

        startActivityForResult(intent, FROM_TIME_TABLE);
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FROM_TIME_TABLE && resultCode == getActivity().RESULT_OK) {

            Long date = data.getLongExtra(BUNDLE_PARA_LONG_DATE, 0);

            selectDate.setTimeInMillis(date);

            if (getResources().getConfiguration().locale == Locale.ENGLISH){
                m_editText.setText(AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddEEE(selectDate));
                //m_editText.setText(convertDateFormat(DATE_FORMAT_1, selectDate));
            }else{
                m_editText.setText(AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddEEE(selectDate));
                //m_editText.setText(convertDateFormat(DATE_FORMAT_2, selectDate));
            }
        }
    }

    private String convertDateFormat(String format, Calendar date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date.getTime());
    }

    public void setOnSearchDataTextFeildParams(OnSearchDataTextFeildParams onSearchDataTextFeildParams){
        this.onSearchDataTextFeildParams = onSearchDataTextFeildParams;
    }

    public Calendar getSelectDate() {
        return selectDate;
    }

    public void setSelectDate(Calendar selectDate){
        this.selectDate.setTime(selectDate.getTime());
    }
}
