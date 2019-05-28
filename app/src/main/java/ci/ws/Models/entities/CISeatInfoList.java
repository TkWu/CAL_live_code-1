package ci.ws.Models.entities;

import java.util.ArrayList;

import ci.ws.Models.CISeatFloor;

/**
 * Created by ryan on 16/5/5.
 */
public class CISeatInfoList {

    /**班機編號*/
    public String iataAircraftTypeCode = "333"; //330-300
    /**飛機上層*/
    public CISeatFloor Up_SeatFloor   = null;
    public ArrayList<CISeatFloor> arUpSeatFloorList = null;
    /**飛機下層*/
    public CISeatFloor Down_SeatFloor = null;
    public ArrayList<CISeatFloor> arMainSeatFloorList = null;

    public CISeatInfoList(){
        Up_SeatFloor = new CISeatFloor();
        Down_SeatFloor = new CISeatFloor();

        arUpSeatFloorList = new ArrayList<CISeatFloor>();
        arMainSeatFloorList = new ArrayList<CISeatFloor>();
    }

}
