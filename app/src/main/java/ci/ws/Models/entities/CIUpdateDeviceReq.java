package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class CIUpdateDeviceReq {

    /**
     * 會員卡號
     */
    public String customerid;

    /***/
    public String appname;

    /**Android Id*/
    public String deviceid;

    /**
     * 手機時區
     */
    public String timezone;

    /**推播Type*/
    public String pushtokentype;

    /**
     * 推播token
     */
    public String pushtoken;

    /**
     推播開關
     1111
     四位字串,
     1為開啟接收，0為關閉
     因目前推播開關未分類，統一回傳 1111 或是 0000
     */
    public String notfiswitch;

    /**語系*/
    public String applanguages;

    /**版本*/
    public String appversion;

    /**
     * 各PNR內航班資訊
     */
    public ArrayList<CIFCM_Event> flights;


    public CIUpdateDeviceReq() {
        appname = "mobile30";
        customerid = "";
        deviceid = "";
        pushtoken = "";
        timezone = "";
        notfiswitch = "";
        ////實際是FCM，只是參數使用GCM
        pushtokentype = "GCM";
        applanguages = "";
        appversion = "";
    }

}
