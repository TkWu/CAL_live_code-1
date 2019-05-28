package ci.ui.object;

import android.content.Context;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import ci.function.Core.CIApplication;
import ci.ws.Models.entities.CIFCM_Event;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Models.entities.CIUpdateDeviceReq;
import ci.ws.Presenter.CIFCMPresenter;

/**
 * Created by Ryan on 16/6/21.
 * 推播機制
 */
public class CIFCMManager {

    private Context m_context = null;

    public CIFCMManager( Context context ){
        m_context = context;
    }

    /**
     推播開關
     1111
     四位字串,
     1為開啟接收，0為關閉
     因目前推播開關未分類，統一回傳 1111 或是 0000
     */
    public String getNotiflySwitch(){
        return AppInfo.getInstance(m_context).GetAppNotiflySwitch()? "1111":"0000";
    }


    /**
     * 功能說明:
     使用API 上傳裝置基本資訊已供後續推波服務使用

     上傳時間點：
     1. 開啟APP，更新所有必需資料後，最後須將最新資料更新
     2. 登入/登出使用者帳號
     3. 裝置成功取得欲查詢行程
     4. App內語系改變
     5. App內推播設定改變
     */
    public void UpdateDeviceToCIServer( List<CITripListResp_Itinerary> arPnrList ){

        CIUpdateDeviceReq req = new CIUpdateDeviceReq();

        req.customerid      = CIApplication.getLoginInfo().GetUserMemberCardNo();
        req.timezone        = CIApplication.getDeviceInfo().getTimeZoneId();
        req.pushtokentype   = "GCM";
        req.deviceid        = CIApplication.getDeviceInfo().getAndroidId();
        req.appname         = "mobile30";
        req.pushtoken       = FirebaseInstanceId.getInstance().getToken();
        req.notfiswitch     = getNotiflySwitch();
        req.applanguages    = CIApplication.getLanguageInfo().getWSLanguage();
        req.appversion      = CIApplication.getVersionName();
        req.flights         = getFlights(arPnrList);

        CIFCMPresenter.getInstance(null).UpdateDevice(req);
    }

    public void UpdateMsgIdToCIServer( String strMSgId ){

        ArrayList<String> arMsgIdList = new ArrayList<>();

        arMsgIdList.add(strMSgId);

        CIFCMPresenter.getInstance(null).UpdateMsg(arMsgIdList);
    }


    public ArrayList<CIFCM_Event> getFlights( List<CITripListResp_Itinerary> arPnrList ){

        ArrayList<CIFCM_Event> events = new ArrayList<>();

        if ( null != arPnrList ) {

            for ( CITripListResp_Itinerary info : arPnrList ){

                String strDeparttime = String.format("%sT%s%s", info.Departure_Date_Gmt, info.Departure_Time_Gmt, ":00.000Z");
                String strArrivaltime = String.format("%sT%s%s", info.Arrival_Date_Gmt, info.Arrival_Time_Gmt, ":00.000Z");

                events.add(setflight1(info.Airlines, info.Flight_Number, info.Pnr_Id, strDeparttime, strArrivaltime ));
            }
        }


        return events;
    }

    /**將航班資訊轉為 CIFCM_Event 物件
     * @param strAirline 航班
     * @param strNumber 航班編號
     * @param strPnr
     * @param strArrivaltime 出發
     * @param strDeparttime 抵達*/
    public CIFCM_Event setflight1(final String strAirline, final String strNumber, final String strPnr, final String strDeparttime , final String strArrivaltime ) {

        CIFCM_Event flight = new CIFCM_Event();

        flight.flight = strAirline + strNumber;
        flight.pnr  = strPnr;
        flight.arrivaltime  = strArrivaltime;
        flight.departtime   = strDeparttime;

        return flight;
    }

}
