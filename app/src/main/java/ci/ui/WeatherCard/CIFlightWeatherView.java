package ci.ui.WeatherCard;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import ci.function.Core.SLog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Base.BaseView;
import ci.ui.WeatherCard.Adapter.CIFlightWeatherAdapter;
import ci.ui.WeatherCard.resultData.CIWeatherResp;
import ci.ui.WeatherCard.resultData.item.CIForecastItem;
import ci.ui.WebView.CIWithoutInternetActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Presenter.CIInquiryFlightStationPresenter;

/**
 * Created by jlchen on 2016/2/3.
 * 飛行狀態-氣象資訊
 */
public class CIFlightWeatherView extends BaseView implements CIFlightWeatherViewCallback {

    private CIFlightWeatherViewPresenter m_Presenter;

    private LinearLayout m_llFlightWeatherV = null;

    private LinearLayout m_llTitle = null;
    private View m_vTitle = null;
    private TextView m_tvTitle = null;

    private RelativeLayout m_rlProBar = null;
    private TextView m_tvErrorMsg = null;
    private ProgressBar m_proBar = null;

    private RelativeLayout m_rlWeather = null;
    private TextView m_tvLocation = null;
    private TextView m_tvNow = null;
    private ImageView m_ivNowWeather = null;
    private TextView m_tvNowTemp = null;
    private TextView m_tvUnit = null;

    private RelativeLayout m_rlWeatherRight = null;
    private LinearLayout m_llCurrently = null;
    private TextView m_tvCurrently = null;
    private TextView m_tvCurrentlyData = null;
    private LinearLayout m_llHumidity = null;
    private TextView m_tvHumidity = null;
    private TextView m_tvHumidityData = null;
    private LinearLayout m_llVisibility = null;
    private TextView m_tvVisibility = null;
    private TextView m_tvVisibilityData = null;

    /*TMP WEATHER */
//    private RelativeLayout m_rlTmpWeatherLink = null;
//    private ImageView m_iv_ic_list_arrow_g2 = null;
//    private TextView m_tmp_wtather_text = null;

    private View m_vDiv = null;
    private GridView m_gvWeather = null;

    private String m_strLocation = "";
    private boolean m_bGetData = false;

    private static final double DEF_TEXT_SIZE_TITLE = 16;
    private static final double DEF_TEXT_SIZE_LOCATION = 20;
    private static final double DEF_TEXT_SIZE_TEMP = 40;
    private static final double DEF_TEXT_SIZE_CONTENT = 13;
    private static final double DEF_IMAGE_BIG_WIDTH = 54;

    public CIFlightWeatherView(Context context) {
        super(context);
        initial();
    }

