package ci.ui.object.item;

import java.util.ArrayList;
import java.util.List;

import ci.ui.define.HomePage_Status;
import ci.ws.Models.entities.CICheckInPax_ItineraryInfoEntity;
import ci.ws.Models.entities.CICheckInAllPaxResp;
import ci.ws.Models.entities.CITripListResp_Itinerary;

/**
 * Created by Ryan on 16/5/21.
 */
public class CIHomeStatusEntity implements Cloneable{

    /**對應定位編號的乘客護照英文名*/
    public String strFirst_name;
    /**對應定位編號的乘客護照英文姓*/
    public String strLast_name;
    /**定位編號*/
    public String PNR_Id;
    /**該定位編號的航段編號*/
    public String Itinerary_Num;
    /**該航段編號的狀態*/
    public int iStatus_Code;
    /**是否可以選餐(Y/N)*/
    public String Is_Select_Meal;
    /**航段序號*/
    public String Segment_Num;
    //調整為直接抓取CPR_Info
//    /**登機證號碼*/
//    public String Boarding_Pass;
//    /**座位號碼*/
//    public String Seat_Number;
//    /**登機門號*/
//    public String Boarding_Gate;
//    /**登機日期*/
//    public String Boarding_Date;
//    /**登機時間*/
//    public String Boarding_Time;
//    /**臨櫃報到櫃檯號碼*/
//    public String Check_In_Counter;
    /**是否已經有CPR資料*/
    public Boolean bHaveCPRData;

    /**會使用到的航段牌卡（使用Itinerary_Num過濾過的航班資訊）*/
    //public CITripListResp_Itinerary Itinerary_Info;
    public List<CITripListResp_Itinerary> ItineraryInfoList;

    /**未使用Itinerary_Num 過濾過的行程資訊*/
    public List<CITripListResp_Itinerary> AllItineraryInfo;

    /**true 為（新站）App online check-in，false 則為（舊站）Web online check-in*/
    public Boolean bIsNewSiteOnlineCheckIn;

    public Boolean bCheckInFinish;
    /**該PNR完整的CPR資料*/
    public CICheckInAllPaxResp CheckInResp;
    /**當下航班狀態對應的PNR資料*/
    public CITripListResp_Itinerary Itinerary_Info;
    /**當下航班狀態對應的CPR資料*/
    public CICheckInPax_ItineraryInfoEntity CPR_Info;

    public CIHomeStatusEntity(){
        PNR_Id          = "";
        Itinerary_Num   = "";
        iStatus_Code    = HomePage_Status.TYPE_A_NO_TICKET;
        strFirst_name   = "";
        strLast_name    = "";
        ItineraryInfoList  = new ArrayList<CITripListResp_Itinerary>();
        bIsNewSiteOnlineCheckIn = true;
        bCheckInFinish  = false;
        Is_Select_Meal  = "";
        Segment_Num     = "";
//        Boarding_Pass   = "";
//        Seat_Number     = "";
//        Boarding_Gate   = "";
//        Boarding_Date   = "";
//        Boarding_Time   = "";
//        Check_In_Counter= "";
        bHaveCPRData    = false;
        CheckInResp     = new CICheckInAllPaxResp();
        Itinerary_Info  = null;
        CPR_Info        = null;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
