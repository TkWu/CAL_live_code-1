package ci.ws.Models.entities;

/**
 * Created by kevincheng on 2016/5/19.
 * 文件：CI_APP_API_MealSelection.docx on 16/05/18 20:53
 * 4.5 DeleteOrderMeal
 * 新增餐點訂單的Request資料
 */
public class CIDeleteOrderMealReq {

    /**訂位代號(Len :6)*/
    public String pnr_Id;

    /**行程序號*/
    public String itinerary_seq;

    /**SSR序號*/
    public String ssr_seq;

    /**訂單號碼(Len :6)*/
    public String pono_num;

    /**手機平台：IOS/ANDROID */
    public String platform;

    /**回傳結果的語言
     * zh-TW:繁體中文／zh-CN:簡體中文／ja-JP:日文／en-US:英文
     * */
    public String language;

    /**user ip */
    public String client_ip;

    /**API版本,參閱版本內容(Release Contents)*/
    public String version;

    public CIDeleteOrderMealReq(){
        pnr_Id          = "";
        itinerary_seq   = "";
        ssr_seq         = "";
        pono_num        = "";
        platform        = "";
        language        = "";
        client_ip       = "";
        version         = "";
    }
}
