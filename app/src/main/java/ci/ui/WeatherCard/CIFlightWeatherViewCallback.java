package ci.ui.WeatherCard;

import ci.ui.WeatherCard.resultData.CIWeatherResp;

/**
 * Created by jlchen on 2016/4/15.
 */
public interface CIFlightWeatherViewCallback {

    void showProgress();

    void hideProgress();

    void onDataBind(CIWeatherResp resultData);

    void showErrorText(String strErr);
}
