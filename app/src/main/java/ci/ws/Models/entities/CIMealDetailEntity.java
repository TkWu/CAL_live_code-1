package ci.ws.Models.entities;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ryan on 2016/9/27.
 */

public class CIMealDetailEntity  implements Serializable {

    /** 是否有此餐點的資訊
     * 當arCommonList, arMenuOnlyList都為null, or 空, 此Flag=false表示沒有該餐點資訊
     * */
    public Boolean bIsHaveMealInfo;

    /**餐點順序
     * 等同選餐順序*/
    public String meal_seq;

    /**餐點類型編號 ; 格式: Length=1 ; B:早餐/L:午餐/D:晚餐/R:便餐/S:特殊餐*/
    public String mealtype_code;

    /**一般餐點*/
    public ArrayList<CIMealInfoEntity> arCommonList;
    /**特殊餐點*/
    public ArrayList<CIMealInfoEntity> arMenuOnlyList;

    public CIMealDetailEntity(){

        arCommonList = new ArrayList<CIMealInfoEntity>();
        arMenuOnlyList = new ArrayList<CIMealInfoEntity>();

        bIsHaveMealInfo = false;
    }
}
