package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ryan on 16/5/18.
 */
public class CIMealPassenagerEntity implements Serializable {

    /** 乘客序號*/
    @Expose
    public int pax_seq;

    /** 乘客子序號*/
    @Expose
    public int pax_subseq;

    /** 乘客名*/
    @Expose
    public String pax_first_name;

    /**乘客姓*/
    @Expose
    public String pax_last_name;

    /** SSR序號*/
    @Expose
    public int ssr_seq;

    /**訂單編號*/
    @Expose
    public String pono_number;

//    /**早餐明細*/
//    @Expose
//    public CIMealDetail breakfast_detail;
//
//    /**午餐明細*/
//    @Expose
//    public CIMealDetail lunch_detail;
//
//    /**晚餐明細*/
//    @Expose
//    public CIMealDetail dinner_detail;

    @Expose
    /** 餐點類型編號 ; 格式: Length=1 ; B:早餐/L:午餐/D:晚餐/R:便餐/S:特殊餐*/
    public String meal_type;

    @Expose
    /**餐點資訊，list內容的順序就是餐點選餐順序*/
    public ArrayList<CIMealInfoEntity> meal_info;

    /**存放選餐失敗的錯誤訊息*/
    public String strErrorMsg;

    public CIMealPassenagerEntity(){
        pax_seq     = 0;
        pax_subseq  = 0;
        pax_first_name  = "";
        pax_last_name   = "";
        ssr_seq = 0;
        pono_number = "";
//        breakfast_detail= new CIMealDetail();
//        lunch_detail    = new CIMealDetail();
//        dinner_detail   = new CIMealDetail();

        //
        meal_info = new ArrayList<>();
        strErrorMsg = "";
    }
}
