package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

public class CIApisQryEntity implements Cloneable {

    /**
     * API MODE
     */
    @Expose
    public String language = "";

    /*
     * APIS info Object
     */
    @Expose
    public CIApisQryEntity.ApisInfoObj apisInfo = new CIApisQryEntity.ApisInfoObj();
    public class ApisInfoObj {
        /**
         * 使用者會員卡號
         */
        @Expose
        public String cardNo = "";

    }
}
