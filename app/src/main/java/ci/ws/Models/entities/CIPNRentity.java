package ci.ws.Models.entities;

import java.util.ArrayList;

/**
 * Created by Ryan on 16/4/26.
 * 顯示同一個PNR的資料,不同PNR的行程不會放在一起
 */
public class CIPNRentity {

    public ArrayList<CITripListResp_Itinerary> arPNRList = null;

    public CIPNRentity(){
        arPNRList = new ArrayList<>();
    }
}
