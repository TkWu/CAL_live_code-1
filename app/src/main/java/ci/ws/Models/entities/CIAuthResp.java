package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

/**
 * Created by Ryan on 2016/11/8.
 */

public class CIAuthResp {

    @Expose
    public String access_token;
    @Expose
    public String token_type;
    @Expose
    public String expires_in;

    public CIAuthResp(){
        access_token= "";
        token_type  = "";
        expires_in  = "";
    }
}
