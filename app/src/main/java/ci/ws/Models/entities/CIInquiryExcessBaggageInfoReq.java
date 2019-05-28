package ci.ws.Models.entities;

import java.io.Serializable;
import java.util.ArrayList;

import ci.function.Core.CIApplication;
import ci.ws.define.WSConfig;

/**
 * Created by KevinCheng on 17/9/12.
 * Doc. : CI_APP_API_AddExcessBaggage_v2.docx
 * 功能說明：查詢加購行李價格。
 */
@SuppressWarnings("serial")
public class CIInquiryExcessBaggageInfoReq implements Serializable{
    /**
     * 訂位代號( Len: 6)
     */
    public String pnr_id;

    /**
     * 航段編號(PK)(Len: 4)
     */
    public String pnr_seq;

    /**
     * 航段編號(PK)(Len: 4)
     */
    public ArrayList<EB> eb;

    /**
     * zh-TW:繁體中文／zh-CN:簡體中文／ja-JP:日文／en-US:英文
     */
    public String language;

    /**
     * app版本
     */
    public String version;

    public CIInquiryExcessBaggageInfoReq() {
        pnr_id      = "";
        pnr_seq     = "";
        eb          =  new ArrayList<>();
        version     = WSConfig.DEF_API_VERSION;
        language    = CIApplication.getLanguageInfo().getWSLanguage();
    }

    public static class EB implements Serializable{
        /**
         * 超額行李單位
         * EXWG: 重量
         * EXPC: 件數
         */
        public String ssrType;

        /**
         * 已加購行李重量/件數
         */
        public String ssrAmount;


        /**
         * 旅客編號(Len: 4)
         */
        public String pax_num;


        /**
         *  自定義tag
         *  名字
         */
        public String first_name;

        /**
         *  自定義tag
         *  姓氏
         */
        public String last_name;
    }
}
