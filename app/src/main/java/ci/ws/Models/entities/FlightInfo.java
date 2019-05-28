package ci.ws.Models.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FlightInfo implements Serializable {
        /**航班編號*/
        @SerializedName("Flight_Num")
        public String flight_num;

        /**航班日期*/
        @SerializedName("Flight_Date")
        public String flight_date;
    }