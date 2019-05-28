package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by kevincheng on 16/5/20.
 * 文件：CI_APP_API_EWAllet.docx 16/05/19 14:47
 * 4.1.1 InquiryExtraServiceByPNRNoSIT
 */
@SuppressWarnings("serial")
public class CIEWallet_ExtraService_Info implements Serializable, Cloneable {

    /**名( Len: Max. 30)*/
    @Expose
    public String FIRSTNAME;

    /**姓( Len: Max. 30)*/
    @Expose
    public String LASTNAME;

    /**機票號碼*/
    @Expose
    public String TICKETNO;

    /**T
     *  HSR:高鐵,
     *  VIP:VIP,
     *  WIFI:Wifi,
     *  FamilyCouch: 親子臥艙加購,
     *  ExtraBaggage:超重行李加購(樂購)
     *  EB:超重行李加購
     *  */
    @Expose
    public String SERVICETYPE;

    /**出發站別(僅THSR) */
    @Expose
    public String THSRDEPSTN;

    /**高鐵訂位代號(僅THSR)*/
    @Expose
    public String THSRBOOKINGNO;

    /**車次，車艙(僅THSR) */
    @Expose
    public String THSRINFO;

    /**高鐵授權碼(僅THSR)*/
    @Expose
    public String THSRAUTHNO;

    /**高鐵出發時間(僅THSR)*/
    @Expose
    public String THSRDEPTIME;

    /**獎項號碼(僅VIP) */
    @Expose
    public String AWARDNO;

    /**出發航班(僅VIP及FamilyCouch)*/
    @Expose
    public String FLIGHTNO;

    /**出發日期(僅VIP) */
    @Expose
    public String FLIGHTDATE;

    /**貴賓室使用機場(僅VIP)*/
    @Expose
    public String LOUNGEAIRPORT;

    /**貴賓室使用航廈(僅VIP) */
    @Expose
    public String LOUNGETERMINAL;

    /**EMD票號,收據號碼(僅FamilyCouch)*/
    @Expose
    public String EMDNO;

    /**親子臥艙座位號(僅FamilyCouch)*/
    @Expose
    public String FAMILYSEATNO;

    /**出發時間(僅FamilyCouch)*/
    @Expose
    public String DEPTIME;

    /**會員卡號(僅Wifi)*/
    @Expose
    public String DYNASTYCARDNO;

    /**Wifi Item(僅Wifi)*/
    @Expose
    public String WIFIITEM;

    /**WiFi帳號(僅Wifi)*/
    @Expose
    public String WIFIID;

    /**WiFi密碼, (僅Wifi) */
    @Expose
    public String WIFIPASSWORD;

    /**WIFI效期(僅Wifi) */
    @Expose
    public String WIFIEXPIRYDATE;

    /**
     * 行李超重資訊
     * TTL20KG0PC-為加購20公斤重量，0PC為0件數
     * */
    @Expose
    public String BGWDINFO;

    /**Y:已使用, N:未使用*/
    @Expose
    public String STATUS;

    /**VIP室名稱(僅VIP)*/
    @Expose
    public String VIPNAME;

    /**機場名稱(僅VIP)*/
    @Expose
    public String AIRPORTNAME;

    /**貴賓室使用航廈(僅VIP) */
    @Expose
    public String AIRPORTTERMINAL;

    /**購買日期*/
    @Expose
    public String PURCHASE_DATE;

    /**超額行李單位
     EXWG: 重量
     EXPC: 件數*/
    @Expose
    public String SSRTYPE;

    /**加購行李重量/件數*/
    @Expose
    public String SSRAMOUNT;

    /**金額*/
    @Expose
    public String EBAMOUNT;

    /**幣別*/
    @Expose
    public String EBCURRENCY;

    /**適用航班*/
    @Expose
    @SerializedName("FLIGHT_INFO")
    public FlightInfoList Flight_Info;

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
