package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

/**
 flight		行程內航班編號
 長度限制為6
 航空公司代碼(2碼)+航班號(四碼，左邊補0)
 格式: CI0100
 pnr		行程內PNR
 AE5798
 arrivaltime		行程內航班抵達時間
 統一回傳航班表定GMT時間
 格式: 2018-06-26T07:05:00.000Z
 departtime		行程內航班起飛時間
 統一回傳航班表定GMT時間
 格式: 2018-06-26T07:05:00.000Z
 * */
public class CIFCM_Event {

    @Expose
    public String flight;

    @Expose
    public String pnr;

    @Expose
    public String arrivaltime;

    @Expose
    public String departtime;

}
