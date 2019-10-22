package ci.function.ManageMiles;

import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.function.TimeTable.CISelectDepartureAirpotActivity;
import ci.ui.TextField.Base.CIBaseTextFieldFragment;
import ci.ui.TextField.CIChooseAirportTextFieldFragment;
import ci.ui.TextField.CIDateForWithInSixMonthsTextFieldFragment;
import ci.ui.TextField.CIOnlyNumberTextFieldFragment;
import ci.ui.define.ViewScaleDef;

/**
 * Created by kevincheng on 2016/3/11.
 */
public class CIFlightDetialInputFragment extends BaseFragment
    implements View.OnClickListener,
                View.OnTouchListener{
    private CIChooseAirportTextFieldFragment m_fromFragment,
                                                m_toFragment;
    private CIOnlyNumberTextFieldFragment m_flightNumberFragment,
                                                m_tickerNumberFragment;
    private CIDateForWithInSixMonthsTextFieldFragment m_datePickerFragment;
    private TextView                    m_tvItem        = null;
    private ImageView                   m_ivCI          = null;
    private ImageView                   m_ivAE          = null;
    private ImageView                   m_ivGarbage     = null;
    private TextView                    m_tvCI          = null;
    private TextView                    m_tvAE          = null;
    private View                        m_view          = null;
    private boolean                     m_bFlightNumberPrefix = true;
    private onFragmentDeletedListener   m_listener            = null;
    private LinearLayout m_llflightTicket=null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_flight_detial_input;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_tvItem = (TextView) view.findViewById(R.id.tv_item);
        m_ivGarbage = (ImageView) view.findViewById(R.id.iv_garbage);
        m_ivCI = (ImageView) view.findViewById(R.id.iv_flight_nubmer_ci);
        m_ivAE = (ImageView) view.findViewById(R.id.iv_flight_nubmer_ae);
        m_tvCI = (TextView) view.findViewById(R.id.tv_ci);
        m_tvAE = (TextView) view.findViewById(R.id.tv_ae);
        m_llflightTicket= (LinearLayout) view.findViewById(R.id.ll_ticketnumber);

        m_view = view;

        m_fromFragment = CIChooseAirportTextFieldFragment.newInstance(
                getString(R.string.departure_airport),
                R.drawable.ic_departure_2,
                R.drawable.ic_departure_4,
                false,
                CISelectDepartureAirpotActivity.FLIGHT_STATUS);
        m_toFragment = CIChooseAirportTextFieldFragment.newInstance(
                getString(R.string.arrival_airport),
                R.drawable.ic_departure_2,
                R.drawable.ic_departure_4,
                true,
                CISelectDepartureAirpotActivity.FLIGHT_STATUS);
        m_datePickerFragment = CIDateForWithInSixMonthsTextFieldFragment.newInstance(getString(R.string.departure_date_only_within_6_months));
        m_flightNumberFragment = CIOnlyNumberTextFieldFragment.newInstance(getString(R.string.flight_number));
        m_tickerNumberFragment = CIOnlyNumberTextFieldFragment.newInstance(getString(R.string.manage_miles_ticket_number));
        m_flightNumberFragment.setWidth(200);
        m_llflightTicket.setVisibility(View.GONE);
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        if(null == view.findViewById(R.id.ll_root)){
            return;
        }
        vScaleDef.selfAdjustAllView(view.findViewById(R.id.ll_root));
        //小數點額外設定
        vScaleDef.setMargins(view.findViewById(R.id.fragment5), 20, 6.3, 20, 0);
        vScaleDef.selfAdjustSameScaleView(view.findViewById(R.id.iv_flight_nubmer_ci), 40, 40);
        vScaleDef.selfAdjustSameScaleView(view.findViewById(R.id.iv_flight_nubmer_ae), 40, 40);
        vScaleDef.selfAdjustSameScaleView(m_ivGarbage, 24, 24);
        vScaleDef.setMargins(m_ivGarbage, 0, 0, 19, 0);

    }

    @Override
    protected void setOnParameterAndListener(View view) {
        m_ivCI.setOnClickListener(this);
        m_ivAE.setOnClickListener(this);
        m_ivGarbage.setOnClickListener(this);
        view.findViewById(R.id.root).setOnTouchListener(this);

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
                        //2016-11-09 Ryan , 更換錯誤訊息顯示方式
                        ,String.format(getResources().getString(R.string.please_input_field), getResources().getString(R.string.from))
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

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction
                .replace(R.id.fragment1, m_fromFragment, m_fromFragment.getClass().getSimpleName())
                .replace(R.id.fragment2, m_toFragment, m_toFragment.getClass().getSimpleName())
                .replace(R.id.fragment3, m_datePickerFragment, m_datePickerFragment.getClass().getSimpleName())
                .replace(R.id.fragment4, m_flightNumberFragment, m_flightNumberFragment.getClass().getSimpleName())
                .replace(R.id.fragment5, m_tickerNumberFragment, m_tickerNumberFragment.getClass().getSimpleName())
                .commitAllowingStateLoss();

        m_view.post(new Runnable() {
            @Override
            public void run() {
                m_flightNumberFragment.setMaxLenght(4);
                m_tickerNumberFragment.setMaxLenght(13);
            }
        });
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

    public Entity getData(){
        Entity data = new Entity();
        data.departureAirport = m_fromFragment.getIAIT();
        data.arrivalAirport = m_toFragment.getIAIT();
        data.departureDate = m_datePickerFragment.getFormatedDate();
        String company;
        if(true == m_bFlightNumberPrefix){
            company = "CI";
        } else {
            company = "AE";
        }
        data.company = company;
        data.flightNumber = m_flightNumberFragment.getText().trim();
        data.tickerNumber = m_tickerNumberFragment.getText().trim();
        return data;
    }

    //哩程補登不需輸入機票號碼欄位，若回覆需輸入再輸
    public void setFlightNumberFragmentVisivle(){
        m_llflightTicket.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_flight_nubmer_ci:
            case R.id.iv_flight_nubmer_ae:
                m_bFlightNumberPrefix = setViewIconSwitch(m_ivCI, m_tvCI, m_bFlightNumberPrefix);
                setViewIconSwitch(m_ivAE, m_tvAE, m_bFlightNumberPrefix);
                break;
            case R.id.iv_garbage:
                if(null != m_listener){
                    m_listener.onFragmentDeleted(this);
                }
                break;
        }
    }

    private boolean setViewIconSwitch(View v,TextView tv,boolean isOn){
        if(true == isOn){
            ((ImageView)v).setImageResource(R.drawable.btn_flight_number_n);
            (tv).setTextColor(getResources().getColor(R.color.white_four));
            return false;
        } else {
            ((ImageView)v).setImageResource(R.drawable.btn_flight_number_enable);
            (tv).setTextColor(getResources().getColor(R.color.french_blue));
            return true;
        }

    }

    public void setImeOption(int imeOption){
        m_tickerNumberFragment.setImeOptions(imeOption);
    }

    public void setNumber(int number){
        //設定項目字串
        String flight_detail = getString(R.string.first_flight_detail);
        String head = String.format(flight_detail, String.valueOf(number));
        m_tvItem.setText(head);
    }

    public static class Entity {
        String  departureAirport;
        String  arrivalAirport;
        String  company;
        String  departureDate;
        String  flightNumber;
        String  tickerNumber;
    }

    public interface onFragmentDeletedListener{
        void onFragmentDeleted(Fragment fragment);
    }

    public void setOnFragmentDeletedListener(onFragmentDeletedListener listener){
        m_listener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        HidekeyBoard();
        return false;
    }

    public void setGarbageIconVisible(int isVisible){
        m_ivGarbage.setVisibility(isVisible);
    }

}
