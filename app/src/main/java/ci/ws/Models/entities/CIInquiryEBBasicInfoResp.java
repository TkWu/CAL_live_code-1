package ci.ws.Models.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KevinCheng on 17/9/12.
 * Doc. : CI_APP_API_AddExcessBaggage_v2.docx
 * 功能說明：加購行李-查詢旅客名單
 */
public class CIInquiryEBBasicInfoResp implements Serializable{
    /**
     * 訂位代號
     */
    public String pnr_id;

    /**
     * 行程序號
     */
    public String pnr_seq;

    /**
     * 起站
     */
    public String departure_station;

    /**
     * 迄站
     */
    public String arrival_station;

    /**
     * 超額行李單位
     * EXWG: 重量
     * EXPC: 件數
     */
    @SerializedName("EB_Type")
    public String ssrType;

    /**
     * 付款幣別
     */
    @SerializedName("EB_Currency")
    public String ebCurrency;

    /**
     * 乘客資訊
     */
    @SerializedName("Pax_info")
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
         * 是否可加購超額行李
         * Y: 可加購
         * N: 不可加購
         */
        public String is_add_excessBaggage;

        /**
         * 已加購行李重量/件數
         */
        public EbOption excessBaggage;

        /**
         * {加購行李重量/件數選項, 金額}
         */
        public ArrayList<EbOption> eb_option;

        /**
         * 不可加購訊息
         */
        public String msg;

    }

    public static class EbOption implements Serializable{


        /**
         * 加購行李重量/件數選項
         */
        public String kgAmt;

        /**
         * 金額
         */
        public String price;
    }
}
