package ci.ws.Models.entities;

import ci.function.Core.CIApplication;

/**
 * Created by KevinCheng on 17/8/29.
 * Doc. : CI_APP_API_CheckVersion.docx
 * 功能說明：版本更新通知與重要公告。
 */
@SuppressWarnings("serial")
public class CICheckVersionAndAnnouncementReq {

    /**
     * zh-TW:繁體中文／zh-CN:簡體中文／ja-JP:日文／en-US:英文
     * */
    public String language;

    /**app版本*/
    public String version;

    /**App平台 android/ios*/
    public String deviceType;

    public CICheckVersionAndAnnouncementReq(){
        version         = CIApplication.getVersionName();
        deviceType      = "android";
        language        = CIApplication.getLanguageInfo().getWSLanguage();
    }
}
