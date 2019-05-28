package ci.ws.Models.entities;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kevincheng on 2017/5/11.
 * Doc. : CA_app_questionnaire_20170503.docx
 */
@SuppressWarnings("serial")
public class CIPushQuestionnaireReq implements Cloneable ,Serializable {


    /**會員卡號*/
    public String cardid;

    /**題目答案, 以逗號格開的字串*/
    public String answers;

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

    public CIPushQuestionnaireReq(){
        cardid          = "";
        language        = "";
        answers         = "";
        departure       = "";
        departure_date  = "";
        arrival         = "";
        fltnumber       = "";
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
