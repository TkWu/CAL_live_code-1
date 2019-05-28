package ci.ws.Models.entities;


/**
 * Created by kevincheng on 2016/5/16.
 * 哩程補登頁面使用
 */
public class CIInquiryAwardRecordReq {
    /**使用Login時回傳的Token*/
    public String   login_token;
    /**會員卡號*/
    public String   card_no;
    /**查詢年份*/
    public String   year;
    /**回傳結果語言(zh-TW:繁體中文／zh-CN:簡體中文／ja-JP:日文／en-US:英文)*/
    public String   culture_info;
    /**須能辨識設備之唯一性，長度限制為固定32碼*/
    public String   device_id;
    /**API版本*/
    public String   version;

    public CIInquiryAwardRecordReq(){
        login_token = "";
        card_no = "";
        year = "";
        culture_info = "";
        device_id = "";
        version = "";
    }


}
