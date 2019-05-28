package ci.ws.Models.entities;

import java.util.ArrayList;

/**
 * Created by Ryan on 16/4/28.
 * 時刻表Response的資料結構
 */
public class CITimeTableListResp {

    /**去程的航班卡List*/
    public ArrayList<CITimeTable_InfoEntity> arDepartureList;
    /**回程的航班卡List*/
    public ArrayList<CITimeTable_InfoEntity> arReturnList;

    public CITimeTableListResp(){
        arDepartureList = new ArrayList<>();
        arReturnList = new ArrayList<>();
    }
}
