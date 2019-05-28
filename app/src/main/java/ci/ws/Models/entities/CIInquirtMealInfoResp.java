package ci.ws.Models.entities;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by ryan on 16/5/18.
 */
public class CIInquirtMealInfoResp implements Serializable {

//    /**早餐明細*/
//    public MealDetail breakfast;
//    /**午餐明細*/
//    public MealDetail lunch;
//    /**晚餐明細*/
//    public MealDetail dinner;

    /** 餐點對應表
     * Key=meal_seq, 餐點順序*/
    public HashMap<String, CIMealDetailEntity> mDetailMap;

    public CIInquirtMealInfoResp(){

//        breakfast   = new MealDetail();
//        lunch       = new MealDetail();
//        dinner      = new MealDetail();

        mDetailMap = new HashMap<>();
    }
}
