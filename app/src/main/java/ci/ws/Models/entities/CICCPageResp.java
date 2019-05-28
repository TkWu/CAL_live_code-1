package ci.ws.Models.entities;

import java.io.Serializable;

/**
 * Created by KevinCheng on 17/9/12.
 * Doc. : CI_APP_API_AddExcessBaggage_v2.docx
 * 功能說明：信用卡付款頁。
 */
public class CICCPageResp implements Serializable{
    /**
     * 付款Token
     */
    public String Token;

    /**
     * 信用卡輸入頁網址
     */
    public String url;


}
