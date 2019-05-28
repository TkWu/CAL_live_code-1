package com.chinaairlines.mobile30.Presenter;

import com.chinaairlines.mobile30.base.SBaseAndroidTestCase;

import java.util.concurrent.TimeUnit;

import ci.ws.Models.entities.CICheckFlightMealOpenReq;
import ci.ws.Presenter.CICheckFlightMealOpenPresenter;
import ci.ws.Presenter.Listener.CICheckFlightMealOpenListener;

/**
 * Created by jlchen on 2016/9/1.
 */
public class CICheckFlightMealOpenWSPresenterTest extends SBaseAndroidTestCase {

    /**
     * 功能：驗證確認是否開放預選餐點WS Presenter與其對應的Model是否互動正確
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

    /**
     *  測試完成後執行（用於移除測試資料）
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * 案例說明：無網路狀態，
     * 驗證 CheckFlightMealOpenFromWS() 調用 CICheckFlightMealOpenModel 邏輯
     * 輸入參數：無
     * 斷言結果：應按順序回呼 showProgress(), hideProgress(), onError()
     */
    public void testCase01() throws Exception {

        CICheckFlightMealOpenPresenter.getInstance(new CICheckFlightMealOpenListener() {
            @Override
            public void onSuccess(String rt_code, String rt_msg) {
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
        }).CheckFlightMealOpenFromWS(new CICheckFlightMealOpenReq());

        //停止unit-test thread，等待callback
        await(10, TimeUnit.SECONDS);

        //斷言完整性
        assertEquals(3, m_iCount);
    }

    /**
     * 案例說明：無網路狀態，
     * 驗證 CheckFlightMealOpenFromWS() 調用 CICheckFlightMealOpenModel 邏輯
     * 輸入參數：無
     * 斷言結果：應按順序回呼 showProgress(), hideProgress(), onError()
     */
    public void testCase02() throws Exception {

        CICheckFlightMealOpenPresenter.getInstance(new CICheckFlightMealOpenListener() {
            @Override
            public void onSuccess(String rt_code, String rt_msg) {
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
        }).CheckFlightMealOpenCancel();

        //斷言完整性
        assertEquals(1, m_iCount);
    }
}
