package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

/**
 * Created by kevincheng on 16/5/3.
 * 查詢兌換里程紀錄的回應資料
 */
public class CIInquiryAwardRecordResp implements Cloneable {

    /**哩程類別(1酬賓獎項受讓)*/
    @Expose
    public String flight_item;

    /**哩程名稱(酬賓獎項受讓)*/
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

    /**備註(未使用/機票發行)*/
    @Expose
    public String flight_remark;

    /**轉讓人名稱*/
    @Expose
    public String nominator;

    /**轉讓人卡號*/
    @Expose
    public String card_no;

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
