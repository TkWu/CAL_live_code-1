package ci.ws.Models.entities;

import ci.function.Core.CIApplication;
import ci.ws.define.WSConfig;

/**
 * Created by KevinCheng on 17/9/12.
 * Doc. : CI_APP_API_AddExcessBaggage_v2.docx
 * 功能說明：信用卡付款頁。
 */
@SuppressWarnings("serial")
public class CICCPageReq {

    /**
     * zh-TW:繁體中文／zh-CN:簡體中文／ja-JP:日文／en-US:英文
     */
    public String language;

    /**
     * app版本
     */
    public String version;

    public CICCPageReq() {
        version     = WSConfig.DEF_API_VERSION;
        language    = CIApplication.getLanguageInfo().getWSLanguage();
    }

}
