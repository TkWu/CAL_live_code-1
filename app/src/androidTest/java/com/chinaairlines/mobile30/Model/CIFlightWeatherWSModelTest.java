package com.chinaairlines.mobile30.Model;

import android.text.TextUtils;

import com.chinaairlines.mobile30.Config;
import com.chinaairlines.mobile30.base.SBaseAndroidTestCase;

import junit.framework.Assert;

import java.util.concurrent.TimeUnit;

import ci.ui.WeatherCard.CIFlightWeatherViewMode;
import ci.ui.WeatherCard.resultData.CIWeatherResp;

/**
 * Created by jlchen on 2016/6/2.
 */
public class CIFlightWeatherWSModelTest extends SBaseAndroidTestCase {

    /**
     * 功能：查詢天氣資料。向API請求資料後，依據結果調用正確之callback。
     */

    /**
     * 向api請求資料-> 是否請求資料成功
     * (1)成功：調用callback的onSuccess返回資料給presenter
     * (2)失敗：調用callback的onError返回錯誤訊息給presenter
     *
     * 影響請求資料是否成功的事件
     * (1)有無網路
     * (2)帶入參數是否正確
     * */

    //計數及順序的斷言用途
    private int m_iCount = 0;

    /**
     *  開始測試前執行（預先設置測試所需的測試資料）
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     *  測試完成後執行（用於移除測試資料）
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * 案例說明：有網路狀態，帶入正確參數，使用findData()向WS請求天氣資料成功
     * 輸入參數：機場代碼="TPE"
     * 斷言結果：回呼onSuccess(), resultData不為null
     */
    public void testCase01() throws Exception {
        setWifiEnabled(true);

        new CIFlightWeatherViewMode().findData(
                Config.LATITUDE,
                Config.LONGITUDE,
                new CIFlightWeatherViewMode.Callback() {
                    @Override
                    public void onSuccess(CIWeatherResp resultData) {
                        m_iCount++;

                        //傳回資料不可為null, 不然應調用onError
                        Assert.assertNotNull(resultData);
                        //取得天氣成功，沒有錯誤訊息
                        Assert.assertTrue(TextUtils.isEmpty(resultData.m_strError));

                        //有取得對應之城市名
                        Assert.assertEquals("Taoyuan City", resultData.getLocation());
                        //有取得氣象資訊
                        Assert.assertNotNull(resultData.getCondition());
                        //未來五天天氣預報
                        Assert.assertEquals(5, resultData.getForecast().GetForecastList().size());
                        //有取得濕度資料
                        Assert.assertFalse(TextUtils.isEmpty(resultData.getHumidity()));
                        //有取得能見度資料
                        Assert.assertFalse(TextUtils.isEmpty(resultData.getVisibility()));

                        Assert.assertEquals(1, m_iCount);

                        //倒數歸零 啟動 unit-test thread
                        countDown();
                    }

                    @Override
                    public void onError(String strError) {
                        fail();
                    }
                });

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     * 案例說明：無網路狀態，帶入正確參數，使用findData()向WS請求天氣資料失敗
     * 輸入參數：機場代碼="TPE"
     * 斷言結果：回呼onError(), strError不為空值
     */
    public void testCase02() throws Exception {
        setWifiEnabled(false);

        //初始化倒數啟動執行緒
        init(1);

        new CIFlightWeatherViewMode().findData(
                Config.LATITUDE,
                Config.LONGITUDE,
                new CIFlightWeatherViewMode.Callback() {
                    @Override
                    public void onSuccess(CIWeatherResp resultData) {
                        fail();
                    }

                    @Override
                    public void onError(String strError) {
                        m_iCount++;

                        Assert.assertFalse(TextUtils.isEmpty(strError));

                        Assert.assertEquals(1, m_iCount);

                        //倒數歸零 啟動 unit-test thread
                        countDown();
                    }
                });

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     * 案例說明：有網路狀態，帶入不正確參數，使用findData()向WS請求天氣資料失敗
     * 輸入參數：機場代碼="'+"TPE"
     * 斷言結果：回呼onError(), strError不為空值
     */
    public void testCase03() throws Exception {
        setWifiEnabled(true);

        String latitude = "'+/\""+Config.LATITUDE;

        new CIFlightWeatherViewMode().findData(
                latitude,
                Config.LONGITUDE,
                new CIFlightWeatherViewMode.Callback() {
                    @Override
                    public void onSuccess(CIWeatherResp resultData) {
                        fail();
                    }

                    @Override
                    public void onError(String strError) {
                        m_iCount++;

                        Assert.assertFalse(TextUtils.isEmpty(strError));

                        Assert.assertEquals(1, m_iCount);

                        //倒數歸零 啟動 unit-test thread
                        countDown();
                    }
                });

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }
}