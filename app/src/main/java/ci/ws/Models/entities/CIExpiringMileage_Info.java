package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

/**
 * 里程管理 里程期限 回傳結果
 * Created by jlchen on 16/5/11.
 */
public class CIExpiringMileage_Info implements Cloneable {

    /**到期日*/
    @Expose
    public String expire_date;

    /**哩程數*/
    @Expose
    public String mileage;

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
