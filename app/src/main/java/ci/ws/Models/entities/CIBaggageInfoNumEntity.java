package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class CIBaggageInfoNumEntity implements Serializable {


    /**行李條顯示號碼*/
    @SerializedName("Baggage_ShowNumber")
    @Expose
   public String Baggage_ShowNumber;


    /**行李條Barcode號碼*/
    @SerializedName("Baggage_BarcodeNumber")
    @Expose
    public String Baggage_BarcodeNumber;

}
