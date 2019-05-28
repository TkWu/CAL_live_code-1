package ci.function.ManageMiles.item;

import java.io.Serializable;

/**
 * Created by jlchen on 2016/3/9.
 */
public class CIAccumulationItem implements Serializable{

    //title
    public String m_strDescription = "";
    //搭機日期or兌換日期
    public String m_strDate = "";
    //里程
    public String m_strMiles = "";
    //有效期限
    public String m_strExDate = "";
    //里程類型
    public String m_strType = "";

    /** Mileage Record*/
    //航班編號
    public String m_strFlightNo = "";
    //客艙
    public String m_strCabin = "";

    /** Redeem Record*/
    /** Award Record*/
    //受/轉讓人
    public String m_strNominee = "";
    //受/轉讓人卡號
    public String m_strCardNo = "";
    //受/轉讓人卡別(需求變更 已不需要此欄位)
//    public String m_strCardType = "";
    //核獎號碼
    public String m_strAwardNo = "";
    //機票號碼
    public String m_strTicketNo = "";
    //是否顯示未使用或機票發行
    public String m_strRemark = "";
    //獎項類別
    public String m_strBnsusg = "";

    public String m_strFlightItem = "";

//    //是否未使用
//    public boolean m_bNotInUse = false;
//    //是否此機票已使用
//    public boolean m_bTicketIssued = false;

    //是否顯示轉讓tag
    public boolean m_bTransfer = false;
}
