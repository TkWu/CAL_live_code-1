package ci.ws.Models.entities;

/**
 * Created by ryan on 16/5/5.
 */
public class CISeatInfo {

    /**橫排名稱*/
    public String Row_Number;

    /**直列名稱*/
    public String Col_Name;

    /**座位種類*/
    public CISeatType SeatType;

    /**座位狀態*/
    public CISeatStatus SeatStatus;

    public CISeatInfo(){
        Row_Number  = "";
        Col_Name    = "";
        SeatType    = CISeatType.Seat;
        SeatStatus  = CISeatStatus.Available;
    }

    public enum CISeatType{

        /**座位*/ Seat,
        /**走道*/ Aisle,
        /**空格*/ Empty,
        /**其他*/ Another
    }

    public enum CISeatStatus {

        /**空位*/ Available,
        /**有人*/ Occupied,
        /**其他*/ Another
    }
}
