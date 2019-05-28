package ci.ws.Models;

import java.util.ArrayList;

import ci.ws.Models.entities.CISeatColInfo;
import ci.ws.Models.entities.CISeatInfo;

/**
 * Created by Ryan on 16/7/1.
 */
public class CISeatFloor {

    /**Row數量*/
    public int SeatRow = 0;
    /**Col數量*/
    public int SeatCol = 0;
    /**Col要顯示的名稱*/
    public ArrayList<CISeatColInfo> arColTextList = null;
    /**所有座位*/
    public ArrayList<CISeatInfo> arSeatList = null;

    public CISeatFloor(){

        arColTextList = new ArrayList<>();
        arSeatList = new ArrayList<>();
    }
}
