package ci.function.MealSelection.item;

import java.io.Serializable;
import java.util.ArrayList;

import ci.ws.Models.entities.CIMealInfoEntity;

/**
 * Created by Ryan on 16/9/12.
 */
public class CIMealResultEntity implements Serializable {

    /**名稱*/
    public String strName;

    /**選餐結果*/
    public ArrayList<CIMealInfoEntity> arMealInfoList;

    public CIMealResultEntity(){

        strName = "";

        arMealInfoList = new ArrayList<>();
    }
}
