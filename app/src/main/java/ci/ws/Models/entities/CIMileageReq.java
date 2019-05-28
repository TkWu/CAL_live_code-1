package ci.ws.Models.entities;

import ci.function.Core.CIApplication;
import ci.ws.cores.CIWSShareManager;
import ci.ws.define.WSConfig;

/**
 * 里程管理 總里程數 請求 資料結構
 * Created by jlchen on 16/5/11.
 */
public class CIMileageReq {

    /**會員卡號*/
    public String card_no;
    /**登入token*/
    public String login_token;
    /**裝置id*/
    public String device_id;
    /**語系*/
    public String culture_info;
    /**版本*/
    public String version;


    public CIMileageReq(){
        card_no         = CIApplication.getLoginInfo().GetUserMemberCardNo();
        login_token     = CIWSShareManager.getAPI().getLoginToken();
        device_id       = CIApplication.getDeviceInfo().getAndroidId();
        culture_info    = CIApplication.getLanguageInfo().getWSLanguage();
        version         = WSConfig.DEF_API_VERSION;
    }
}
