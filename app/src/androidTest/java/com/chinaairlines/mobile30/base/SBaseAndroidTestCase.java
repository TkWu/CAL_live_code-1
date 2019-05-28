package com.chinaairlines.mobile30.base;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * Created by ShihMin on 2015/10/22.
 * Last Edited by Kevin
 * Version : 1.0.1
 * -------------------------------------------------------------------------------------------------
 * 單元測試說明
 * -------------------------------------------------------------------------------------------------
 * 類別及方法命名規則
 * -------------------------------------------------------------------------------------------------
 * （一）類別(Class)
 *      { Class Name } + Test
 *      例如：SScheduleModelTest、SSerialModelTest、SCustomerPresenterTest
 *
 * （二）方法(Method)
 *      testCase + { 數字、底線 }
 *      例如：testCase01、testCase02、testCase1_1、testCase1_2
 * -------------------------------------------------------------------------------------------------
 * 測試案例說明文件
 * -------------------------------------------------------------------------------------------------
 * （一）於每個class開頭扼要說明目前被測試的單元的主要功能為何
 *      例如：
 *
 *      [Model]
 *      主要功能
 *      （1）員工行事曆
 *      （2）會辦單指派（指派、轉指派）
 *      模式：
 *      online mode
 *
 *      [Presenter]
 *      主要邏輯
 *      (1) MODE_SERIAL_QUERY  側邊欄流水訊號查詢頁面
 *      (2) MODE_CUSTOMER_INFO 客戶資料查詢中的流水訊號頁面
 *
 *      [View]
 *      (略)
 *
 * （二）於setUp()中扼要說明前置作業
 *      例如：
 *           protected void setUp() throws Exception {
 *           super.setUp();
 *           //關閉無線網路
 *           setWifiEnabled(false);
 *           }
 *
 * （三）於每個測試案例方法（testCase()）前端攥寫說明，格式如下：
 *
 *      （1）案例說明：（例如：有可用網路、驗證 assignScheduleData() 有調用SScheduleModel搜尋遠端資料的方法）
 *      （2）輸入參數：
 *
 *          例如：
 *            建立資料 SScheduleList data（可列出重要的變數）
 *            or
 *            調整 SScheduleList 某一變數
 *            or
 *            SRespException:
 *            error code    = -31
 *            error message = 找不到任何資料,請重新查詢
 *
 *      （3）斷言結果：
 *          [Model]
 *          (例如: 應可從資料庫搜尋到兩筆資料....)
 *
 *          [Presenter]
 *          (例如: 調用順序為 showProgress()、hideAll().....)
 *
 *          [View]
 *          (略)
 *
 * （四）於每個一般方法前的javadoc格式
 *      （1）使用說明： 使用方法需宣告哪個permission，方法效果
 *          例如：可設定倒數啟動的次數及timeout，使單元測試執行緒等待執行
 *      （2）@param + 參數名 如有定義參數需定義說明
 *          例如：@param unit    時間單位，null不會設定timeout
 *      （3）@return 如有返回值需定義說明
 *          例如：boolean值，是否成功執行
 *
 * -------------------------------------------------------------------------------------------------
 *   ModelTest 測試案例說明範例
 * -------------------------------------------------------------------------------------------------
 *
 *  案例說明：有可網路狀態、調用findByContractNum(String FoNum,CallBack callBack)、驗證是否可取的資料
 *  輸入參數：FoNum = "EE201310001590"
 *  斷言結果：callBack取得相對應的主機號碼資料
 *
 * -------------------------------------------------------------------------------------------------
 *  PresenterTest 測試案例說明範例
 * -------------------------------------------------------------------------------------------------
 *
 *  案例說明：無網路狀態，調用fetchSingleDispatchData() 驗證是否可調用 SDispatchModel搜尋遠端的方法
 *  輸入參數： SRespException:
 *           error code    = -31
 *           error message = 找不到任何資料,請重新查詢
 *
 *  斷言結果：（1）調用的順序 showProgress(),hideProgress(),onFailedFetchOrUpdateData()
 *          （2）onFailedFetchOrUpdateData(SRespException exception)的exception不為null
 * -------------------------------------------------------------------------------------------------
 * </pre>
 *********************************************************************************************/
public abstract class SBaseAndroidTestCase extends AndroidTestCase {

    protected final String TAG                  = getClass().getSimpleName();
    private WifiManager m_wifiManager        = null;



    //設置倒數啟動執行緒物件（使用於異步任務，主要控制unit test main thread）
    private CountDownLatch m_threadController = null;

