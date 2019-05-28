package ci.function.SeatSelection;

import android.view.View;

import java.io.Serializable;

/** 選位用的乘客資料
 * Created by jlchen on 2016/7/5.
 */
public class CIPassengerSeatItem implements Serializable{
    //是否為當前乘客
//    public boolean m_bNowSelect = false;
    //乘客名
    public String m_strPassengerName = "";
    //乘客去程選位順序
    public int m_iPassengerSeq = -1;
    //乘客去程座位
    public String m_strPassengerSeat = "";
    //乘客號碼
    public String m_strPaxNum = "";
//    //check-in選位用
    public String m_strDid = "";
    //當前選位view
    public View m_vItem = null;

    //判斷去程或回程的tag(回程=true)
//    public boolean m_bReturn = false;
    //乘客回程選位順序
//    public int m_iPassengerSeqReturn = -1;
    //乘客回程座位
//    public String m_strPassengerSeatReturn = "";
    public CIPassengerSeatItem(){}
}
