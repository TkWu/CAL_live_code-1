package com.chinaairlines.mobile30.Model;

import android.text.TextUtils;

import com.chinaairlines.mobile30.Config;
import com.chinaairlines.mobile30.base.SBaseAndroidTestCase;

import junit.framework.Assert;

import java.util.concurrent.TimeUnit;

import ci.ws.Models.CICancelCheckInModel;
import ci.ws.Models.entities.CICancelCheckInReq;
import ci.ws.Models.entities.CICancelCheckInReq_ItineraryInfo;
import ci.ws.Models.entities.CICancelCheckInReq_PaxInfo;
import ci.ws.Models.entities.CICancelCheckInResp;
import ci.ws.Models.entities.CICancelCheckInResp_ItineraryInfo;
import ci.ws.Models.entities.CICancelCheckInResp_PaxInfo;
import ci.ws.define.CIWSResultCode;

/**
 * Created by jlchen on 2016/8/11.
 */
public class CICancelCheckInWSModelTest extends SBaseAndroidTestCase {
    /**
     * 功能：旅客取消報到。向API請求資料後，依據結果調用正確之callback。
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
    private int                 m_iCount            = 0;
    //向WS請求所發出的request
    private CICancelCheckInReq  m_reqCancelCheckIn  = null;

    /**
     *  開始測試前執行（預先設置測試所需的測試資料）
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        //設定request值
        //以下為2016-08-11的pnr(可cancel check-in的資料)
        m_reqCancelCheckIn = new CICancelCheckInReq();

        CICancelCheckInReq_PaxInfo paxInfo1 = new CICancelCheckInReq_PaxInfo();
        paxInfo1.First_Name = "YENCHEN";
        paxInfo1.Last_Name  = "WANG";
        paxInfo1.Uci        = "200197B7000D07E3"; //旅客ID
        paxInfo1.Pnr_Id     = "2QU4RH";

        CICancelCheckInReq_ItineraryInfo itineraryInfo1 = new CICancelCheckInReq_ItineraryInfo();
        itineraryInfo1.Departure_Station    = "TPE";
        itineraryInfo1.Arrival_Station      = "NRT";
        itineraryInfo1.Did                  = "2002E7C300229245"; //航段ID

        paxInfo1.Itinerary_Info.add(itineraryInfo1);

        m_reqCancelCheckIn.Pax_Info.add(paxInfo1);
    }

    /**
     *  測試完成後執行（用於移除測試資料）
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * 案例說明：有網路狀態，帶入正確參數，使用sendReqDataToWS()向WS請求旅客取消報到成功
     * 輸入參數：m_reqCancelCheckIn
     * 斷言結果：回呼onSuccess(), rt_code必為000, resp不為null, 並驗證回傳結果是否正確
     */
    public void testCase01() throws Exception {

        //開啟網路
        setWifiEnabled(true);

        CICancelCheckInModel model = new CICancelCheckInModel(new CICancelCheckInModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CICancelCheckInResp resp) {

                m_iCount++;

                //結果代碼為000，表示成功
                Assert.assertEquals( Config.WS_RESULT_CODE_SUCCESS, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                //傳回資料不可為null, 不然應調用onError
                Assert.assertNotNull( resp );

                //驗證其資料內容
                Assert.assertEquals( m_reqCancelCheckIn.Pax_Info.size(), resp.Pax_Info.size() );

                for ( int i = 0; i < resp.Pax_Info.size(); i ++ ){
                    CICancelCheckInResp_PaxInfo paxInfoResp = resp.Pax_Info.get(i);
                    CICancelCheckInReq_PaxInfo  paxInfoReq  = m_reqCancelCheckIn.Pax_Info.get(i);

                    Assert.assertEquals( paxInfoReq.First_Name,             paxInfoResp.First_Name );
                    Assert.assertEquals( paxInfoReq.Last_Name,              paxInfoResp.Last_Name );
                    Assert.assertEquals( paxInfoReq.Uci,                    paxInfoResp.Uci );
                    Assert.assertEquals( paxInfoReq.Pnr_Id,                 paxInfoResp.Pnr_Id );
                    Assert.assertEquals( paxInfoReq.Itinerary_Info.size(),  paxInfoResp.Itinerary_Info.size() );

                    for ( int j = 0; j < paxInfoResp.Itinerary_Info.size(); j ++ ){

                        CICancelCheckInResp_ItineraryInfo itineraryInfoResp = paxInfoResp.Itinerary_Info.get(j);
                        CICancelCheckInReq_ItineraryInfo  itineraryInfoReq  = paxInfoReq.Itinerary_Info.get(j);

                        Assert.assertEquals( itineraryInfoReq.Departure_Station,    itineraryInfoResp.Departure_Station );
                        Assert.assertEquals( itineraryInfoReq.Arrival_Station,      itineraryInfoResp.Arrival_Station );
                        Assert.assertEquals( itineraryInfoReq.Did,                  itineraryInfoResp.Did );
                        Assert.assertEquals( Config.WS_RESULT_CODE_SUCCESS,         itineraryInfoResp.rt_code );
                        Assert.assertEquals( " Cancel Check In",                    itineraryInfoResp.Step );
                    }
                }

                Assert.assertEquals(1, m_iCount);

                //倒數歸零 啟動 unit-test thread
                countDown();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                //測試失敗
                fail();
            }
        });

        model.sendReqDataToWS(m_reqCancelCheckIn);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     * 案例說明：無網路狀態，帶入正確參數，使用sendReqDataToWS()向WS請求旅客取消報到失敗
     * 輸入參數：m_reqCancelCheckIn
     * 斷言結果：回呼onError, rt_code必為9999
     */
    public void testCase02() throws Exception {

        //關閉網路
        setWifiEnabled(false);

        //初始化倒數啟動執行緒
        init(1);

        CICancelCheckInModel model = new CICancelCheckInModel(new CICancelCheckInModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CICancelCheckInResp resp) {

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
        });

        model.sendReqDataToWS(m_reqCancelCheckIn);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     * 案例說明：有網路狀態，帶入不正確參數，使用sendReqDataToWS()向WS請求旅客取消報到失敗
     * 輸入參數：m_reqCancelCheckIn(帶入錯誤的條件, 如Pnr_Id="123")
     * 斷言結果：回呼onError, rt_code必為-999
     */
    public void testCase03() throws Exception {

        //開啟網路
        setWifiEnabled(true);

        m_reqCancelCheckIn.Pax_Info.get(0).Pnr_Id = "123";

        CICancelCheckInModel model = new CICancelCheckInModel(new CICancelCheckInModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CICancelCheckInResp resp) {

                fail();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                m_iCount++;

                //結果代碼為-999，表示錯誤訊息
                Assert.assertEquals( Config.WS_RESULT_CODE_ERROR, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                Assert.assertEquals( 1, m_iCount );

                //倒數歸零 啟動 unit-test thread
                countDown();
            }
        });

        model.sendReqDataToWS(m_reqCancelCheckIn);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }
}
