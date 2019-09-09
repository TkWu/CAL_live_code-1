package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Ryan on 16/5/3.
 */

public class CIBaseFlightStationODEntity implements Cloneable {

    /**generatedId = true 代表自動建立id*/
    @DatabaseField(generatedId = true)
    public int id;

    /**對應的出發地場站代號*/
    @DatabaseField
    public String departure_iata;

    /**出發地場站 isOriginal 欄位*/
    @DatabaseField
    public String isOriginal;

    /**目的地機場代號*/
    @DatabaseField
    @Expose
    public String arrival_iata;

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
