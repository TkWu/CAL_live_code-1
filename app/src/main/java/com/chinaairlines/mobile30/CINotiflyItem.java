package com.chinaairlines.mobile30;

import java.io.Serializable;

/**
 * Created by Ryan on 16/7/7.
 */
public class CINotiflyItem implements Serializable{

    public static final String NOTIFY_INFO = "NotifyInfo";

    /**Promotion - 確認推播*/
    public static final String TYPE_EWALLET             = "ewt_cfm";
    /**重大事件推播*/
    public static final String TYPE_EMERGENCY_MSG       = "emr_msg";
    /**Promotion - 促銷*/
    public static final String TYPE_PROMOTION_SELL      = "pro_sel";
    /**Promotion - 兌換里程*/
    public static final String TYPE_PROMOTION_VIP       = "pro_vip";
    /**Promotion - 兌換里程*/
    public static final String TYPE_PROMOTION_WIFI      = "pro_wif";
    /**航班異動 - 改班/改時*/
    public static final String TYPE_FLIGHT_CHANGE       = "flt_chg";
    /**航班異動 - 改登機門*/
    public static final String TYPE_FLIGHT_GATE_CHANGE  = "flt_gte";
    /**航班異動 - 取消*/
    public static final String TYPE_FLIGHT_CANCEL       = "flt_can";
    /**問卷調查*/
    public static final String TYPE_QUESTIONNAIRE       = "flt_qus";

    /**類型*/
    public String type;
    /**link*/
    public String url;
    /**訊息*/
    public String msg;
    /**推播資料 */
    public Data data;
    /**推播訊息*/
    public String message;

    /**訊息id*/
    public String msg_id;

    public static class Data implements Serializable{
        /**問卷版本*/
        public String version;
        /**涵航班訊息的問卷答案唯一index*/
        public String token;
    }
}
