package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by ryan on 16/5/2.
 */
@DatabaseTable(tableName = "NationalList")
public class CINationalEntity implements Cloneable {

    /**generatedId = true 代表自動建立id*/
    @DatabaseField(generatedId = true)
    public int id;


    /**國碼*/
    @Expose
    @DatabaseField
    public String country_cd;

    /**國家名稱*/
    @Expose
    @DatabaseField
    public String country_name;

    /**國家名稱英文*/
    //@Expose
    //@DatabaseField
    //public String country_name_e;

    /**電話國碼*/
    @Expose
    @DatabaseField
    public String country_phone_cd;


    /**三碼的代碼, 居住國家, 發證國家帶給APIS WS用的*/
    @Expose
    @DatabaseField
    public String resident_country_cd;


    /**法定成人年齡*/
    @Expose
    @DatabaseField
    public String legal_adult_age;


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
