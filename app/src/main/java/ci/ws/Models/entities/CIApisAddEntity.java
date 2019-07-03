package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

public class CIApisAddEntity implements Cloneable {
    /**
     * 男性
     */
    public static final String SEX_MALE = "M";

    /**
     * 女性
     */
    public static final String SEX_FEMALE = "F";

    /**
     * APIS info Object
     */
    @Expose
    public CIApisQryRespEntity apisInfo = new CIApisQryRespEntity();


    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

}