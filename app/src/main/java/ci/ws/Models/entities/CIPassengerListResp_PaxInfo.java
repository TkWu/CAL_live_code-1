package ci.ws.Models.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * TripDetail - Passenger查詢回來的資料
 * Created by jlchen on 16/5/11.
 */
@SuppressWarnings("serial")
public class CIPassengerListResp_PaxInfo implements Serializable {

    /**成人*/
    public static final String PASSENGER_ADULT  = "ADT";
    /**兒童*/
    public static final String PASSENGER_CHILD  = "CHD";
    /**嬰兒*/
    public static final String PASSENGER_INFANT  = "INF";


    /**旅客名*/
    @SerializedName("First_Name")
    public String First_Name;

    /**旅客姓氏*/
    @SerializedName("Last_Name")
    public String Last_Name;

    /**旅客類型 ADT:成人,CHD:兒童,INF:嬰兒*/
    @SerializedName("Pax_Type")
    public String Pax_Type;

    @SerializedName("Pax_Number")
    public String Pax_Number;

    /**座位編號*/
    @SerializedName("Seat_Number")
    public String Seat_Number;

    /**座位狀態(是否可以選)*/
    @SerializedName("Seat_Status")
    public String Seat_Status;

    /**免費行李重量,數量
     * 制換成各語系對應的可讀單位*/
    @SerializedName("Baggage_Allowence")
    public String Baggage_Allowence;

    /**貴賓休息室(Y/N)*/
    @SerializedName("Vip_Lounge")
    public String Vip_Lounge;

    /**訂位艙等 (Len: 1)
     * 此為訂位時的原始艙等, 未經轉換,
     * 可用於可否選位判斷,
     * 除N/L/X/G/S外，皆可選位*/
    @SerializedName("Booking_Class")
    public String Booking_Class;

    /**班機艙等
     * 使用Booking_Class 轉換為下列三種艙等
     * #CIWSBookingClass.BOOKING_CLASS_BUSINESS
     * #CIWSBookingClass.BOOKING_CLASS_PREMIUM
     * #CIWSBookingClass.BOOKING_CLASS_ECONOMY
     * 用於抓取艙等中文名稱, 以及判斷是否可選餐
     * 目前僅 CIWSBookingClass.BOOKING_CLASS_BUSINESS, 可選餐*/
    public String Class_of_Service;

    /**根據Class_of_Service轉換成可顯示的字串
     * */
    public String Class_of_Service_Name;

    /**機票號碼*/
    @SerializedName("Ticket")
    public String Ticket;

    /**是否符合選位資格(Y/N)*/
    @SerializedName("Is_Change_Seat")
    public String Is_Change_Seat;

    /**是否符合選餐資格(Y/N)*/
    @SerializedName("Is_Change_Meal")
    public String Is_Change_Meal;

    /**是否樂購行李重量(Y/N)*/
    @SerializedName("Is_Add_Baggage")
    public String Is_Add_Baggage;

    /**已加購樂購行李重量/件數*/
    @SerializedName("DBBaggage")
    public BaggageEntity DBBaggage;

    /**是否可加購超額行李(Len: 1)
     Y:可
     N:不可*/
    @SerializedName("Is_Add_ExcessBaggage")
    public String Is_Add_ExcessBaggage;

    /**已加購超額行李重量/件數*/
    @SerializedName("ExcessBaggage")
    public BaggageEntity ExcessBaggage;

    /**卡號*/
    @SerializedName("Card_Id")
    public String Card_Id;

    /**卡別*/
    @SerializedName("Card_Type")
    public String Card_Type;

    /**餐點*/
    @SerializedName("Meal")
    public CIPassengerListResp_Meal Meal;

    //依CPR取得的狀態寫入乘客狀態
    public int Status_Code = -1;

    //旅客id
    public String Uci;

    //航班id
    public String Did;

    //是否取得CPR 資料
    public Boolean bHaveCPR;

    /**是否已經報到( Len: Max. 5)*/
    public Boolean CPR_Is_Check_In;

    /**是否可以報到(Len: 1)*/
    public Boolean CPR_Is_Do_Check_In;

    /**是否可以取消報到(Len: 1)*/
    public Boolean CPR_Is_Do_Cancel_Check_In;

    /**是否可以換位(Len: 1)*/
    public Boolean CPR_Is_Change_Seat;

    /**黑名單(Len: 1)*/
    public Boolean CPR_Is_Black;

    /**登機證號碼*/
    public String CPR_Boarding_Pass;

    /**是否可以顯示登機證*/
    public String Is_Display_Boarding_pass;

    @SerializedName("Baggage_Number")
    /**行李追蹤資訊*/
    public ArrayList<CIBaggageInfoNumEntity> BaggageInfoNum;

    public CIPassengerListResp_PaxInfo(){

        bHaveCPR = false;
        CPR_Is_Check_In = false;
        CPR_Is_Do_Check_In = false;
        CPR_Is_Do_Cancel_Check_In = false;
        CPR_Is_Change_Seat = false;
        CPR_Is_Black = false;
        CPR_Boarding_Pass = "";

        Meal = new CIPassengerListResp_Meal();
    }
}


