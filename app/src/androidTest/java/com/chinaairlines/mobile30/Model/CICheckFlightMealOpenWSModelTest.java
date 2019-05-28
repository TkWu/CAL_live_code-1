package com.chinaairlines.mobile30.Model;

import android.text.TextUtils;

import com.chinaairlines.mobile30.Config;
import com.chinaairlines.mobile30.base.SBaseAndroidTestCase;

import junit.framework.Assert;

import java.util.concurrent.TimeUnit;

import ci.ws.Models.CICheckFlightMealOpenModel;
import ci.ws.Models.entities.CICheckFlightMealOpenReq;
import ci.ws.define.CIWSResultCode;

/**
 * Created by jlchen on 2016/8/15.
 */
public class CICheckFlightMealOpenWSModelTest extends SBaseAndroidTestCase {
    /**
     * 功能：確認航班是否開放預定選餐。向API請求資料後，依據結果調用正確之callback。
     */

    /**
     * 向api請求資料-> 是否請求資料成功
     * (1)成功：調用callback的onSuccess返回資料給presenter
     * (2)失敗：調用callback的onError返回錯誤訊息給presenter
     *
     * 影響請求資料是否成功的事件
     * (1)有無網路
     * (2)帶入參數是否正確
     * (3)...
     * */

    //計數及順序的斷言用途
    private int                         m_iCount                    = 0;
    //向WS請求所發出的request
    private CICheckFlightMealOpenReq    m_reqCheckFlightMealOpen    = null;

    /**
     *  開始測試前執行（預先設置測試所需的測試資料）
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        //設定request值
        m_reqCheckFlightMealOpen                = new CICheckFlightMealOpenReq();
        //航空公司代號
        m_reqCheckFlightMealOpen.flight_company = "CI";
        //航班號碼
        m_reqCheckFlightMealOpen.flight_num     = "0006";
        //出發+目的地的機場代碼
        m_reqCheckFlightMealOpen.flight_sector  = "TPELAX";
        //出發日
        m_reqCheckFlightMealOpen.flight_date    = "2016-02-20";
        //訂位狀態
        m_reqCheckFlightMealOpen.pnr_status     = "HK";
        //艙等
        m_reqCheckFlightMealOpen.seat_class     = "Y";
    }

    /**
     *  測試完成後執行（用於移除測試資料）
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * 案例說明：有網路狀態，帶入正確參數，使用sendReqDataToWS()向WS請求依航班檢查是否開放預定餐點成功
     * 輸入參數：m_reqCheckFlightMealOpen
     * 斷言結果：回呼onSuccess(), rt_code必為000
     */
    public void testCase01() throws Exception {

        //開啟網路
        setWifiEnabled(true);

        new CICheckFlightMealOpenModel(new CICheckFlightMealOpenModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg) {
                m_iCount++;

                //結果代碼為000，表示成功
                Assert.assertEquals( Config.WS_RESULT_CODE_SUCCESS, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                Assert.assertEquals(1, m_iCount);

                //倒數歸零 啟動 unit-test thread
                countDown();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                //測試失敗
                fail();
            }
        }).sendReqDataToWS(m_reqCheckFlightMealOpen);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     * 案例說明：無網路狀態，帶入正確參數，使用sendReqDataToWS()向WS請求依航班檢查是否開放預定餐點失敗
     * 輸入參數：m_reqCheckFlightMealOpen
     * 斷言結果：回呼onError, rt_code必為9999
     */
    public void testCase02() throws Exception {

        //關閉網路
        setWifiEnabled(false);

        //初始化倒數啟動執行緒
        init(1);

        new CICheckFlightMealOpenModel(new CICheckFlightMealOpenModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg) {
                //測試失敗
                fail();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                m_iCount++;

                //結果代碼為9999，表示無網路連線
                Assert.assertEquals( CIWSResultCode.NO_INTERNET_CONNECTION, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                Assert.assertEquals(1, m_iCount);

                //倒數歸零 啟動 unit-test thread
                countDown();
            }
        }).sendReqDataToWS(m_reqCheckFlightMealOpen);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     * 案例說明：有網路狀態，帶入不正確參數，使用sendReqDataToWS()向WS請求依航班檢查是否開放預定餐點失敗
     * 輸入參數：m_reqCancelCheckIn(帶入錯誤的條件, 如flight_sector="123")
     * 斷言結果：回呼onError, rt_code不為000, 且rt_msg有錯誤訊息
     */
    public void testCase03() throws Exception {

        //開啟網路
        setWifiEnabled(true);

        m_reqCheckFlightMealOpen.flight_sector = "123";

        new CICheckFlightMealOpenModel(new CICheckFlightMealOpenModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg) {
                //測試失敗
                fail();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                m_iCount++;

                //結果代碼為error，表示取得資料失敗
                Assert.assertEquals( Config.WS_RESULT_ERROR, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                Assert.assertEquals(1, m_iCount);

                //倒數歸零 啟動 unit-test thread
                countDown();
            }
        }).sendReqDataToWS(m_reqCheckFlightMealOpen);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }
}
