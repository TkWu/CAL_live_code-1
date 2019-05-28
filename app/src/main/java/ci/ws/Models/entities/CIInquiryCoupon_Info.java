package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by jlchen on 2016/6/16.
 */
@SuppressWarnings("serial")
public class CIInquiryCoupon_Info implements Serializable, Cloneable {

    /**Excluded項目*/
    @Expose
    public String ExcludedItem;

    /**有效期限*/
    @Expose
    public String ExpiryDate;

    /**Coupon名稱*/
    @Expose
    public String Title;

    /**BarCode 圖片*/
    @Expose
    public String BarCodeImage;

    /**訊息 圖片*/
    @Expose
    public String InformationImage;

    /**折扣數(10%或-30)*/
    @Expose
    public String Discont;

    /**折扣單位(幣別)(off為不需顯示)*/
    @Expose
    public String DiscontUnit;

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
