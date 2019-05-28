package ci.ws.Models.entities;

import ci.function.Core.CIApplication;
import ci.ui.object.CILoginInfo;

/**
 * Created by kevincheng on 2017/7/14.
 * Doc. : CI_APP_API_AddFQTV.docx
 */
@SuppressWarnings("serial")
public class CIAddMemberCardReq {

    /**行程的PNR*/
    public String Pnr_id;

    /**目前的航段編號*/
    public String Pnr_Seq;

    /**這個是該行程該乘客在 PNR，
     * 非cpr時取得的pnr number，
     * 可參照選餐應該也有用到這個參數*/
    public String Pax_Number;

    /**目前登入的會員卡號*/
    public String Card_Id;

    /**目前使用者名字*/
    public String First_Name;

    /**目前使用者名字*/
    public String Last_Name;

    /**
     * zh-TW:繁體中文／zh-CN:簡體中文／ja-JP:日文／en-US:英文
     * */
    public String language;

    /**版本*/
    public String Version;

    public CIAddMemberCardReq(){
        CILoginInfo info = new CILoginInfo(CIApplication.getContext());
        Pnr_id      = "";
        Pnr_Seq     = "";
        Pax_Number  = "";
        Card_Id     = info.GetUserMemberCardNo();
        First_Name  = info.GetUserFirstName();
        Last_Name   = info.GetUserLastName();
        language    = CIApplication.getLanguageInfo().getWSLanguage();
        Version     = "1.0.0.0";
    }
}
