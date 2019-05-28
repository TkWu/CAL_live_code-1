package ci.ws.Models.entities;

/**
 * Created by Ryan on 16/4/25.
 */
public class CILoginReq {

    /**會員編號/EMAIL/MOBILE*/
    public String user_id;
    /**密碼*/
    public String password;
    /**社群UID*/
    public String social_id;
    /**社群商; FACEBOOK/GOOGLE+*/
    public String social_vendor;
    /**社群信箱*/
    public String social_email;
    /**是否綁定社群 ; YES/NO*/
    public String is_social_combine;

    public static final String SOCIAL_COMBINE_NO = "NO";
    public static final String SOCIAL_COMBINE_YES= "YES";

    public CILoginReq(){
        user_id     = "";
        password    = "";
        social_id   = "";
        social_vendor   = "";
        social_email    = "";
        is_social_combine = SOCIAL_COMBINE_NO;
    }
}
