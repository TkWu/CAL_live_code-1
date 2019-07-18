package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

public class CIApisQryRespEntity implements Serializable {
    /**
     * 使用者會員卡號
     */
    @Expose
    public String cardNo = "";

    /**
     * PAX陣列
     */
    @Expose
    public ArrayList<CIApispaxInfo> paxInfo;

    public CIApisQryRespEntity() {
        paxInfo = new ArrayList<CIApispaxInfo>();
    }

    public void addInfosObjArray(CIApispaxInfo _input){
        if (this.paxInfo != null){
            paxInfo.add(_input);
        }
    }

    public void clearInfosObjArray(){
        if (this.paxInfo != null){
            paxInfo.clear();
        }
    }

    public ArrayList<CIApispaxInfo> getInfosObjArray(){
        if (this.paxInfo != null){
            return this.paxInfo;
        }else{
            return null;
        }
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
         * 文件動作：I/U/D
         */
        @Expose
        public String mode = "";
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

    public class CIApispaxInfo implements Serializable {
        public String firstName = "firstName";
        public String lastName = "lastName";
        public ArrayList<ApisRespDocObj> documentInfos;


        public CIApispaxInfo(){
            documentInfos = new ArrayList<ApisRespDocObj>();
        }

        public void setName(String input_firstName, String input_lastName){
            firstName = input_firstName;
            lastName = input_lastName;
        }
        public void addDocumentInfos(ApisRespDocObj _input_documentInfosObj) {

            if (documentInfos != null) {
                documentInfos.add(_input_documentInfosObj);
            }
        }

        public class basicDocuments_obj{
            /**
             * 文件類型
             */
            @Expose
            public final String documentType = "N";
            /**
             * 文件自訂名稱
             */
            @Expose
            public String documentName = "documentName";
            /**
             * 行動裝置 deviceid
             */
            @Expose
            public String deviceId = "deviceId";
            /**
             * 文件物件內容
             */
            @Expose
            public BasicDocuments basicDocuments;

            public basicDocuments_obj() {
                basicDocuments = new BasicDocuments();
            }
        }

        public class otherDocuments_obj{
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
             * 行動裝置 deviceid
             */
            @Expose
            public String deviceId = "deviceId";
            /**
             * 文件物件內容
             */
            @Expose
            public OtherDocuments otherDocuments;

            public otherDocuments_obj() {
                otherDocuments = new OtherDocuments();
            }

        }

        public class docas_obj{
            /**
             * 文件類型
             */
            @Expose
            public String documentType = "A";
            /**
             * 文件自訂名稱
             */
            @Expose
            public String documentName = "documentName";
            /**
             * 行動裝置 deviceid
             */
            @Expose
            public String deviceId = "deviceId";
            /**
             * 文件物件內容
             */
            @Expose
            public Docas docas;

            public docas_obj() {
                docas = new Docas();
            }
        }


        public class BasicDocuments implements Serializable {
            public String gender = "";
            public String birthday = "";
            public String residence = "";
            public String nationality = "";
        }

        public class OtherDocuments implements Serializable {
            public String documentNo = "";
            public String expireDay = "";
            public String issueCountry = "";
        }

        public class Docas implements Serializable {
            public String country = "";
            public String state = "";
            public String city = "";
            public String address = "";
            public String zipcode = "";
        }
    }
}
