package ci.ws.Models.entities;


/**
 * Created by kevincheng on 2016/5/16.
 * 哩程補登頁面使用
 */
public class CIReclaimMileageReq implements Cloneable{
    /**使用Login時回傳的Token*/
    public String   login_token;
    /**會員卡號*/
    public String   card_no;
    /**會員EMail*/
    public String   email;
    /**是否更新Email(True/False)*/
    public String   update_email_chk;
    /**啟程站 1 (3碼為英文字母大寫)*/
    public String   dep_city1;
    /**終點站 1 (3碼為英文字母大寫)*/
    public String   arr_city1;
    /**搭乘日期 1 (YYYY-MM-DD)*/
    public String   dep_date1;
    /**航空公司 1 (CI/AE)*/
    public String   cdc1;
    /**班機號碼 1 (4位數字，須以0補到4位數，如0052)*/
    public String   fno1;
    /**機票號碼 1 (297/803(公司代號)+10位數字(票號))*/
    public String   ticket_no1;
    /**啟程站 2 (3碼為英文字母大寫)*/
    public String   dep_city2;
    /**終點站 2 (3碼為英文字母大寫)*/
    public String   arr_city2;
    /**搭乘日期 2 (YYYY-MM-DD)*/
    public String   dep_date2;
    /**航空公司 2 (CI/AE)*/
    public String   cdc2;
    /**班機號碼 2 (4位數字，須以0補到4位數，如0052)*/
    public String   fno2;
    /**機票號碼 2 (297/803(公司代號)+10位數字(票號))*/
    public String   ticket_no2;
    /**回傳結果語言(zh-TW:繁體中文／zh-CN:簡體中文／ja-JP:日文／en-US:英文)*/
    public String   culture_info;
    /**須能辨識設備之唯一性，長度限制為固定32碼*/
    public String   device_id;
    /**API版本*/
    public String   version;

    public CIReclaimMileageReq(){
        login_token = "";
        card_no = "";
        email = "";
        update_email_chk = "";
        dep_city1 = "";
        arr_city1 = "";
        dep_date1 = "";
        cdc1 = "";
        fno1 = "";
        ticket_no1 = "";
        dep_city2 = "";
        arr_city2 = "";
        dep_date2 = "";
        cdc2 = "";
        fno2 = "";
        ticket_no2 = "";
        culture_info = "";
        device_id = "";
        version = "";
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
