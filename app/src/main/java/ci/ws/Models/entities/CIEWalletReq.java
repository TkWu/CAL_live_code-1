package ci.ws.Models.entities;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by kevincheng on 2016/5/20.
 * 文件：CI_APP_API_EWAllet.docx 16/05/19 14:47
 * 4.1.1 InquiryExtraServiceByPNRNoSIT
 */
@SuppressWarnings("serial")
public class CIEWalletReq implements Serializable {

    //1.卡號登入
    /**華夏會員卡號(Len :9)*/
    public String Card_Id;
    /**名( Len: Max. 30)*/
    public String First_Name_C;
    /**姓( Len: Max. 30)*/
    public String Last_Name_C;
    //2.PNR登入
    /**訂位代號(Len :6)*/
    public String PNR_ID;
    //3.Ticket登入
    /**機票票號(Len :13)*/
    public String Ticket;
    /**名( Len: Max. 30)*/
    public String First_Name_T;
    /**姓( Len: Max. 30)*/
    public String Last_Name_T;

    /**訂位代號陣列(Len:6)*/
    public Set<String> Pnr_List;
    /**名( Len: Max. 30)*/
    public String First_Name_P;
    /**姓( Len: Max. 30)*/
    public String Last_Name_P;

    /**回傳結果的語言
     * zh-TW:繁體中文／zh-CN:簡體中文／ja-JP:日文／en-US:英文
     * */
    public String Language;

    /**API版本,參閱版本內容(Release Contents)*/
    public String Version;

//    /**user ip */
//    public String client_ip;

    public CIEWalletReq(){
        Card_Id     = "";
        First_Name_C= "";
        Last_Name_C = "";
        Pnr_List    = new LinkedHashSet<>();

        PNR_ID      = "";
        First_Name_P= "";
        Last_Name_P = "";
        Ticket      = "";
        First_Name_T= "";
        Last_Name_T = "";

        Language    = "";
        Version     = "";
//        client_ip = "";
    }
}
