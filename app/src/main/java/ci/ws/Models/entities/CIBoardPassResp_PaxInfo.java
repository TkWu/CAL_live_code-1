package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jlchen on 2016/5/31.
 */
@SuppressWarnings("serial")
public class CIBoardPassResp_PaxInfo implements Serializable, Cloneable {

    /**名( Len: Max. 30)*/
    @Expose
    public String First_Name;

    /**姓( Len: Max. 30)*/
    @Expose
    public String Last_Name;

    /**座位編號(Len: 3)*/
    @Expose
    public String Seat_Number;

    /**登機證資料*/
    @Expose
    public String Boarding_Pass;

    /**座位區*/
    @Expose
    public String Seat_Zone;

    /**Seq No*/
    @Expose
    public String Seq_No;

    /**是否為天盟會員: Y是/N否*/
    @Expose
    public String Sky_Priority;

    /**貴賓室等級*/
    @Expose
    public String Lounge;

    /**是否有貴賓室
     * Y：在登機證頁面顯示貴賓室
     * N：在登機證頁面不顯示貴賓室
     * */
    @Expose
    public String Is_Lounge = "N";

    /**2016/06/22新加的tag, 文件未說明其用途*/
    @Expose
    public String Is_Print_BP;

    /**是否有online check-in，
     * Y：在登機證頁面顯示牌卡
     * N：在登機證頁面不顯示牌卡
     * */
    @Expose
    public String Is_Check_In = "Y";

    /**是否顯示登機證，要搭配Is_Check_In, Boarding_Pass 服用，
     * 判斷寫在Mode層,
     * Y：可顯示
     * N：不可顯示
     * */
    @Expose
    public String Is_Display_Boarding_Pass = "N";

    /**顯示特殊登機證背景
     * caerulea：顯示藍鵲號
     * */
    @Expose
    public String Boarding_Pass_SP = "";

    /**
     * 會員卡號
     * */
    @Expose
    public String Card_Id = "";

    /**
     * 訂位代號
     * */
    @Expose
    public String Ticket = "";

    /**
     * 是否顯示Tag於登機證上面，TSA_PRE = 3 才顯示（1.0.28需求）
     */
    @Expose
    public String TSA_PRE;

    /**乘客行李資料*/
    @Expose
    public List<CIBoardPassResp_BaggageInfo> Baggage_Info;


    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
