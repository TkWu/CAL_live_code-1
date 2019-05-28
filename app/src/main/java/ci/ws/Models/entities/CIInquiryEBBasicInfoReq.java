package ci.ws.Models.entities;

import ci.function.Core.CIApplication;
import ci.ws.define.WSConfig;

/**
 * Created by KevinCheng on 17/9/12.
 * Doc. : CI_APP_API_AddExcessBaggage_v2.docx
 * 功能說明：加購行李-查詢旅客名單
 */
@SuppressWarnings("serial")
public class CIInquiryEBBasicInfoReq {
    /**
     * 訂位代號( Len: 6)
     */
    public String pnr_id;

    /**
     * 航段編號(PK)(Len: 4)
     */
    public String pnr_seq;

    /**
     * 旅客編號(Len: 4)
     */
    public String pax_num;

    /**
     * zh-TW:繁體中文／zh-CN:簡體中文／ja-JP:日文／en-US:英文
     */
    public String language;

    /**
     * app版本
     */
    public String version;

    public CIInquiryEBBasicInfoReq() {
        pnr_id      = "";
        pnr_seq     = "";
        pax_num     = "";
        version     = WSConfig.DEF_API_VERSION;
        language    = CIApplication.getLanguageInfo().getWSLanguage();
    }
}
