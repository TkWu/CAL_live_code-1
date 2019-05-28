package com.chinaairlines.mobile30.manager;

import android.text.TextUtils;

import com.chinaairlines.mobile30.base.SBaseAndroidTestCase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import ci.function.Core.CIApplication;
import ci.ui.define.HomePage_Status;
import ci.ui.object.CIPNRStatusManager;
import ci.ui.object.CIPNRStatusModel;
import ci.ui.object.item.CIHomeStatusEntity;
import ci.ws.Models.CIInquiryCheckInModel;
import ci.ws.Models.CIInquiryTripModel;
import ci.ws.Models.entities.CIInquiryTripEntity;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Models.entities.CITripListResp;
import ci.ws.Presenter.CIInquiryFlightStationPresenter;
import ci.ws.Presenter.Listener.CIInquiryFlightStatusStationListener;
import ci.ws.cores.object.GsonTool;

/**
 * Created by kevincheng on 2016/6/13.
 */
public class CIPNRStatusManagerTest extends SBaseAndroidTestCase {
    /**
     * 此類別主要是根據PNR及CPR資料來攥寫邏輯轉換首頁狀態
     * 並將所需資料使用 CIHomeStatusEntity 帶過去CIHomeFragment
     * 去給對應的頁面使用
     * 此測試必須在DEV環境下
     */

    private int m_iCount = 0;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        //在Dev環境，以無網路狀態可以取得模擬資料
        setWifiEnabled(false);
        //設定非登入狀態
        CIApplication.getLoginInfo().SetLoginStatus(false);
        //預設資料庫內都有一筆資料可以顯示
        insertDataToDB();
        //取得機場資料
        getTheAirportData();