    /**
     * 須在AndroidManifest.xml中定義以下uses-permission<br>
     * uses-permission android:name="android.permission.CHANGE_WIFI_STATE"<br>
     *
     * 設定當前測試的wifi狀態<br>
     * @param enable
     * 如設定 enable = true, 當前在未啟動狀態下會延遲5秒等待wifi連線<br>
     * 啟動失敗最多重試5次<br>
     *
     * 如設定 enable = false, 當前在啟動狀態下會延遲5秒等待wifi關閉連線<br>
     */
    public void setWifiEnabled(boolean enable) {
        if (m_wifiManager == null) {
            m_wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        }
        if (true == enable && WifiManager.WIFI_STATE_ENABLED > m_wifiManager.getWifiState()) {
            int retry = 0;
            while (WifiManager.WIFI_STATE_ENABLED > m_wifiManager.getWifiState() && retry < 5) {
                retry++;
                m_wifiManager.setWifiEnabled(true);
                try {
                    Log.d(TAG, "Wifi state enabling... wait 5s");
                    Thread.sleep(5 * 1000);
                    if (m_wifiManager.getWifiState() > WifiManager.WIFI_STATE_ENABLED) {
                        Log.d(TAG, "Wifi尚未連接.. 重新啟動(" + retry + ")");
                    } else {
                        Log.d(TAG, "Wifi已啟動");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if(false == enable && WifiManager.WIFI_STATE_DISABLED != m_wifiManager.getWifiState()){
            m_wifiManager.setWifiEnabled(false);
            Log.d(TAG, "Wifi state disabling... wait 5s");
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Wifi已關閉");
        } else if (true == enable && WifiManager.WIFI_STATE_ENABLED == m_wifiManager.getWifiState()) {
            Log.d(TAG, "Wifi已啟動");
        } else if (false == enable && WifiManager.WIFI_STATE_DISABLED == m_wifiManager.getWifiState()) {
            Log.d(TAG, "Wifi已關閉");
        }
    }

    /**
     * 初始化倒數啟動執行緒
     * @param count 倒數計數數量，小於或等於零會設為1
     */
    public void init(int count){
        if(0 >= count){
            count = 1;
        }
        m_threadController = new CountDownLatch(count);
    }

    /**
     * 使單元測試執行緒等待執行<br>
     * 若無init()則會自動初始化並預設倒數次數為1
     * @throws InterruptedException 中斷異常
     */
    public void await() throws InterruptedException {
        await(0, 0, null);
    }

    /**
     * 設定倒數啟動次數並使單元測試執行緒等待執行<br>
     * 若無init()，且倒數次數等於或小於0，則會自動初始化並預設倒數次數為1
     * @param count 倒數啟動次數，數值大於等於1才會重設倒數次數
     * @throws InterruptedException 中斷異常
     */
    public void await(int count) throws InterruptedException {
        await(count, 0, null);
    }

    /**
     * 可設定Timeout，並使單元測試執行緒等待執行<br>
     * 若無init()則會自動初始化並預設倒數次數為1
     * @param timeout 倒數啟動次數，數值小於等於零不會設定timeout
     * @param unit    時間單位，null不會設定timeout
     * @return        true是成功執行
     * @throws InterruptedException 中斷異常
     */
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return await(0, timeout, unit);
    }

    /**
     * 可設定倒數啟動的次數及timeout，並使單元測試執行緒等待執行<br>
     * 若無init()，且倒數次數等於或小於0，則會自動初始化並預設倒數次數為1
     * @param count   倒數啟動次數，數值大於等於1才會重設倒數次數
     * @param timeout 時間值，數值小於等於零不會設定timeout
     * @param unit    時間單位，null不會設定timeout
     * @return        true是成功執行
     * @throws InterruptedException 中斷異常
     */
    public boolean await(int count, long timeout, TimeUnit unit) throws InterruptedException {
        //如果沒有初始化或有設定count數才會初始化倒數啟動物件
        if(null == m_threadController || 1 <= count){
            init(count);
        }
        if(0 < timeout && null != unit){
            return m_threadController.await(timeout, unit);
        }else{
            m_threadController.await();
            return true;
        }
    }

    /**
     * 倒數由await()設定的次數，數字歸零時將啟動等待的執行緒
     */
    public void countDown() {
        m_threadController.countDown();
    }

    /**
     * 立即啟動等待的執行緒
     */
    public void latch(){
        long lCount = getCount();
        for(long loop = 0;loop < lCount;loop++){
            m_threadController.countDown();
        }
    }

    /**
     * 取得剩餘倒數次數
     * @return 次數
     */
    public long getCount(){
        return m_threadController.getCount();
    }

}
