package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

/**行李資訊內容*/
public class CIBaggageInfoContentResp  implements Serializable {

//    /**UI顯示用*/
//    public String strTitle;

    /**狀態處理時間*/
    @Expose
    public String Execute_Time;

    /**班機號碼*/
    @Expose
    public String Flight_Number;

    /**各段起飛時間*/
    @Expose
    public String Flight_Departure_Date;

    /**各段出發站*/
    @Expose
    public String Flight_Departure_Station;

    /**行李運送資訊*/
    @Expose
    public String Bag_Status;

}


