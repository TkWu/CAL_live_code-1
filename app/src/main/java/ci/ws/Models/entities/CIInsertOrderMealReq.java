package ci.ws.Models.entities;

/**
 * Created by kevincheng on 2016/5/19.
 * 文件：CI_APP_API_MealSelection.docx on 16/05/18 20:53
 * 4.4 InsertOrderMeal
 * 新增餐點訂單的Request資料
 */
public class CIInsertOrderMealReq {

    /**訂位代號(Len :6)*/
    public String pnr_Id;

    /**行程序號*/
    public String itinerary_seq;

    /**乘客序號*/
    public String pax_seq;

    /**乘客子序號*/
    public String pax_subseq;

    /**乘客搭乘艙別，僅C艙可選餐 F/C/Y */
    public String pax_seat_class;

    /**訂單明細 (參考訂單明細欄位補充)，依InquiryMealInfo取得的m
     * eal_seq決定明細順序,格式範例為
     * meal_content_seq1, meal_code1, mealtype_code1;
     * meal_content_seq2, meal_code2, mealtype_code2;
     * meal_content_seq3, meal_code3, mealtype_code3;
     * ex: 若僅㦘一道餐:C,CLDMCWS000040,L;N,N,N;N,N,N;
     * ex: meal_seq為2: N,N,N; C,CLDMCWS000040,L;N,N,N;
     * meal_content_seq 餐點內容代號 ; 格式: Length=1 ; A/B/C
     * meal_code        餐點餐型編號 ; 格式: Length=13
     * mealtype_code    餐點類型編號 ; 格式: Length=1 ; B:早餐/L:午餐/D:晚餐
     * */
    public String meal_detail;

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

    public CIInsertOrderMealReq(){
        pnr_Id          = "";
        itinerary_seq   = "";
        pax_seq         = "";
        pax_subseq      = "";
        pax_seat_class  = "";
        meal_detail     = "";
        platform        = "";
        language        = "";
        client_ip       = "";
        version         = "";
    }
}
