package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Kevin on 2017/12/14.
 */
@SuppressWarnings("serial")
public class CIBoardPassResp_BaggageInfo implements Serializable, Cloneable {

    /**行李託運起站*/
    @Expose
    public String Baggage_BoardPoint;

    /**行李託運迄站*/
    @Expose
    public String Baggage_OffPoint;

    /**行李託運日期*/
    @Expose
    public String Baggage_Date;

    /**編號清單*/
    @Expose
    public List<NumbersInfo> Baggage_Numbers;


    public static class NumbersInfo implements Serializable{
        /**行李託運號碼*/
        @Expose
        public String Baggage_ShowNumber;

        /**行李Barcode*/
        @Expose
        public String Baggage_BarcodeNumber;

        /**是否有行李追蹤狀態(Y/N)*/
        @Expose
        public String Baggage_IsStatus;
    }


    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
