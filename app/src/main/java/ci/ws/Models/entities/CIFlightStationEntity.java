package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Ryan on 16/5/3.
 */
@DatabaseTable(tableName = "CIFS_StationList")
public class CIFlightStationEntity implements Cloneable {


    /**generatedId = true 代表自動建立id*/
    @DatabaseField(generatedId = true)
    public int id;

    /**IATA Code 機場代號*/
    @DatabaseField
    @Expose
    public String IATA;

    /**英文名*/
    @DatabaseField
    @Expose
    public String name;

    /**所屬區域*/
    @DatabaseField
    @Expose
    public String localization_name;

    /**機場區域代碼*/
    @DatabaseField
    @Expose
    public String area;

    /**機場區域名稱*/
    @DatabaseField
    @Expose
    public String area_name;

    /**機場所在洲*/
    @DatabaseField
    @Expose
    public String continent;

    /**機場所在城市*/
    @DatabaseField
    @Expose
    public String city;

    /**機場所在國家*/
    @DatabaseField
    @Expose
    public String country;

    /**機場名稱*/
    @DatabaseField
    @Expose
    public String airport_name;

    /**經度*/
    @DatabaseField
    @Expose
    public String longitude;

    /**緯度*/
    @DatabaseField
    @Expose
    public String latitude;

    /**該場站是否可以onLine Check-In*/
    @DatabaseField
    @Expose
    public String is_vcheckin;

    /**該廠站是否可以列印線上登機證*/
    @DatabaseField
    @Expose
    public String is_vpass;

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
