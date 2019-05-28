package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import ci.ws.cores.object.GsonTool;

/**
 * Created by joannyang on 16/6/6.
 */
public class CICheckInAllPaxReq {

    @Expose
    private NowPnr Now_Pnr;

    public void setNowPnr(String strPnrId, ArrayList<String> arSegmentNumber) {
        if( null == Now_Pnr ) {
            Now_Pnr = new NowPnr();
        }
        Now_Pnr.Pnr_Id = strPnrId;

        if( null == arSegmentNumber ) {
            Now_Pnr.Segment_Number = null;
        } else {
            String[] strSegNo = new String[arSegmentNumber.size()];
            Now_Pnr.Segment_Number = arSegmentNumber.toArray(strSegNo);
        }

    }

    @Expose
    public OtherPnr Other_Pnr;

    public void setOtherPnr(String strPnrId, String strFirstName, String strLastName ) {
        if( null == Other_Pnr ) {
            Other_Pnr = new OtherPnr();
        }

        Other_Pnr.Pnr_Id = strPnrId;
        Other_Pnr.First_Name = strFirstName;
        Other_Pnr.Last_Name = strLastName;


    }

    @Expose
    public OtherTicket Other_Ticket;

    public void setOtherTicket(String strTicketNo, String strFirstName, String strLastName ) {
        if( null == Other_Ticket ) {
            Other_Ticket = new OtherTicket();
        }

        Other_Ticket.Ticket = strTicketNo;
        Other_Ticket.First_Name = strFirstName;
        Other_Ticket.Last_Name = strLastName;

    }

    private class NowPnr implements Serializable {

        @Expose
        public String Pnr_Id;

        @Expose
        public String[] Segment_Number;
    }

    private class OtherPnr implements Serializable {
        @Expose
        public String Pnr_Id;

        @Expose
        public String First_Name;

        @Expose
        public String Last_Name;
    }

    private class OtherTicket implements Serializable {
        @Expose
        public String Ticket;

        @Expose
        public String First_Name;

        @Expose
        public String Last_Name;
    }

}
