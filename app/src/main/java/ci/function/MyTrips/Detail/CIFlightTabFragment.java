package ci.function.MyTrips.Detail;

import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.ui.WeatherCard.CIFlightWeatherView;
import ci.ui.define.ViewScaleDef;

/**
 * Created by kevincheng on 2016/3/3.
 */
public class CIFlightTabFragment extends BaseFragment{

    public interface OnFlightTabFragmentParameter {
        //取得要查詢天氣的機場代碼(目的地)
        String GetAirName();
    }

    private OnFlightTabFragmentParameter    m_Parameter;
    private CIFlightWeatherView             m_FlightWeatherView;
    private String                          m_strLocal;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_my_trips_detial_flight_tab;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_FlightWeatherView = (CIFlightWeatherView) view.findViewById(R.id.weatherView);

        if ( null != m_Parameter ){
            if ( null != m_Parameter.GetAirName() )
                m_strLocal = m_Parameter.GetAirName();
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        //tabLayout本身高度43.2
        //tabLayout~flightDetail中間空白高度需為226.8
        //而viewPager本身有設定paddingTop23.5
        //所以中間需再留白203.3(參考zeplin)
        //2017.1.6 留白空間要再多加30
        vScaleDef.setViewSize(view.findViewById(R.id.v_empty), ViewGroup.LayoutParams.MATCH_PARENT, 233.3);
        vScaleDef.setMargins(view.findViewById(R.id.fragment1),10,0,10,0);
        vScaleDef.setMargins(view.findViewById(R.id.fragment2),10,10,10,0);
        vScaleDef.setMargins(view.findViewById(R.id.fragment3),10,10,10,20);
        vScaleDef.setMargins(view.findViewById(R.id.weatherView), 10, 10, 10, 0);
    }

    @Override
    protected void setOnParameterAndListener(View view) {
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        CIFlightDetialFragment flightDetialFragment = new CIFlightDetialFragment();
        flightDetialFragment.setArguments(getArguments());
        CIFlightAirCraftFragment flightAirCraftFragment = new CIFlightAirCraftFragment();
        flightAirCraftFragment.setArguments(getArguments());
        CIFlightSupportFragment flightSupportFragment = new CIFlightSupportFragment();
        flightSupportFragment.setArguments(getArguments());
        transaction.replace(R.id.fragment1, flightDetialFragment, CIFlightDetialFragment.class.getSimpleName());
        transaction.replace(R.id.fragment2, flightAirCraftFragment, CIFlightAirCraftFragment.class.getSimpleName());
        transaction.replace(R.id.fragment3, flightSupportFragment, CIFlightSupportFragment.class.getSimpleName());
        transaction.commit();
    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

    public void uiSetParameterListener(OnFlightTabFragmentParameter onParameter) {
        m_Parameter = onParameter;
    }

    @Override
    public void onResume() {
        super.onResume();

        if ( null != m_FlightWeatherView)
            m_FlightWeatherView.seachWeather(m_strLocal);
    }

    public void seachWeather(){
        if ( null != m_FlightWeatherView)
            m_FlightWeatherView.seachWeather(m_strLocal);
    }
}
