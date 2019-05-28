package ci.ws.Models.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KevinCheng on 17/9/12.
 * Doc. : CI_APP_API_AddExcessBaggage_v2.docx
 * 功能說明：查詢加購行李價格。
 */
public class CIInquiryExcessBaggageInfoResp implements Serializable{
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
     * 乘客資訊
     */
    @SerializedName("pax_info")
    public ArrayList<PaxInfo> Pax_info;

    public static class PaxInfo implements Serializable{


        /**
         * 乘客編號
         */
        @SerializedName("paxNum")
        public String pax_num;

        /**
         * 超額行李單位
         * EXWG: 重量
         * EXPC: 件數
         */
        @SerializedName("ssrType")
        public String ssrType;

        /**
         * 已加購行李重量/件數
         */
        @SerializedName("ssrAmount")
        public String ssrAmount;

        /**
         * 金額
         */
        @SerializedName("ebAmount")
        public String ebAmount;

        /**
         * 幣別
         */
        @SerializedName("ebCurrency")
        public String ebCurrency;

        /**
         *  自定義tag
         *  名字
         */
        public String first_name;


        /**
         *  自定義tag
         *  姓氏
         */
        public String last_name;

    }

}
