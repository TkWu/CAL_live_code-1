package ci.ws.Models.entities;

import java.util.ArrayList;

/**
 * Created by Ryan on 16/4/27.
 * 用來表示所有的航段牌卡資料,
 */
public class CIFlightStatusResp {

    public ArrayList<CIFlightStatus_infoEntity> arFlightList;

    public CIFlightStatusResp(){

        arFlightList = new ArrayList<>();
    }
}
