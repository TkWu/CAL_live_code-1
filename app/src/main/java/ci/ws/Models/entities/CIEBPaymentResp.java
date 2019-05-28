package ci.ws.Models.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KevinCheng on 17/9/12.
 * Doc. : CI_APP_API_AddExcessBaggage_v2.docx
 * 功能說明：加購行李付款。
 */
public class CIEBPaymentResp implements Serializable{
    /**
     * 訂位代號
     */
    public String pnr_id;

    /**
     * 行程序號
     */
    public String pnr_seq;

    /**
     * 總價幣別
     */
    public String ttl_currency;


    /**
     * 總價金額
     */
    public String ttl_amount;

    /**
     * 起站
     */
    public String departure_station;

    /**
     * 迄站
     */
    public String arrival_station;


    /**
     * 加購日期
     */
    public String purchase_date;


    /**
     * 乘客資訊
     */
    @SerializedName("pax_info")
    public ArrayList<PaxInfo> Pax_Info;

    public static class PaxInfo implements Serializable{


        /**
         * 乘客編號
         */
        public String pax_num;

        /**
         * 名( Len: Max. 30)
         */
        public String first_name;

        /**
         * 姓( Len: Max. 30)
         */
        public String last_name;

        /**
         * 超額行李單位
         * EXWG: 重量
         * EXPC: 件數
         */
        public String ssrType;

        /**
         * 已加購行李重量/件數
         */
        public String ssrAmount;

        /**
         * 金額
         */
        public String ebAmount;

        /**
         * 幣別
         */
        public String ebCurrency;

        /**
         * EMD票號
         */
        public String emd_num;

        /**
         * 機票號碼
         */
        public String ticket_num;

        /**
         * 飛航資訊
         */
        public ArrayList<FlightInfo> flight_info;


    }

    public static class FlightInfo implements Serializable{
        /**
         * 航班編號
         */
        public String flight_num;

        /**
         * 航班日期
         */
        public String flight_date;

    }
}
