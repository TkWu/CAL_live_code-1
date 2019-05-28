package ci.ws.Models.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * TripDetail - Passenger - Meal資料
 * Created by jlchen on 16/5/11.
 */
@SuppressWarnings("serial")
public class CIPassengerListResp_Meal implements Serializable {

    /**一般餐點，可進行APP選餐*/
    public static final String MEAL_TYPE_NORMAL = "1";
    /**特殊餐點，不能選餐，只能使用客服換餐*/
    public static final String MEAL_TYPE_SPECIAL= "2";

    /**訂位代號*/
    @SerializedName("pnr_id")
    public String pnr_id;

    /**行程序號*/
    @SerializedName("itinerary_seq")
    public String itinerary_seq;

    /**航班日期*/
    @SerializedName("flight_date")
    public String flight_date;

    /**航班編號*/
    @SerializedName("flight_num")
    public String flight_num;

    /**航班航段*/
    @SerializedName("flight_sector")
    public String flight_sector;

    /**PNR狀態 用於CheckFlightMealOpen request*/
    @SerializedName("tkt_confirm_code")
    public String tkt_confirm_code;

    /**處理結果代碼*/
    @SerializedName("rt_code")
    public String rt_code;

    /**處理結果說明*/
    @SerializedName("rt_msg")
    public String rt_msg;

    /**SSR序號
     * 用來取消餐點用的參數*/
    public int ssr_seq;

    /**訂單號碼Len :6
     * 用來取消餐點用的參數*/
    public String pono_number;

    /**餐點種類
     1:一般餐點 ->可以選餐
     2:特殊餐點 ->不能選餐*/
    public String meal_type;

    /**餐點資訊,
     * Array順序就是選餐順序*/
    public ArrayList<CIMealInfoEntity> meal_info;


    public CIPassengerListResp_Meal(){

        meal_info = new ArrayList<>();
    }
//    /**早餐 餐點資料*/
//    @SerializedName("breakfast_detail")
//    public CIPassengerListResp_MealDetail breakfast_detail;
//
//    /**午餐 餐點資料*/
//    @SerializedName("lunch_detail")
//    public CIPassengerListResp_MealDetail lunch_detail;
//
//    /**晚餐 餐點資料*/
//    @SerializedName("dinner_detail")
//    public CIPassengerListResp_MealDetail dinner_detail;
}
