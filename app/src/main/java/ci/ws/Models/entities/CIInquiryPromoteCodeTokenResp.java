package ci.ws.Models.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kevincheng on 2017/5/11.
 * Doc. : CA_app_questionnaire_20170503.docx
 */
@SuppressWarnings("serial")
public class CIInquiryPromoteCodeTokenResp {

    /**問卷版本*/
    @SerializedName("rt_token")
    public String token;


    public CIInquiryPromoteCodeTokenResp(){
        token = "";
    }
}
