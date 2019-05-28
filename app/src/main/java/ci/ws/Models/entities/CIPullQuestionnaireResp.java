package ci.ws.Models.entities;

import java.util.ArrayList;

import ci.function.Core.CIApplication;

/**
 * Created by kevincheng on 2017/5/11.
 * Doc. : CA_app_questionnaire_20170503.docx
 */
@SuppressWarnings("serial")
public class CIPullQuestionnaireResp {

    /**問卷版本*/
    public String version;

    /**華夏會員卡號(Len :9)*/
    public ArrayList<String> ques;

    /**問卷抬頭*/
    public String title;

    /**
     * 非API要求，主要用來在接收到responce之後，將要求的語言寫入
     * 以讓後續如果需要取用待提交的問卷時，方便辨識是否與app當下設定
     * 的語言不同，如果不同則重新要求題目
     * zh-TW:繁體中文／zh-CN:簡體中文／ja-JP:日文／en-US:英文
     * */
    public String language;

    public CIPullQuestionnaireResp(){
        version     = "";
        title       = "";
        ques = new ArrayList<>();
        language    = CIApplication.getLanguageInfo().getWSLanguage();
    }
}
