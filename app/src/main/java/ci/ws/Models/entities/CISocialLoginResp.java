package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

/**
 * Created by Ryan on 2016/11/15.
 */

public class CISocialLoginResp {

    @Expose
    public String flag;
    @Expose
    public String open_id_kind;
    @Expose
    public String mileage;
    @Expose
    public String card_type;
    @Expose
    public String open_id;
    @Expose
    public String last_name;
    @Expose
    public String card_no;
    @Expose
    public String first_name;
    /**卡別到期日*/
    @Expose
    public String card_type_exp;
    @Expose
    public String email;
}
