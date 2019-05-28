package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

/**
 * Created by Ryan on 16/3/30.
 * 註冊Response
 */
public class CISignUpResp {

    /**會員卡號*/
    @Expose
    public String card_no;

    /**會員英文名*/
    @Deprecated
    public String first_name;
    /**會員英文姓氏*/
    @Deprecated
    public String last_name;
    /**會員卡別
     * DYNA：華夏卡
     * GOLD：金卡
     * EMER：翡翠卡
     * PARA：晶鑽卡
     * {@link ci.ws.define.CICardType}*/
    @Deprecated
    public String card_type;
    /**卡片有效日期*/
    @Deprecated
    public String card_vaild_date;



    public CISignUpResp(){
        this.card_no = "";
        this.first_name = "";
        this.last_name  = "";
        this.card_type = "";
        this.card_vaild_date = "";
    }
}
