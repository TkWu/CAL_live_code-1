
package ci.ui.WeatherCard.resultData;

import org.json.JSONObject;

//當前氣象資訊
public class CIWeatherResp_Condition {
    private int m_iCode;
    private int m_iTemperature;
    private String m_strDescription;

    public int getCode() {
        return m_iCode;
    }

    public int getTemperature() {
        return m_iTemperature;
    }

    public String getDescription() {
        return m_strDescription;
    }

    public void DecodeJSON(JSONObject data) {
        //Weather id
        m_iCode = data.optInt("code");

        //溫度
        //m_iTemperature = data.optInt("temp");

        //20190508 weather api 對應新版yahoo 修正
        m_iTemperature = data.optInt("temperature");

        //氣象名稱(晴天/陰天...之類的),目前不會用到
        m_strDescription = data.optString("text");
    }
}
