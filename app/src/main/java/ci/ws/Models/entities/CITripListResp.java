package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ci.ws.define.CIWSHomeStatus_Code;

/**
 * Created by ryan on 16/4/26.
 * 依照航段來顯示所有的行程資料
 */
@SuppressWarnings("serial")
public class CITripListResp implements Serializable {

    @Expose
    public String Segment_Num;

    /**對應定位編號的乘客護照英文名*/
    @Expose
    public String First_Name;

    /**對應定位編號的乘客護照英文姓*/
    @Expose
    public String Last_Name;

    /**是否選過餐點(Y/N)*/
    @Expose
    public String Is_Select_Meal;

    /**該定位編號的航段編號*/
    @Expose
    public String Itinerary_Num = "";

    /**定位編號*/
    @Expose
    public String PNR_Id = "";

    /**該航段編號的狀態*/
    @Expose
    public int Status_Code = CIWSHomeStatus_Code.TYPE_A_NO_TICKET;

    @Expose
    public List<CITripListResp_Itinerary> Itinerary_Info = new ArrayList<CITripListResp_Itinerary>();

    /**訂位代號陣列(Len:6)*/
    @Expose
    public List<String> PNR_List;

}
