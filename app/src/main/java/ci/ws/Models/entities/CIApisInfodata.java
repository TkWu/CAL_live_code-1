package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class CIApisInfodata {
    /**
     * 使用者會員卡號
     */
    @Expose
    public String cardNo = "";

    /**
     * PAX陣列
     */
    @Expose
    public ArrayList paxInfo;

    public CIApisInfodata() {
        paxInfo = new ArrayList();
    }

    public void addInfos(CIApispaxInfo _input){
        if (this.paxInfo != null){
            paxInfo.add(_input);
        }
    }

}
