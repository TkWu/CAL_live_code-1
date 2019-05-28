package com.chinaairlines.mobile30.Presenter;

import com.chinaairlines.mobile30.base.SBaseAndroidTestCase;

import junit.framework.Assert;

import java.util.concurrent.TimeUnit;

import ci.ws.Models.entities.CIExpiringMileageResp;
import ci.ws.Models.entities.CIInquiryAwardRecordRespList;
import ci.ws.Models.entities.CIMileageRecordResp;
import ci.ws.Models.entities.CIMileageResp;
import ci.ws.Models.entities.CIRedeemRecordResp;
import ci.ws.Presenter.CIInquiryMileagePresenter;
import ci.ws.Presenter.Listener.CIInquiryMileageListener;

/**
 * Created by jlchen on 2016/9/2.
 */
public class CIInquiryMileageWSPresenterTest extends SBaseAndroidTestCase {

    /**
     * 功能：驗證哩程管理Presenter與其對應的Model是否互動正確
     */

    //計數及順序的斷言用途
    private int m_iCount = 0;

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
     * 案例說明：無網路狀態，
     * 驗證 InquiryMileageFromWS() 調用 CIInquiryMileageModel 邏輯
     * 輸入參數：無
     * 斷言結果：應按順序回呼 showProgress(), onInquiryMileageError(), hideProgress()
     */
    public void testCase01() throws Exception {

        CIInquiryMileagePresenter.getInstance(new CIInquiryMileageListener() {
            @Override
            public void onInquiryMileageSuccess(String rt_code,
                                                String rt_msg,
                                                CIMileageResp data) {
                fail();
            }

            @Override
            public void onInquiryMileageError(String rt_code, String rt_msg) {

                m_iCount++;
                //斷言順序性及確保單次執行
                assertEquals(2, m_iCount);
            }

            @Override
            public void onInquiryExpiringMileageSuccess(String rt_code,
                                                        String rt_msg,
                                                        CIExpiringMileageResp expiringMileageResp) {
                fail();
            }

            @Override
            public void onInquiryExpiringMileageError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryMileageRecordSuccess(String rt_code,
                                                      String rt_msg,
                                                      CIMileageRecordResp mileageRecordResp) {
                fail();
            }

            @Override
            public void onInquiryMileageRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryRedeemRecordSuccess(String rt_code,
                                                     String rt_msg,
                                                     CIRedeemRecordResp redeemRecordResp) {
                fail();
            }

            @Override
            public void onInquiryRedeemRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryAwardRecordSuccess(String rt_code,
                                                    String rt_msg,
                                                    CIInquiryAwardRecordRespList awardRecordResps) {
                fail();
            }

            @Override
            public void onInquiryAwardRecordError(String rt_code, String rt_msg) {
                fail();
            }

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
                assertEquals(3, m_iCount);
            }

            @Override
            public void onAuthorizationFailedError(String rt_code, String rt_msg) {
                fail();
            }
        }).InquiryMileageFromWS();

        //停止unit-test thread，等待callback
        await(10, TimeUnit.SECONDS);

