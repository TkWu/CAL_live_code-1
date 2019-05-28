package ci.ws.Models.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by kevincheng on 2017/9/11.
 */

public class BaggageEntity implements Serializable {

    /**EB:超重行李加購*/
    @SerializedName("SERVICETYPE")
    public String servicetype;

    /**EMD票號*/
    @SerializedName("EMDNO")
    public String emdno;

    /**Y:已使用, N:未使用*/
    @SerializedName("STATUS")
    public String status;

    /**機票號碼*/
    @SerializedName("TICKETNO")
    public String ticketno;

    /**名( Len: Max. 30)*/
    @SerializedName("LASTNAME")
    public String lastname;

    /**姓( Len: Max. 30)*/
    @SerializedName("FIRSTNAME")
    public String firstname;


    /***/
    @SerializedName("ebTktNum")
    public String ebtktnum;

    /**購買日期*/
    @SerializedName("PURCHASE_DATE")
    public String purchase_date;

    public static final String EXWG = "EXWG";
    public static final String EXPC = "EXPC";

    /**超額行李單位
     EXWG: 重量
     EXPC: 件數*/
    @SerializedName("SSRTYPE")
    public String ssrtype;

    @SerializedName("SSRAMOUNT")
    /**加購行李重量/件數*/
    public String ssramount;

    /**金額*/
    @SerializedName("EBAMOUNT")
    public String ebamount;

    /**幣別*/
    @SerializedName("EBCURRENCY")
    public String ebcurrency;

    /**適用航班*/
    @SerializedName("FLIGHT_INFO")
    public FlightInfoList Flight_Info;

    public static class FlightInfo implements Serializable {
        /**航班編號*/
        @SerializedName("FLIGHT_NUM")
        public String flight_num;

        /**航班日期*/
        @SerializedName("FLIGHT_DATE")
        public String flight_date;
    }

}
