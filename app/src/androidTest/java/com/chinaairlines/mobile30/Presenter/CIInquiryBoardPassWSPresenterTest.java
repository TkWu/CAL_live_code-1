package com.chinaairlines.mobile30.Presenter;

import com.chinaairlines.mobile30.base.SBaseAndroidTestCase;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import ci.ws.Models.entities.CIBoardPassResp;
import ci.ws.Presenter.CIInquiryBoardPassPresenter;
import ci.ws.Presenter.Listener.CIInquiryBoardPassListener;

/**
 * Created by jlchen on 2016/6/2.
 */
public class CIInquiryBoardPassWSPresenterTest extends SBaseAndroidTestCase {

    /**
     * 功能：驗證登機證Presenter與其對應的Model是否互動正確
     */

    //卡號
    private static final String         CARD_NO     = "";
    //訂票代號(可能為多筆)
    private static final Set<String>    PNR_LIST    = new LinkedHashSet<>();

    //訂票代號(單筆)
    private static final String         PNR_ID      = "";
    //機票號碼
    private static final String         TICKET      = "";
    //護照名字
    private static final String         FIRST_NAME  = "";
    //護照姓氏
    private static final String         LAST_NAME   = "";

    //計數及順序的斷言用途
    private int                         m_iCount    = 0;

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
     * 案例說明：無網路狀態，使用卡號與PNR list查詢登機證資料，
     * 驗證 InquiryBoardPassFromWSByPNRListAndCardNo() 調用 CIInquiryBoardingPassModel 邏輯
     * 輸入參數：無
     * 斷言結果：應按順序回呼 showProgress(), hideProgress(), onError()
     */
    public void testCase01() throws Exception{

        CIInquiryBoardPassPresenter.getInstance(new CIInquiryBoardPassListener() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                fail();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                m_iCount++;
                //斷言順序性及確保單次執行
                assertEquals(3, m_iCount);
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
                assertEquals(2, m_iCount);
            }

        }).InquiryBoardPassFromWSByPNRListAndCardNo(CARD_NO,"","", PNR_LIST);

        //停止unit-test thread，等待callback
        await(10, TimeUnit.SECONDS);

        //斷言完整性
        assertEquals(3, m_iCount);
    }

    /**
     * 案例說明：無網路狀態，使用PNR與護照姓名查詢登機證資料，
     * 驗證 InquiryBoardPassFromWSByPNRNo() 調用 CIInquiryBoardingPassModel 邏輯
     * 輸入參數：無
     * 斷言結果：應按順序回呼 showProgress(), hideProgress(), onError()
     */
    public void testCase02() throws Exception{

        CIInquiryBoardPassPresenter.getInstance(new CIInquiryBoardPassListener() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                fail();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                m_iCount++;
                //斷言順序性及確保單次執行
                assertEquals(3, m_iCount);
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
                assertEquals(2, m_iCount);
            }

        }).InquiryBoardPassFromWSByPNRNo(PNR_ID, FIRST_NAME, LAST_NAME);

        //停止unit-test thread，等待callback
        await(10, TimeUnit.SECONDS);

        //斷言完整性
        assertEquals(3, m_iCount);
    }

    /**
     * 案例說明：無網路狀態，使用PNR與護照姓名查詢登機證資料，
     * 驗證 InquiryBoardPassFromWSByTicket() 調用 CIInquiryBoardingPassModel 邏輯
     * 輸入參數：無
     * 斷言結果：應按順序回呼 showProgress(), hideProgress(), onError()
     */
    public void testCase03() throws Exception{

        CIInquiryBoardPassPresenter.getInstance(new CIInquiryBoardPassListener() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                fail();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                m_iCount++;
                //斷言順序性及確保單次執行
                assertEquals(3, m_iCount);
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
                assertEquals(2, m_iCount);
            }

        }).InquiryBoardPassFromWSByTicket(TICKET, FIRST_NAME, LAST_NAME);

        //停止unit-test thread，等待callback
        await(10, TimeUnit.SECONDS);

        //斷言完整性
        assertEquals(3, m_iCount);
    }

    /**
     * 案例說明：無網路狀態，驗證調用 interrupt() 邏輯
     * 輸入參數：無
     * 斷言結果：應回呼 hideProgress()
     */
    public void testCase04() throws Exception{

        CIInquiryBoardPassPresenter.getInstance(new CIInquiryBoardPassListener() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                fail();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
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

        }).interrupt();

        //斷言完整性
        assertEquals(1, m_iCount);
    }
}
