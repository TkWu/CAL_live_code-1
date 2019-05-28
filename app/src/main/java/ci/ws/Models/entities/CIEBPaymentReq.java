package ci.ws.Models.entities;

import java.util.ArrayList;

import ci.function.Core.CIApplication;
import ci.ws.define.WSConfig;

/**
 * Created by KevinCheng on 17/9/12.
 * Doc. : CI_APP_API_AddExcessBaggage_v2.docx
 * 功能說明：加購行李付款。
 */
@SuppressWarnings("serial")
public class CIEBPaymentReq {
    /**
     * 訂位代號( Len: 6)
     */
    public String pnr_id;

    /**
     * 航段編號(PK)(Len: 4)
     */
    public String pnr_seq;

    /**
     * 付款Token
     */
    public String token;

    /**
     * 航段編號(PK)(Len: 4)
     */
    public ArrayList<CIInquiryExcessBaggageInfoReq.EB> eb;

    /**
     * zh-TW:繁體中文／zh-CN:簡體中文／ja-JP:日文／en-US:英文
     */
    public String language;

    /**
     * app版本
     */
    public String version;

    public CIEBPaymentReq() {
        pnr_id      = "";
        pnr_seq     = "";
        token       = "";
        eb          =  new ArrayList<>();
        version     = WSConfig.DEF_API_VERSION;
        language    = CIApplication.getLanguageInfo().getWSLanguage();
    }
}
