package ci.ws.Models.entities;

import ci.function.Core.CIApplication;

/**
 * Created by kevincheng on 2017/5/11.
 * Doc. : CA_app_questionnaire_20170503.docx
 */
@SuppressWarnings("serial")
public class CIPullQuestionnaireReq {

    /**行程的PNR*/
    public String PNR;

    /**推播所帶token*/
    public String token;

    /**會員卡號*/
    public String cardid;

    /**班機起飛站，ex: TPE*/
    public String departure;

    /**班機起飛日期，ex: 2017-04-23*/
    public String departure_date;

    /**班機起飛時間，ex: 13:50*/
    public String arrival;

    /**班機班號，ex: CI0100*/
    public String fltnumber;

    /**
     * zh-TW:繁體中文／zh-CN:簡體中文／ja-JP:日文／en-US:英文
     * */
    public String language;

    /**
     * app版本
     * */
    public String appver;

    public CIPullQuestionnaireReq(){
        PNR             = "";
        token           = "";
        cardid          = "";
        departure       = "";
        departure_date  = "";
        arrival         = "";
        fltnumber       = "";
        language        = "";
        appver          = CIApplication.getVersionName();
    }
}
