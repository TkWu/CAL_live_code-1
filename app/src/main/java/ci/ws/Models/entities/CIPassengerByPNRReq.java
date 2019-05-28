package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

/**
 * 功能說明: 使用PNR、出發地及目的地
 * 取得該行程的乘客資料
 * 需帶user IP位置
 * Created by jlchen on 16/5/11.
 */
public class CIPassengerByPNRReq {

    /**訂位代號( Len: 6)*/
    public String pnr_Id;
//    /**名( Len: Max. 30)*/
//    public String first_name;
//    /**姓( Len: Max. 30)*/
//    public String last_name;
    /**出發地代號*/
    public String departure_station;
    /**目的地代號*/
    public String arrival_station;

    //2016-07-25 add PNR_Seq, 讓Server抓取正確的航段資料
    public String pnr_seq;

    //201801106 add Segment_Number，for 行李追蹤抓資料使用
    /**航段序號 (Len: 4)*/
    public String Segment_Number;


    public CIPassengerByPNRReq(){
        pnr_Id = "";
//        first_name = "";
//        last_name = "";
        departure_station = "";
        arrival_station = "";
        pnr_seq = "";
    }
}
