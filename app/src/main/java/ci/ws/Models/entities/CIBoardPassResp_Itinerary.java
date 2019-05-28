package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jlchen on 2016/5/31.
 */
@SuppressWarnings("serial")
public class CIBoardPassResp_Itinerary implements Serializable, Cloneable {

    /**
     * 自訂的tag
     * 集中於{@link ci.ws.Models.CIInquiryBoardingPassModel#DecodeResponse_Success}
     * 統一從 PnrInfo 塞入，以便於顯示登機證背景資料使用
     */
    public String Pnr_Id;

    /**出發日期 (當地) (完整) (Len: 10)*/
    @Expose
    public String Departure_Date;

    /**出發時間( Len: Max. 5)*/
    @Expose
    public String Departure_Time;

    /**出發地 (城市代碼) (Len: 3)*/
    @Expose
    public String Departure_Station;

    /**出發地航站名稱*/
    @Expose
    public String Departure_Station_Name2;

    /**目的地時間( Len: Max. 5)*/
    @Expose
    public String Arrival_Time;

    /**目的地 (城市代碼) (Len: 3)*/
    @Expose
    public String Arrival_Station;

    /**目的地航站名稱 (Len: 3)*/
    @Expose
    public String Arrival_Station_Name2;

    /**航空公司 (Len: 2)*/
    @Expose
    public String Airlines;

    /**班機編號 (Len: 4)*/
    @Expose
    public String Flight_Number;

    /**搭機艙等 (Len: 1)*/
    @Expose
    public String Class_Of_Service;

    /**搭機艙等敍述*/
    @Expose
    public String Class_Of_Service_Desc;

    /**訂位艙等*/
    @Expose
    public String Booking_Class;

    /**登機門*/
    @Expose
    public String Boarding_Gate;

    /**登機日期*/
    @Expose
    public String Boarding_Date;

    /**登機時間*/
    @Expose
    public String Boarding_Time;

    /**航廈*/
    @Expose
    public String Departure_Terminal;

    /**乘客資料清單*/
    @Expose
    public List<CIBoardPassResp_PaxInfo> Pax_Info;

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
