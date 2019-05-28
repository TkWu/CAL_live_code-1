package ci.ws.Models.entities;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Locale;

/**
 * Created by joannyang on 16/5/30.
 */
@DatabaseTable(tableName = "tbl_CompanionsAPISs")
public class CICompanionApisEntity implements Cloneable {

    @DatabaseField( id = true, canBeNull = false ,useGetSet = true)
    public String id ;

    /** Primary key ( full_name,card_no, doc_type )*/
    public void setId(String id) {
        this.id = id;
    }

    public void setId(String strFullName, String strCardNo, String strDocType) {
        if( TextUtils.isEmpty(strFullName) || TextUtils.isEmpty(strCardNo) || TextUtils.isEmpty(strDocType) ) {
            return;
        }

        StringBuffer sb = new StringBuffer();
        this.id = sb.append(strFullName).append(":").append(strCardNo).append(":").append(strDocType).toString();
    }

    public String getId() {
        return this.id;
    }

    /**同行者id ( first name(大寫去空白) + last name(大寫去空白) */
    @DatabaseField(canBeNull = false)
    public String full_name;

//    /**男性*/
//    public static final String SEX_MALE     = "M";
//    /**女性*/
//    public static final String SEX_FEMALE   = "F";

    /**旅行證件類別,參考xxx,P=護照*/
    @DatabaseField(canBeNull = false)
    @Expose
    public String doc_type;

    /**證件號碼*/
    @DatabaseField(canBeNull = false)
    @Expose
    public String doc_no;

    /**國籍代碼*/
    @DatabaseField
    @Expose
    public String nationality;

    /**證件效期*/
    @DatabaseField
    @Expose
    public String doc_expired_date;

    /**發證國家*/
    @DatabaseField
    @Expose
    public String issue_country;

    /**居住地國家*/
    @DatabaseField
    @Expose
    public String resident_city;

    /**英文姓*/
    @DatabaseField(canBeNull = false)
    @Expose
    public String last_name;

    /**英文名字*/
    @DatabaseField(canBeNull = false)
    @Expose
    public String first_name;

    /**生日(2016-07-03)*/
    @DatabaseField
    @Expose
    public String birthday;

    /**性別性別(Male :M /Female: F)*/
    @DatabaseField
    @Expose
    public String sex;

    /**完整地址(僅Inquiry 會提供, Insert/Update不用帶)*/
    @DatabaseField
    @Expose
    public String aadr;

    /**地址-街名*/
    @DatabaseField
    @Expose
    public String addr_street;

    /**地址-城市*/
    @DatabaseField
    @Expose
    public String addr_city;

    /**地址-洲*/
    @DatabaseField
    @Expose
    public String addr_state;

    /**地址-國家*/
    @DatabaseField
    @Expose
    public String addr_country;

    /**地址-郵遞區號*/
    @DatabaseField
    @Expose
    public String addr_zipcode;

    /**使用者會員卡號 */
    @DatabaseField(canBeNull = false)
    public String card_no;

}
