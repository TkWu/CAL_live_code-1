package com.chinaairlines.mobile30.Model;

import android.text.TextUtils;

import com.chinaairlines.mobile30.Config;
import com.chinaairlines.mobile30.base.SBaseAndroidTestCase;

import junit.framework.Assert;

import java.util.concurrent.TimeUnit;

import ci.function.Core.CIApplication;
import ci.ws.Models.CIInquiryExpiringMileageModel;
import ci.ws.Models.CILoginWSModel;
import ci.ws.Models.entities.CIExpiringMileageResp;
import ci.ws.Models.entities.CILoginReq;
import ci.ws.Models.entities.CILoginResp;
import ci.ws.Models.entities.CIMileageReq;
import ci.ws.cores.CIWSShareManager;
import ci.ws.define.CIWSResultCode;
import ci.ws.define.WSConfig;

/**
 * Created by jlchen on 2016/8/19.
 */
public class CIInquiryExpiringMileageWSModelTest extends SBaseAndroidTestCase {

    /**
     * 功能：查詢會員近六個月即將到期的里程資料。向API請求資料後，依據結果調用正確之callback。
     */

    /**
     * 向API請求資料-> 是否請求資料成功
     * (1)成功：調用callback的 onSuccess 返回資料給presenter
     * (2)失敗：調用callback的 onError 返回錯誤訊息給presenter
     *
     * 影響請求資料是否成功的事件
     * (1)有無網路
     * (2)傳入參數是否正確
     * */

    //計數及順序的斷言用途
    private int             m_iCount        = 0;
    //向WS請求所發出的request
    private CIMileageReq    m_reqMileage    = null;

    /**
     *  開始測試前執行（預先設置測試所需的測試資料）
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        m_reqMileage = new CIMileageReq();
    }

    /**
     *  測試完成後執行（用於移除測試資料）
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        CIApplication.getLoginInfo().ClearLoginData();
        CIWSShareManager.getAPI().setLoginToken("");
    }

    /**
     * 案例說明：有網路狀態，傳入正確的參數，使用setMileageReq()和DoConnection()向WS請求即將到期里程資料成功
     * 輸入參數：m_reqMileage
     * 斷言結果：回呼onSuccess(), rt_code必為000, resp不為null, 並驗證回傳結果是否正確
     */
    public void testCase01() throws Exception {

        doLogin();

        //開啟網路
        setWifiEnabled(true);

        //初始化倒數啟動執行緒
        init(1);

        CIInquiryExpiringMileageModel model = new CIInquiryExpiringMileageModel(
                new CIInquiryExpiringMileageModel.InquiryExpiringMileageCallBack() {
            @Override
            public void onInquiryExpiringMileageSuccess(String rt_code,
                                                        String rt_msg,
                                                        CIExpiringMileageResp mileageResp) {

                m_iCount++;

                //結果代碼為000，表示成功
                Assert.assertEquals( Config.WS_RESULT_CODE_SUCCESS, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                //傳回資料不可為null, 不然應調用onError
                Assert.assertNotNull( mileageResp );

                //近六個月快到期的里程資料，應該回傳長度為6的list
                Assert.assertEquals( 6, mileageResp.size() );

                Assert.assertEquals( 1, m_iCount );

                //倒數歸零 啟動unit-test thread
                countDown();
            }

            @Override
            public void onInquiryExpiringMileageError(String rt_code, String rt_msg) {

                //如果呼叫這個callback則斷言失敗
                fail();
            }
        });

        model.setMileageReq(m_reqMileage);
        model.DoConnection();

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     * 案例說明：無網路狀態，傳入正確的參數，使用setMileageReq()和DoConnection()向WS請求即將到期里程資料失敗
     * 輸入參數：m_reqMileage
     * 斷言結果：回呼onError, rt_code必為9999
     */
    public void testCase02() throws Exception {

        doLogin();

        //關閉網路
        setWifiEnabled(false);

        //初始化倒數啟動執行緒
        init(1);

        CIInquiryExpiringMileageModel model = new CIInquiryExpiringMileageModel(
                new CIInquiryExpiringMileageModel.InquiryExpiringMileageCallBack() {
                    @Override
                    public void onInquiryExpiringMileageSuccess(String rt_code,
                                                                String rt_msg,
                                                                CIExpiringMileageResp mileageResp) {

                        //如果呼叫這個callback則斷言失敗
                        fail();
                    }

                    @Override
                    public void onInquiryExpiringMileageError(String rt_code, String rt_msg) {

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

        model.setMileageReq(m_reqMileage);
        model.DoConnection();

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     * 案例說明：有網路狀態，傳入錯誤的參數，使用setMileageReq()和DoConnection()向WS請求即將到期里程資料失敗
     * 輸入參數：m_reqMileage（card_no=null）
     * 斷言結果：回呼onError, rt_code必為-999
     */
    public void testCase03() throws Exception {

        doLogin();

        m_reqMileage.card_no = null;

        //關閉網路
        setWifiEnabled(true);

        //初始化倒數啟動執行緒
        init(1);

        CIInquiryExpiringMileageModel model = new CIInquiryExpiringMileageModel(
                new CIInquiryExpiringMileageModel.InquiryExpiringMileageCallBack() {
                    @Override
                    public void onInquiryExpiringMileageSuccess(String rt_code,
                                                                String rt_msg,
                                                                CIExpiringMileageResp mileageResp) {

                        //如果呼叫這個callback則斷言失敗
                        fail();
                    }

                    @Override
                    public void onInquiryExpiringMileageError(String rt_code, String rt_msg) {

                        m_iCount++;

                        //結果代碼為9999，表示無網路連線
                        Assert.assertEquals( Config.WS_RESULT_CODE_ERROR, rt_code );

                        //結果訊息不為空值
                        Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                        Assert.assertEquals(1, m_iCount);

                        //倒數歸零 啟動 unit-test thread
                        countDown();
                    }
                });

        model.setMileageReq(m_reqMileage);
        model.DoConnection();

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    private void doLogin() throws Exception {

        //開啟網路
        setWifiEnabled(true);

        final CILoginReq loginReq = new CILoginReq();
        loginReq.user_id = Config.CARD_NO;
        loginReq.password = Config.PASSWORD;

        new CILoginWSModel(loginReq,
                CIApplication.getLanguageInfo().getWSLanguage(),
                CIApplication.getDeviceInfo().getAndroidId(),
                WSConfig.DEF_API_VERSION,
                new CILoginWSModel.LoginCallback() {
            @Override
            public void onLoginSuccess(String rt_code, String rt_msg, CILoginResp loginResp) {

                m_reqMileage.login_token = loginResp.member_token;
                m_reqMileage.card_no     = loginResp.card_no;

                //倒數歸零 啟動unit-test thread
                countDown();
            }

            @Override
            public void onLoginError(String rt_code, String rt_msg) {

                m_reqMileage.login_token = "%c3%83%c2%ae%c2%a3w%c2%a0%5e%c2%97%c2%a7%c2%a5";
                m_reqMileage.card_no     = Config.CARD_NO;

                //倒數歸零 啟動unit-test thread
                countDown();
            }
        }).DoConnection();

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);
    }
}
