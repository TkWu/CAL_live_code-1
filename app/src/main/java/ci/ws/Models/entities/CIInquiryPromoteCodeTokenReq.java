package ci.ws.Models.entities;

import ci.function.Core.CIApplication;
import ci.ws.define.WSConfig;

/**
 * 以促銷代碼查詢token
 * Created by kevin on 17/12/11.
 */
public class CIInquiryPromoteCodeTokenReq {

    /**促銷代碼*/
    public String OfferCode;
    /**語系*/
    public String language;
    /**API版本*/
    public String Version;


    public CIInquiryPromoteCodeTokenReq(){
        OfferCode       = "";
        language        = CIApplication.getLanguageInfo().getWSLanguage();
        Version         = WSConfig.DEF_API_VERSION;
    }
}
