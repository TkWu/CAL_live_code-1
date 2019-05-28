package com.chinaairlines.mobile30.Model;

import android.text.TextUtils;

import com.chinaairlines.mobile30.Config;
import com.chinaairlines.mobile30.base.SBaseAndroidTestCase;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import ci.function.Core.CIApplication;
import ci.ws.Models.CIInquiryBoardingPassModel;
import ci.ws.Models.entities.CIBoardPassResp;
import ci.ws.Models.entities.CIBoardPassRespItineraryList;
import ci.ws.Models.entities.CIBoardPassResp_Itinerary;
import ci.ws.Models.entities.CIBoardPassResp_PaxInfo;
import ci.ws.Models.entities.CIInquiryBoardPassDBEntity;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.CIWSResultCode;

/**
 * Created by jlchen on 2016/6/2.
 */
public class CIInquiryBoardPassWSModelTest extends SBaseAndroidTestCase {

    /**
     * 功能：查詢登機證資料。
     * (1)向API請求資料後，依據結果調用正確之callback。
     * (2)若向API請求資料成功，則將資料存入DB，於無網路時可先讀取DB已存資料。
     */

    /**
     * API提供三種查詢方式：
     * (1)使用卡號與PNR List查詢
     * (2)使用訂位代號、護照英文姓名查詢
     * (3)使用機票號碼、護照英文姓名查詢
     *
     * 向API請求資料-> 是否請求資料成功
     * (1)成功：調用callback的 onSuccess 返回資料給presenter
     * (2)失敗：調用callback的 onError 返回錯誤訊息給presenter
     *
     * 影響請求資料是否成功的事件
     * (1)有無網路
     * (2)帶入參數是否正確
     * (3)...
     * */

    /**
     * 存取登機證資料至DB-> 是否存取成功
     * (1)成功：返回true
     * (2)失敗：返回false
     *
     * 影響存取資料是否成功的事件
     * (1)存入資料格式不正確
     *
     *
     * 於DB取出已存取的登機證資料-> 是否取得成功
     * (1)成功：返回ArrayList<CIBoardPassResp_Itinerary> data
     * (2)失敗：返回null
     *
     * 影響讀取資料是否成功的事件
     * (1)存入資料格式不正確, 造成解析失敗
     * (2)資料表尚未建立
     * (3)資料尚未存入於DB
     * */

    //計數及順序的斷言用途
    private int m_iCount = 0;

    //request參數
    //以下為2016-08-11的pnr(可查詢旅客的資料)
    //卡號
    private final String    CARD_NO     = Config.CARD_NO;
    //訂票代號(可能為多筆)
    private Set<String>     m_listPNR;
    //訂票代號(單筆)
    private final String    PNR_ID      = Config.PNR;
    //機票號碼
    private final String    TICKET      = Config.TICKET_NO;
    //護照名字
    private final String    FIRST_NAME  = Config.FIRST_NAME;
    //護照姓氏
    private final String    LAST_NAME   = Config.LAST_NAME;

    /**
     *  開始測試前執行（預先設置測試所需的測試資料）
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        //設定request值
        //以下為2016-08-11的pnr(可查詢旅客的資料)
        m_listPNR = Config.getPnrList();
    }

    /**
     *  測試完成後執行（用於移除測試資料）
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     *  案例說明：有網路狀態，帶入正確的卡號及PNR list參數，
     *          使用sendReqDataToWSByPNRListAndCardNo()向WS請求登機證資料成功
     *  輸入參數：卡號="WB0000000"、PNR=["2QU4RH"]
     *  斷言結果：找到資料時, 回呼onSuccess，rt_code為"000"，rt_msg不為空值，datas不為null(變動性質,故不驗證其內容)
     */
    public void testCase1_1() throws Exception{
        //開啟網路
        setWifiEnabled(true);

        CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(new CIInquiryBoardingPassModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                m_iCount++;

                //結果代碼為000，表示成功
                Assert.assertEquals( Config.WS_RESULT_CODE_SUCCESS, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                //傳回資料不可為null, 不然應調用onError
                Assert.assertNotNull( datas );

                //pnr資料不為null, 且至少應傳回一筆以上pnr資料
                Assert.assertNotNull( datas.Pnr_Info );
                Assert.assertTrue( 0 < datas.Pnr_Info.size() );

                Assert.assertEquals(1, m_iCount);

                //倒數歸零 啟動unit-test thread
                countDown();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                //如果呼叫這個callback則斷言失敗
                fail();
            }
        });

