package ci.ui.object;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import ci.function.Core.CIApplication;
import ci.ui.define.HomePage_Status;
import ci.ui.define.UiMessageDef;
import ci.ui.object.item.CIHomeStatusEntity;
import ci.ws.Models.CIInquiryTripModel;
import ci.ws.Models.entities.CICheckInPax_ItineraryInfoEntity;
import ci.ws.Models.entities.CICheckInPax_InfoEntity;
import ci.ws.Models.entities.CICheckInAllPaxResp;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Models.entities.CIInquiryTripEntity;
import ci.ws.Models.entities.CITripListResp;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Models.entities.CITripbyCardReq;
import ci.ws.Models.entities.CITripbyPNRReq;
import ci.ws.Presenter.CIInquiryCheckInPresenter;
import ci.ws.Presenter.CIInquiryFlightStationPresenter;
import ci.ws.Presenter.Listener.CIInquiryCheckInListener;
import ci.ws.Presenter.Listener.CIInquiryFlightStatusStationListener;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.CIWSHomeStatus_Code;
import ci.ws.define.CIWSResultCode;

/**
 * Created by Ryan on 16/5/20.
 */
public class CIPNRStatusManager {

    public interface CIHomeStatusListener {

        /**更新PNR資料成功, 通知畫面*/
        void onRefreshPNRSuccess( CIHomeStatusEntity HomeStatusEntity );

        /**通知畫面, 不需更新畫面PNR, 保留現有的狀態*/
        void onNonUpdatePNRInfo();

        /**通知畫面, NoTicket*/
        void onHomePageNoTicket();

        /**查詢失敗*/
        void onInquiryError(String rt_code, String rt_msg);

        /**通知畫面停止更新*/
        void onDismissRefresh();
    }

    private static CIPNRStatusManager   s_Instance = null;

    private static Handler              s_hdUIThreadhandler = null;

    private CIHomeStatusListener        m_Listener          = null;

    private CIInquiryTripModel          m_InquiryTrip       = null;
    /**從DB要到的 PNR 資料*/
    private CIHomeStatusEntity          m_HomeStatusEntityFromDB = null;
    /**從WS要到的 PNR 資料 含 CPR 資料*/
    private CIHomeStatusEntity          m_HomeStatusEntityFromWS = null;

    private CIPNRStatusModel            m_PNRstatusModel    = null;

    CIInquiryCheckInPresenter           m_checkInPresenter  = null;

    CIInquiryFlightStationPresenter     m_inquiryFlightStationPresenter = null;

    //出發場站的詳細資訊
    private CIFlightStationEntity       m_FlightStationInfo = null;

    //用於從Trip叮選行程後,回首頁要更新首頁
    private boolean m_bIsHomePageTripChange = false;

    public static CIPNRStatusManager getInstance( CIHomeStatusListener Listener ){

        if ( null == s_Instance){
            s_Instance = new CIPNRStatusManager();
        }

        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        s_Instance.setCallBack(Listener);
        return s_Instance;
    }

    //新增不異動設定Listener，僅取物件的方法 by kevin 2016/12
    public static CIPNRStatusManager getInstanceWithoutSetListener(){
        if(null == s_Instance){
            return getInstance(null);
        }
        return s_Instance;
    }

    private void setCallBack( CIHomeStatusListener Listener ){
        m_Listener = Listener;
    }

    private CIInquiryTripModel GetInquiryTrip(){
        if ( null == m_InquiryTrip ){
            m_InquiryTrip = new CIInquiryTripModel(m_InquiryCallBack);
        }
        m_InquiryTrip.setCallback(m_InquiryCallBack);
        return m_InquiryTrip;
    }

    public CIPNRStatusModel getPNRstatusModel(){
        if ( null == m_PNRstatusModel ){
            m_PNRstatusModel = new CIPNRStatusModel();
        }
        return m_PNRstatusModel;
    }


    /**
     * 新增DB資料，若PNR有重複則更新，會針對目前登入狀態及帳號分別處理
     * 2016-05-27 modifly by Ryan for 將轉換資料的邏輯移至Function
     */
    public void insertOrUpdatePNRDataToDB(CITripListResp Tripslist, Boolean bIsVisibleAtHome ){

        if( null == Tripslist ) {
            return;
        }

        CIInquiryTripEntity data = new CIInquiryTripEntity();

        String jsonResp = GsonTool.toJson(Tripslist);

        data.PNR                = Tripslist.PNR_Id;
        data.firstname          = Tripslist.First_Name;
        data.lastname           = Tripslist.Last_Name;
        data.respResult         = jsonResp;
        data.isVisibleAtHome    = bIsVisibleAtHome;
        data.Status_Code        = Tripslist.Status_Code;
        data.Itinerary_Num      = Tripslist.Itinerary_Num;
        data.Is_Select_Meal     = Tripslist.Is_Select_Meal;
        data.Segment_Num        = Tripslist.Segment_Num;

        getPNRstatusModel().insertOrUpdate(data);
    }

    /**
     * 從DB取得全部行程資料，此資料會針對目前登入狀態及帳號有所區分
     * @return
     */
    public List<CIInquiryTripEntity> getAllTripData(){

        return getPNRstatusModel().findData();
    }

    /**
     * 從DB取得全部行程資料，此資料會針對目前登入狀態及帳號有所區分
     * @return 解開過的PNR資料
     */
    public ArrayList<CITripListResp> getAllPNRListData(){

        ArrayList<CITripListResp> arPNRList = new ArrayList<CITripListResp>();

        List<CIInquiryTripEntity> arList = getAllTripData();
        if ( null != arList ){
            for ( CIInquiryTripEntity entity : arList ){
                CITripListResp tripResp = null;
                try {
                    tripResp = GsonTool.toObject( entity.respResult, CITripListResp.class );
                } catch ( Exception e) {
                    e.printStackTrace();
                }

                if ( null != tripResp ){
                    arPNRList.add(tripResp);
                }
            }
        }

        return arPNRList;
    }

    /**
     * 從DB取得僅可顯示在首頁的全部行程資料，此資料會針對目前登入狀態及帳號有所區分存取
     */
    public List<CIInquiryTripEntity> getAllTripDataForOnlyVisibleInHome(){

        return getPNRstatusModel().findDataForOnlyVisibleAtHome();
    }

