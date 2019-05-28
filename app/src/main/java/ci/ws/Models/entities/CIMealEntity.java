package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;
/**
 * Created by kevincheng on 2016/5/5.
 */
public class CIMealEntity {

        /**餐點名稱*/
        @Expose
        public String meal_name;
        /**餐型代碼*/
        @Expose
        public String meal_code;
        /**餐點英文名稱*/
        @Expose
        public String meal_name_e;
        /**餐點簡中名稱*/
        @Expose
        public String meal_name_cn;
        /**餐點日文名稱*/
        @Expose
        public String meal_name_jp;
}