    public CIFlightWeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_view_weather;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater) {

        m_llFlightWeatherV = (LinearLayout) findViewById(R.id.ll_flight_weather_view);

        //氣象資訊
        m_llTitle = (LinearLayout) findViewById(R.id.llayout_title);
        m_vTitle = (View) findViewById(R.id.v_title);
        m_tvTitle = (TextView) findViewById(R.id.tv_title);

        //loading天氣資料時的轉圈圈 & 沒網路或撈不到資料時要顯示的錯誤訊息
        m_rlProBar = (RelativeLayout) findViewById(R.id.rl_proBar);
        m_tvErrorMsg = (TextView) findViewById(R.id.tv_msg);
        m_proBar = (ProgressBar) findViewById(R.id.proBar_load);

        //氣象資訊內容(loading完才會顯示)
        m_rlWeather = (RelativeLayout) findViewById(R.id.rlayout_weather);

        //(左上方) 目的地名稱. 氣象圖示. 現在溫度等資訊
        m_tvLocation = (TextView) findViewById(R.id.tv_location);
        m_tvNow = (TextView) findViewById(R.id.tv_now);
        m_ivNowWeather = (ImageView) findViewById(R.id.iv_big_weather);
        m_tvNowTemp = (TextView) findViewById(R.id.tv_now_temp);
        m_tvUnit = (TextView) findViewById(R.id.tv_unit);

        //(右上方) 氣象及濕度等資訊
        m_rlWeatherRight = (RelativeLayout) findViewById(R.id.rl_weather_right_view);
        m_llCurrently = (LinearLayout) findViewById(R.id.ll_currently);
        m_tvCurrently = (TextView) findViewById(R.id.tv_currently);
        m_tvCurrentlyData = (TextView) findViewById(R.id.tv_currently_data);
        m_llHumidity = (LinearLayout) findViewById(R.id.ll_humidity);
        m_tvHumidity = (TextView) findViewById(R.id.tv_humidity);
        m_tvHumidityData = (TextView) findViewById(R.id.tv_humidity_data);
        m_llVisibility = (LinearLayout) findViewById(R.id.ll_visibility);
        m_tvVisibility = (TextView) findViewById(R.id.tv_visibility);
        m_tvVisibilityData = (TextView) findViewById(R.id.tv_visibility_data);

        /*TMP WEATHER*/
//        m_rlTmpWeatherLink = (RelativeLayout) findViewById(R.id.rl_tmp_wtather_click);
//        m_iv_ic_list_arrow_g2 = (ImageView) findViewById(R.id.iv_ic_list_arrow_g2);
//        m_tmp_wtather_text = (TextView) findViewById(R.id.tmp_wtather_text);

        //分隔線 和 下方的氣象預報資訊
        m_vDiv = findViewById(R.id.div);
        m_gvWeather = (GridView) findViewById(R.id.gv_weather);

        showProgress();
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {

        //氣象資訊title欄 左間隔10px
        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) m_llTitle.getLayoutParams();
        lParams.leftMargin = vScaleDef.getLayoutWidth(10);

        //藍條 寬2, 高16
        lParams = (LinearLayout.LayoutParams) m_vTitle.getLayoutParams();
        lParams.topMargin = vScaleDef.getLayoutHeight(12.1);
        lParams.width = vScaleDef.getLayoutWidth(2);
        lParams.height = vScaleDef.getLayoutHeight(16);

        //氣象資訊字樣 於 藍條 左方,其間隔8px, 高21, 字串大小為16
        lParams = (LinearLayout.LayoutParams) m_tvTitle.getLayoutParams();
        lParams.topMargin = vScaleDef.getLayoutHeight(10);
        lParams.leftMargin = vScaleDef.getLayoutWidth(8);
        lParams.height = vScaleDef.getLayoutHeight(20);
        vScaleDef.setTextSize(DEF_TEXT_SIZE_TITLE, m_tvTitle);

        RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) m_rlWeather.getLayoutParams();
        rParams.height = vScaleDef.getLayoutHeight(231.7);

        m_rlProBar.getLayoutParams().height = vScaleDef.getLayoutHeight(231.7);

        //目的地名稱字串 與title間隔及與左間隔皆為19.7, 高23.7, 字串大小20
        rParams = (RelativeLayout.LayoutParams) m_tvLocation.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(20);
        rParams.leftMargin = vScaleDef.getLayoutWidth(20);
        rParams.height = vScaleDef.getLayoutHeight(23.7);
        rParams.width = vScaleDef.getLayoutWidth(149);
        vScaleDef.setTextSize(DEF_TEXT_SIZE_LOCATION, m_tvLocation);
        m_tvLocation.setMaxHeight(vScaleDef.getLayoutWidth(200));

        //目的地右邊的now字串 與title間隔26.7, 高15.3, 字串大小13
        rParams = (RelativeLayout.LayoutParams) m_tvNow.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(20);
        rParams.leftMargin = vScaleDef.getLayoutWidth(5);
        rParams.height = vScaleDef.getLayoutHeight(23.7);
        vScaleDef.setTextSize(DEF_TEXT_SIZE_CONTENT, m_tvNow);

        //目前天氣的大圖片 與目的地間隔8.7, 左間隔為19.7, 寬高皆為54
        rParams = (RelativeLayout.LayoutParams) m_ivNowWeather.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(8);
        rParams.leftMargin = vScaleDef.getLayoutWidth(20);
        rParams.bottomMargin = vScaleDef.getLayoutHeight(24);
        rParams.width = vScaleDef.getLayoutMinUnit(DEF_IMAGE_BIG_WIDTH);
        rParams.height = vScaleDef.getLayoutMinUnit(DEF_IMAGE_BIG_WIDTH);

        //現在溫度(數字) 與目的地間隔11.4, 與左方圖片間隔10, 高48, 字串大小40
        rParams = (RelativeLayout.LayoutParams) m_tvNowTemp.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(11.4);
        rParams.leftMargin = vScaleDef.getLayoutWidth(10);
        rParams.height = vScaleDef.getLayoutHeight(48);
        vScaleDef.setTextSize(DEF_TEXT_SIZE_TEMP, m_tvNowTemp);

        //現在溫度(單位) 與目的地間隔18.1, 高15.7, 字串大小13
        rParams = (RelativeLayout.LayoutParams) m_tvUnit.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(18.1);
        rParams.height = vScaleDef.getLayoutHeight(15.7);
        vScaleDef.setTextSize(DEF_TEXT_SIZE_CONTENT, m_tvUnit);

        //當前氣象及濕度資訊欄 , 與大圖間隔100, 與下方分隔線間隔20px
        rParams = (RelativeLayout.LayoutParams) m_rlWeatherRight.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(6.2);
        rParams.leftMargin = vScaleDef.getLayoutWidth(5);
        rParams.width = vScaleDef.getLayoutMinUnit(146);

        //當前氣象 字串大小13
        rParams = (RelativeLayout.LayoutParams) m_llCurrently.getLayoutParams();
        vScaleDef.setTextSize(DEF_TEXT_SIZE_CONTENT, m_tvCurrently);
        vScaleDef.setTextSize(DEF_TEXT_SIZE_CONTENT, m_tvCurrentlyData);

        //當前濕度 字串大小13
        rParams = (RelativeLayout.LayoutParams) m_llHumidity.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(6);
        vScaleDef.setTextSize(DEF_TEXT_SIZE_CONTENT, m_tvHumidity);
        vScaleDef.setTextSize(DEF_TEXT_SIZE_CONTENT, m_tvHumidityData);

        //能見度 字串大小13
        rParams = (RelativeLayout.LayoutParams) m_llVisibility.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(6);
        vScaleDef.setTextSize(DEF_TEXT_SIZE_CONTENT, m_tvVisibility);
        vScaleDef.setTextSize(DEF_TEXT_SIZE_CONTENT, m_tvVisibilityData);

        //分隔線 與上方大圖間隔27.3, 左右間隔20, 高1
        rParams = (RelativeLayout.LayoutParams) m_vDiv.getLayoutParams();
        rParams.leftMargin = vScaleDef.getLayoutWidth(20);
        rParams.rightMargin = vScaleDef.getLayoutWidth(20);
        rParams.height = vScaleDef.getLayoutHeight(1);

        //天氣預報欄 與上方分隔線間隔14, 左間隔20, 右間隔20, 下間隔20.3px
        rParams = (RelativeLayout.LayoutParams) m_gvWeather.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(14);
        rParams.leftMargin = vScaleDef.getLayoutWidth(20);
        rParams.rightMargin = vScaleDef.getLayoutWidth(20);
        rParams.bottomMargin = vScaleDef.getLayoutHeight(20.3);
        rParams.height = vScaleDef.getLayoutHeight(66.7);
        m_gvWeather.setLayoutParams(rParams);

        /*TMP WEATHER 字串大小為16 */
