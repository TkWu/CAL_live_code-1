package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

public class CIApisQryRespEntity {
    /**
     * 使用者會員卡號
     */
    @Expose
    public String cardNo = "";

    /**
     * PAX陣列
     */
    @Expose
    public ArrayList<CIApisRespPaxInfo> paxInfo;

    public CIApisQryRespEntity() {
        paxInfo = new ArrayList<CIApisRespPaxInfo>();
    }

    public void addInfos(CIApisRespPaxInfo _input){
        if (this.paxInfo != null){
            paxInfo.add(_input);
        }
    }

    public class CIApisRespPaxInfo {
        public String firstName = "firstName";
        public String lastName = "lastName";
        public ArrayList<ApisRespDocObj> documentInfos;


    }

    public class ApisRespDocObj implements Serializable {
        /**
         * 證件序號
         */
        @Expose
        public String SEQ = "";
        /**
         * 行動裝置 deviceid
         */
        @Expose
        public String deviceId = "deviceId";
        /**
         * 文件類型
         */
        @Expose
        public String documentType = "";
        /**
         * 文件自訂名稱
         */
        @Expose
        public String documentName = "documentName";
        /**
         * 文件物件
         */
        @Expose
        public CIApispaxInfo.BasicDocuments basicDocuments;
        @Expose
        public CIApispaxInfo.OtherDocuments otherDocuments;
        @Expose
        public CIApispaxInfo.Docas docas;

    }
}
