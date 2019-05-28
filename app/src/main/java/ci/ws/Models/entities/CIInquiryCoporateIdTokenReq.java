package ci.ws.Models.entities;

import ci.function.Core.CIApplication;
import ci.ws.define.WSConfig;

/**
 * Created by 644336 on 19/04/15.
 * 功能說明：以企業會員代碼查詢token。
 */

public class CIInquiryCoporateIdTokenReq {
    /**企業會員代碼*/
    public String OfferCode;
    /**語系*/
    public String language;
    /**API版本*/
    public String Version;


    public CIInquiryCoporateIdTokenReq(){
        OfferCode       = "";
        language        = CIApplication.getLanguageInfo().getWSLanguage();
        Version         = WSConfig.DEF_API_VERSION;
    }
}
