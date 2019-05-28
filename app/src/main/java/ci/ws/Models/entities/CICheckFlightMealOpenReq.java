package ci.ws.Models.entities;

/**
 * Created by JL Chen on 2016/05/19.
 * 文件：CI_APP_API_MealSelection.docx on 16/05/18 20:53
 * 4.6 CheckFlightMealOpen
 * 依航班檢查是否開放預訂餐點的Request資料
 */
public class CICheckFlightMealOpenReq {

    /**航空公司; CI/AE*/
    public String flight_company;

    /**航班編號*/
    public String flight_num;

    /**航班航段*/
    public String flight_sector;

    /**航班日期*/
    public String flight_date;

    /**訂位狀態; HK/HL*/
    public String pnr_status;

    /**艙別; F/C/Y*/
    public String seat_class;


    public CICheckFlightMealOpenReq(){
        flight_company  = "";
        flight_num      = "";
        flight_sector   = "";
        flight_date     = "";
        pnr_status      = "";
        seat_class      = "";
    }
}
