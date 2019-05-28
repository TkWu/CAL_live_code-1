package ci.ui.WeatherCard;

import ci.ui.WeatherCard.resultData.CIWeatherResp;

/**
 * Created by jlchen on 2016/4/15.
 */
public class CIFlightWeatherViewPresenter implements CIFlightWeatherViewMode.Callback {

    private String                      m_strLocation   = null;
    private CIFlightWeatherViewCallback m_view          = null;
    private CIFlightWeatherViewMode     m_model         = null;

    //經度
    private String                      strLongitude    = "";
    //緯度
    private String                      strLatitude     = "";

//    public CIFlightWeatherViewPresenter(CIFlightWeatherViewCallback view, String strLocation) {
//        this.m_view         = view;
//        this.m_strLocation  = strLocation;
//    }

//    public void loadWeatherData() {
//        if ( null != m_view )
//            m_view.showProgress();
//
//        if ( null == m_model )
//            m_model = new CIFlightWeatherViewMode();
//
//        m_model.findData(m_strLocation, this);
//    }

    //2016-12-01 Modify by Ryan for 調整為抓經緯度查天氣不抓機場代碼
    public CIFlightWeatherViewPresenter(CIFlightWeatherViewCallback view) {
        this.m_view         = view;
    }

    /**帶入緯度以及經度*/
    public void loadWeatherData( String strLatitude, String strLongitude ) {
        if ( null != m_view )
            m_view.showProgress();

        if ( null == m_model )
            m_model = new CIFlightWeatherViewMode();

        m_model.findData(strLatitude, strLongitude, this);
    }

    @Override
    public void onSuccess(CIWeatherResp resultData) {
        if ( null != m_view ){
            m_view.hideProgress();
            m_view.onDataBind(resultData);
        }
    }

    @Override
    public void onError(String strError) {
        if ( null != m_view ){
            m_view.hideProgress();
            m_view.showErrorText(strError);
        }
    }
}