        //預先產生新的實例，並初始化環境
        CIHomeStatusEntity data = new CIHomeStatusEntity();
        data.bHaveCPRData = true;
        CIPNRStatusManager.getInstance(null).setHomeStatusEntityFromWS(data);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        //清除設定檔案資料
        CIInquiryTripModel.setTestFileName(null);
        CIInquiryCheckInModel.setTestFileName(null);
        //清除DB的測試資料
        clearDBData();
        //清除實例
        CIPNRStatusManager.clearInstance();
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 9 時的首頁狀態
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_NoTicket.json
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_3D.json
     *      - 模擬api回應設定：onSuccess
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為無訂票，HomePage_Status.TYPE_A_NO_TICKET，且無法取得CPR資料
     */
    public void testCase01() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_NoTicket.json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_3D.json");
        CIInquiryCheckInModel.setIsOnSuccess(true);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {
                fail();
            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                m_iCount++;

                assertEquals(1, m_iCount);
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onDismissRefresh() {
                m_iCount++;

                assertEquals(2, m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 6 時的首頁狀態
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_6.json
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_3D.json
     *      - 模擬api回應設定：onSuccess
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為 B(空)，HomePage_Status.TYPE_B_HAVE_TICKET，且無法取得CPR資料
     */
    public void testCase02() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_6.json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_3D.json");
        CIInquiryCheckInModel.setIsOnSuccess(true);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {

                m_iCount++;

                assertEquals(1, m_iCount);
                //檢查CPR資料是否為空
                assertCPRDataEmpty(HomeStatusEntity);
                //此時才會更新狀態，所以才會符合模擬資料
                assertEquals(HomePage_Status.TYPE_B_HAVE_TICKET, HomeStatusEntity.iStatus_Code);


            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                fail();
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onDismissRefresh() {

                m_iCount++;

                assertEquals(2, m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 5 時的首頁狀態
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_5.json
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_3D.json
     *      - 模擬api回應設定：onSuccess
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為 C(空)Seat，HomePage_Status.TYPE_C_SELECT_SEAT_180D_14D，且無法取得CPR資料
     */
    public void testCase03() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_5.json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_3D.json");
        CIInquiryCheckInModel.setIsOnSuccess(true);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {
                m_iCount++;

                assertEquals(1, m_iCount);
                //檢查CPR資料是否為空
                assertCPRDataEmpty(HomeStatusEntity);

                assertEquals(HomePage_Status.TYPE_C_SELECT_SEAT_180D_14D, HomeStatusEntity.iStatus_Code);

            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                fail();
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onDismissRefresh() {
                m_iCount++;

                assertEquals(2, m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 4 時的首頁狀態
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_4.json
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_3D.json
     *      - 模擬api回應設定：onSuccess
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為 C(空)Seat + Meal，HomePage_Status.TYPE_C_SEAT_MEAL_14D_24H，且無法取得CPR資料
     */
    public void testCase04() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_4.json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_3D.json");
        CIInquiryCheckInModel.setIsOnSuccess(true);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {

                m_iCount++;

                assertEquals(1, m_iCount);

                assertEquals(HomePage_Status.TYPE_C_SEAT_MEAL_14D_24H, HomeStatusEntity.iStatus_Code);
                //檢查CPR資料是否為空
                assertCPRDataEmpty(HomeStatusEntity);

            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                fail();
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onDismissRefresh() {
                m_iCount++;

                assertEquals(2, m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 3時，起飛機場是無法online Check In 時的首頁狀態
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_3(NoOnlineCheckIn).json
     *        a. 起飛機場為TSA台北(松山)，此機場無法 Online Check In
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_3D.json
     *      - 模擬api回應設定：onSuccess
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為 E(空)，HomePage_Status.TYPE_E_DESK_CHECKIN，且無法取得CPR資料
     */
    public void testCase05_01() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_3(NoOnlineCheckIn).json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_3D.json");
        CIInquiryCheckInModel.setIsOnSuccess(true);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {
                m_iCount++;

                assertEquals(1, m_iCount);
                //檢查CPR資料是否為空
                assertCPRDataEmpty(HomeStatusEntity);

                assertEquals(HomePage_Status.TYPE_E_DESK_CHECKIN, HomeStatusEntity.iStatus_Code);
                assertTrue(HomeStatusEntity.bIsNewSiteOnlineCheckIn);

            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                fail();
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onDismissRefresh() {
                m_iCount++;

                assertEquals(2, m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 3時，起飛機場可以online Check In，
     *  查詢CPR ws回應錯誤的首頁狀態
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_3.json
     *        a. 起飛機場為TPE台北(桃園)，此機場可以Online Check In
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_3(error).json
     *      - 模擬api回應設定：onError
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為 E(空)，HomePage_Status.TYPE_E_DESK_CHECKIN，且無法取得CPR資料
     */
    public void testCase05_02() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_3.json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_3(error).json");
        CIInquiryCheckInModel.setIsOnSuccess(false);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {
                m_iCount++;

                //首刷以及取得PNR資料時會再刷一次
                assertEquals(1, m_iCount);
                //檢查CPR資料是否為空

                assertEquals(HomePage_Status.TYPE_E_DESK_CHECKIN, HomeStatusEntity.iStatus_Code);
                assertTrue(HomeStatusEntity.bIsNewSiteOnlineCheckIn);
            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                fail();
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {fail();}

            @Override
            public void onDismissRefresh() {
                m_iCount++;

                assertEquals(2, m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 3時，起飛機場可以online Check In，
     *  查詢CPR ws回應錯誤(-998)查無資料的首頁狀態
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_3.json
     *        a. 起飛機場為TPE台北(桃園)，此機場可以Online Check In
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_3Dw.json
     *      - 模擬api回應設定：onError
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為 Dw，HomePage_Status.TYPE_E_DESK_CHECKIN，且無法取得CPR資料
     */
    public void testCase05_03() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_3.json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_3Dw.json");
        CIInquiryCheckInModel.setIsOnSuccess(false);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {

                m_iCount++;

                assertEquals(1, m_iCount);
                //檢查CPR資料是否為空
                assertCPRDataEmpty(HomeStatusEntity);

                assertEquals(HomePage_Status.TYPE_D_ONLINE_CHECKIN, HomeStatusEntity.iStatus_Code);
                assertFalse(HomeStatusEntity.bIsNewSiteOnlineCheckIn);

            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                fail();
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onDismissRefresh() {

                m_iCount++;

                assertEquals(2, m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 3時，起飛機場可以online Check In，
     *  查詢CPR ws回應成功的首頁狀態
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_3.json
     *        a. 起飛機場為TPE台北(桃園)，此機場可以Online Check In
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_3Df.json
     *      - 模擬api回應設定：onSuccess
     *      - "Is_Black":"N",       不在黑名單中
     *      - "Is_Check_In":"Y",    已經完成報到
     *      - "Is_Do_Check_In":"Y"  可以報到了
     *      - "Boarding_Pass":"..." 有資料
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為 Df，HomePage_Status.TYPE_D_ONLINE_CHECKIN_FINISH，且可以取得CPR資料
     */
    public void testCase05_04() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_3.json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_3Df.json");
        CIInquiryCheckInModel.setIsOnSuccess(true);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {

                m_iCount++;

                assertTrue(1 == m_iCount);

                assertEquals(HomePage_Status.TYPE_D_ONLINE_CHECKIN_FINISH, HomeStatusEntity.iStatus_Code);
                assertTrue(HomeStatusEntity.bIsNewSiteOnlineCheckIn);
                //驗證是否取得CPR資料
                assertEquals("17:20", HomeStatusEntity.CPR_Info.Boarding_Time);
                assertEquals("2016-05-21", HomeStatusEntity.CPR_Info.Boarding_Date);
                assertEquals("G1", HomeStatusEntity.CPR_Info.Boarding_Gate);
                assertEquals("024D", HomeStatusEntity.CPR_Info.Seat_Number);
                assertEquals("3", HomeStatusEntity.CPR_Info.Check_In_Counter);
                assertTrue(!TextUtils.isEmpty(HomeStatusEntity.CPR_Info.Boarding_Pass));

            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                fail();
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onDismissRefresh() {

                m_iCount++;

                assertTrue(2 == m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 3時，起飛機場可以online Check In，
     *  查詢CPR ws回應成功的首頁狀態
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_3.json
     *        a. 起飛機場為TPE台北(桃園)，此機場可以Online Check In
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_3Eg.json
     *      - 模擬api回應設定：onSuccess
     *      - "Is_Black":"N",       不在黑名單中
     *      - "Is_Check_In":"Y",    已經完成報到
     *      - "Is_Do_Check_In":"Y"  可以報到了
     *      - "Boarding_Pass":""    無資料
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為 Eg，HomePage_Status.TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5，且可以取得CPR資料
     */
    public void testCase05_05() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_3.json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_3Eg.json");
        CIInquiryCheckInModel.setIsOnSuccess(true);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {

                m_iCount++;

                assertTrue(1 == m_iCount);

                assertEquals(HomePage_Status.TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5, HomeStatusEntity.iStatus_Code);

                //驗證是否取得CPR資料
                assertEquals("17:20", HomeStatusEntity.CPR_Info.Boarding_Time);
                assertEquals("2016-05-21", HomeStatusEntity.CPR_Info.Boarding_Date);
                assertEquals("G1", HomeStatusEntity.CPR_Info.Boarding_Gate);
                assertEquals("024D", HomeStatusEntity.CPR_Info.Seat_Number);
                assertEquals("3", HomeStatusEntity.CPR_Info.Check_In_Counter);
                assertTrue(TextUtils.isEmpty(HomeStatusEntity.CPR_Info.Boarding_Pass));

            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                fail();
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onDismissRefresh() {

                m_iCount++;

                assertTrue(2 == m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 3時，起飛機場可以online Check In，
     *  查詢CPR ws回應成功的首頁狀態
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_3.json
     *        a. 起飛機場為TPE台北(桃園)，此機場可以Online Check In
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_3D.json
     *      - 模擬api回應設定：onSuccess
     *      - "Is_Black":"N",       不在黑名單中
     *      - "Is_Check_In":"N",    尚未完成報到
     *      - "Is_Do_Check_In":"Y"  可以報到了
     *      - "Boarding_Pass":""    無資料
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為 D，HomePage_Status.TYPE_D_ONLINE_CHECKIN，且可以取得CPR資料
     */
    public void testCase05_06() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_3.json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_3D.json");
        CIInquiryCheckInModel.setIsOnSuccess(true);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {

                m_iCount++;

                assertTrue(1 == m_iCount);

                assertEquals(HomePage_Status.TYPE_D_ONLINE_CHECKIN, HomeStatusEntity.iStatus_Code);
                //驗證是否取得CPR資料
                assertEquals("17:20", HomeStatusEntity.CPR_Info.Boarding_Time);
                assertEquals("2016-05-21", HomeStatusEntity.CPR_Info.Boarding_Date);
                assertEquals("G1", HomeStatusEntity.CPR_Info.Boarding_Gate);
                assertEquals("024D", HomeStatusEntity.CPR_Info.Seat_Number);
                assertEquals("3", HomeStatusEntity.CPR_Info.Check_In_Counter);
                assertTrue(TextUtils.isEmpty(HomeStatusEntity.CPR_Info.Boarding_Pass));

            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                fail();
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onDismissRefresh() {

                m_iCount++;

                assertTrue(2 == m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 3時，起飛機場可以online Check In，
     *  查詢CPR ws回應成功的首頁狀態
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_3.json
     *        a. 起飛機場為TPE台北(桃園)，此機場可以Online Check In
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_3E.json
     *      - 模擬api回應設定：onSuccess
     *      - "Is_Black":"Y",       在黑名單中
     *      - "Is_Check_In":"N",    尚未完成報到
     *      - "Is_Do_Check_In":"Y"  可以報到了
     *      - "Boarding_Pass":""    無資料
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為 E，HomePage_Status.TYPE_E_DESK_CHECKIN，且可以取得CPR資料
     */
    public void testCase05_07() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_3.json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_3E.json");
        CIInquiryCheckInModel.setIsOnSuccess(true);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {

                m_iCount++;

                assertTrue(1 == m_iCount);

                assertEquals(HomePage_Status.TYPE_E_DESK_CHECKIN, HomeStatusEntity.iStatus_Code);
                //驗證是否取得CPR資料
                assertEquals("17:20", HomeStatusEntity.CPR_Info.Boarding_Time);
                assertEquals("2016-05-21", HomeStatusEntity.CPR_Info.Boarding_Date);
                assertEquals("G1", HomeStatusEntity.CPR_Info.Boarding_Gate);
                assertEquals("024D", HomeStatusEntity.CPR_Info.Seat_Number);
                assertEquals("3", HomeStatusEntity.CPR_Info.Check_In_Counter);
                assertTrue(TextUtils.isEmpty(HomeStatusEntity.CPR_Info.Boarding_Pass));


            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                fail();
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onDismissRefresh() {

                m_iCount++;

                assertTrue(2 == m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 2時，機場無提供Online Check-In的首頁狀態
     *
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_2(NoOnlineCheckIn).json
     *        a. 起飛機場為TSA台北(松山)，此機場無法 Online Check In
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_2.json
     *      - 模擬api回應設定：onSuccess
     *      - "Is_Black":"N",       不在黑名單中
     *      - "Is_Check_In":"Y",    已經完成報到
     *      - "Is_Do_Check_In":"Y"  可以報到了
     *      - "Boarding_Pass":"..." 有資料
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為 Eg(空)，HomePage_Status.TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5，且無CPR資料
     */
    public void testCase06_01() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_2(NoOnlineCheckIn).json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_2.json");
        CIInquiryCheckInModel.setIsOnSuccess(true);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {

                m_iCount++;

                //首刷以及取得PNR資料時會再刷一次
                assertTrue(1 == m_iCount);
                //檢查CPR資料是否為空
                assertCPRDataEmpty(HomeStatusEntity);

                assertEquals(HomePage_Status.TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5, HomeStatusEntity.iStatus_Code);
            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                fail();
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onDismissRefresh() {

                m_iCount++;

                assertTrue(2 == m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 2時，起飛機場可以online Check In，
     *  查詢CPR ws回應失敗的首頁狀態
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_2.json
     *        a. 起飛機場為TSA台北(松山)，此機場無法 Online Check In
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_2(error).json
     *      - 模擬api回應設定：onError
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為 Eg(空)，HomePage_Status.TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5，且無CPR資料
     */
    public void testCase06_02() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_2.json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_2(error).json");
        CIInquiryCheckInModel.setIsOnSuccess(false);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {

                m_iCount++;

                assertTrue(1 == m_iCount);
                //檢查CPR資料是否為空
                assertCPRDataEmpty(HomeStatusEntity);

                assertEquals(HomePage_Status.TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5, HomeStatusEntity.iStatus_Code);

            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                fail();
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {fail();}

            @Override
            public void onDismissRefresh() {

                m_iCount++;

                assertTrue(2 == m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 2時，起飛機場可以online Check In，
     *  查詢CPR ws回應成功的首頁狀態
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_2.json
     *        a. 起飛機場為TPE台北(桃園)，此機場可以 Online Check In
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_2Eg.json
     *      - 模擬api回應設定：onSuccess
     *      - "Is_Black":"N",       不在黑名單中
     *      - "Is_Check_In":"Y",    已經完成報到
     *      - "Is_Do_Check_In":"Y"  可以報到了
     *      - "Boarding_Pass":""    無資料
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為 Eg，HomePage_Status.TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5，且可以取得CPR資料
     */
    public void testCase06_03() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_2.json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_2Eg.json");
        CIInquiryCheckInModel.setIsOnSuccess(true);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {

                m_iCount++;

                assertTrue(1 == m_iCount);

                assertEquals(HomePage_Status.TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5, HomeStatusEntity.iStatus_Code);
                //驗證是否取得CPR資料
                assertEquals("17:20", HomeStatusEntity.CPR_Info.Boarding_Time);
                assertEquals("2016-05-21", HomeStatusEntity.CPR_Info.Boarding_Date);
                assertEquals("G1", HomeStatusEntity.CPR_Info.Boarding_Gate);
                assertEquals("024D", HomeStatusEntity.CPR_Info.Seat_Number);
                assertEquals("3", HomeStatusEntity.CPR_Info.Check_In_Counter);
                assertTrue(TextUtils.isEmpty(HomeStatusEntity.CPR_Info.Boarding_Pass));

            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                fail();
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onDismissRefresh() {

                m_iCount++;

                assertTrue(2 == m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 2時，起飛機場可以online Check In，
     *  查詢CPR ws回應成功的首頁狀態
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_2.json
     *        a. 起飛機場為TPE台北(桃園)，此機場可以 Online Check In
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_2Dg.json
     *      - 模擬api回應設定：onSuccess
     *      - "Is_Black":"N",       不在黑名單中
     *      - "Is_Check_In":"Y",    已經完成報到
     *      - "Is_Do_Check_In":"Y"  可以報到了
     *      - "Boarding_Pass":"..." 有資料
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為 Dg，HomePage_Status.TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5，且可以取得CPR資料
     */
    public void testCase06_04() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_2.json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_2Dg.json");
        CIInquiryCheckInModel.setIsOnSuccess(true);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {

                m_iCount++;

                assertTrue(1 == m_iCount);

                assertEquals(HomePage_Status.TYPE_D_ONLINE_CHECKIN_FINISH_AT_AIRPORT_1H5, HomeStatusEntity.iStatus_Code);
                //驗證是否取得CPR資料
                assertEquals("17:20", HomeStatusEntity.CPR_Info.Boarding_Time);
                assertEquals("2016-05-21", HomeStatusEntity.CPR_Info.Boarding_Date);
                assertEquals("G1", HomeStatusEntity.CPR_Info.Boarding_Gate);
                assertEquals("024D", HomeStatusEntity.CPR_Info.Seat_Number);
                assertEquals("3", HomeStatusEntity.CPR_Info.Check_In_Counter);
                assertTrue(!TextUtils.isEmpty(HomeStatusEntity.CPR_Info.Boarding_Pass));

            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                fail();
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onDismissRefresh() {

                m_iCount++;

                assertTrue(2 == m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 11時
     *  查詢CPR ws回應成功的首頁狀態
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_11.json
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_2Dg.json
     *      - 模擬api回應設定：onSuccess
     *      - "Is_Black":"N",       不在黑名單中
     *      - "Is_Check_In":"Y",    已經完成報到
     *      - "Is_Do_Check_In":"Y"  可以報到了
     *      - "Boarding_Pass":"..." 有資料
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為 F，HomePage_Status.TYPE_F_IN_FLIGHT，且可以取得CPR資料
     */
    public void testCase07_01() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_11.json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_2Dg.json");
        CIInquiryCheckInModel.setIsOnSuccess(true);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {

                m_iCount++;

                assertTrue(1 == m_iCount);

                assertEquals(HomePage_Status.TYPE_F_IN_FLIGHT, HomeStatusEntity.iStatus_Code);
                //驗證是否取得CPR資料
                assertEquals("17:20", HomeStatusEntity.CPR_Info.Boarding_Time);
                assertEquals("2016-05-21", HomeStatusEntity.CPR_Info.Boarding_Date);
                assertEquals("G1", HomeStatusEntity.CPR_Info.Boarding_Gate);
                assertEquals("024D", HomeStatusEntity.CPR_Info.Seat_Number);
                assertEquals("3", HomeStatusEntity.CPR_Info.Check_In_Counter);
                assertTrue(!TextUtils.isEmpty(HomeStatusEntity.CPR_Info.Boarding_Pass));

            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                fail();
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onDismissRefresh() {
                m_iCount++;

                assertTrue(2 == m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 11時
     *  查詢CPR ws回應失敗的首頁狀態
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_11.json
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_2(error).json
     *      - 模擬api回應設定：onError
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為 F(空)，HomePage_Status.TYPE_F_IN_FLIGHT，且無法取得CPR資料
     */
    public void testCase07_02() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_11.json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_2(error).json");
        CIInquiryCheckInModel.setIsOnSuccess(false);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {

                m_iCount++;

                assertTrue(1 == m_iCount);

                assertEquals(HomePage_Status.TYPE_F_IN_FLIGHT, HomeStatusEntity.iStatus_Code);
                //檢查CPR資料是否為空
                assertCPRDataEmpty(HomeStatusEntity);
            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                fail();
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {fail();}

            @Override
            public void onDismissRefresh() {
                m_iCount++;

                assertTrue(2 == m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 12時
     *  查詢CPR ws回應成功的首頁狀態
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_12.json
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_2Dg.json
     *      - 模擬api回應設定：onSuccess
     *      - "Is_Black":"N",       不在黑名單中
     *      - "Is_Check_In":"Y",    已經完成報到
     *      - "Is_Do_Check_In":"Y"  可以報到了
     *      - "Boarding_Pass":"..." 有資料
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為 G，HomePage_Status.TYPE_G_TRANSITION，且可以取得CPR資料
     */
    public void testCase08_01() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_12.json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_2Dg.json");
        CIInquiryCheckInModel.setIsOnSuccess(true);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {

                m_iCount++;

                assertTrue(1 == m_iCount);

                assertEquals(HomePage_Status.TYPE_G_TRANSITION, HomeStatusEntity.iStatus_Code);
                //驗證是否取得CPR資料
                assertEquals("17:20", HomeStatusEntity.CPR_Info.Boarding_Time);
                assertEquals("2016-05-21", HomeStatusEntity.CPR_Info.Boarding_Date);
                assertEquals("G1", HomeStatusEntity.CPR_Info.Boarding_Gate);
                assertEquals("024D", HomeStatusEntity.CPR_Info.Seat_Number);
                assertEquals("3", HomeStatusEntity.CPR_Info.Check_In_Counter);
                assertTrue(!TextUtils.isEmpty(HomeStatusEntity.CPR_Info.Boarding_Pass));

            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                fail();
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onDismissRefresh() {

                m_iCount++;

                assertTrue(2 == m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 12時
     *  查詢CPR ws回應失敗的首頁狀態
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_12.json
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_2(error).json
     *      - 模擬api回應設定：onError
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為 G(空)，HomePage_Status.TYPE_G_TRANSITION，且無法取得CPR資料
     */
    public void testCase08_02() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_12.json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_2(error).json");
        CIInquiryCheckInModel.setIsOnSuccess(false);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {

                m_iCount++;

                assertTrue(1 == m_iCount);

                assertEquals(HomePage_Status.TYPE_G_TRANSITION, HomeStatusEntity.iStatus_Code);
                //檢查CPR資料是否為空
                assertCPRDataEmpty(HomeStatusEntity);
            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                fail();
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {fail();}

            @Override
            public void onDismissRefresh() {

                m_iCount++;

                assertTrue(2 == m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 0時
     *  查詢CPR ws回應成功的首頁狀態
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_0.json
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_2Dg.json
     *      - 模擬api回應設定：onSuccess
     *      - "Is_Black":"N",       不在黑名單中
     *      - "Is_Check_In":"Y",    已經完成報到
     *      - "Is_Do_Check_In":"Y"  可以報到了
     *      - "Boarding_Pass":"..." 有資料
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為 H，HomePage_Status.TYPE_H_ARRIVAL，且可以取得CPR資料
     */
    public void testCase09_01() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_0.json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_2Dg.json");
        CIInquiryCheckInModel.setIsOnSuccess(true);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {

                m_iCount++;

                assertTrue(1 == m_iCount);

                assertEquals(HomePage_Status.TYPE_H_ARRIVAL, HomeStatusEntity.iStatus_Code);
                //驗證是否取得CPR資料
                assertEquals("17:20", HomeStatusEntity.CPR_Info.Boarding_Time);
                assertEquals("2016-05-21", HomeStatusEntity.CPR_Info.Boarding_Date);
                assertEquals("G1", HomeStatusEntity.CPR_Info.Boarding_Gate);
                assertEquals("024D", HomeStatusEntity.CPR_Info.Seat_Number);
                assertEquals("3", HomeStatusEntity.CPR_Info.Check_In_Counter);
                assertTrue(!TextUtils.isEmpty(HomeStatusEntity.CPR_Info.Boarding_Pass));

            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                fail();
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {
                fail();
            }

            @Override
            public void onDismissRefresh() {

                m_iCount++;

                assertTrue(2 == m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    /**
     *  案例說明：DEV環境，無網路狀態，驗證PNR資料Status_Code = 0時
     *  查詢CPR ws回應失敗的首頁狀態
     *  輸入參數：
     *  (1) CIInquiryTripModel
     *      - 模擬資料:dev/assets/json/HomeStatus/MyTripByCardNo_0.json
     *      - 模擬api回應設定：onSuccess
     *  (2) CIInquiryCheckInModel
     *      - 模擬資料: dev/assets/json/HomeStatus/InquiryCheckInByCard_2(error).json
     *      - 模擬api回應設定：onError
     *  (3) CILoginInfo
     *      - 登入狀態：false
     *  斷言結果：首頁狀態應為 H(空)，HomePage_Status.TYPE_H_ARRIVAL，且無法取得CPR資料
     */
    public void testCase09_02() throws Exception{

        CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_0.json");
        CIInquiryTripModel.setIsOnSuccess(true);
        CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_2(error).json");
        CIInquiryCheckInModel.setIsOnSuccess(false);

        CIPNRStatusManager.getInstance(new CIPNRStatusManager.CIHomeStatusListener() {
            @Override
            public void onRefreshPNRSuccess(CIHomeStatusEntity HomeStatusEntity) {

                m_iCount++;

                assertTrue(1 == m_iCount);

                assertEquals(HomePage_Status.TYPE_H_ARRIVAL, HomeStatusEntity.iStatus_Code);
                //檢查CPR資料是否為空
                assertCPRDataEmpty(HomeStatusEntity);

            }

            @Override
            public void onNonUpdatePNRInfo() {
                fail();
            }

            @Override
            public void onHomePageNoTicket() {
                fail();
            }

            @Override
            public void onInquiryError(String rt_code, String rt_msg) {fail();}

            @Override
            public void onDismissRefresh() {

                m_iCount++;

                assertTrue(2 == m_iCount);

                countDown();
            }

        }).RefreshHomePageStatus();
        await(10, TimeUnit.SECONDS);
        assertEquals(2, m_iCount);
    }

    private void assertCPRDataEmpty(CIHomeStatusEntity HomeStatusEntity){
        assertEquals(null , HomeStatusEntity.CPR_Info);
    }

    /**
     * 因應環境需求，預先清空並建立一筆資料到資料庫
     */
    private void insertDataToDB(){
        CIPNRStatusModel model = new CIPNRStatusModel();
        CIInquiryTripEntity data = new CIInquiryTripEntity();
        data.PNR = "A12345";
        data.Status_Code = 6;
        data.isVisibleAtHome = true;
        CITripListResp resp = new CITripListResp();
        resp.PNR_Id = "A12345";
        resp.Status_Code = 6;
        resp.Itinerary_Info = new LinkedList<>();

        CITripListResp_Itinerary info = new CITripListResp_Itinerary();

        SimpleDateFormat sdf_time = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
        SimpleDateFormat sdf_date = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
        sdf_time.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        sdf_date.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        sdf_time.applyPattern("HH:mm");
        sdf_date.applyPattern("yyyy-MM-dd");
        info.Pnr_Id = "A12345";
        info.Arrival_Time_Gmt = sdf_time.format(new Date(System.currentTimeMillis()));
        info.Arrival_Date_Gmt = sdf_date.format(new Date(System.currentTimeMillis()));;
        resp.Itinerary_Info.add(info);
        data.respResult = GsonTool.toJson(resp);
        model.Clear();
        model.insertOrUpdate(data);
    }

    /**
     * 清除DB中的測試資料
     */
    private void clearDBData(){
        CIPNRStatusModel model = new CIPNRStatusModel();
        model.Clear();
    }

    private void getTheAirportData(){
        CIInquiryFlightStationPresenter presenter = CIInquiryFlightStationPresenter.getInstance(new CIInquiryFlightStatusStationListener() {
            @Override
            public void onAllStationSuccess(String rt_code, String rt_msg, CIInquiryFlightStationPresenter presenter) {
                if(null != presenter.getAllDepatureStationList()){

                } else {
                    fail();
                }
            }
            @Override
            public void onStationError(String rt_code, String rt_msg) {
                fail();
            }
            @Override
            public void onODStationSuccess(String rt_code, String rt_msg, CIInquiryFlightStationPresenter presenter) {}
            @Override
            public void showProgress() {}
            @Override
            public void hideProgress() {}
        }, CIInquiryFlightStationPresenter.ESource.TimeTable);

        if(null == presenter.getAllDepatureStationList()){
            presenter.initAllStationDB();
            presenter.InquiryAllStationListFromWS();
        }
    }
}
