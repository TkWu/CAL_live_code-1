package com.chinaairlines.mobile30.Model;

import android.text.TextUtils;

import com.chinaairlines.mobile30.Config;
import com.chinaairlines.mobile30.base.SBaseAndroidTestCase;

import junit.framework.Assert;

import java.util.concurrent.TimeUnit;

import ci.ws.Models.CIInquiryAllPassengerByPNRModel;
import ci.ws.Models.entities.CIPassengerByPNRReq;
import ci.ws.Models.entities.CIPassengerListResp;
import ci.ws.define.CIWSResultCode;

/**
 * Created by jlchen on 2016/7/12.
 */
public class CIInquiryAllPassengerByPNRWSModelTest extends SBaseAndroidTestCase {

    /**
     * 功能：查詢航段的旅客資料。向API請求資料後，依據結果調用正確之callback。
     */

    /**
     * 向api請求資料-> 是否請求資料成功
     * (1)成功：調用callback的 onInquiryPassengerByPNRSuccess 返回資料給presenter
     * (2)失敗：調用callback的 onInquiryPassengerByPNRError 返回錯誤訊息給presenter
     *
     * 影響請求資料是否成功的事件
     * (1)有無網路
     * (2)帶入參數是否正確
     * */

    //計數及順序的斷言用途
    private int                 m_iCount            = 0;
    //向WS請求所發出的request
    private CIPassengerByPNRReq m_reqPassenger      = null;

    /**
     *  開始測試前執行（預先設置測試所需的測試資料）
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        //設定request值
        //以下為2016-08-11的pnr(可查詢旅客的資料)
        m_reqPassenger                   = new CIPassengerByPNRReq();
        //訂票代號
        m_reqPassenger.pnr_Id            = Config.PNR;
        //航段序號
        m_reqPassenger.pnr_seq           = Config.PNR_SEQ;
        //出發地
        m_reqPassenger.departure_station = Config.DEPARTURE_STATION;
        //目的地
        m_reqPassenger.arrival_station   = Config.ARRVAL_STATION;
    }

    /**
     *  測試完成後執行（用於移除測試資料）
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * 案例說明：有網路狀態，帶入正確參數，先使用setAllPassengerReq()傳入查詢參數後，
     *          再使用DoConnection()向WS請求旅客取消報到成功。
     * 輸入參數：m_reqPassenger
     * 斷言結果：回呼 onInquiryPassengerByPNRSuccess()，
     *          rt_code必為000，resp不為null，至少回傳一筆以上的乘客資料。
     */
    public void testCase01() throws Exception{
        //開啟網路
        setWifiEnabled(true);

        CIInquiryAllPassengerByPNRModel model = new CIInquiryAllPassengerByPNRModel(
                new CIInquiryAllPassengerByPNRModel.InquiryPassengerByPNRCallBack() {
            @Override
            public void onInquiryPassengerByPNRSuccess(String rt_code, String rt_msg, CIPassengerListResp resp) {
                m_iCount++;

                //結果代碼為000，表示成功
                Assert.assertEquals( Config.WS_RESULT_CODE_SUCCESS, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                //傳回資料不可為null, 不然應調用onError
                Assert.assertNotNull( resp );

                //旅客List不為null, 並且筆數至少一筆以上
                Assert.assertNotNull( resp.Pax_Info );
                Assert.assertTrue( 0 < resp.Pax_Info.size() );

                Assert.assertEquals(1, m_iCount);

                //倒數歸零 啟動unit-test thread
                countDown();
            }

            @Override
            public void onInquiryPassengerByPNRError(String rt_code, String rt_msg) {
                fail();
            }
        });

        model.setAllPassengerReq(m_reqPassenger);
        model.DoConnection();

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     * 案例說明：無網路狀態，帶入正確參數，先使用setAllPassengerReq()傳入查詢參數後，
     *          再使用DoConnection()向WS請求旅客取消報到失敗。
     * 輸入參數：m_reqPassenger
     * 斷言結果：回呼 onInquiryPassengerByPNRError() , rt_code必為9999
     */
    public void testCase02() throws Exception{

        //關閉網路
        setWifiEnabled(false);

        //初始化倒數啟動執行緒
        init(1);

        CIInquiryAllPassengerByPNRModel model = new CIInquiryAllPassengerByPNRModel(
                new CIInquiryAllPassengerByPNRModel.InquiryPassengerByPNRCallBack() {
                    @Override
                    public void onInquiryPassengerByPNRSuccess(String rt_code, String rt_msg, CIPassengerListResp resp) {
                        fail();
                    }

                    @Override
                    public void onInquiryPassengerByPNRError(String rt_code, String rt_msg) {
                        m_iCount++;

                        //結果代碼為9999，表示無網路連線
                        Assert.assertEquals( CIWSResultCode.NO_INTERNET_CONNECTION, rt_code );

                        //結果訊息不為空值
                        Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                        Assert.assertEquals(1, m_iCount);

                        //倒數歸零 啟動 unit-test thread
                        countDown();
                    }
                });

        model.setAllPassengerReq(m_reqPassenger);
        model.DoConnection();

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     * 案例說明：有網路狀態，帶入不正確參數，先使用setAllPassengerReq()傳入查詢參數後，
     *          再使用DoConnection()向WS請求旅客取消報到失敗。
     * 輸入參數：m_reqPassenger（帶入錯誤的條件, pnr_Id="123"）
     * 斷言結果：回呼 onInquiryPassengerByPNRError() , rt_code必為-999
     */
    public void testCase03() throws Exception{
        //開啟網路
        setWifiEnabled(true);

        m_reqPassenger.pnr_Id = "123";

        CIInquiryAllPassengerByPNRModel model = new CIInquiryAllPassengerByPNRModel(
                new CIInquiryAllPassengerByPNRModel.InquiryPassengerByPNRCallBack() {
                    @Override
                    public void onInquiryPassengerByPNRSuccess(String rt_code, String rt_msg, CIPassengerListResp PassengerList) {
                        fail();
                    }

                    @Override
                    public void onInquiryPassengerByPNRError(String rt_code, String rt_msg) {
                        m_iCount++;

                        //結果代碼為-999，表示錯誤訊息
                        Assert.assertEquals( Config.WS_RESULT_CODE_ERROR, rt_code );

                        //結果訊息不為空值
                        Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                        Assert.assertEquals( 1, m_iCount );

                        //倒數歸零 啟動unit-test thread
                        countDown();
                    }
                });

        model.setAllPassengerReq(m_reqPassenger);
        model.DoConnection();

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }
}
