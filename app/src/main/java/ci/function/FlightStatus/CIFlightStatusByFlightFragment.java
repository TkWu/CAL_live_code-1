package ci.function.FlightStatus;

import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.CIFlightNumberTextFieldFragment;
import ci.ui.define.ViewScaleDef;

/**
 * FlightStatus的ByFlight頁面，使用getSearchCIorAE與getFlightNumber回傳選取與輸入值
 * Created by flowmahuang on 2016/3/28.
 */
public class CIFlightStatusByFlightFragment extends BaseFragment implements View.OnClickListener {
    private CITextFieldFragment m_FlightNumber = null;
    private FrameLayout m_fragment = null;
    private RelativeLayout m_ButtonCI = null;
    private RelativeLayout m_ButtonAE = null;
    private TextView m_TextCI = null;
    private TextView m_TextAE = null;
    private boolean setSearchCIorAE = true;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_flight_status_by_flight;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_ButtonCI = (RelativeLayout) view.findViewById(R.id.button_ci);
        m_ButtonAE = (RelativeLayout) view.findViewById(R.id.button_ae);
        m_TextCI = (TextView) view.findViewById(R.id.text_ci);
        m_TextAE = (TextView) view.findViewById(R.id.text_ae);
        m_fragment = (FrameLayout) view.findViewById(R.id.fragment1);

        setSearchCIorAE = true; // edited by kevin
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
//        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root));
//        vScaleDef.selfAdjustSameScaleView(m_ButtonAE, 40, 40);
//        vScaleDef.selfAdjustSameScaleView(m_ButtonCI, 40, 40);

        vScaleDef.setPadding(view.findViewById(R.id.root), 20, 26, 20, 0);

        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams)m_ButtonCI.getLayoutParams();
        lParams.width = vScaleDef.getLayoutMinUnit(40);
        lParams.height = vScaleDef.getLayoutMinUnit(40);
        lParams.topMargin = vScaleDef.getLayoutHeight(16);
        lParams.leftMargin = vScaleDef.getLayoutWidth(10);

        vScaleDef.setTextSize(18, m_TextCI);

        lParams = (LinearLayout.LayoutParams)m_ButtonAE.getLayoutParams();
        lParams.width = vScaleDef.getLayoutMinUnit(40);
        lParams.height = vScaleDef.getLayoutMinUnit(40);
        lParams.topMargin = vScaleDef.getLayoutHeight(16);
        lParams.leftMargin = vScaleDef.getLayoutWidth(10);

        vScaleDef.setTextSize(18, m_TextAE);

        lParams = (LinearLayout.LayoutParams)view.findViewById(R.id.fragment1).getLayoutParams();
        lParams.leftMargin = vScaleDef.getLayoutWidth(20);
    }

    @Override
    protected void setOnParameterAndListener(View view) {
        m_ButtonCI.setOnClickListener(this);
        m_ButtonAE.setOnClickListener(this);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
        m_FlightNumber = CIFlightNumberTextFieldFragment.newInstance(
                getString(R.string.flight_number),
                CITextFieldFragment.TypeMode.NORMAL);
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(m_fragment.getId(), m_FlightNumber);
        transaction.commitAllowingStateLoss();

        //m_FlightNumber.setWidth(200);
    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_ci:
                if (setSearchCIorAE) return;
                m_ButtonCI.setBackgroundResource(R.drawable.btn_flight_number_enable);
                m_TextCI.setTextColor(ContextCompat.getColor(getActivity(), R.color.french_blue));
                m_ButtonAE.setBackgroundResource(R.drawable.btn_flight_number_n);
                m_TextAE.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_four));
                setSearchCIorAE = true;
                break;
            case R.id.button_ae:
                if (!setSearchCIorAE) return;
                m_ButtonAE.setBackgroundResource(R.drawable.btn_flight_number_enable);
                m_TextAE.setTextColor(ContextCompat.getColor(getActivity(), R.color.french_blue));
                m_ButtonCI.setBackgroundResource(R.drawable.btn_flight_number_n);
                m_TextCI.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_four));
                setSearchCIorAE = false;
                break;
        }
    }

    public boolean getSearchCIorAE() {
        return setSearchCIorAE;
    }

    public String getFlightNumber() {
        return m_FlightNumber.getText().toString();
    }

    public boolean checkNumInput() {
        boolean checkNum = true;
        if (m_FlightNumber.getText().isEmpty())
            checkNum = false;
        return checkNum;
    }

    /**
     * 設定航班編號
     * @param flightNumber
     */
    public void setFlightNumber(final String flightNumber){
        if(null != m_FlightNumber){
            m_FragmentHandler.post(new Runnable() {
                @Override
                public void run() {
                    m_FlightNumber.setText(flightNumber);
                }
            });
        }
        //edited by Kevin
    }

    /**
     * 設定航空公司
     * @param airline
     */
    public void setAirline(final String airline){
        if(null == m_ButtonCI || null == m_ButtonAE){
            return;
        }
        if(!TextUtils.isEmpty(airline)){
            if(airline.equals("CI")){
                setSearchCIorAE = false;
                onClick(m_ButtonCI);
            } else if(airline.equals("AE")){
                setSearchCIorAE = true;
                onClick(m_ButtonAE);
            }
        }
        //edited by Kevin
    }

}