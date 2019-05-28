
package ci.ui.WeatherCard.resultData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ci.ui.WeatherCard.resultData.item.CIForecastItem;

//當前氣象資訊
public class CIWeatherResp_Forecast {
    private int iLength	= 0;

    private ArrayList<CIForecastItem> m_arForecastList = new ArrayList<>();

    /**取得天氣預報列表*/
    public ArrayList<CIForecastItem> GetForecastList(){
        return m_arForecastList;
    }

    public void DecodeJSON(JSONArray data) {

        m_arForecastList.clear();

        if ( null != data && data.length() > 0 ){
            iLength = data.length();
        }

        //只要取五天的資料就好
        if ( iLength > 5 ){
            iLength = 5;
        }

        for ( int nIdx = 0; nIdx < iLength; nIdx++ ){

            JSONObject jsObj = data.optJSONObject(nIdx);

            CIForecastItem item = new CIForecastItem();

            if ( jsObj.has("text") ){
                item.m_strCurrently = jsObj.optString("text");
            }
            if ( jsObj.has("day") ){
                item.m_strDay = jsObj.optString("day");
            }
            if ( jsObj.has("high") ){
                //item.m_strHigh = jsObj.optString("high");
                item.m_strHigh = String.valueOf(jsObj.optInt("high"));
            }
            if ( jsObj.has("low") ){
                //item.m_strLow = jsObj.optString("low");
                item.m_strLow = String.valueOf(jsObj.optInt("low"));
            }
            if ( jsObj.has("code") ){
                //item.m_strCode = jsObj.optString("code");
                item.m_strCode = String.valueOf(jsObj.optInt("code"));
            }

            m_arForecastList.add(item);
        }
    }
}
