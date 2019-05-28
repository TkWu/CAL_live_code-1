package com.chinaairlines.mobile30.Presenter;

import com.chinaairlines.mobile30.base.SBaseAndroidTestCase;

import junit.framework.Assert;

import java.util.concurrent.TimeUnit;

import ci.ws.Models.entities.CIInquiryCouponResp;
import ci.ws.Presenter.CIInquiryCouponInfoPresenter;
import ci.ws.Presenter.Listener.CIInquiryCouponInfoListener;

/**
 * Created by jlchen on 2016/9/1.
 */
public class CIInquiryCouponInfoWSPresenterTest extends SBaseAndroidTestCase {

    /**
     * 功能：驗證優惠券Presenter與其對應的Model是否互動正確
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
     * 驗證 InquiryCouponInfoFromWS() 調用 CIInquiryCouponInfoModel 邏輯
     * 輸入參數：無
     * 斷言結果：應按順序回呼 showProgress(), hideProgress(), onError()
     */
    public void testCase01() throws Exception {

        CIInquiryCouponInfoPresenter.getInstance(new CIInquiryCouponInfoListener() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIInquiryCouponResp datas) {
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

        }).InquiryCouponInfoFromWS();

        //停止unit-test thread，等待callback
        await(10, TimeUnit.SECONDS);

        Assert.assertEquals(3, m_iCount);
    }

    /**
     * 案例說明：無網路狀態，驗證調用 interrupt() 邏輯
     * 輸入參數：無
     * 斷言結果：應回呼 hideProgress()
     */
    public void testCase02() throws Exception {

        CIInquiryCouponInfoPresenter.getInstance(new CIInquiryCouponInfoListener() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIInquiryCouponResp datas) {
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

        Assert.assertEquals(1, m_iCount);
    }
}
