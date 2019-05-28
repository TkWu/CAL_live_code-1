package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

/**
 * 里程活動 查詢里程明細 的回傳結果
 * Created by jlchen on 16/5/16.
 */
public class CIMileageRecord_Info implements Cloneable {

    //哩程類別[flight_item] Tag會顯示的值, 1為搭機里程; 2為推廣或合作哩程
    //搭機哩程
    public static final String FLIGHT_MILES         = "1";
    //推廣哩程
    public static final String PROMOTION_MILES      = "2";
    //合作哩程
    public static final String COOPERATION_MILES    = "2";

    /**搭乘日期(YYYY-MM-DD)*/
    @Expose
    public String flight_date;

    /**哩程類別(1搭機哩程/2推廣哩程&合作哩程)*/
    @Expose
    public String flight_item;

    /**哩程類別名稱(搭機哩程/推廣哩程/合作哩程)*/
    @Expose
    public String flight_item_desc;

    /**哩程名稱(搭機哩程會顯示A->B,其餘里程類別同類別名稱)*/
    @Expose
    public String flight_desc;

    /**哩程數*/
    @Expose
    public String mileage;

    /**有效日期*/
    @Expose
    public String due_date;

    /**航班編號-僅搭機里程才會有此欄位*/
    @Expose
    public String flight_number;

    /**艙等-僅搭機里程才會有此欄位*/
    @Expose
    public String cabin;

    /**班機艙等
     * 使用cabin 轉換為下列三種艙等
     * #CIWSBookingClass.BOOKING_CLASS_BUSINESS
     * #CIWSBookingClass.BOOKING_CLASS_PREMIUM
     * #CIWSBookingClass.BOOKING_CLASS_ECONOMY
     * 用於抓取艙等中文名稱 */
    public String Booking_Class_Name_Tag;

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
