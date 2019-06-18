package ci.ws.Models.entities;


import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CINApisEntity implements Cloneable {

    public CINApisEntity()
    {
        paxInfo = new ArrayList();
    }
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public void setId(String strCardNo, String strDocType) {
        if (TextUtils.isEmpty(strCardNo) || TextUtils.isEmpty(strDocType)) {
            return;
        }

        StringBuffer sb = new StringBuffer();
        this.id = sb.append(strCardNo).append(":").append(strDocType).toString();
    }

    public String getId() {
        return id;
    }

    public void addInfos(PaxInfoObj _input){
        if (this.paxInfo != null){
            paxInfo.add(_input);
        }
    }
    /**
     * 男性
     */
    public static final String SEX_MALE = "M";
    /**
     * 女性
     */
    public static final String SEX_FEMALE = "F";

    /**
     * 使用者會員卡號
     */
    @Expose
    public String cardNo = "123";

    /**
     * 行動裝置 deviceid
     */
    @Expose
    public String deviceId = "456";

    /**
     * PAX陣列
     */
    @Expose
    public ArrayList paxInfo;

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public class basicDocuments{
        public String gender = "123";
        public String birthday = "123";
        public String residence = "123";
        public String nationality = "123";
    }

    public class otherDocuments{
        public String documentNo = "123";
        public String expireDay = "123";
        public String issueCountry = "123";
    }

    public class docas{
        public String country = "123";
        public String state = "123";
        public String city = "123";
        public String address = "123";
        public String zipcode = "123";
    }

    public class PaxInfoObj{
        public String firstName = "GGG";
        public String lastName = "HHH";
        public ArrayList ArrdocumentInfos = new ArrayList();


        public void setName(String input_firstName, String input_lastName){
            firstName = input_firstName;
            lastName = input_lastName;
        }
        public void addDocumentInfos(Object _input_documentInfosObj) {

            if (ArrdocumentInfos != null) {
                ArrdocumentInfos.add((_input_documentInfosObj));
            }
        }
    }


    public class DocumentInfosObj{
        public String documentType = "YRTH";
        public String documentName = "YRdgfdsog";
        public Object documentTypeObj;

        public DocumentInfosObj(String docType) {
            switch (docType){
                case "A":
                    documentTypeObj = new basicDocuments();
                case "N":
                    documentTypeObj = new docas();
                default:
                    documentTypeObj = new otherDocuments();
            }
        }
    }
}