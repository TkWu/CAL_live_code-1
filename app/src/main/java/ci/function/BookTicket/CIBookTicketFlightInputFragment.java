package ci.function.BookTicket;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.Calendar;

import ci.function.Base.BaseFragment;
import ci.function.TimeTable.CISelectDepartureAirpotActivity;
import ci.ui.TextField.Base.CIBaseTextFieldFragment;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.CIChooseAirportTextFieldFragment;
import ci.ui.TextField.CIChooseSearchDateTextFieldFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;

/**
 * Created by kevincheng on 2016/4/1.
 */
public class CIBookTicketFlightInputFragment extends BaseFragment
    implements CITextFieldFragment.dropDownListener,
               View.OnClickListener{

    private CIChooseAirportTextFieldFragment    m_fromFragment          = null;
    private CIChooseAirportTextFieldFragment    m_toFragment            = null;
    private CIChooseSearchDateTextFieldFragment m_departureDateFragment = null;
    private TextView                            m_tvMultiStopValue      = null;
    private ImageView                           m_ivGarbage             = null;
    private onFragmentDeletedListener           m_listener              = null;
    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_book_ticket_flight_input;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_tvMultiStopValue      = (TextView) view.findViewById(R.id.tv_multi_stop_value);
        m_ivGarbage             = (ImageView) view.findViewById(R.id.iv_garbage);

        Bundle bundle = getArguments();

        if(bundle == null){
            return;
        }

        Boolean blIsORginal  = bundle.getBoolean(UiMessageDef.BUNDLE_BOOKING_ISORIGINAL_Y,false);

        if (blIsORginal) {
            m_fromFragment          = CIChooseAirportTextFieldFragment.newInstance(
                    "*" + getString(R.string.from),
                    R.drawable.ic_departure_2,
                    R.drawable.ic_departure_4,
                    false,
                    CISelectDepartureAirpotActivity.BOOKT_TICKET_ISOriginal_Y);
        } else {
            m_fromFragment          = CIChooseAirportTextFieldFragment.newInstance(
                    "*" + getString(R.string.from),
                    R.drawable.ic_departure_2,
                    R.drawable.ic_departure_4,
                    false,
                    CISelectDepartureAirpotActivity.BOOKT_TICKET);
        }

        m_toFragment            = CIChooseAirportTextFieldFragment.newInstance(
                "*"+getString(R.string.to),
                R.drawable.ic_arrival_2,
                R.drawable.ic_arrival_4,
                true,
                CISelectDepartureAirpotActivity.BOOKT_TICKET);
        m_departureDateFragment = CIChooseSearchDateTextFieldFragment.newInstance("*" + getString(R.string.departure_date), true, true);

        m_departureDateFragment.setOnSearchDataTextFeildParams(onSearchDataTextFeildParams);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_from, m_fromFragment);
        transaction.replace(R.id.fl_to, m_toFragment);
        transaction.replace(R.id.fl_departure_date, m_departureDateFragment);
        transaction.commit();
    }

    /**
     * 點擊選擇日期欄位時，如果目的地欄位尚未選擇，則會跳出提示視窗；如果已經選擇，則要帶入出發地日期毫秒、出發地及目的地的代碼
     */
    CIChooseSearchDateTextFieldFragment.OnSearchDataTextFeildParams onSearchDataTextFeildParams
            = new CIChooseSearchDateTextFieldFragment.OnSearchDataTextFeildParams() {
        @Override
        public boolean isOpenSelectPage() {
            if (TextUtils.isEmpty(m_toFragment.getIAIT())) {
                showDialog(getString(R.string.warning)
                        ,String.format(getString(R.string.please_input_field), getString(R.string.to))
                        //,getResources().getString(R.string.select_error_msg) + " " + getString(R.string.to)
                        ,getString(R.string.confirm));
                return false;//false 就是不打開選擇機場頁面
            } else {
                return true;
            }
        }

        @Override
        public long getDepartureDate() {
            return getDepartureCalender().getTimeInMillis();
        }

        @Override
        public long getRetureDate() {
            return 0;
        }

        @Override
        public String getDepartureAirport() {
            return m_fromFragment.getText();
        }

        @Override
        public String getDestinationAirport() {
            return m_toFragment.getText();
        }
    };

    public Calendar getDepartureCalender(){
        return m_departureDateFragment.getSelectDate();
    }

    public String getToIAIT(){
        return m_toFragment.getIAIT();
    }

    public String getFromIAIT(){
        return m_fromFragment.getIAIT();
    }

    public void setFromIataAndText(String iata,String text){
        if(null != m_fromFragment){
            m_fromFragment.setIAIT(iata);
            m_fromFragment.setText(text);
        }
    }

    public void setToIataAndText(String iata,String text){
        if(null != m_toFragment){
            m_toFragment.setIAIT(iata);
            m_toFragment.setText(text);
        }
    }

    public void setDepartureDateAndText(Calendar date, String text){
        if(null != m_departureDateFragment) {
            m_departureDateFragment.setSelectDate(date);
            m_departureDateFragment.setText(text);
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.setViewSize(view.findViewById(R.id.rl_title), ViewGroup.LayoutParams.MATCH_PARENT,32);
        vScaleDef.setViewSize(m_tvMultiStopValue, ViewGroup.LayoutParams.MATCH_PARENT,22.7);
        vScaleDef.setTextSize(16, m_tvMultiStopValue);
        vScaleDef.setMargins(m_tvMultiStopValue, 30, 0, 30, 0);
        vScaleDef.setMargins(view.findViewById(R.id.fl_from), 0, 20, 0, 0);
        vScaleDef.setMargins(view.findViewById(R.id.fl_to), 0, 6, 0, 0);
        vScaleDef.setMargins(view.findViewById(R.id.fl_departure_date), 0, 6, 0, 0);
        vScaleDef.selfAdjustSameScaleView(m_ivGarbage, 24, 24);
        vScaleDef.setMargins(m_ivGarbage, 0, 0, 19, 0);
    }

    @Override
    protected void setOnParameterAndListener(View view) {
        m_departureDateFragment.setDropDownListener(this);
        m_ivGarbage.setOnClickListener(this);

        m_fromFragment.setOnCIChooseAirportTextFragmentClick(onCIChooseAirportTextFragmentClickForFrom);
        m_toFragment.setOnCIChooseAirportTextFragmentClick(onCIChooseAirportTextFragmentClickForTo);

        /**
         * 選擇出發地就初始化目的地欄位物件
         */
        m_fromFragment.setAfterTextChangedListener(new CIBaseTextFieldFragment.afterTextChangedListener() {
            @Override
            public void afterTextChangedListener(Editable editable) {
                m_toFragment.setText("");
                m_toFragment.setIAIT("");
            }
        });
    }

    /**
     * 出發地監聽，主要是點擊欄位時會被觸發，固定都會開啟選擇機場的視窗，所以isOpenSelectPage()會回傳true
     * 出發地欄位開啟選單時不會去調用getFromIAIT()
     */
    CIChooseAirportTextFieldFragment.OnCIChooseAirportTextFragmentClick onCIChooseAirportTextFragmentClickForFrom = new CIChooseAirportTextFieldFragment.OnCIChooseAirportTextFragmentClick() {

        @Override
        public boolean isOpenSelectPage() {
            return true;//false 就是不打開選擇機場頁面
        }

        @Override
        public String getFromIAIT() {
            return m_fromFragment.getIAIT();
        }
    };


    /**
     * 目的地監聽，主要是點擊欄位時會被觸發，如果判斷出發地(m_fromFragment)尚未被選擇，
     * 則會跳出提示視窗要求選擇，則isOpenSelectPage()會回傳false，反之亦然；如果已經選
     * 擇出發地了，則會將跳出選單，另外目的地欄位開啟時會呼叫getFromIAIT()去取得出發地的IAIT
     */
    CIChooseAirportTextFieldFragment.OnCIChooseAirportTextFragmentClick onCIChooseAirportTextFragmentClickForTo = new CIChooseAirportTextFieldFragment.OnCIChooseAirportTextFragmentClick() {
        @Override
        public boolean isOpenSelectPage() {
            if ((m_fromFragment).getIAIT().equals("")) {

                showDialog(getString(R.string.warning)
                        ,String.format(getString(R.string.please_input_field), getString(R.string.from))
                        //,getResources().getString(R.string.select_error_msg) + " " + getString(R.string.from)
                        ,getString(R.string.confirm));

                return false;//false 就是不打開選擇機場頁面
            } else {
                return true;
            }
        }

        @Override
        public String getFromIAIT() {
            return m_fromFragment.getIAIT();
        }
    };

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
    public void onLanguageChangeUpdateUI() {

    }

    public void setNumber(int number){
        String str = String.format(getString(R.string.book_ticket_multi_stop_flight), number);
        m_tvMultiStopValue.setText(str);
    }

    public void setGarbageIconVisible(int isVisible){
        m_ivGarbage.setVisibility(isVisible);
    }

    @Override
    public void onDropDown(CITextFieldFragment.TypeMode mode, View v, String tag) {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_garbage:
                if(null != m_listener){
                    m_listener.onFragmentDeleted(this);
                }
                break;
        }
    }

    public interface onFragmentDeletedListener{
        void onFragmentDeleted(Fragment fragment);
    }

    public void setOnFragmentDeletedListener(onFragmentDeletedListener listener){
        m_listener = listener;
    }


}