        model.sendReqDataToWSByPNRListAndCardNo(CARD_NO, m_listPNR);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     *  案例說明：無網路狀態，帶入正確的卡號及PNR list參數，
     *          使用sendReqDataToWSByPNRListAndCardNo()向WS請求登機證資料失敗
     *  輸入參數：卡號="WB0000000"、PNR=["2QU4RH"]
     *  斷言結果：必定回應onError，rt_code為"9999"
     */
    public void testCase1_2() throws Exception{

        //關閉網路
        setWifiEnabled(false);

        //初始化倒數啟動執行緒
        init(1);

        CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(new CIInquiryBoardingPassModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                //如果呼叫這個callback則斷言失敗
                fail();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                m_iCount++;

                //結果代碼為9999，表示無網路連線
                Assert.assertTrue(CIWSResultCode.NO_INTERNET_CONNECTION.equals(rt_code));

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                Assert.assertEquals(1, m_iCount);

                //倒數歸零 啟動unit-test thread
                countDown();
            }
        });

        model.sendReqDataToWSByPNRListAndCardNo(CARD_NO, m_listPNR);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     *  案例說明：有網路狀態，僅帶入正確的卡號參數，
     *          使用sendReqDataToWSByPNRListAndCardNo()向WS請求登機證資料成功
     *  輸入參數：卡號="WB0000000"、PNR=null
     *  斷言結果：
     *  找到資料時, 回呼onSuccess，rt_code為"000"，rt_msg不為空值，datas不為null
     */
    public void testCase1_3() throws Exception{
        setWifiEnabled(true);

        CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(new CIInquiryBoardingPassModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                m_iCount++;

                //結果代碼為000，表示成功
                Assert.assertEquals( Config.WS_RESULT_CODE_SUCCESS, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                //傳回資料不可為null, 不然應調用onError
                Assert.assertNotNull( datas );

                //pnr資料不為null, 且至少應傳回一筆以上pnr資料
                Assert.assertNotNull( datas.Pnr_Info );
                Assert.assertTrue( 0 < datas.Pnr_Info.size() );

                Assert.assertEquals(1, m_iCount);

                //倒數歸零 啟動unit-test thread
                countDown();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                fail();
            }
        });

        model.sendReqDataToWSByPNRListAndCardNo(CARD_NO, null);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     *  案例說明：有網路狀態，僅帶入PNR list參數，
     *          使用sendReqDataToWSByPNRListAndCardNo()向WS請求登機證資料成功
     *  輸入參數：卡號=null、PNR=["2QU4RH"]
     *  斷言結果：
     *  找到資料時, 回呼onSuccess，rt_code為"000"，rt_msg不為空值，datas不為null
     */
    public void testCase1_4() throws Exception{
        setWifiEnabled(true);

        CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(new CIInquiryBoardingPassModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                m_iCount++;

                //結果代碼為000，表示成功
                Assert.assertEquals( Config.WS_RESULT_CODE_SUCCESS, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                //傳回資料不可為null, 不然應調用onError
                Assert.assertNotNull( datas );

                //pnr資料不為null, 且至少應傳回一筆以上pnr資料
                Assert.assertNotNull( datas.Pnr_Info );
                Assert.assertTrue( 0 < datas.Pnr_Info.size() );

                Assert.assertEquals(1, m_iCount);

                //倒數歸零 啟動unit-test thread
                countDown();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                fail();
            }
        });

        model.sendReqDataToWSByPNRListAndCardNo(null, m_listPNR);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     *  案例說明：有網路狀態，傳入錯誤的卡號參數，
     *          使用sendReqDataToWSByPNRListAndCardNo()向WS請求登機證資料失敗
     *  輸入參數：卡號="123"
     *  斷言結果：回呼onError, rt_code必為-999
     */
    public void testCase1_5() throws Exception{
        setWifiEnabled(true);

        String strCard="123";

        CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(new CIInquiryBoardingPassModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                //如果呼叫這個callback則斷言失敗
                fail();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                m_iCount++;

                //結果代碼為-999，表示有錯誤訊息
                Assert.assertEquals( Config.WS_RESULT_CODE_ERROR, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                Assert.assertEquals( 1, m_iCount );
                //倒數歸零啟動 unit-test thread
                countDown();
            }
        });

        model.sendReqDataToWSByPNRListAndCardNo(strCard, null);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     *  案例說明：有網路狀態，傳入錯誤的PNR list參數，
     *          使用sendReqDataToWSByPNRListAndCardNo()向WS請求登機證資料失敗
     *  輸入參數：PNR=["123"]
     *  斷言結果：回呼onError, rt_code必為-999
     */
    public void testCase1_6() throws Exception{
        setWifiEnabled(true);

        Set<String> listPNR = new LinkedHashSet<>();
        listPNR.add("123");

        CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(new CIInquiryBoardingPassModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                fail();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                m_iCount++;

                //結果代碼為-999，表示有錯誤訊息
                Assert.assertEquals( Config.WS_RESULT_CODE_ERROR, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                Assert.assertEquals( 1, m_iCount );
                //倒數歸零啟動 unit-test thread
                countDown();
            }
        });

        model.sendReqDataToWSByPNRListAndCardNo(null, listPNR);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     *  案例說明：有網路狀態，未傳入任何參數，
     *          使用sendReqDataToWSByPNRListAndCardNo()向WS請求登機證資料失敗
     *  輸入參數：卡號=null、PNR=null
     *  斷言結果：必定回呼onError，rt_code不為空值，rt_msg不為空值(變動性質,故不驗證其內容)
     */
    public void testCase1_7() throws Exception{
        setWifiEnabled(true);

        CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(new CIInquiryBoardingPassModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                //如果呼叫這個callback則斷言失敗
                fail();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                m_iCount++;

                //結果代碼為-999，表示有錯誤訊息
                Assert.assertEquals( Config.WS_RESULT_CODE_ERROR, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                Assert.assertEquals( 1, m_iCount );
                //倒數歸零啟動 unit-test thread
                countDown();
            }
        });

        model.sendReqDataToWSByPNRListAndCardNo(null, null);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    //---------------------------------------------------------------------

    /**
     *  案例說明：有網路狀態，帶入正確的PNR id及護照英文姓名參數，
     *          使用sendReqDataToWSByPNRNo()向WS請求登機證資料成功
     *  輸入參數：PNR No="2QU4RH", First name="YENCHEN", Last name="WANG"
     *  斷言結果：找到資料時, 回呼onSuccess，rt_code為"000"，rt_msg不為空值，datas不為null(變動性質,故不驗證其內容)
     */
    public void testCase2_1() throws Exception{
        //開啟網路
        setWifiEnabled(true);

        CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(new CIInquiryBoardingPassModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                m_iCount++;

                //結果代碼為000，表示成功
                Assert.assertEquals( Config.WS_RESULT_CODE_SUCCESS, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                //傳回資料不可為null, 不然應調用onError
                Assert.assertNotNull( datas );

                //pnr資料不為null, 且應只會傳回一筆pnr資料
                Assert.assertNotNull( datas.Pnr_Info );
                Assert.assertEquals(1, datas.Pnr_Info.size() );

                Assert.assertEquals(1, m_iCount);

                //倒數歸零 啟動unit-test thread
                countDown();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                //如果呼叫這個callback則斷言失敗
                fail();
            }
        });

        model.sendReqDataToWSByPNRNo(PNR_ID, FIRST_NAME, LAST_NAME);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     *  案例說明：無網路狀態，帶入正確的PNR id及護照英文姓名參數，
     *          使用sendReqDataToWSByPNRNo()向WS請求登機證資料失敗
     *  輸入參數：PNR No="2QU4RH", First name="YENCHEN", Last name="WANG"
     *  斷言結果：必定回應onError，rt_code為"9999"
     */
    public void testCase2_2() throws Exception{

        //關閉網路
        setWifiEnabled(false);

        //初始化倒數啟動執行緒
        init(1);

        CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(new CIInquiryBoardingPassModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                //如果呼叫這個callback則斷言失敗
                fail();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                m_iCount++;

                //結果代碼為9999，表示無網路連線
                Assert.assertTrue(CIWSResultCode.NO_INTERNET_CONNECTION.equals(rt_code));

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                Assert.assertEquals(1, m_iCount);

                //倒數歸零 啟動unit-test thread
                countDown();
            }
        });

        model.sendReqDataToWSByPNRNo(PNR_ID, FIRST_NAME, LAST_NAME);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     *  案例說明：有網路狀態，未帶入正確的PNR id參數，
     *          使用sendReqDataToWSByPNRNo()向WS請求登機證資料失敗
     *  輸入參數：PNR No=null, First name="YENCHEN", Last name="WANG"
     *  斷言結果：回呼onError, rt_code必為-999
     */
    public void testCase2_3() throws Exception{
        setWifiEnabled(true);

        CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(new CIInquiryBoardingPassModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                fail();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                m_iCount++;

                //結果代碼為-999，表示有錯誤訊息
                Assert.assertEquals( Config.WS_RESULT_CODE_ERROR, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                Assert.assertEquals( 1, m_iCount );
                //倒數歸零啟動 unit-test thread
                countDown();
            }
        });

        model.sendReqDataToWSByPNRNo(null, FIRST_NAME, LAST_NAME);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     *  案例說明：有網路狀態，未帶入正確的First name參數，
     *          使用sendReqDataToWSByPNRNo()向WS請求登機證資料失敗
     *  輸入參數：PNR No="2QU4RH", First name=null, Last name="WANG"
     *  斷言結果：回呼onError, rt_code必為-999
     */
    public void testCase2_4() throws Exception{
        setWifiEnabled(true);

        CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(new CIInquiryBoardingPassModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                fail();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                m_iCount++;

                //結果代碼為-999，表示有錯誤訊息
                Assert.assertEquals( Config.WS_RESULT_CODE_ERROR, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                Assert.assertEquals( 1, m_iCount );
                //倒數歸零啟動 unit-test thread
                countDown();
            }
        });

        model.sendReqDataToWSByPNRNo(PNR_ID, null, LAST_NAME);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     *  案例說明：有網路狀態，未帶入正確的Last name參數，
     *          使用sendReqDataToWSByPNRNo()向WS請求登機證資料失敗
     *  輸入參數：PNR No="2QU4RH", First name="YENCHEN", Last name=null
     *  斷言結果：回呼onError, rt_code必為-999
     */
    public void testCase2_5() throws Exception{
        setWifiEnabled(true);

        CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(new CIInquiryBoardingPassModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                //如果呼叫這個callback則斷言失敗
                fail();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                m_iCount++;

                //結果代碼為-999，表示有錯誤訊息
                Assert.assertEquals( Config.WS_RESULT_CODE_ERROR, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                Assert.assertEquals( 1, m_iCount );
                //倒數歸零啟動 unit-test thread
                countDown();
            }
        });

        model.sendReqDataToWSByPNRNo(PNR_ID, FIRST_NAME, null);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    //---------------------------------------------------------------------

    /**
     *  案例說明：有網路狀態，帶入正確的Ticket No及護照英文姓名參數，
     *          使用sendReqDataToWSByTicket()向WS請求登機證資料成功
     *  輸入參數：TICKET="6779034982829", First name="YENCHEN", Last name="WANG"
     *  斷言結果：找到資料時, 回呼onSuccess，rt_code為"000"，rt_msg不為空值，datas不為null(變動性質,故不驗證其內容)
     */
    public void testCase3_1() throws Exception{
        //開啟網路
        setWifiEnabled(true);

        CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(new CIInquiryBoardingPassModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                m_iCount++;

                //結果代碼為000，表示成功
                Assert.assertEquals( Config.WS_RESULT_CODE_SUCCESS, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                //傳回資料不可為null, 不然應調用onError
                Assert.assertNotNull( datas );

                //pnr資料不為null, 且應只會傳回一筆pnr資料
                Assert.assertNotNull( datas.Pnr_Info );
                Assert.assertEquals(1, datas.Pnr_Info.size() );

                Assert.assertEquals(1, m_iCount);

                //倒數歸零 啟動unit-test thread
                countDown();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                //如果呼叫這個callback則斷言失敗
                fail();
            }
        });

        model.sendReqDataToWSByTicket(TICKET, FIRST_NAME, LAST_NAME);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     *  案例說明：無網路狀態，帶入正確的Ticket No及護照英文姓名參數，
     *          使用sendReqDataToWSByTicket()向WS請求登機證資料失敗
     *  輸入參數：TICKET="6779034982829", First name="YENCHEN", Last name="WANG"
     *  斷言結果：必定回應onError，rt_code為"9999"
     */
    public void testCase3_2() throws Exception{

        //關閉網路
        setWifiEnabled(false);

        //初始化倒數啟動執行緒
        init(1);

        CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(new CIInquiryBoardingPassModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                //如果呼叫這個callback則斷言失敗
                fail();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                m_iCount++;

                //結果代碼為9999，表示無網路連線
                Assert.assertTrue(CIWSResultCode.NO_INTERNET_CONNECTION.equals(rt_code));

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                Assert.assertEquals(1, m_iCount);

                //倒數歸零 啟動unit-test thread
                countDown();
            }
        });

        model.sendReqDataToWSByTicket(TICKET, FIRST_NAME, LAST_NAME);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     *  案例說明：有網路狀態，未帶入正確的TICKET參數，
     *          使用sendReqDataToWSByTicket()向WS請求登機證資料失敗
     *  輸入參數：TICKET=null, First name="YENCHEN", Last name="WANG"
     *  斷言結果：回呼onError, rt_code必為-999
     */
    public void testCase3_3() throws Exception{
        setWifiEnabled(true);

        final CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(new CIInquiryBoardingPassModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                fail();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                m_iCount++;

                //結果代碼為-999，表示有錯誤訊息
                Assert.assertEquals( Config.WS_RESULT_CODE_ERROR, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                Assert.assertEquals( 1, m_iCount );
                //倒數歸零啟動 unit-test thread
                countDown();
            }
        });

        model.sendReqDataToWSByTicket(null, FIRST_NAME, LAST_NAME);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     *  案例說明：有網路狀態，未帶入正確的First name參數，
     *          使用sendReqDataToWSByTicket()向WS請求登機證資料失敗
     *  輸入參數：TICKET="6779034982829", First name=null, Last name="WANG"
     *  斷言結果：回呼onError, rt_code必為-999
     */
    public void testCase3_4() throws Exception{
        setWifiEnabled(true);

        CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(new CIInquiryBoardingPassModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                fail();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                m_iCount++;

                //結果代碼為-999，表示有錯誤訊息
                Assert.assertEquals( Config.WS_RESULT_CODE_ERROR, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                Assert.assertEquals( 1, m_iCount );
                //倒數歸零啟動 unit-test thread
                countDown();
            }
        });

        model.sendReqDataToWSByTicket(TICKET, null, LAST_NAME);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     *  案例說明：有網路狀態，未帶入正確的Last name參數，
     *          使用sendReqDataToWSByTicket()向WS請求登機證資料失敗
     *  輸入參數：TICKET="6779034982829", First name="YENCHEN", Last name=null
     *  斷言結果：回呼onError, rt_code必為-999
     */
    public void testCase3_5() throws Exception{
        setWifiEnabled(true);

        CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(new CIInquiryBoardingPassModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                //如果呼叫這個callback則斷言失敗
                fail();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                m_iCount++;

                //結果代碼為-999，表示有錯誤訊息
                Assert.assertEquals( Config.WS_RESULT_CODE_ERROR, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                Assert.assertEquals( 1, m_iCount );
                //倒數歸零啟動 unit-test thread
                countDown();
            }
        });

        model.sendReqDataToWSByTicket(TICKET, FIRST_NAME, null);

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     *  案例說明：無網路狀態，使用saveDataToDB()，
     *          將登機證資料(格式ArrayList<CIBoardPassResp_Itinerary>)存入DB成功
     *  輸入參數：entity.respResult = GsonTool.toJson(setDBData()); (登機證資料)
     *  斷言結果：回傳true表示存入資料格式正確，並且存取成功
     */
    public void testCase4_1() throws Exception{
        //關閉網路
        setWifiEnabled(false);

        //初始化倒數啟動執行緒
        init(1);

        CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(null);

        CIInquiryBoardPassDBEntity entity = new CIInquiryBoardPassDBEntity();
        entity.respResult = GsonTool.toJson(setDBData());

        //驗證是否寫入資料成功
        Assert.assertTrue(model.saveDataToDB(entity));

        //測試完畢需清空測試資料
        clearDBData(entity);
    }

    /**
     *  案例說明：無網路狀態，使用saveDataToDB()將格式錯誤的資料存入DB
     *  輸入參數：entity.respResult = "123"
     *  斷言結果：回傳false表示存入錯誤資料
     */
    public void testCase4_2() throws Exception{
        //關閉網路
        setWifiEnabled(false);

        //初始化倒數啟動執行緒
        init(1);

        CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(null);

        CIInquiryBoardPassDBEntity entity = new CIInquiryBoardPassDBEntity();

        //驗證是否寫入資料失敗
        Assert.assertFalse(model.saveDataToDB(entity));

        //測試完畢需清空測試資料
        clearDBData(entity);
    }

    /**
     *  案例說明：無網路狀態，已saveDataToDB()成功，使用getDataFromDB()從DB取出登機證資料成功
     *  輸入參數：無
     *  斷言結果：回傳登機證資料，資料不可為null，且內容正確
     */
    public void testCase4_3() throws Exception{
        //關閉網路
        setWifiEnabled(false);

        //初始化倒數啟動執行緒
        init(1);

        //預先將資料存入db
        ArrayList<CIBoardPassResp_Itinerary> alSaveData = setDBData();

        CIInquiryBoardPassDBEntity entity = new CIInquiryBoardPassDBEntity();
        entity.respResult = GsonTool.toJson(alSaveData);

        CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(null);
        Assert.assertTrue(model.saveDataToDB(entity));

        //資料於db取出
        ArrayList<CIBoardPassResp_Itinerary> alDBData =  model.getDataFromDB();

        //驗證資料內容是否一致
        //Assert.assertEquals(alDBData, alSaveData);
        //Assert.assertTrue(alDBData.equals(alSaveData));
        Assert.assertEquals(alDBData.get(0).Pax_Info.get(0).First_Name, alSaveData.get(0).Pax_Info.get(0).First_Name);
        Assert.assertEquals(alDBData.get(0).Pax_Info.get(0).Last_Name, alSaveData.get(0).Pax_Info.get(0).Last_Name);
        Assert.assertEquals(alDBData.get(0).Pax_Info.get(0).Seat_Number, alSaveData.get(0).Pax_Info.get(0).Seat_Number);
        Assert.assertEquals(alDBData.get(0).Pax_Info.get(0).Boarding_Pass, alSaveData.get(0).Pax_Info.get(0).Boarding_Pass);
        Assert.assertEquals(alDBData.get(0).Pax_Info.get(0).Seat_Zone, alSaveData.get(0).Pax_Info.get(0).Seat_Zone);
        Assert.assertEquals(alDBData.get(0).Pax_Info.get(0).Seq_No, alSaveData.get(0).Pax_Info.get(0).Seq_No);
        Assert.assertEquals(alDBData.get(0).Pax_Info.get(0).Sky_Priority, alSaveData.get(0).Pax_Info.get(0).Sky_Priority);
        Assert.assertEquals(alDBData.get(0).Pax_Info.get(0).Lounge, alSaveData.get(0).Pax_Info.get(0).Lounge);
        Assert.assertEquals(alDBData.get(0).Pax_Info.get(0).Is_Print_BP, alSaveData.get(0).Pax_Info.get(0).Is_Print_BP);
        Assert.assertEquals(alDBData.get(0).Pax_Info.get(0).Is_Check_In, alSaveData.get(0).Pax_Info.get(0).Is_Check_In);

        Assert.assertEquals(alDBData.get(0).Departure_Date, alSaveData.get(0).Departure_Date);
        Assert.assertEquals(alDBData.get(0).Departure_Time, alSaveData.get(0).Departure_Time);
        Assert.assertEquals(alDBData.get(0).Departure_Station, alSaveData.get(0).Departure_Station);
        Assert.assertEquals(alDBData.get(0).Arrival_Time, alSaveData.get(0).Arrival_Time);
        Assert.assertEquals(alDBData.get(0).Arrival_Station, alSaveData.get(0).Arrival_Station);
        Assert.assertEquals(alDBData.get(0).Airlines, alSaveData.get(0).Airlines);
        Assert.assertEquals(alDBData.get(0).Flight_Number, alSaveData.get(0).Flight_Number);
        Assert.assertEquals(alDBData.get(0).Class_Of_Service, alSaveData.get(0).Class_Of_Service);
        Assert.assertEquals(alDBData.get(0).Class_Of_Service_Desc, alSaveData.get(0).Class_Of_Service_Desc);
        Assert.assertEquals(alDBData.get(0).Boarding_Gate, alSaveData.get(0).Boarding_Gate);
        Assert.assertEquals(alDBData.get(0).Boarding_Date, alSaveData.get(0).Boarding_Date);
        Assert.assertEquals(alDBData.get(0).Boarding_Time, alSaveData.get(0).Boarding_Time);
        Assert.assertEquals(alDBData.get(0).Departure_Terminal, alSaveData.get(0).Departure_Terminal);

        //測試完畢需清空測試資料
        clearDBData(entity);
    }

    /**
     *  案例說明：無網路狀態，saveDataToDB()回傳false時，使用getDataFromDB()，從DB取出登機證資料失敗
     *  輸入參數：無
     *  斷言結果：回傳資料為null
     */
    public void testCase4_4() throws Exception{
        //關閉網路
        setWifiEnabled(false);

        //初始化倒數啟動執行緒
        init(1);

        CIInquiryBoardingPassModel model = new CIInquiryBoardingPassModel(null);

        CIInquiryBoardPassDBEntity entity = new CIInquiryBoardPassDBEntity();

        //驗證是否寫入資料失敗
        Assert.assertFalse(model.saveDataToDB(entity));

        //驗證取得登機證資料失敗
        Assert.assertNull(model.getDataFromDB());

        //測試完畢需清空測試資料
        clearDBData(entity);
    }

    //測試資料
    private CIBoardPassRespItineraryList setDBData(){

        CIBoardPassResp_PaxInfo paxInfo = new CIBoardPassResp_PaxInfo();
        paxInfo.First_Name = "YENCHEN";
        paxInfo.Last_Name = "WANG";
        paxInfo.Seat_Number = "";
        paxInfo.Boarding_Pass = "M1WANG/YENCHEN        E2QU4RH ATLMYRDL 5330 225T00000000 18A>518                        2A0067790349828                             ";
        paxInfo.Seat_Zone = null;
        paxInfo.Seq_No = "";
        paxInfo.Sky_Priority = "N";
        paxInfo.Lounge = "N";
        paxInfo.Is_Print_BP = "N";
        paxInfo.Is_Check_In = "N";

        CIBoardPassResp_Itinerary itinerary = new CIBoardPassResp_Itinerary();
        itinerary.Departure_Date = "2016-08-12";
        itinerary.Departure_Time = "17:40";
        itinerary.Departure_Station = "ATL";
        itinerary.Arrival_Time = "19:06";
        itinerary.Arrival_Station = "MYR";
        itinerary.Airlines = "CI";
        itinerary.Flight_Number = "5330";
        itinerary.Class_Of_Service = "T";
        itinerary.Class_Of_Service_Desc = "";
        itinerary.Boarding_Gate = "";
        itinerary.Boarding_Date = "";
        itinerary.Boarding_Time = "";
        itinerary.Departure_Terminal = "";
        itinerary.Pax_Info = new ArrayList<>();
        itinerary.Pax_Info.add(paxInfo);

        CIBoardPassRespItineraryList list = new CIBoardPassRespItineraryList();
        list.add(itinerary);

        return list;
    }

    //清空DB裡的測試資料
    private void clearDBData(CIInquiryBoardPassDBEntity entity){
        RuntimeExceptionDao<CIInquiryBoardPassDBEntity, Integer> dao
                = CIApplication.getDbManager().getRuntimeExceptionDao(CIInquiryBoardPassDBEntity.class);
        dao.delete(entity);
    }
}