        Assert.assertEquals(3, m_iCount);
    }

    /**
     * 案例說明：無網路狀態，
     * 驗證 InquiryMileageCancel() 調用 CIInquiryMileageModel 邏輯
     * 輸入參數：無
     * 斷言結果：應回呼 hideProgress()
     */
    public void testCase02() throws Exception {

        CIInquiryMileagePresenter.getInstance(new CIInquiryMileageListener() {
            @Override
            public void onInquiryMileageSuccess(String rt_code,
                                                String rt_msg,
                                                CIMileageResp data) {
                fail();
            }

            @Override
            public void onInquiryMileageError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryExpiringMileageSuccess(String rt_code,
                                                        String rt_msg,
                                                        CIExpiringMileageResp expiringMileageResp) {
                fail();
            }

            @Override
            public void onInquiryExpiringMileageError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryMileageRecordSuccess(String rt_code,
                                                      String rt_msg,
                                                      CIMileageRecordResp mileageRecordResp) {
                fail();
            }

            @Override
            public void onInquiryMileageRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryRedeemRecordSuccess(String rt_code,
                                                     String rt_msg,
                                                     CIRedeemRecordResp redeemRecordResp) {
                fail();
            }

            @Override
            public void onInquiryRedeemRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryAwardRecordSuccess(String rt_code,
                                                    String rt_msg,
                                                    CIInquiryAwardRecordRespList awardRecordResps) {
                fail();
            }

            @Override
            public void onInquiryAwardRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void showProgress() {
                fail();
            }

            @Override
            public void hideProgress() {

                m_iCount++;
                //斷言順序性及確保單次執行
                assertEquals(1, m_iCount);
            }

            @Override
            public void onAuthorizationFailedError(String rt_code, String rt_msg) {
                fail();
            }
        }).InquiryMileageCancel();

        Assert.assertEquals(1, m_iCount);
    }

    /**
     * 案例說明：無網路狀態，
     * 驗證 InquiryExpiringMileageFromWS() 調用 CIInquiryExpiringMileageModel 邏輯
     * 輸入參數：無
     * 斷言結果：應按順序回呼 showProgress(), onInquiryExpiringMileageError(), hideProgress()
     */
    public void testCase03() throws Exception {

        CIInquiryMileagePresenter.getInstance(new CIInquiryMileageListener() {
            @Override
            public void onInquiryMileageSuccess(String rt_code,
                                                String rt_msg,
                                                CIMileageResp data) {
                fail();
            }

            @Override
            public void onInquiryMileageError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryExpiringMileageSuccess(String rt_code,
                                                        String rt_msg,
                                                        CIExpiringMileageResp expiringMileageResp) {
                fail();
            }

            @Override
            public void onInquiryExpiringMileageError(String rt_code, String rt_msg) {

                m_iCount++;
                //斷言順序性及確保單次執行
                assertEquals(2, m_iCount);
            }

            @Override
            public void onInquiryMileageRecordSuccess(String rt_code,
                                                      String rt_msg,
                                                      CIMileageRecordResp mileageRecordResp) {
                fail();
            }

            @Override
            public void onInquiryMileageRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryRedeemRecordSuccess(String rt_code,
                                                     String rt_msg,
                                                     CIRedeemRecordResp redeemRecordResp) {
                fail();
            }

            @Override
            public void onInquiryRedeemRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryAwardRecordSuccess(String rt_code,
                                                    String rt_msg,
                                                    CIInquiryAwardRecordRespList awardRecordResps) {
                fail();
            }

            @Override
            public void onInquiryAwardRecordError(String rt_code, String rt_msg) {
                fail();
            }

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
                assertEquals(3, m_iCount);
            }

            @Override
            public void onAuthorizationFailedError(String rt_code, String rt_msg) {
                fail();
            }
        }).InquiryExpiringMileageFromWS();

        //停止unit-test thread，等待callback
        await(10, TimeUnit.SECONDS);

        Assert.assertEquals(3, m_iCount);
    }

    /**
     * 案例說明：無網路狀態，
     * 驗證 InquiryExpiringMileageCancel() 調用 CIInquiryExpiringMileageModel 邏輯
     * 輸入參數：無
     * 斷言結果：應回呼 hideProgress()
     */
    public void testCase04() throws Exception {

        CIInquiryMileagePresenter.getInstance(new CIInquiryMileageListener() {
            @Override
            public void onInquiryMileageSuccess(String rt_code,
                                                String rt_msg,
                                                CIMileageResp data) {
                fail();
            }

            @Override
            public void onInquiryMileageError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryExpiringMileageSuccess(String rt_code,
                                                        String rt_msg,
                                                        CIExpiringMileageResp expiringMileageResp) {
                fail();
            }

            @Override
            public void onInquiryExpiringMileageError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryMileageRecordSuccess(String rt_code,
                                                      String rt_msg,
                                                      CIMileageRecordResp mileageRecordResp) {
                fail();
            }

            @Override
            public void onInquiryMileageRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryRedeemRecordSuccess(String rt_code,
                                                     String rt_msg,
                                                     CIRedeemRecordResp redeemRecordResp) {
                fail();
            }

            @Override
            public void onInquiryRedeemRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryAwardRecordSuccess(String rt_code,
                                                    String rt_msg,
                                                    CIInquiryAwardRecordRespList awardRecordResps) {
                fail();
            }

            @Override
            public void onInquiryAwardRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void showProgress() {
                fail();
            }

            @Override
            public void hideProgress() {

                m_iCount++;
                //斷言順序性及確保單次執行
                assertEquals(1, m_iCount);
            }

            @Override
            public void onAuthorizationFailedError(String rt_code, String rt_msg) {
                fail();
            }
        }).InquiryExpiringMileageCancel();

        Assert.assertEquals(1, m_iCount);
    }

    /**
     * 案例說明：無網路狀態，
     * 驗證 InquiryMileageRecordFromWS() 調用 CIInquiryMileageRecordModel 邏輯
     * 輸入參數：無
     * 斷言結果：應按順序回呼 showProgress(), onInquiryMileageRecordError(), hideProgress()
     */
    public void testCase05() throws Exception {

        CIInquiryMileagePresenter.getInstance(new CIInquiryMileageListener() {
            @Override
            public void onInquiryMileageSuccess(String rt_code,
                                                String rt_msg,
                                                CIMileageResp data) {
                fail();
            }

            @Override
            public void onInquiryMileageError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryExpiringMileageSuccess(String rt_code,
                                                        String rt_msg,
                                                        CIExpiringMileageResp expiringMileageResp) {
                fail();
            }

            @Override
            public void onInquiryExpiringMileageError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryMileageRecordSuccess(String rt_code,
                                                      String rt_msg,
                                                      CIMileageRecordResp mileageRecordResp) {
                fail();
            }

            @Override
            public void onInquiryMileageRecordError(String rt_code, String rt_msg) {

                m_iCount++;
                //斷言順序性及確保單次執行
                assertEquals(2, m_iCount);
            }

            @Override
            public void onInquiryRedeemRecordSuccess(String rt_code,
                                                     String rt_msg,
                                                     CIRedeemRecordResp redeemRecordResp) {
                fail();
            }

            @Override
            public void onInquiryRedeemRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryAwardRecordSuccess(String rt_code,
                                                    String rt_msg,
                                                    CIInquiryAwardRecordRespList awardRecordResps) {
                fail();
            }

            @Override
            public void onInquiryAwardRecordError(String rt_code, String rt_msg) {
                fail();
            }

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
                assertEquals(3, m_iCount);
            }

            @Override
            public void onAuthorizationFailedError(String rt_code, String rt_msg) {
                fail();
            }
        }).InquiryMileageRecordFromWS();

        //停止unit-test thread，等待callback
        await(10, TimeUnit.SECONDS);

        Assert.assertEquals(3, m_iCount);
    }

    /**
     * 案例說明：無網路狀態，
     * 驗證 InquiryMileageRecordCancel() 調用 CIInquiryMileageRecordModel 邏輯
     * 輸入參數：無
     * 斷言結果：應回呼 hideProgress()
     */
    public void testCase06() throws Exception {

        CIInquiryMileagePresenter.getInstance(new CIInquiryMileageListener() {
            @Override
            public void onInquiryMileageSuccess(String rt_code,
                                                String rt_msg,
                                                CIMileageResp data) {
                fail();
            }

            @Override
            public void onInquiryMileageError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryExpiringMileageSuccess(String rt_code,
                                                        String rt_msg,
                                                        CIExpiringMileageResp expiringMileageResp) {
                fail();
            }

            @Override
            public void onInquiryExpiringMileageError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryMileageRecordSuccess(String rt_code,
                                                      String rt_msg,
                                                      CIMileageRecordResp mileageRecordResp) {
                fail();
            }

            @Override
            public void onInquiryMileageRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryRedeemRecordSuccess(String rt_code,
                                                     String rt_msg,
                                                     CIRedeemRecordResp redeemRecordResp) {
                fail();
            }

            @Override
            public void onInquiryRedeemRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryAwardRecordSuccess(String rt_code,
                                                    String rt_msg,
                                                    CIInquiryAwardRecordRespList awardRecordResps) {
                fail();
            }

            @Override
            public void onInquiryAwardRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void showProgress() {
                fail();
            }

            @Override
            public void hideProgress() {

                m_iCount++;
                //斷言順序性及確保單次執行
                assertEquals(1, m_iCount);
            }

            @Override
            public void onAuthorizationFailedError(String rt_code, String rt_msg) {
                fail();
            }
        }).InquiryMileageRecordCancel();

        Assert.assertEquals(1, m_iCount);
    }

    /**
     * 案例說明：無網路狀態，
     * 驗證 InquiryRedeemRecordFromWS() 調用 CIInquiryRedeemRecordModel 邏輯
     * 輸入參數：無
     * 斷言結果：應按順序回呼 showProgress(), onInquiryRedeemRecordError(), hideProgress()
     */
    public void testCase07() throws Exception {

        CIInquiryMileagePresenter.getInstance(new CIInquiryMileageListener() {
            @Override
            public void onInquiryMileageSuccess(String rt_code,
                                                String rt_msg,
                                                CIMileageResp data) {
                fail();
            }

            @Override
            public void onInquiryMileageError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryExpiringMileageSuccess(String rt_code,
                                                        String rt_msg,
                                                        CIExpiringMileageResp expiringMileageResp) {
                fail();
            }

            @Override
            public void onInquiryExpiringMileageError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryMileageRecordSuccess(String rt_code,
                                                      String rt_msg,
                                                      CIMileageRecordResp mileageRecordResp) {
                fail();
            }

            @Override
            public void onInquiryMileageRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryRedeemRecordSuccess(String rt_code,
                                                     String rt_msg,
                                                     CIRedeemRecordResp redeemRecordResp) {
                fail();
            }

            @Override
            public void onInquiryRedeemRecordError(String rt_code, String rt_msg) {

                m_iCount++;
                //斷言順序性及確保單次執行
                assertEquals(2, m_iCount);
            }

            @Override
            public void onInquiryAwardRecordSuccess(String rt_code,
                                                    String rt_msg,
                                                    CIInquiryAwardRecordRespList awardRecordResps) {
                fail();
            }

            @Override
            public void onInquiryAwardRecordError(String rt_code, String rt_msg) {
                fail();
            }

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
                assertEquals(3, m_iCount);
            }

            @Override
            public void onAuthorizationFailedError(String rt_code, String rt_msg) {
                fail();
            }
        }).InquiryRedeemRecordFromWS();

        //停止unit-test thread，等待callback
        await(10, TimeUnit.SECONDS);

        Assert.assertEquals(3, m_iCount);
    }

    /**
     * 案例說明：無網路狀態，
     * 驗證 InquiryRedeemRecordCancel() 調用 CIInquiryRedeemRecordModel 邏輯
     * 輸入參數：無
     * 斷言結果：應回呼 hideProgress()
     */
    public void testCase08() throws Exception {

        CIInquiryMileagePresenter.getInstance(new CIInquiryMileageListener() {
            @Override
            public void onInquiryMileageSuccess(String rt_code,
                                                String rt_msg,
                                                CIMileageResp data) {
                fail();
            }

            @Override
            public void onInquiryMileageError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryExpiringMileageSuccess(String rt_code,
                                                        String rt_msg,
                                                        CIExpiringMileageResp expiringMileageResp) {
                fail();
            }

            @Override
            public void onInquiryExpiringMileageError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryMileageRecordSuccess(String rt_code,
                                                      String rt_msg,
                                                      CIMileageRecordResp mileageRecordResp) {
                fail();
            }

            @Override
            public void onInquiryMileageRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryRedeemRecordSuccess(String rt_code,
                                                     String rt_msg,
                                                     CIRedeemRecordResp redeemRecordResp) {
                fail();
            }

            @Override
            public void onInquiryRedeemRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryAwardRecordSuccess(String rt_code,
                                                    String rt_msg,
                                                    CIInquiryAwardRecordRespList awardRecordResps) {
                fail();
            }

            @Override
            public void onInquiryAwardRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void showProgress() {
                fail();
            }

            @Override
            public void hideProgress() {

                m_iCount++;
                //斷言順序性及確保單次執行
                assertEquals(1, m_iCount);
            }

            @Override
            public void onAuthorizationFailedError(String rt_code, String rt_msg) {
                fail();
            }
        }).InquiryRedeemRecordCancel();

        Assert.assertEquals(1, m_iCount);
    }

    /**
     * 案例說明：無網路狀態，
     * 驗證 InquiryAwardRecordFromWS() 調用 CIInquiryAwardRecordModel 邏輯
     * 輸入參數：無
     * 斷言結果：應按順序回呼 showProgress(), onInquiryAwardRecordError(), hideProgress()
     */
    public void testCase09() throws Exception {

        CIInquiryMileagePresenter.getInstance(new CIInquiryMileageListener() {
            @Override
            public void onInquiryMileageSuccess(String rt_code,
                                                String rt_msg,
                                                CIMileageResp data) {
                fail();
            }

            @Override
            public void onInquiryMileageError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryExpiringMileageSuccess(String rt_code,
                                                        String rt_msg,
                                                        CIExpiringMileageResp expiringMileageResp) {
                fail();
            }

            @Override
            public void onInquiryExpiringMileageError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryMileageRecordSuccess(String rt_code,
                                                      String rt_msg,
                                                      CIMileageRecordResp mileageRecordResp) {
                fail();
            }

            @Override
            public void onInquiryMileageRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryRedeemRecordSuccess(String rt_code,
                                                     String rt_msg,
                                                     CIRedeemRecordResp redeemRecordResp) {
                fail();
            }

            @Override
            public void onInquiryRedeemRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryAwardRecordSuccess(String rt_code,
                                                    String rt_msg,
                                                    CIInquiryAwardRecordRespList awardRecordResps) {
                fail();
            }

            @Override
            public void onInquiryAwardRecordError(String rt_code, String rt_msg) {

                m_iCount++;
                //斷言順序性及確保單次執行
                assertEquals(2, m_iCount);
            }

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
                assertEquals(3, m_iCount);
            }

            @Override
            public void onAuthorizationFailedError(String rt_code, String rt_msg) {
                fail();
            }
        }).InquiryAwardRecordFromWS();

        //停止unit-test thread，等待callback
        await(10, TimeUnit.SECONDS);

        Assert.assertEquals(3, m_iCount);
    }

    /**
     * 案例說明：無網路狀態，
     * 驗證 InquiryAwardRecordCancel() 調用 CIInquiryAwardRecordModel 邏輯
     * 輸入參數：無
     * 斷言結果：應回呼 hideProgress()
     */
    public void testCase10() throws Exception {

        CIInquiryMileagePresenter.getInstance(new CIInquiryMileageListener() {
            @Override
            public void onInquiryMileageSuccess(String rt_code,
                                                String rt_msg,
                                                CIMileageResp data) {
                fail();
            }

            @Override
            public void onInquiryMileageError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryExpiringMileageSuccess(String rt_code,
                                                        String rt_msg,
                                                        CIExpiringMileageResp expiringMileageResp) {
                fail();
            }

            @Override
            public void onInquiryExpiringMileageError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryMileageRecordSuccess(String rt_code,
                                                      String rt_msg,
                                                      CIMileageRecordResp mileageRecordResp) {
                fail();
            }

            @Override
            public void onInquiryMileageRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryRedeemRecordSuccess(String rt_code,
                                                     String rt_msg,
                                                     CIRedeemRecordResp redeemRecordResp) {
                fail();
            }

            @Override
            public void onInquiryRedeemRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onInquiryAwardRecordSuccess(String rt_code,
                                                    String rt_msg,
                                                    CIInquiryAwardRecordRespList awardRecordResps) {
                fail();
            }

            @Override
            public void onInquiryAwardRecordError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void showProgress() {
                fail();
            }

            @Override
            public void hideProgress() {

                m_iCount++;
                //斷言順序性及確保單次執行
                assertEquals(1, m_iCount);
            }

            @Override
            public void onAuthorizationFailedError(String rt_code, String rt_msg) {
                fail();
            }
        }).InquiryAwardRecordCancel();

        Assert.assertEquals(1, m_iCount);
    }
}
