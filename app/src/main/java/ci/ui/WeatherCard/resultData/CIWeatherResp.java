package ci.ui.WeatherCard.resultData;

import org.json.JSONObject;

//yahoo api 所回傳的json, key=channel於此進行處理
public class CIWeatherResp {

    //錯誤訊息
    public String                   m_strError = null;

    /**地區*/
    private String                  m_strLocation;
    /**氣象資訊*/
    private CIWeatherResp_Forecast  m_Forecast;
    private CIWeatherResp_Condition m_Condition;
    /*濕度*/
    private String                  m_strHumidity;
    /**能見度*/
    private String                  m_strVisibility;
    /**溫度單位*/
    private String                  m_strUnits;

    public String getLocation() {
        return m_strLocation;
    }

    public CIWeatherResp_Forecast getForecast() {
        return m_Forecast;
    }

    public CIWeatherResp_Condition getCondition() {
        return m_Condition;
    }

    public String getHumidity() {
        return m_strHumidity;
    }

    public String getVisibility() {
        return m_strVisibility;
    }

    public String getUnits() {
        return m_strUnits;
    }

//    public void DecodeJSON(JSONObject data) {
//
//        //當前地區資料
//        JSONObject joLocation   = data.optJSONObject("location");
//        //地區
//        String strRegion        = joLocation.optString("region");
//        //國家
//        String strCountry       = joLocation.optString("country");
//        //城市
//        String strCity          = joLocation.optString("city");
//
//        m_strLocation           = strCity;
//        //String.format("%s, %s", locationData.optString("city"), (region.length() != 0 ? region : country));
//
//        //氣象查詢結果
//        JSONObject joItem       = data.optJSONObject("item");
//        //未來五天的天氣預測
//        m_Forecast              = new CIWeatherResp_Forecast();
//        m_Forecast.DecodeJSON(joItem.optJSONArray("forecast"));
//        //當前氣象
//        m_Condition             = new CIWeatherResp_Condition();
//        m_Condition.DecodeJSON(joItem.optJSONObject("condition"));
//
//        //大氣資料
//        JSONObject joAtmosphere = data.optJSONObject("atmosphere");
//        //濕度
//        m_strHumidity           = joAtmosphere.optString("humidity");
//        m_strVisibility         = joAtmosphere.optString("visibility");
//
//        //取得溫度單位(C/F)
//        JSONObject joUnits      = data.optJSONObject("units");
//        m_strUnits              = joUnits.optString("temperature");
//    }

    //20190508 weather api 對應新版yahoo 修正
    public void DecodeJSON(JSONObject data) {

        //當前地區資料
        JSONObject joLocation   = data.optJSONObject("location");
        //地區
        String strRegion        = joLocation.optString("region");
        //國家
        String strCountry       = joLocation.optString("country");
        //城市
        String strCity          = joLocation.optString("city");
        m_strLocation           = strCity;
        //String.format("%s, %s", locationData.optString("city"), (region.length() != 0 ? region : country));

        //氣象查詢結果
        //JSONObject joItem       = data.optJSONObject("item");
        //未來五天的天氣預測
        //m_Forecast              = new CIWeatherResp_Forecast();
        //m_Forecast.DecodeJSON(joItem.optJSONArray("forecast"));
        //m_Forecast.DecodeJSON(data.optJSONArray("forecasts"));

        //當前氣象
        JSONObject joItem       = data.optJSONObject("current_observation");

        m_Condition             = new CIWeatherResp_Condition();
        m_Condition.DecodeJSON(joItem.optJSONObject("condition"));

        //大氣資料
        //JSONObject joAtmosphere = data.optJSONObject("atmosphere");
        JSONObject joAtmosphere = joItem.optJSONObject("atmosphere");
        //濕度
        m_strHumidity           = joAtmosphere.optString("humidity");
        m_strVisibility         = joAtmosphere.optString("visibility");

        //取得溫度單位(C/F)
        //JSONObject joUnits      = data.optJSONObject("units");
        //m_strUnits              = joUnits.optString("temperature")
        m_strUnits= "C";
    }
}
