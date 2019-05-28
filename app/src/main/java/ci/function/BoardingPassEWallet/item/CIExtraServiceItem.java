package ci.function.BoardingPassEWallet.item;

import java.util.ArrayList;

import ci.ws.Models.entities.CIEWallet_ExtraService_Info;

/**
 * Created by kevincheng on 2016/3/25.
 * 其他服務的航班資訊
 */
public class CIExtraServiceItem {
    public static final String DEF_IS_EXPIRED_TAG = "Expired_Tag";
    boolean m_bIsExpired;          //是否過期

    public ArrayList<CIEWallet_ExtraService_Info> m_arExtraServiceDataList = new ArrayList<>();

    public CIExtraServiceItem(boolean isExpired) {
        this.m_bIsExpired = isExpired;
    }

    public boolean getIsExpired(){
        return m_bIsExpired;
    }
}
