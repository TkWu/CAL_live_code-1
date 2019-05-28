package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by joannyang on 16/5/23.
 */
public class CIApisDocmuntTypeEntity implements Serializable {

    /**護照代碼*/
    @Expose
    public String code_1A;
    /**護照明稱*/
    @Expose
    public String name_tw;
    /**護照英文名稱*/
    @Expose
    public String name_cn;
    /**護照簡中名稱*/
    @Expose
    public String name_en;
    /**護照日文名稱*/
    @Expose
    public String name_jp;
    /**發證國家*/
    @Expose
    public String issued_country;

    public CIApisDocmuntTypeEntity( String strCode, String issued_country ){
        this.code_1A = strCode;
        this.issued_country = issued_country;
    }

    public String getName(Locale locale) {

        if( null == locale ) {
            return "";
        }

        if(Locale.TAIWAN.equals(locale)) {
            return name_tw;
        } else if( Locale.CHINA.equals(locale) ) {
            return name_cn;
        } else if( Locale.ENGLISH.equals(locale) ) {
            return name_en;
        } else if( Locale.JAPAN.equals(locale) ) {
            return name_jp;
        } else {
            return null;
        }

    }
}
