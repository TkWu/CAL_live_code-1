package ci.ws.Models.entities;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Ryan on 16/5/4.
 */

@DatabaseTable(tableName = "tbl_MyAPISs")
public class CIApisEntity implements Cloneable {

    @DatabaseField(id = true, canBeNull = false, useGetSet = true)
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public void setId(String strCardNo, String strDocType) {
        if( TextUtils.isEmpty(strCardNo) || TextUtils.isEmpty(strDocType) ) {
            return ;
        }

        StringBuffer sb = new StringBuffer();
        this.id = sb.append(strCardNo).append(":").append(strDocType).toString();
    }

    public String getId() {
        return id;
    }


    /**男性*/
    public static final String SEX_MALE     = "M";
    /**女性*/
    public static final String SEX_FEMALE   = "F";

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
    @DatabaseField
    @Expose
    public String last_name;

    /**英文名字*/
    @DatabaseField
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

    @Override
    public Object clone() {
        try {
            return super.clone();
        }catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