    /**更新某筆PNR_Id的是否於主頁顯示的狀態*/
    public void UpdatePNRisVisibleAtHome( String strPNR_Id, Boolean bVisible ){
        getPNRstatusModel().UpdatePNRisVisibleAtHome(strPNR_Id, bVisible);
    }

    /**利用PNR_Id取得有儲存的PNR資料*/
    public CITripListResp FindTripListByPNRId( String strPNRId ){

        CIInquiryTripEntity tripEntity = getPNRstatusModel().findTripDataByPNR(strPNRId);

        CITripListResp tripResp = null;

        if ( null != tripEntity ){
            try {
                tripResp = GsonTool.toObject( tripEntity.respResult, CITripListResp.class );
            } catch ( Exception e) {
                e.printStackTrace();
            }
        }

        return tripResp;
    }

    /**取得已經丁選在首頁的PNR資料*/
    public CITripListResp getHomePagePNRTrip(){

        List<CIInquiryTripEntity> arTripList = getPNRstatusModel().findDataForOnlyVisibleAtHome();

        CITripListResp tripResp = null;

        if ( null != arTripList ){
            for ( CIInquiryTripEntity entity : arTripList ){
                try {
                    tripResp = GsonTool.toObject( entity.respResult, CITripListResp.class );
                } catch ( Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return tripResp;
    }

    /**根據Pnr Id 來 刪除Trip*/
    public int deleteTripDataByPNR( ArrayList<String> arPNRIdList ){
        return getPNRstatusModel().deleteTripDataByPNR(arPNRIdList);
    }

    //========================

    /**搭配首頁更新機制, 丟入航段資訊來更新,
     * 未登入情況則不會觸發更新,
     * 登入中如果沒帶入資料, 則會透過會員卡號去取一個最接近的PNR並取出航段
     * @param strPNR_Id 訂位代號*/
    public void RefreshHomePageItinerary( String strPNR_Id, String strFirst_Name, String strLast_Name ){
        GetInquiryTrip().CancelRequest();

        if ( null == strPNR_Id || strPNR_Id.length() <= 0 ){
            CITripbyCardReq req = new CITripbyCardReq();
            req.Card_Id = CIApplication.getLoginInfo().GetUserMemberCardNo();
            //2018-06-29 第二階段CR
            // 將DB的PNR資料傳上去Server，由Server檢核PNR的有效性，再回傳給APP
            // 新增FirstName LastName 避免PNRId重複使用導致看到別人的資料
            req.First_Name  = CIApplication.getLoginInfo().GetUserFirstName();
            req.Last_Name   = CIApplication.getLoginInfo().GetUserLastName();
            //List<CIInquiryTripEntity> lDBTrip = CIPNRStatusManager.getInstance(null).getAllTripData();
            List<CIInquiryTripEntity> lDBTrip = getAllTripData();
            Set<String> Pnr_List = new LinkedHashSet<>();
            if ( null != lDBTrip ){
                for ( int i = 0 ; i < lDBTrip.size() ; i ++ ){
                    if ( 0 < lDBTrip.get(i).PNR.length() )
                        Pnr_List.add(lDBTrip.get(i).PNR);
                }
            }
            req.Pnr_List    = Pnr_List;
            //
            GetInquiryTrip().InquiryTripByCardNo(req);
        } else {
            //m_HomeStatusEntityFromDB.ItineraryInfoList.add(pnrinfo);
            CITripbyPNRReq PnrReq = new CITripbyPNRReq();
            PnrReq.Pnr_id       = strPNR_Id;
            PnrReq.First_Name   = strFirst_Name;
            PnrReq.Last_Name    = strLast_Name;
            GetInquiryTrip().InquiryTripByPNR(PnrReq);
        }
    }

    private void clearHomepageBoardingPassData(){
        CIModelInfo info = new CIModelInfo(CIApplication.getContext());
        info.setHomepageBoardingPassData("");
    }

    /**
     * 取消刷新所有請求
     */
    public void cancelRefresh(){
        GetInquiryTrip().CancelRequest();
        if(null != m_inquiryFlightStationPresenter){
            m_inquiryFlightStationPresenter.cancel();
        }
        if(null != m_checkInPresenter){
            m_checkInPresenter.InquiryCheckInCancel();
        }
    }

    public void RefreshHomePageStatus() {

        new Thread(new Runnable() {
            @Override
            public void run() {
//               synchronized (this){

                //刷新前先清除首頁Boarding Pass資料
                clearHomepageBoardingPassData();

                //一旦重刷就取消先前的請求
                cancelRefresh();

                //從DB取得首頁要顯示的PNR
                m_HomeStatusEntityFromDB = getVisibleInHomePNR_Info_FromDB();
                if (null == m_HomeStatusEntityFromDB.ItineraryInfoList || m_HomeStatusEntityFromDB.ItineraryInfoList.size() <= 0) {
                    //當PNR 為空的又是「正式」會員登入則改為使用會員卡號去跟Server要最接近的PNR
                    if (CIApplication.getLoginInfo().GetLoginStatus()
                            && CIApplication.getLoginInfo().GetLoginType()
                            .equals(UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER)) {
                        //跟ws要新的PNR狀態資料，待回覆後會再次更新首頁狀態
                        RefreshHomePageItinerary(null, "", "");
                    } else {
                        //沒登入又沒資料就返回顯示NoTicket
                        SendHomePageNoTicket();
                        m_HomeStatusEntityFromDB = null;
                    }
                } else {
                    //有資料的判斷邏輯
                    int iSize = m_HomeStatusEntityFromDB.ItineraryInfoList.size();
                    //檢查最後一班航班的時間是否已經超過24hr
                    if (CheckPNRTimeIsOver24hr(m_HomeStatusEntityFromDB.ItineraryInfoList.get(iSize - 1))) {
                        //將可以顯示在首頁資料庫pnr設定不能顯示在首頁
                        SetHomePagePNRVisibleFalse();
                        //當PNR已經超過24hr又是「正式」會員登入則改為使用會員卡號去跟Server要最接近的PNR
                        if (CIApplication.getLoginInfo().GetLoginStatus()
                                && CIApplication.getLoginInfo().GetLoginType()
                                .equals(UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER)) {
                            //跟ws要新的PNR狀態資料，待回覆後會再次更新首頁狀態
                            RefreshHomePageItinerary(null, "", "");
                        } else {
                            //若原來有PNR 但當次回補, PNR ID 有誤, 則把該筆行程移開首頁
                            SendHomePageNoTicket();
                        }
                    } else {
                        //當航班時間尚未走完, 則判斷航班資訊是否符合PNR Status
                        CITripListResp tripListResp = new CITripListResp();
                        tripListResp.PNR_Id = m_HomeStatusEntityFromDB.PNR_Id;
                        tripListResp.Itinerary_Num = m_HomeStatusEntityFromDB.Itinerary_Num;
                        tripListResp.Status_Code = m_HomeStatusEntityFromDB.iStatus_Code;
                        tripListResp.First_Name = m_HomeStatusEntityFromDB.strFirst_name;
                        tripListResp.Last_Name = m_HomeStatusEntityFromDB.strLast_name;
                        tripListResp.Is_Select_Meal = m_HomeStatusEntityFromDB.Is_Select_Meal;
                        tripListResp.Segment_Num = m_HomeStatusEntityFromDB.Segment_Num;
                        tripListResp.Itinerary_Info.addAll(m_HomeStatusEntityFromDB.ItineraryInfoList);
                        //過濾航段
                        if (IsTripItineraryMapping(m_HomeStatusEntityFromDB, tripListResp)) {

                            //轉換UI認得的Status Code
                            TransferPNRStatusToUI(m_HomeStatusEntityFromDB);
                            //調整刷新首頁狀態的邏輯 , 增加判斷如果當下已經有新的CPR資料則先維持原樣, 帶CPR 更新後再統一更新畫面
                            //2016-06-16 modifly by ryan
                            if (null == m_HomeStatusEntityFromWS ||
                                    null != m_HomeStatusEntityFromWS && m_HomeStatusEntityFromWS.bHaveCPRData == false) {
                                //先刷一次首頁狀態
                                SendRefreshPNRNonDismissRefresh(m_HomeStatusEntityFromDB);
                                //
                                //將原PNR資料送到推播主機
                                CIApplication.getFCMManager().UpdateDeviceToCIServer(tripListResp.Itinerary_Info);
                                //CIApplication.getMrqManager().SendPNRINfotoMRQ(CIMrqInfoManager.TAG_LAUNCHED, tripListResp.Itinerary_Info);
                                //
                            }
                            //跟ws要新的PNR狀態資料，待回覆後會再次更新首頁狀態
                            RefreshHomePageItinerary(m_HomeStatusEntityFromDB.PNR_Id,
                                    m_HomeStatusEntityFromDB.strFirst_name,
                                    m_HomeStatusEntityFromDB.strLast_name);
                        } else {
                            //將該PNR_Id 的主頁狀態回寫為N
                            SetHomePagePNRVisibleFalse();
                            SendHomePageNoTicket();
                        }
                    }
                }
//               }
            }
        }).start();
    }

    /**取得首頁所需要PNR資訊, 從DB讀取資料*/
    private CIHomeStatusEntity getVisibleInHomePNR_Info_FromDB(){

        CIHomeStatusEntity HomeStatusEntity = new CIHomeStatusEntity();

        List<CIInquiryTripEntity> list =  getAllTripDataForOnlyVisibleInHome();
        if ( null != list && list.size() > 0 ){

            CIInquiryTripEntity tripEntity = list.get(0);
            CITripListResp tripResp = null;
            try {
                tripResp = GsonTool.toObject( tripEntity.respResult, CITripListResp.class );
            } catch ( Exception e) {
                e.printStackTrace();
            }

            if ( null != tripResp && IsTripPNRmapping(tripResp) ){

                HomeStatusEntity.PNR_Id         = tripEntity.PNR;
                HomeStatusEntity.iStatus_Code   = tripEntity.Status_Code;
                HomeStatusEntity.strFirst_name  = tripEntity.firstname;
                HomeStatusEntity.strLast_name   = tripEntity.lastname;
                HomeStatusEntity.Itinerary_Num  = tripEntity.Itinerary_Num;
                HomeStatusEntity.Is_Select_Meal = tripEntity.Is_Select_Meal;
                HomeStatusEntity.Segment_Num    = tripEntity.Segment_Num;

                HomeStatusEntity.ItineraryInfoList = tripResp.Itinerary_Info;
            }
        }
        return HomeStatusEntity;
    }

    public Boolean TransferPNRStatusToUI( CIHomeStatusEntity homeStatusEntity ){

        Boolean bUpdateStatus = true;
        int iUI_StatusCode = HomePage_Status.TYPE_A_NO_TICKET;

        int iWS_StatusCode = homeStatusEntity.iStatus_Code;

        switch (iWS_StatusCode){
            case CIWSHomeStatus_Code.TYPE_B_HAVE_TICKET:
                iUI_StatusCode = HomePage_Status.TYPE_B_HAVE_TICKET;
                break;
            case CIWSHomeStatus_Code.TYPE_C_SEAT_MEAL_14D_24H:
                iUI_StatusCode = HomePage_Status.TYPE_C_SEAT_MEAL_14D_24H;
                break;
            case CIWSHomeStatus_Code.TYPE_C_SEAT_180D_14D:
                iUI_StatusCode = HomePage_Status.TYPE_C_SELECT_SEAT_180D_14D;
                break;
            case CIWSHomeStatus_Code.TYPE_D_E_CHECKIN:
                iUI_StatusCode = HomePage_Status.TYPE_E_DESK_CHECKIN;
                //還需額外透過CPR 確認狀態
                bUpdateStatus = false;
                break;
            case CIWSHomeStatus_Code.TYPE_D_E_GATE_INFO:
                iUI_StatusCode = HomePage_Status.TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5;
                //還需額外透過CPR 確認狀態
                bUpdateStatus = false;
                break;
            case CIWSHomeStatus_Code.TYPE_F_IN_FLIGHT:
                iUI_StatusCode = HomePage_Status.TYPE_F_IN_FLIGHT;
                //還需額外透過CPR 確認狀態
                bUpdateStatus = false;
                break;
            case CIWSHomeStatus_Code.TYPE_G_TRANSITION:
                iUI_StatusCode = HomePage_Status.TYPE_G_TRANSITION;
                //還需額外透過CPR 確認狀態
                bUpdateStatus = false;
                break;
            case CIWSHomeStatus_Code.TYPE_H_ARRIVAL:
                iUI_StatusCode = HomePage_Status.TYPE_H_ARRIVAL;
                //還需額外透過CPR 確認狀態
                bUpdateStatus = false;
                break;
            default:
                break;
        }

        homeStatusEntity.iStatus_Code = iUI_StatusCode;
        if ( bUpdateStatus ){
            //AppInfo.getInstance(CIApplication.getContext()).SetHoemPageStatus(iUI_StatusCode);
            return true;
        }
        return false;
    }

    /**確認航段抵達時間是否已經過期24hr*/
    public Boolean CheckPNRTimeIsOver24hr( CITripListResp_Itinerary info ){

        Boolean bIsOver = false;
        //判斷GMT 是否已經超過24hr, 超過則調整為其他狀態
        if ( null != info ){
            //2016-11-17 Modify by Ryan for PNR時間改抓取實際時間, DISPLAY
            String strDMT = info.getDisplayArrivalDate_GMT() + " " + info.getDisplayArrivalTime_GMT();
            //String strDMT = info.Arrival_Date_Gmt + " " + info.Arrival_Time_Gmt;
            if ( strDMT.length() > 0 ){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
                try {
                    Date dGMT = sdf.parse(strDMT);
                    Date dNow = new Date(System.currentTimeMillis());

                    long ldff = dNow.getTime() - dGMT.getTime();
                    long hours   = ldff / (1000 * 60 * 60 );

                    if ( hours > 24 ){
                        bIsOver = true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return bIsOver;
    }

    /**確認回覆的PNR Status Code 以及 PNR Id 與回傳的行程牌卡是否有對應*/
    private Boolean IsTripPNRmapping( CITripListResp Tripslist ){
        Boolean bIsMapping = false;
        for ( CITripListResp_Itinerary Info : Tripslist.Itinerary_Info ){
            if ( TextUtils.equals( Tripslist.PNR_Id , Info.Pnr_Id ) ){
                bIsMapping = true;
                break;
            }
        }
        return bIsMapping;
    }

    /**依照航段編號過濾航段, 只保留要顯示的航段, 如果有符合的航段則更新狀態資料*/
    private Boolean IsTripItineraryMapping( CIHomeStatusEntity homeStatusEntity, CITripListResp Tripslist ){

        Boolean bIsMapping = false;

        //取得有狀態的航段
        homeStatusEntity.ItineraryInfoList.clear();
        for ( CITripListResp_Itinerary Info : Tripslist.Itinerary_Info ){
            if ( TextUtils.equals(Tripslist.PNR_Id, Info.Pnr_Id) &&
                    TextUtils.equals( Tripslist.Itinerary_Num, Info.Itinerary_Num ) ){
                homeStatusEntity.ItineraryInfoList.add(Info);
                bIsMapping = true;
            }
        }
        //如果有符合的航段則更新
        if ( bIsMapping ){
            //
            homeStatusEntity.Itinerary_Num  = Tripslist.Itinerary_Num;
            homeStatusEntity.PNR_Id         = Tripslist.PNR_Id;
            homeStatusEntity.iStatus_Code   = Tripslist.Status_Code;
            homeStatusEntity.strFirst_name  = Tripslist.First_Name;
            homeStatusEntity.strLast_name   = Tripslist.Last_Name;
            homeStatusEntity.Is_Select_Meal = Tripslist.Is_Select_Meal;
            homeStatusEntity.Segment_Num    = Tripslist.Segment_Num;
            //

            //2016-06-23 modifly by Ryan for
            //調整為依照 Segment_Num, 來抓取正確的航段
            for ( CITripListResp_Itinerary itinerary : Tripslist.Itinerary_Info ){
                if ( TextUtils.equals(itinerary.Segment_Number , Tripslist.Segment_Num) ){
                    homeStatusEntity.Itinerary_Info = itinerary;
                    break;
                }
            }
        }
        return bIsMapping;
    }

    /**將首頁狀態寫為False*/
    private void SetHomePagePNRVisibleFalse(){
        //若原來有PNR 但當次回補, PNR ID 有誤, 則把該筆行程移開首頁
        if ( null != m_HomeStatusEntityFromDB && m_HomeStatusEntityFromDB.PNR_Id.length() > 0 ){
            //將該PNR_Id 的主頁狀態回寫為N
            UpdatePNRisVisibleAtHome(m_HomeStatusEntityFromDB.PNR_Id, false);
        }
        m_HomeStatusEntityFromDB = null;
    }

    CIInquiryTripModel.InquiryTripsCallBack m_InquiryCallBack = new CIInquiryTripModel.InquiryTripsCallBack(){

        @Override
        public void onInquiryTripsSuccess( String rt_code, String rt_msg, CITripListResp Tripslist ) {

            //2018-06-29 CR 調整整理資料邏輯，將Server回覆的資料更新至DB，僅保留有效的pnr
            //刪除失效的PNR
            if ( null != Tripslist.PNR_List && Tripslist.PNR_List.size() > 0 ){
                CIPNRStatusManager.getInstance(null).deleteTripDataByPNR(new ArrayList<String>(Tripslist.PNR_List));
            }
            //

            //當PNR_ID 對應不上, 則設為NO_Ticket
            if ( false == IsTripPNRmapping(Tripslist) ){
                //若原來有PNR 但當次回補, PNR ID 有誤, 則把該筆行程移開首頁
                SetHomePagePNRVisibleFalse();
                SendHomePageNoTicket();
                return;
            }

            //有航段的邏輯
            CIHomeStatusEntity HomeStatusEntityFromWS = new CIHomeStatusEntity();
            
            //取得有狀態的航段, 並更新PNR資料
            //當沒有對應資料則顯示NoTicket
            if ( false == IsTripItineraryMapping(HomeStatusEntityFromWS, Tripslist ) ){
                //若原來有PNR 但當次回補, PNR ID 有誤, 則把該筆行程移開首頁
                SetHomePagePNRVisibleFalse();
                SendHomePageNoTicket();
                return;
            }

            //ws回應的資料先更新資料庫
            insertOrUpdatePNRDataToDB(Tripslist, false);


            if ( CIWSHomeStatus_Code.TYPE_A_NO_TICKET == Tripslist.Status_Code ){
                UpdatePNRisVisibleAtHome(Tripslist.PNR_Id, false);
                SendHomePageNoTicket();
                return;
            }

            int iSize = Tripslist.Itinerary_Info.size();
            if ( iSize > 0 ){//WS有回資料時，查詢最後航段的抵達時間是否超過二十四小時
                if ( CheckPNRTimeIsOver24hr(Tripslist.Itinerary_Info.get(iSize-1)) ){
                    SetHomePagePNRVisibleFalse();
                    SendHomePageNoTicket();
                    return;
                }
            }

            //ws回應的資料必須更新資料庫的舊首頁PNR，或是新增首頁PNR
            //insertOrUpdatePNRDataToDB(Tripslist, true);
            UpdatePNRisVisibleAtHome(Tripslist.PNR_Id, true);

            //將更新的資料存在記憶體 - 2016-06-16 modifly by ryan
            m_HomeStatusEntityFromWS = HomeStatusEntityFromWS;
            m_HomeStatusEntityFromWS.AllItineraryInfo = Tripslist.Itinerary_Info;
            //尚未轉換的Status Code
          int wsStatusCode = m_HomeStatusEntityFromWS.iStatus_Code;
            //將PNR資料送到MRQ Server
            //這邊送原始的PNR資料上去給推播主機, 避免後續行程異動沒有更新到
            //CIApplication.getMrqManager().SendPNRINfotoMRQ( CIMrqInfoManager.TAG_LAUNCHED, m_HomeStatusEntityFromWS.AllItineraryInfo );
            CIApplication.getFCMManager().UpdateDeviceToCIServer(m_HomeStatusEntityFromWS.AllItineraryInfo);
            //
            //
            //由此介面來決定是否要繼續呼叫CPR來取得更多資訊
            if ( TransferPNRStatusToUI(m_HomeStatusEntityFromWS) ){
                //CPR資訊足夠直接回復畫面
                SendRefreshPNRSuccess(m_HomeStatusEntityFromWS);
            } else {
                //另一隻CPR WS 的邏輯
                if(wsStatusCode == CIWSHomeStatus_Code.TYPE_F_IN_FLIGHT
                        || wsStatusCode == CIWSHomeStatus_Code.TYPE_G_TRANSITION
                        || wsStatusCode == CIWSHomeStatus_Code.TYPE_H_ARRIVAL
                        ){
                    //如果符合飛行中、轉機、抵達，就刷CPR資料，且不需再判斷機場是否可online check-in
                    RefreshHomePageStatusByCPR(m_HomeStatusEntityFromWS);
                    return;
                }
                String iata = getDepartureAirportIATA(m_HomeStatusEntityFromWS);
                int iCheckAirportResultCode = CheckTheAirportOnlineCheckIn(iata);
                if(CHECK_AIRPORT_YES == iCheckAirportResultCode){
                    //如果此機場可以online check-in 就查詢CPR
                    RefreshHomePageStatusByCPR(m_HomeStatusEntityFromWS);
                } else if(CHECK_AIRPORT_NO == iCheckAirportResultCode){
                    if(null == m_HomeStatusEntityFromWS.ItineraryInfoList
                            || m_HomeStatusEntityFromWS.ItineraryInfoList.size() <= 0){
                        //沒有行程資訊
                        SendHomePageNoTicket();
                        return;
                    }
                    //如果不能online check-in，就更新首頁狀態E(空)
                    SendRefreshPNRSuccess(m_HomeStatusEntityFromWS);
                } else if(CHECK_AIRPORT_SEND_REQUEST == iCheckAirportResultCode){
                    //如果是 CHECK_AIRPORT_SEND_REQUEST 則會在ws返回結果時再次執行RefreshHomePageStatus
                    //或是保持原狀，故不先dismiss refresh progress
                }
            }
        }

        @Override
        public void onInquiryTripsError( final String rt_code, final String rt_msg ) {
            if(rt_code.equals(CIWSResultCode.NO_RESULTS) &&
                    (null != m_HomeStatusEntityFromDB && m_HomeStatusEntityFromDB.ItineraryInfoList.size() > 0)){
                //資料且DB內有能被顯示在首頁的PNR資料，則在資查看是否有登入，有登入則重新要求ws給PNR資料
                //且把DB內查到的資料，設定為不顯示在首頁
                //因為會再去向WS要資料，所以之後直接返回不秀error提示視窗
                SetHomePagePNRVisibleFalse();
                RefreshHomePageStatus();
                return;
            } else if(null != m_HomeStatusEntityFromDB && m_HomeStatusEntityFromDB.ItineraryInfoList.size() > 0){
                //其他錯誤且db有資料，以舊資料刷新首頁
                SendRefreshPNRSuccess(m_HomeStatusEntityFromDB);
            } else {
                //其他錯誤而db無資料，則顯示無訂票
                SendHomePageNoTicket();
            }

//            SendError(rt_code, rt_msg); 暫時關閉 by kevin 2016/06/21
        }
    };


    /**確認機場是否可以online check in 的返回值*/
    private static final int CHECK_AIRPORT_SEND_REQUEST = 0;

    private static final int CHECK_AIRPORT_NO = 1;

    private static final int CHECK_AIRPORT_YES = 2;
    /**
     * 判斷此航段之啟程機場是否可以online check-in
     * @param IAIT 機場代碼
     * @return int 如果可以online check-in 就回 CHECK_AIRPORT_YES(2)
     */
    private int CheckTheAirportOnlineCheckIn(final String IAIT){
        if(TextUtils.isEmpty(IAIT)){
            //若ＷＳ沒有提供iata，則無法確認是否為online check-in，視同無法
            return CHECK_AIRPORT_NO;
        }
        m_inquiryFlightStationPresenter = CIInquiryFlightStationPresenter.getInstance(new CIInquiryFlightStatusStationListener() {
            @Override
            public void onAllStationSuccess(String rt_code, String rt_msg, CIInquiryFlightStationPresenter presenter) {
                if(null != presenter.getAllDepatureStationList()){
                    //當ws有回來航班資料就在重刷一次首頁狀態
                    RefreshHomePageStatus();
                } else {
                    //如果ws沒給資料但卻回覆成功，就不再重新向ＷＳ要資料，避免無窮循環
                    SendHomePageNoChange();
                }
            }
            @Override
            public void onStationError(String rt_code, String rt_msg) {
                //如果ws回覆error，就不再重新向ＷＳ要資料，避免無窮循環
                //保留原畫面上的狀態
                SendHomePageNoChange();
//                SendError(rt_code, rt_msg);  暫時關閉 by kevin 2016/06/21
            }
            @Override
            public void onODStationSuccess(String rt_code, String rt_msg, CIInquiryFlightStationPresenter presenter) {}
            @Override
            public void showProgress() {}
            @Override
            public void hideProgress() {}
        }, CIInquiryFlightStationPresenter.ESource.TimeTable);

        if(null == m_inquiryFlightStationPresenter.getAllDepatureStationList()){
            m_inquiryFlightStationPresenter.initAllStationDB();
            m_inquiryFlightStationPresenter.InquiryAllStationListFromWS();
            //向ws要求資料
            return CHECK_AIRPORT_SEND_REQUEST;
        } else {
            //將資訊保留起來, 供取得CPR時判斷用
            m_FlightStationInfo = m_inquiryFlightStationPresenter.getStationInfoByIATA(IAIT);
            if(null != m_FlightStationInfo){
                //如果有找到機場資料，就判斷tag是否為Y
                return m_FlightStationInfo.is_vcheckin.equals("Y") ? CHECK_AIRPORT_YES : CHECK_AIRPORT_NO;
            } else {
                //如果沒有找到機場資料就回覆false，無法確認此機場是否能夠online check in
                return CHECK_AIRPORT_NO;
            }
        }
    }

    /**
     * 取得check in機場 IATA
     * @param entity  給首頁用的資料
     * @return  機場代碼
     */
    private String getDepartureAirportIATA(CIHomeStatusEntity entity){
        for(CITripListResp_Itinerary data:entity.ItineraryInfoList){
            if(TextUtils.equals(data.Itinerary_Num, entity.Itinerary_Num)){
                //判斷為相同航段序號時，則取此航段序號之出發站機場代碼
                if(TextUtils.equals(data.Segment_Number,entity.Segment_Num)){
                    return data.Departure_Station;
                }
            }
        }
        return null;
    }

    /**
     * 依據CPR資料刷新首頁狀態及資料
     */
    public void RefreshHomePageStatusByCPR(final CIHomeStatusEntity HomeStatusEntityFromWS){

        if(null == HomeStatusEntityFromWS){
            return ;
        }

        if ( null == m_checkInPresenter ){
            m_checkInPresenter = new CIInquiryCheckInPresenter(null);
        }

        m_checkInPresenter.setCallBack( new CIInquiryCheckInListener() {
            @Override
            public void onInquiryCheckInSuccess(String rt_code, String rt_msg, CICheckInAllPaxResp CheckInList) {

                TransferPNRStatusToUIByCPR(CheckInList, HomeStatusEntityFromWS);
            }

            @Override
            public void onInquiryCheckInError(String rt_code, String rt_msg) {

                if(null == HomeStatusEntityFromWS.ItineraryInfoList
                        ||HomeStatusEntityFromWS.ItineraryInfoList.size() <= 0){
                    //沒有行程資訊
                    SendHomePageNoTicket();
                    return;
                }
                int iWsStatusCode = HomeStatusEntityFromWS.ItineraryInfoList.get(0).Status_Code;
                //查無資料代碼是-998
                if(iWsStatusCode == CIWSHomeStatus_Code.TYPE_D_E_CHECKIN){
                    if(rt_code.equals(CIWSResultCode.NO_RESULTS)){
                        //首頁狀態D
                        HomeStatusEntityFromWS.iStatus_Code = HomePage_Status.TYPE_D_ONLINE_CHECKIN;
                        HomeStatusEntityFromWS.bIsNewSiteOnlineCheckIn = false;
                        //因查無資料會跳轉Web Check-In，故僅更新主頁狀態，不顯示錯誤
                        SendRefreshPNRSuccess(HomeStatusEntityFromWS);
                        return;
                    } else {
                        //首頁狀態E
                        HomeStatusEntityFromWS.iStatus_Code = HomePage_Status.TYPE_E_DESK_CHECKIN;
                    }
                } else if (iWsStatusCode == CIWSHomeStatus_Code.TYPE_D_E_GATE_INFO){
                    //首頁狀態Eg(空)
                    HomeStatusEntityFromWS.iStatus_Code = HomePage_Status.TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5;
                }
                SendRefreshPNRSuccess(HomeStatusEntityFromWS);
//                SendError(rt_code, rt_msg); 暫時關閉 by kevin 2016/06/21
            }

            @Override
            public void onInquiryCheckInAllPaxSuccess(String rt_code, String rt_msg, CICheckInAllPaxResp CheckInList) {

            }

            @Override
            public void onInquiryCheckInAllPaxError(String rt_code, String rt_msg) {

            }

            @Override
            public void showProgress() {}

            @Override
            public void hideProgress() {}
        });

        m_checkInPresenter.InquiryCheckInByPNRFromWS(HomeStatusEntityFromWS.PNR_Id,
                                            HomeStatusEntityFromWS.strFirst_name,
                                            HomeStatusEntityFromWS.strLast_name);
    }

    private void TransferPNRStatusToUIByCPR(CICheckInAllPaxResp resp,
                                            CIHomeStatusEntity HomeStatusEntityFromWS){
        HomeStatusEntityFromWS.bHaveCPRData     = false;
        if(resp == null || resp.size() <= 0){
            //無資料，首頁狀態G或D
            HomeStatusEntityFromWS.bHaveCPRData = false;
            //將更新的資料存在記憶體
            m_HomeStatusEntityFromWS = HomeStatusEntityFromWS;
            //
            SendRefreshPNRSuccess(HomeStatusEntityFromWS);
            return;
        }

        //2016-06-29 確認作法, 調整為 依照  Segment_Num 以及 Itinerary_Num 這兩個參數來抓對應的航段
        CICheckInPax_InfoEntity RespCPRData = resp.get(0);
        CICheckInPax_ItineraryInfoEntity CurrentCPRInfo = null;

        //2016-06-29 將資料回歸至 CPR_Info
        //刷新CPR報到資料
        HomeStatusEntityFromWS.CheckInResp      = resp;
        //2016-07-11 modifly by ryan for 將拿到的CPR 拿到的正確時間資料更新到PNR
        for ( CICheckInPax_ItineraryInfoEntity CPRInfo : RespCPRData.m_Itinerary_InfoList ){
            //2016-06-29 確認作法, 調整為 依照  Segment_Num 以及 Itinerary_Num 這兩個參數來抓對應的航段
            if ( TextUtils.equals( CPRInfo.Segment_Number, HomeStatusEntityFromWS.Segment_Num ) ){
                CurrentCPRInfo = CPRInfo;
                HomeStatusEntityFromWS.CPR_Info = CPRInfo;
                HomeStatusEntityFromWS.bHaveCPRData = true;
                //break;
            }
            //2016-11-15 Modify by Ryan for 調整PNR時間邏輯, 不再抓取CPR的時間來更新, 調整為抓取DISPLAY TAG
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
//            //2016-07-11 更新所有PNR行程的時間，把最新的CPR時間更新至PNR
//            for ( CITripListResp_Itinerary PNRInfo : HomeStatusEntityFromWS.AllItineraryInfo ){
//                if ( TextUtils.equals(CPRInfo.Segment_Number, PNRInfo.Segment_Number) ){
//                    UpdatePNRDateTimeFromCPR(PNRInfo, CPRInfo);
//                    break;
//                }
//            }
//            //
//            //2016-07-11 modifly by ryan for 要顯示在畫面上的航段也要更新
//            //該行段已經使用 Itinerary_Num 過濾出要顯示在畫面上的航段，所以顯示時間也要更新
//            for ( CITripListResp_Itinerary pnrInfo : HomeStatusEntityFromWS.ItineraryInfoList ){
//                if ( TextUtils.equals(CPRInfo.Segment_Number, pnrInfo.Segment_Number) ){
//                    UpdatePNRDateTimeFromCPR(pnrInfo, CPRInfo);
//                    break;
//                }
//            }
//            //
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
        }

        //status_code首頁狀態 3 Check-in 或 2 Boarding Gate 需取得CPR資料做流程分析來決定首頁狀態
        if(null == HomeStatusEntityFromWS.ItineraryInfoList
                ||HomeStatusEntityFromWS.ItineraryInfoList.size() <= 0){
            //沒有行程資訊
            UpdatePNRisVisibleAtHome( HomeStatusEntityFromWS.PNR_Id ,false);
            SendHomePageNoTicket();
            return;
        }

        // iStatus_Code 在這裡已經 從PNR status code 轉換成 Home status code by Kevin
        int iHomeStatusCode = HomeStatusEntityFromWS.iStatus_Code;

        if(iHomeStatusCode == HomePage_Status.TYPE_E_DESK_CHECKIN){
            if( null != CurrentCPRInfo && true == CurrentCPRInfo.Is_Do_Check_In ){
                //server有給資料，可以報到了，就接著判斷是否可以報到了

                if(true == CurrentCPRInfo.Is_Check_In){

                    //調整判斷邏輯,
                    //參照wireframe, HomePage Overview
                    if(     null != m_FlightStationInfo &&
                            true == TextUtils.equals(m_FlightStationInfo.is_vpass,"Y")  &&
                            false == TextUtils.isEmpty(CurrentCPRInfo.Boarding_Pass)    ){ //已經報到了，接著判斷有無登機證資料

                        //有登機證資料，首頁狀態Df
                        HomeStatusEntityFromWS.iStatus_Code = HomePage_Status.TYPE_D_ONLINE_CHECKIN_FINISH;
                    } else {
                        //無登機證資料，首頁狀態E
                        //已經heck-in完成，卻無登機證資料，表示不能列印線上電子登機證，但無法得知是否已經取得紙本，故在heck-in時間內，顯示櫃檯資訊
                        //HomeStatusEntityFromWS.iStatus_Code = HomePage_Status.TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5;
                        //HomeStatusEntityFromWS.iStatus_Code = HomePage_Status.TYPE_E_DESK_CHECKIN;
                        HomeStatusEntityFromWS.iStatus_Code = HomePage_Status.TYPE_D_ONLINE_CHECKIN_FINISH_BOARDING_PASS_NOT_AVAILABLE;
                    }
                } else {
                    //未報到，就接者判斷是否在黑名單
                    if(true == CurrentCPRInfo.Is_Black){
                        //黑名單內，首頁狀態E
                        HomeStatusEntityFromWS.iStatus_Code = HomePage_Status.TYPE_E_DESK_CHECKIN;
                    } else {
                        //不再黑名單內，首頁狀態D
                        HomeStatusEntityFromWS.iStatus_Code = HomePage_Status.TYPE_D_ONLINE_CHECKIN;
                    }
                }
            } else {
                //還不可以報到，首頁狀態E
                HomeStatusEntityFromWS.iStatus_Code = HomePage_Status.TYPE_E_DESK_CHECKIN;
            }
        } else if (iHomeStatusCode == HomePage_Status.TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5){

            //2016-11-22 Mdify by Ryan for 調整邏輯, 登機門時間有可能已經關櫃, 所以要移除是否可以Check-in的判斷
            //調整判斷邏輯，參照wireframe HomePage Overview
            //if(  null != CurrentCPRInfo && true == CurrentCPRInfo.Is_Do_Check_In && true == CurrentCPRInfo.Is_Check_In ){
            if(  null != CurrentCPRInfo && true == CurrentCPRInfo.Is_Check_In ){

                if (    null == m_FlightStationInfo ||
                        true == TextUtils.equals(m_FlightStationInfo.is_vpass,"N") ||
                        true == TextUtils.isEmpty(CurrentCPRInfo.Boarding_Pass)     ) {

                    //2017-01-06 Modify by Ryan for 修正為，只要Check-in過了，就當做有登機證，直接導引去登機門
                    //HomeStatusEntityFromWS.iStatus_Code = HomePage_Status.TYPE_D_ONLINE_CHECKIN_FINISH_BOARDING_PASS_NOT_AVAILABLE;
                    HomeStatusEntityFromWS.iStatus_Code = HomePage_Status.TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5;
                } else {
                    HomeStatusEntityFromWS.iStatus_Code = HomePage_Status.TYPE_D_ONLINE_CHECKIN_FINISH_AT_AIRPORT_1H5;
                }
            } else {
                //2017-01-19 Modify by ryan for 未Check-in 導引至櫃台並且調整為Ｅ1的狀態
                HomeStatusEntityFromWS.iStatus_Code = HomePage_Status.TYPE_E_DESK_CHECKIN;
                //登機時間，未Check-in，一律引導去櫃檯
                //HomeStatusEntityFromWS.iStatus_Code = HomePage_Status.TYPE_D_ONLINE_CHECKIN_FINISH_BOARDING_PASS_NOT_AVAILABLE;
            }

            //2016-08-39 Ryan , 舊邏輯先保留
//            if(  null != entity && true == entity.Is_Do_Check_In && true == entity.Is_Check_In ){
//                //server有給資料，可以報到了，也已經報到了，就判斷是否有登機證資料
//                if (false == TextUtils.isEmpty(entity.Boarding_Pass)) {
//                    //server有給資料，有登機證資料，首頁狀態Dg
//                    HomeStatusEntityFromWS.iStatus_Code = HomePage_Status.TYPE_D_ONLINE_CHECKIN_FINISH_AT_AIRPORT_1H5;
//                } else {
//                    //server有給資料，無登機證資料，首頁狀態Eg
//                    HomeStatusEntityFromWS.iStatus_Code = HomePage_Status.TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5;
//                }
//            } else {
//                //server有給資料，沒有報到或不能報到，首頁皆為狀態Eg
//                HomeStatusEntityFromWS.iStatus_Code = HomePage_Status.TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5;
//            }
        } else if ( iHomeStatusCode == HomePage_Status.TYPE_F_IN_FLIGHT     ||
                    iHomeStatusCode == HomePage_Status.TYPE_G_TRANSITION    ||
                    iHomeStatusCode == HomePage_Status.TYPE_H_ARRIVAL       ){

            if ( false == CurrentCPRInfo.Is_Check_In ){
                //航段已經在飛行中,轉機,抵達狀態 還沒有Check-in 則直接移除行程
                UpdatePNRisVisibleAtHome( HomeStatusEntityFromWS.PNR_Id ,false);
                SendHomePageNoTicket();
                return;
            }
        }

        //將更新的資料存在記憶體 - 2016-06-16 modifly by ryan
        m_HomeStatusEntityFromWS = HomeStatusEntityFromWS;
        //
        SendRefreshPNRSuccess(HomeStatusEntityFromWS);
        //
        //將PNR資料送到 推播主機
        //List<MrqEvent> events = new ArrayList<>();
        //CIApplication.getMrqManager().SendPNRINfotoMRQ(CIMrqInfoManager.TAG_LAUNCHED, HomeStatusEntityFromWS.AllItineraryInfo);
        //
        CIApplication.getFCMManager().UpdateDeviceToCIServer(HomeStatusEntityFromWS.AllItineraryInfo);
    }

    private void SendRefreshPNRSuccess( final CIHomeStatusEntity HomeStatusEntity ){


        s_hdUIThreadhandler.post(new Runnable() {
            @Override
            public void run() {
                if(null != HomeStatusEntity && null != m_Listener ){
                    m_Listener.onRefreshPNRSuccess((CIHomeStatusEntity)HomeStatusEntity.clone());
                }
                if ( null != m_Listener )
                    m_Listener.onDismissRefresh();
            }
        });
    }

    private void SendError( final String rt_code, final String rt_msg ){


        s_hdUIThreadhandler.post(new Runnable() {
            @Override
            public void run() {
                if ( null != m_Listener ) {
                    m_Listener.onInquiryError(rt_code, rt_msg);
                }
            }
        });

    }

    private void SendHomePageNoTicket(){


        s_hdUIThreadhandler.post(new Runnable() {
            @Override
            public void run() {
                if ( null != m_Listener ) {
                    m_Listener.onHomePageNoTicket();
                    m_Listener.onDismissRefresh();
                }
            }
        });


        //當沒有PNR資料或原PNR資料已經過期時,送空的行程資訊上去推播主機
        CIApplication.getFCMManager().UpdateDeviceToCIServer(null);
//        List<MrqEvent> events = new ArrayList<>();
//        CIApplication.getMrqManager().SendDataToMRQServer(CIMrqInfoManager.TAG_LAUNCHED, events );
//        //
    }

    private void SendHomePageNoChange(){
        //保留原畫面上的狀態

        s_hdUIThreadhandler.post(new Runnable() {
            @Override
            public void run() {
                if ( null != m_Listener ) {
                    m_Listener.onNonUpdatePNRInfo();
                    m_Listener.onDismissRefresh();
                }
            }
        });

    }

    private void SendRefreshPNRNonDismissRefresh( final CIHomeStatusEntity HomeStatusEntity ){

        s_hdUIThreadhandler.post(new Runnable() {
            @Override
            public void run() {

                if(null != HomeStatusEntity && null != m_Listener){
                    m_Listener.onRefreshPNRSuccess((CIHomeStatusEntity) HomeStatusEntity.clone());
                }
            }
        });

    }
    
    /**取得當前首頁的PNR,CPR狀態*/
    public CIHomeStatusEntity getHomeStatusFromMemory(){
        return m_HomeStatusEntityFromWS;
    }

    /**取得當前首頁的PNR,CPR狀態*/
    public void clearMemoryData_HomeStatus(){
        m_HomeStatusEntityFromWS = null;
    }

    /**unit-test 用來清除此類別的實例*/
    public static void clearInstance(){
        s_Instance = null;
    }

    /**unit-test 用來初始化環境*/
    public void setHomeStatusEntityFromWS(CIHomeStatusEntity m_HomeStatusEntityFromWS) {
        this.m_HomeStatusEntityFromWS = m_HomeStatusEntityFromWS;
    }

    /**將CPR上新的Date Time資訊更新至 PNR*/
    public void UpdatePNRDateTimeFromCPR(CITripListResp_Itinerary PNRInfo, CICheckInPax_ItineraryInfoEntity CPRInfo ){

        PNRInfo.Departure_Date      = CPRInfo.Display_Departure_Date;
        PNRInfo.Departure_Date_Gmt  = CPRInfo.Display_Departure_Date_Gmt;
        PNRInfo.Departure_Time      = CPRInfo.Display_Departure_Time;
        PNRInfo.Departure_Time_Gmt  = CPRInfo.Display_Departure_Time_Gmt;

        PNRInfo.Arrival_Date        = CPRInfo.Display_Arrival_Date;
        PNRInfo.Arrival_Date_Gmt    = CPRInfo.Display_Arrival_Date_Gmt;
        PNRInfo.Arrival_Time        = CPRInfo.Display_Arrival_Time;
        PNRInfo.Arrival_Time_Gmt    = CPRInfo.Display_Arrival_Time_Gmt;
    }

    /**TripDetail更換首頁行程後, 告知首頁須更新行程*/
    public void setHomePageTripIsChange( boolean bChange ){
        m_bIsHomePageTripChange = bChange;
    }

    /**檢查首頁丁選的行程是否有變更*/
    public boolean IsHomePageTripChange(){
        return m_bIsHomePageTripChange;
    }
}
