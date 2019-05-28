package com.chinaairlines.mobile30.Presenter;

import com.chinaairlines.mobile30.Config;
import com.chinaairlines.mobile30.base.SBaseAndroidTestCase;

import junit.framework.Assert;

import java.util.concurrent.TimeUnit;

import ci.ui.WeatherCard.CIFlightWeatherViewCallback;
import ci.ui.WeatherCard.CIFlightWeatherViewPresenter;
import ci.ui.WeatherCard.resultData.CIWeatherResp;
import ci.ws.Models.entities.CIFlightStationEntity;

/**
 * Created by jlchen on 2016/6/2.
 */
public class CIFlightWeatherWSPresenterTest extends SBaseAndroidTestCase {

    /**
     * 功能：驗證天氣Presenter與其對應的Model是否互動正確
     */

    //計數及順序的斷言用途
    private int                 m_iCount    = 0;

    /**
     *  PresenterTest不需要網路
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setWifiEnabled(false);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * 案例說明：驗證 loadWeatherData() 調用 CIFlightWeatherViewMode 的邏輯
     * 輸入參數：無
     * 斷言結果：showProgress(), hideProgress(), showErrorText()
     */
    public void testCase01() throws Exception {

        //2017-02-20 因應改用經緯度查天氣需求調整

        CIFlightWeatherViewPresenter presenter = new CIFlightWeatherViewPresenter(new CIFlightWeatherViewCallback() {
            @Override
            public void showProgress() {

                m_iCount++;
                //斷言順序性及確保單次執行
                assertEquals(1, m_iCount);
            }

            @Override
            public void hideProgress() {

                m_iCount++;
                //斷言順序性及確保單次執行
                assertEquals(2, m_iCount);
            }

            @Override
            public void onDataBind(CIWeatherResp resultData) {
                fail();
            }

            @Override
            public void showErrorText(String strErr) {

                m_iCount++;
                //斷言順序性及確保單次執行
                assertEquals(3, m_iCount);
            }
        });

        presenter.loadWeatherData(Config.LATITUDE, Config.LONGITUDE);

        //停止unit-test thread，等待callback
        await(10, TimeUnit.SECONDS);

        Assert.assertEquals(3, m_iCount);
    }
}