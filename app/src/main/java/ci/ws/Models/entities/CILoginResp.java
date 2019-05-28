package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

/**
 * Created by ryan on 16/3/25.
 * 此物件將使用 GSON直接解析Json Object
 * 部分參數, Server會回覆 null
 * 使用前請先判斷個member 是否為null
 */
public class CILoginResp {

    @Expose
    public String card_no;
    /**會員卡別
     * DYNA：華夏卡
     * GOLD：金卡
     * EMER：翡翠卡
     * PARA：晶鑽卡
     * {@link ci.ws.define.CICardType}*/
    @Expose
    public String card_type;
    @Expose
    public String cin_name;
    @Expose
    public String surname_cht;
    @Expose
    public String surname_en;
    @Expose
    public String first_name;
    @Expose
    public String last_name;
    @Expose
    public String email;
    @Expose
    public String mobile;
    @Expose
    public String home_tel;
    @Expose
    public String buss_tel;
    @Expose
    public String brth_date;
    @Expose
    public String member_token;
    /**總累積哩程數*/
    @Expose
    public String mileage;
    /**卡別到期日*/
    @Expose
    public String card_type_exp;


    public CILoginResp(){
        this.card_no    = "";
        this.card_type  = "";
        this.cin_name   = "";
        this.surname_cht= "";
        this.surname_en = "";
        this.first_name = "";
        this.last_name  = "";
        this.email      = "";
        this.mobile     = "";
        this.home_tel   = "";
        this.buss_tel   = "";
        this.brth_date  = "";
        this.member_token = "";
        this.mileage    = "";
        this.card_type_exp = "";
    }
}
