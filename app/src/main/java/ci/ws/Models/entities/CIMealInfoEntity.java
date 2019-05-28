package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Ryan on 16/5/18.
 */
public class CIMealInfoEntity implements Serializable {

    public static final String MEALTYPE_BREAKFAST   = "B";
    public static final String MEALTYPE_LUNCH       = "L";
    public static final String MEALTYPE_DINNER      = "D";
    public static final String MEALTYPE_R           = "R";
    public static final String MEALTYPE_SPECIAL     = "S";

    /** 第幾道餐 (>0)*/
    @Expose
    public String meal_seq;

    /** 餐點內容代號 ; 格式: Length=1 ; A/B/C*/
    @Expose
    public String meal_content_seq;

    /** 餐點餐型編號 ; 格式: Length=13*/
    @Expose
    public String meal_code;

    /** 餐點類型編號 ; 格式: Length=1 ; B:早餐/L:午餐/D:晚餐/R:便餐/S:特殊餐*/
    @Expose
    public String mealtype_code;

    /** 餐點類型描述*/
    @Expose
    public String mealtype_desc;

    /** 菜單餐點內容*/
    @Expose
    public String meal_name;

    /**是否為獨享餐(true/false)*/
    //@Expose
    public boolean is_menu_only;
}
