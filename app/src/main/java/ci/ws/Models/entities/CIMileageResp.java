package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

/**
 * 里程管理 總里程數 回傳結果
 * Created by jlchen on 16/5/11.
 */
public class CIMileageResp {

    /**總累積哩程數*/
    @Expose
    public String mileage;

    /**會員卡號*/
    @Expose
    public String card_no;
}
