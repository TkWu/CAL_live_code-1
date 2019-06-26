package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class CIApispaxInfo {
    public String firstName = "firstName";
    public String lastName = "lastName";
    public ArrayList documentInfos;


    public CIApispaxInfo(){
        documentInfos = new ArrayList();
    }

    public void setName(String input_firstName, String input_lastName){
        firstName = input_firstName;
        lastName = input_lastName;
    }
    public void addDocumentInfos(Object _input_documentInfosObj) {

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


    public class BasicDocuments{
        public String gender = "gender";
        public String birthday = "birthday";
        public String residence = "residence";
        public String nationality = "nationality";
    }

    public class OtherDocuments{
        public String documentNo = "documentNo";
        public String expireDay = "expireDay";
        public String issueCountry = "issueCountry";
    }

    public class Docas{
        public String country = "country";
        public String state = "state";
        public String city = "city";
        public String address = "address";
        public String zipcode = "zipcode";
    }
}
