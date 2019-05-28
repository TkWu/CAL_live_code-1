package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.util.Locale;

/**
 * Created by joannyang on 16/5/23.
 */
public class CIApisNationalEntity {

    /** */
    @Expose
    public String country_cd;
    /** */
    @Expose
    public String resident_cd;
    /** */
    @Expose
    public String issue_cd;
    /** */
    @Expose
    public String name_tw;
    /** */
    @Expose
    public String name_cn;
    @Expose
    public String name_en;
    /** */
    @Expose
    public String name_jp;

    public String getCountryName(Locale locale) {
        if( null == locale ) {
            return null;
        }

        if(Locale.TAIWAN.equals(locale) ) {
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
