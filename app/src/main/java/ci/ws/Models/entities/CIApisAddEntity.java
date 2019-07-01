package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

public class CIApisAddEntity implements Cloneable {

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

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

    /**
     * API MODE
     */
    @Expose
    public String mode = "";

    /**
     * API MODE
    */
    @Expose
    public String language = "";


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