//        lParams = (LinearLayout.LayoutParams) m_rlTmpWeatherLink.getLayoutParams();
//        lParams.leftMargin = vScaleDef.getLayoutHeight(5);
//        lParams.rightMargin = vScaleDef.getLayoutHeight(5);
//        rParams = (RelativeLayout.LayoutParams) m_iv_ic_list_arrow_g2.getLayoutParams();
//        rParams.width = vScaleDef.getLayoutMinUnit(24);
//        rParams.height = vScaleDef.getLayoutMinUnit(24);

        //error msg
        vScaleDef.setTextSize(DEF_TEXT_SIZE_TITLE, m_tvErrorMsg);
        rParams = (RelativeLayout.LayoutParams) m_tvErrorMsg.getLayoutParams();
        rParams.leftMargin = vScaleDef.getLayoutWidth(20);
        rParams.rightMargin = vScaleDef.getLayoutWidth(20);
    }

    public void seachWeather(String strLocation) {
        m_strLocation = strLocation;

        //已經找過天氣資料了
        if (true == m_bGetData)
            return;

        if (!AppInfo.getInstance(m_Context).bIsNetworkAvailable()) {
            //轉圈圈隱藏
            hideProgress();

            //顯示錯誤訊息
            showErrorText(m_Context.getString(R.string.system_no_network_connection));
        } else {
            if (null == m_Presenter)
                m_Presenter = new CIFlightWeatherViewPresenter(CIFlightWeatherView.this);
            //2016-12-01 Modify by Ryan for 調整為抓經緯度查天氣不抓機場代碼
            //m_Presenter = new CIFlightWeatherViewPresenter(CIFlightWeatherView.this, strLocation);

            CIFlightStationEntity flightStationEntity = CIInquiryFlightStationPresenter.getInstance(null, CIInquiryFlightStationPresenter.ESource.TimeTable).getStationInfoByIATA(strLocation);
            if (null != flightStationEntity &&
                    false == TextUtils.isEmpty(flightStationEntity.latitude) &&
                    false == TextUtils.isEmpty(flightStationEntity.longitude)) {

                m_Presenter.loadWeatherData(flightStationEntity.latitude, flightStationEntity.longitude);
            } else {
                SLog.d("[CAL]", "weather getStationInfoByIATA Error ");
                onDataBind(null);
            }
        }
    }

    @Override
    public void showProgress() {
        if (null != m_rlProBar)
            m_rlProBar.setVisibility(VISIBLE);

        if (null != m_rlWeather)
            m_rlWeather.setVisibility(INVISIBLE);
    }

    @Override
    public void hideProgress() {
        if (null != m_rlProBar)
            m_rlProBar.setVisibility(GONE);
    }

    //取得天氣資料成功
    @Override
    public void onDataBind(CIWeatherResp resultData) {

        if (null == resultData) {
            showErrorText(m_Context.getString(R.string.no_match_data));
            return;
        }

        if (null != m_rlWeather)
            m_rlWeather.setVisibility(VISIBLE);

        if (null != m_tvErrorMsg)
            m_tvErrorMsg.setVisibility(GONE);

        //擷取天氣預報清單顯示於gridview
        ArrayList<CIForecastItem> arrayList = resultData.getForecast().GetForecastList();

        if (null != arrayList && 0 < arrayList.size()) {
            CIFlightWeatherAdapter adapter = new CIFlightWeatherAdapter(m_Context, arrayList);
            if (null != m_gvWeather)
                m_gvWeather.setAdapter(adapter);
        }

        //目的地 (顯示地區地名)
        if (null != m_tvLocation && null != resultData.getLocation()) {
            m_tvLocation.setText(resultData.getLocation());
        } else {
            m_tvLocation.setText(m_strLocation);
        }

        //現在溫度
        if (null != m_tvNowTemp)
            m_tvNowTemp.setText(String.valueOf(resultData.getCondition().getTemperature()));

        //依取得的weatherCode,顯示對應的Currently狀態
        String strWeatherCode = String.valueOf(resultData.getCondition().getCode());
        String strWeatherCurrently = "";
        try {
            strWeatherCurrently = m_Context.getString(
                    AppInfo.getInstance(m_Context).GetStringResourceId(
                            m_Context.getString(R.string.weather_code), strWeatherCode));
        } catch (Exception e) {
            strWeatherCurrently = m_Context.getString(R.string.weather_code_3200);
        }

        if (null != m_tvCurrentlyData)
            m_tvCurrentlyData.setText(strWeatherCurrently);

        //當前氣象的大圖
        if (null != m_ivNowWeather) {
            //2016-12-01 Modify By Ryan for 設定預設圖檔
            int iImgRes = AppInfo.getInstance(m_Context).GetIconResourceId(m_Context.getString(R.string.weather_code), strWeatherCode);
            if (iImgRes <= 0) {
                m_ivNowWeather.setImageResource(R.drawable.weather_code_3200);
            } else {
                m_ivNowWeather.setImageResource(iImgRes);
            }
        }

        //濕度
        if (null != m_tvHumidityData && null != resultData.getHumidity()) {
            m_tvHumidityData.setText(resultData.getHumidity() + "%");
        } else {
            m_tvHumidityData.setText("");
        }

        //能見度
        if (null != m_tvVisibilityData && null != resultData.getVisibility()) {
            m_tvVisibilityData.setText(resultData.getVisibility());
        } else {
            m_tvVisibilityData.setText("");
        }

        m_bGetData = true;

    }

    //取得天氣資料失敗 顯示錯誤訊息
    @Override
    public void showErrorText(String strErr) {
        if (null != m_rlWeather)
            m_rlWeather.setVisibility(INVISIBLE);

        if (null != m_rlProBar)
            m_rlProBar.setVisibility(VISIBLE);
        if (null != m_proBar)
            m_proBar.setVisibility(GONE);

        if (null != m_tvErrorMsg && null != strErr) {
            m_tvErrorMsg.setText(strErr);
            m_tvErrorMsg.setVisibility(VISIBLE);
        }
    }
}