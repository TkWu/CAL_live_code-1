package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

/**
 * 里程活動 查詢已兌換明細 的回傳結果
 * Created by jlchen on 16/5/16.
 */
public class CIRedeemRecord_Info implements Cloneable {

    //哩程類別 [flight_item] Tag
    //座艙升等
    public static final String FLIGHT_ITEM_UPGRADE_AWARDS   = "1";
    //酬賓機票
    public static final String FLIGHT_ITEM_TICKET_AWARDS    = "2";
    //酬賓獎項轉讓
    public static final String FLIGHT_ITEM_AWARDS_TRANSFER  = "3";

    //獎項種類 [bnsusg] Tag
    //升等（Upgrade Award)
    public static final String BNSUSG_UPGRADE_AWARD         = "UP";
    //免票（Upgrage Ticket)
    public static final String BNSUSG_UPGRADE_TICKET        = "TK";
    //超重行李（Excess Baggage）
    public static final String BNSUSG_EXCESS_BAGGAGE        = "EB";
    //VIP（VIP Lounge Useage）
    public static final String BNSUSG_VIP_LOUNGE_USEAGE     = "VL";
    //其他（種類名稱，請參照award.desc）
    public static final String BNSUSG_OTHER                 = "OTHER";

    /**哩程類別(1座艙升等/2酬賓機票/3酬賓獎項轉讓)*/
    @Expose
    public String flight_item;

    /*哩程名稱(座艙升等/酬賓機票/酬賓獎項轉讓)*/
    @Expose
    public String flight_desc;

    /**獎項到期日(YYYY-MM-DD)*/
    @Expose
    public String flight_duedate;

    /**核獎號碼(9位數字)*/
    @Expose
    public String flight_awdno;

    /**兌換日期(YYYY-MM-DD)*/
    @Expose
    public String flight_date;

    /**哩程數*/
    @Expose
    public String flight_mileage;

    /**備註(未使用/機票發行)*/
    @Expose
    public String flight_remark;

    /**機票號碼*/
    @Expose
    public String ticket;

    /**受讓人名稱*/
    @Expose
    public String nominee;

    /**受讓人卡號*/
    @Expose
    public String card_no;

    /**獎項種類(UP/TK/EB/VL/OTHER)*/
    @Expose
    public String bnsusg;

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
